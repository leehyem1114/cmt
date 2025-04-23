package com.example.cmtProject.mapper.mes.qualityControl;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.example.cmtProject.dto.mes.qualityControl.IqcDTO;

@Mapper
public interface IqcMapper {

	List<IqcDTO> getAllIqc();

	void iqcRemarksUpdate(IqcDTO iqcDTO);
	
	void isVisiableToFalse(List<Long> ids);	
	
	void saveExcelData(IqcDTO dto);

	List<Map<String, Object>> getMaterialReceipts();
	
	int getMaxIqcCodeSeq(String datePart);

	void insertIqcInspectionList(Map<String, Object> row);





	



}
