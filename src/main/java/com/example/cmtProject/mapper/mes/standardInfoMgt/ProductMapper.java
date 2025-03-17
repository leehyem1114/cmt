package com.example.cmtProject.mapper.mes.standardInfoMgt;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.cmtProject.entity.Product;

@Mapper
public interface ProductMapper {
	
	// 전체 조회
    public List<Product> list() throws Exception;
    
    // 선택 조회
    public Product select(String pdtCode) throws Exception;
    
    // 등록
    public int insert(Product product) throws Exception;
    
    // 수정
    public int update(Product product) throws Exception;
    
    // 삭제
    public int delete(int pdtCode) throws Exception;

}
