package com.example.cmtProject.mapper.mes.manufacturingMgt;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.cmtProject.dto.mes.manufacturingMgt.MfgPlanDTO;
import com.example.cmtProject.dto.mes.manufacturingMgt.MfgPlanSalesOrderDTO;
import com.example.cmtProject.dto.mes.manufacturingMgt.MfgScheduleDTO;
import com.example.cmtProject.dto.mes.manufacturingMgt.MfgSchedulePlanDTO;

@Mapper
public interface MfgMapper {

	// 생산 계획 목록 조회
	List<MfgPlanDTO> getMfgPlanTotalList();
	
	// 생산 계획 등록 시 수주 목록 조회
	List<MfgPlanSalesOrderDTO> getSoList();

	// 제조 계획 목록 조회
	List<MfgScheduleDTO> getMfgScheduleTotalList();

	// 제조 계획 등록 시 생산 계획 목록 조회
	List<MfgSchedulePlanDTO> getMpList();


}
