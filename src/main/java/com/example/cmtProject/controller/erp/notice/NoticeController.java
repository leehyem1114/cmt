package com.example.cmtProject.controller.erp.notice;

import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.cmtProject.dto.erp.employees.EmpDTO;
import com.example.cmtProject.dto.erp.employees.EmpRegistDTO;
import com.example.cmtProject.dto.erp.notice.NoticeDTO;
import com.example.cmtProject.entity.erp.notice.Notice;
import com.example.cmtProject.service.erp.employees.EmployeesService;
import com.example.cmtProject.service.erp.notice.NoticeService;

import jakarta.websocket.Session;

@Controller
public class NoticeController {
	@Autowired NoticeService noticeService;
	@Autowired EmployeesService empService;
	
	
	@GetMapping("/noticeList")
	public String noticeList(Model model,NoticeDTO noticeDTO) {
		List<NoticeDTO> noticeList = noticeService.getAllNoticesWithNames();
		model.addAttribute("noticeList",noticeList);
		
		return "erp/notice/noticeList";
	}
	
	@GetMapping("/noticeForm")
	public String noticeForm(Model model, Principal principal,EmpDTO empDTO,NoticeDTO noticeDTO) {
		String empId = principal.getName();
		EmpDTO loginUser = empService.getEmpList(empId);
		model.addAttribute("loginUser", loginUser);
		model.addAttribute("today", LocalDate.now());
		
		return "erp/notice/noticeForm";
	}
	
	@PostMapping("/noticeForm")
	public String registNotice(@ModelAttribute NoticeDTO noticeDTO) {
		int notice = noticeService.regiNoti(noticeDTO);
		if(notice > 0) {
			System.out.println("공지사항 올라감");
			return "redirect:/noticeList";
		} else {
			return "erp/notice/noticeList";
		}
		
	}
	
	@GetMapping("/notice/detail/{id}")
	public String noticeDetail(@PathVariable("id") Long  id,Model model) {
		NoticeDTO noticeList = noticeService.getNoticeDetail(id);
	        model.addAttribute("notice", noticeList);
	        
	        System.out.println(">>>상세" + noticeList);
	        return "erp/notice/noticeDetail";
	    }
	
	@PostMapping("/deleteNotice/{noticeId}")
	@ResponseBody
	public String deleteNotice(@PathVariable("noticeId") Long noticeId,Model model,Principal principal) {
		int result = noticeService.deleteById(noticeId);
		 Map<String, Object> response = new HashMap<>();
		 String empId = principal.getName(); //empid
		 
		 return result > 0 ? "response" : "";
	}
}
