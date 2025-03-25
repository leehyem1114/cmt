package com.example.cmtProject.entity.erp.salesMgt;

public enum PurchasesOrderStatus {
	PO_CREATED, // 발주 생성 - 구매 요청이 접수됨
	PO_APPROVED, //발주 승인 - 구매 요청이 승인됨
	PO_ORDERED, // 발주 확정 - 공급업체에 주문이 확정됨
	PO_INTRANSIT, // 배송 중 - 발주된 자재가 운송 중
	PO_RECEIVED ,// 입고 완료 - 자재가 창고에 도착하여 입고됨
	PO_INSPECTED, // 품질 검사 완료 - 입고된 자재의 품질 검사가 완료됨
	PO_CLOSED, // 발주 완료 - 모든 발주 프로세스가 종료됨
	PO_CANCELED // 발주 취소 - 주문이 취소됨
}
