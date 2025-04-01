package com.example.cmtProject.controller.erp.attendanceMgt;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.cmtProject.dto.erp.attendanceMgt.AttendDTO;
import com.example.cmtProject.dto.erp.attendanceMgt.WorkTimeDTO;
import com.example.cmtProject.entity.erp.employees.Employees;
import com.example.cmtProject.entity.erp.employees.PrincipalDetails;
import com.example.cmtProject.service.erp.attendanceMgt.WorkTimeService;

@Controller
@RequestMapping("/worktimes")
public class WorkTimeController {
	
	private static final Logger logger = LoggerFactory.getLogger(WorkTimeController.class);
	
	@Autowired
	WorkTimeService workTimeService;

	// 출결 정보 목록 페이지 (HTML 렌더링)
    @GetMapping("/list")
    public String showWorkTimePage(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        
    	if (principalDetails == null) {
            return "redirect:/loginForm"; // 로그인 페이지로 리다이렉트
        }
    	// 유저정보
    	Employees loginUser = principalDetails.getUser();
    	
    	// 어드민은 모든정보 보기, 매니저는 자기 부서만, 사원은 자기거만 보기
    	if (principalDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))){
    		// ADMIN은 모든 출결정보 조회
    		List<WorkTimeDTO> workTimeList = workTimeService.getAllAttends();
    		model.addAttribute("attendList", workTimeList);
    		
    	}else if (principalDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"))) {
    		// MANAGER는 같은 부서 출결정보 조회
    		List<WorkTimeDTO> attendList = workTimeService.getAttendsByDept(loginUser.getDeptNo());
        	model.addAttribute("attendList", attendList);
        	
    	} else {
    		// USER는 본인의 출결정보만 조회
    		List<WorkTimeDTO> attendList = workTimeService.getAttendsByEmpNo(loginUser.getEmpNo());
    		model.addAttribute("attendList", attendList);
    	}
        return "erp/attendanceMgt/workTimeList"; // templates/erp/attendanceMgt/attendList.html 렌더링
    }
	

}
