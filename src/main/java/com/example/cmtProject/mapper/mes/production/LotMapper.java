package com.example.cmtProject.mapper.mes.production;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.cmtProject.dto.mes.production.LotOrderDTO;

@Mapper
public interface LotMapper {

	//lot테이블에서 마지막 lotNo값 가져오기
	int getLotNo();

	//LOT-20250421-PR-02 뒤에 02부분을 가져온다 AS
	LotOrderDTO getLotOrderPrcType(@Param("todayStr") String todayStr,@Param("type") String type);

	
	//List<LotOrderDTO> getLotOrderPrcType(@Param("todayStr") String todayStr,@Param("type") String type);

}
