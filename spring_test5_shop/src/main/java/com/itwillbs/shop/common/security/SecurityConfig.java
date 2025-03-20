package com.itwillbs.shop.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final UserDetailsService userService;
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// 스프링 시큐리티 기능 비활성화
		http
			.csrf(csrf -> csrf.disable()) // CSRF 비활성화
			.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
				.requestMatchers("/**").permitAll() // 모든 요청을 허용(통과시킴)
			)
			.formLogin(formLogin -> formLogin
				.disable() // 로그인 폼 비활성화
			);
		
		return http.build();
	}
	
	// 패스워드 인코딩에 사용할 단방향 암호화를 수행하는 패스워드 인코더 객체 생성 메서드 정의
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
}



















