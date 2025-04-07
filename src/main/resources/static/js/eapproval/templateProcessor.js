/**
 * templateProcessor.js - 템플릿 처리 유틸리티
 * 
 * 문서 양식의 플레이스홀더를 실제 데이터로 치환하는 기능을 제공합니다.
 * 
 * @version 1.0.0
 * @since 2025-04-07
 */

const TemplateProcessor = (function() {
    // API URL 상수 정의
    const API_URL = '/api/eapproval/template-data';
    
    /**
     * 템플릿 문자열 내의 플레이스홀더를 실제 데이터로 치환
     * 
     * @param {string} template - 플레이스홀더가 포함된 템플릿 문자열
     * @param {Object} data - 치환할 데이터 객체
     * @returns {string} 치환된 결과 문자열
     */
    function processTemplate(template, data) {
        if (!template || typeof template !== 'string') {
            console.warn('유효하지 않은 템플릿 문자열입니다.');
            return template;
        }
        
        if (!data || typeof data !== 'object') {
            console.warn('유효하지 않은 데이터 객체입니다.');
            return template;
        }
        
        // {{변수명}} 패턴 찾기
        return template.replace(/\{\{([^}]+)\}\}/g, function(match, key) {
            // 키를 트림하여 공백 제거
            const trimmedKey = key.trim();
            
            // 데이터 객체에서 값을 조회
            const value = data[trimmedKey];
            
            // 값이 존재하면 값으로 치환, 없으면 플레이스홀더 유지
            return value !== undefined ? value : match;
        });
    }
    
    /**
     * 서버에서 템플릿 데이터 로드
     * 
     * @returns {Promise<Object>} 템플릿 데이터 객체
     */
    async function loadTemplateData() {
        try {
            // 로딩 표시
            const loading = window.AlertUtil ? 
                AlertUtil.showLoading('템플릿 데이터 로드 중...') : null;
            
            // API 호출
            const response = await fetch(API_URL);
            
            // 로딩 숨김
            if (loading) loading.close();
            
            if (!response.ok) {
                throw new Error('템플릿 데이터를 불러올 수 없습니다.');
            }
            
            const result = await response.json();
            
            // API 응답 성공 확인
            if (!result.success) {
                throw new Error(result.message || '템플릿 데이터를 불러올 수 없습니다.');
            }
            
            // data 필드 반환
            return result.data || {};
            
        } catch (error) {
            console.error('템플릿 데이터 로드 오류:', error);
            
            // 오류 알림
            if (window.AlertUtil) {
                await AlertUtil.showWarning('데이터 로드 오류', '템플릿 데이터를 불러오는 중 오류가 발생했습니다.');
            }
            
            // 빈 객체 반환
            return {};
        }
    }
    
    /**
     * 템플릿 처리 - 서버에서 데이터 로드 후 처리
     * 
     * @param {string} template - 플레이스홀더가 포함된 템플릿 문자열
     * @returns {Promise<string>} 처리된 템플릿 문자열
     */
    async function processTemplateFromServer(template) {
        // 템플릿 데이터 로드
        const data = await loadTemplateData();
        
        // 템플릿 처리
        return processTemplate(template, data);
    }
    
    // 공개 API
    return {
        processTemplate,
        processTemplateFromServer,
        loadTemplateData
    };
})();

// 전역 스코프에 노출
window.TemplateProcessor = TemplateProcessor;