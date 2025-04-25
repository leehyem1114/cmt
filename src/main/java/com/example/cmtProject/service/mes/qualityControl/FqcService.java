package com.example.cmtProject.service.mes.qualityControl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.dto.mes.qualityControl.FqcDTO;
import com.example.cmtProject.dto.mes.qualityControl.IqcDTO;
import com.example.cmtProject.entity.erp.employees.Employees;
import com.example.cmtProject.mapper.mes.qualityControl.FqcMapper;

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

	// FQC 코드 생성
	private String generateFqcCode() {
		String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		int maxSeq = fqcMapper.getMaxFqcCodeSeq(datePart);
		int nextSeq = maxSeq + 1;
		String seqStr = String.format("%03d", nextSeq);
		return "FQC-" + datePart + "-" + seqStr;
	}

	// 물건 입고시 입고 검사 검사전과 필요한 데이터 가져와서 인서트 하기
	@Transactional
	public void insertFqcInspection() {
		List<Map<String, Object>> result = fqcMapper.getProductsIssues();
		
		for (Map<String, Object> row : result) {
			row.put("issueCode", row.get("ISSUE_CODE"));
		    row.put("pdtCode", row.get("PDT_CODE"));
		    row.put("issuedQty", row.get("ISSUED_QTY"));
		    row.put("whsCode", row.get("WAREHOUSE_CODE"));
		    row.put("iqcCode", generateFqcCode());

			fqcMapper.insertFqcInspectionList(row);
		}
	}

	// 검사전에서 검사중으로 업데이트
	@Transactional
	public void updateFqcInspectionStatusProcessing(Employees loginUser, FqcDTO fqcDTO) {
		fqcDTO.setFqcStartTime(LocalDateTime.now());
		fqcMapper.updateFqcInspectionStatusProcessing(loginUser, fqcDTO);
	}

	@Transactional
	// 검사중에서 검사완료로 업데이트
	public void updateFqcInspectionStatusComplete(FqcDTO fqcDTO) {
		
	}

	

	

	
	

}
