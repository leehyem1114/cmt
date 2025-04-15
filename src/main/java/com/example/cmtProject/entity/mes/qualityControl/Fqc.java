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

    @Column(name = "EMP_ID", nullable = false)
    private String empId;

    @Column(name = "QCM_NO", nullable = false)
    private Long qcmNo;

    @Column(name = "PDT_NO", nullable = false)
    private Long pdtNo;

    @Column(name = "FQC_TIME")
    private LocalDateTime fqcTime;

    @Column(name = "FQC_MEASURED_VALUE")
    private Double fqcMeasuredValue;

    @Column(name = "FQC_INSPECTION_STATUS")
    private String fqcInspectionStatus;

    @Column(name = "FQC_INSPECTION_RESULT", length = 1)
    private String fqcInspectionResult; // 'P' / 'F'

    @Column(name = "WHS_NO")
    private Long whsNo;

    @Column(name = "LOT_NO", nullable = false)
    private String lotNo;
	
}
