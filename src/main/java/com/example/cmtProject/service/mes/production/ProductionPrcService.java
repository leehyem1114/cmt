package com.example.cmtProject.service.mes.production;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.dto.mes.production.WorkOrderDTO;
import com.example.cmtProject.dto.mes.standardInfoMgt.BomInfoDTO;
import com.example.cmtProject.mapper.mes.production.ProductionPrcMapper;

@Service
public class ProductionPrcService {

	@Autowired
	private ProductionPrcMapper productionPrcMapper;
	
	public List<WorkOrderDTO> selectWoStandByList() {
		
		return productionPrcMapper.selectWoStandByList();
	}

	public List<WorkOrderDTO> selectWoCodeList(String data) {
		// TODO Auto-generated method stub
		return productionPrcMapper.selectWoCodeList(data);
	}

	public List<BomInfoDTO> selectPdtCodeList(String data) {
		// TODO Auto-generated method stub
		return productionPrcMapper.selectPdtCodeList(data);
	}

	
	
}
