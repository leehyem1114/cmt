package com.example.cmtProject.service.erp.employees;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.cmtProject.entity.erp.employees.Employees;
import com.example.cmtProject.entity.erp.employees.PrincipalDetails;
import com.example.cmtProject.repository.erp.employees.EmployeesRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final EmployeesRepository employeesRepository;

    @Override
    public UserDetails loadUserByUsername(String empId) throws UsernameNotFoundException {
        Employees user = employeesRepository.findByEmpId(empId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        return new PrincipalDetails(user);
    }
}
