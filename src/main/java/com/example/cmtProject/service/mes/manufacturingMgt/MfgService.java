package com.example.cmtProject.service.mes.manufacturingMgt;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.dto.mes.manufacturingMgt.MfgPlanDTO;
import com.example.cmtProject.dto.mes.manufacturingMgt.MfgPlanSalesOrderDTO;
import com.example.cmtProject.dto.mes.manufacturingMgt.MfgScheduleDTO;
import com.example.cmtProject.dto.mes.manufacturingMgt.MfgSchedulePlanDTO;
import com.example.cmtProject.mapper.mes.manufacturingMgt.MfgMapper;

@Service
public class MfgService {

	@Autowired
	private MfgMapper mfgMapper;
	
	// 생산 계획 내역 조회
	public List<MfgPlanDTO> getMfgPlanTotalList(){
		return mfgMapper.getMfgPlanTotalList();
	}

	// 생산 계획 등록 시 수주 내역 조회
	public List<MfgPlanSalesOrderDTO> getSoList() {
		return mfgMapper.getSoList();
	}
	
	// 제조 계획 내역 조회
	public List<MfgScheduleDTO> getMfgScheduleTotalList() {
		return mfgMapper.getMfgScheduleTotalList();
	}

	// 제조 계획 등록 시 생산 계획 내역 조회
	public List<MfgSchedulePlanDTO> getMpList() {
		return mfgMapper.getMpList();
	}
		
}
