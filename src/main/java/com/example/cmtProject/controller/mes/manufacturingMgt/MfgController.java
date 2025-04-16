package com.example.cmtProject.controller.mes.manufacturingMgt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.cmtProject.service.mes.manufacturingMgt.MfgService;

@Controller
@RequestMapping("/mfg")
public class MfgController {
	
	@Autowired
	private MfgService mfgService;
	
	@GetMapping("/mfg-plan")
	public String mfgPlan() {
		
		return "mes/manufacturingMgt/mfgPlan";
	}
	
	@GetMapping("/mfg-schedule")
	public String mfgSchedule() {
		
		return "mes/manufacturingMgt/mfgSchedule";
	}
	
	@GetMapping("/mfg-history")
	public String mfgHistory() {
		
		return "mes/manufacturingMgt/mfgHistory";
	}
	

}
