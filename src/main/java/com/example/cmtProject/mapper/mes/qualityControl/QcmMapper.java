package com.example.cmtProject.mapper.mes.qualityControl;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.cmtProject.dto.comm.CommonCodeDetailDTO;
import com.example.cmtProject.dto.mes.qualityControl.QcmDTO;

@Mapper
public interface QcmMapper {

	List<QcmDTO> getAllQcm();
	
	void qcmUpdate(QcmDTO qcmDTO);
	
	void qcmInsert(QcmDTO qcmDTO);
	
	String existsByQcmCode(Long qcmNO);

	List<CommonCodeDetailDTO> getUnitLengthList();

	List<CommonCodeDetailDTO> getUnitWeightList();

	void saveExcelData(QcmDTO dto);



	



}
