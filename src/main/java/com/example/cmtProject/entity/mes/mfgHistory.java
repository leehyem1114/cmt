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
@Table(name = "MFG_HISTORIES")
@NoArgsConstructor
@AllArgsConstructor
public class mfgHistory { // 생산 이력

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MFG_HISTORIES_MH_NO")
    @SequenceGenerator(name = "SEQ_MFG_HISTORIES_MH_NO", sequenceName = "SEQ_MFG_HISTORIES_MH_NO")
    @Column(name = "MH_NO")
	private Long mhNo;  // 생산 이력 번호
	
    @Column(name = "MP_NO")
	private Long mpNo;  // 생산 계획 번호

    @Column(name = "PDT_CODE")
	private String pdtCode;  // 제품 코드
    
    @Column(name = "MP_STATUS")
	private String mpStatus;  // 생산 상태
    
    @Column(name = "WORK_START_TIME")
	private LocalDate workStartTime;  // 작업 시작일

    @Column(name = "WORK_END_TIME")
	private LocalDate workEndTime;  // 작업 종료일
	
}
