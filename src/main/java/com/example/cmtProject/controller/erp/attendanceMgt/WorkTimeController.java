package com.example.cmtProject.controller.erp.attendanceMgt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/worktimes")
public class WorkTimeController {
	
	private static final Logger logger = LoggerFactory.getLogger(WorkTimeController.class);


	@GetMapping("/list")
	public String main() {
		return "erp/attendanceMgt/worktimeList";
	}
	

}
