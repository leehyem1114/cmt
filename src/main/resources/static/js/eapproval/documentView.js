/**
 * documentView.js - 문서 상세 보기 및 결재 처리
 * 
 * 결재 문서 상세 정보 표시 및 결재/반려 처리 기능을 제공합니다.
 * 
 * @version 1.0.0
 * @since 2025-04-01
 */

// 전역 변수 선언
let docId, isCurrentApprover;
let approvalModal;

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    console.log('문서 상세 페이지 초기화 시작');
    
    try {
        // 초기 데이터 로드
        initData();
        
        // 이벤트 리스너 등록
        registerEvents();
        
        // 모달 초기화
        initModal();
        
        console.log('문서 상세 페이지 초기화 완료');
    } catch (error) {
        console.error('초기화 중 오류 발생:', error);
        AlertUtil.showError('초기화 오류', '페이지 초기화 중 오류가 발생했습니다.');
    }
});

/**
 * 초기 데이터 로드 함수
 */
function initData() {
    // Thymeleaf에서 전달한 데이터 사용
    if (window.documentData) {
        docId = window.documentData.docId;
        isCurrentApprover = window.documentData.isCurrentApprover;
        console.log('문서 정보:', { docId, isCurrentApprover });
    } else {
        // 직접 DOM에서 데이터 추출
        docId = document.getElementById('docId').value;
        isCurrentApprover = document.getElementById('isCurrentApprover').value === 'true';
        console.log('DOM에서 추출한 문서 정보:', { docId, isCurrentApprover });
    }
    
    if (!docId) {
        throw new Error('문서 ID를 찾을 수 없습니다.');
    }
}

/**
 * 이벤트 리스너 등록 함수
 */
function registerEvents() {
    // 결재 버튼 클릭 이벤트
    const approveBtn = document.getElementById('approveBtn');
    if (approveBtn) {
        approveBtn.addEventListener('click', function() {
            openApprovalModal('승인');
        });
    }
    
    // 반려 버튼 클릭 이벤트
    const rejectBtn = document.getElementById('rejectBtn');
    if (rejectBtn) {
        rejectBtn.addEventListener('click', function() {
            openApprovalModal('반려');
        });
    }
}

/**
 * 모달 초기화 함수
 */
function initModal() {
    // 부트스트랩 모달 초기화
    const modalElement = document.getElementById('approvalModal');
    if (modalElement) {
        approvalModal = new bootstrap.Modal(modalElement);
        
        // 모달 확인 버튼 이벤트
        const confirmBtn = document.getElementById('confirmBtn');
        if (confirmBtn) {
            confirmBtn.addEventListener('click', function() {
                processApproval();
            });
        }
    }
}

/**
 * 결재 모달 열기 함수
 * 
 * @param {string} decision - 결재 결정 (승인/반려)
 */
function openApprovalModal(decision) {
    if (!approvalModal) return;
    
    // 결재 타입 설정
    document.getElementById('decision').value = decision;
    
    // 모달 제목 설정
    const modalTitle = document.getElementById('approvalModalLabel');
    if (modalTitle) {
        modalTitle.textContent = decision === '승인' ? '결재 승인' : '결재 반려';
    }
    
    // 의견 초기화
    document.getElementById('comment').value = '';
    
    // 모달 열기
    approvalModal.show();
}

/**
 * 결재 처리 함수
 */
async function processApproval() {
    try {
        // 폼 데이터 수집
        const decision = document.getElementById('decision').value;
        const comment = document.getElementById('comment').value;
        
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
        
        // API 호출 - 일반 AJAX 사용
        const result = await ApiUtil.processRequest(
            async () => {
                return await $.ajax({
                    url: `/api/eapproval/document/${docId}/process`,
                    type: 'POST',
                    data: {
                        decision: decision,
                        comment: comment
                    }
                });
            },
            {
                loadingMessage: '결재 처리 중...',
                successMessage: decision === '승인' ? '결재가 승인되었습니다.' : '결재가 반려되었습니다.',
                errorMessage: '결재 처리 중 오류가 발생했습니다.',
                successCallback: function() {
                    // 모달 닫기
                    approvalModal.hide();
                    
                    // 페이지 새로고침 또는 목록 페이지 이동
                    setTimeout(function() {
                        location.href = '/eapproval/documents';
                    }, 1000);
                }
            }
        );
        
        return result;
    } catch (error) {
        console.error('결재 처리 중 오류:', error);
        await AlertUtil.showError('결재 처리 오류', '결재 처리 중 오류가 발생했습니다.');
        return false;
    }
}