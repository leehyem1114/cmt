package com.example.cmtProject.controller.mes.qualityControl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.cmtProject.dto.mes.qualityControl.FqcDTO;
import com.example.cmtProject.dto.mes.qualityControl.IqcDTO;
import com.example.cmtProject.entity.erp.employees.PrincipalDetails;
import com.example.cmtProject.entity.mes.qualityControl.Fqc;
import com.example.cmtProject.repository.mes.qualityControl.FqcRepository;
import com.example.cmtProject.service.mes.qualityControl.FqcService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;



@Controller
@RequestMapping("/fqc")
@Slf4j
public class FqcController {
	
	@Autowired
	private FqcService fqcService;
	
	@Autowired
	private FqcRepository fqcRepository;
	
	
	
	@GetMapping("/inspection-info")
	public String getFqcInfo(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		
		if (principalDetails == null) {
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        }
    	// 유저정보
    	String loginUser = principalDetails.getUsername();
    	
    	// 현재 유저 Role 넘겨주기
    	model.addAttribute("userRole", principalDetails.getAuthorities().iterator().next().getAuthority());
    	
    	// FQC 리스트
    	List<FqcDTO> fqcList = fqcService.getAllFqc();
    	model.addAttribute("fqcList", fqcList);
    	log.info("" + fqcList);
    	
    	
    	return "mes/qualityControl/fqcList";
	}
	
	
	// 삭제 메서드
    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<String> deleteFqc(@RequestBody Map<String, List<Long>> data) {
        List<Long> ids = data.get("ids");

        // 삭제 로직 실행 (예: attendService.deleteByIds(ids))
        fqcRepository.deleteAllById(ids);

        return ResponseEntity.ok("success");
    }
	
	
	
//----------------------------------------------------------------------------------------------------	
	
	
	//엑셀 파일 다운로드
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
	
	// 엑셀 파일 적용
	@PostMapping("/saveExcelData")
	@ResponseBody
	public ResponseEntity<?> saveExcelData(@RequestBody List<FqcDTO> list) {

	    for (FqcDTO dto : list) {
	        fqcService.saveExcelData(dto); // insert or update
	    }
	    return ResponseEntity.ok("엑셀 저장 완료");
	}
	
	


}
