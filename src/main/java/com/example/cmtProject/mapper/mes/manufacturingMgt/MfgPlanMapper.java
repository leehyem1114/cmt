package com.example.cmtProject.mapper.mes.manufacturingMgt;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.cmtProject.dto.mes.manufacturingMgt.MfgPlanDTO;
import com.example.cmtProject.dto.mes.manufacturingMgt.MfgPlanSalesOrderDTO;

@Mapper
public interface MfgPlanMapper {

	// 생산 계획 목록 조회
	List<MfgPlanDTO> getMfgPlanTotalList();
	
	// 생산 계획 등록 시 수주 목록 조회
	List<MfgPlanSalesOrderDTO> getSoList();

	// 생산 계획 등록
	void registMpPlan(MfgPlanDTO mfgPlanDTO);

	// 생산 계획 수정
	void updateMpPlan(List<MfgPlanDTO> mpList);

	// 생산 계획 삭제 (숨김 처리)
	void isVisiableToFalse(List<Long> mpNos);

	// BOM 조회
	List<Map<String, Object>> getBomList(String pdtCode);
	
	// 원자재 재고 조회
	List<Map<String, Object>> getMaterialInventory();
	
	// 수주에 따른 BOM 조회
	List<Map<String, Object>> getMfgPlanBomList(String soCode);
	
	
	// 재고 조회
	//List<Map<String, Object>> selectAvailableQty(@Param("soCode") String soCode, @Param("soQty") Long soQty);
	String selectAvailableQty(@Param("soCode") String soCode, @Param("soQty") Long soQty);
	
	// 엑셀 데이터 저장
	void saveExcelData(MfgPlanDTO dto);

	



}
