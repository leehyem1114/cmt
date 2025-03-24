package com.example.cmtProject.service.erp.attendanceMgt;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cmtProject.dto.erp.attendanceMgt.AttendDTO;
import com.example.cmtProject.entity.erp.attendanceMgt.Attend;
import com.example.cmtProject.entity.erp.employees.Employees;
import com.example.cmtProject.repository.erp.attendanceMgt.AttendRepository;
import com.example.cmtProject.repository.erp.employees.EmployeesRepository;

@Service
public class AttendService {

	@Autowired
    private AttendRepository attendRepository;
    @Autowired
	private EmployeesRepository employeeRepository;
    
    // 모든 출결 정보 조회
    public List<AttendDTO> getAllAttends() {
        return attendRepository.findAll().stream()
                .map(Attend::toDto)
                .collect(Collectors.toList());
    }

    // 출결 정보 저장
//    @Transactional
//    public AttendDTO saveAttend(AttendDTO dto) {
//        Employees employee = employeeRepository.findById(dto.getEmpNo())
//                .orElseThrow(() -> new RuntimeException("사원을 찾을 수 없습니다."));
//
//        Attend attend = Attend.toEntity(dto, employee);
//        Attend savedAttend = attendRepository.save(attend);
//        return AttendDTO.fromEntity(savedAttend);
//    }

//    // 특정 사원의 출결 정보 조회
//    public List<AttendDTO> getAttendsByEmployeeId(Long employeeId) {
//        return attendRepository.findByEmpNo_EmpNo(employeeId).stream()
//                .map(AttendDTO::fromEntity)
//                .collect(Collectors.toList());
//    }

    // 출결 정보 수정
//    @Transactional
//    public AttendDTO updateAttend(Long id, AttendDTO dto) {
//        Attend attend = attendRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("출결 정보를 찾을 수 없습니다."));
//        
//        attend.setAttendDate(dto.getAttendDate());
//        attend.setAttendType(dto.getAttendType());
//        attend.setAttendStatus(dto.getAttendStatus());
//        attend.setRemarks(dto.getRemarks());
//
//        Attend updatedAttend = attendRepository.save(attend);
//        return AttendDTO.fromEntity(updatedAttend);
//    }

    // 출결 정보 삭제
    @Transactional
    public void deleteAttend(Long id) {
        attendRepository.deleteById(id);
    }
}

