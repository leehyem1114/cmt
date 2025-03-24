package com.example.cmtProject.dto.erp.eapproval;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipientDTO {
	/** 수신자 번호 */
    private Integer recipientNo;
    
    /** 문서 ID */
    private String docId;
    
    /** 수신자 사원 번호 */
    private Integer receiverNo;
    
    /** 수신자 이름 (조회용) */
    private String receiverName;
    
    /** 수신 상태 (미열람, 열람) */
    private String readStatus;
    
    /** 읽은 일시 */
    private LocalDateTime readDate;
    
    /** 행 타입 (조회:select, 등록:insert, 수정:update, 삭제:delete) */
    private String rowType;

}
