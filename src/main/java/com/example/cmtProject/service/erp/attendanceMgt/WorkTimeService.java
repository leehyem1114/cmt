package com.example.cmtProject.service.erp.attendanceMgt;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cmtProject.dto.erp.attendanceMgt.WorkTimeDTO;
import com.example.cmtProject.mapper.erp.attendanceMgt.WorkTimeMapper;
import com.example.cmtProject.repository.erp.attendanceMgt.WorkTimeRepository;

@Service
public class WorkTimeService {

	@Autowired
    private WorkTimeRepository workTimeRepository;
	
	@Autowired 
	private WorkTimeMapper workTimeMapper;

	// 모든 근무 시간 정보 조회
	public List<WorkTimeDTO> getAllAttends() {
		return workTimeMapper.getAllAttends();
	}
	
	// 같은 부서 출결 정보 조회
	public List<WorkTimeDTO> getAttendsByDept(Long deptNo) {
		return workTimeMapper.getAttendsByDept(deptNo);
	}

	// 사원 하나의 정보만 조회
	public List<WorkTimeDTO> getAttendsByEmpNo(Long empNo) {
		return workTimeMapper.getAttendsByEmpNo(empNo);
	}
	
	
	
	// 출결 정보 삭제
    @Transactional
    public void deleteWorkTime(Long id) {
    	workTimeRepository.deleteById(id);
    }

	
}