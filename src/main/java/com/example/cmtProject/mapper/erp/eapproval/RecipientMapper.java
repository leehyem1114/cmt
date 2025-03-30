package com.example.cmtProject.mapper.erp.eapproval;

import com.example.cmtProject.dto.erp.eapproval.RecipientDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecipientMapper {
    // 수신자 저장
    void insertRecipient(RecipientDTO recipient);
    
    // 수신자 일괄 저장
    void insertRecipients(List<RecipientDTO> recipients);
    
    // 수신 상태 업데이트
    void updateReadStatus(
            @Param("recipientNo") Integer recipientNo,
            @Param("readStatus") String readStatus);
    
    // 문서ID로 수신자 목록 조회
    List<RecipientDTO> selectRecipientsByDocId(String docId);
    
    // 수신자별 수신 문서 목록 조회
    List<RecipientDTO> selectDocumentsByReceiverId(Integer receiverId);
    
    // 문서의 수신자 삭제 (문서 삭제 시)
    void deleteRecipientsByDocId(String docId);
}