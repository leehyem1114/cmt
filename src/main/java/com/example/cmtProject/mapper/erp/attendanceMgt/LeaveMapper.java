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
	
	
	//////////////////////////////////////////////////////////////////////////////
   // 결재 연동을 위한 메서드 추가
    
    // 결재 문서ID로 휴가 정보 조회
    LeaveDTO getLeaveByDocId(@Param("docId") String docId);
    
    // 결재 문서ID와 함께 휴가 저장
    void insertLeaveWithDocId(@Param("dto") LeaveDTO dto, 
                           @Param("empId") String empId, 
                           @Param("docId") String docId);
    
    // 휴가 상태 업데이트
    int updateLeaveStatus(@Param("levNo") Long levNo, 
                        @Param("status") String status, 
                        @Param("remarks") String remarks);
}


