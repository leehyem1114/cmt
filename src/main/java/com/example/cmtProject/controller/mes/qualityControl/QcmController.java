package com.example.cmtProject.controller.mes.qualityControl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.cmtProject.dto.erp.attendanceMgt.AttendDTO;
import com.example.cmtProject.dto.mes.qualityControl.QcmDTO;
import com.example.cmtProject.entity.erp.employees.Employees;
import com.example.cmtProject.entity.erp.employees.PrincipalDetails;
import com.example.cmtProject.service.mes.qualityControl.QcmService;

import lombok.extern.slf4j.Slf4j;


@Controller
@RequestMapping("/qcm")
@Slf4j
public class QcmController {
	
	@Autowired
	private QcmService qcmService;
	
	@GetMapping("quality-info")
	public String getMethodName(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		
		if (principalDetails == null) {
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        }
    	// 유저정보
    	Employees loginUser = principalDetails.getUser();
		
    	List<QcmDTO> qcmList = qcmService.getAllQcm();
    	model.addAttribute("qcmList", qcmList);
    	
		return "mes/qualityControl/qcmList";
	}
	

}
