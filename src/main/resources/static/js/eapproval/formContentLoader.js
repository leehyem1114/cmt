/**
 * formContentLoader.js - 문서 양식 로드 모듈
 * 
 * 전자결재 문서 양식을 불러오고 관리하는 기능을 제공합니다.
 * - 양식 내용 불러오기
 * - 에디터에 양식 내용 적용
 * 
 * @version 1.0.0
 */

// 즉시 실행 함수로 모듈 스코프 생성
const FormContentLoader = (function() {
    /**
     * 모듈 초기화
     */
    function initialize() {
        console.log('FormContentLoader 초기화');
        setupEventHandlers();
    }
    
    /**
     * 이벤트 핸들러 설정
     */
    function setupEventHandlers() {
        // 양식 선택 시 내용 로드
        $('#formId').on('change', function() {
            const formId = $(this).val();
            if (formId) {
                loadFormContent(formId);
            }
        });
    }
    
    /**
     * 양식 내용 로드
     * @param {string} formId - 양식 ID
     * @returns {Promise<boolean>} 로드 성공 여부
     */
    async function loadFormContent(formId) {
        if (!formId) return false;
        
        console.log(`양식 ID ${formId} 선택됨, 내용 로드 중...`);
        
        try {
            // 로딩 표시
            const $contentCard = $('#contentEditor').closest('.card');
            $contentCard.append('<div class="loading-overlay"><div class="spinner-border text-primary" role="status"><span class="visually-hidden">로딩 중...</span></div></div>');
            
            // API 호출
            const response = await fetch(`/api/eapproval/form/${formId}`);
            const responseData = await response.json();
            
            // 로딩 표시 제거
            $contentCard.find('.loading-overlay').remove();
            
            if (!responseData.success || !responseData.data) {
                throw new Error(responseData.message || '양식을 불러올 수 없습니다.');
            }
            
            // 양식 내용 추출
			const formData = responseData.data;
			const formContent = formData.FORM_CONTENT || '';
			console.log('추출된 양식 내용:', formContent);
            
            if (formContent) {
                // Summernote 에디터에 내용 설정
                $('#contentEditor').summernote('code', formContent);
                console.log('양식 내용이 에디터에 로드되었습니다.');
            } else {
                console.warn('양식 내용이 비어 있습니다.');
                await AlertUtil.showWarning('양식 내용 없음', '선택한 양식에 기본 내용이 없습니다.');
            }
            
            return true;
        } catch (error) {
            console.error('양식 로드 중 오류 발생:', error);
            await AlertUtil.showWarning('양식 로드 실패', error.message || '양식을 불러올 수 없습니다.');
            return false;
        }
    }
    
    /**
     * 에디터 초기화
     */
    function initializeEditor() {
        $('#contentEditor').summernote({
            height: 400,
            lang: 'ko-KR',
            placeholder: '내용을 입력하세요',
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
                    // 초기 내용 설정 (문서 데이터가 있는 경우)
                    const content = DocumentFormManager.getDocumentContent();
                    if (content) {
                        $('#contentEditor').summernote('code', content);
                    }
                }
            }
        });
    }
    
    /**
     * 현재 에디터 내용 가져오기
     * @returns {string} 에디터 내용 HTML
     */
    function getEditorContent() {
        return $('#contentEditor').summernote('code');
    }
    
    // 공개 API
    return {
        initialize,
        initializeEditor,
        loadFormContent,
        getEditorContent
    };
})();