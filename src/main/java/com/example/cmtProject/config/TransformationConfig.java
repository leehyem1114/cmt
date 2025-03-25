package com.example.cmtProject.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.cmtProject.comm.response.ModelTransformer;

import lombok.extern.slf4j.Slf4j;

/**
 * 데이터 변환 시스템을 위한 설정
 */
@Configuration
@Slf4j
public class TransformationConfig implements WebMvcConfigurer {
    
    private final ModelTransformer modelTransformer;
    
    public TransformationConfig(ModelTransformer modelTransformer) {
        this.modelTransformer = modelTransformer;
        log.info("데이터 변환 시스템 초기화 완료");
    }
    
    /**
     * ModelTransformer를 Spring MVC에 등록
     */
    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> handlers) {
        handlers.add(modelTransformer);
    }
}