package com.example.cmtProject.mapper.mes.production;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.cmtProject.dto.mes.production.LotOrderDTO;
import com.example.cmtProject.dto.mes.production.LotOriginDTO;
import com.example.cmtProject.dto.mes.production.LotStructurePathDTO;
import com.example.cmtProject.dto.mes.standardInfoMgt.BomStructurePathDTO;

@Mapper
public interface LotMapper {

	//lot테이블에서 마지막 lotNo값 가져오기
	Long getLotNo();

	//LOT-20250421-PR-02 뒤에 02부분을 가져온다 AS
	LotOrderDTO getLotOrderPrcType(@Param("todayStr") String todayStr,@Param("type") String type);

	//LOT테이블에 데이터 입력
	void insertLot(LotOriginDTO lod);

	//재귀로 BOM의 path가져오기
	List<BomStructurePathDTO> selectStructurePath(String pdtCode);

	//LOT테이블에서 하단 오른쪽 그리드에 최초 보여줄 전체 데이터 PATH
	List<LotStructurePathDTO> selectStructurePathAll(@Param("woCode") String woCode,@Param("pdtCode") String pdtCode);
	
	//List<LotOrderDTO> getLotOrderPrcType(@Param("todayStr") String todayStr,@Param("type") String type);

}
