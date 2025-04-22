package com.example.cmtProject.service.mes.inventory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cmtProject.mapper.mes.inventory.MaterialReceiptMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MaterialReceiptService {
	
	@Autowired
	private MaterialReceiptMapper mrm;
	
	/**
	 * 
	 *  입고 상태인 목록 조회
	 */
	
	
	public List<Map<String, Object>> receiptList(Map<String,Object>map){
		
		return mrm.mReceiptList(map);
	}
	
	/**
	 * 
	 *  미입고 상태인 발주 목록 조회
	 */
	
	
	public List<Map<String, Object>> puchasesList(Map<String,Object>map){
		
		return mrm.puchasesList(map);
	}

	/**
	 * 발주 정보 바탕으로 입고정보 생성
	 */
	@Transactional
    public Map<String, Object> createReceiptFromPurchaseOrder(Long poNo, String userName) {
        Map<String, Object> resultMap = new HashMap<>();
        
        try {
            // 발주 정보 조회를 위한 조건 맵 생성
            Map<String, Object> searchMap = new HashMap<>();
            
            // 발주 정보 조회
            List<Map<String, Object>> purchaseOrders = mrm.puchasesList(searchMap);
            System.out.println("문제찾기11111111111111111111111111111111111");
            // 해당 발주번호 찾기
            Map<String, Object> targetOrder = null;
            for (Map<String, Object> order : purchaseOrders) {
                if (poNo.equals(order.get("PO_NO"))) {
                    targetOrder = order;
                    break;
                }
            }
            System.out.println("문제찾기222222222222222222222222222222222222");
            
//            if (targetOrder == null) {
//                resultMap.put("success", false);
//                resultMap.put("message", "발주 정보를 찾을 수 없습니다. 발주번호: " + poNo);
//                return resultMap;
//            }
            System.out.println("문제찾기333333333333333333333333333333");
            
            // 입고 정보 생성을 위한 Map 생성
            Map<String, Object> receiptMap = new HashMap<>();
            System.out.println("문제찾기4444444444444444444444444444444");
            
            // 시퀀스를 이용한 입고 번호 생성
            Long receiptNo = generateSequentialReceiptNo();
            receiptMap.put("receiptNo", receiptNo);
            System.out.println("문제찾기555555555555555555555555555555555");
            
            // 입고 코드 생성 (예: RCV-yyyyMMdd-순번)
            LocalDate today = LocalDate.now();
            String receiptCode = "RCV-" + today.getYear() + String.format("%02d", today.getMonthValue()) + 
                                String.format("%02d", today.getDayOfMonth()) + "-" + receiptNo;
            receiptMap.put("receiptCode", receiptCode);
            System.out.println("문제찾기6666666666666666666666666666666666");
            
            // 발주 정보로부터 입고 정보 설정
            receiptMap.put("poNo", poNo);
            System.out.println("문제찾기7777777777777777777777777777777777");
            
            // 발주의 원자재 코드를 기반으로 자재 번호를 설정 (임시로 발주번호와 동일하게 설정)
            receiptMap.put("mtlNo", poNo);
            System.out.println("문제찾기888888888888888888888888888888888888");
            
            // 입고 수량은 발주 수량과 동일하게 설정
//            receiptMap.put("receivedQty", targetOrder.get("PO_QUANTITY").toString());
            
            System.out.println("문제찾기99999999999999999999999999999999999");
            // LOT 번호 생성 (예: LOT-yyyyMMdd-순번)
            String lotNo = "LOT-" + today.getYear() + String.format("%02d", today.getMonthValue()) + 
                        String.format("%02d", today.getDayOfMonth()) + "-" + receiptNo;
            receiptMap.put("lotNo", lotNo);
            System.out.println("문제찾기99999999999999999999999999999999999");
            
            // 입고일은 현재 날짜로 설정
            receiptMap.put("receiptDate", today);
            System.out.println("문제찾기99999999999999999999999999999999999");
            
            // 입고 상태, 창고 코드, 위치 코드 설정
            receiptMap.put("receiptStatus", "RECEIVED");
            System.out.println("문제찾기ㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁ");
//            receiptMap.put("warehouseCode", targetOrder.get("WHS_CODE"));
            System.out.println("문제찾기ㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴ");
            receiptMap.put("locationCode", "DEFAULT");
            System.out.println("문제찾기ㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷ");
            
            // 입고 담당자, 생성자, 수정자 정보 설정
            receiptMap.put("receiver", userName);
            receiptMap.put("createdBy", userName);
            receiptMap.put("updatedBy", userName);
            
            System.out.println("문제찾기99999999999999999999999999999999999");
            // 생성일, 수정일 설정
            receiptMap.put("createdDate", today);
            receiptMap.put("updatedDate", today);
            
            System.out.println("문제찾기99999999999999999999999999999999999");
            // 입고 정보 저장
            int insertResult = mrm.insertMaterialReceipt(receiptMap);
            System.out.println("문제찾기99999999999999999999999999999999999");
            
            if (insertResult > 0) {
//                // 발주 상태 업데이트 (입고 완료 상태로)
//                Map<String, Object> updateParams = new HashMap<>();
//                updateParams.put("poNo", poNo);
//                updateParams.put("poStatus", "PO_RECEIVED");
//                int updateResult = mrm.updatePurchaseOrderStatus(updateParams);
                
                resultMap.put("success", true);
                resultMap.put("message", "입고 처리가 완료되었습니다.");
                resultMap.put("receiptData", receiptMap);
                System.out.println("문제찾기99999999999999999999999999999999999");
            } else {
                resultMap.put("success", false);
                resultMap.put("message", "입고 정보 저장에 실패했습니다.");
                System.out.println("문제찾기99999999999999999999999999999999999");
            }
            
        } catch (Exception e) {
            resultMap.put("success", false);
            resultMap.put("message", "입고 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return resultMap;
    }
	
	
	// 테스트 위해서 잠시 만든 시퀀스 애플리케이션을 재시작하면 카운터가 다시 0으로 
	private static long receiptNoCounter = 0;

	/**
	 * 순차적인 입고 번호 생성 (1부터 시작하여 호출 시마다 1씩 증가)
	 * @return 생성된 순차 번호
	 */
	private Long generateSequentialReceiptNo() {
	    // 동시성 문제 방지를 위해 동기화 처리
	    synchronized (this) {
	        receiptNoCounter++;
	        return receiptNoCounter;
	    }
	}
}
