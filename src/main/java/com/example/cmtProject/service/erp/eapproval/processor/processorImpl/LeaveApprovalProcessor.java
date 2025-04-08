//package com.example.cmtProject.service.erp.eapproval.processor.processorImpl;
//
//import com.example.cmtProject.dto.erp.attendanceMgt.LeaveDTO;
//import com.example.cmtProject.dto.erp.eapproval.DocumentDTO;
//import com.example.cmtProject.entity.erp.employees.Employees;
//import com.example.cmtProject.service.erp.attendanceMgt.LeaveService;
//import com.example.cmtProject.service.erp.eapproval.processor.ApprovalPostProcessor;
//import com.example.cmtProject.service.erp.eapproval.processor.FormDataExtractor;
//import com.example.cmtProject.util.SecurityUtil;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.jsoup.nodes.Document;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Propagation;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.time.temporal.ChronoUnit;
//
//
///**
// * 휴가 신청 결재 후처리기
// * 결재 완료 시 휴가 신청 처리, 반려 시 휴가 취소 처리
// */
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class LeaveApprovalProcessor implements ApprovalPostProcessor {
//
//    private final FormDataExtractor formDataExtractor;
//    private final LeaveService leaveService;
//    
//    // 휴가 신청서 양식 ID - 실제 양식 ID로 변경
//    private static final String LEAVE_FORM_ID = "ㅅ교"; 
//    
//    @Override
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    public boolean processApproved(DocumentDTO document) {
//        log.info("휴가 신청 결재 완료 처리 시작: 문서ID={}", document.getDocId());
//        
//        try {
//            // 이미 처리된 휴가가 있는지 확인
//            LeaveDTO existingLeave = leaveService.getLeaveByDocId(document.getDocId());
//            if (existingLeave != null) {
//                log.info("이미 처리된 휴가 신청: {}", document.getDocId());
//                return true;
//            }
//            
//            // HTML 파싱
//            Document htmlDoc = formDataExtractor.parseHtml(document);
//            
//            // 휴가 신청 정보 추출
//            LeaveDTO leaveDTO = extractLeaveData(htmlDoc, document);
//            
//            // 휴가 승인 상태로 설정
//            leaveDTO.setLevApprovalStatus("승인");
//            leaveDTO.setLevApprovalDate(LocalDateTime.now());
//            leaveDTO.setLevRemarks("결재 승인: " + document.getDocId());
//            
//            // 휴가 정보 저장
//            Employees currentUser = SecurityUtil.getCurrentUser();
//            leaveService.insertLeaveWithDocId(leaveDTO, currentUser, document.getDocId());
//            log.info("휴가 신청 정보 저장 완료: 문서ID={}", document.getDocId());
//            
//            return true;
//        } catch (Exception e) {
//            log.error("휴가 신청 처리 중 오류 발생: {}", e.getMessage(), e);
//            return false;
//        }
//    }
//
//    @Override
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    public boolean processRejected(DocumentDTO document) {
//        log.info("휴가 신청 결재 반려 처리 시작: 문서ID={}", document.getDocId());
//        
//        try {
//            // 이미 처리된 휴가가 있는지 확인
//            LeaveDTO existingLeave = leaveService.getLeaveByDocId(document.getDocId());
//            if (existingLeave != null) {
//                log.info("이미 처리된 휴가 반려: {}", document.getDocId());
//                return true;
//            }
//            
//            // HTML 파싱
//            Document htmlDoc = formDataExtractor.parseHtml(document);
//            
//            // 휴가 신청 정보 추출
//            LeaveDTO leaveDTO = extractLeaveData(htmlDoc, document);
//            
//            // 휴가 반려 상태로 설정
//            leaveDTO.setLevApprovalStatus("반려");
//            leaveDTO.setLevApprovalDate(LocalDateTime.now());
//            leaveDTO.setLevRemarks("결재 반려: " + document.getDocId());
//            
//            // 휴가 정보 저장 (반려 이력 목적)
//            Employees currentUser = SecurityUtil.getCurrentUser();
//            leaveService.insertLeaveWithDocId(leaveDTO, currentUser, document.getDocId());
//            log.info("휴가 반려 정보 저장 완료: 문서ID={}", document.getDocId());
//            
//            return true;
//        } catch (Exception e) {
//            log.error("휴가 반려 처리 중 오류 발생: {}", e.getMessage(), e);
//            return false;
//        }
//    }
//
//    @Override
//    public boolean canProcess(String formId) {
//        boolean result = LEAVE_FORM_ID.equals(formId);
//        log.debug("휴가 양식 처리 가능 여부: formId={}, 결과={}", formId, result);
//        return result;
//    }
//    
//    /**
//     * HTML 문서에서 휴가 신청 정보 추출
//     */
//    private LeaveDTO extractLeaveData(Document htmlDoc, DocumentDTO document) {
//        log.debug("휴가 신청 정보 추출 시작");
//        
//        LeaveDTO leaveDTO = new LeaveDTO();
//        
//        // 기안자 정보 설정
//        leaveDTO.setEmpId(document.getDrafterId());
//        
//        // 휴가 유형 설정 - 선택자 이름이 다를 수 있으므로 로그 추가
//        String leaveType = formDataExtractor.extractField(htmlDoc, "#leaveType");
//        log.debug("추출된 휴가 유형: {}", leaveType);
//        if (leaveType == null || leaveType.isEmpty()) {
//            // 대체 선택자 시도
//            leaveType = formDataExtractor.extractField(htmlDoc, "select[name='leaveType']");
//            log.debug("대체 선택자로 추출된 휴가 유형: {}", leaveType);
//            
//            // 여전히 비어있으면 기본값 설정
//            if (leaveType == null || leaveType.isEmpty()) {
//                leaveType = "연차";
//                log.debug("휴가 유형 기본값 사용: {}", leaveType);
//            }
//        }
//        leaveDTO.setLevType(leaveType);
//        
//        // 휴가 일자 설정 - 다양한 형식 시도
//        LocalDateTime startDate = formDataExtractor.extractDateField(htmlDoc, "#leaveStartDate", "yyyy-MM-dd");
//        if (startDate == null) {
//            startDate = formDataExtractor.extractDateField(htmlDoc, "input[name='leaveStartDate']", "yyyy-MM-dd");
//        }
//        
//        LocalDateTime endDate = formDataExtractor.extractDateField(htmlDoc, "#leaveEndDate", "yyyy-MM-dd");
//        if (endDate == null) {
//            endDate = formDataExtractor.extractDateField(htmlDoc, "input[name='leaveEndDate']", "yyyy-MM-dd");
//        }
//        
//        // 날짜가 추출되지 않으면 현재 날짜 사용
//        if (startDate == null) {
//            startDate = LocalDateTime.now();
//            log.debug("휴가 시작일 추출 실패, 현재 날짜 사용");
//        }
//        
//        if (endDate == null) {
//            endDate = startDate;
//            log.debug("휴가 종료일 추출 실패, 시작일과 동일하게 설정");
//        }
//        
//        leaveDTO.setLevStartDate(startDate);
//        leaveDTO.setLevEndDate(endDate);
//        
//        // 휴가 일수 계산
//        int days = (int) ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate()) + 1;
//        leaveDTO.setLevDays(days);
//        leaveDTO.setLevLeftDays(15 - days); // 가정: 연간 휴가 15일
//        
//        // 휴가 사유
//        String reason = formDataExtractor.extractField(htmlDoc, "#leaveReason");
//        if (reason == null || reason.isEmpty()) {
//            reason = formDataExtractor.extractField(htmlDoc, "textarea[name='leaveReason']");
//            if (reason == null || reason.isEmpty()) {
//                reason = "휴가 신청";
//            }
//        }
//        leaveDTO.setLevReason(reason);
//        
//        // 신청일시 - 문서 기안일로 설정
//        leaveDTO.setLevReqDate(document.getDraftDate());
//        
//        log.debug("휴가 신청 정보 추출 완료: {}", leaveDTO);
//        return leaveDTO;
//    }
//
