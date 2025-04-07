/**
 * docFormEdit.js - 문서 양식 편집 스크립트
 * 
 * 문서 양식 등록 및 수정 기능을 제공합니다.
 * - 양식 정보 입력 및 유효성 검사
 * - 에디터를 이용한 양식 내용 편집
 * - 양식 저장 기능
 * 
 * @version 1.0.0
 * @since 2025-04-07
 */

const DocFormEdit = (function() {
    //===========================================================================
    // 모듈 내부 변수
    //===========================================================================
    
    /**
     * 에디터 인스턴스
     */
    let editor = null;
    
    /**
     * 초기 폼 데이터
     */
    let initialFormData = {};
    
    /**
     * API URL 상수 정의
     */
    const API_URLS = {
        FORMS: '/api/eapproval/forms',
        FORM: (id) => `/api/eapproval/forms/${id}`
    };
    
    /**
     * 페이지 URL 상수 정의
     */
    const PAGE_URLS = {
        FORM_LIST: '/eapproval/forms'
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
            console.log('DocFormEdit 초기화를 시작합니다.');
            
            // 초기 데이터 로드
            loadInitialData();
            
            // 에디터 초기화
            await initializeEditor();
            
            // 이벤트 리스너 등록
            registerEventListeners();
            
            console.log('DocFormEdit 초기화가 완료되었습니다.');
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
        
        // window.docFormData에서 초기 데이터 가져오기
        const isNew = window.docFormData?.isNew === true;
        const docForm = window.docFormData?.docForm || {};
        
        // 초기 데이터 저장
        initialFormData = {
            isNew: isNew,
            formId: docForm.FORM_ID || docForm.formId || '',
            formContent: docForm.FORM_CONTENT || docForm.formContent || '',
            creatorId: docForm.CREATOR_ID || docForm.creatorId || '',
            createDate: docForm.CREATE_DATE || docForm.createDate || null,
            updaterId: docForm.UPDATER_ID || docForm.updaterId || '',
            updateDate: docForm.UPDATE_DATE || docForm.updateDate || null
        };
        
        console.log('초기 데이터 로드 완료:', initialFormData);
    }
    
    /**
     * 에디터 초기화 함수
     * 
     * @returns {Promise<void>}
     */
    async function initializeEditor() {
        try {
            console.log('에디터 초기화를 시작합니다.');
            
            // 에디터 요소 확인
            const editorElement = document.getElementById('formContentEditor');
            if (!editorElement) {
                throw new Error('에디터 요소를 찾을 수 없습니다.');
            }
            
            // jQuery 확인
            if (typeof $ !== 'function') {
                throw new Error('jQuery를 찾을 수 없습니다.');
            }
            
            // Summernote 에디터 옵션
            const editorOptions = {
                height: 500,
                lang: 'ko-KR',
                placeholder: '양식 내용을 입력하세요',
                toolbar: [
                    ['style', ['style']],
                    ['font', ['bold', 'underline', 'clear']],
                    ['color', ['color']],
                    ['para', ['ul', 'ol', 'paragraph']],
                    ['table', ['table']],
                    ['insert', ['link']],
                    ['view', ['fullscreen', 'codeview', 'help']]
                ],
                callbacks: {
                    onInit: function() {
                        console.log('에디터가 초기화되었습니다.');
                        editor = this;
                    }
                }
            };
            
            // Summernote 에디터 초기화
            $(editorElement).summernote(editorOptions);
            
            // 초기 내용 설정
            if (initialFormData.formContent) {
                $(editorElement).summernote('code', initialFormData.formContent);
                console.log('초기 에디터 내용 설정 완료');
            }
            
            console.log('에디터 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('에디터 초기화 중 오류:', error);
            throw error;
        }
    }
    
    /**
     * 이벤트 리스너 등록 함수
     */
    function registerEventListeners() {
        console.log('이벤트 리스너 등록을 시작합니다.');
        
        // 저장 버튼
        const saveBtn = document.getElementById('saveBtn');
        if (saveBtn) {
            saveBtn.addEventListener('click', () => {
                saveForm();
            });
        }
        
        // 취소 버튼
        const cancelBtn = document.getElementById('cancelBtn');
        if (cancelBtn) {
            cancelBtn.addEventListener('click', () => {
                confirmCancel();
            });
        }
        
        console.log('이벤트 리스너 등록이 완료되었습니다.');
    }
    
    //===========================================================================
    // 폼 처리 함수
    //===========================================================================
    
    /**
     * 폼 유효성 검사 함수
     * 
     * @returns {boolean} 유효성 검사 결과
     */
    async function validateForm() {
        console.log('폼 유효성 검사를 시작합니다.');
        
        // 양식 ID 검사
        const formId = document.getElementById('formId').value.trim();
        if (!formId) {
            await AlertUtil.showWarning('필수 항목 누락', '양식 ID를 입력해주세요.');
            document.getElementById('formId').focus();
            return false;
        }
        
        // 양식 내용 검사
        const formContent = $('#formContentEditor').summernote('code');
        if (!formContent || formContent === '<p><br></p>' || formContent.trim() === '') {
            await AlertUtil.showWarning('필수 항목 누락', '양식 내용을 입력해주세요.');
            $('#formContentEditor').summernote('focus');
            return false;
        }
        
        console.log('폼 유효성 검사 통과');
        return true;
    }
    
    /**
     * 폼 데이터 수집 함수
     * 
     * @returns {Object} 수집된 폼 데이터
     */
    function collectFormData() {
        console.log('폼 데이터 수집을 시작합니다.');
        
        const formData = {
            formId: document.getElementById('formId').value.trim(),
            formContent: $('#formContentEditor').summernote('code'),
            creatorId: initialFormData.creatorId || null,
            createDate: initialFormData.createDate || null,
            updaterId: initialFormData.updaterId || null,
            updateDate: initialFormData.updateDate || null
        };
        
        console.log('폼 데이터 수집 완료:', formData);
        return formData;
    }
    
    /**
     * 양식 저장 함수
     * 
     * @returns {Promise<void>}
     */
    async function saveForm() {
        try {
            console.log('양식 저장을 시작합니다.');
            
            // 유효성 검사
            if (!(await validateForm())) {
                return;
            }
            
            // 폼 데이터 수집
            const formData = collectFormData();
            
            // API 호출
            const response = await ApiUtil.postWithLoading(
                API_URLS.FORMS,
                formData,
                '양식 저장 중...'
            );
            
            // 응답 확인
            if (!response.success) {
                throw new Error(response.message || '양식 저장에 실패했습니다.');
            }
            
            // 성공 메시지 표시
            await AlertUtil.showSuccess(
                '저장 완료', 
                initialFormData.isNew ? '새 양식이 등록되었습니다.' : '양식이 수정되었습니다.',
                () => {
                    // 성공 후 목록 페이지로 이동
                    window.location.href = `${PAGE_URLS.FORM_LIST}?msg=${encodeURIComponent('양식이 성공적으로 저장되었습니다.')}`;
                }
            );
            
            console.log('양식 저장이 완료되었습니다.');
        } catch (error) {
            console.error('양식 저장 중 오류:', error);
            await ApiUtil.handleApiError(error, '양식 저장 실패');
        }
    }
    
    /**
     * 취소 확인 함수
     * 
     * @returns {Promise<void>}
     */
    async function confirmCancel() {
        try {
            console.log('취소 확인 대화상자를 표시합니다.');
            
            const confirmed = await AlertUtil.showConfirm({
                title: '작업 취소',
                text: '변경 사항이 저장되지 않습니다. 계속하시겠습니까?',
                icon: 'warning',
                confirmButtonText: '예',
                cancelButtonText: '아니오'
            });
            
            if (confirmed) {
                // 목록 페이지로 이동
                window.location.href = PAGE_URLS.FORM_LIST;
            }
        } catch (error) {
            console.error('취소 확인 중 오류:', error);
        }
    }
    
    //===========================================================================
    // 공개 API
    //===========================================================================
    
    return {
        // 초기화 함수
        initialize,
        
        // 폼 처리 함수
        saveForm,
        validateForm
    };
})();

// DOM 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    DocFormEdit.initialize();
});