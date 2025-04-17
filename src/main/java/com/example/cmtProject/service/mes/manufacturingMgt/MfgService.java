package com.example.cmtProject.service.mes.manufacturingMgt;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.dto.mes.manufacturingMgt.MfgPlanDTO;
import com.example.cmtProject.mapper.mes.manufacturingMgt.MfgMapper;

@Service
public class MfgService {

	@Autowired
	private MfgMapper mfgMapper;
	
	public List<MfgPlanDTO> getMfgPlanTotalList(){
		return mfgMapper.getMfgPlanTotalList();
	}
}
