package com.example.cmtProject.service.erp.attendanceMgt;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cmtProject.dto.erp.attendanceMgt.AttendDto;
import com.example.cmtProject.entity.erp.attendanceMgt.Attend;
import com.example.cmtProject.repository.erp.attendanceMgt.AttendRepository;

import lombok.RequiredArgsConstructor;

@Service
public class AttendService {

    private final AttendRepository attendRepository;
    private final EmployeeRepository employeeRepository;

    // 출결 정보 저장
    @Transactional
    public AttendDto saveAttend(AttendDto dto) {
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("사원을 찾을 수 없습니다."));

        Attend attend = Attend.toEntity(dto, employee);
        Attend savedAttend = attendRepository.save(attend);
        return AttendDto.fromEntity(savedAttend);
    }

    // 모든 출결 정보 조회
    public List<AttendDto> getAllAttends() {
        return attendRepository.findAll().stream()
                .map(AttendDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 특정 사원의 출결 정보 조회
    public List<AttendDto> getAttendsByEmployeeId(Long employeeId) {
        return attendRepository.findByEmployeeId(employeeId).stream()
                .map(AttendDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 출결 정보 수정
    @Transactional
    public AttendDto updateAttend(Long id, AttendDto dto) {
        Attend attend = attendRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("출결 정보를 찾을 수 없습니다."));
        
        attend.setAttendDate(dto.getAttendDate());
        attend.setAttendType(dto.getAttendType());
        attend.setAttendStatus(dto.getAttendStatus());
        attend.setRemarks(dto.getRemarks());

        Attend updatedAttend = attendRepository.save(attend);
        return AttendDto.fromEntity(updatedAttend);
    }

    // 출결 정보 삭제
    @Transactional
    public void deleteAttend(Long id) {
        attendRepository.deleteById(id);
    }
}

