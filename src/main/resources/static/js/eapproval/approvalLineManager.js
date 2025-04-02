///**
// * approvalLineManager.js - 결재선 관리 모듈
// * 
// * 전자결재 결재선 관리를 위한 기능을 제공합니다.
// * - 결재선 추가/수정/삭제
// * - 결재선 유효성 검증
// * - 결재자 정보 조회 및 설정
// * 
// * @version 1.0.0
// */
//
//// 즉시 실행 함수로 모듈 스코프 생성
//const ApprovalLineManager = (function() {
//    // 결재선 데이터
//    let approvalLines = [];
//    
//    /**
//     * 모듈 초기화
//     * @param {Array} initialLines - 초기 결재선 데이터
//     */
//    function initialize(initialLines) {
//        console.log('ApprovalLineManager 초기화');
//        if (initialLines && Array.isArray(initialLines)) {
//            approvalLines = [...initialLines];
//        }
//        renderApprovalLines();
//        setupEventHandlers();
//    }
//    
//    /**
//     * 이벤트 핸들러 설정
//     */
//    function setupEventHandlers() {
//        // 결재자 추가 버튼
//        $('#btnAddApprover').on('click', addApprover);
//        
//        // 결재자 삭제 버튼 (동적 요소)
//        $(document).on('click', '.btn-remove-line', function() {
//            const index = $(this).closest('tr').data('index');
//            removeApprover(index);
//        });
//        
//        // 결재자 선택 변경 (동적 요소)
//        $(document).on('change', '.approver-select', function() {
//            const $row = $(this).closest('tr');
//            const index = $row.data('index');
//            const approverNo = $(this).val();
//            handleApproverChange(index, approverNo, $(this));
//        });
//        
//        // 결재 타입 변경 (동적 요소)
//        $(document).on('change', 'select[data-field="approvalType"]', function() {
//            const index = $(this).closest('tr').data('index');
//            const value = $(this).val();
//            updateApproverData(index, 'approvalType', value);
//        });
//    }
//    
//    /**
//     * 결재자 추가
//     */
//    function addApprover() {
//        approvalLines.push({
//            approvalNo: null,
//            docId: DocumentFormManager.getDocumentId() || '',
//            approverNo: '',
//            approverName: '',
//            approverPosition: '',
//            approvalOrder: approvalLines.length + 1,
//            approvalType: '결재',
//            approvalStatus: '대기'
//        });
//        renderApprovalLines();
//    }
//    
//    /**
//     * 결재자 삭제
//     * @param {number} index - 삭제할 결재선 인덱스
//     */
//    function removeApprover(index) {
//        approvalLines.splice(index, 1);
//        
//        // 순서 재조정
//        approvalLines.forEach((line, idx) => {
//            line.approvalOrder = idx + 1;
//        });
//        
//        renderApprovalLines();
//    }
//    
//    /**
//     * 결재자 데이터 업데이트
//     * @param {number} index - 업데이트할 결재선 인덱스
//     * @param {string} key - 업데이트할 속성 키
//     * @param {*} value - 업데이트할 값
//     */
//    function updateApproverData(index, key, value) {
//        if (approvalLines[index]) {
//            approvalLines[index][key] = value;
//        }
//    }
//    
//    /**
//     * 결재자 선택 변경 처리
//     * @param {number} index - 결재선 인덱스
//     * @param {string} approverNo - 결재자 번호
//     * @param {jQuery} $select - 선택 요소
//     */
//    function handleApproverChange(index, approverNo, $select) {
//        console.log(`결재자 선택 변경: index=${index}, approverNo=${approverNo}`);
//        
//        const $row = $select.closest('tr');
//        
//        if (!approverNo) {
//            $row.find('input[readonly]').eq(1).val('');
//            updateApproverData(index, 'approverNo', '');
//            updateApproverData(index, 'approverPosition', '');
//            updateApproverData(index, 'approverName', '');
//            return;
//        }
//        
//        // 선택된 결재자 정보 업데이트
//        const $option = $select.find('option:selected');
//        const approverName = $option.attr('data-name') || $option.text().split('(')[0].trim();
//        const approverPosition = $option.attr('data-position') || '';
//        const approverDept = $option.attr('data-dept') || '';
//        
//        console.log(`선택된 결재자 정보: 이름=${approverName}, 직위=${approverPosition}, 부서=${approverDept}`);
//        
//        // UI 업데이트 - 직위 필드 명시적 업데이트
//        $row.find('input[readonly]').eq(1).val(approverPosition);
//        
//        // 데이터 업데이트
//        updateApproverData(index, 'approverNo', approverNo);
//        updateApproverData(index, 'approverName', approverName);
//        updateApproverData(index, 'approverPosition', approverPosition);
//    }
//    
//    /**
//     * 결재선 렌더링
//     */
//    function renderApprovalLines() {
//        console.log('결재선 렌더링 시작:', approvalLines);
//        const $tbody = $('#approvalLineTable tbody');
//        $tbody.empty();
//        
//        if (approvalLines.length === 0) {
//            $tbody.html(`
//                <tr>
//                    <td colspan="5" class="text-center text-muted py-3">
//                        결재선을 추가해주세요
//                    </td>
//                </tr>
//            `);
//            return;
//        }
//        
//        approvalLines.forEach((line, index) => {
//            const row = `
//                <tr data-index="${index}">
//                    <td>
//                        <input type="text" value="${line.approvalOrder}" class="form-control" readonly />
//                    </td>
//                    <td>
//                        <select data-field="approverNo" class="form-select approver-select" required>
//                            <option value="">결재자 선택</option>
//                            <!-- 결재자 목록은 별도 함수로 채움 -->
//                        </select>
//                    </td>
//                    <td>
//                        <input type="text" value="${line.approverPosition || ''}" class="form-control" readonly />
//                    </td>
//                    <td>
//                        <select data-field="approvalType" class="form-select">
//                            <option value="결재" ${line.approvalType === '결재' ? 'selected' : ''}>결재</option>
//                            <option value="합의" ${line.approvalType === '합의' ? 'selected' : ''}>합의</option>
//                            <option value="참조" ${line.approvalType === '참조' ? 'selected' : ''}>참조</option>
//                        </select>
//                    </td>
//                    <td class="text-center">
//                        <button type="button" class="btn btn-danger btn-sm btn-remove-line">
//                            <i class="bi bi-trash"></i>
//                        </button>
//                    </td>
//                </tr>
//            `;
//            $tbody.append(row);
//        });
//        
//        // 결재자 목록 로드
//        loadApproversToDropdowns();
//    }
//    
//    /**
//     * 결재자 목록을 드롭다운에 로드
//     */
//    async function loadApproversToDropdowns() {
//        try {
//            // 이미 로드된 목록이 있으면 그대로 사용
//            const approvers = await loadApprovers();
//            if (!approvers) {
//                console.warn('결재자 목록 로드 실패');
//                return;
//            }
//            
//            // 모든 결재자 선택 드롭다운 업데이트
//            $('.approver-select').each(function() {
//                const $select = $(this);
//                const $row = $select.closest('tr');
//                const index = $row.data('index');
//                const currentValue = approvalLines[index]?.approverNo || '';
//                
//                // 기본 옵션 유지
//                let optionsHtml = '<option value="">결재자 선택</option>';
//                
//                // 결재자 옵션 추가
//                approvers.forEach(approver => {
//                    const approverValue = approver.EMP_NO || approver.EMP_ID;
//                    const selected = approverValue == currentValue ? 'selected' : '';
//                    
//                    optionsHtml += `<option value="${approverValue}" 
//                                        data-position="${approver.DEPT_POSITION || ''}" 
//                                        data-name="${approver.EMP_NAME || ''}"
//                                        data-dept="${approver.DEPT_NAME || ''}"
//                                        ${selected}>
//                                    ${approver.EMP_NAME || ''} (${approver.DEPT_NAME || ''})
//                                    </option>`;
//                });
//                
//                // HTML 삽입
//                $select.html(optionsHtml);
//                
//                // 선택된 값이 있으면 직위 정보 업데이트
//                if (currentValue) {
//                    const $selectedOption = $select.find('option:selected');
//                    const position = $selectedOption.attr('data-position') || '';
//                    
//                    // 직위 입력 필드 업데이트
//                    $row.find('input[readonly]').eq(1).val(position);
//                }
//            });
//        } catch (error) {
//            console.error('결재자 드롭다운 로드 오류:', error);
//            await AlertUtil.showWarning('결재자 목록 로드 실패', '결재자 정보를 불러올 수 없습니다.');
//        }
//    }
//    
//    /**
//     * 결재자 목록 로드
//     * @returns {Promise<Array|null>} 결재자 목록 또는 실패 시 null
//     */
//    async function loadApprovers() {
//        try {
//            console.log('결재자 목록 로드 시작');
//            
//            // 로딩 표시
//            const loading = AlertUtil.showLoading('결재자 목록 로드 중...');
//            
//            // API 호출
//            const response = await fetch('/api/eapproval/approvers');
//            const responseData = await response.json();
//            
//            // 로딩 종료
//            loading.close();
//            
//            if (!responseData.success || !responseData.data) {
//                console.warn('결재자 목록 조회 실패:', responseData.message);
//                throw new Error(responseData.message || '결재자 목록을 불러올 수 없습니다.');
//            }
//            
//            const approvers = responseData.data;
//            console.log('결재자 목록:', approvers);
//            
//            return approvers;
//        } catch (error) {
//            console.error('결재자 목록 로드 오류:', error);
//            await AlertUtil.showWarning('결재자 조회 실패', error.message || '결재자 목록을 불러올 수 없습니다.');
//            return null;
//        }
//    }
//    
//    /**
//     * 결재선 유효성 검사
//     * @returns {boolean} 유효성 검사 결과
//     */
//    function validateApprovalLines() {
//        if (approvalLines.length === 0) {
//            AlertUtil.showWarning('유효성 검사', '결재선을 추가해주세요.');
//            return false;
//        }
//        
//        // 결재선 유효성 검사
//        for (let i = 0; i < approvalLines.length; i++) {
//            if (!approvalLines[i].approverNo) {
//                AlertUtil.showWarning('유효성 검사', `${i+1}번째 결재자를 선택해주세요.`);
//                return false;
//            }
//        }
//        
//        return true;
//    }
//    
//    /**
//     * 현재 결재선 데이터 반환
//     * @returns {Array} 결재선 데이터 배열
//     */
//    function getApprovalLines() {
//        return approvalLines;
//    }
//    
//    // 공개 API
//    return {
//        initialize,
//        getApprovalLines,
//        validateApprovalLines,
//        renderApprovalLines,
//        addApprover,
//        removeApprover
//    };
//})();