package com.example.cmtProject.repository.erp.employees;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cmtProject.entity.erp.employees.Employees;

public interface EmployeesRepository extends JpaRepository<Employees, Long> {
	 Optional<Employees> findByEmpId(String empId);
	
	
	
}