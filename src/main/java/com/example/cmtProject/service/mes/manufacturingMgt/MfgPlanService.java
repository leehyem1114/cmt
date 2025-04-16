package com.example.cmtProject.service.mes.manufacturingMgt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.mapper.mes.manufacturingMgt.MfgMapper;

@Service
public class MfgPlanService {

	@Autowired
	private MfgMapper mfgMapper;
}
