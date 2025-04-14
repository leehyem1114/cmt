package com.example.cmtProject.controller.mes.standardInfoMgt;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Controller
@RequestMapping("/bom")
public class BomInfoController {

	@GetMapping("/bom-info")
	public String getMethodName() {
		
		
		return "mes/standardInfoMgt/bomInfo";
	}
	
	@GetMapping("/excel-file-down")
	public void downloadExcel(HttpServletResponse response) throws IOException {
	    String fileName = "bom_form.xls";
	    String filePath = "/excel/" + fileName;

	    // /static/ 디렉토리 기준으로 파일을 읽어옴
	    log.info("filePath:"+filePath);
	    InputStream inputStream = new ClassPathResource(filePath).getInputStream();

	    log.info("inputStream:"+inputStream);
	    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	    response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

	    // 파일 내용을 응답 스트림에 복사
	    StreamUtils.copy(inputStream, response.getOutputStream());
	    response.flushBuffer();
	}
	
}
