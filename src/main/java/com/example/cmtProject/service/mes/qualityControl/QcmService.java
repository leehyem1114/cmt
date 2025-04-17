package com.example.cmtProject.service.mes.qualityControl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.dto.mes.qualityControl.QcmDTO;
import com.example.cmtProject.mapper.mes.qualityControl.QcmMapper;

@Service
public class QcmService {
	
	@Autowired
	private QcmMapper qcmMapper;

	// QCM 모든 정보 출력
	public List<QcmDTO> getAllQcm() {
		return qcmMapper.getAllQcm();
	}

}
