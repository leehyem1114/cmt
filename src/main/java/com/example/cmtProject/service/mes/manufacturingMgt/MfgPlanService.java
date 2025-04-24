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
	
	// 원자재 재고 조회
	@Transactional
	public boolean isCurrentQtyEnough(String pdtCode, Long soQty) {
		boolean isCqe = true;
		
		// 원자재 재고 조회
		List<Map<String, Object>> mtlInventory = mfgPlanMapper.getMaterialInventory();
		
		// 수주에 따른 BOM(자재 사용량) 조회
		List<Map<String, Object>> mfgPlanBomList = mfgPlanMapper.getMfgPlanBomList();
		
		// 재고 조회할 상품의 BOM 조회
		List<Map<String, Object>> bomList = mfgPlanMapper.getBomList(pdtCode);
		
		// 재고 정보 맵 변환
		Map<String, Integer> mtlInventoryMap = new HashMap<>();
		
		for (Map<String, Object> mtl : mtlInventory) { 
		    String mtlCode = (String) mtl.get("MTL_CODE"); // 자재 코드
		    String qtyStr = (String) mtl.get("CURRENT_QTY"); // 재고 수량
		    

		    if (mtlCode != null && qtyStr != null && !qtyStr.isEmpty()) {
		        try {
		            int qty = Integer.parseInt(qtyStr);
		            mtlInventoryMap.put(mtlCode, qty);
		        } catch (NumberFormatException e) {
		            System.out.println("수량 변환 오류 : " + qtyStr);
		        }
		    }
		}

		// mfgPlanBomList 사용량 -> 재고 차감
		for (Map<String, Object> mfgPlanBom : mfgPlanBomList) {
		    String mtlCode = (String) mfgPlanBom.get("MTL_CODE"); // 자재 코드
		    int bomQty = 0; // BOM에서 사용되는 자재 수량

		    // 총 사용량(TOTAL_QTY) 있으면 숫자 변환
		    if (mfgPlanBom.get("TOTAL_QTY") != null) {
		        bomQty = ((Number) mfgPlanBom.get("TOTAL_QTY")).intValue();
		    } else if (mfgPlanBom.get("BOM_QTY") != null) { // 없으면 BOM 수량 문자열에서 숫자로 변환
		        bomQty = Integer.parseInt((String) mfgPlanBom.get("BOM_QTY").toString());
		    }

		    if (mtlCode != null && mtlInventoryMap.containsKey(mtlCode)) {
		    	int currentQty = Integer.parseInt(mtlInventoryMap.get(mtlCode).toString()); // 재고 수량 문자열에서 숫자로 변환
		        int remainingQty = currentQty - bomQty; // 재고 수량 - BOM 사용 수량
		        mtlInventoryMap.put(mtlCode, remainingQty); // 재고 수량 갱신
		    }
		}
		
		// bomList BOM_QTY -> 재고 차감
		for (Map<String, Object> bom : bomList) {
		    String mtlCode = (String) bom.get("MTL_CODE"); // 자재 코드
		    int bomQty = 0;

		    if (bom.get("TOTAL_QTY") != null) {
		        bomQty = ((Number) bom.get("TOTAL_QTY")).intValue();
		    } else if (bom.get("BOM_QTY") != null) {
		        bomQty = Integer.parseInt(bom.get("BOM_QTY").toString());
		    }

		    if (mtlCode != null && mtlInventoryMap.containsKey(mtlCode)) {
		        int remainingQty = mtlInventoryMap.get(mtlCode) - bomQty;
		        mtlInventoryMap.put(mtlCode, remainingQty);
		    }
		}
		
		// bomList의 MTR_CODE 존재 + 수량 1 이상 확인 
		for (Map<String, Object> bom : bomList) {
			String mtlCode = (String) bom.get("MTL_CODE"); // 자재 코드
			
			// 자재 코드 존재, 자재 재고 맵에 포함된 경우만 체크
			if (mtlCode != null && mtlInventoryMap.containsKey(mtlCode)) {
		        int currentQty = mtlInventoryMap.get(mtlCode);
		        
		        if (currentQty < 1) { // 재고 부족
		        	isCqe = false; // 생산 불가
		            break;
		        }
		    }
		}
		return isCqe;
	}
	
	// 생산 계획 등록
	public void registMpPlan(MfgPlanDTO mfgPlanDTO) {
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
