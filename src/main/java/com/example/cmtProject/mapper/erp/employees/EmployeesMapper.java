package com.example.cmtProject.mapper.erp.employees;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.multipart.MultipartFile;

import com.example.cmtProject.dto.erp.employees.EmpCountDTO;
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
	//아이디 중복체크
	int selectEmpId(String empId);
	//아이디 찾기
	String selectId(Map<String, String> map);
	//사원 현황
	EmpCountDTO selectCount(EmpCountDTO countDTO);
	//부서별 현황
	List<EmpCountDTO> selectDeptCount(EmpCountDTO countDTO);

}
