package com.example.cmtProject.controller.mes.manufacturingMgt;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.cmtProject.comm.response.ApiResponse;
import com.example.cmtProject.dto.mes.manufacturingMgt.MfgScheduleDTO;
import com.example.cmtProject.dto.mes.manufacturingMgt.MfgScheduleDetailDTO;
import com.example.cmtProject.dto.mes.manufacturingMgt.MfgSchedulePlanDTO;
import com.example.cmtProject.service.mes.manufacturingMgt.MfgScheduleService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/ms")
@Slf4j
public class MfgScheduleController {
	
	@Autowired
	private MfgScheduleService mfgScheduleService;
		
	// 제조 계획 조회
	@GetMapping("/mfg-schedule")
	public String mfgSchedule(Model model) {
		
		// 제조 계획
		List<MfgScheduleDTO> msList = mfgScheduleService.getMfgScheduleTotalList();
		model.addAttribute("msList", msList);
		
		// 생산 계획
		List<MfgSchedulePlanDTO> mpList = mfgScheduleService.getMpList();
		model.addAttribute("mpList", mpList);
		
		return "mes/manufacturingMgt/mfgSchedule";
	}
	
	// 제조 계획 등록 조회 (생산 계획 데이터)
	@GetMapping("/mfgScheduleRegiList")
	public String mfgScheduleRegiList(Model model) {
		List<MfgSchedulePlanDTO> mpList = mfgScheduleService.getMpList();
		
		return "mes/manufacturingMgt/mfgSchedule";
	}
	
	// 제조 계획 등록
	@PostMapping("/mfgScheduleRegi")
	@ResponseBody
	public String mfgScheduleRegi(@RequestBody List<MfgScheduleDTO> msList) {
		
		for(MfgScheduleDTO dto : msList) {
			// 제조 계획 등록
			mfgScheduleService.registMsPlan(dto);
			// 제조 계획 등록 시 생산 계획 상태 업데이트
			mfgScheduleService.updateMpStatus(dto.getMpCode());
		}
		return "success";
	}
	
	// 제조 계획 상세 조회
	@GetMapping("/ms-selected")
	@ResponseBody
	public List<MfgScheduleDetailDTO> getMsdList(@RequestParam("msCode") String msCode) {
	    return mfgScheduleService.getMsdDetailList(msCode);
	}
	

	
	
	//----------------------------------------------------------------------------------------------------	
	
	// 엑셀 파일 다운로드
	@GetMapping("/excel-file-down")
	public void downloadExcel(HttpServletResponse response) throws IOException {
		String fileName = "ms_form.xls";
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
	@PostMapping("/saveExcelData")
	@ResponseBody
	public ResponseEntity<?> saveExcelData(@RequestBody List<MfgScheduleDTO> list) {

		for (MfgScheduleDTO dto : list) {
			mfgScheduleService.saveExcelData(dto); // insert or update
		}
		return ResponseEntity.ok("엑셀 저장 완료");
	}
	
	@GetMapping("/detail")
	@ResponseBody
	public ApiResponse<List<Map<String, Object>>> getMsDetail(@RequestParam("msCode") String msCode) {
	    List<Map<String, Object>> msdList = mfgScheduleService.getBomDetailByMsCode(msCode);
	    
	    System.out.println("디테일================" + msdList);
	    
	    return ApiResponse.success(msdList);
	}

	
	
	
}
