package com.example.cmtProject.controller.mes.manufacturingMgt;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.cmtProject.dto.mes.manufacturingMgt.MfgPlanDTO;
import com.example.cmtProject.dto.mes.manufacturingMgt.MfgPlanSalesOrderDTO;
import com.example.cmtProject.dto.mes.manufacturingMgt.MfgScheduleDTO;
import com.example.cmtProject.dto.mes.manufacturingMgt.MfgSchedulePlanDTO;
import com.example.cmtProject.repository.mes.manufacturingMgt.MfgPlansRepository;
import com.example.cmtProject.service.erp.employees.EmployeesService;
import com.example.cmtProject.service.erp.saleMgt.SalesOrderService;
import com.example.cmtProject.service.mes.manufacturingMgt.MfgService;
import com.example.cmtProject.service.mes.standardInfoMgt.ProductService;

@Controller
@RequestMapping("/mfg")
public class MfgController {
	
	@Autowired
	private MfgService mfgService;
	
	@Autowired
	private SalesOrderService salesOrderService;
	
	@Autowired
	private EmployeesService employeesService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private MfgPlansRepository mfgPlansRepository;
	
	// 생산 계획 조회
	@GetMapping("/mfg-plan")
	public String mfgPlan(Model model) {
		
		  // 현재 로그인한 사용자 정보 가져오기
//	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//	    String currentUserId = auth.getName();

		// 생산 계획
		List<MfgPlanDTO> mpList = mfgService.getMfgPlanTotalList();
		
//		  for (MfgPlanDTO dto : mpList) {
//		        dto.setEmpId(currentUserId); // empId 필드에 현재 사용자 ID 설정
//		    }

		model.addAttribute("mpList", mpList);
		
		
		// 수주
		List<MfgPlanSalesOrderDTO> soList = mfgService.getSoList();
		model.addAttribute("soList", soList);
		
		// 제품
//		List<ProductTotalDTO> pdtList = productService.getProductTotalList();
//		model.addAttribute("pdtList", pdtList);
		
		// 사원
//		List<EmpListPreviewDTO> empList = employeesService.getEmpList();
//		model.addAttribute("empList", empList);
		
		return "mes/manufacturingMgt/mfgPlan";
	}
	
	// 생산 계획 등록 조회
	@GetMapping("/mfgPlanRegiList")
	public String mfgPlanRegiList(Model model) {
		List<MfgPlanSalesOrderDTO> soList = mfgService.getSoList();
		model.addAttribute("soList", soList);
		
		System.out.println("soList 확인 : " + soList);
		
	    return "mes/manufacturingMgt/mfgPlan";
	}
	
	// 원자재 재고 조회
	@GetMapping("/selectCurrentQty")
	@ResponseBody
	public boolean selectCurrentQty(@RequestParam("pdtCode") String pdtCode,@RequestParam("soQty") Long soQty) {
		boolean isAvailable = mfgService.isCurrentQtyEnough(pdtCode, soQty);
		return isAvailable;
	}
	
	// 생산 계획 등록
	@PostMapping("/mfgPlanRegi")
	@ResponseBody
	public String mfgPlanRegi(@RequestBody MfgPlanDTO mfgPlanDTO) {
	    // 현재 로그인한 사용자 정보 가져오기
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String currentUserId = auth.getName();

	    // ADMIN 권한 여부 확인
	    boolean isAdmin = auth.getAuthorities().stream()
	            .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

	    // empId: 사원은 본인 ID, 관리자는 null (전체 조회)
	    String empIdForQuery = isAdmin ? null : currentUserId;
	
		mfgService.registMpPlan(mfgPlanDTO);
		
		return "success";
	}
	
	// 생산계획번호 생성
	@ResponseBody
	@GetMapping("/makeMpCode")
	public String makeMpCode(@RequestParam("data") String data) {

	    // 날짜 형태 yyyyMMdd 형태로 변환
		LocalDate today = LocalDate.now();        
	    DateTimeFormatter todayFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
	    String mpToday = today.format(todayFormat);

	    // 해당 날짜의 MP_CODE 등록 수 조회
	    int count = mfgPlansRepository.getNextMpCode(data);

	    String mpCode = "";
	    data = data.replace("-", "");
	    
	    // 생성되어야 할 갯수
	    count++;

	    // 숫자 형식 포맷 처리
	    if (count > 100) {
	        mpCode = "MP-" + data + "-" + count;
	    } else if (count > 10) {
	        mpCode = "MP-" + data + "-" + "0" + count;
	    } else if (count >= 0) {
	        mpCode = "MP-" + data + "-" + "00" + count;
	    } else {
	        mpCode = "minus";
	    }

	    return mpCode;
	}
	
	// 생산 계획 수정
	@PostMapping("/mfgPlanUpdate")
	@ResponseBody
	public String updateMpPlan(@RequestBody MfgPlanDTO mfgPlanDTO) {
		mfgService.updateMpPlan(mfgPlanDTO);
		
		return "success";
	}
	
	// 생산 계획 삭제
	@PostMapping("/mfgPlanDelete")
	@ResponseBody
	public String deleteMpPlan(@RequestBody List<String> mpCodes) {
	    try {
	    	mfgService.deleteMpPlan(mpCodes);
	    	return "success";
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	return "fail";
	    }
	}
	
	// 제조 계획 조회
	@GetMapping("/mfg-schedule")
	public String mfgSchedule(Model model) {
		// 제조 계획
		List<MfgScheduleDTO> msList = mfgService.getMfgScheduleTotalList();
		model.addAttribute("msList", msList);
		
		// 생산 계획
		List<MfgSchedulePlanDTO> mpList = mfgService.getMpList();
		model.addAttribute("mpList", mpList);
		
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ : " + mpList);
		return "mes/manufacturingMgt/mfgSchedule";
	}
	
	// 제품 계획 등록 조회
	@GetMapping("/mfgScheduleRegiList")
	public String mfgScheduleRegiList(Model model) {
		List<MfgSchedulePlanDTO> mpList = mfgService.getMpList();
		model.addAttribute("mpList", mpList);
		
		System.out.println("mpList 확인 : " + mpList);
		
	    return "mes/manufacturingMgt/mfgSchedule";
	}
	
	// 제조 계획 등록
	@PostMapping("/mfgScheduleRegi")
	@ResponseBody
	public String mfgScheduleRegi(@RequestBody List<MfgScheduleDTO> msList) {
		
		mfgService.registMsPlan(msList);
		
		return "success";
	}
	
	// 생산 이력 조회
	@GetMapping("/mfg-history")
	public String mfgHistory(Model model) {
		
		return "mes/manufacturingMgt/mfgHistory";
	}
	

}
