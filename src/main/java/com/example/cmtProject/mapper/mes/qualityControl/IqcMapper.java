package com.example.cmtProject.mapper.mes.qualityControl;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.cmtProject.dto.comm.CommonCodeDetailDTO;
import com.example.cmtProject.dto.mes.qualityControl.IqcDTO;
import com.example.cmtProject.dto.mes.qualityControl.QcmDTO;

@Mapper
public interface IqcMapper {

	List<IqcDTO> getAllIqc();

	void saveExcelData(IqcDTO dto);



	



}
