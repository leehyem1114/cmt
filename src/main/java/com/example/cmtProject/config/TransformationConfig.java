package com.example.cmtProject.config;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.cmtProject.comm.response.ModelTransformer;
import com.example.cmtProject.comm.response.TransformationHelper;

import lombok.extern.slf4j.Slf4j;

/**
 * 변환 시스템을 위한 URL 패턴 기반 설정
 * URL 패턴에 따라 데이터 변환 처리를 관리함
 */
@Configuration
@Slf4j
public class TransformationConfig implements WebMvcConfigurer {
    
    private final ModelTransformer modelTransformer;
    private final TransformationHelper transformHelper;
    
    // 변환 시스템 전체 활성화 여부
    private final boolean transformEnabled = true;
    
    // 요청 변환 활성화 여부
    private final boolean requestTransformEnabled = true;
    
    // 응답 변환 활성화 여부
    private final boolean responseTransformEnabled = true;
    
    // 요청 변환 패턴 목록 - 이 패턴과 일치하는 URL은 변환이 적용됨
    private final List<Pattern> requestIncludePatterns = new ArrayList<>();
    
    // 요청 변환 제외 패턴 목록 - 이 패턴과 일치하는 URL은 변환에서 제외
    private final List<Pattern> requestExcludePatterns = new ArrayList<>();
    
    // 응답 변환 패턴 목록 - 이 패턴과 일치하는 URL은 변환이 적용됨
    private final List<Pattern> responseIncludePatterns = new ArrayList<>();
    
    // 응답 변환 제외 패턴 목록 - 이 패턴과 일치하는 URL은 변환에서 제외
    private final List<Pattern> responseExcludePatterns = new ArrayList<>();
    
    public TransformationConfig(ModelTransformer modelTransformer, TransformationHelper transformHelper) {
        this.modelTransformer = modelTransformer;
        this.transformHelper = transformHelper;
        
        // 패턴 초기화
        initPatterns();
        
        log.info("데이터 변환 시스템 초기화 완료: 전체={}, 요청={}, 응답={}", 
                 transformEnabled, requestTransformEnabled, responseTransformEnabled);
    }
    
    /**
     * URL 패턴 초기화
     * 여기에서 URL 패턴을 정의하여 어떤 URL이 변환 대상인지 설정
     */
    private void initPatterns() {
        // 요청 변환 포함 패턴 - 기본적으로 모든 URL 포함
        requestIncludePatterns.add(Pattern.compile(".*"));
        
        // 요청 변환 제외 패턴 - 변환하지 않을 URL 패턴 추가
        requestExcludePatterns.add(Pattern.compile("/api/raw/.*"));
        requestExcludePatterns.add(Pattern.compile("/docs/.*"));
        requestExcludePatterns.add(Pattern.compile("/error"));
        requestExcludePatterns.add(Pattern.compile("/swagger-ui/.*"));
        
        // 응답 변환 포함 패턴 - 기본적으로 모든 URL 포함
        responseIncludePatterns.add(Pattern.compile(".*"));
        
        // 응답 변환 제외 패턴 - 변환하지 않을 URL 패턴 추가
        responseExcludePatterns.add(Pattern.compile("/api/raw/.*"));
        responseExcludePatterns.add(Pattern.compile("/docs/.*"));
        responseExcludePatterns.add(Pattern.compile("/error"));
        responseExcludePatterns.add(Pattern.compile("/swagger-ui/.*"));
        
        log.info("URL 패턴 초기화 완료");
    }
    
    /**
     * 주어진 URL이 요청 변환 대상인지 확인
     * @param url 확인할 URL
     * @return 변환 대상이면 true, 아니면 false
     */
    public boolean shouldTransformRequest(String url) {
        if (!transformEnabled || !requestTransformEnabled) return false;
        
        // 제외 패턴과 일치하면 변환하지 않음
        for (Pattern pattern : requestExcludePatterns) {
            if (pattern.matcher(url).matches()) {
                log.debug("요청 변환 제외 URL: {}", url);
                return false;
            }
        }
        
        // 포함 패턴과 일치하면 변환
        for (Pattern pattern : requestIncludePatterns) {
            if (pattern.matcher(url).matches()) {
                log.debug("요청 변환 대상 URL: {}", url);
                return true;
            }
        }
        
        // 기본적으로 변환하지 않음
        return false;
    }
    
    /**
     * 주어진 URL이 응답 변환 대상인지 확인
     * @param url 확인할 URL
     * @return 변환 대상이면 true, 아니면 false
     */
    public boolean shouldTransformResponse(String url) {
        if (!transformEnabled || !responseTransformEnabled) return false;
        
        // 제외 패턴과 일치하면 변환하지 않음
        for (Pattern pattern : responseExcludePatterns) {
            if (pattern.matcher(url).matches()) {
                log.debug("응답 변환 제외 URL: {}", url);
                return false;
            }
        }
        
        // 포함 패턴과 일치하면 변환
        for (Pattern pattern : responseIncludePatterns) {
            if (pattern.matcher(url).matches()) {
                log.debug("응답 변환 대상 URL: {}", url);
                return true;
            }
        }
        
        // 기본적으로 변환하지 않음
        return false;
    }
    
    /**
     * ModelTransformer를 Spring MVC에 등록
     */
    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> handlers) {
        handlers.add(modelTransformer);
    }
}