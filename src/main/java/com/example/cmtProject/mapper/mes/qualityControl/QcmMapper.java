package com.example.cmtProject.mapper.mes.qualityControl;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.cmtProject.dto.comm.CommonCodeDetailDTO;
import com.example.cmtProject.dto.mes.qualityControl.QcmDTO;

@Mapper
public interface QcmMapper {

	List<QcmDTO> getAllQcm();

	int qcmUpdate(QcmDTO qcmDTO);

	List<CommonCodeDetailDTO> getUnitLengthList();

	List<CommonCodeDetailDTO> getUnitWeightList();

	int saveExcelData(QcmDTO dto);

}
