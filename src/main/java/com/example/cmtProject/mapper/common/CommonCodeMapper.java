package com.example.cmtProject.mapper.common;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface CommonCodeMapper {
	
	List<Map<String, Object>> commonList(Map<String, Object> map);
	
	List<Map<String, Object>> commonDetailList(Map<String, Object> map);

}//CommonCodeMapper
