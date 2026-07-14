package com.leansofx.qaserviceuser.service.impl;

import com.leansofx.qaserviceuser.dto.PatientDTO;
import com.leansofx.qaserviceuser.entity.Patient;
import com.leansofx.qaserviceuser.repository.PatientRepository;
import com.leansofx.qaserviceuser.util.BusinessKeyGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * PatientService.verify 单元测试（纯 Mockito，不依赖数据库）。
 * 覆盖 V2.1 未命中建档 / V2.2 命中复用 / V2.3 归一化 / V2.4 参数校验 / V2.5 并发冲突回退。
 */
@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private BusinessKeyGenerator businessKeyGenerator;

    @InjectMocks
    private PatientServiceImpl patientService;

    // V2.1 未命中建档 → 返回新建档案且 isNew=true（对应 AC-1）
    @Test
    void verify_newPatient() {
        // 中文姓名 toLowerCase 为 no-op，归一化后仍为“张三”
        when(patientRepository.findByNormalizedNameAndBirthday("张三", "1990-01-15"))
                .thenReturn(Optional.empty());
        when(businessKeyGenerator.generate()).thenReturn("PAT-20260714-ABCD");
        Patient saved = new Patient("PAT-20260714-ABCD", "张三", "张三", "1990-01-15");
        when(patientRepository.save(any(Patient.class))).thenReturn(saved);

        PatientDTO dto = patientService.verify("张三", "1990-01-15");

        assertTrue(dto.isNew(), "未命中应新建档案 isNew=true");
        assertEquals("PAT-20260714-ABCD", dto.getBusinessKey());
        assertEquals("张三", dto.getName());
    }

    // V2.2 命中复用 → 返回同一业务键且 isNew=false（对应 AC-2）
    @Test
    void verify_existingPatient() {
        Patient existing = new Patient("PAT-20260714-ABCD", "张三", "张三", "1990-01-15");
        when(patientRepository.findByNormalizedNameAndBirthday("张三", "1990-01-15"))
                .thenReturn(Optional.of(existing));

        PatientDTO dto = patientService.verify("张三", "1990-01-15");

        assertFalse(dto.isNew(), "命中应复用档案 isNew=false");
        assertEquals("PAT-20260714-ABCD", dto.getBusinessKey());
    }

    // V2.3 姓名含首尾空格/大小写差异 → 归一化后正确命中或新建（对应 AC-3）
    @Test
    void verify_normalization_hitExisting() {
        Patient existing = new Patient("PAT-20260714-ABCD", "ZhangSan", "zhangsan", "1990-01-15");
        when(patientRepository.findByNormalizedNameAndBirthday("zhangsan", "1990-01-15"))
                .thenReturn(Optional.of(existing));

        PatientDTO dto = patientService.verify("  ZhangSan  ", "1990-01-15");

        assertFalse(dto.isNew(), "归一化后命中已建档，isNew=false");
        assertEquals("PAT-20260714-ABCD", dto.getBusinessKey());
    }

    @Test
    void verify_normalization_newPatient() {
        when(patientRepository.findByNormalizedNameAndBirthday("zhangsan", "2000-01-01"))
                .thenReturn(Optional.empty());
        when(businessKeyGenerator.generate()).thenReturn("PAT-20260714-WXYZ");
        Patient saved = new Patient("PAT-20260714-WXYZ", "ZhangSan", "zhangsan", "2000-01-01");
        when(patientRepository.save(any(Patient.class))).thenReturn(saved);

        PatientDTO dto = patientService.verify("  ZhangSan  ", "2000-01-01");

        assertTrue(dto.isNew(), "归一化后未命中应新建档案");
        assertEquals("ZhangSan", dto.getName(), "应存储 trim 后的原始姓名");
        assertEquals("PAT-20260714-WXYZ", dto.getBusinessKey());
    }

    // V2.4 name 缺失或 birthday 格式非法 → 抛 IllegalArgumentException（对应 AC-4）
    @Test
    void verify_invalidParam() {
        assertThrows(IllegalArgumentException.class,
                () -> patientService.verify(null, "1990-01-15"), "name 为 null 应抛异常");
        assertThrows(IllegalArgumentException.class,
                () -> patientService.verify("   ", "1990-01-15"), "name 仅空白应抛异常");
        assertThrows(IllegalArgumentException.class,
                () -> patientService.verify("张三", null), "birthday 为 null 应抛异常");
        assertThrows(IllegalArgumentException.class,
                () -> patientService.verify("张三", "1990/01/15"), "birthday 含斜杠应抛异常");
        assertThrows(IllegalArgumentException.class,
                () -> patientService.verify("张三", "1990-1-15"), "birthday 单数字月份（位数不足）应抛异常");
    }

    // V2.5 模拟唯一键冲突 → 回退查重返回已有档案、不产生重复（对应 AC-5）
    @Test
    void verify_concurrentFallback() {
        String normalized = "并发测试";
        String birthday = "2000-01-01";

        Patient existing = new Patient("PAT-20260714-YYYY", "并发测试", normalized, birthday);
        when(patientRepository.findByNormalizedNameAndBirthday(normalized, birthday))
                .thenReturn(Optional.empty(), Optional.of(existing));
        when(businessKeyGenerator.generate()).thenReturn("PAT-20260714-XXXX");
        when(patientRepository.save(any(Patient.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate key"));

        PatientDTO dto = patientService.verify("并发测试", birthday);

        assertFalse(dto.isNew(), "并发冲突回退后应复用已有档案 isNew=false");
        assertEquals("PAT-20260714-YYYY", dto.getBusinessKey());
        verify(patientRepository, times(2)).findByNormalizedNameAndBirthday(normalized, birthday);
    }
}
