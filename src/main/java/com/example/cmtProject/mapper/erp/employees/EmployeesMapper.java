package com.example.cmtProject.mapper.erp.employees;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.cmtProject.dto.erp.employees.EmpListPreviewDTO;

@Mapper
public interface EmployeesMapper {

	List<EmpListPreviewDTO> selectEmplist();

}
