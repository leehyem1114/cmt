package com.example.cmtProject.mapper.erp.employees;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.cmtProject.dto.erp.employees.EmpListPreviewDTO;
import com.example.cmtProject.dto.erp.employees.EmpRegistDTO;
import com.example.cmtProject.dto.erp.employees.searchEmpDTO;

@Mapper
public interface EmployeesMapper {

	List<EmpListPreviewDTO> selectEmplist();

	List<searchEmpDTO> selectDept(searchEmpDTO searchEmpDTO);

	int insertEmp(EmpRegistDTO empRegistDTO);

}
