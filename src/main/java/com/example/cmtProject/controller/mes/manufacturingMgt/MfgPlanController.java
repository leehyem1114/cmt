package com.example.cmtProject.controller.mes.manufacturingMgt;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.cmtProject.dto.mes.manufacturingMgt.MfgPlanDTO;
import com.example.cmtProject.dto.mes.manufacturingMgt.MfgPlanSalesOrderDTO;
import com.example.cmtProject.repository.mes.manufacturingMgt.MfgPlanRepository;
import com.example.cmtProject.service.erp.employees.EmployeesService;
import com.example.cmtProject.service.erp.saleMgt.SalesOrderService;
import com.example.cmtProject.service.mes.manufacturingMgt.MfgPlanService;
import com.example.cmtProject.service.mes.standardInfoMgt.ProductService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/mp")
@Slf4j
public class MfgPlanController {
	
	@Autowired
	private MfgPlanService mfgPlanService;
	
	@Autowired
	private SalesOrderService salesOrderService;
	
	@Autowired
	private EmployeesService employeesService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private MfgPlanRepository mfgPlanRepository;
	
	// 생산 계획 조회
	@GetMapping("/mfg-plan")
	public String mfgPlan(Model model) {
		
		  // 현재 로그인한 사용자 정보 가져오기
//	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//	    String currentUserId = auth.getName();

		// 생산 계획
		List<MfgPlanDTO> mpList = mfgPlanService.getMfgPlanTotalList();
		
//		  for (MfgPlanDTO dto : mpList) {
//		        dto.setEmpId(currentUserId); // empId 필드에 현재 사용자 ID 설정
//		    }

		model.addAttribute("mpList", mpList);
		
		
		// 수주
		List<MfgPlanSalesOrderDTO> soList = mfgPlanService.getSoList();
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
		List<MfgPlanSalesOrderDTO> soList = mfgPlanService.getSoList();
		model.addAttribute("soList", soList);
		
		System.out.println("soList 확인 : " + soList);
		
	    return "mes/manufacturingMgt/mfgPlan";
	}
	
	// 원자재 재고 조회
	@GetMapping("/selectCurrentQty")
	@ResponseBody
	public boolean selectCurrentQty(@RequestParam("pdtCode") String pdtCode,@RequestParam("soQty") Long soQty) {
		boolean isAvailable = mfgPlanService.isCurrentQtyEnough(pdtCode, soQty);
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
	
	    mfgPlanService.registMpPlan(mfgPlanDTO);
		
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
	    int count = mfgPlanRepository.getNextMpCode(data);

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
	public ResponseEntity<String> updateBatch(@RequestBody List<MfgPlanDTO> mpList) {
		mfgPlanService.updateMpPlan(mpList);
		
	    return ResponseEntity.ok("success");
	}
	
	// 생산 계획 삭제 (숨김 처리)
    @PostMapping("/mfgPlanDelete")
    @ResponseBody
    public ResponseEntity<String> deleteMpPlan(@RequestBody Map<String, List<Long>> data) {
        List<Long> mpNos = data.get("mpNos");

        // 삭제 로직 실행
        mfgPlanService.isVisiableToFalse(mpNos);

        return ResponseEntity.ok("success");
    }
	
	
	//----------------------------------------------------------------------------------------------------	
	
	
	// 엑셀 파일 다운로드
	@GetMapping("/excel-file-down")
	public void downloadExcel(HttpServletResponse response) throws IOException {
		String fileName = "mp_form.xls";
		String filePath = "/excel/" + fileName;

		// /static/ 디렉토리 기준으로 파일을 읽어옴
		log.info("filePath:" + filePath);
		InputStream inputStream = new ClassPathResource(filePath).getInputStream();

		log.info("inputStream:" + inputStream);
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

		// 파일 내용을 응답 스트림에 복사
		StreamUtils.copy(inputStream, response.getOutputStream());
		response.flushBuffer();
	}

	// 엑셀 파일 적용
	@PostMapping("/SaveExcelData")
	@ResponseBody
	public ResponseEntity<?> saveExcelData(@RequestBody List<MfgPlanDTO> list) {

		for (MfgPlanDTO dto : list) {
			mfgPlanService.saveExcelData(dto); // insert or update
		}
		return ResponseEntity.ok("엑셀 저장 완료");
	}
	
	
	// 생산 이력 조회
	@GetMapping("/mfg-history")
	public String mfgHistory(Model model) {
		
		return "mes/manufacturingMgt/mfgHistory";
	}
	

}
