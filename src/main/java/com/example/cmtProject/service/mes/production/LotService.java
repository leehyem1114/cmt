package com.example.cmtProject.service.mes.production;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.dto.mes.production.LotOrderDTO;
import com.example.cmtProject.dto.mes.production.LotOriginDTO;
import com.example.cmtProject.dto.mes.production.LotStructurePathDTO;
import com.example.cmtProject.dto.mes.standardInfoMgt.BomStructurePathDTO;
import com.example.cmtProject.mapper.mes.production.LotMapper;

@Service
public class LotService {

	@Autowired
	private LotMapper lotMapper;
	
	public Long getLotNo() {
		// TODO Auto-generated method stub
		return lotMapper.getLotNo();
	}

	public LotOrderDTO getLotOrderPrcType(String todayStr, String type) {
		// TODO Auto-generated method stub
		return lotMapper.getLotOrderPrcType(todayStr, type);
	}

	public void insertLot(LotOriginDTO lod) {
		// TODO Auto-generated method stub
		lotMapper.insertLot(lod);
	}

	public List<BomStructurePathDTO> selectStructurePath(String pdtCode) {
		// TODO Auto-generated method stub
		return lotMapper.selectStructurePath(pdtCode);
	}

	public List<LotStructurePathDTO> selectStructurePathAll(String woCode, String pdtCode) {
		// TODO Auto-generated method stub
		return lotMapper.selectStructurePathAll(woCode, pdtCode);
	}

	public List<LotOriginDTO> selectLotOrigin(String woCode) {
		// TODO Auto-generated method stub
		return lotMapper.selectLotOrigin(woCode);
	}

	public void updateLotResentPRC(LotOriginDTO lotOrigin) {
		// TODO Auto-generated method stub
		lotMapper.updateLotResentPRC(lotOrigin);
	}
	


	
}
