package com.example.cmtProject.comm.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class RequestTransformer extends RequestBodyAdviceAdapter {
    
    private final ObjectMapper objectMapper;
    
    public RequestTransformer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }
    
    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        // Map 타입 변환
        if (body instanceof Map) {
            return transformMap((Map<String, Object>) body);
        } 
        // List 타입 변환
        else if (body instanceof List) {
            List<?> list = (List<?>) body;
            if (!list.isEmpty() && list.get(0) instanceof Map) {
                return list.stream()
                        .map(item -> transformMap((Map<String, Object>) item))
                        .collect(Collectors.toList());
            }
        }
        return body;
    }
    
    // Map의 키 이름 변환 (대문자+언더스코어 -> 카멜케이스)
    private Map<String, Object> transformMap(Map<String, Object> map) {
        Map<String, Object> result = new LinkedHashMap<>();
        
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String transformedKey = transformKey(entry.getKey());
            Object value = entry.getValue();
            
            // 재귀적으로 중첩된 맵 처리
            if (value instanceof Map) {
                value = transformMap((Map<String, Object>) value);
            } else if (value instanceof List) {
                List<?> list = (List<?>) value;
                if (!list.isEmpty() && list.get(0) instanceof Map) {
                    value = list.stream()
                            .map(item -> transformMap((Map<String, Object>) item))
                            .collect(Collectors.toList());
                }
            }
            
            result.put(transformedKey, value);
        }
        
        return result;
    }
    
    // 대문자+언더스코어를 카멜케이스로 변환 (예: CMN_CODE -> cmnCode)
    private String transformKey(String key) {
        StringBuilder result = new StringBuilder();
        boolean nextUpperCase = false;
        
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            
            if (c == '_') {
                nextUpperCase = true;
            } else {
                if (i == 0) {
                    result.append(Character.toLowerCase(c));
                } else if (nextUpperCase) {
                    result.append(Character.toUpperCase(c));
                    nextUpperCase = false;
                } else {
                    result.append(Character.toLowerCase(c));
                }
            }
        }
        
        return result.toString();
    }
} //RequestTransformer