package com.example.cmtProject.mapper.erp.salaries;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.cmtProject.dto.erp.employees.EmpListPreviewDTO;
import com.example.cmtProject.dto.erp.employees.EmpRegistDTO;
import com.example.cmtProject.dto.erp.employees.searchEmpDTO;
import com.example.cmtProject.dto.erp.salaries.SalaryItemDTO;

@Mapper
public interface SalariesMapper {

	List<SalaryItemDTO> salItemTypes();



}
