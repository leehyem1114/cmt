package com.example.cmtProject.mapper.erp.eapproval;

import com.example.cmtProject.dto.erp.eapproval.ApprovalLineDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ApprovalLineMapper {
    // 결재선 저장
    void insertApprovalLine(ApprovalLineDTO approvalLine);
    
    // 결재선 수정
    void updateApprovalLine(ApprovalLineDTO approvalLine);
    
    // 문서ID로 결재선 조회
    List<ApprovalLineDTO> selectApprovalLinesByDocId(String docId);
    
    // 특정 결재자의 특정 문서 결재선 조회
    ApprovalLineDTO selectApprovalLineByDocIdAndApproverId(
            @Param("docId") String docId, 
            @Param("approverNo") Integer approverNo);
    
    // 문서의 다음 결재자 조회
    List<ApprovalLineDTO> selectNextApproversByDocId(String docId);
    
    // 문서의 결재선 삭제 (문서 삭제 시)
    void deleteApprovalLinesByDocId(String docId);
    
    // 결재선 일괄 저장 (배치)
    void insertApprovalLines(List<ApprovalLineDTO> approvalLines);
    
} //ApprovalLineMapper