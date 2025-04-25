package com.example.cmtProject.service.mes.manufacturingMgt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.cmtProject.dto.mes.manufacturingMgt.MfgPlanDTO;
import com.example.cmtProject.dto.mes.manufacturingMgt.MfgPlanSalesOrderDTO;
import com.example.cmtProject.mapper.mes.manufacturingMgt.MfgPlanMapper;

import jakarta.transaction.Transactional;

@Service
public class MfgPlanService {

	@Autowired
	private MfgPlanMapper mfgPlanMapper;
	
	// 생산 계획 내역 조회
	public List<MfgPlanDTO> getMfgPlanTotalList(){
	    // 현재 로그인한 사용자 정보 가져오기
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String currentUserId = auth.getName();
	    
	    
		return mfgPlanMapper.getMfgPlanTotalList();
	}

	// 생산 계획 등록 시 수주 내역 + 소요 시간 조회
	@Transactional
	public List<MfgPlanSalesOrderDTO> getSoList() {
	    // 1. 수주 목록 조회
	    List<MfgPlanSalesOrderDTO> soList = mfgPlanMapper.getSoList();

	    for (MfgPlanSalesOrderDTO so : soList) {
	        Long totalLeadTime = 0L;

	        // 2. 해당 제품에 대한 BOM + 공정 시간 리스트 조회
	        List<Map<String, Object>> bomList = mfgPlanMapper.getBomList(so.getPdtCode());

	        System.out.println("bom===========================" + bomList);
	        
	        for (Map<String, Object> bom : bomList) {
	            String qtyStr = String.valueOf(bom.get("BOM_QTY"));
	            //String timeStr = String.valueOf(bom.get("LEAD_TIME"));
	            String timeStr = "3";
	            
	            System.out.println("qtyStr============ " + qtyStr);
	            System.out.println("timeStr============ " + timeStr);


	            // 3. BOM 수량과 공정 소요시간이 존재하면 계산
	            if (qtyStr != null && timeStr != null && !qtyStr.isEmpty() && !timeStr.isEmpty()) {
	                try {
	                    int qty = Integer.parseInt(qtyStr);
	                    int leadTime = Integer.parseInt(timeStr);

	                    totalLeadTime += (long) qty * leadTime; // 누적 소요시간 계산      
	                    System.out.println("########################################"+leadTime);
	                    System.out.println("####%^&^%&^^^^^^^^^^^^^^^^^^^^^^^^^^"+totalLeadTime);
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
	public void registMpPlan(MfgPlanDTO mfgPlanDTO) {
//		String soCode = mfgPlanDTO.getSoCode();  // 수주 코드
//	    Long soQty = mfgPlanDTO.getSoQty();      // 수주 수량
//	    
//	    if (isCurrentQtyEnough(soCode, soQty)) {
//	        mfgPlanMapper.registMpPlan(mfgPlanDTO);  // 재고 충분 -> 등록 진행
//	        return true;
//	    } else {
//	        // 재고 부족 -> 등록 취소
//	        return false;
//	    }
		mfgPlanMapper.registMpPlan(mfgPlanDTO);
	
	}
	
	// 생산 계획 수정
	@Transactional
	public void updateMpPlan(List<MfgPlanDTO> mpList) {
		mfgPlanMapper.updateMpPlan(mpList);
	}

	// 생산 계획 삭제 (숨김 처리)
	@Transactional
	public void isVisiableToFalse(List<Long> mpNos) {
		mfgPlanMapper.isVisiableToFalse(mpNos);
	}	


	// 엑셀 데이터 저장
	@Transactional
	public void saveExcelData(MfgPlanDTO dto) {
		mfgPlanMapper.saveExcelData(dto);
	}






}
