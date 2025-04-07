/**
 * docFormView.js - 문서 양식 상세 조회 스크립트
 * 
 * 문서 양식 상세 정보 표시 및 관리 기능을 제공합니다.
 * - 양식 정보 조회
 * - 수정 및 삭제 기능
 * 
 * @version 1.0.0
 * @since 2025-04-07
 */

const DocFormView = (function() {
    //===========================================================================
    // 모듈 내부 변수
    //===========================================================================
    
    /**
     * 양식 정보
     */
    let formId = '';
    
    /**
     * API URL 상수 정의
     */
    const API_URLS = {
        FORM: (id) => `/api/eapproval/forms/${id}`
    };
    
    /**
     * 페이지 URL 상수 정의
     */
    const PAGE_URLS = {
        FORM_LIST: '/eapproval/forms',
        FORM_EDIT: (id) => `/eapproval/forms/edit/${id}`
    };
    
    //===========================================================================
    // 초기화 및 이벤트 처리 함수
    //===========================================================================
    
    /**
     * 모듈 초기화 함수
     * 
     * @returns {Promise<void>}
     */
    async function initialize() {
        try {
            console.log('DocFormView 초기화를 시작합니다.');
            
            // 초기 데이터 로드
            loadInitialData();
            
            // 이벤트 리스너 등록
            registerEventListeners();
            
            console.log('DocFormView 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('초기화 중 오류 발생:', error);
            await AlertUtil.showError('초기화 오류', '페이지 초기화 중 오류가 발생했습니다.');
        }
    }
    
    /**
     * 초기 데이터 로드 함수
     */
    function loadInitialData() {
        console.log('초기 데이터를 로드합니다.');
        
        // window.docFormData에서 양식 ID 가져오기
        formId = window.docFormData?.formId || '';
        
        if (!formId) {
            // URL에서 양식 ID 추출 시도
            const urlParts = window.location.pathname.split('/');
            formId = urlParts[urlParts.length - 1];
        }
        
        console.log('로드된 양식 ID:', formId);
    }
    
    /**
     * 이벤트 리스너 등록 함수
     */
    function registerEventListeners() {
        console.log('이벤트 리스너 등록을 시작합니다.');
        
        // 목록으로 버튼
        const backBtn = document.getElementById('backBtn');
        if (backBtn) {
            backBtn.addEventListener('click', () => {
                window.location.href = PAGE_URLS.FORM_LIST;
            });
        }
        
        // 수정 버튼
        const editBtn = document.getElementById('editBtn');
        if (editBtn) {
            editBtn.addEventListener('click', () => {
                if (formId) {
                    window.location.href = PAGE_URLS.FORM_EDIT(formId);
                }
            });
        }
        
        // 삭제 버튼
        const deleteBtn = document.getElementById('deleteBtn');
        if (deleteBtn) {
            deleteBtn.addEventListener('click', () => {
                if (formId) {
                    deleteForm(formId);
                }
            });
        }
        
        console.log('이벤트 리스너 등록이 완료되었습니다.');
    }
    
    //===========================================================================
    // 양식 관리 함수
    //===========================================================================
    
    /**
     * 양식 삭제 함수
     * 
     * @param {string} formId - 삭제할 양식 ID
     * @returns {Promise<void>}
     */
    async function deleteForm(formId) {
        try {
            console.log(`양식 삭제 시작: ${formId}`);
            
            if (!formId) {
                throw new Error('삭제할 양식 ID가 지정되지 않았습니다.');
            }
            
            // 삭제 확인 대화상자
            const confirmed = await AlertUtil.showConfirm({
                title: '양식 삭제',
                text: `양식 ID "${formId}"를 삭제하시겠습니까?\n삭제된 양식은 복구할 수 없습니다.`,
                icon: 'warning',
                confirmButtonText: '삭제',
                cancelButtonText: '취소'
            });
            
            if (!confirmed) {
                console.log('사용자가 삭제를 취소했습니다.');
                return;
            }
            
            // 삭제 API 호출
            const response = await ApiUtil.delWithLoading(
                API_URLS.FORM(formId),
                null,
                '양식 삭제 중...'
            );
            
            // 응답 확인
            if (!response.success) {
                throw new Error(response.message || '양식 삭제에 실패했습니다.');
            }
            
            // 성공 메시지 표시
            await AlertUtil.showSuccess('삭제 완료', '양식이 성공적으로 삭제되었습니다.', () => {
                // 성공 후 목록 페이지로 이동
                window.location.href = `${PAGE_URLS.FORM_LIST}?msg=${encodeURIComponent('양식이 삭제되었습니다.')}`;
            });
            
            console.log(`양식 삭제 완료: ${formId}`);
        } catch (error) {
            console.error('양식 삭제 중 오류:', error);
            await ApiUtil.handleApiError(error, '양식 삭제 실패');
        }
    }
    
    //===========================================================================
    // 공개 API
    //===========================================================================
    
    return {
        // 초기화 함수
        initialize,
        
        // 양식 관리 함수
        deleteForm
    };
})();

// DOM 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    DocFormView.initialize();
});