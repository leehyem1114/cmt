package com.example.cmtProject.dto.erp.saleMgt;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.cmtProject.entity.erp.employees.Employees;
import com.example.cmtProject.entity.erp.salesMgt.SalesOrder;
import com.example.cmtProject.entity.mes.standardInfoMgt.Clients;
import com.example.cmtProject.entity.mes.standardInfoMgt.Products;
import com.example.cmtProject.entity.mes.standardInfoMgt.Warehouses;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesOrderDTO {

	//SalesOrder entity
	private Long soNo; //수주주문번호(pk)
	private String soCode;
	private LocalDate soDate;  //수주일자
	private LocalDate shipDate;  //출하일자
	private int soQuantity;  //수량
	private int pdtShippingPrice; //출고단가
	private int soValue;  //공급가액
	private String soStatus; //종결여부
	private String soComments;  //비고
	
	//Clients entity
	private Long cltNo;  //거래처no
	private String cltCode; //거래처코드
	private String cltName; //거래처명
	
	//Products entity
	private Long pdtNo;
	private String pdtCode;
	private String pdtName;
	
	//Warehouses entity
	private Long whsNo;  //창고no(pk)
	private String whsCode; //창고코드
	private String whsName; //창고명
	
	//Employees entity
	private Long empNo;
    private String empId;
    private String empName; 
    
    // SalesOrderDTO를 생성
 	public static SalesOrderDTO fromSalesOrder(SalesOrder salesOrder, Clients clients, Products products, Warehouses warehouses, Employees employees) {
     	return SalesOrderDTO.builder()
 			.soNo(salesOrder.getSoNo())
 			.soCode(salesOrder.getSoCode())
 			.soDate(salesOrder.getSoDate())
 			.shipDate(salesOrder.getShipDate())
 			.soQuantity(salesOrder.getSoQuantity())
 			.pdtShippingPrice(salesOrder.getPdtShippingPrice())
 			.soValue(salesOrder.getSoValue())
 			.soStatus(salesOrder.getSoStatus())
 			.soComments(salesOrder.getSoComments())
 			.cltNo(clients.getCltNo())
 			.cltCode(clients.getCltCode())
 			.cltName(clients.getCltName())
 			.pdtNo(products.getPdtNo())
 			.pdtCode(products.getPdtCode())
 			.pdtName(products.getPdtName())
 			.whsNo(warehouses.getWhsNo())
 			.whsCode(warehouses.getWhsCode())
 			.whsName(warehouses.getWhsName())
 			.empNo(employees.getEmpNo())
 			.empId(employees.getEmpId())
 			.empName(employees.getEmpName())
 			.build();
 	}
}
