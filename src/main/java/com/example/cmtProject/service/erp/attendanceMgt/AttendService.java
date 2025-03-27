package com.example.cmtProject.service.erp.attendanceMgt;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cmtProject.dto.erp.attendanceMgt.AttendDTO;
import com.example.cmtProject.entity.erp.attendanceMgt.Attend;
import com.example.cmtProject.entity.erp.employees.Employees;
import com.example.cmtProject.repository.erp.attendanceMgt.AttendRepository;
import com.example.cmtProject.repository.erp.employees.EmployeesRepository;

@Service
public class AttendService {

	private static final Logger logger = LoggerFactory.getLogger(AttendService.class);


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
    
    // 사원 하나의 정보만 조회
    public List<Attend> getAttendsByEmpNo(Long empNo) {
    	return attendRepository.findByEmpNoOrderByAtdNoDesc(empNo);
	}

    
    // 페이징 처리
    public List<AttendDTO> getAttendPage(int page, int perPage) {
        Pageable pageable = PageRequest.of(page - 1, perPage, Sort.by("attendDate").descending());
        Page<Attend> attendPage = attendRepository.findPagedAttends(pageable);

        return attendPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public long getTotalCount() {
        return attendRepository.count();
    }

    private AttendDTO convertToDto(Attend attend) {
        return new AttendDTO(
                attend.getAtdNo(),
                attend.getEmpNo(),
                attend.getEmpName(),
                attend.getAttendDate(),
                attend.getAttendType(),
                attend.getAttendStatus(),
                attend.getRemarks()
        );
    }
    // 페이징 처리
    

    // 출결 정보 저장
    @Transactional
    public AttendDTO saveAttend(AttendDTO dto, Employees employee) {

        Attend attend = Attend.builder()
                .empNo(employee.getEmpNo())
                .empName(employee.getEmpName()) // 사원 이름 자동으로 설정
                .attendDate(LocalDateTime.now()) // 출근 처리 시 현재 날짜 설정
                .attendType(dto.getAttendType() != null ? dto.getAttendType() : "WORK") // 출근 유형 기본 NORMAL
                .attendStatus(dto.getAttendStatus() != null ? dto.getAttendStatus() : "NORMAL") // 출근 상태 기본 NORMAL
                .remarks(dto.getRemarks())
                .build();

            Attend savedAttend = attendRepository.save(attend);
            
            return AttendDTO.builder()
                .atdNo(savedAttend.getAtdNo())
                .empNo(employee.getEmpNo())
                .empName(employee.getEmpName())
                .attendDate(savedAttend.getAttendDate())
                .attendType(savedAttend.getAttendType())
                .attendStatus(savedAttend.getAttendStatus())
                .remarks(savedAttend.getRemarks())
                .build();
        }

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

