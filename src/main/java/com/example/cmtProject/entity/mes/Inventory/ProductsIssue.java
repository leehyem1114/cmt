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
@Table(name ="PRODUCTS_ISSUE")
@NoArgsConstructor
@AllArgsConstructor
public class ProductsIssue {

	@Id
    @Column(name = "ISSUE_NO")
    private Long issueNo; //출고 관리번호

    @Column(name = "ISSUE_CODE", length = 50)
    private String issueCode; //출고 코드

    @Column(name = "PDT_NO", nullable = false)
    private Long pdtNo; //제품번호

//    @Column(name = "REQUEST_DEPT", length = 50)
//    private String requestDept; //요청부서
//
//    @Column(name = "REQUESTER", length = 50)
//    private String requester; // 요청자

    @Column(name = "REQUEST_QTY", length = 50)
    private String requestQty; //요청수량

    @Column(name = "ISSUED_QTY", length = 50)
    private String issuedQty; //출고수량

    @Column(name = "LOT_NO", length = 50)
    private String lotNo; //LOT 번호

    @Column(name = "REQUEST_DATE")
    private LocalDate requestDate; //요청일

    @Column(name = "ISSUE_DATE")
    private LocalDate issueDate; //출고일

    @Column(name = "ISSUE_STATUS", length = 50)
    private String issueStatus; //출고상태

    @Column(name = "WAREHOUSE_CODE", length = 50)
    private String warehouseCode; //창고코드
    
    @Column(name = "ISSUER", length = 50)
    private String issuer; //출고담당자

    @Column(name = "CREATED_BY", length = 50)
    private String createdBy; //생성자

    @Column(name = "UPDATED_BY", length = 50)
    private String updatedBy; //수정자

    @Column(name = "CREATED_DATE")
    private LocalDate createdDate; //생성일시

    @Column(name = "UPDATED_DATE")
    private LocalDate updatedDate; //수정일시


}
