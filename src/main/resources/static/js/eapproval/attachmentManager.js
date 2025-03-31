/**
 * documentFormManager.js - 기안서 작성 관리 모듈
 * 
 * 기안서 작성 페이지의 기능을 관리하는 모듈입니다.
 * 결재선 관리, 첨부파일 관리, 기안서 데이터 저장 등의 기능을 제공합니다.
 * 
 * @version 1.0.0
 * @since 2025-03-30
 */

const DocumentFormManager = (function() {
    //================================================================================
    // 모듈 내부 변수
    //================================================================================

    // 결재선 데이터 관리 객체
    const ApprovalLineManager = (function() {
        // 결재선 데이터 배열
        let approvalLines = [];
        
        // 초기화 함수
        function initialize(initialData) {
            approvalLines = initialData || [];
            render();
        }
        
        // 결재자 추가 함수
        function addApprover() {
            approvalLines.push({
                approvalNo: null,
                docId: $('#docId').val(),
                approverNo: '',
                approverName: '',
                approverPosition: '',
                approvalOrder: approvalLines.length + 1,
                approvalType: '결재',
                approvalStatus: '대기'
            });
            render();
        }
        
        // 결재자 삭제 함수
        function removeApprover(index) {
            approvalLines.splice(index, 1);
            
            // 순서 재조정
            approvalLines.forEach((line, idx) => {
                line.approvalOrder = idx + 1;
            });
            
            render();
        }
        
        // 결재자 정보 업데이트 함수
        function updateApprover(index, key, value, renderSkip = true) {
            if (approvalLines[index]) {
                approvalLines[index][key] = value;
                // renderSkip이 true일 때만 render 호출
                if (renderSkip) {
                    render();
                }
            }
        }
        
        // 결재선 렌더링 함수
        function render() {
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
            // 결재자 목록 로드
            loadApprovers();
        }
        
        // 결재선 데이터 가져오기
        function getApprovalLines() {
            return [...approvalLines];
        }
        
        // 공개 메서드
        return {
            initialize,
            addApprover,
            removeApprover,
            updateApprover,
            getApprovalLines
        };
    })();
    
    // 첨부 파일 관리 객체
    const AttachmentManager = (function() {
        const fileList = [];
        
        // 파일 추가
        function addFiles(newFiles) {
            if (!newFiles || newFiles.length === 0) return;
            
            // FileList 객체를 배열로 변환하여 추가
            Array.from(newFiles).forEach(file => {
                fileList.push(file);
            });
            
            render();
        }
        
        // 파일 삭제
        function removeFile(index) {
            if (index >= 0 && index < fileList.length) {
                fileList.splice(index, 1);
                render();
            }
        }
        
        // 파일 목록 렌더링
        function render() {
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
        
        // 파일 크기 포맷팅 함수
        function formatFileSize(bytes) {
            if (bytes === 0) return '0 Bytes';
            const k = 1024;
            const sizes = ['Bytes', 'KB', 'MB', 'GB'];
            const i = Math.floor(Math.log(bytes) / Math.log(k));
            return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
        }
        
        // 파일 목록 가져오기
        function getFiles() {
            return [...fileList];
        }
        
        // 공개 메서드
        return {
            addFiles,
            removeFile,
            getFiles
        };
    })();

    //================================================================================
    // 모듈 함수
    //================================================================================

    /**
     * 결재자 목록 로드 함수
     * API를 호출하여 결재자 목록을 가져와 결재선 셀렉트 박스에 표시합니다.
     */
    async function loadApprovers() {
        try {
            console.log('결재자 목록 로드 시작');
            const response = await ApiUtil.getWithLoading(
                '/api/eapproval/approvers', 
                null, 
                '결재자 목록 조회 중...'
            );
            
            console.log('API 응답:', response);
            
            if (response.success && response.data) {
                const approvers = response.data;
                console.log('결재자 목록:', approvers);
                
                // 문제 해결을 위한 대체 방식: 직접 HTML 생성 후 삽입
                $('.approver-select').each(function() {
                    const $select = $(this);
                    const $row = $select.closest('tr');
                    const index = $row.data('index');
                    const approvalLines = ApprovalLineManager.getApprovalLines();
                    const currentValue = approvalLines[index]?.approverNo || '';
                    
                    console.log(`처리 중인 셀렉트 박스 - 인덱스: ${index}, 현재 값: ${currentValue}`);
                    
                    // 기본 옵션 유지
                    let optionsHtml = '<option value="">결재자 선택</option>';
                    
                    // 결재자 옵션 추가 - DEPT_POSITION 사용 및 EMP_ID 백업 값 추가
                    approvers.forEach(approver => {
                        // EMP_NO가 null이면 EMP_ID를 대체 값으로 사용
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
                    
                    console.log(`셀렉트 박스 업데이트 완료. 옵션 개수: ${$select.find('option').length}`);
                    
                    // 선택된 값이 있으면 직위 정보 명시적으로 업데이트
                    if (currentValue) {
                        const $selectedOption = $select.find('option:selected');
                        const position = $selectedOption.attr('data-position') || '';
                        
                        // 직위 입력 필드 업데이트
                        $row.find('input[readonly]').eq(1).val(position);
                        console.log(`직위 정보 업데이트: ${position}`);
                        
                        // ApprovalLineManager 데이터도 업데이트
                        const approverName = $selectedOption.attr('data-name') || '';
                        ApprovalLineManager.updateApprover(index, 'approverPosition', position, false);
                        ApprovalLineManager.updateApprover(index, 'approverName', approverName, false);
                    }
                });
            } else {
                console.warn('결재자 목록 조회 실패:', response.message);
                await AlertUtil.showWarning('결재자 조회 실패', response.message || '결재자 목록을 불러올 수 없습니다.');
            }
        } catch (error) {
            console.error('결재자 목록 로드 오류:', error);
            await ApiUtil.handleApiError(error, '결재자 목록 조회 오류');
        }
    }

    /**
     * 폼 유효성 검사
     * 필수 입력 항목이 모두 입력되었는지 검사합니다.
     */
    function validateForm() {
        const formId = $('#formId').val();
        const title = $('#title').val().trim();
        const content = $('#contentEditor').summernote('code');
        const approvalLines = ApprovalLineManager.getApprovalLines();
        
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
     * @param {boolean} isTempSave - 임시저장 여부
     */
    async function saveDocument(isTempSave) {
        if (!validateForm()) return;
        
        try {
            // 폼 데이터 구성
            const formData = new FormData();
            const docId = $('#docId').val();
            const docNumber = $('#docNumber').val();
            const formId = $('#formId').val();
            const title = $('#title').val().trim();
            const content = $('#contentEditor').summernote('code');
            const approvalLines = ApprovalLineManager.getApprovalLines();
            
            console.log('저장할 문서 정보:', {
                docId,
                docNumber,
                formId,
                title,
                approvalLines,
                isTempSave
            });
            
            // 기본 문서 정보
            formData.append('docId', docId || '');
            formData.append('docNumber', docNumber || '');
            formData.append('formId', formId);
            formData.append('title', title);
            formData.append('content', content);
            formData.append('isTempSave', isTempSave);
            
            // 결재선 정보 (JSON 문자열로 변환)
            formData.append('approvalLinesJson', JSON.stringify(approvalLines));
            
            // 첨부파일 추가
            const files = AttachmentManager.getFiles();
            console.log(`첨부파일 ${files.length}개 추가`);
            for (let i = 0; i < files.length; i++) {
                formData.append('files', files[i]);
            }
            
            // FormData 내용 디버깅 로그
            console.log('FormData 키 목록:');
            for (const key of formData.keys()) {
                console.log(`- ${key}`);
            }
            
            // 저장 API 호출
            await ApiUtil.processRequest(
                () => {
                    return $.ajax({
                        url: '/api/eapproval/document',
                        type: 'POST',
                        data: formData,
                        processData: false,
                        contentType: false
                    });
                },
                {
                    loadingMessage: isTempSave ? '임시저장 중...' : '결재요청 중...',
                    successMessage: isTempSave ? '문서가 임시저장되었습니다.' : '결재요청이 완료되었습니다.',
                    errorMessage: '문서 저장 중 오류가 발생했습니다.',
                    successCallback: function(response) {
                        console.log('저장 성공 응답:', response);
                        if (response.success) {
                            // 저장 성공 시 페이지 이동
                            if (isTempSave) {
                                location.href = '/eapproval/approvalList';
                            } else {
                                location.href = '/eapproval/document/view/' + response.data.docId;
                            }
                        }
                    }
                }
            );
        } catch (error) {
            console.error('문서 저장 오류:', error);
        }
    }

    /**
     * 이벤트 등록 함수
     * 각종 버튼 및 이벤트 핸들러를 등록합니다.
     */
    function registerEvents() {
        // 결재자 추가 버튼 클릭
        $('#btnAddApprover').on('click', function() {
            ApprovalLineManager.addApprover();
        });
        
        // 결재자 삭제 버튼 클릭 (동적 요소에 대한 이벤트 등록)
        $(document).on('click', '.btn-remove-line', function() {
            const index = $(this).closest('tr').data('index');
            ApprovalLineManager.removeApprover(index);
        });
        
        // 결재자 선택 변경 (동적 요소에 대한 이벤트 등록)
        $(document).on('change', '.approver-select', function() {
            const $row = $(this).closest('tr');
            const index = $row.data('index');
            const approverNo = $(this).val();
            const $option = $(this).find('option:selected');
            
            console.log(`결재자 선택 변경: index=${index}, approverNo=${approverNo}`);
            
            if (!approverNo) {
                $row.find('input[readonly]').eq(1).val('');
                ApprovalLineManager.updateApprover(index, 'approverNo', '');
                ApprovalLineManager.updateApprover(index, 'approverPosition', '');
                ApprovalLineManager.updateApprover(index, 'approverName', '');
                return;
            }
            
            // 선택된 결재자 정보 업데이트
            const approverName = $option.attr('data-name') || $option.text().split('(')[0].trim();
            const approverPosition = $option.attr('data-position') || '';
            const approverDept = $option.attr('data-dept') || '';
            
            console.log(`선택된 결재자 정보: 이름=${approverName}, 직위=${approverPosition}, 부서=${approverDept}`);
            
            // UI 업데이트 - 직위 필드 명시적 업데이트
            $row.find('input[readonly]').eq(1).val(approverPosition);
            
            // 데이터 업데이트
            ApprovalLineManager.updateApprover(index, 'approverNo', approverNo);
            ApprovalLineManager.updateApprover(index, 'approverName', approverName);
            ApprovalLineManager.updateApprover(index, 'approverPosition', approverPosition);
        });
        
        // 결재 타입 변경
        $(document).on('change', 'select[data-field="approvalType"]', function() {
            const index = $(this).closest('tr').data('index');
            const value = $(this).val();
            ApprovalLineManager.updateApprover(index, 'approvalType', value);
        });
        
        // 파일 선택 시 추가
        $('#fileInput').on('change', function() {
            AttachmentManager.addFiles(this.files);
            $(this).val(''); // 파일 선택 초기화 (같은 파일 다시 선택 가능하도록)
        });
        
        // 파일 추가 버튼 클릭
        $('#btnAddFiles').on('click', function() {
            $('#fileInput').click();
        });
        
        // 양식 선택 시 내용 로드
        $('#formId').on('change', async function() {
            const formId = $(this).val();
            if (!formId) return;
            
            try {
                // 양식 내용 로드
                const response = await ApiUtil.getWithLoading(
                    '/api/eapproval/form/' + formId, 
                    null, 
                    '양식 로드 중...'
                );
                
                if (response.success && response.data) {
                    $('#contentEditor').summernote('code', response.data.formContent);
                } else {
                    await AlertUtil.showWarning('양식 로드 실패', response.message || '양식을 불러올 수 없습니다.');
                }
            } catch (error) {
                console.error('양식 로드 오류:', error);
                await ApiUtil.handleApiError(error, '양식 로드 오류');
            }
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
     * 초기화 함수
     * 모듈 및 에디터를 초기화합니다.
     */
    async function initialize() {
        console.log('DocumentFormManager 초기화 시작');
        
        // 에디터 초기화
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
            ]
        });
        
        // 이벤트 초기화
        registerEvents();
        
        try {
            // Thymeleaf에서 전달한 데이터 사용
            const approvalLinesStr = window.documentData?.approvalLines;
            console.log('Thymeleaf 결재선 데이터:', approvalLinesStr);
            
            // 결재선 초기화
            let approvalLines = [];
            
            if (approvalLinesStr && approvalLinesStr.length > 0) {
                try {
                    // JSON 파싱
                    approvalLines = Array.isArray(approvalLinesStr) ? 
                        approvalLinesStr : 
                        JSON.parse(approvalLinesStr);
                } catch (e) {
                    console.warn('결재선 데이터 파싱 실패:', e);
                    approvalLines = [];
                }
            }
            
            console.log('기존 결재선 데이터:', approvalLines);
            ApprovalLineManager.initialize(approvalLines);
            
            // 초기화 후 결재자 목록 로드
            console.log('결재자 목록 로드 시작');
            await loadApprovers();
        } catch (error) {
            console.error('초기화 중 오류:', error);
            // 오류 발생 시 빈 배열로 초기화
            ApprovalLineManager.initialize([]);
            
            // 결재자 목록은 여전히 로드
            await loadApprovers();
        }
        
        console.log('DocumentFormManager 초기화 완료');
    }

    // 공개 API
    return {
        initialize
    };
})();

// 페이지 로드 시 초기화
$(async function() {
    console.log('페이지 로드: 초기화 시작');
    try {
        await DocumentFormManager.initialize();
        console.log('페이지 로드: 초기화 완료');
    } catch (error) {
        console.error('페이지 초기화 오류:', error);
    }
});