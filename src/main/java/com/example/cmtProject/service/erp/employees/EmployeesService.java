package com.example.cmtProject.service.erp.employees;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.cmtProject.dto.erp.employees.EmpListPreviewDTO;
import com.example.cmtProject.dto.erp.employees.EmpRegistDTO;
import com.example.cmtProject.dto.erp.employees.searchEmpDTO;
import com.example.cmtProject.entity.erp.employees.Employees;
import com.example.cmtProject.mapper.erp.employees.EmployeesMapper;
import com.example.cmtProject.repository.erp.employees.EmployeesRepository;

@Service
public class EmployeesService {
	@Autowired private EmployeesMapper empMapper;
	@Autowired private EmployeesRepository empRepository;
	@Autowired private BCryptPasswordEncoder passwordEncoder;
	
	
	//사원리스트
	public List<EmpListPreviewDTO> getEmplist() {
		return empMapper.selectEmplist();
	}
	//사원검색
	public List<searchEmpDTO> getSearchDept(searchEmpDTO searchEmpDTO) {
		return empMapper.selectDept(searchEmpDTO);
	}
	//사원추가
	public int insertEmp(EmpRegistDTO empRegistDTO) {
		String pw = passwordEncoder.encode(empRegistDTO.getEmpPassword());
		empRegistDTO.setEmpPassword(pw);
		
		return empMapper.insertEmp(empRegistDTO);
	}
	//사원수정
	public int updateEmp(EmpRegistDTO dto) {
		return empMapper.updateEmp(dto);
	}
	
	//멤버 리스트에서 사원조회
	public EmpRegistDTO getEmpDetail(String id) {
		return empMapper.selectEmpDetail(id);
	}
}
