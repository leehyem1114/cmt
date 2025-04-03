/**
 * documentFormManager.js - 문서 폼 관리 모듈
 * 
 * 전자결재 문서 폼을 관리하고 제출하는 기능을 제공합니다.
 * - 문서 데이터 초기화
 * - 폼 유효성 검사
 * - 문서 저장 (임시저장/결재요청)
 * 
 * @version 1.0.0
 */

// 즉시 실행 함수로 모듈 스코프 생성
const DocumentFormManager = (function() {
    // 문서 정보
    let documentData = {
        docId: '',
        docNumber: '',
        formId: '',
        title: '',
        content: ''
    };
    
    /**
     * 모듈 초기화
     */
    function initialize() {
        console.log('DocumentFormManager 초기화 시작');
        
        try {
            // 모듈 내부 데이터 초기화
            initializeData();
            
            // UI 초기화 (스타일 등)
            initializeUI();
            
            // 이벤트 핸들러 등록
            setupEventHandlers();
            
            console.log('DocumentFormManager 초기화 완료');
        } catch (error) {
            console.error('DocumentFormManager 초기화 중 오류 발생:', error);
            AlertUtil.showError('초기화 오류', '페이지 초기화 중 오류가 발생했습니다.');
        }
    }
    
    /**
     * 데이터 초기화 함수
     */
    function initializeData() {
        // Thymeleaf에서 전달받은 데이터 로드
        if (window.documentData) {
			documentData = {
			    docId: window.documentData.DOC_ID || '',
			    docNumber: window.documentData.DOC_NUMBER || '',
			    formId: window.documentData.FORM_ID || '',
			    title: window.documentData.TITLE || '',
			    content: window.documentData.CONTENT || ''
            };
            
            // 폼 필드 초기화
            $('#docId').val(documentData.docId);
            $('#docNumber').val(documentData.docNumber);
            $('#formId').val(documentData.formId);
            $('#title').val(documentData.title);
            
            console.log('문서 데이터 초기화:', documentData);
        } else {
            // DOM에서 직접 데이터 로드
            documentData.docId = $('#docId').val() || '';
            documentData.docNumber = $('#docNumber').val() || '';
            documentData.formId = $('#formId').val() || '';
            documentData.title = $('#title').val() || '';
            // content는 에디터 초기화 후 로드
        }
    }
    
    /**
     * UI 초기화 함수
     */
    function initializeUI() {
        // 로딩 오버레이 스타일 추가
        addLoadingOverlayStyle();
        
        // 에디터 컨테이너에 상대 위치 클래스 추가
        const editorCard = $('#contentEditor').closest('.card');
        editorCard.addClass('relative-container');
    }
    
    /**
     * 로딩 오버레이 스타일 추가
     */
    function addLoadingOverlayStyle() {
        const styleEl = document.createElement('style');
        styleEl.textContent = `
            .loading-overlay {
                position: absolute;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: rgba(255, 255, 255, 0.8);
                display: flex;
                justify-content: center;
                align-items: center;
                z-index: 1000;
            }
            
            .relative-container {
                position: relative;
            }
        `;
        document.head.appendChild(styleEl);
    }
    
    /**
     * 이벤트 핸들러 등록
     */
    function setupEventHandlers() {
        // 임시저장 버튼 클릭
        $('#btnTempSave').on('click', function() {
            saveDocument(true);
        });
        
        // 결재요청 버튼 클릭
        $('#btnSubmit').on('click', function() {
            saveDocument(false);
        });
    }
    
    /**
     * 폼 유효성 검사
     * @returns {boolean} 유효성 검사 결과
     */
    function validateForm() {
        const formId = $('#formId').val();
        const title = $('#title').val().trim();
        const content = FormContentLoader.getEditorContent();
        
        if (!formId) {
            AlertUtil.showWarning('유효성 검사', '문서 양식을 선택해주세요.');
            $('#formId').focus();
            return false;
        }
        
        if (!title) {
            AlertUtil.showWarning('유효성 검사', '제목을 입력해주세요.');
            $('#title').focus();
            return false;
        }
        
        if (!content || content === '<p><br></p>') {
            AlertUtil.showWarning('유효성 검사', '내용을 입력해주세요.');
            $('#contentEditor').summernote('focus');
            return false;
        }
        
        // 결재선 유효성 검사
        return ApprovalLineManager.validateApprovalLines();
    }
    
    /**
     * 문서 저장 (임시저장 또는 결재요청)
     * @param {boolean} isTempSave - 임시저장 여부
     */
    async function saveDocument(isTempSave) {
        if (!validateForm()) return;
        
        try {
            // 로딩 표시 시작
            const loading = AlertUtil.showLoading(isTempSave ? '임시저장 중...' : '결재요청 중...');
            
            // 폼 데이터 구성
            const formData = new FormData();
            formData.append('docId', documentData.docId || '');
            formData.append('docNumber', documentData.docNumber || '');
            formData.append('formId', $('#formId').val());
            formData.append('title', $('#title').val().trim());
            formData.append('content', FormContentLoader.getEditorContent());
            formData.append('isTempSave', isTempSave);
            formData.append('approvalLinesJson', JSON.stringify(ApprovalLineManager.getApprovalLines()));
            
            // 첨부파일 추가
            AttachmentManager.appendFilesToFormData(formData);
            
            // API 호출
            const response = await fetch('/api/eapproval/document', {
                method: 'POST',
                body: formData
            });
            
            // 로딩 종료
            loading.close();
            
            if (!response.ok) {
                throw new Error('서버 응답 오류: ' + response.status);
            }
            
            const result = await response.json();
            
            // 최상위 속성은 소문자로 접근
            if (result.success) {
                // 성공 알림
                await AlertUtil.showSuccess(
                    '저장 완료', 
                    isTempSave ? '문서가 임시저장되었습니다.' : '결재요청이 완료되었습니다.'
                );
                
                // 저장 완료 후 페이지 이동
                setTimeout(() => {
                    if (isTempSave) {
                        location.href = '/eapproval/documents';
                    } else {
                        // 내부 데이터는 대문자 속성으로 접근
                        const docId = result.data?.DOC_ID;
                        if (docId) {
                            location.href = `/eapproval/document/view/${docId}`;
                        } else {
                            location.href = '/eapproval/documents';
                        }
                    }
                }, 500);
            } else {
                // 오류 메시지 소문자로 접근
                throw new Error(result.message || '문서 저장 중 오류가 발생했습니다.');
            }
        } catch (error) {
            console.error('문서 저장 오류:', error);
            await AlertUtil.showError('저장 실패', error.message || '문서 저장 중 오류가 발생했습니다.');
        }
    }
    
    /**
     * 문서 ID 가져오기
     * @returns {string} 문서 ID
     */
    function getDocumentId() {
        return documentData.docId;
    }
    
    /**
     * 문서 내용 가져오기
     * @returns {string} 문서 내용
     */
    function getDocumentContent() {
        return documentData.content;
    }
    
    // 공개 API
    return {
        initialize,
        validateForm,
        saveDocument,
        getDocumentId,
        getDocumentContent
    };
})();