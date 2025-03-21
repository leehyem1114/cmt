package com.example.cmtProject.repository.erp.employees;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cmtProject.entity.erp.employees.Employees;

public interface EmployeesRepository extends JpaRepository<Employees, Long> {
	
	//사원 ID로 검색
	Employees findByEmpId(String empId);
}