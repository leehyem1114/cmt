package com.example.cmtProject.mapper.erp.saleMgt;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.cmtProject.dto.erp.saleMgt.PurchasesOrderEditDTO;
import com.example.cmtProject.dto.erp.saleMgt.PurchasesOrderMainDTO;

@Mapper
public interface PurchasesOrderMapper {

	// 발주 메인 SELECT
	List<PurchasesOrderMainDTO> poMainSelect();

	int poMainUpdate(PurchasesOrderEditDTO poEditDto);

}
