package com.example.cmtProject.service.erp.saleMgt;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.dto.erp.saleMgt.SalesOrderDTO;
import com.example.cmtProject.mapper.erp.saleMgt.SalesOrderMapper;

@Service
public class SalesOrderService {

	@Autowired
  	private SalesOrderMapper salesOrderMapper;
	
	// 수주 메인 SELECT
	public List<SalesOrderDTO> soMainSelect(){
		
		List<SalesOrderDTO> soMainList = salesOrderMapper.soMainSelect();
		
		return soMainList;
	}
	
}
