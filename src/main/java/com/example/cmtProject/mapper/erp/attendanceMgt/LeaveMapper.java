package com.example.cmtProject.mapper.erp.attendanceMgt;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.cmtProject.dto.erp.attendanceMgt.LeaveDTO;

@Mapper
public interface LeaveMapper {

	// ADMIN은 모든 휴가정보 조회
	List<LeaveDTO> getAllLeaves();

	// 휴가 일정 관리 저장
	void insertLeave(@Param("dto") LeaveDTO dto, @Param("empId") String empId);
	
	
	

}
