package com.example.cmtProject.mapper.erp.employees;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.multipart.MultipartFile;

import com.example.cmtProject.dto.erp.employees.EmpListPreviewDTO;
import com.example.cmtProject.dto.erp.employees.EmpRegistDTO;
import com.example.cmtProject.dto.erp.employees.searchEmpDTO;

@Mapper
public interface EmployeesMapper {

	List<EmpListPreviewDTO> selectEmplist();

	List<searchEmpDTO> selectDept(searchEmpDTO searchEmpDTO);

	int insertEmp(EmpRegistDTO empRegistDTO);


	int updateEmp(EmpRegistDTO dto);

	EmpRegistDTO  selectEmpDetail(String id);


	EmpRegistDTO selectMyEmpList(String empId);

	int updateEmpDetail(String id);

}
