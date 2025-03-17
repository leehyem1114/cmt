package com.example.cmtProject.service.mes.standardInfoMgt;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.entity.Product;
import com.example.cmtProject.mapper.mes.standardInfoMgt.ProductMapper;

@Service
public class ProductService {

	@Autowired
	private ProductMapper productMapper;
	
	// 전체 조회
	public List<Product> list() throws Exception{
		
		List<Product> productList = productMapper.list();
		return productList;
	}
	
    // 선택 조회
	public Product select(String pdtCode) throws Exception{
		
		Product product = productMapper.select(pdtCode);
		return product;
	}
	
    // 등록
	public int insert(Product product) throws Exception{
		
		int result = productMapper.insert(product);
		return result;
	}
	
    // 수정
    public int update(Product product) throws Exception{
    	
    	int result = productMapper.update(product);
		return result;
    }
    
    // 삭제
    public int delete(int pdtCode) throws Exception{
    	
    	int result = productMapper.delete(pdtCode);
		return result;
    }
}
