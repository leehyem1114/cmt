package com.example.cmtProject.comm.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class ResponseTransformer implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    public ResponseTransformer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // ApiResponse 타입의 응답에만 적용
        return returnType.getParameterType().equals(ApiResponse.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof ApiResponse) {
            ApiResponse<?> apiResponse = (ApiResponse<?>) body;
            Object data = apiResponse.getData();
            
            if (data instanceof List) {
                List<?> list = (List<?>) data;
                if (!list.isEmpty() && list.get(0) instanceof Map) {
                    // 리스트의 각 맵 객체 변환
                    return transformListData(apiResponse);
                } else if (!list.isEmpty()) {
                    // DTO 객체 리스트 변환
                    return transformDtoListData(apiResponse);
                }
            } else if (data instanceof Map) {
                // 단일 맵 객체 변환
                return transformMapData(apiResponse);
            } else if (data != null && !(data instanceof String) && !(data instanceof Number) && !(data instanceof Boolean)) {
                // 단일 DTO 객체 변환
                return transformDtoData(apiResponse);
            }
        }
        return body;
    }

    // 맵 리스트 데이터 변환
    @SuppressWarnings("unchecked")
    private ApiResponse<?> transformListData(ApiResponse<?> apiResponse) {
        List<Map<String, Object>> originalList = (List<Map<String, Object>>) apiResponse.getData();
        List<Map<String, Object>> transformedList = originalList.stream()
                .map(this::transformMap)
                .collect(java.util.stream.Collectors.toList());
        
        return ApiResponse.success(apiResponse.getMessage(), transformedList);
    }
    
    // DTO 리스트 데이터 변환
    @SuppressWarnings("unchecked")
    private ApiResponse<?> transformDtoListData(ApiResponse<?> apiResponse) {
        List<?> originalList = (List<?>) apiResponse.getData();
        List<Map<String, Object>> transformedList = originalList.stream()
                .map(dto -> {
                    // DTO를 Map으로 변환
                    Map<String, Object> map = objectMapper.convertValue(dto, Map.class);
                    return transformMap(map);
                })
                .collect(java.util.stream.Collectors.toList());
        
        return ApiResponse.success(apiResponse.getMessage(), transformedList);
    }
    
    // 단일 맵 데이터 변환
    @SuppressWarnings("unchecked")
    private ApiResponse<?> transformMapData(ApiResponse<?> apiResponse) {
        Map<String, Object> originalMap = (Map<String, Object>) apiResponse.getData();
        Map<String, Object> transformedMap = transformMap(originalMap);
        
        return ApiResponse.success(apiResponse.getMessage(), transformedMap);
    }
    
    // 단일 DTO 데이터 변환
    private ApiResponse<?> transformDtoData(ApiResponse<?> apiResponse) {
        Object dto = apiResponse.getData();
        // DTO를 Map으로 변환
        Map<String, Object> map = objectMapper.convertValue(dto, Map.class);
        Map<String, Object> transformedMap = transformMap(map);
        
        return ApiResponse.success(apiResponse.getMessage(), transformedMap);
    }
    
    // 맵의 키 이름 변환 (카멜케이스 -> 대문자+언더스코어)
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
                            .collect(java.util.stream.Collectors.toList());
                }
            }
            
            result.put(transformedKey, value);
        }
        
        return result;
    }
    
    // 카멜케이스를 대문자+언더스코어로 변환 (예: cmnCode -> CMN_CODE)
    private String transformKey(String key) {
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                result.append('_');
            }
            result.append(Character.toUpperCase(c));
        }
        
        return result.toString();
    }
}