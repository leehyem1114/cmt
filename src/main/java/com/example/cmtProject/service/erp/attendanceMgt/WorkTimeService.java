package com.example.cmtProject.service.erp.attendanceMgt;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cmtProject.dto.erp.attendanceMgt.WorkTimeDTO;
import com.example.cmtProject.entity.erp.attendanceMgt.WorkTime;
import com.example.cmtProject.entity.erp.employees.Employees;
import com.example.cmtProject.repository.erp.attendanceMgt.WorkTimeRepository;
import com.example.cmtProject.repository.erp.employees.EmployeesRepository;

@Service
public class WorkTimeService {

	@Autowired
    private WorkTimeRepository workTimeRepository;
    @Autowired
	private EmployeesRepository employeeRepository;

    // 출결 정보 저장
    @Transactional
    public WorkTimeDTO saveWorkTime(WorkTimeDTO dto) {
        Employees employee = employeeRepository.findById(dto.getEmpNo())
                .orElseThrow(() -> new RuntimeException("사원을 찾을 수 없습니다."));

        WorkTime workTime = WorkTime.toEntity(dto, employee);
        WorkTime savedWorkTime = workTimeRepository.save(workTime);
        return WorkTimeDTO.fromEntity(savedWorkTime);
    }

    // 모든 출결 정보 조회
    public List<WorkTimeDTO> getAllWorkTime() {
        return workTimeRepository.findAll().stream()
                .map(WorkTimeDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // 특정 사원의 출결 정보 조회
    public List<WorkTimeDTO> getWorkTimeByEmployeeId(Long employeeId) {
        return workTimeRepository.findByEmpNo_EmpNo(employeeId).stream()
                .map(WorkTimeDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // 출결 정보 수정
    @Transactional
    public WorkTimeDTO updateWorkTime(Long id, WorkTimeDTO dto) {
    	WorkTime workTime = workTimeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("출결 정보를 찾을 수 없습니다."));
        
    	workTime.setWorkDate(dto.getWorkDate());
    	workTime.setWorkType(dto.getWorkType());
    	workTime.setWorkStatus(dto.getWorkStatus());
    	workTime.setRemarks(dto.getRemarks());

    	WorkTime updatedWorkTime = workTimeRepository.save(workTime);
        return WorkTimeDTO.fromEntity(updatedWorkTime);
    }

    // 출결 정보 삭제
    @Transactional
    public void deleteWorkTime(Long id) {
    	workTimeRepository.deleteById(id);
    }
}