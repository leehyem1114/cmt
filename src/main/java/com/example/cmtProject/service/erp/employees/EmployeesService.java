package com.example.cmtProject.service.erp.employees;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.dto.erp.employees.EmpListPreviewDTO;
import com.example.cmtProject.dto.erp.employees.searchEmpDTO;
import com.example.cmtProject.entity.erp.employees.Employees;
import com.example.cmtProject.mapper.erp.employees.EmployeesMapper;
import com.example.cmtProject.repository.erp.employees.EmployeesRepository;

@Service
public class EmployeesService {
	@Autowired private EmployeesMapper empMapper;

	public List<EmpListPreviewDTO> getEmplist() {
		return empMapper.selectEmplist();
	}

	public List<searchEmpDTO> getSearchDept(searchEmpDTO searchEmpDTO) {
		return empMapper.selectDept(searchEmpDTO);
	}
}
