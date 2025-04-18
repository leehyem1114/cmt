package com.example.cmtProject.service.mes.qualityControl;

import java.lang.module.ModuleDescriptor.Builder;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.dto.comm.CommonCodeDetailDTO;
import com.example.cmtProject.dto.mes.qualityControl.QcmDTO;
import com.example.cmtProject.mapper.mes.qualityControl.QcmMapper;

import jakarta.transaction.Transactional;

@Service
public class QcmService {
	
	@Autowired
	private QcmMapper qcmMapper;

	// QCM 모든 정보 출력
	public List<QcmDTO> getAllQcm() {
		return qcmMapper.getAllQcm();
	}

	@Transactional
	public int qcmUpdate(QcmDTO qcmDTO) {
		
		return qcmMapper.qcmUpdate(qcmDTO);
	}

	@Transactional
	public List<CommonCodeDetailDTO> getUnitLengthList() {
		return qcmMapper.getUnitLengthList();
	}

	@Transactional
	public List<CommonCodeDetailDTO> getUnitWeightList() {
		return qcmMapper.getUnitWeightList();
	}

	@Transactional
	public int saveExcelData(QcmDTO dto) {
		return qcmMapper.saveExcelData(dto);
		
	}

}
