package com.example.cmtProject.mapper.mes.standardInfoMgt;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.cmtProject.dto.mes.standardInfoMgt.ProductsEditDto;
import com.example.cmtProject.entity.mes.standardInfoMgt.Products;

@Mapper
public interface ProductMapper {
	
	// 전체 조회
    public List<Products> list() throws Exception;
    
    // 선택 조회
    public Products select(String pdtCode) throws Exception;
    
    // 등록
    public int insert(Products product) throws Exception;
    
    // 수정
    public int update(Products product) throws Exception;
    
    // 삭제
    public int delete(int pdtCode) throws Exception;

	public int pdtMainUpdate(ProductsEditDto pdtEditDto);

}
