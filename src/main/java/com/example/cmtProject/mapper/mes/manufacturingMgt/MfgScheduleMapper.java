package com.example.cmtProject.mapper.mes.manufacturingMgt;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.cmtProject.dto.mes.manufacturingMgt.MfgScheduleDTO;
import com.example.cmtProject.dto.mes.manufacturingMgt.MfgScheduleDetailDTO;
import com.example.cmtProject.dto.mes.manufacturingMgt.MfgSchedulePlanDTO;

@Mapper
public interface MfgScheduleMapper {
	
	// 제조 계획 목록 조회
	List<MfgScheduleDTO> getMfgScheduleTotalList();
	
	// 제조 계획 상세 정보 조회
	//List<MfgScheduleDetailDTO> getMsdList();

	// 제조 계획 등록
	void registMsPlan(MfgScheduleDTO mfgScheduleDTO);
	
	// 제조 계획 등록 시 생산 계획 상태 업데이트
	void updateMpStatus(String mpNo);

	// 제조 계획 상세 조회
	List<MfgScheduleDetailDTO> getMsdDetailList(String msCode);

	// 엑셀 데이터 저장
	void saveExcelData(MfgScheduleDTO dto);

	// 제조 계획 등록 시 생산 계획 내역 조회
	List<MfgSchedulePlanDTO> getMpList();

	List<Map<String, Object>> selectBomDetailByMsCode1(@Param("msCode") String msCode);

	void insertBomDetailFromBom(@Param("msCode") String msCode);



}
