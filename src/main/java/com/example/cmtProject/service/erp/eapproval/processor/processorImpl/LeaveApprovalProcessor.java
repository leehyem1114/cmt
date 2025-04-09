package com.example.cmtProject.service.erp.eapproval.processor.processorImpl;

import com.example.cmtProject.dto.erp.attendanceMgt.LeaveDTO;
import com.example.cmtProject.dto.erp.eapproval.ApprovalLineDTO;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;

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
    
    // 휴가 신청서 양식 ID - 실제 양식 ID로 변경 필요
    private static final String LEAVE_FORM_ID = "test001"; 
    
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean processApproved(DocumentDTO document) {
        log.info("휴가 신청 결재 완료 처리 시작: 문서ID={}", document.getDocId());
        
        try {
            // 이미 처리된 휴가가 있는지 확인
            LeaveDTO existingLeave = leaveService.getLeaveByDocId(document.getDocId());
            if (existingLeave != null) {
                log.info("이미 처리된 휴가 신청: {}", document.getDocId());
                return true;
            }
            
            // HTML 파싱
            Document htmlDoc = formDataExtractor.parseHtml(document);
            
            // 휴가 신청 정보 추출
            LeaveDTO leaveDTO = extractLeaveData(htmlDoc, document);
            
            // 문서ID 설정
            leaveDTO.setDocId(document.getDocId());
            
            // 휴가 승인 상태로 설정 - 명시적으로 설정
            leaveDTO.setLevApprovalStatus("승인");
            leaveDTO.setLevApprovalDate(LocalDateTime.now());
            leaveDTO.setLevRemarks("결재 승인 처리");
            
            // 결재자 ID가 없는 경우 현재 사용자로 설정
            if (leaveDTO.getLevApprover() == null) {
                Employees currentUser = SecurityUtil.getCurrentUser();
                if (currentUser != null) {
                    leaveDTO.setLevApprover(currentUser.getEmpId());
                    log.debug("현재 사용자를 결재자로 설정: {}", currentUser.getEmpId());
                }
            }
            
            // 휴가 정보 저장
            Employees currentUser = SecurityUtil.getCurrentUser();
            if (currentUser == null) {
                log.warn("현재 사용자 정보를 가져올 수 없습니다. 기안자 정보 사용");
                currentUser = new Employees();
                currentUser.setEmpId(document.getDrafterId());
            }
            
            leaveService.insertLeaveWithDocId(leaveDTO, currentUser, document.getDocId());
            log.info("휴가 신청 정보 저장 완료: 문서ID={}", document.getDocId());
            
            return true;
        } catch (Exception e) {
            log.error("휴가 신청 처리 중 오류 발생: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean processRejected(DocumentDTO document) {
        log.info("휴가 신청 결재 반려 처리 시작: 문서ID={}", document.getDocId());
        
        try {
            // 이미 처리된 휴가가 있는지 확인
            LeaveDTO existingLeave = leaveService.getLeaveByDocId(document.getDocId());
            if (existingLeave != null) {
                log.info("이미 처리된 휴가 반려: {}", document.getDocId());
                return true;
            }
            
            // HTML 파싱
            Document htmlDoc = formDataExtractor.parseHtml(document);
            
            // 휴가 신청 정보 추출
            LeaveDTO leaveDTO = extractLeaveData(htmlDoc, document);
            
            // 문서ID 설정
            leaveDTO.setDocId(document.getDocId());
            
            // 휴가 반려 상태로 설정 - 명시적으로 설정
            leaveDTO.setLevApprovalStatus("반려");
            leaveDTO.setLevApprovalDate(LocalDateTime.now());
            leaveDTO.setLevRemarks("결재 반려 처리");
            
            // 결재자 ID가 없는 경우 현재 사용자로 설정
            if (leaveDTO.getLevApprover() == null) {
                Employees currentUser = SecurityUtil.getCurrentUser();
                if (currentUser != null) {
                    leaveDTO.setLevApprover(currentUser.getEmpId());
                    log.debug("현재 사용자를 결재자로 설정: {}", currentUser.getEmpId());
                }
            }
            
            // 휴가 정보 저장 (반려 이력 목적)
            Employees currentUser = SecurityUtil.getCurrentUser();
            if (currentUser == null) {
                log.warn("현재 사용자 정보를 가져올 수 없습니다. 기안자 정보 사용");
                currentUser = new Employees();
                currentUser.setEmpId(document.getDrafterId());
            }
            
            leaveService.insertLeaveWithDocId(leaveDTO, currentUser, document.getDocId());
            log.info("휴가 반려 정보 저장 완료: 문서ID={}", document.getDocId());
            
            return true;
        } catch (Exception e) {
            log.error("휴가 반려 처리 중 오류 발생: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean canProcess(String formId) {
        boolean result = LEAVE_FORM_ID.equals(formId);
        log.debug("휴가 양식 처리 가능 여부: formId={}, 결과={}", formId, result);
        return result;
    }
    
    /**
     * HTML 문서에서 휴가 신청 정보 추출
     */
    private LeaveDTO extractLeaveData(Document htmlDoc, DocumentDTO document) {
        log.debug("휴가 신청 정보 추출 시작");
        
        LeaveDTO leaveDTO = new LeaveDTO();
        
        // 기안자 정보 설정
        leaveDTO.setEmpId(document.getDrafterId());
        
        // 휴가 유형 추출
        String leaveType = extractLeaveType(htmlDoc);
        leaveDTO.setLevType(leaveType);
        
        // 휴가 일자 추출
        LocalDateTime startDate = extractStartDate(htmlDoc);
        LocalDateTime endDate = extractEndDate(htmlDoc, startDate);
        
        leaveDTO.setLevStartDate(startDate);
        leaveDTO.setLevEndDate(endDate);
        
        // 휴가 일수 추출
        int days = extractLeaveDays(htmlDoc, startDate, endDate);
        leaveDTO.setLevDays(days);
        
        // 남은 휴가 일수 설정 (임시값, 실제로는 사용자 정보에서 가져와야 함)
        leaveDTO.setLevLeftDays(15 - days); 
        
        // test위해 사용한일수 임의값
        leaveDTO.setLevUsedDays(2);
        
        // 휴가 사유 추출
        String reason = extractLeaveReason(htmlDoc);
        leaveDTO.setLevReason(reason);
        
        // 신청일시 - 문서 기안일로 설정
        leaveDTO.setLevReqDate(document.getDraftDate());
        
        //최종 결재자 ID
        if(document.getApprovalLines() != null && !document.getApprovalLines().isEmpty()) {
        
            ApprovalLineDTO lastApprover = document.getApprovalLines()
                    .stream()
                    .max(Comparator.comparing(ApprovalLineDTO::getApprovalOrder))
                    .orElse(null);
            
            leaveDTO.setLevApprover(lastApprover.getApproverId());
        }
        
        //상태 
        
        leaveDTO.setLevApprovalStatus(document.getDocStatus());
        
        log.debug("휴가 신청 정보 추출 완료: {}", leaveDTO);
        return leaveDTO;
    }
    
    /**
     * 휴가 유형 추출
     */
    private String extractLeaveType(Document htmlDoc) {
        // 현재 양식에 맞는 선택자들
        String[] selectors = {
            "#leaveType", 
            "select[id='leaveType']",
            "select.form-select",
            "select[name='leaveType']"
        };
        
        for (String selector : selectors) {
            String value = formDataExtractor.extractField(htmlDoc, selector);
            log.debug("휴가 유형 추출 시도 ({}): '{}'", selector, value);
            
            if (value != null && !value.trim().isEmpty()) {
                return value;
            }
        }
        
        // 기본값 반환
        log.debug("휴가 유형을 찾을 수 없어 기본값 사용: '연차'");
        return "연차";
    }
    
    /**
     * 시작 날짜 추출
     */
    private LocalDateTime extractStartDate(Document htmlDoc) {
        // 현재 양식에 맞는 선택자들
        String[][] selectorFormats = {
            {"#startDate", "yyyy-MM-dd"},
            {"input[id='startDate']", "yyyy-MM-dd"},
            {"#leaveStartDate", "yyyy-MM-dd"},
            {"input[name='leaveStartDate']", "yyyy-MM-dd"}
        };
        
        for (String[] pair : selectorFormats) {
            LocalDateTime date = formDataExtractor.extractDateField(htmlDoc, pair[0], pair[1]);
            log.debug("시작일 추출 시도 ({}): {}", pair[0], date);
            
            if (date != null) {
                return date;
            }
        }
        
        // 기본값 반환 (현재 날짜)
        log.debug("시작일을 찾을 수 없어 현재 날짜 사용");
        return LocalDateTime.now();
    }
    
    /**
     * 종료 날짜 추출
     */
    private LocalDateTime extractEndDate(Document htmlDoc, LocalDateTime defaultDate) {
        // 현재 양식에 맞는 선택자들
        String[][] selectorFormats = {
            {"#endDate", "yyyy-MM-dd"},
            {"input[id='endDate']", "yyyy-MM-dd"},
            {"#leaveEndDate", "yyyy-MM-dd"},
            {"input[name='leaveEndDate']", "yyyy-MM-dd"}
        };
        
        for (String[] pair : selectorFormats) {
            LocalDateTime date = formDataExtractor.extractDateField(htmlDoc, pair[0], pair[1]);
            log.debug("종료일 추출 시도 ({}): {}", pair[0], date);
            
            if (date != null) {
                return date;
            }
        }
        
        // 기본값 반환 (시작일과 동일)
        log.debug("종료일을 찾을 수 없어 시작일과 동일하게 설정");
        return defaultDate;
    }
    
    /**
     * 휴가 사유 추출
     */
    private String extractLeaveReason(Document htmlDoc) {
        // 현재 양식에 맞는 선택자들
        String[] selectors = {
            "#leaveReason", 
            "textarea[id='leaveReason']",
            "textarea[name='leaveReason']",
            "textarea.form-control"
        };
        
        for (String selector : selectors) {
            String value = formDataExtractor.extractField(htmlDoc, selector);
            log.debug("휴가 사유 추출 시도 ({}): '{}'", selector, value);
            
            if (value != null && !value.trim().isEmpty()) {
                return value;
            }
        }
        
        // 기본값 반환
        return "휴가 신청";
    }
    
    /**
     * 휴가일수 추출 - 자동 계산된 값 활용
     */
    private int extractLeaveDays(Document htmlDoc, LocalDateTime startDate, LocalDateTime endDate) {
        // 자동 계산된 휴가일수 추출 시도
        String leaveDaysStr = formDataExtractor.extractField(htmlDoc, "#leaveDays");
        log.debug("자동 계산된 휴가일수 추출 시도: '{}'", leaveDaysStr);
        
        if (leaveDaysStr != null && !leaveDaysStr.trim().isEmpty()) {
            try {
                // 숫자로 파싱 시도
                return Double.valueOf(leaveDaysStr).intValue();
            } catch (NumberFormatException e) {
                log.debug("휴가일수 파싱 실패: {}", leaveDaysStr);
            }
        }
        
        // 자동 계산 값이 없으면 직접 계산
        if (startDate != null && endDate != null) {
            return (int) ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate()) + 1;
        }
        
        // 기본값
        return 1;
    }

}
