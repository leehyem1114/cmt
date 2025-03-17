package com.example.cmtProject.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Product {
	private String pdtCode;
	private String pdtName;
	private String pdtSpecification;
	private String pdtShippingPrice;
	private String pdtComments;
}
