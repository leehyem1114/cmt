package com.example.cmtProject.util;

import org.springframework.core.io.ClassPathResource;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.pdf.BaseFont;

import java.io.OutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfGenerator {
	 public void generatePdf(String htmlContent, String outputPath) throws Exception {
		 String outputPath1 = "D:/pdfs/payslip.pdf";
		 File file = new File(outputPath1);
		 File parentDir = file.getParentFile();
		 if (!parentDir.exists()) {
		     parentDir.mkdirs();  // 폴더 자동 생성
		 }
	        try (OutputStream os = new FileOutputStream(outputPath1)) {
	            ITextRenderer renderer = new ITextRenderer();
	            
	            String fontPath = new ClassPathResource("fonts/NanumGothic-Regular.ttf")
	                      .getFile().getAbsolutePath();
	            renderer.getFontResolver().addFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
	            
	            renderer.setDocumentFromString(htmlContent);  // HTML 문자열로 변환
	            renderer.layout();
	            renderer.createPDF(os);
	        }
	    }

}
