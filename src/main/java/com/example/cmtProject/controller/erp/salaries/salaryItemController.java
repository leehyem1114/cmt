package com.example.cmtProject.controller.erp.salaries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.cmtProject.dto.comm.CommonCodeDetailDTO;
import com.example.cmtProject.dto.comm.CommonCodeDetailNameDTO;
import com.example.cmtProject.dto.erp.salaries.SalaryItemDTO;
import com.example.cmtProject.entity.erp.salaries.PayMent;
import com.example.cmtProject.service.comm.CommonService;
import com.example.cmtProject.service.erp.salaries.SalaryItemService;

@Controller
@RequestMapping("/salaries")
public class salaryItemController {
	@Autowired
	private SalaryItemService salaryItemService;
	@Autowired
	private CommonService commonService;
	
	// 급여 유형
	@GetMapping("/salaryItem")
	public String salaryItemGet(Model model) {
		 	
		// 급여 유형 목록 조회
	    List<SalaryItemDTO> salItemList = salaryItemService.getSalaryItems();
	    model.addAttribute("salItemList", salItemList);
	    
	    // 공통코드에서 급여 유형명 가져오기
 		List<CommonCodeDetailNameDTO> sliTypeList = commonService.getCodeListByGroup("SAL_TYPE"); 
 		model.addAttribute("sliTypeList", sliTypeList);
	    
 		// 급여 유형 불러오기
		List<SalaryItemDTO> getSalItemTypes = salaryItemService.getSalItemTypes();
//		model.addAttribute("", getSalItemTypes);
	    
		// 공통코드에서 급여 유형별 항목 가져오기
// 		List<CommonCodeDetailNameDTO> sliTypeNameList = commonService.getCodeListByGroup("BONUS"); 
	    List<CommonCodeDetailDTO> bonusList = commonService.getCommonCodeDetails("BONUS", "");
	    List<CommonCodeDetailDTO> taxList = commonService.getCommonCodeDetails("TAX", "");
// 		model.addAttribute("bonusList", bonusList);
// 		model.addAttribute("taxList", taxList);
	    
	    // 급여 유형별 항목 합쳐서 가져오기
	    List<CommonCodeDetailDTO> allList = new ArrayList<>();
	    allList.addAll(bonusList);
	    allList.addAll(taxList);
	    model.addAttribute("allList", allList);  // Thymeleaf로 넘김
	    

	    
	    
	    System.out.println("아이템 유형 리스트 : " + salItemList);
		
		
		return "erp/salaries/salaryItemList";
	}
	
	@GetMapping("/getNameByType")
	@ResponseBody
	public Map<String, String> getNameByType(@RequestParam("selectedType") String type) {
		  Map<String, String> result = new HashMap<>();

		   // String name = salaryItemService.getFirstItemNameByType(type);
		  //  result.put("name", name);
		    return result;
	}
	
	// 급여 유형 추가
	@GetMapping("/salItemRegister")
	public String salItemRegister(Model model) {
		model.addAttribute("salaryItemDTO", new SalaryItemDTO());
		model.addAttribute("sliTypeList", salaryItemService.getSalItemTypes());
	    
		// 공통코드에서 급여 유형별 항목 가져오기
// 		List<CommonCodeDetailNameDTO> sliTypeNameList = commonService.getCodeListByGroup("BONUS"); 
	    List<CommonCodeDetailDTO> bonusList = commonService.getCommonCodeDetails("BONUS", "");
	    List<CommonCodeDetailDTO> taxList = commonService.getCommonCodeDetails("TAX", "");
// 		model.addAttribute("bonusList", bonusList);
// 		model.addAttribute("taxList", taxList);
	    
	    // 급여 유형별 항목 합쳐서 가져오기
	    List<CommonCodeDetailDTO> allList = new ArrayList<>();
	    allList.addAll(bonusList);
	    allList.addAll(taxList);
	    model.addAttribute("allList", allList);  // Thymeleaf로 넘김
		return "erp/salaries/salItemRegisterForm"; // 템플릿 경로
	}
	
	// 급여 유형 수정
//	@PutMapping("/salaryItems/{salItemNo}")
//	@ResponseBody
//	public String salaryItemUpdate(@PathVariable("salItemNo") Long salItemNo, @ModelAttribute("salaryItemDTO") SalaryItemDTO salaryItemDTO) {
//	    salaryItemDTO.setSalItemNo(salItemNo);
//	    
//	    salaryItemService.modifySalaryItem(salaryItemDTO);
//	    
//	    return "redirect:/salary/salaryItem";
//	}
	
	
	
//	@GetMapping("/salaryItem")
//	public String salaryItemGet(@RequestParam("salItemValue") String salItemType) {
//		// "수당" → "BONUS", "공제" → "TAX"
////	    String salType = switch (salItemType) {
////	        case "수당" -> "BONUS";
////	        case "공제" -> "TAX";
////	        default -> "";
////	    };
//
////	    String name = salaryItemService.getItemNameByType(salItemType);
////
////	    Map<String, String> result = new HashMap<>();
////	    result.put("name", name);
//	    return "erp/salaries/salaryItem";
//	}
	
	@DeleteMapping("/delete/{sliNo}")
	@ResponseBody
	public ResponseEntity<String> deleteSalItem(@PathVariable("sliNo") Long sliNo) {
		try {
            salaryItemService.deleteSalItem(sliNo);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

	
	
	// 급여 지급 이력
	@GetMapping("/salaryList")
	public String salaryListGet(Model model) {
		List<PayMent> salaryList = salaryItemService.getAllSalaries();
		model.addAttribute("salaryList", salaryList);
	    // 등록 폼을 위한 DTO 객체도 미리 전달
	   // model.addAttribute("salaryItemDTO", new SalaryItemDTO());
		return "erp/salaries/salaryList";
	}
	
	// 급여 대장
	@GetMapping("/payroll")
	public String payrollGet(Model model) {
		//model.addAttribute("monthlyPayroll", monthlyPayroll);
		//model.addAttribute("yearlyPayroll", yearlyPayroll);
		
		return "erp/salaries/payroll";
	}

}
