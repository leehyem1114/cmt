package com.example.cmtProject.entity.erp.salesMgt;


import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
//에러처리
@Data
@Entity
@Table(name = "PURCHASES_ORDER")
@NoArgsConstructor
@AllArgsConstructor
public class PurchasesOrder {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PURCHASESORDER_PO_NO" )
	@SequenceGenerator(name = "SEQ_PURCHASESORDER_PO_NO", sequenceName="SEQ_PURCHASESORDER_PO_NO", allocationSize = 1)
	@Column(name = "PO_NO")
	private Long poNo; //발주주문번호(pk)
	
	@Column(name = "PO_CODE")
	private String poCode;
	
	@Column(name = "PO_DATE") //발주일자
    @DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate poDate;
	
	@Column(name = "RCV_DATE") //입고일자
    @DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate rcvDate;  
	
	@Column(name = "EMP_NO")
	private Long empNo;  //사원번호
	
	@Column(name = "WHS_CODE")
	private String whsCode; //창고 코드
	
	@Column(name = "MTL_CODE")
	private String mtlCode; //원자재 코드
	
	@Column(name = "CLT_CODE")
	private String cltCode;  //공급업체 코드
	
	@Column(name = "PO_QUANTITY")
	private int poQuantity;  //수량
	
	@Column(name = "MTL_RECEIVING_PRICE")
	private int mtlReceivingPrice;  //입고단가
	
	@Column(name = "PO_VALUE")
	private int poValue;  //공급가액
	
	@Column(name = "PO_STATUS")
	private String poStatus;  //종결여부
	
	@Column(name = "PO_COMMENTS")
	private String poComments;  //비고
	
}
