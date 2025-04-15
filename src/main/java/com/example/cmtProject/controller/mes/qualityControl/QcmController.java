package com.example.cmtProject.controller.mes.qualityControl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/qcm")
@Slf4j
public class QcmController {
	
	@GetMapping("quality-info")
	public String getMethodName() {
		return "mes/qualityControl/qcmList";
	}
	

}
