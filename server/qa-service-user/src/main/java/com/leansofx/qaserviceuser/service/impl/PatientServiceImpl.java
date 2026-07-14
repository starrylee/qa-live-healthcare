package com.leansofx.qaserviceuser.service.impl;

import com.leansofx.qaserviceuser.dto.PatientDTO;
import com.leansofx.qaserviceuser.entity.Patient;
import com.leansofx.qaserviceuser.repository.PatientRepository;
import com.leansofx.qaserviceuser.service.PatientService;
import com.leansofx.qaserviceuser.util.BusinessKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 患者验证建档核心逻辑实现：归一化 → 查重 → 建档，并兜底并发唯一索引冲突。
 */
@Service
public class PatientServiceImpl implements PatientService {

    private static final String BIRTHDAY_PATTERN = "^\\d{4}-\\d{2}-\\d{2}$";

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private BusinessKeyGenerator businessKeyGenerator;

    @Override
    public PatientDTO verify(String name, String birthday) {
        if (name == null || name.trim().isEmpty()
                || birthday == null || !birthday.matches(BIRTHDAY_PATTERN)) {
            throw new IllegalArgumentException("姓名或生日格式非法：name 不可为空，birthday 必须为 YYYY-MM-DD");
        }

        String trimmedName = name.trim();
        String normalizedName = trimmedName.toLowerCase();

        Optional<Patient> existing = patientRepository.findByNormalizedNameAndBirthday(normalizedName, birthday);
        if (existing.isPresent()) {
            return toDTO(existing.get(), false);
        }

        String businessKey = businessKeyGenerator.generate();
        Patient patient = new Patient(businessKey, trimmedName, normalizedName, birthday);
        try {
            Patient saved = patientRepository.save(patient);
            return toDTO(saved, true);
        } catch (DataIntegrityViolationException e) {
            // 并发唯一索引冲突：回退查重，复用已存在的档案，保证仅产生一份档案与一个业务键
            Optional<Patient> fallback = patientRepository.findByNormalizedNameAndBirthday(normalizedName, birthday);
            return fallback.map(p -> toDTO(p, false))
                    .orElseThrow(() -> new IllegalStateException("建档冲突回退后仍查无档案", e));
        }
    }

    @Override
    public Optional<PatientDTO> getByBusinessKey(String businessKey) {
        return patientRepository.findById(businessKey)
                .map(p -> toDTO(p, false));
    }

    private PatientDTO toDTO(Patient patient, boolean isNew) {
        return new PatientDTO(patient.getId(), patient.getName(), patient.getBirthday(), isNew);
    }
}
