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
@Table(name ="MATERIAL_RECEIPT")
@NoArgsConstructor
@AllArgsConstructor
public class MaterialReceipt {
	
    @Id
    @Column(name = "RECEIPT_NO",precision = 19, scale = 0)
    private Long receiptNo; // 입고 관리번호

    @Column(name = "RECEIPT_CODE", length = 50)
    private String receiptCode; // 입고코드

    @Column(name = "PO_NO", precision = 19, scale = 0)
    private Long poNo; //발주 번호

    @Column(name = "MTL_NO",precision = 19, scale = 0, nullable = false)
    private Long mtlNo; //자재번호

    @Column(name = "RECEIVED_QTY", length = 50)
    private String receivedQty; //입고 수량

    @Column(name = "LOT_NO", length = 50)
    private String lotNo; // LOT 번호

    @Column(name = "RECEIPT_DATE")
    private LocalDate receiptDate; //입고일
    
    @Column(name = "RECEIPT_STATUS", length = 50)
    private String receiptStatus; //입고상태

    @Column(name = "WAREHOUSE_CODE", length = 50)
    private String warehouseCode; //창고코드

    @Column(name = "LOCATION_CODE", length = 50)
    private String locationCode; // 위치코드

    @Column(name = "RECEIVER", length = 50)
    private String receiver; //입고 담당자

//    @Column(name = "INVOICE_NO", length = 50)
//    private String invoiceNo; // 송장 번호 ??필요할지 모르겠음

    @Column(name = "CREATED_BY", length = 50)
    private String createdBy; //생성자

    @Column(name = "UPDATED_BY", length = 50)
    private String updatedBy; //수정자

    @Column(name = "CREATED_DATE")
    private LocalDate createdDate; //생성일시

    @Column(name = "UPDATED_DATE")
    private LocalDate updatedDate; //수정일시


}
