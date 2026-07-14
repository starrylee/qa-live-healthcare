package com.leansofx.qaserviceuser.service;

import com.leansofx.qaserviceuser.dto.PatientDTO;

import java.util.Optional;

/**
 * 患者验证建档核心领域逻辑。
 */
public interface PatientService {

    /**
     * 先归一化查重，后建档：命中已有档案返回 isNew=false；未命中则生成业务键建档返回 isNew=true。
     *
     * @param name    患者姓名（允许首尾空格与大小写差异）
     * @param birthday 出生日期，格式 YYYY-MM-DD
     * @return 患者档案 DTO
     * @throws IllegalArgumentException 当 name 为空/空白，或 birthday 为 null/空白/格式非法
     */
    PatientDTO verify(String name, String birthday);

    /**
     * 按业务键查询患者档案。
     *
     * @param businessKey 患者业务键（PAT- 前缀）
     * @return 命中返回 PatientDTO，否则 Optional.empty()
     */
    Optional<PatientDTO> getByBusinessKey(String businessKey);
}
