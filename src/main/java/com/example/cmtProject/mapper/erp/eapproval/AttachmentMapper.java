package com.example.cmtProject.mapper.erp.eapproval;

import com.example.cmtProject.dto.erp.eapproval.AttachmentDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AttachmentMapper {
    // 첨부파일 저장
    void insertAttachment(AttachmentDTO attachment);
    
    // 첨부파일 일괄 저장
    void insertAttachments(List<AttachmentDTO> attachments);
    
    // 파일 번호로 첨부파일 조회
    AttachmentDTO selectAttachmentByFileNo(Integer fileNo);
    
    // 문서ID로 첨부파일 목록 조회
    List<AttachmentDTO> selectAttachmentsByDocId(String docId);
    
    // 첨부파일 삭제
    void deleteAttachment(Integer fileNo);
    
    // 문서의 첨부파일 삭제 (문서 삭제 시)
    void deleteAttachmentsByDocId(String docId);
}