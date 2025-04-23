package com.example.cmtProject.service.mes.qualityControl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.dto.mes.qualityControl.FqcDTO;
import com.example.cmtProject.dto.mes.qualityControl.IqcDTO;
import com.example.cmtProject.mapper.mes.qualityControl.FqcMapper;
import com.example.cmtProject.mapper.mes.qualityControl.IqcMapper;

import jakarta.transaction.Transactional;

@Service
public class FqcService {
	
	@Autowired
	private FqcMapper fqcMapper;

	// 모든 입고 검사 목록
	public List<FqcDTO> getAllFqc() {
		return fqcMapper.getAllFqc();
	}
	
	@Transactional
	public void fqcRemarksUpdate(FqcDTO fqcDTO) {
		fqcMapper.fqcRemarksUpdate(fqcDTO);
	}
	
	// 저장 대신 업데이트로 안보이게 하기
	@Transactional
	public void isVisiableToFalse(List<Long> ids) {
		fqcMapper.isVisiableToFalse(ids);
	}

	// 엑셀 데이터 저장
	@Transactional
	public void saveExcelData(FqcDTO dto) {
		fqcMapper.saveExcelData(dto);
	}

	

	

	

	
	

}
