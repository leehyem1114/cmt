package com.example.cmtProject.mapper.mes.manufacturingMgt;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.cmtProject.dto.mes.manufacturingMgt.MfgPlanDTO;

@Mapper
public interface MfgMapper {

	// 생산 계획 등록 전체 목록
	List<MfgPlanDTO> getMfgPlanTotalList();

}
