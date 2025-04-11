package com.example.cmtProject.service.erp.attendanceMgt;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cmtProject.dto.erp.attendanceMgt.AttendDTO;
import com.example.cmtProject.dto.erp.attendanceMgt.WorkTemplateDTO;
import com.example.cmtProject.entity.erp.attendanceMgt.Attend;
import com.example.cmtProject.entity.erp.employees.Employees;
import com.example.cmtProject.mapper.erp.attendanceMgt.AttendsMapper;
import com.example.cmtProject.mapper.erp.attendanceMgt.WorkTimeMapper;
import com.example.cmtProject.repository.erp.attendanceMgt.AttendRepository;
import com.example.cmtProject.repository.erp.employees.EmployeesRepository;

@Service
public class AttendService {

	private static final Logger logger = LoggerFactory.getLogger(AttendService.class);


	@Autowired
    private AttendRepository attendRepository;
    @Autowired
    private AttendsMapper attendsMapper;
    @Autowired
    private WorkTimeMapper workTimeMapper;
    
    // 모든 출결 정보 조회
    public List<AttendDTO> getAllAttends() {
        return attendsMapper.getAllAttends();
    }
    
    // 같은 부서 출결 정보 조회
    public List<AttendDTO> getAttendsByDept(Long deptNo) {
    	return attendsMapper.getAttendsByDept(deptNo);
    }
    
    // 사원 하나의 정보만 조회
    public List<AttendDTO> getAttendsByEmpNo(Long empNo) {
    	return attendsMapper.getAttendsByEmpNo(empNo);
    }
    

    // 출근 정보 저장
    @Transactional
    public void saveAttend(AttendDTO dto, Employees employee) {
    	
    	LocalDateTime workTemplateByEmpNo = workTimeMapper.getWorkTemplateByEmpNo(employee.getEmpNo());
    	
    	if (workTemplateByEmpNo.isBefore(LocalDateTime.now())) {
    	    dto.setAtdStatus("ATS002");
    	}
    	if (workTemplateByEmpNo.isAfter(LocalDateTime.now())) {
    	    dto.setAtdStatus("ATS001");
    	}
    	
    	AttendDTO newDto = AttendDTO.builder()
        		.empNo(employee.getEmpNo())
                .empName(employee.getEmpName()) // 사원 이름 자동으로 설정
                .atdDate(LocalDateTime.now()) // 출근 처리 시 현재 날짜 설정
                .atdStatus(dto.getAtdStatus() != null ? dto.getAtdStatus() : "ATS001") // 출근 상태 기본 NORMAL
                .atdType(dto.getAtdType() != null ? dto.getAtdType() : "ATT001") // 출근 유형 기본 NORMAL
                .atdRemarks(dto.getAtdRemarks())
                .build();
        
        attendsMapper.insertAttend(newDto, employee);
        }
    
    // 퇴근 정보 저장
    @Transactional
	public void updateAttendLeave(Map<String, String> dto, Long atdNo) {
    	
		Attend attend = Attend.builder()
				.atdNo(atdNo)
				.atdLeave(LocalDateTime.now()) // 퇴근 처리 시 현재 시간 설정
				.atdType(dto.get("atdType"))
				.build();
		attendsMapper.updateAttendLeave(attend.getAtdNo(), LocalDateTime.now(), attend.getAtdType());
	}

    
    
    
    
    
    
    
    
}

