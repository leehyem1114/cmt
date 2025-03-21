//package com.example.cmtProject.entity.erp.attendanceMgt;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//
//import groovy.transform.builder.Builder;
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.EnumType;
//import jakarta.persistence.Enumerated;
//import jakarta.persistence.FetchType;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.Table;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Entity
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//@Table(name = "work_times")
//public class WorkTime {
//	
// 	@Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long id; // 근무시간NO (WKT_NO)
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "emp_no", nullable = false)
//    private Employee employee; // 사원번호 (EMP_NO), 사원 테이블 참조
//
//    @Column(name = "wkt_date", nullable = false)
//    private LocalDate workDate; // 근무일자 (WKT_DATE)
//
//    @Column(name = "wkt_start_time")
//    private LocalTime startTime; // 출근시간 (WKT_START_TIME)
//
//    @Column(name = "wkt_end_time")
//    private LocalTime endTime; // 퇴근시간 (WKT_END_TIME)
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "wkt_status", nullable = false)
//    private WorkStatus workStatus; // 근무상태 (WKT_STATUS)
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "wkt_type", nullable = false)
//    private WorkType workType; // 기준근무유형 (WKT_TYPE)
//
//    @Column(name = "wkt_remarks", length = 200)
//    private String remarks; // 비고 (WKT_REMARKS)
//
//}
