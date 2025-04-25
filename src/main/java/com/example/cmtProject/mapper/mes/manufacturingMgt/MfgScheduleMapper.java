package com.example.cmtProject.mapper.mes.manufacturingMgt;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

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
	void registMsPlan(List<MfgScheduleDTO> msList);

	// 제조 계획 상세 조회
	List<MfgScheduleDetailDTO> getMsdDetailList(String msCode);

	// 엑셀 데이터 저장
	void saveExcelData(MfgScheduleDTO dto);

	// 제조 계획 등록 시 생산 계획 내역 조회
	List<MfgSchedulePlanDTO> getMpList();


}
