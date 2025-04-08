package com.example.cmtProject.service.erp.eapproval.processor.processorImpl;

import com.example.cmtProject.dto.erp.attendanceMgt.LeaveDTO;
import com.example.cmtProject.dto.erp.eapproval.DocumentDTO;
import com.example.cmtProject.entity.erp.employees.Employees;
import com.example.cmtProject.service.erp.attendanceMgt.LeaveService;
import com.example.cmtProject.service.erp.eapproval.processor.ApprovalPostProcessor;
import com.example.cmtProject.service.erp.eapproval.processor.FormDataExtractor;
import com.example.cmtProject.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 휴가 신청 결재 후처리기
 * 결재 완료 시 휴가 신청 처리, 반려 시 휴가 취소 처리
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LeaveApprovalProcessor implements ApprovalPostProcessor {
    private final FormDataExtractor formDataExtractor;
    private final LeaveService leaveService;
    
    private static final String LEAVE_FORM_ID = "FORM_LEAVE";

    @Override
    public boolean processApproved(DocumentDTO document) {
        try {
            // HTML 파싱 및 데이터 추출
            Document htmlDoc = formDataExtractor.parseHtml(document);
            LeaveDTO leaveDTO = extractLeaveData(htmlDoc, document);
            
            // 기존 LeaveService 메서드 호출 - 비즈니스 로직은 서비스에 위임
//            leaveService.processApprovedLeave(leaveDTO, document.getDocId());
//            leaveService.insertLeave(leaveDTO, );
            log.debug("@@@@@@@@@@@@@@@@@@@@@" + leaveDTO);
            
            return true;
        } catch (Exception e) {
            log.error("휴가 승인 처리 중 오류: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean processRejected(DocumentDTO document) {
        try {
            // HTML 파싱 및 데이터 추출
            Document htmlDoc = formDataExtractor.parseHtml(document);
            LeaveDTO leaveDTO = extractLeaveData(htmlDoc, document);
            
            // 반려 사유
            String rejectReason = "결재 반려: " + document.getDocId();
            
            // 기존 LeaveService 메서드 호출
//            leaveService.processRejectedLeave(leaveDTO, document.getDocId(), rejectReason);
            
            return true;
        } catch (Exception e) {
            log.error("휴가 반려 처리 중 오류: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean canProcess(String formId) {
        return LEAVE_FORM_ID.equals(formId);
    }
    
    // 문서에서 휴가 신청 정보 추출 - 데이터 추출에만 집중
    private LeaveDTO extractLeaveData(Document htmlDoc, DocumentDTO document) {
        LeaveDTO leaveDTO = new LeaveDTO();
        
        // 기안자 정보 설정
        leaveDTO.setEmpId(document.getDrafterId());
        leaveDTO.setEmpName(document.getDrafterName());
        
        // 휴가 유형 설정
        String leaveType = formDataExtractor.extractField(htmlDoc, "#leaveType");
        leaveDTO.setLevType(leaveType);
        
        // 휴가 일자 설정
        LocalDateTime startDate = formDataExtractor.extractDateField(
            htmlDoc, "#leaveStartDate", "yyyy-MM-dd");
        LocalDateTime endDate = formDataExtractor.extractDateField(
            htmlDoc, "#leaveEndDate", "yyyy-MM-dd");
        
        leaveDTO.setLevStartDate(startDate);
        leaveDTO.setLevEndDate(endDate);
        
        // 휴가 일수 계산
        if (startDate != null && endDate != null) {
            int days = (int) ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate()) + 1;
            leaveDTO.setLevDays(days);
        }
        
        // 휴가 사유
        String reason = formDataExtractor.extractField(htmlDoc, "#leaveReason");
        leaveDTO.setLevReason(reason);
        
        // 신청일시 - 문서 기안일로 설정
        leaveDTO.setLevReqDate(document.getDraftDate());
        
        return leaveDTO;
    }
}