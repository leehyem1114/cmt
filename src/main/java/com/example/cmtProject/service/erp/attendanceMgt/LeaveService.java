package com.example.cmtProject.service.erp.attendanceMgt;

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
import com.example.cmtProject.dto.erp.attendanceMgt.LeaveDTO;
import com.example.cmtProject.entity.erp.attendanceMgt.Attend;
import com.example.cmtProject.entity.erp.employees.Employees;
import com.example.cmtProject.mapper.erp.attendanceMgt.AttendsMapper;
import com.example.cmtProject.mapper.erp.attendanceMgt.LeaveMapper;
import com.example.cmtProject.repository.erp.attendanceMgt.AttendRepository;
import com.example.cmtProject.repository.erp.employees.EmployeesRepository;

@Service
public class LeaveService {

	private static final Logger logger = LoggerFactory.getLogger(LeaveService.class);
	
	@Autowired
	LeaveMapper leaveMapper;

	// ADMIN은 모든 휴가정보 조회
	public List<LeaveDTO> getAllLeaves() {
		return leaveMapper.getAllLeaves();
	}

	// 휴가 일정 관리 저장
	@Transactional
	public void insertLeave(LeaveDTO dto, Employees loginUser) {
		dto.setEmpId(loginUser.getEmpId());
		leaveMapper.insertLeave(dto, dto.getEmpId());	
	}    
    
    
    
    
    
    
    
}

