package com.example.cmtProject.service.erp.eapproval;

import com.example.cmtProject.dto.erp.eapproval.DocumentDTO;
import com.example.cmtProject.dto.erp.eapproval.ApprovalLineDTO;
import com.example.cmtProject.mapper.erp.eapproval.DocumentMapper;
import com.example.cmtProject.mapper.erp.eapproval.ApprovalLineMapper;
import com.example.cmtProject.mapper.erp.eapproval.AttachmentMapper;
import com.example.cmtProject.mapper.erp.eapproval.RecipientMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentMapper documentMapper;
    private final ApprovalLineMapper approvalLineMapper;
    private final AttachmentMapper attachmentMapper;
    private final RecipientMapper recipientMapper;

    /**
     * 문서 저장 (임시저장 또는 결재요청)
     */
    @Transactional
    public DocumentDTO saveDocument(DocumentDTO documentDTO, boolean isTempSave) {
        // 문서 ID 생성
        if (documentDTO.getDocId() == null || documentDTO.getDocId().isEmpty()) {
            documentDTO.setDocId(UUID.randomUUID().toString());
        }
        
        // 임시저장 여부 설정
        documentDTO.setIsTempSaved(isTempSave ? "Y" : "N");
        
        // 문서 상태 설정
        documentDTO.setDocStatus(isTempSave ? "임시저장" : "진행중");
        
        // 문서 번호 생성 (신규 문서인 경우)
        if (documentDTO.getDocNumber() == null || documentDTO.getDocNumber().isEmpty()) {
            int seq = documentMapper.selectDocumentSequence();
            String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            documentDTO.setDocNumber(dateStr + "-" + documentDTO.getDraftDept() + "-" + seq);
        }
        
        // 문서 저장
        if (documentMapper.selectDocumentById(documentDTO.getDocId()) == null) {
            documentMapper.insertDocument(documentDTO);
        } else {
            // 기존 결재선 삭제 (재설정할 경우)
            if (!isTempSave) {
                approvalLineMapper.deleteApprovalLinesByDocId(documentDTO.getDocId());
            }
            documentMapper.updateDocument(documentDTO);
        }
        
        // 임시저장이 아닌 경우, 결재선 저장
        if (!isTempSave && documentDTO.getApprovalLines() != null && !documentDTO.getApprovalLines().isEmpty()) {
            for (ApprovalLineDTO lineDTO : documentDTO.getApprovalLines()) {
                lineDTO.setDocId(documentDTO.getDocId());
                lineDTO.setApprovalStatus("대기");
                approvalLineMapper.insertApprovalLine(lineDTO);
            }
        }
        
        return documentMapper.selectDocumentById(documentDTO.getDocId());
    }

    /**
     * 결재 처리 (승인/반려)
     */
    @Transactional
    public void processApproval(String docId, Integer approverId, String decision, String comment) {
        // 문서 조회
        DocumentDTO document = documentMapper.selectDocumentById(docId);
        if (document == null) {
            throw new RuntimeException("문서를 찾을 수 없습니다: " + docId);
        }
        
        // 결재자의 결재라인 조회
        ApprovalLineDTO approvalLine = approvalLineMapper.selectApprovalLineByDocIdAndApproverId(docId, approverId);
        if (approvalLine == null) {
            throw new RuntimeException("결재 권한이 없습니다.");
        }
        
        // 결재 처리
        approvalLine.setApprovalStatus(decision);
        approvalLine.setApprovalComment(comment);
        approvalLine.setApprovalDate(LocalDateTime.now());
        approvalLineMapper.updateApprovalLine(approvalLine);
        
        // 반려 처리
        if ("반려".equals(decision)) {
            documentMapper.updateDocumentStatus(docId, "반려");
            return;
        }
        
        // 다음 결재자 확인
        List<ApprovalLineDTO> nextApprovers = approvalLineMapper.selectNextApproversByDocId(docId);
        
        // 다음 결재자가 없으면 결재 완료 처리
        if (nextApprovers.isEmpty()) {
            documentMapper.updateDocumentStatus(docId, "완료");
            documentMapper.updateApprovalDate(docId, LocalDateTime.now());
        }
    }

    /**
     * 문서 상세 조회
     */
    public DocumentDTO getDocumentDetail(String docId) {
        DocumentDTO document = documentMapper.selectDocumentById(docId);
        if (document != null) {
            List<ApprovalLineDTO> approvalLines = approvalLineMapper.selectApprovalLinesByDocId(docId);
            document.setApprovalLines(approvalLines);
        }
        return document;
    }

    /**
     * 기안자 문서 목록 조회
     */
    public List<DocumentDTO> getDrafterDocuments(Integer drafterId) {
        return documentMapper.selectDocumentsByDrafterId(drafterId);
    }
    
    /**
     * 기안자 및 상태별 문서 목록 조회
     */
    public List<DocumentDTO> getDocumentsByDrafterAndStatus(Integer drafterId, String status) {
        return documentMapper.selectDocumentsByDrafterAndStatus(drafterId, status);
    }

    /**
     * 결재 대기 문서 목록 조회
     */
    public List<DocumentDTO> getPendingDocuments(Integer approverId) {
        return documentMapper.selectPendingDocumentsByApproverId(approverId);
    }

    /**
     * 상태별 문서 목록 조회
     */
    public List<DocumentDTO> getDocumentsByStatus(String status) {
        return documentMapper.selectDocumentsByStatus(status);
    }
    
    /**
     * 문서 삭제 (임시저장 문서만 삭제 가능)
     * @return 삭제 성공 여부
     */
    @Transactional
    public boolean deleteDocument(String docId) {
        DocumentDTO document = documentMapper.selectDocumentById(docId);
        
        if (document == null || !"Y".equals(document.getIsTempSaved())) {
            return false;
        }
        
        // 관련 데이터 삭제
        approvalLineMapper.deleteApprovalLinesByDocId(docId);
        recipientMapper.deleteRecipientsByDocId(docId);
        attachmentMapper.deleteAttachmentsByDocId(docId);
        
        // 문서 삭제
        int result = documentMapper.deleteDocument(docId);
        return result > 0;
    }
    
}