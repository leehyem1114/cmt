package com.example.cmtProject.mapper.erp.attendanceMgt;

import java.time.LocalDateTime;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.cmtProject.dto.comm.CommonCodeDetailDTO;
import com.example.cmtProject.dto.erp.attendanceMgt.AttendDTO;

@Mapper
public interface AttendsMapper {
	
	 // 출근 했을시 출근 버튼 숨기기
    boolean hasCheckedInToday(@Param("empNo") Long empNo,
                              @Param("start") LocalDateTime start,
                              @Param("end") LocalDateTime end);

    // 가장 최근 출근 버튼 ATD_NO 출력
    Long findLatestCheckInAtdNo(@Param("empNo") Long empNo);
    
    // 퇴근 했을시 퇴근 버튼 숨기기
    boolean hasCheckedOutToday(@Param("atdNo") Long atdNo,
                               @Param("start") LocalDateTime start,
                               @Param("end") LocalDateTime end);

    // 퇴근 시간 업데이트
    int updateAttendLeave(@Param("atdNo") Long atdNo,
                          @Param("leaveTime") LocalDateTime leaveTime,
                          @Param("atdType") String leave);
	

}
