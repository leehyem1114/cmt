package com.example.cmtProject.service.mes.manufacturingMgt;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.dto.mes.manufacturingMgt.MfgPlanDTO;
import com.example.cmtProject.dto.mes.manufacturingMgt.MfgPlanSalesOrderDTO;
import com.example.cmtProject.dto.mes.manufacturingMgt.MfgScheduleDTO;
import com.example.cmtProject.dto.mes.manufacturingMgt.MfgSchedulePlanDTO;
import com.example.cmtProject.mapper.mes.manufacturingMgt.MfgMapper;

import jakarta.transaction.Transactional;

@Service
public class MfgService {

	@Autowired
	private MfgMapper mfgMapper;
	
	// 생산 계획 내역 조회
	public List<MfgPlanDTO> getMfgPlanTotalList(){
		return mfgMapper.getMfgPlanTotalList();
	}

	// 생산 계획 등록 시 수주 내역 + 소요 시간 조회
	@Transactional
	public List<MfgPlanSalesOrderDTO> getSoList() {
	    // 1. 수주 목록 조회
	    List<MfgPlanSalesOrderDTO> soList = mfgMapper.getSoList();

	    for (MfgPlanSalesOrderDTO so : soList) {
	        Long totalLeadTime = 0L;

	        // 2. 해당 제품에 대한 BOM + 공정 시간 리스트 조회
	        List<Map<String, Object>> bomList = mfgMapper.getBomList(so.getPdtCode());

	        for (Map<String, Object> bom : bomList) {
	            String qtyStr = String.valueOf(bom.get("BOM_QTY"));
	            String timeStr = String.valueOf(bom.get("LEAD_TIME"));

	            // 3. BOM 수량과 공정 소요시간이 존재하면 계산
	            if (qtyStr != null && timeStr != null && !qtyStr.isEmpty() && !timeStr.isEmpty()) {
	                try {
	                    int qty = Integer.parseInt(qtyStr);
	                    int leadTime = Integer.parseInt(timeStr);

	                    totalLeadTime += (long) qty * leadTime; // 누적 소요시간 계산
	                } catch (NumberFormatException e) {
	                    e.printStackTrace();
	                }
	            }
	        }

	        // 4. 계산된 총 소요시간을 DTO에 세팅
	        so.setLeadTime(totalLeadTime);
	    }
		
		return soList;
	}
	
	// 생산 계획 등록
	public void registMpPlan(MfgSchedulePlanDTO mfgSchedulePlanDTO) {
		mfgMapper.registMpPlan(mfgSchedulePlanDTO);
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
