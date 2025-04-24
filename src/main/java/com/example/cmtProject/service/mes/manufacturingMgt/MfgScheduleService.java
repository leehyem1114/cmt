package com.example.cmtProject.service.mes.manufacturingMgt;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.dto.mes.manufacturingMgt.MfgScheduleDTO;
import com.example.cmtProject.dto.mes.manufacturingMgt.MfgScheduleDetailDTO;
import com.example.cmtProject.mapper.mes.manufacturingMgt.MfgScheduleMapper;

import jakarta.transaction.Transactional;

@Service
public class MfgScheduleService {

	@Autowired
	private MfgScheduleMapper mfgScheduleMapper;	
	
	// 제조 계획 내역 조회
	public List<MfgScheduleDTO> getMfgScheduleTotalList() {
		return mfgScheduleMapper.getMfgScheduleTotalList();
	}
	
	// 제조 계획 상세 정보 조회
//	public List<MfgScheduleDetailDTO> getMsdList() {
//		return mfgMapper.getMsdList();
//	}

	// 제조 계획 등록
	public void registMsPlan(List<MfgScheduleDTO> msList) {
		mfgScheduleMapper.registMsPlan(msList);
	}

	// 제조 계획 상세 조회
	public List<MfgScheduleDetailDTO> getMsdDetailList(String msCode) {
		return mfgScheduleMapper.getMsdDetailList(msCode);
	}

	// 엑셀 데이터 저장
	@Transactional
	public void saveExcelData(MfgScheduleDTO dto) {
		mfgScheduleMapper.saveExcelData(dto);
	}





}
