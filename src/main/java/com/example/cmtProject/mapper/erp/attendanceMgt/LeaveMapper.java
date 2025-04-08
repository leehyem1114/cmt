package com.example.cmtProject.mapper.erp.attendanceMgt;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.cmtProject.dto.erp.attendanceMgt.LeaveDTO;

@Mapper
public interface LeaveMapper {

	// ADMIN은 모든 휴가정보 조회
	List<LeaveDTO> getAllLeaves();
	
	// MANAGER는 같은 부서 출결정보 조회
	List<LeaveDTO> getLeavesByDept(Long deptNo);
	
	// USER는 본인의 출결정보만 조회
	List<LeaveDTO> getLeavesByEmpId(String empId);
	
	// ADMIN은 모든 휴가 보유내역 조회
	List<LeaveDTO> getAllUsedLeftLeaves();
	
	// MANAGER은 같은 부서 휴가 보유내역 조회
	List<LeaveDTO> getUsedLeftLeavesByDept(Long deptNo);
	
	// USER는 개인 휴가 보유내역 조회
	List<LeaveDTO> getUsedLeftLeavesByEmpId(String empId);

	// 휴가 일정 관리 저장
	void insertLeave(@Param("dto") LeaveDTO dto, @Param("empId") String empId);


	
	
	

}
