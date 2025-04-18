package com.example.cmtProject.service.mes.production;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.dto.mes.production.WorkOrderDTO;
import com.example.cmtProject.mapper.mes.production.WorkOrderMapper;

@Service
public class WorkOrderService {
	@Autowired WorkOrderMapper orderMapper;
	//작업지시 리스트
	public List<WorkOrderDTO> getOrderList() {
		return orderMapper.selectOrderList();
	}
}
