package com.example.cmtProject.service.mes.qualityControl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.dto.mes.qualityControl.IqcDTO;
import com.example.cmtProject.dto.mes.qualityControl.QcmDTO;
import com.example.cmtProject.entity.erp.employees.Employees;
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
	public void iqcRemarksAndQcmNameUpdate(IqcDTO iqcDTO) {
		iqcMapper.iqcRemarksAndQcmNameUpdate(iqcDTO);
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
	public void insertIqcInspection(Map<String, Object> updateMap) {
		List<Map<String, Object>> result = iqcMapper.getMaterialReceipts(updateMap);
		
		for (Map<String, Object> row : result) {
		    row.put("receiptCode", row.get("RECEIPT_CODE"));
		    row.put("mtlCode", row.get("MTL_CODE"));
		    row.put("receivedQty", row.get("RECEIVED_QTY"));
		    row.put("whsCode", row.get("WAREHOUSE_CODE"));
		    row.put("iqcCode", generateIqcCode());

			iqcMapper.insertIqcInspectionList(row);
		}
	}

	// 검사전에서 검사중으로 업데이트
	@Transactional
	public void updateIqcInspectionStatusProcessing(Employees loginUser, IqcDTO iqcDTO) {
		iqcDTO.setIqcStartTime(LocalDateTime.now());
		iqcMapper.updateIqcInspectionStatusProcessing(loginUser, iqcDTO);
	}

	@Transactional
	// 검사중에서 검사완료로 업데이트
	public void updateIqcInspectionStatusComplete(IqcDTO iqcDTO) {
		iqcDTO.setIqcEndTime(LocalDateTime.now());
		iqcMapper.updateIqcInspectionStatusComplete(iqcDTO);
	}

	
	// 검사완료로 넘어갈때 측정값 저장
	@Transactional
	public IqcDTO autoInspect(String iqcCode) {
	    QcmDTO qcm = iqcMapper.selectQcmInfoByIqcCode(iqcCode);

	    if (qcm == null || qcm.getQcmMinValue() == null || qcm.getQcmMaxValue() == null) {
	        throw new IllegalArgumentException("검사 기준 정보가 없습니다.");
	    }

	    double min = qcm.getQcmMinValue();
	    double max = qcm.getQcmMaxValue();

	    Double weightValue = null;
	    Double lengthValue = null;
	    String result = "";

	    // 무게 검사일 경우
	    if (qcm.getQcmUnitWeight() != null) {
	        weightValue = generateRandom(min, max);
	        result = isPass(weightValue, min, max) ? "합격" : "불합격";
	    }

	    // 길이 검사일 경우
	    if (qcm.getQcmUnitLength() != null) {
	        lengthValue = generateRandom(min, max);
	        result = isPass(lengthValue, min, max) ? "합격" : "불합격";
	    }

	    // 저장
	    System.out.println(iqcCode + weightValue + lengthValue + result);
	    iqcMapper.updateMeasuredValues(iqcCode, weightValue, lengthValue, result);

	    return new IqcDTO(weightValue, lengthValue, result);
	}

	// 랜덤값 생성 (소수점 1자리)
	private double generateRandom(double min, double max) {
	    double extendedMin = min - 1;
	    double extendedMax = max + 1;
	    return Math.round((Math.random() * (extendedMax - extendedMin) + extendedMin) * 10.0) / 10.0;
	}

	// 합격 여부 판정
	private boolean isPass(double value, double min, double max) {
	    return value >= min && value <= max;
	}

	
	
	
	

	
	

}
