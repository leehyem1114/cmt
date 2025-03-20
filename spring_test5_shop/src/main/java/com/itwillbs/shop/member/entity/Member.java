package com.itwillbs.shop.member.entity;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.itwillbs.shop.common.constant.MemberRole;
import com.itwillbs.shop.member.dto.MemberDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "member")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Member {
	@Id
	@Column(name = "member_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String name;
	
	@Column(unique = true)
	private String email;
	
	private String password;
	private String address;
	
	@Enumerated(EnumType.STRING)
	private MemberRole role;
	
	public static Member createMember(MemberDTO memberDTO, PasswordEncoder passwordEncoder) {
		Member member = new Member();
		member.setName(memberDTO.getName());
		member.setEmail(memberDTO.getEmail());
		// 패스워드는 PasswordEncoder 객체 활용하여 단방향 암호화 수행 후 저장
		member.setPassword(passwordEncoder.encode(memberDTO.getPassword()));
		member.setAddress(memberDTO.getAddress());
		member.setRole(MemberRole.USER); // 회원 기본은 USER
		
		return member;
	}
}























