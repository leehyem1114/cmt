package com.example.cmtProject.mapper.erp.eapproval;

import com.example.cmtProject.dto.erp.eapproval.DocumentDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper

public interface DocumentMapper {
    // 문서 저장
    void insertDocument(DocumentDTO document);
    
    // 문서 정보 수정
    void updateDocument(DocumentDTO document);
    
    // 문서 상태 변경
    void updateDocumentStatus(@Param("docId") String docId, 
                              @Param("docStatus") String docStatus);
    
    // 문서 결재일자 업데이트
    void updateApprovalDate(@Param("docId") String docId, 
                           @Param("approvalDate") LocalDateTime approvalDate);
    
    // 문서 조회
    DocumentDTO selectDocumentById(String docId);
    
    // 기안자별 문서 목록 조회
    List<DocumentDTO> selectDocumentsByDrafterId(Integer drafterId);
    
    // 기안자 및 상태별 문서 목록 조회
    List<DocumentDTO> selectDocumentsByDrafterAndStatus(
            @Param("drafterId") Integer drafterId, 
            @Param("status") String status);
    
    // 상태별 문서 목록 조회
    List<DocumentDTO> selectDocumentsByStatus(String docStatus);
    
    // 결재자별 대기 문서 목록 조회
    List<DocumentDTO> selectPendingDocumentsByApproverId(Integer approverId);
    
 // 문서 번호 자동 생성을 위한 시퀀스 조회
    int selectDocumentSequence();
    
    // 문서 삭제 (임시저장 문서만)
    int deleteDocument(String docId);
    
}