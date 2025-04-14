package com.example.cmtProject.controller.mes.standardInfoMgt;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
	public String getMethodName(Model model) {
		
		/*
		// 1. 임시 데이터 생성
	    List<Map<String, Object>> data = new ArrayList<>();

	    Map<String, Object> row1 = new HashMap<>();
	    row1.put("soNo", "SO-20240401");
	    row1.put("soDate", "2024-04-01");

	    Map<String, Object> row2 = new HashMap<>();
	    row2.put("soNo", "SO-20240402");
	    row2.put("soDate", "2024-04-02");

	    data.add(row1);
	    data.add(row2);

	    // 2. Grid 옵션 설정
	    Map<String, Object> gridOptions = new HashMap<>();
	    gridOptions.put("columns", List.of(
	        Map.of("name", "soNo", "header", "OrderNumber", "width", 120),
	        Map.of("name", "soDate", "header", "OrderDate", "width", 150)
	    ));
	    gridOptions.put("data", data);

	    // 3. Thymeleaf에 전달
	    model.addAttribute("gridOptions", gridOptions);
		log.info("gridOptions:"+gridOptions);
		*/
		
		
		
		//------------------------ 그리드 컬럼과 데이터 생성 ---------------------------------
		List<Map<String, Object>> columnDefs = List.of(
		    Map.of("header", "제품명", "name", "productName"),
		    Map.of("header", "수량", "name", "quantity")
		);
	
		List<Map<String, Object>> data = List.of(
		    Map.of("productName", "차체 A", "quantity", 100),
		    Map.of("productName", "차체 B", "quantity", 200)
		);
	
		model.addAttribute("gridOptions", Map.of(
		    "columns", columnDefs,
		    "data", data
		));
		
		
		
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
