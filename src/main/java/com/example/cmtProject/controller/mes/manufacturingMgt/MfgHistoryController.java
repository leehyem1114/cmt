package com.example.cmtProject.controller.mes.manufacturingMgt;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.cmtProject.dto.mes.manufacturingMgt.MfgHistoryDTO;
import com.example.cmtProject.dto.mes.manufacturingMgt.MfgScheduleDTO;
import com.example.cmtProject.service.mes.manufacturingMgt.MfgHistoryService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/mh")
@Slf4j
public class MfgHistoryController {
	
	@Autowired
	private MfgHistoryService mfgHistoryService;
		

	
	
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
	
}
