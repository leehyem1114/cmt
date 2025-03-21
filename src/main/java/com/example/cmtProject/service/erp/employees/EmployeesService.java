package com.example.cmtProject.service.erp.employees;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.repository.erp.employees.EmployeesRepository;

@Service
public class EmployeesService {
	@Autowired private EmployeesRepository empRepository;
}
