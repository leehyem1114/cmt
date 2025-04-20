package com.example.cmtProject.mapper.mes.production;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.cmtProject.dto.mes.production.WorkOrderDTO;
import com.example.cmtProject.dto.mes.standardInfoMgt.BomInfoDTO;

@Mapper
public interface ProductionPrcMapper {

	//status가 standby인 것만 가져온다
	List<WorkOrderDTO> selectWoStandByList();

	//woCode select
	List<WorkOrderDTO> selectWoCodeList(String data);

	List<BomInfoDTO> selectPdtCodeList(String data);
}
