package com.example.cmtProject.service.mes.production;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.mapper.mes.production.LotMapper;

@Service
public class LotService {

	@Autowired
	private LotMapper lotMapper;
	
	public int getLotNo() {
		// TODO Auto-generated method stub
		return lotMapper.getLotNo();
	}

}
