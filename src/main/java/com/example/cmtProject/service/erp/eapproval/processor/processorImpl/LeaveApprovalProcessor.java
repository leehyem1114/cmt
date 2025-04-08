
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
//
//import java.time.LocalDateTime;
//import java.time.temporal.ChronoUnit;
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
//    // 휴가 신청서 양식 ID - 실제 양식 ID로 변경 필요
//    private static final String LEAVE_FORM_ID = "FORM_LEAVE";
//
//    @Override
//    public boolean processApproved(DocumentDTO document) {
//        log.info("휴가 신청 결재 완료 처리 시작: 문서ID={}", document.getDocId());
//        
//        try {
//            // 이미 처리된 휴가 있는지 확인
//            LeaveDTO existingLeave = leaveService.getLeaveByDocId(document.getDocId());
//            if (existingLeave != null) {
//                log.info("이미 처리된 휴가 신청: {}", document.getDocId());
//                return leaveService.updateLeaveStatus(existingLeave.getLevNo(), "승인", "결재 완료");
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
//            if (currentUser == null) {
//                log.error("현재 사용자 정보를 가져올 수 없습니다");
//                return false;
//            }
//            
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
//    public boolean processRejected(DocumentDTO document) {
//        log.info("휴가 신청 결재 반려 처리 시작: 문서ID={}", document.getDocId());
//        
//        try {
//            // 이미 처리된 휴가 있는지 확인
//            LeaveDTO existingLeave = leaveService.getLeaveByDocId(document.getDocId());
//            if (existingLeave != null) {
//                log.info("이미 처리된 휴가 신청 반려 처리: {}", document.getDocId());
//                return leaveService.updateLeaveStatus(existingLeave.getLevNo(), "반려", "결재 반려");
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
//            if (currentUser == null) {
//                log.error("현재 사용자 정보를 가져올 수 없습니다");
//                return false;
//            }
//            
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
//        return LEAVE_FORM_ID.equals(formId);
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
//        leaveDTO.setEmpName(document.getDrafterName());
//        
//        // 휴가 유형 설정
//        String leaveType = formDataExtractor.extractField(htmlDoc, "#leaveType");
//        leaveDTO.setLevType(leaveType);
//        
//        // 휴가 일자 설정
//        LocalDateTime startDate = formDataExtractor.extractDateField(
//            htmlDoc, "#leaveStartDate", "yyyy-MM-dd");
//        LocalDateTime endDate = formDataExtractor.extractDateField(
//            htmlDoc, "#leaveEndDate", "yyyy-MM-dd");
//        
//        leaveDTO.setLevStartDate(startDate);
//        leaveDTO.setLevEndDate(endDate);
//        
//        // 휴가 일수 계산
//        if (startDate != null && endDate != null) {
//            int days = (int) ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate()) + 1;
//            leaveDTO.setLevDays(days);
//            
//            // 잔여 휴가 일수는 현재 데이터베이스에서 조회해야 할 수 있음
//            // 임시로 설정
//            leaveDTO.setLevLeftDays(15 - days); // 가정: 연간 휴가 15일
//        }
//        
//        // 휴가 사유
//        String reason = formDataExtractor.extractField(htmlDoc, "#leaveReason");
//        leaveDTO.setLevReason(reason);
//        
//        // 신청일시 - 문서 기안일로 설정
//        leaveDTO.setLevReqDate(document.getDraftDate());
//        
//        log.debug("휴가 신청 정보 추출 완료: {}", leaveDTO);
//        return leaveDTO;
//    }
//}
