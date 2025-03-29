package com.example.cmtProject.entity.mes.standardInfoMgt;

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
@Table(name = "WAREHOUSES")
@NoArgsConstructor
@AllArgsConstructor
public class Warehouses {
	
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_WAREHOUSES_WHS_NO")
    @SequenceGenerator(name = "SEQ_WAREHOUSES_WHS_NO", sequenceName = "SEQ_WAREHOUSES_WHS_NO")
    @Column(name = "WHS_NO")
	private Long whsNo;  //창고no(pk)
	
	@Column(name = "WHS_CODE")
	private String whsCode; //창고코드
	
	@Column(name = "WHS_NAME")
	private String whsName; //창고명
	
	@Column(name = "WHS_LOCATION")
	private String whsLocation; //창고위치
	
	@Column(name = "WHS_USED")
	private String whsUsed; //창고사용
	
	@Column(name = "WHS_COMMENTS")
	private String whsComments; //비고   
}


