package com.example.cmtProject.entity.mes.qualityControl;

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
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "FQCM")
@EntityListeners(AuditingEntityListener.class)
public class Fqcm {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FQCM_NO")
    private Long fqcmNo;

    @Column(name = "FQCM_CODE", nullable = false)
    private String fqcmCode;

    @Column(name = "FQCM_NAME", nullable = false)
    private String fqcmName;

    @Column(name = "PDT_NO", nullable = false)
    private Long pdtNo;

    @Column(name = "FQCM_TARGET_VALUE", nullable = false)
    private Double fqcmTargetValue;

    @Column(name = "FQCM_MAX_VALUE", nullable = false)
    private Double fqcmMaxValue;

    @Column(name = "FQCM_MIN_VALUE", nullable = false)
    private Double fqcmMinValue;

    @Column(name = "FQCM_UNIT", nullable = false)
    private String fqcmUnit;

    @Column(name = "FQCM_METHOD")
    private String fqcmMethod;
    
}
