package com.example.cmtProject.mapper.mes.manufacturingMgt;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.cmtProject.dto.mes.manufacturingMgt.MfgPlanDTO;
import com.example.cmtProject.dto.mes.manufacturingMgt.MfgScheduleDTO;

@Mapper
public interface MfgMapper {

	// 생산 계획 목록
	List<MfgPlanDTO> getMfgPlanTotalList();

	// 제조 계획 목록
	List<MfgScheduleDTO> getMfgScheduleTotalList();

}
