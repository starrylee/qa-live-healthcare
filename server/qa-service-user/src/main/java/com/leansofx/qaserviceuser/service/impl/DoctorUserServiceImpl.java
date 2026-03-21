package com.leansofx.qaserviceuser.service.impl;

import com.leansofx.qaserviceuser.dto.DoctorUserDTO;
import com.leansofx.qaserviceuser.entity.DoctorUser;
import com.leansofx.qaserviceuser.repository.DoctorUserRepository;
import com.leansofx.qaserviceuser.service.DoctorUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DoctorUserServiceImpl implements DoctorUserService {

    @Autowired
    private DoctorUserRepository doctorUserRepository;

    @Override
    public List<DoctorUserDTO> getAllDoctors() {
        return doctorUserRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<DoctorUserDTO> getDoctorById(String id) {
        return doctorUserRepository.findById(id)
                .map(this::toDTO);
    }

    @Override
    public Optional<DoctorUserDTO> getDoctorByUsername(String username) {
        return doctorUserRepository.findByUsername(username)
                .map(this::toDTO);
    }

    @Override
    public List<DoctorUserDTO> getActiveDoctors() {
        return doctorUserRepository.findByIsActiveTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DoctorUserDTO createDoctor(DoctorUserDTO doctorDTO) {
        DoctorUser entity = toEntity(doctorDTO);
        DoctorUser saved = doctorUserRepository.save(entity);
        return toDTO(saved);
    }

    @Override
    public DoctorUserDTO updateDoctor(String id, DoctorUserDTO doctorDTO) {
        Optional<DoctorUser> existingOpt = doctorUserRepository.findById(id);
        if (existingOpt.isPresent()) {
            DoctorUser existing = existingOpt.get();
            existing.setUsername(doctorDTO.getUsername());
            existing.setPassword(doctorDTO.getPassword());
            existing.setName(doctorDTO.getName());
            existing.setTitle(doctorDTO.getTitle());
            existing.setDepartment(doctorDTO.getDepartment());
            existing.setAvatar(doctorDTO.getAvatar());
            existing.setExperience(doctorDTO.getExperience());
            existing.setSpecialties(listToString(doctorDTO.getSpecialties()));
            existing.setIsActive(doctorDTO.getIsActive());
            DoctorUser saved = doctorUserRepository.save(existing);
            return toDTO(saved);
        }
        return null;
    }

    @Override
    public void deleteDoctor(String id) {
        doctorUserRepository.deleteById(id);
    }

    @Override
    public DoctorUserDTO toDTO(DoctorUser entity) {
        DoctorUserDTO dto = new DoctorUserDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setPassword(entity.getPassword());
        dto.setName(entity.getName());
        dto.setTitle(entity.getTitle());
        dto.setDepartment(entity.getDepartment());
        dto.setAvatar(entity.getAvatar());
        dto.setExperience(entity.getExperience());
        dto.setSpecialties(stringToList(entity.getSpecialties()));
        dto.setIsActive(entity.getIsActive());
        return dto;
    }

    @Override
    public DoctorUser toEntity(DoctorUserDTO dto) {
        DoctorUser entity = new DoctorUser();
        entity.setId(dto.getId());
        entity.setUsername(dto.getUsername());
        entity.setPassword(dto.getPassword());
        entity.setName(dto.getName());
        entity.setTitle(dto.getTitle());
        entity.setDepartment(dto.getDepartment());
        entity.setAvatar(dto.getAvatar());
        entity.setExperience(dto.getExperience());
        entity.setSpecialties(listToString(dto.getSpecialties()));
        entity.setIsActive(dto.getIsActive());
        return entity;
    }

    private List<String> stringToList(String str) {
        if (str == null || str.isEmpty()) {
            return List.of();
        }
        return Arrays.asList(str.split(","));
    }

    private String listToString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        return String.join(",", list);
    }
}
