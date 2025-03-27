package com.example.cmtProject.controller.erp.attendanceMgt;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.cmtProject.dto.erp.attendanceMgt.AttendDTO;
import com.example.cmtProject.service.erp.attendanceMgt.AttendService;

@Controller
@RequestMapping("/attends")
public class AttendController {
	
	private static final Logger logger = LoggerFactory.getLogger(AttendController.class);
	
	@Autowired
    private AttendService attendService;

    // 출결 정보 목록 페이지 (HTML 렌더링)
    @GetMapping("/list")
    public String showAttendPage(Model model) {
        List<AttendDTO> attendList = attendService.getAllAttends();
        model.addAttribute("attendList", attendList);
        return "erp/attendanceMgt/attendList"; // templates/erp/attendanceMgt/attendList.html 렌더링
    }

    // 출결 정보 삭제
    @GetMapping("/delete/{id}")
    public String deleteAttend(@PathVariable Long id) {
        attendService.deleteAttend(id);
        return "redirect:/attends/view";
    }

//    // 출결 정보 등록
//    @PostMapping
//    public String createAttend(@ModelAttribute AttendDTO dto) {
//        attendService.saveAttend(dto);
//        return "redirect:/attends/view"; // 등록 후 리스트 페이지로 리다이렉트
//    }
//
//    // 특정 사원의 출결 정보 조회 (HTML 렌더링)
//    @GetMapping("/employee/{employeeId}")
//    public String getAttendsByEmployee(@PathVariable Long employeeId, Model model) {
//        List<AttendDTO> attends = attendService.getAttendsByEmployeeId(employeeId);
//        model.addAttribute("attends", attends);
//        return "erp/attendanceMgt/attendList"; // 특정 사원의 출결 정보를 렌더링
//    }
//
//    // 출결 정보 수정
//    @PostMapping("/update/{id}")
//    public String updateAttend(@PathVariable Long id, @ModelAttribute AttendDTO dto) {
//        attendService.updateAttend(id, dto);
//        return "redirect:/attends/view";
//    }
    
    
    
    
    
    
    
    
    
    
}