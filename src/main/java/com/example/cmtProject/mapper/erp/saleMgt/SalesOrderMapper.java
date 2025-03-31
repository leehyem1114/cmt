package com.example.cmtProject.mapper.erp.saleMgt;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.cmtProject.dto.erp.saleMgt.SalesOrderMainDTO;

@Mapper
public interface SalesOrderMapper {

	// 수주 메인 SELECT
	List<SalesOrderMainDTO> soMainSelect();
}
