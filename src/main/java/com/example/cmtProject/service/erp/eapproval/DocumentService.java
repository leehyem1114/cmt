//package com.example.cmtProject.service.erp.eapproval;
//
//import com.example.cmtProject.dto.erp.eapproval.DocumentDTO;
//import com.example.cmtProject.dto.erp.eapproval.ApprovalLineDTO;
//import com.example.cmtProject.mapper.erp.eapproval.DocumentMapper;
//import com.example.cmtProject.mapper.erp.eapproval.ApprovalLineMapper;
//import com.example.cmtProject.service.erp.employees.EmployeesService;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class DocumentService {
//
//    private final DocumentMapper documentMapper;
//    private final ApprovalLineMapper approvalLineMapper;
//    private final EmployeesService employeesService;
//
//    /**
//     * 문서 저장 (임시저장 또는 결재요청)
//     */
//    @Transactional
//    public DocumentDTO saveDocument(DocumentDTO documentDTO, boolean isTempSave) {
//        log.info("문서 저장 시작: 임시저장={}, 제목={}", isTempSave, documentDTO.getTitle());
//        
//        // 문서 ID 생성
//        if (documentDTO.getDocId() == null || documentDTO.getDocId().isEmpty()) {
//            documentDTO.setDocId(UUID.randomUUID().toString());
//            log.debug("신규 문서 ID 생성: {}", documentDTO.getDocId());
//        }
//        
//        // 임시저장 여부 설정
//        documentDTO.setIsTempSaved(isTempSave ? "Y" : "N");
//        
//        // 문서 상태 설정
//        documentDTO.setDocStatus(isTempSave ? "임시저장" : "진행중");
//        
//        // 문서 번호 생성 (신규 문서인 경우)
//        if (documentDTO.getDocNumber() == null || documentDTO.getDocNumber().isEmpty()) {
//            int seq = documentMapper.selectDocumentSequence();
//            String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
//            documentDTO.setDocNumber(dateStr + "-" + documentDTO.getDraftDept() + "-" + seq);
//            log.debug("문서 번호 생성: {}", documentDTO.getDocNumber());
//        }
//        
//        // 문서 저장
//        if (documentMapper.selectDocumentById(documentDTO.getDocId()) == null) {
//            log.debug("신규 문서 삽입");
//            documentMapper.insertDocument(documentDTO);
//        } else {
//            log.debug("기존 문서 수정");
//            // 기존 결재선 삭제 (재설정할 경우)
//            if (!isTempSave) {
//                approvalLineMapper.deleteApprovalLinesByDocId(documentDTO.getDocId());
//            }
//            documentMapper.updateDocument(documentDTO);
//        }
//        
//        // 임시저장이 아닌 경우, 결재선 저장
//        if (!isTempSave && documentDTO.getApprovalLines() != null && !documentDTO.getApprovalLines().isEmpty()) {
//            log.debug("결재선 저장: {}개", documentDTO.getApprovalLines().size());
//            for (ApprovalLineDTO lineDTO : documentDTO.getApprovalLines()) {
//                lineDTO.setDocId(documentDTO.getDocId());
//                lineDTO.setApprovalStatus("대기");
//                approvalLineMapper.insertApprovalLine(lineDTO);
//            }
//        }
//        
//        log.info("문서 저장 완료: {}", documentDTO.getDocId());
//        return documentMapper.selectDocumentById(documentDTO.getDocId());
//    }
//
//    /**
//     * 결재 처리 (승인/반려)
//     */
//    @Transactional
//    public void processApproval(String docId, Integer approverId, String decision, String comment) {
//        log.info("결재 처리: 문서={}, 결재자={}, 결정={}", docId, approverId, decision);
//        
//        // 문서 조회
//        DocumentDTO document = documentMapper.selectDocumentById(docId);
//        if (document == null) {
//            log.error("결재 처리 실패: 문서를 찾을 수 없음 {}", docId);
//            throw new RuntimeException("문서를 찾을 수 없습니다: " + docId);
//        }
//        
//        // 결재자의 결재라인 조회
//        ApprovalLineDTO approvalLine = approvalLineMapper.selectApprovalLineByDocIdAndApproverId(docId, approverId);
//        if (approvalLine == null) {
//            log.error("결재 처리 실패: 결재권한 없음, 문서={}, 결재자={}", docId, approverId);
//            throw new RuntimeException("결재 권한이 없습니다.");
//        }
//        
//        // 결재 처리
//        approvalLine.setApprovalStatus(decision);
//        approvalLine.setApprovalComment(comment);
//        approvalLine.setApprovalDate(LocalDateTime.now());
//        approvalLineMapper.updateApprovalLine(approvalLine);
//        log.debug("결재 상태 업데이트: {}", decision);
//        
//        // 반려 처리
//        if ("반려".equals(decision)) {
//            documentMapper.updateDocumentStatus(docId, "반려");
//            log.info("문서 반려 처리 완료: {}", docId);
//            return;
//        }
//        
//        // 다음 결재자 확인
//        List<ApprovalLineDTO> nextApprovers = approvalLineMapper.selectNextApproversByDocId(docId);
//        
//        // 다음 결재자가 없으면 결재 완료 처리
//        if (nextApprovers.isEmpty()) {
//            documentMapper.updateDocumentStatus(docId, "완료");
//            documentMapper.updateApprovalDate(docId, LocalDateTime.now());
//            log.info("문서 최종 승인 완료: {}", docId);
//        } else {
//            log.debug("다음 결재자 대기 중: {}명", nextApprovers.size());
//        }
//    }
//
//    /**
//     * 문서 상세 조회
//     */
//    public DocumentDTO getDocumentDetail(String docId) {
//        log.debug("문서 상세 조회: {}", docId);
//        
//        DocumentDTO document = documentMapper.selectDocumentById(docId);
//        if (document != null) {
//            List<ApprovalLineDTO> approvalLines = approvalLineMapper.selectApprovalLinesByDocId(docId);
//            document.setApprovalLines(approvalLines);
//            log.debug("결재선 조회: {}개", approvalLines.size());
//        }
//        
//        return document;
//    }
//
//    /**
//     * 기안자 ID로 문서 목록 조회
//     */
//    public List<DocumentDTO> getDrafterDocumentsByEmpId(String empId) {
//        log.debug("기안자별 문서 목록 조회: {}", empId);
//        
//        // empId를 사용하여 사원 번호(empNo) 조회
//        Integer drafterId = getEmployeeNoByEmpId(empId);
//        
//        // 사원 번호로 문서 목록 조회
//        return documentMapper.selectDocumentsByDrafterId(drafterId);
//    }
//    
//    /**
//     * 기안자 및 상태별 문서 목록 조회
//     */
//    public List<DocumentDTO> getDocumentsByDrafterAndStatus(Integer drafterId, String status) {
//        log.debug("기안자 및 상태별 문서 조회: 기안자={}, 상태={}", drafterId, status);
//        return documentMapper.selectDocumentsByDrafterAndStatus(drafterId, status);
//    }
//
//    /**
//     * 직원 ID로 결재 대기 문서 목록 조회
//     */
//    public List<DocumentDTO> getPendingDocumentsByEmpId(String empId) {
//        log.debug("결재 대기 문서 목록 조회: {}", empId);
//        
//        // empId를 사용하여 사원 번호(empNo) 조회
//        Integer approverId = getEmployeeNoByEmpId(empId);
//        
//        // 사원 번호로 대기 문서 목록 조회
//        return documentMapper.selectPendingDocumentsByApproverId(approverId);
//    }
//
//    /**
//     * 상태별 문서 목록 조회
//     */
//    public List<DocumentDTO> getDocumentsByStatus(String status) {
//        log.debug("상태별 문서 목록 조회: {}", status);
//        return documentMapper.selectDocumentsByStatus(status);
//    }
//    
//    /**
//     * 문서 삭제 (임시저장 문서만 삭제 가능)
//     * @return 삭제 성공 여부
//     */
//    @Transactional
//    public boolean deleteDocument(String docId) {
//        log.info("문서 삭제 요청: {}", docId);
//        
//        DocumentDTO document = documentMapper.selectDocumentById(docId);
//        
//        if (document == null) {
//            log.warn("삭제할 문서가 존재하지 않음: {}", docId);
//            return false;
//        }
//        
//        if (!"Y".equals(document.getIsTempSaved())) {
//            log.warn("임시저장 문서가 아니므로 삭제 불가: {}", docId);
//            return false;
//        }
//        
//        // 관련 데이터 삭제
//        approvalLineMapper.deleteApprovalLinesByDocId(docId);
//        log.debug("결재선 삭제 완료: {}", docId);
//        
//        // 문서 삭제
//        int result = documentMapper.deleteDocument(docId);
//        log.info("문서 삭제 완료: {}", docId);
//        
//        return result > 0;
//    }
//    
//    /**
//     * 직원 ID로 직원 번호 조회 (Helper 메서드)
//     */
//    private Integer getEmployeeNoByEmpId(String empId) {
//        try {
//            // EmployeesService를 통해 사원 번호를 조회하는 로직 인데 아직 거기다 구현 안함
//            return employeesService.getEmployeeNoByEmpId(empId);
//        } catch (Exception e) {
//            log.error("직원 번호 조회 중 오류: {}", empId, e);
//            throw new RuntimeException("직원 정보를 조회할 수 없습니다.", e);
//        }
//    }
//    
//    
//}