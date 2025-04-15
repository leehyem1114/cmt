package com.example.cmtProject.entity.mes;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "MFG_RESULTS")
@NoArgsConstructor
@AllArgsConstructor
public class mfgResults { // 생산 실적
	
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MFG_RESULTS_MR_NO")
    @SequenceGenerator(name = "SEQ_MFG_RESULTS_MR_NO", sequenceName = "SEQ_MFG_RESULTS_MR_NO")
    @Column(name = "MR_NO")
	private Long mrNo;  // 생산 실적 번호
	
	@Column(name = "MO_NO")
	private Long msNo;  // 제조 계획 번호

    @Column(name = "PDT_CODE")
	private String pdtCode;  // 제품 코드
    
    @Column(name = "PDT_PRC_TYPE_CODE")
	private String pdtPrcTypeCode;  // 공정 코드

    @Column(name = "EQP_CODE")
	private String eqpCode;  // 설비 코드
    
    @Column(name = "EMP_ID")
	private String empId;  // 작업자 사번
    
    @Column(name = "MR_QTY")
	private Long mrQty;  // 생산 수량

    @Column(name = "MR_NG_QTY")
	private Long mrNgQty;  // 불량 수량
    
    @Column(name = "MR_DURATION_TIME")
	private Long mrDurationTime;  // 소요시간
    
    @Column(name = "STATUS")
	private String status;  // 진행 상태
    
    @Column(name = "MR_CREATED_AT")
	private LocalDate mrCreatedAt;  // 등록일자

}
