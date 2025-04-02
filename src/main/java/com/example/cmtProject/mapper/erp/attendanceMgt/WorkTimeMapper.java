package com.example.cmtProject.mapper.erp.attendanceMgt;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.cmtProject.dto.erp.attendanceMgt.WorkTimeDTO;

@Mapper
public interface WorkTimeMapper {
	
	// 어드민 모든 출결 정보 조회
	List<WorkTimeDTO> getAllAttends();
	
	// 매니저 같은 부서 출결 정보 조회
	List<WorkTimeDTO> getAttendsByDept(@Param("deptNo") Long deptNo);
	
	// 유저 개인 출결 정보 조회
	List<WorkTimeDTO> getAttendsByEmpNo(@Param("empNo") Long empNo);
	
	

}
