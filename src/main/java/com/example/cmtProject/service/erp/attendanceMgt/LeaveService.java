package com.example.cmtProject.service.erp.attendanceMgt;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cmtProject.dto.erp.attendanceMgt.LeaveDTO;
import com.example.cmtProject.entity.erp.employees.Employees;
import com.example.cmtProject.mapper.erp.attendanceMgt.LeaveMapper;

@Service
public class LeaveService {

	private static final Logger logger = LoggerFactory.getLogger(LeaveService.class);
	
	@Autowired
	LeaveMapper leaveMapper;

	// ADMIN은 모든 휴가정보 조회
	public List<LeaveDTO> getAllLeaves() {
		return leaveMapper.getAllLeaves();
	}

	// MANAGER는 같은 부서 출결정보 조회
	public List<LeaveDTO> getLeavesByDept(Long deptNo) {
		return leaveMapper.getLeavesByDept(deptNo);
	}
	
	// USER는 본인의 출결정보만 조회
	public List<LeaveDTO> getLeavesByEmpId(String empId) {
		return leaveMapper.getLeavesByEmpId(empId);
	}    
	
	// ADMIN은 모든 휴가 보유내역 조회
	public List<LeaveDTO> getAllUsedLeftLeaves() {
		return leaveMapper.getAllUsedLeftLeaves();
	}
	
	// ADMIN은 모든 휴가 보유내역 조회
	public List<LeaveDTO> getUsedLeftLeavesByDept(Long deptNo) {
		return leaveMapper.getUsedLeftLeavesByDept(deptNo);
	}
	
	// ADMIN은 모든 휴가 보유내역 조회
	public List<LeaveDTO> getUsedLeftLeavesByEmpId(String empId) {
		return leaveMapper.getUsedLeftLeavesByEmpId(empId);
	}

	// 휴가 일정 관리 저장
	@Transactional
	public void insertLeave(LeaveDTO dto, Employees loginUser) {
		if (dto.getLevLeftDays() - dto.getLevDays() < 0) {
		    throw new IllegalArgumentException("남은 휴가일수는 음수일 수 없습니다.");
		}
		dto.setEmpId(loginUser.getEmpId());
		leaveMapper.insertLeave(dto, dto.getEmpId());	
	}

    
}
    
    
    
    
    

