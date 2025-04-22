//package com.example.cmtProject.entity.mes.Inventory;
//
//import java.time.LocalDate;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@Entity
//@Table(name ="MATERIAL_INVENTORY")
//@NoArgsConstructor
//@AllArgsConstructor
//public class MaterialInventory {
//	
//    @Id
//    @Column(name = "INV_NO", precision = 19, scale = 0)
//    private Long invNo; //재고 관리번호
//	
////    @Column(name = "MTL_NO", nullable = false, precision = 19, scale = 0)
////    private Long mtlNo; //자재 번호
//
//    @Column(name = "MTL_CODE", length = 50)
//    private String mtlCode; //자재 코드
//    
//    @Column(name = "WAREHOUSE_CODE", length = 50)
//    private String warehouseCode; //창고코드
//
//    @Column(name = "LOCATION_CODE", length = 50)
//    private String locationCode; //위치코드
//
//    @Column(name = "CURRENT_QTY", length = 50)
//    private String currentQty; // 현재 수량
//
//    @Column(name = "ALLOCATED_QTY", length = 50)
//    private String allocatedQty; // 할당수량(계획수량)
//
//    @Column(name = "AVAILABLE_QTY", length = 50)
//    private String availableQty; // 가용수량
//
//    @Column(name = "LOT_NO", length = 50)
//    private String lotNo; // LOT 번호
//
//    @Column(name = "LAST_MOVEMENT_DATE")
//    private LocalDate lastMovementDate; // 마지막 이동일
//
//    @Column(name = "LAST_ADJUSTMENT_DATE")
//    private LocalDate lastAdjustmentDate; // 마지막 조정일자
//
//    @Column(name = "ADJUSTMENT_REASON", length = 500)
//    private String adjustmentReason; // 조정사유(재고조정)
//
//    @Column(name = "SAFETY_STOCK_ALERT", length = 10)
//    private String safetyStockAlert; // 안전재고 알림 여부
//
//    @Column(name = "CREATED_BY", length = 50)
//    private String createdBy; //생성자
//
//    @Column(name = "UPDATED_BY", length = 50)
//    private String updatedBy; //수정자
//
//    @Column(name = "CREATED_DATE")
//    private LocalDate createdDate; //생성일시
//
//    @Column(name = "UPDATED_DATE")
//    private LocalDate updatedDate; //수정일시
//
//
//	
//
//}
