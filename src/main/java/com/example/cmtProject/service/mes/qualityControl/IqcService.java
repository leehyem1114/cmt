package com.example.cmtProject.service.mes.qualityControl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.dto.mes.qualityControl.IqcDTO;
import com.example.cmtProject.mapper.mes.qualityControl.IqcMapper;

import jakarta.transaction.Transactional;

@Service
public class IqcService {
	
	@Autowired
	private IqcMapper iqcMapper;

	// 모든 입고 검사 목록
	public List<IqcDTO> getAllIqc() {
		return iqcMapper.getAllIqc();
	}

	@Transactional
	public void saveExcelData(IqcDTO dto) {
		iqcMapper.saveExcelData(dto);
	}

	

	

	
	

}
