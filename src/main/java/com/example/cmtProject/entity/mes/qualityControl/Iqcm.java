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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "IQCM")  // 테이블 ID = IQCM
public class Iqcm {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IQCM_NO")
    private Long iqcmNo;

    @Column(name = "IQCM_CODE", nullable = false)
    private String iqcmCode;

    @Column(name = "IQCM_NAME", nullable = false)
    private String iqcmName;

    @Column(name = "MTL_NO", nullable = false)
    private Long mtlNo;

    @Column(name = "IQCM_TARGET_VALUE", nullable = false)
    private Double iqcmTargetValue;

    @Column(name = "IQCM_MAX_VALUE", nullable = false)
    private Double iqcmMaxValue;

    @Column(name = "IQCM_MIN_VALUE", nullable = false)
    private Double iqcmMinValue;

    @Column(name = "IQCM_UNIT", nullable = false)
    private String iqcmUnit;

    @Column(name = "IQCM_METHOD")
    private String iqcmMethod;
}
