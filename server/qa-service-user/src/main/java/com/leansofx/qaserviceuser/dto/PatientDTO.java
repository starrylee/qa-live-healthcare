package com.leansofx.qaserviceuser.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 患者验证建档结果 DTO。
 * 实体业务键 id 映射为 businessKey；isNew 标识本次是否为新建档案。
 */
public class PatientDTO {

    private String businessKey;
    private String name;
    private String birthday;

    private boolean isNew;

    public PatientDTO() {
    }

    public PatientDTO(String businessKey, String name, String birthday, boolean isNew) {
        this.businessKey = businessKey;
        this.name = name;
        this.birthday = birthday;
        this.isNew = isNew;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

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

    @JsonProperty("isNew")
    public boolean isNew() {
        return isNew;
    }

    @JsonProperty("isNew")
    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }
}
