package com.example.cmtProject.comm.response;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.example.cmtProject.config.TransformationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * URL 패턴 기반 응답 데이터 변환 처리
 * 카멜케이스 형식을 대문자+언더스코어 형식으로 변환
 */
@ControllerAdvice
@Slf4j
public class ResponseTransformer implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;
    private final TransformationHelper transformHelper;
    private final TransformationConfig transformConfig;

    public ResponseTransformer(ObjectMapper objectMapper, TransformationHelper transformHelper, 
                              TransformationConfig transformConfig) {
        this.objectMapper = objectMapper;
        this.transformHelper = transformHelper;
        this.transformConfig = transformConfig;
        log.info("ResponseTransformer 초기화 완료");
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // URL 정보 추출
        String requestURI = extractRequestURI(returnType);
        if (requestURI == null) {
            return false;
        }
        
        // 설정에 따라 변환 여부 결정
        return transformConfig.shouldTransformResponse(requestURI);
    }

    /**
     * 메소드 파라미터에서 요청 URI 추출
     */
    private String extractRequestURI(MethodParameter returnType) {
        try {
            StringBuilder uriBuilder = new StringBuilder();
            
            // 컨트롤러 클래스의 기본 URL 가져오기
            if (returnType.getContainingClass().isAnnotationPresent(RequestMapping.class)) {
                String[] classPaths = returnType.getContainingClass().getAnnotation(RequestMapping.class).value();
                if (classPaths.length > 0) {
                    uriBuilder.append(classPaths[0]);
                }
            }
            
            // 메소드에 RequestMapping이 있으면 경로 추가
            if (returnType.hasMethodAnnotation(RequestMapping.class)) {
                RequestMapping annotation = returnType.getMethodAnnotation(RequestMapping.class);
                String[] methodPaths = annotation.value();
                if (methodPaths.length > 0) {
                    if (uriBuilder.length() > 0 && !uriBuilder.toString().endsWith("/") && !methodPaths[0].startsWith("/")) {
                        uriBuilder.append("/");
                    }
                    uriBuilder.append(methodPaths[0]);
                }
            }
            
            // GetMapping 처리
            else if (returnType.hasMethodAnnotation(org.springframework.web.bind.annotation.GetMapping.class)) {
                org.springframework.web.bind.annotation.GetMapping annotation = 
                    returnType.getMethodAnnotation(org.springframework.web.bind.annotation.GetMapping.class);
                String[] paths = annotation.value();
                if (paths.length > 0) {
                    if (uriBuilder.length() > 0 && !uriBuilder.toString().endsWith("/") && !paths[0].startsWith("/")) {
                        uriBuilder.append("/");
                    }
                    uriBuilder.append(paths[0]);
                }
            }
            
            // PostMapping 처리
            else if (returnType.hasMethodAnnotation(org.springframework.web.bind.annotation.PostMapping.class)) {
                org.springframework.web.bind.annotation.PostMapping annotation = 
                    returnType.getMethodAnnotation(org.springframework.web.bind.annotation.PostMapping.class);
                String[] paths = annotation.value();
                if (paths.length > 0) {
                    if (uriBuilder.length() > 0 && !uriBuilder.toString().endsWith("/") && !paths[0].startsWith("/")) {
                        uriBuilder.append("/");
                    }
                    uriBuilder.append(paths[0]);
                }
            }
            
            // PutMapping 처리
            else if (returnType.hasMethodAnnotation(org.springframework.web.bind.annotation.PutMapping.class)) {
                org.springframework.web.bind.annotation.PutMapping annotation = 
                    returnType.getMethodAnnotation(org.springframework.web.bind.annotation.PutMapping.class);
                String[] paths = annotation.value();
                if (paths.length > 0) {
                    if (uriBuilder.length() > 0 && !uriBuilder.toString().endsWith("/") && !paths[0].startsWith("/")) {
                        uriBuilder.append("/");
                    }
                    uriBuilder.append(paths[0]);
                }
            }
            
            // DeleteMapping 처리
            else if (returnType.hasMethodAnnotation(org.springframework.web.bind.annotation.DeleteMapping.class)) {
                org.springframework.web.bind.annotation.DeleteMapping annotation = 
                    returnType.getMethodAnnotation(org.springframework.web.bind.annotation.DeleteMapping.class);
                String[] paths = annotation.value();
                if (paths.length > 0) {
                    if (uriBuilder.length() > 0 && !uriBuilder.toString().endsWith("/") && !paths[0].startsWith("/")) {
                        uriBuilder.append("/");
                    }
                    uriBuilder.append(paths[0]);
                }
            }
            
            // PatchMapping 처리
            else if (returnType.hasMethodAnnotation(org.springframework.web.bind.annotation.PatchMapping.class)) {
                org.springframework.web.bind.annotation.PatchMapping annotation = 
                    returnType.getMethodAnnotation(org.springframework.web.bind.annotation.PatchMapping.class);
                String[] paths = annotation.value();
                if (paths.length > 0) {
                    if (uriBuilder.length() > 0 && !uriBuilder.toString().endsWith("/") && !paths[0].startsWith("/")) {
                        uriBuilder.append("/");
                    }
                    uriBuilder.append(paths[0]);
                }
            }
            
            return uriBuilder.toString();
        } catch (Exception e) {
            log.warn("RequestURI 추출 실패: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                 MediaType selectedContentType,
                                 Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                 ServerHttpRequest request, ServerHttpResponse response) {
        
        if (body == null) return null;
        
        try {
            Object result;
            
            // ApiResponse 타입 처리
            if (body instanceof ApiResponse) {
                ApiResponse<?> apiResponse = (ApiResponse<?>) body;
                Object data = apiResponse.getData();
                
                if (data != null) {
                    Object transformedData = transformData(data);
                    result = createNewApiResponse(apiResponse, transformedData);
                } else {
                    result = body;
                }
            } 
            // 직접 반환하는 데이터 처리
            else {
                result = transformData(body);
            }
            
            log.debug("응답 데이터 변환 완료: {}", returnType.getExecutable());
            return result;
        } catch (Exception e) {
            log.error("응답 데이터 변환 실패: {}", e.getMessage(), e);
            return body;
        }
    }
    
    // 데이터 타입에 따른 변환
    private Object transformData(Object data) {
        if (data == null) return null;
        
        if (data instanceof List) {
            List<?> list = (List<?>) data;
            if (!list.isEmpty()) {
                if (list.get(0) instanceof Map) {
                    // 맵 리스트 변환
                    return list.stream()
                        .map(item -> transformHelper.transformMapFromInternalToExternal((Map<String, Object>) item))
                        .collect(Collectors.toList());
                } else if (!(list.get(0) instanceof String) && !(list.get(0) instanceof Number) && !(list.get(0) instanceof Boolean)) {
                    // DTO 리스트 변환
                    return list.stream()
                        .map(dto -> {
                            try {
                                Map<String, Object> map = objectMapper.convertValue(dto, Map.class);
                                return transformHelper.transformMapFromInternalToExternal(map);
                            } catch (Exception e) {
                                log.warn("DTO 리스트 항목 변환 실패: {}", e.getMessage());
                                return dto;
                            }
                        })
                        .collect(Collectors.toList());
                }
            }
            return list;
        } else if (data instanceof Map) {
            // 맵 변환
            return transformHelper.transformMapFromInternalToExternal((Map<String, Object>) data);
        } else if (!(data instanceof String) && !(data instanceof Number) && !(data instanceof Boolean)) {
            try {
                // DTO 변환
                Map<String, Object> map = objectMapper.convertValue(data, Map.class);
                return transformHelper.transformMapFromInternalToExternal(map);
            } catch (Exception e) {
                log.warn("DTO 변환 실패: {}", e.getMessage());
                return data;
            }
        }
        
        return data;
    }
    
    // 원본 ApiResponse의 속성을 유지하면서 새 ApiResponse 객체 생성
    private ApiResponse<?> createNewApiResponse(ApiResponse<?> original, Object transformedData) {
        if (original.isSuccess()) {
            return ApiResponse.success(original.getMessage(), transformedData);
        } else {
            return ApiResponse.error(original.getMessage(), original.getCode());
        }
    }
}