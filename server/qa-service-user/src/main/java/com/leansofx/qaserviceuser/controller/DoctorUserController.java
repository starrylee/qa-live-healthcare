package com.leansofx.qaserviceuser.controller;

import com.leansofx.qaserviceuser.dto.DoctorUserDTO;
import com.leansofx.qaserviceuser.service.DoctorUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorUserController {

    @Autowired
    private DoctorUserService doctorUserService;

    @GetMapping
    public List<DoctorUserDTO> getAllDoctors() {
        return doctorUserService.getAllDoctors();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorUserDTO> getDoctorById(@PathVariable String id) {
        return doctorUserService.getDoctorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<DoctorUserDTO> getDoctorByUsername(@PathVariable String username) {
        return doctorUserService.getDoctorByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/active")
    public List<DoctorUserDTO> getActiveDoctors() {
        return doctorUserService.getActiveDoctors();
    }

    @PostMapping
    public DoctorUserDTO createDoctor(@RequestBody DoctorUserDTO doctorDTO) {
        return doctorUserService.createDoctor(doctorDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorUserDTO> updateDoctor(@PathVariable String id, @RequestBody DoctorUserDTO doctorDTO) {
        DoctorUserDTO updated = doctorUserService.updateDoctor(id, doctorDTO);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable String id) {
        doctorUserService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }
}
