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
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "IQC")
@EntityListeners(AuditingEntityListener.class)
public class Iqc {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IQC_NO")
    private Long iqcNo;

    @Column(name = "IQC_CODE", nullable = false)
    private String iqcCode;

    @Column(name = "EMP_ID", nullable = false)
    private String empId;

    @Column(name = "QCM_NO", nullable = false)
    private Long qcmNo;

    @Column(name = "MTL_NAME", nullable = false)
    private String mtlName;

    @Column(name = "IQC_TIME")
    private LocalDateTime iqcTime;

    @Column(name = "IQC_MEASURED_VALUE")
    private Double iqcMeasuredValue;
    
    @Column(name = "QCM_UNIT_LENGTH")
    private String qcmUnitLength;

    @Column(name = "QCM_UNIT_WEIGHT")
    private String qcmUnitWeight;

    @Column(name = "IQC_INSPECTION_STATUS")
    private String iqcInspectionStatus; // 검사전 / 검사중 / 검사완료

    @Column(name = "IQC_INSPECTION_RESULT", length = 1)
    private String iqcInspectionResult; // 'P' / 'F'

    @Column(name = "WHS_NAME")
    private String whsName;

    @Column(name = "LOT_NO", nullable = false)
    private String lotNo;

}
