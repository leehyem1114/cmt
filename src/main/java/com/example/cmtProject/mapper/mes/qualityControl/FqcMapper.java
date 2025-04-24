package com.example.cmtProject.mapper.mes.qualityControl;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.cmtProject.dto.mes.qualityControl.FqcDTO;

@Mapper
public interface FqcMapper {

	List<FqcDTO> getAllFqc();
	
	void fqcRemarksUpdate(FqcDTO fqcDTO);
		
	void isVisiableToFalse(List<Long> ids);

	void saveExcelData(FqcDTO dto);





	



}
