package com.example.cmtProject.controller.erp.eapproval;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.cmtProject.entity.Eapproval;

@Controller
@RequestMapping("/eapproval")
public class eapprovalController {
	
	
	@GetMapping("/approvalList")
	public String eapprovalListGET(Model model, Eapproval eapVO) {
//		List<eapVO>eapList = 
		
		
		
		return "erp/eapproval/approvalList";
	}

}
