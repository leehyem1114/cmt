package com.example.cmtProject.dto.mes.standardInfoMgt;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductsDTO {
	
	private Long pdtNo;
	private String pdtCode;
	private String pdtName;
	private String pdtSpecification;
	private String pdtShippingPrice;
	private String pdtComments;
}
