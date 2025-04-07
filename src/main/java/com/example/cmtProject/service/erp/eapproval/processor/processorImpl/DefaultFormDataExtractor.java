package com.example.cmtProject.service.erp.eapproval.processor.processorImpl;

import com.example.cmtProject.dto.erp.eapproval.DocumentDTO;
import com.example.cmtProject.service.erp.eapproval.processor.FormDataExtractor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 결재 문서 데이터 추출 구현체
 */
@Component
@Slf4j
public class DefaultFormDataExtractor implements FormDataExtractor {

    @Override
    public Document parseHtml(DocumentDTO document) {
        try {
            String content = document.getContent();
            if (content == null || content.trim().isEmpty()) {
                log.warn("문서 내용이 비어있습니다: {}", document.getDocId());
                return Jsoup.parse("<html><body></body></html>");
            }
            
            return Jsoup.parse(content);
        } catch (Exception e) {
            log.error("HTML 파싱 중 오류 발생: {}", e.getMessage(), e);
            return Jsoup.parse("<html><body></body></html>");
        }
    }

    @Override
    public String extractField(Document htmlDoc, String selector) {
        try {
            Elements elements = htmlDoc.select(selector);
            if (elements.isEmpty()) {
                log.warn("선택자로 요소를 찾을 수 없습니다: {}", selector);
                return "";
            }
            
            // input, select 등 폼 요소 처리
            if (elements.is("input[type=text], input[type=hidden], input:not([type])")) {
                return elements.val();
            } else if (elements.is("input[type=checkbox], input[type=radio]")) {
                return elements.hasAttr("checked") ? elements.val() : "";
            } else if (elements.is("select")) {
                return elements.select("option[selected]").val();
            } else if (elements.is("textarea")) {
                return elements.val();
            }
            
            // 일반 요소의 경우 텍스트 반환
            return elements.text();
        } catch (Exception e) {
            log.error("필드 추출 중 오류 발생: {}", e.getMessage(), e);
            return "";
        }
    }

    @Override
    public LocalDateTime extractDateField(Document htmlDoc, String selector, String pattern) {
        try {
            String dateStr = extractField(htmlDoc, selector);
            if (dateStr == null || dateStr.trim().isEmpty()) {
                return null;
            }
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return LocalDateTime.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            log.error("날짜 필드 파싱 중 오류 발생: {}", e.getMessage(), e);
            return null;
        }
    }
}