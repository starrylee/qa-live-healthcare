package com.leansofx.qaserviceuser.controller;

import com.leansofx.qaserviceuser.dto.PatientDTO;
import com.leansofx.qaserviceuser.exception.ResourceNotFoundException;
import com.leansofx.qaserviceuser.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 患者验证建档接口。
 * - POST /api/patients/verify：提交姓名+生日完成验证/建档，返回 PatientDTO
 * - GET  /api/patients/{businessKey}：按业务键查询档案，不存在返回 404
 */
@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @PostMapping("/verify")
    public PatientDTO verify(@RequestBody VerifyRequest request) {
        return patientService.verify(request.getName(), request.getBirthday());
    }

    @GetMapping("/{businessKey}")
    public PatientDTO getByBusinessKey(@PathVariable String businessKey) {
        return patientService.getByBusinessKey(businessKey)
                .orElseThrow(() -> new ResourceNotFoundException("患者档案不存在: " + businessKey));
    }

    /**
     * 验证请求体：仅收集 name + birthday（与弱认证目标一致，不强制 phone/gender）。
     */
    public static class VerifyRequest {

        private String name;
        private String birthday;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }
    }
}
