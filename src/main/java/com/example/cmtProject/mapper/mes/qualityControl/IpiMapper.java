package com.example.cmtProject.mapper.mes.qualityControl;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.cmtProject.dto.mes.qualityControl.FqcDTO;
import com.example.cmtProject.dto.mes.qualityControl.QcmDTO;
import com.example.cmtProject.entity.erp.employees.Employees;

@Mapper
public interface IpiMapper {

	List<FqcDTO> getAllFqc();

	void fqcRemarksAndQcmNameUpdate(FqcDTO fqcDTO);
	
	void isVisiableToFalse(List<Long> ids);	
	
	void saveExcelData(FqcDTO dto);

	List<Map<String, Object>> getLot(Map<String, Object> updateMap);
	
	int getMaxFqcCodeSeq(String datePart);

	void insertFqcInspectionList(Map<String, Object> row);
	
	void updateFqcInspectionStatusProcessing(@Param("emp") Employees loginUser, @Param("fqc") FqcDTO fqcDTO);

	void updateFqcInspectionStatusComplete(FqcDTO fqcDTO);

	QcmDTO selectQcmInfoByFqcCode(String fqcCode);

	void updateMeasuredValues(@Param("fqcCode") String fqcCode,
		    				  @Param("weightValue") Double weightValue,
		    				  @Param("lengthValue") Double lengthValue,
		    				  @Param("result") String result);

//	void updateMeasuredValues(Map<String, Object> param);



	



}
