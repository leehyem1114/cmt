/**
 * document-form.js - 기안서 작성 관리 모듈
 * 
 * 기안서 작성 페이지의 기능을 관리하는 모듈입니다.
 * 결재선 관리, 첨부파일 관리, 기안서 데이터 저장 등의 기능을 제공합니다.
 * 
 * @version 1.1.0
 */

// 즉시 실행 함수로 모듈 스코프 생성
const DocumentForm = (function() {
    //================================================================================
    // 모듈 내부 변수
    //================================================================================
    
    // 문서 정보
    let documentData = {
        docId: '',
        docNumber: '',
        formId: '',
        title: '',
        content: '',
        approvalLines: []
    };
    
    // 결재선 데이터
    let approvalLines = [];
    
    // 첨부 파일 목록
    const fileList = [];

    //================================================================================
    // 모듈 초기화 함수
    //================================================================================
    
    /**
     * 모듈 초기화 함수
     */
    async function initialize() {
        console.log('DocumentForm 초기화 시작');
        
        try {
            // 모듈 내부 데이터 초기화
            initializeData();
            
            // UI 초기화 (에디터, 스타일 등)
            initializeUI();
            
            // 이벤트 핸들러 등록
            registerEventHandlers();
            
            // 결재자 목록 로드
            await loadApprovers();
            
            console.log('DocumentForm 초기화 완료');
        } catch (error) {
            console.error('DocumentForm 초기화 중 오류 발생:', error);
            await AlertUtil.showError('초기화 오류', '페이지 초기화 중 오류가 발생했습니다.');
        }
    }
    
    /**
     * 데이터 초기화 함수
     */
    function initializeData() {
        // Thymeleaf에서 전달받은 데이터 로드
        if (window.documentData) {
            documentData = {
                docId: window.documentData.docId || '',
                docNumber: window.documentData.docNumber || '',
                formId: window.documentData.formId || '',
                title: window.documentData.title || '',
                content: window.documentData.content || ''
            };
            
            // 결재선 데이터 로드
            if (window.documentData.approvalLines) {
                approvalLines = Array.isArray(window.documentData.approvalLines) ? 
                    window.documentData.approvalLines : [];
            }
        } else {
            // DOM에서 직접 데이터 로드
            documentData.docId = document.getElementById('docId').value || '';
            documentData.docNumber = document.getElementById('docNumber').value || '';
            documentData.formId = document.getElementById('formId').value || '';
            documentData.title = document.getElementById('title').value || '';
            // content는 에디터 초기화 후 로드
        }
        
        console.log('문서 데이터 초기화:', documentData);
        console.log('결재선 데이터 초기화:', approvalLines);
    }
    
    /**
     * UI 초기화 함수
     */
    function initializeUI() {
        // 로딩 오버레이 스타일 추가
        addLoadingOverlayStyle();
        
        // 에디터 초기화
        initializeEditor();
        
        // 결재선 테이블 초기화
        renderApprovalLines();
        
        // 첨부파일 목록 초기화
        renderFileList();
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
        
        // 에디터 컨테이너에 상대 위치 클래스 추가
        const editorCard = document.getElementById('contentEditor').closest('.card');
        editorCard.classList.add('relative-container');
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
                    // 초기 내용 설정
                    if (documentData.content) {
                        $('#contentEditor').summernote('code', documentData.content);
                    }
                }
            }
        });
    }

    //================================================================================
    // 이벤트 핸들러 등록 및 처리
    //================================================================================
    
    /**
     * 이벤트 핸들러 등록
     */
    function registerEventHandlers() {
        // 결재자 추가 버튼
        $('#btnAddApprover').on('click', addApprover);
        
        // 결재자 삭제 버튼 (동적 요소)
        $(document).on('click', '.btn-remove-line', function() {
            const index = $(this).closest('tr').data('index');
            removeApprover(index);
        });
        
        // 결재자 선택 변경 (동적 요소)
        $(document).on('change', '.approver-select', function() {
            const $row = $(this).closest('tr');
            const index = $row.data('index');
            const approverNo = $(this).val();
            handleApproverChange(index, approverNo, $(this));
        });
        
        // 결재 타입 변경 (동적 요소)
        $(document).on('change', 'select[data-field="approvalType"]', function() {
            const index = $(this).closest('tr').data('index');
            const value = $(this).val();
            updateApproverData(index, 'approvalType', value);
        });
        
        // 파일 선택 시 추가
        $('#fileInput').on('change', function() {
            addFiles(this.files);
            $(this).val(''); // 파일 선택 초기화
        });
        
        // 파일 추가 버튼 클릭
        $('#btnAddFiles').on('click', function() {
            $('#fileInput').click();
        });
        
        // 양식 선택 시 내용 로드
        $('#formId').on('change', function() {
            const formId = $(this).val();
            loadFormContent(formId);
        });
        
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
     * 결재자 선택 변경 처리
     */
    function handleApproverChange(index, approverNo, $select) {
        console.log(`결재자 선택 변경: index=${index}, approverNo=${approverNo}`);
        
        const $row = $select.closest('tr');
        
        if (!approverNo) {
            $row.find('input[readonly]').eq(1).val('');
            updateApproverData(index, 'approverNo', '');
            updateApproverData(index, 'approverPosition', '');
            updateApproverData(index, 'approverName', '');
            return;
        }
        
        // 선택된 결재자 정보 업데이트
        const $option = $select.find('option:selected');
        const approverName = $option.attr('data-name') || $option.text().split('(')[0].trim();
        const approverPosition = $option.attr('data-position') || '';
        const approverDept = $option.attr('data-dept') || '';
        
        console.log(`선택된 결재자 정보: 이름=${approverName}, 직위=${approverPosition}, 부서=${approverDept}`);
        
        // UI 업데이트 - 직위 필드 명시적 업데이트
        $row.find('input[readonly]').eq(1).val(approverPosition);
        
        // 데이터 업데이트
        updateApproverData(index, 'approverNo', approverNo);
        updateApproverData(index, 'approverName', approverName);
        updateApproverData(index, 'approverPosition', approverPosition);
    }

    //================================================================================
    // 결재선 관리 함수
    //================================================================================
    
    /**
     * 결재자 추가
     */
    function addApprover() {
        approvalLines.push({
            approvalNo: null,
            docId: documentData.docId,
            approverNo: '',
            approverName: '',
            approverPosition: '',
            approvalOrder: approvalLines.length + 1,
            approvalType: '결재',
            approvalStatus: '대기'
        });
        renderApprovalLines();
    }
    
    /**
     * 결재자 삭제
     */
    function removeApprover(index) {
        approvalLines.splice(index, 1);
        
        // 순서 재조정
        approvalLines.forEach((line, idx) => {
            line.approvalOrder = idx + 1;
        });
        
        renderApprovalLines();
    }
    
    /**
     * 결재자 데이터 업데이트
     */
    function updateApproverData(index, key, value) {
        if (approvalLines[index]) {
            approvalLines[index][key] = value;
        }
    }
    
    /**
     * 결재선 렌더링
     */
    function renderApprovalLines() {
        console.log('결재선 렌더링 시작:', approvalLines);
        const $tbody = $('#approvalLineTable tbody');
        $tbody.empty();
        
        if (approvalLines.length === 0) {
            $tbody.html(`
                <tr>
                    <td colspan="5" class="text-center text-muted py-3">
                        결재선을 추가해주세요
                    </td>
                </tr>
            `);
            return;
        }
        
        approvalLines.forEach((line, index) => {
            const row = `
                <tr data-index="${index}">
                    <td>
                        <input type="text" value="${line.approvalOrder}" class="form-control" readonly />
                    </td>
                    <td>
                        <select data-field="approverNo" class="form-select approver-select" required>
                            <option value="">결재자 선택</option>
                            <!-- 결재자 목록은 별도 함수로 채움 -->
                        </select>
                    </td>
                    <td>
                        <input type="text" value="${line.approverPosition || ''}" class="form-control" readonly />
                    </td>
                    <td>
                        <select data-field="approvalType" class="form-select">
                            <option value="결재" ${line.approvalType === '결재' ? 'selected' : ''}>결재</option>
                            <option value="합의" ${line.approvalType === '합의' ? 'selected' : ''}>합의</option>
                            <option value="참조" ${line.approvalType === '참조' ? 'selected' : ''}>참조</option>
                        </select>
                    </td>
                    <td class="text-center">
                        <button type="button" class="btn btn-danger btn-sm btn-remove-line">
                            <i class="bi bi-trash"></i>
                        </button>
                    </td>
                </tr>
            `;
            $tbody.append(row);
        });
        
		console.log('결재선 행 추가 완료, 결재자 목록 로드 시작');
		    }
		    
		    /**
		     * 결재자 목록 로드
		     */
		    async function loadApprovers() {
		        try {
		            console.log('결재자 목록 로드 시작');
		            
		            // 로딩 표시
		            const loading = AlertUtil.showLoading('결재자 목록 로드 중...');
		            
		            // API 호출
		            const response = await fetch('/api/eapproval/approvers');
		            const responseData = await response.json();
		            
		            // 로딩 종료
		            loading.close();
		            
		            if (!responseData.success || !responseData.data) {
		                console.warn('결재자 목록 조회 실패:', responseData.message);
		                throw new Error(responseData.message || '결재자 목록을 불러올 수 없습니다.');
		            }
		            
		            const approvers = responseData.data;
		            console.log('결재자 목록:', approvers);
		            
		            // 모든 결재자 선택 드롭다운 업데이트
		            $('.approver-select').each(function() {
		                const $select = $(this);
		                const $row = $select.closest('tr');
		                const index = $row.data('index');
		                const currentValue = approvalLines[index]?.approverNo || '';
		                
		                // 기본 옵션 유지
		                let optionsHtml = '<option value="">결재자 선택</option>';
		                
		                // 결재자 옵션 추가
		                approvers.forEach(approver => {
		                    const approverValue = approver.EMP_NO || approver.EMP_ID;
		                    const selected = approverValue == currentValue ? 'selected' : '';
		                    
		                    optionsHtml += `<option value="${approverValue}" 
		                                        data-position="${approver.DEPT_POSITION || ''}" 
		                                        data-name="${approver.EMP_NAME || ''}"
		                                        data-dept="${approver.DEPT_NAME || ''}"
		                                        ${selected}>
		                                    ${approver.EMP_NAME || ''} (${approver.DEPT_NAME || ''})
		                                    </option>`;
		                });
		                
		                // HTML 삽입
		                $select.html(optionsHtml);
		                
		                // 선택된 값이 있으면 직위 정보 업데이트
		                if (currentValue) {
		                    const $selectedOption = $select.find('option:selected');
		                    const position = $selectedOption.attr('data-position') || '';
		                    
		                    // 직위 입력 필드 업데이트
		                    $row.find('input[readonly]').eq(1).val(position);
		                }
		            });
		            
		            return true;
		        } catch (error) {
		            console.error('결재자 목록 로드 오류:', error);
		            await AlertUtil.showWarning('결재자 조회 실패', error.message || '결재자 목록을 불러올 수 없습니다.');
		            return false;
		        }
		    }

		    //================================================================================
		    // 첨부파일 관리 함수
		    //================================================================================
		    
		    /**
		     * 파일 추가
		     */
		    function addFiles(newFiles) {
		        if (!newFiles || newFiles.length === 0) return;
		        
		        // FileList 객체를 배열로 변환하여 추가
		        Array.from(newFiles).forEach(file => {
		            fileList.push(file);
		        });
		        
		        renderFileList();
		    }
		    
		    /**
		     * 파일 삭제
		     */
		    function removeFile(index) {
		        if (index >= 0 && index < fileList.length) {
		            fileList.splice(index, 1);
		            renderFileList();
		        }
		    }
		    
		    /**
		     * 파일 목록 렌더링
		     */
		    function renderFileList() {
		        const $fileList = $('#fileList');
		        
		        if (fileList.length === 0) {
		            $fileList.html('<div class="text-muted text-center">첨부된 파일이 없습니다</div>');
		            return;
		        }
		        
		        let fileListHtml = '';
		        fileList.forEach((file, index) => {
		            fileListHtml += `
		                <div class="d-flex align-items-center justify-content-between mb-2">
		                    <div>
		                        <i class="bi bi-file-earmark mr-2"></i>
		                        <span class="ms-2">${file.name} (${formatFileSize(file.size)})</span>
		                    </div>
		                    <button type="button" class="btn btn-sm btn-outline-danger btn-remove-file" data-index="${index}">
		                        <i class="bi bi-x"></i>
		                    </button>
		                </div>
		            `;
		        });
		        
		        $fileList.html(fileListHtml);
		        
		        // 삭제 버튼 이벤트 핸들러
		        $('.btn-remove-file').on('click', function() {
		            const index = $(this).data('index');
		            removeFile(index);
		        });
		    }
		    
		    /**
		     * 파일 크기 포맷팅
		     */
		    function formatFileSize(bytes) {
		        if (bytes === 0) return '0 Bytes';
		        const k = 1024;
		        const sizes = ['Bytes', 'KB', 'MB', 'GB'];
		        const i = Math.floor(Math.log(bytes) / Math.log(k));
		        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
		    }

		    //================================================================================
		    // 양식 및 문서 저장 함수
		    //================================================================================
		    
		    /**
		     * 양식 내용 로드
		     */
		    async function loadFormContent(formId) {
		        if (!formId) return;
		        
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
		            const formContent = formData.formContent || '';
		            
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
		     * 폼 유효성 검사
		     */
		    function validateForm() {
		        const formId = $('#formId').val();
		        const title = $('#title').val().trim();
		        const content = $('#contentEditor').summernote('code');
		        
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
		        
		        if (approvalLines.length === 0) {
		            AlertUtil.showWarning('유효성 검사', '결재선을 추가해주세요.');
		            return false;
		        }
		        
		        // 결재선 유효성 검사
		        for (let i = 0; i < approvalLines.length; i++) {
		            if (!approvalLines[i].approverNo) {
		                AlertUtil.showWarning('유효성 검사', `${i+1}번째 결재자를 선택해주세요.`);
		                return false;
		            }
		        }
		        
		        return true;
		    }
		    
		    /**
		     * 문서 저장 (임시저장 또는 결재요청)
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
		            formData.append('content', $('#contentEditor').summernote('code'));
		            formData.append('isTempSave', isTempSave);
		            formData.append('approvalLinesJson', JSON.stringify(approvalLines));
		            
		            // 첨부파일 추가
		            for (let i = 0; i < fileList.length; i++) {
		                formData.append('files', fileList[i]);
		            }
		            
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
		                        const docId = result.data?.docId;
		                        if (docId) {
		                            location.href = `/eapproval/document/view/${docId}`;
		                        } else {
		                            location.href = '/eapproval/documents';
		                        }
		                    }
		                }, 500);
		            } else {
		                throw new Error(result.message || '문서 저장 중 오류가 발생했습니다.');
		            }
		        } catch (error) {
		            console.error('문서 저장 오류:', error);
		            await AlertUtil.showError('저장 실패', error.message || '문서 저장 중 오류가 발생했습니다.');
		        }
		    }

		    // 공개 API
		    return {
		        initialize,
		        saveDocument,
		        validateForm
		    };
		})();

		// 페이지 로드 시 초기화
		$(async function() {
		    console.log('페이지 로드: DocumentForm 초기화 시작');
		    try {
		        await DocumentForm.initialize();
		        console.log('페이지 로드: DocumentForm 초기화 완료');
		    } catch (error) {
		        console.error('페이지 초기화 오류:', error);
		    }
		});