/**
 * approvalLineManager.js - 결재선 관리 모듈
 * 
 * 결재선 추가, 삭제, 수정 등의 기능을 제공하는 독립 모듈입니다.
 * 결재 문서 작성 및 조회 화면에서 재사용 가능합니다.
 * 
 * @version 1.0.0
 * @since 2025-03-30
 */

const ApprovalLineManager = (function() {
    //================================================================================
    // 모듈 내부 변수
    //================================================================================

    // 결재선 데이터 배열
    let approvalLines = [];
    
    // 결재선 UI 요소 셀렉터
    let tableSelector = '#approvalLineTable';
    
    // 콜백 함수 저장 객체
    let callbacks = {
        onAfterAdd: null,
        onAfterRemove: null,
        onAfterUpdate: null,
        onAfterRender: null
    };

    //================================================================================
    // 내부 유틸리티 함수
    //================================================================================

    /**
     * 결재선 데이터의 유효성을 검사합니다.
     * @param {Array} lines - 검사할 결재선 데이터 배열
     * @returns {boolean} 유효성 검사 결과
     */
    function validateApprovalLines(lines) {
        if (!Array.isArray(lines)) {
            console.error('결재선 데이터가 배열 형식이 아닙니다.');
            return false;
        }
        
        // 여기에 추가 유효성 검사 로직을 구현할 수 있습니다.
        // 예: 필수 필드 확인, 타입 체크 등
        
        return true;
    }

    /**
     * 결재자 목록을 로드합니다.
     * @param {string} apiUrl - 결재자 목록 조회 API URL (기본값: '/api/eapproval/approvers')
     */
    async function loadApprovers(apiUrl = '/api/eapproval/approvers') {
        try {
            console.log('결재자 목록 로드 시작');
            const response = await ApiUtil.getWithLoading(
                apiUrl, 
                null, 
                '결재자 목록 조회 중...'
            );
            
            console.log('API 응답:', response);
            
            if (response.success && response.data) {
                const approvers = response.data;
                console.log('결재자 목록:', approvers);
                
                // 결재자 셀렉트 박스에 옵션 추가
                $(`${tableSelector} .approver-select`).each(function() {
                    const $select = $(this);
                    const $row = $select.closest('tr');
                    const index = $row.data('index');
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
                        updateApprover(index, 'approverPosition', position, false);
                        updateApprover(index, 'approverName', approverName, false);
                    }
                });
                
                // 콜백 호출
                if (callbacks.onAfterRender) {
                    callbacks.onAfterRender(approvalLines);
                }
                
                return true;
            } else {
                console.warn('결재자 목록 조회 실패:', response.message);
                await AlertUtil.showWarning('결재자 조회 실패', response.message || '결재자 목록을 불러올 수 없습니다.');
                return false;
            }
        } catch (error) {
            console.error('결재자 목록 로드 오류:', error);
            await ApiUtil.handleApiError(error, '결재자 목록 조회 오류');
            return false;
        }
    }

    //================================================================================
    // 결재선 관리 기능 함수
    //================================================================================

    /**
     * 결재선 관리 모듈을 초기화합니다.
     * @param {Object} options - 초기화 옵션
     * @param {Array} options.initialData - 초기 결재선 데이터
     * @param {string} options.tableSelector - 결재선 테이블 셀렉터
     * @param {Object} options.callbacks - 콜백 함수 객체
     */
    function initialize(options = {}) {
        console.log('결재선 관리 모듈 초기화:', options);
        
        // 옵션에서 초기 데이터 가져오기
        if (options.initialData) {
            approvalLines = Array.isArray(options.initialData) ? 
                [...options.initialData] : [];
        }
        
        // 테이블 셀렉터 설정
        if (options.tableSelector) {
            tableSelector = options.tableSelector;
        }
        
        // 콜백 함수 설정
        if (options.callbacks) {
            callbacks = { ...callbacks, ...options.callbacks };
        }
        
        // 결재선 렌더링
        render();
        
        // 이벤트 리스너 등록
        registerEventListeners();
        
        console.log('결재선 관리 모듈 초기화 완료');
    }
    
    /**
     * 이벤트 리스너를 등록합니다.
     */
    function registerEventListeners() {
        // 결재자 선택 변경 (동적 요소에 대한 이벤트 등록)
        $(document).off('change', `${tableSelector} .approver-select`).on('change', `${tableSelector} .approver-select`, function() {
            const $row = $(this).closest('tr');
            const index = $row.data('index');
            const approverNo = $(this).val();
            const $option = $(this).find('option:selected');
            
            console.log(`결재자 선택 변경: index=${index}, approverNo=${approverNo}`);
            
            if (!approverNo) {
                $row.find('input[readonly]').eq(1).val('');
                updateApprover(index, 'approverNo', '');
                updateApprover(index, 'approverPosition', '');
                updateApprover(index, 'approverName', '');
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
            updateApprover(index, 'approverNo', approverNo);
            updateApprover(index, 'approverName', approverName);
            updateApprover(index, 'approverPosition', approverPosition);
        });
        
        // 결재 타입 변경
        $(document).off('change', `${tableSelector} select[data-field="approvalType"]`).on('change', `${tableSelector} select[data-field="approvalType"]`, function() {
            const index = $(this).closest('tr').data('index');
            const value = $(this).val();
            updateApprover(index, 'approvalType', value);
        });
        
        // 삭제 버튼 클릭 이벤트
        $(document).off('click', `${tableSelector} .btn-remove-line`).on('click', `${tableSelector} .btn-remove-line`, function() {
            const index = $(this).closest('tr').data('index');
            removeApprover(index);
        });
    }
    
    /**
     * 결재자를 추가합니다.
     * @param {Object} approverData - 추가할 결재자 데이터 (선택사항)
     * @returns {number} 추가된 결재자의 인덱스
     */
    function addApprover(approverData = {}) {
        const newApprover = {
            approvalNo: null,
            docId: approverData.docId || $('#docId').val() || '',
            approverNo: approverData.approverNo || '',
            approverName: approverData.approverName || '',
            approverPosition: approverData.approverPosition || '',
            approvalOrder: approvalLines.length + 1,
            approvalType: approverData.approvalType || '결재',
            approvalStatus: approverData.approvalStatus || '대기'
        };
        
        approvalLines.push(newApprover);
        console.log('결재자 추가:', newApprover);
        
        render();
        
        // 콜백 호출
        if (callbacks.onAfterAdd) {
            callbacks.onAfterAdd(newApprover, approvalLines.length - 1);
        }
        
        return approvalLines.length - 1;
    }
    
    /**
     * 결재자를 삭제합니다.
     * @param {number} index - 삭제할 결재자의 인덱스
     * @returns {boolean} 삭제 성공 여부
     */
    function removeApprover(index) {
        if (index < 0 || index >= approvalLines.length) {
            console.error(`유효하지 않은 인덱스: ${index}`);
            return false;
        }
        
        const removedApprover = approvalLines[index];
        approvalLines.splice(index, 1);
        
        // 순서 재조정
        approvalLines.forEach((line, idx) => {
            line.approvalOrder = idx + 1;
        });
        
        console.log('결재자 삭제:', removedApprover);
        
        render();
        
        // 콜백 호출
        if (callbacks.onAfterRemove) {
            callbacks.onAfterRemove(removedApprover, index);
        }
        
        return true;
    }
    
    /**
     * 결재자 정보를 업데이트합니다.
     * @param {number} index - 업데이트할 결재자의 인덱스
     * @param {string} key - 업데이트할 필드명
     * @param {*} value - 새 값
     * @param {boolean} renderSkip - 렌더링 스킵 여부
     * @returns {boolean} 업데이트 성공 여부
     */
    function updateApprover(index, key, value, renderSkip = true) {
        if (index < 0 || index >= approvalLines.length) {
            console.error(`유효하지 않은 인덱스: ${index}`);
            return false;
        }
        
        const oldValue = approvalLines[index][key];
        approvalLines[index][key] = value;
        
        console.log(`결재자 업데이트: index=${index}, key=${key}, value=${value}`);
        
        // 콜백 호출
        if (callbacks.onAfterUpdate) {
            callbacks.onAfterUpdate(approvalLines[index], index, key, oldValue, value);
        }
        
        // renderSkip이 true일 때만 render 호출
        if (!renderSkip) {
            render();
        }
        
        return true;
    }
    
    /**
     * 결재선을 렌더링합니다.
     */
    function render() {
        console.log('결재선 렌더링 시작:', approvalLines);
        const $tbody = $(`${tableSelector} tbody`);
        
        if (!$tbody.length) {
            console.error(`결재선 테이블을 찾을 수 없습니다: ${tableSelector}`);
            return false;
        }
        
        $tbody.empty();
        
        if (approvalLines.length === 0) {
            $tbody.html(`
                <tr>
                    <td colspan="5" class="text-center text-muted py-3">
                        결재선을 추가해주세요
                    </td>
                </tr>
            `);
            return true;
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
        
        return true;
    }
    
    /**
     * 결재선 데이터를 가져옵니다.
     * @returns {Array} 결재선 데이터 배열
     */
    function getApprovalLines() {
        return [...approvalLines];
    }
    
    /**
     * 결재선 데이터를 설정합니다.
     * @param {Array} lines - 새 결재선 데이터
     * @returns {boolean} 설정 성공 여부
     */
    function setApprovalLines(lines) {
        if (!validateApprovalLines(lines)) {
            return false;
        }
        
        approvalLines = [...lines];
        render();
        
        return true;
    }
    
    /**
     * 결재선의 유효성을 검사합니다.
     * @returns {boolean} 유효성 검사 결과
     */
    function validateLineData() {
        if (approvalLines.length === 0) {
            return false;
        }
        
        for (let i = 0; i < approvalLines.length; i++) {
            if (!approvalLines[i].approverNo) {
                return false;
            }
        }
        
        return true;
    }

    //================================================================================
    // 공개 API
    //================================================================================
    return {
        initialize,
        addApprover,
        removeApprover,
        updateApprover,
        render,
        getApprovalLines,
        setApprovalLines,
        validateLineData
    };
})();