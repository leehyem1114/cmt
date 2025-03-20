package com.example.cmtProject.controller.erp.attendanceMgt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/attendance")
public class AttendanceMgtController {

	private static final Logger logger = LoggerFactory.getLogger(AttendanceMgtController.class);

	@GetMapping("/regist")
	public String main() {
		return "erp/attendanceMgt/workTime/regist";
	}
	
	@GetMapping("/list")
	public String list() {
		return "erp/attendanceMgt/workTime/list";
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
