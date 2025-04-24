package com.example.cmtProject.mapper.mes.manufacturingMgt;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.cmtProject.dto.mes.manufacturingMgt.MfgScheduleDTO;
import com.example.cmtProject.dto.mes.manufacturingMgt.MfgScheduleDetailDTO;

@Mapper
public interface MfgHistoryMapper {

	// 엑셀 데이터 저장
	void saveExcelData(MfgScheduleDTO dto);


}
