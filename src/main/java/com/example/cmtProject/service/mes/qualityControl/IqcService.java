package com.example.cmtProject.service.mes.qualityControl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Random;

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

	// 비고란 수정
	@Transactional
	public void iqcRemarksUpdate(IqcDTO iqcDTO) {
		iqcMapper.iqcRemarksUpdate(iqcDTO);
	}
	
	// 삭제 대신 안보이게 하기
	@Transactional
	public void isVisiableToFalse(List<Long> ids) {
		iqcMapper.isVisiableToFalse(ids);
	}

	// 엑셀 데이터 저장
	@Transactional
	public void saveExcelData(IqcDTO dto) {
		iqcMapper.saveExcelData(dto);
	}
	
	// IQC 코드 생성
	private String generateIqcCode() {
		String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		int maxSeq = iqcMapper.getMaxIqcCodeSeq(datePart);
		int nextSeq = maxSeq + 1;
		String seqStr = String.format("%03d", nextSeq);
		return "IQC-" + datePart + "-" + seqStr;
	}

	// 물건 입고시 입고 검사 검사전과 필요한 데이터 가져와서 인서트 하기
	@Transactional
	public void insertIqcInspection() {
		List<Map<String, Object>> result = iqcMapper.getMaterialReceipts();
		
		for (Map<String, Object> row : result) {
			// 예: IQC_CODE 자동 생성 (선택)
			String iqcCode = generateIqcCode(); // 'IQC-20250422-001' 같은 것
			row.put("iqcCode", iqcCode); // insert 쿼리에서 #{iqcCode}로 사용될 수 있도록

			iqcMapper.insertIqcInspectionList(row);
		}
	}

	
	
	
	

	
	

}
