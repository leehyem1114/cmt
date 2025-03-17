package com.example.cmtProject.config.auth;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.cmtProject.entity.Member;

//시큐리티 session을 사용하기 위한 클래스
//Security Session -> Authenctication -> UserDetails(PrincipalDetails)
public class PrincipalDetails implements UserDetails {

	private Member member;

	public PrincipalDetails(Member member){
       this.member = member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
       //return List.of(new SimpleGrantedAuthority("ROLE_" + member.getEmpLevel())); //db에 ROLE_가 없는 데이터를 가져와서 security로 넘기기전에 ROLE_를 붙인다
       return List.of(new SimpleGrantedAuthority(member.getEmpLevel()));
    }

    @Override
    public String getPassword() {
       return member.getEmpPassword();
    }

    @Override
    public String getUsername() {
       return member.getEmpName();
    }
}
