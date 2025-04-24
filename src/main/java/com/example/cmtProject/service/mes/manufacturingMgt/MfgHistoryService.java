package com.example.cmtProject.service.mes.manufacturingMgt;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.dto.mes.manufacturingMgt.MfgScheduleDTO;
import com.example.cmtProject.dto.mes.manufacturingMgt.MfgScheduleDetailDTO;
import com.example.cmtProject.mapper.mes.manufacturingMgt.MfgHistoryMapper;
import com.example.cmtProject.mapper.mes.manufacturingMgt.MfgScheduleMapper;

import jakarta.transaction.Transactional;

@Service
public class MfgHistoryService {

	@Autowired
	private MfgHistoryMapper mfgHistoryMapper;	

	// 엑셀 데이터 저장
	@Transactional
	public void saveExcelData(MfgScheduleDTO dto) {
		mfgHistoryMapper.saveExcelData(dto);
	}





}
