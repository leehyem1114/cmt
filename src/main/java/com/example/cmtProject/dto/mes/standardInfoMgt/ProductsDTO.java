package com.example.cmtProject.dto.mes.standardInfoMgt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductsDTO {
	
	private Long pdtNo;
	private String pdtCode;
	private String pdtName;
	private String pdtShippingPrice;
	private String pdtComments;
    private String pdtUseyn;
    private String mtlTypeCode;
    private String pdtWeight;
    private String wtTypeCode;
    private String pdtSize;
    private String ltTypeCode;
    private String pdtType;
}
