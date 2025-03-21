package com.example.cmtProject.service.erp.attendanceMgt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.dto.erp.attendanceMgt.WorkTimeDto;
import com.example.cmtProject.entity.erp.attendanceMgt.WorkTime;
import com.example.cmtProject.repository.erp.attendanceMgt.WorkTiemRepository;

@Service
public class WorkTimeService {
	
	@Autowired
	WorkTiemRepository workTiemRepository;
	
	public static WorkTime toEntity(WorkTimeDto dto, Employee employee) {
	    return WorkTime.builder()
	        .employee(employee)
	        .workDate(dto.getWorkDate())
	        .startTime(dto.getStartTime())
	        .endTime(dto.getEndTime())
	        .workStatus(dto.getWorkStatus())
	        .workType(dto.getWorkType())
	        .remarks(dto.getRemarks())
	        .build();
	}

}
