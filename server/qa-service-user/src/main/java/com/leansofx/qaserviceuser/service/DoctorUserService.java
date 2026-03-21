package com.leansofx.qaserviceuser.service;

import com.leansofx.qaserviceuser.dto.DoctorUserDTO;
import com.leansofx.qaserviceuser.entity.DoctorUser;

import java.util.List;
import java.util.Optional;

public interface DoctorUserService {

    List<DoctorUserDTO> getAllDoctors();

    Optional<DoctorUserDTO> getDoctorById(String id);

    Optional<DoctorUserDTO> getDoctorByUsername(String username);

    List<DoctorUserDTO> getActiveDoctors();

    DoctorUserDTO createDoctor(DoctorUserDTO doctorDTO);

    DoctorUserDTO updateDoctor(String id, DoctorUserDTO doctorDTO);

    void deleteDoctor(String id);

    DoctorUserDTO toDTO(DoctorUser entity);

    DoctorUser toEntity(DoctorUserDTO dto);
}
