package com.example.cmtProject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration //자바가 인식하는 설정 클래스로 지정
@EnableWebSecurity //스프링 시큐리티 필터가 스프링 필터체인에 등록이 됨
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true) 
public class SecurityConfig{

	//password 암호화 하기 위한 Bean객체
    @Bean
    public BCryptPasswordEncoder encoderPwd(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests((authorizeRequests) -> authorizeRequests
            .requestMatchers("/user/**").authenticated() //user는 로그인하면 접근 가능
            .requestMatchers("/manager/**").hasAnyRole("ADMIN", "MANAGER") //ADMIN또는 MANAGER가 접근
            .requestMatchers("/admin/**").hasRole("ADMIN") //ADMIN만 접근
            .anyRequest().permitAll() //그 외 모든 요청은 허용
            )
            .formLogin(login -> login
                    .loginPage("/loginForm") // 로그인 페이지 설정
                    .usernameParameter("empName") // username 필드 이름 변경
                    .passwordParameter("empPassword") // password 필드 이름 변경
                    .loginProcessingUrl("/login") //login주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 진행해준다. 그렇기 때문에 cotroller에 login페이지가 없다.
                    .defaultSuccessUrl("/loginSuccess") //로그인이 성공하면 main페이지로 간다
                    .failureUrl("/loginFail")
                    .permitAll() // 로그인 페이지는 누구나 접근 가능
            )
            .logout(logout -> logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
                    .invalidateHttpSession(true)
                    //.deleteCookies("JSESSIONID") //세션 삭제
                );

        //.anyRequest().authenticated()); 모든 요청에 대해 인증(로그인) 필요

        /*
        formLogin 으로 가는 경우
        ✔ 로그인하지 않은 사용자가 인증이 필요한 페이지에 접근하면 자동으로 로그인 페이지로 이동
        ✔ 권한(ROLE)이 부족한 사용자는 기본적으로 403 Forbidden (로그인 페이지 이동 X)
        ✔ 권한이 부족한 경우 로그인 페이지로 리디렉트하려면 accessDeniedHandler() 설정 필요
        */
        return http.build();
    }
}
