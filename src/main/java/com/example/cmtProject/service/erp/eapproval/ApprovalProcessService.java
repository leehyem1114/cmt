package com.example.cmtProject.service.erp.eapproval;

import com.example.cmtProject.constants.ApprovalStatus;
import com.example.cmtProject.constants.DocumentStatus;
import com.example.cmtProject.dto.erp.eapproval.ApprovalLineDTO;
import com.example.cmtProject.mapper.erp.eapproval.ApprovalLineMapper;
import com.example.cmtProject.mapper.erp.eapproval.DocumentMapper;
import com.example.cmtProject.comm.exception.ApprovalProcessException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 결재 프로세스 처리 서비스
 * 결재 승인/반려 등 결재 프로세스와 관련된 비즈니스 로직을 담당
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ApprovalProcessService {

    private final DocumentMapper documentMapper;
    private final ApprovalLineMapper approvalLineMapper;
    
    /**
     * 결재 승인 처리
     */
    @Transactional
    public void approve(String docId, Integer approverId, String comment) {
        log.debug("결재 승인 처리: 문서={}, 결재자={}", docId, approverId);
        
        // 결재자의 결재라인 조회
        ApprovalLineDTO approvalLine = approvalLineMapper.selectApprovalLineByDocIdAndApproverId(docId, approverId);
        if (approvalLine == null) {
            throw new ApprovalProcessException("결재 권한이 없습니다");
        }
        
        // 이미 처리된 결재인지 확인
        if (!ApprovalStatus.PENDING.equals(approvalLine.getApprovalStatus())) {
            throw new ApprovalProcessException("이미 처리된 결재입니다");
        }
        
        // 결재 처리
        approvalLine.setApprovalStatus(ApprovalStatus.APPROVED);
        approvalLine.setApprovalComment(comment);
        approvalLine.setApprovalDate(LocalDateTime.now());
        approvalLineMapper.updateApprovalLine(approvalLine);
        
        // 다음 결재자 확인
        processNextApprover(docId);
    }
    
    /**
     * 결재 반려 처리
     */
    @Transactional
    public void reject(String docId, Integer approverId, String comment) {
        log.debug("결재 반려 처리: 문서={}, 결재자={}", docId, approverId);
        
        // 결재자의 결재라인 조회
        ApprovalLineDTO approvalLine = approvalLineMapper.selectApprovalLineByDocIdAndApproverId(docId, approverId);
        if (approvalLine == null) {
            throw new ApprovalProcessException("결재 권한이 없습니다");
        }
        
        // 이미 처리된 결재인지 확인
        if (!ApprovalStatus.PENDING.equals(approvalLine.getApprovalStatus())) {
            throw new ApprovalProcessException("이미 처리된 결재입니다");
        }
        
        // 필수 입력값 확인
        if (comment == null || comment.trim().isEmpty()) {
            throw new ApprovalProcessException("반려 시 의견을 입력해야 합니다");
        }
        
        // 결재 처리
        approvalLine.setApprovalStatus(ApprovalStatus.REJECTED);
        approvalLine.setApprovalComment(comment);
        approvalLine.setApprovalDate(LocalDateTime.now());
        approvalLineMapper.updateApprovalLine(approvalLine);
        
        // 문서 상태 변경
        documentMapper.updateDocumentStatus(docId, DocumentStatus.REJECTED);
        log.info("문서 반려 처리 완료: {}", docId);
    }
    
    /**
     * 다음 결재자 처리
     */
    private void processNextApprover(String docId) {
        // 다음 결재자 확인
        List<ApprovalLineDTO> nextApprovers = approvalLineMapper.selectNextApproversByDocId(docId);
        
        // 다음 결재자가 없으면 결재 완료 처리
        if (nextApprovers.isEmpty()) {
            documentMapper.updateDocumentStatus(docId, DocumentStatus.COMPLETED);
            documentMapper.updateApprovalDate(docId, LocalDateTime.now());
            log.info("문서 최종 승인 완료: {}", docId);
        } else {
            log.debug("다음 결재자 대기 중: {}명", nextApprovers.size());
        }
    }
    
    /**
     * 결재 대기 여부 확인
     * 특정 결재자가 해당 문서의 현재 결재자인지 확인
     */
    public boolean isCurrentApprover(String docId, Integer approverId) {
        ApprovalLineDTO approvalLine = approvalLineMapper.selectApprovalLineByDocIdAndApproverId(docId, approverId);
        return approvalLine != null && ApprovalStatus.PENDING.equals(approvalLine.getApprovalStatus());
    }
    
    /**
     * 결재선 생성
     * 문서에 결재선을 추가하는 로직
     */
    @Transactional
    public void createApprovalLines(String docId, List<ApprovalLineDTO> approvalLines) {
        if (approvalLines == null || approvalLines.isEmpty()) {
            log.warn("결재선이 비어있습니다: {}", docId);
            return;
        }
        
        log.debug("결재선 생성: 문서={}, 결재선 수={}", docId, approvalLines.size());
        
        // 이전 결재선 삭제
        approvalLineMapper.deleteApprovalLinesByDocId(docId);
        
        // 새 결재선 생성
        for (ApprovalLineDTO line : approvalLines) {
            line.setDocId(docId);
            line.setApprovalStatus(ApprovalStatus.PENDING);
            approvalLineMapper.insertApprovalLine(line);
        }
    }
}