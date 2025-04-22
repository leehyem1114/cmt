package com.example.cmtProject.mapper.mes.production;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LotMapper {

	//lot테이블에서 마지막 lotNo값 가져오기
	int getLotNo();

}
