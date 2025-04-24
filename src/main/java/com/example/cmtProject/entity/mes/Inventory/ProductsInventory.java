package com.example.cmtProject.entity.mes.Inventory;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name ="PRODUCTS_INVENTORY")
@NoArgsConstructor
@AllArgsConstructor
public class ProductsInventory {
	
   @Id
    @Column(name = "PINV_NO", precision = 19, scale = 0)
    private Long pinvNo; //제품 재고 관리번호

    @Column(name = "PDT_NO", nullable = false, precision = 19, scale = 0)
    private Long pdtNo; // 제품 번호

    @Column(name = "PDT_CODE", length = 50)
    private Long pdtCode; // 제품 코드

    @Column(name = "WAREHOUSE_CODE", length = 50)
    private String warehouseCode; //창고코드

    @Column(name = "LOCATION_CODE", length = 50)
    private String locationCode; //위치코드

    @Column(name = "CURRENT_QTY", length = 50)
    private String currentQty; //현재 수량

    @Column(name = "ALLOCATED_QTY", length = 50)
    private String allocatedQty; //할당수량(계획수량)

    @Column(name = "AVAILABLE_QTY", length = 50)
    private String availableQty; // 가용수량 

    @Column(name = "LOT_NO", length = 50)
    private String lotNo; // 완제품 LOT 번호

    @Column(name = "LAST_MOVEMENT_DATE")
    private LocalDate lastMovementDate; // 마지막 이동일

    @Column(name = "LAST_ADJUSTMENT_DATE")
    private LocalDate lastAdjustmentDate; // 마지막 조정일자

    @Column(name = "ADJUSTMENT_REASON", length = 500)
    private String adjustmentReason; //조정사유(재고조정)

    @Column(name = "SAFETY_STOCK_ALERT", length = 10)
    private String safetyStockAlert; //안전재고 알림여부

    @Column(name = "CREATED_BY", length = 50)
    private String createdBy; //생성자

    @Column(name = "UPDATED_BY", length = 50)
    private String updatedBy; //수정자

    @Column(name = "CREATED_DATE", insertable = true, updatable = false)
    private LocalDate createdDate; //생성일시

    @Column(name = "UPDATED_DATE")
    private LocalDate updatedDate; //수정일시


}

