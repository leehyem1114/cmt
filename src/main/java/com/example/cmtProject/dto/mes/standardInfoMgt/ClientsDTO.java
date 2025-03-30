package com.example.cmtProject.dto.mes.standardInfoMgt;

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
@NoArgsConstructor
@AllArgsConstructor
public class ClientsDTO {

	private Long cltNo;  //거래처no
	private String cltCode; //거래처코드
	private String cltName; //거래처명
	private String cltPhone; //전화번호
	private String cltEmail; //email
	private String cltAddress; //주소
	private String cltComments; //비고
}
