package com.example.cmtProject.entity.mes.qualityControl;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import groovy.transform.builder.Builder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "FQC")
@EntityListeners(AuditingEntityListener.class)
public class Fqc {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FQC_NO")
    private Long fqcNo;

    @Column(name = "FQC_CODE", nullable = false)
    private String fqcCode;
    
    @Column(name = "ISSUE_CODE")
    private String issueCode;

    @Column(name = "EMP_ID", nullable = false)
    private String empId;
 
    @Column(name = "EMP_NAME")
    private String empName;

    @Column(name = "QCM_NO")
    private Long qcmNo;
    
    @Column(name = "QCM_NAME")
    private String qcmName;

    @Column(name = "PDT_CODE")
    private String pdtCode;

    @Column(name = "PDT_NAME")
    private String pdtName;

    @Column(name = "FQC_START_TIME")
    private LocalDateTime fqcStartTime;

    @Column(name = "FQC_END_TIME")
    private LocalDateTime fqcEndTime;
    
    @Column(name = "ISSUED_QTY")
    private String issuedQty;

    @Column(name = "UNIT_QTY")
    private String unitQty;

    @Column(name = "FQC_MEASURED_WEIGHT_VALUE")
    private Double fqcMeasuredWeightValue;
    
    @Column(name = "FQC_MEASURED_LENGTH_VALUE")
    private Double fqcMeasuredLengthValue;

    @Column(name = "QCM_UNIT_LENGTH")
    private String qcmUnitLength;

    @Column(name = "QCM_UNIT_WEIGHT")
    private String qcmUnitWeight;

    @Column(name = "FQC_INSPECTION_STATUS")
    private String fqcInspectionStatus; // 검사전 / 검사중 / 검사완료

    @Column(name = "FQC_INSPECTION_RESULT")
    private String fqcInspectionResult; // 합격 / 불합격 / 확인불가

    @Column(name = "WHS_CODE")
    private String whsCode;
    
    @Column(name = "WHS_NAME")
    private String whsName;

    @Column(name = "LOT_NO", nullable = false)
    private String lotNo;
    
    @Column(name = "FQC_REMARKS")
    private String fqcRemarks;
    
    @Column(name = "FQC_VISIABLE")
    private String fqcVisiable;
    
	
}
