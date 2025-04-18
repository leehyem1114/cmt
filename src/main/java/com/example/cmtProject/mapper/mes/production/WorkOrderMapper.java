package com.example.cmtProject.mapper.mes.production;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.cmtProject.dto.mes.production.WorkOrderDTO;

@Mapper
public interface WorkOrderMapper {
	//작업지시 리스트
	List<WorkOrderDTO> selectOrderList();

}
