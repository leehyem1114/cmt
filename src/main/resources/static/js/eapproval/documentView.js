/**
 * DocumentView - 문서 상세 보기 및 결재 처리 모듈
 * 
 * 결재 문서 상세 정보 표시 및 결재/반려 처리 기능을 제공합니다.
 * - 문서 상세 정보 표시
 * - 결재/반려 처리
 * - 결재 의견 관리
 * 
 * @version 1.1.0
 * @since 2025-04-04
 * @update 2025-04-04 - SimpleGridManager 템플릿 스타일로 코드 리팩토링
 */
const DocumentView = (function() {
    //===========================================================================
    // 모듈 내부 변수 - 필요에 맞게 수정하세요
    //===========================================================================
    
    /**
     * 문서 관련 정보
     */
    let docId;               // 현재 문서 ID
    let isCurrentApprover;   // 현재 사용자가 결재자인지 여부
    let approverNo;          // 결재자 번호
    
    /**
     * UI 관련 요소
     */
    let approvalModal;       // 결재 모달 참조
    
    /**
     * API URL 상수 정의
     * 문서 관련 API 엔드포인트 정의
     */
    const API_URLS = {
        PROCESS: (docId) => `/api/eapproval/document/${docId}/process` // 결재 처리 API
    };
    
    //===========================================================================
    // 초기화 및 이벤트 처리 함수
    //===========================================================================
    
    /**
     * 모듈 초기화 함수
     * 문서 상세 보기 페이지를 초기화합니다.
     * 
     * @returns {Promise<void>}
     */
    async function initialize() {
        try {
            console.log('DocumentView 초기화를 시작합니다.');
            
            // 초기 데이터 로드
            await initData();
            
            // 이벤트 리스너 등록
            await registerEvents();
            
            // 모달 초기화
            await initModal();
            
            console.log('DocumentView 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('초기화 중 오류 발생:', error);
            await AlertUtil.showError('초기화 오류', '페이지 초기화 중 오류가 발생했습니다.');
        }
    }
    
    /**
     * 초기 데이터 로드 함수
     * 서버에서 전달된 데이터 또는 DOM에서 초기 데이터를 로드합니다.
     * 
     * @returns {Promise<void>}
     */
    async function initData() {
        try {
            console.log('문서 데이터 초기화를 시작합니다.');
            
            // Thymeleaf에서 전달된 데이터 확인 (window.documentData 전역변수)
            if (window.documentData) {
                docId = window.documentData.DOC_ID;
                isCurrentApprover = window.documentData.IS_CURRENT_APPROVER === true;
                console.log('Thymeleaf에서 문서 정보 로드:', { docId, isCurrentApprover });
            } else {
                // DOM에서 직접 데이터 추출
                const docIdElement = document.getElementById('docId');
                docId = docIdElement ? docIdElement.value : null;
                
                const isCurrentApproverElement = document.getElementById('isCurrentApprover');
                isCurrentApprover = isCurrentApproverElement ? isCurrentApproverElement.value === 'true' : false;
                
                console.log('DOM에서 문서 정보 로드:', { docId, isCurrentApprover });
            }
            
            // 문서 ID 검증
            if (!docId) {
                throw new Error('문서 ID를 찾을 수 없습니다.');
            }
            
            // 현재 결재자 번호 가져오기
            approverNo = await getCurrentApproverNo();
            if (!approverNo && isCurrentApprover) {
                console.warn('현재 사용자의 결재자 번호를 찾을 수 없습니다. 결재 처리가 불가능할 수 있습니다.');
            }
            
            console.log('문서 데이터 초기화 완료');
        } catch (error) {
            console.error('데이터 초기화 중 오류:', error);
            throw error;
        }
    }
    
    /**
     * 이벤트 리스너 등록 함수
     * 버튼 클릭 및 기타 이벤트 리스너를 등록합니다.
     * 
     * @returns {Promise<void>}
     */
    async function registerEvents() {
        try {
            console.log('이벤트 리스너 등록을 시작합니다.');
            
            // 결재 버튼 클릭 이벤트
            const approveBtn = document.getElementById('approveBtn');
            if (approveBtn) {
                approveBtn.addEventListener('click', function() {
                    openApprovalModal('승인');
                });
                console.log('결재 버튼 이벤트 등록 완료');
            }
            
            // 반려 버튼 클릭 이벤트
            const rejectBtn = document.getElementById('rejectBtn');
            if (rejectBtn) {
                rejectBtn.addEventListener('click', function() {
                    openApprovalModal('반려');
                });
                console.log('반려 버튼 이벤트 등록 완료');
            }
            
            // 목록 버튼 클릭 이벤트 (뒤로가기)
            const backBtn = document.querySelector('button[onclick="history.back()"]');
            if (backBtn) {
                // onclick 속성 제거 후 이벤트 리스너로 대체
                backBtn.removeAttribute('onclick');
                backBtn.addEventListener('click', function(e) {
                    e.preventDefault();
                    goToDocumentList();
                });
                console.log('목록 버튼 이벤트 등록 완료');
            }
            
            console.log('이벤트 리스너 등록이 완료되었습니다.');
        } catch (error) {
            console.error('이벤트 리스너 등록 중 오류:', error);
            throw error;
        }
    }
    
    /**
     * 모달 초기화 함수
     * 결재 모달을 초기화하고 이벤트 핸들러를 등록합니다.
     * 
     * @returns {Promise<void>}
     */
    async function initModal() {
        try {
            console.log('모달 초기화를 시작합니다.');
            
            // 모달 요소 확인
            const modalElement = document.getElementById('approvalModal');
            if (!modalElement) {
                console.warn('approvalModal 요소를 찾을 수 없습니다.');
                return;
            }
            
            // Bootstrap 모달 초기화
            approvalModal = new bootstrap.Modal(modalElement);
            
            // 모달 확인 버튼 이벤트
            const confirmBtn = document.getElementById('confirmBtn');
            if (confirmBtn) {
                confirmBtn.addEventListener('click', function() {
                    processApproval();
                });
                console.log('모달 확인 버튼 이벤트 등록 완료');
            }
            
            console.log('모달 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('모달 초기화 중 오류:', error);
            throw error;
        }
    }
    
    //===========================================================================
    // 결재 처리 함수
    //===========================================================================
    
    /**
     * 결재 모달 열기 함수
     * 결재 유형(승인/반려)에 따라 모달을 구성하여 표시합니다.
     * 
     * @param {string} decision - 결재 결정 (승인/반려)
     */
    function openApprovalModal(decision) {
        try {
            if (!approvalModal) {
                console.warn('approvalModal이 초기화되지 않았습니다.');
                return;
            }
            
            console.log(`결재 모달 열기: ${decision}`);
            
            // 결재 타입 설정
            const decisionField = document.getElementById('decision');
            if (decisionField) {
                decisionField.value = decision;
            }
            
            // 모달 제목 설정
            const modalTitle = document.getElementById('approvalModalLabel');
            if (modalTitle) {
                modalTitle.textContent = decision === '승인' ? '결재 승인' : '결재 반려';
            }
            
            // 의견 초기화
            const commentField = document.getElementById('comment');
            if (commentField) {
                commentField.value = '';
            }
            
            // 반려 시 의견 필수 안내 표시
            const commentHelp = document.getElementById('commentHelp');
            if (commentHelp) {
                commentHelp.style.display = decision === '반려' ? 'block' : 'none';
            }
            
            // 모달 열기
            approvalModal.show();
        } catch (error) {
            console.error('결재 모달 열기 중 오류:', error);
            AlertUtil.showError('모달 오류', '결재 모달을 열 수 없습니다.');
        }
    }
    
    /**
     * 결재 처리 함수
     * 결재 또는 반려 처리를 수행합니다.
     * 
     * @returns {Promise<void>}
     */
    async function processApproval() {
        try {
            console.log('결재 처리 시작');
            
            // 폼 데이터 수집
            const decision = document.getElementById('decision').value;
            const comment = document.getElementById('comment').value;
            
            // 결재 결정 검증
            if (!decision) {
                await AlertUtil.showWarning('필수 항목 누락', '결재 결정(승인/반려)이 필요합니다.');
                return;
            }
            
            // 반려 시 의견 필수
            if (decision === '반려' && !comment.trim()) {
                await AlertUtil.showWarning('반려 의견 누락', '반려 시에는 의견을 입력해주세요.');
                document.getElementById('comment').focus();
                return;
            }
            
            // 결재자 번호 확인
            if (!approverNo) {
                await AlertUtil.showError('결재자 정보 오류', '결재자 정보를 찾을 수 없습니다.');
                return;
            }
            
            // 로딩 표시
            const loading = AlertUtil.showLoading('결재 처리 중...');
            
            try {
                // 요청 데이터 구성
                const formData = new FormData();
                formData.append('decision', decision);
                formData.append('comment', comment);
                formData.append('approverId', approverNo);
                
                // API 호출 - ApiUtil 사용
                const response = await ApiUtil.post(API_URLS.PROCESS(docId), formData);
                
                // 로딩 종료
                loading.close();
                
                // 응답 확인
                if (!response.success) {
                    throw new Error(response.message || '결재 처리 중 오류가 발생했습니다.');
                }
                
                // 성공 알림
                await AlertUtil.showSuccess(
                    '결재 처리 완료', 
                    decision === '승인' ? '결재가 승인되었습니다.' : '결재가 반려되었습니다.'
                );
                
                // 모달 닫기
                if (approvalModal) {
                    approvalModal.hide();
                }
                
                // 페이지 이동
                setTimeout(function() {
                    goToDocumentList();
                }, 500);
            } catch (apiError) {
                // 로딩 종료
                loading.close();
                
                console.error('결재 처리 API 호출 중 오류:', apiError);
                await AlertUtil.showError('결재 처리 실패', apiError.message || '결재 처리 중 오류가 발생했습니다.');
            }
        } catch (error) {
            console.error('결재 처리 중 오류:', error);
            await AlertUtil.showError('결재 처리 오류', error.message || '결재 처리 중 오류가 발생했습니다.');
        }
    }
    
    //===========================================================================
    // 유틸리티 함수
    //===========================================================================
    
    /**
     * 문서 목록 페이지로 이동하는 함수
     */
    function goToDocumentList() {
        window.location.href = '/eapproval/documents';
    }
    
    /**
     * 현재 결재자 번호 가져오기 함수
     * 결재선 테이블에서 현재 사용자의 결재자 정보를 찾습니다.
     * 
     * @returns {Promise<string|null>} 결재자 번호 또는 null
     */
    async function getCurrentApproverNo() {
        try {
            console.log('현재 결재자 번호 확인 시작');
            
            // hidden input에 이미 저장된 approverNo 확인
            const existingApproverNo = document.getElementById('approverNo');
            if (existingApproverNo && existingApproverNo.value) {
                console.log(`기존 결재자 번호 사용: ${existingApproverNo.value}`);
                return existingApproverNo.value;
            }
            
            // 결재선 테이블에서 현재 사용자의 결재자 정보 찾기
            const approvalTable = document.querySelector('table.table-bordered:not(.text-center)');
            if (!approvalTable) {
                console.warn('결재선 테이블을 찾을 수 없습니다.');
                return null;
            }
            
            // 결재 상태가 '대기'인 행 찾기
            const rows = approvalTable.querySelectorAll('tbody tr');
            for (let i = 0; i < rows.length; i++) {
                const statusCell = rows[i].querySelector('td:nth-child(5) span');
                if (statusCell && statusCell.textContent.trim() === '대기') {
                    // 결재자 번호 생성 (인덱스 + 1)
                    const approverNo = (i + 1).toString();
                    
                    // hidden input으로 저장
                    const approverNoInput = document.createElement('input');
                    approverNoInput.type = 'hidden';
                    approverNoInput.id = 'approverNo';
                    approverNoInput.value = approverNo;
                    document.body.appendChild(approverNoInput);
                    
                    console.log(`새 결재자 번호 생성: ${approverNo}`);
                    return approverNo;
                }
            }
            
            console.warn('대기 상태인 결재자를 찾을 수 없습니다.');
            return null;
        } catch (error) {
            console.error('결재자 번호 확인 중 오류:', error);
            return null;
        }
    }
    
    //===========================================================================
    // 공개 API - 외부에서 접근 가능한 메서드
    //===========================================================================
    
    return {
        // 초기화 및 기본 기능
        initialize,         // 모듈 초기화
        
        // 결재 처리 함수
        openApprovalModal,  // 결재 모달 열기
        processApproval,    // 결재 처리 실행
        
        // 유틸리티 함수
        goToDocumentList    // 문서 목록으로 이동
    };
})();

// DOM 로드 시 초기화
document.addEventListener('DOMContentLoaded', async function() {
    try {
        // 모듈 초기화
        await DocumentView.initialize();
    } catch (error) {
        console.error('초기화 중 오류 발생:', error);
        if (window.AlertUtil) {
            await AlertUtil.showError('초기화 오류', '페이지 초기화 중 오류가 발생했습니다.');
        } else {
            alert('페이지 초기화 중 오류가 발생했습니다.');
        }
    }
});