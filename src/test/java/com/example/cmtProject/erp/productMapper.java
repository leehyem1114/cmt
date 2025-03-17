	package com.example.cmtProject.erp;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.cmtProject.entity.Product;
import com.example.cmtProject.mapper.mes.standardInfoMgt.ProductMapper;

class productMapper {

	@Autowired
	private ProductMapper productMapper;
	
	@Test
	public List<Product> list() throws Exception{
		//List<Product> productList = productMapper.list();
		List<Product> productList = productMapper.list();
		System.out.println(productList);
		return productList;
	}

}
