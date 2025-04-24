package com.example.cmtProject.controller.mes.manufacturingMgt;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.cmtProject.dto.mes.manufacturingMgt.MfgScheduleDTO;
import com.example.cmtProject.dto.mes.manufacturingMgt.MfgScheduleDetailDTO;
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
		
		// 제조 계획 상세
		//List<MfgScheduleDetailDTO> msdList = mfgService.getMsdList();
		
		return "mes/manufacturingMgt/mfgSchedule";
	}
	
	// 제조 계획 상세 조회
	@GetMapping("/ms-selected")
	@ResponseBody
	public List<MfgScheduleDetailDTO> getMsdList(@RequestParam("msCode") String msCode) {
	    return mfgScheduleService.getMsdDetailList(msCode);
	}
	
	// 제조 계획 등록
	@PostMapping("/mfgScheduleRegi")
	@ResponseBody
	public String mfgScheduleRegi(@RequestBody List<MfgScheduleDTO> msList) {
		
		mfgScheduleService.registMsPlan(msList);
		
		return "success";
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
	public ResponseEntity<?> saveExcelData(@RequestBody List<MfgScheduleDTO> list) {

		for (MfgScheduleDTO dto : list) {
			mfgScheduleService.saveExcelData(dto); // insert or update
		}
		return ResponseEntity.ok("엑셀 저장 완료");
	}
	
}
