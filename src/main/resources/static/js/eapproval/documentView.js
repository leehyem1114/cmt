/**
 * DocumentView - 문서 상세 보기 및 결재 처리 모듈
 * 
 * 결재 문서 상세 정보 표시 및 결재/반려 처리 기능을 제공합니다.
 * - 문서 상세 정보 표시
 * - 결재/반려 처리
 * - 결재 의견 관리
 * 
 * @version 1.3.0
 * @since 2025-04-03
 * @update 2025-04-05 - 결재자 ID 처리 관련 코드 개선 및 다양한 선택자 지원
 */
const DocumentView = (function() {
    //===========================================================================
    // 모듈 내부 변수 - 필요에 맞게 수정하세요
    //===========================================================================
    
    /**
     * 문서 관련 정보
     */
    let documentData = {
        docId: null,           // 현재 문서 ID
        isCurrentApprover: false,   // 현재 사용자가 결재자인지 여부
        approverId: null          // 결재자 ID
    };
    
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
            
            // 디버그용: window 객체의 documentData 내용 출력
            console.log('window.documentData의 실제 내용:', window.documentData);
            
            // 방법 1: Thymeleaf에서 전달된 데이터 확인
            if (window.documentData) {
                console.log('window.documentData 존재함:', window.documentData);
                
                if (window.documentData.docId) {
                    documentData.docId = window.documentData.docId;
                    console.log('Thymeleaf에서 문서 ID 로드:', documentData.docId);
                }
                
                documentData.isCurrentApprover = window.documentData.isCurrentApprover === true;
                console.log('Thymeleaf에서 결재자 여부 로드:', documentData.isCurrentApprover);
                
                // 결재자 ID 로드
                if (window.documentData.approverId) {
                    documentData.approverId = window.documentData.approverId;
                    console.log('Thymeleaf에서 결재자 ID 로드:', documentData.approverId);
                }
            }
            
            // 방법 2: DOM에서 직접 데이터 추출
            if (!documentData.docId) {
                const docIdElement = document.getElementById('docId');
                if (docIdElement) {
                    documentData.docId = docIdElement.value;
                    console.log('DOM에서 문서 ID 로드:', documentData.docId);
                }
            }
            
            if (documentData.isCurrentApprover === undefined) {
                const isCurrentApproverElement = document.getElementById('isCurrentApprover');
                if (isCurrentApproverElement) {
                    documentData.isCurrentApprover = isCurrentApproverElement.value === 'true';
                    console.log('DOM에서 결재자 여부 로드:', documentData.isCurrentApprover);
                }
            }
            
            // 결재자 ID 확인 - hidden input에서 확인
            if (!documentData.approverId) {
                const approverIdElement = document.getElementById('approverId');
                if (approverIdElement && approverIdElement.value) {
                    documentData.approverId = approverIdElement.value;
                    console.log('DOM에서 결재자 ID 로드:', documentData.approverId);
                }
            }
            
            // 방법 3: URL에서 문서 ID 추출 (fallback)
            if (!documentData.docId) {
                const urlParts = window.location.pathname.split('/');
                const potentialDocId = urlParts[urlParts.length - 1];
                if (potentialDocId && potentialDocId.length > 10) {
                    documentData.docId = potentialDocId;
                    console.log('URL에서 문서 ID 로드:', documentData.docId);
                }
            }
            
            // 문서 ID 검증
            if (!documentData.docId) {
                console.error('어떤 방법으로도 문서 ID를 찾을 수 없습니다');
                throw new Error('문서 ID를 찾을 수 없습니다.');
            }
            
            // 결재자 ID가 없는 경우, 테이블에서 찾기 시도
            if (!documentData.approverId && documentData.isCurrentApprover) {
                documentData.approverId = await findApproverIdFromTable();
                console.log('테이블에서 찾은 결재자 ID:', documentData.approverId);
            }
            
            console.log('문서 데이터 초기화 완료:', documentData);
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
            
            // 디버그 로깅
            console.log('결재 처리 파라미터:', {
                decision: decision,
                comment: comment,
                approverId: documentData.approverId,
                docId: documentData.docId
            });
            
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
            
            // 결재자 ID 확인
            if (!documentData.approverId) {
                // 다시 한번 찾기 시도
                console.log('결재자 ID가 없어 다시 찾기 시도');
                documentData.approverId = await findApproverIdFromTable();
                
                if (!documentData.approverId) {
                    console.error('결재자 ID를 찾지 못함');
                    
                    // 최후의 수단: 현재 로그인한 사용자 ID 사용 (관리자에게 문의 안내)
                    const confirmed = await AlertUtil.showConfirm({
                        title: '결재자 정보 오류',
                        text: '결재자 정보를 찾을 수 없습니다. 임시 ID를 사용하여 계속하시겠습니까?\n(오류가 계속되면 관리자에게 문의하세요)',
                        confirmButtonText: '계속',
                        cancelButtonText: '취소'
                    });
                    
                    if (!confirmed) {
                        return;
                    }
                    
                    // 임시 ID 생성
                    documentData.approverId = 'emergency-' + Date.now();
                    console.log(`임시 결재자 ID 생성: ${documentData.approverId}`);
                }
            }
            
            console.log('최종 사용할 결재자 ID:', documentData.approverId);
            
            // 로딩 표시
            const loading = AlertUtil.showLoading('결재 처리 중...');
            
            try {
                // 요청 데이터 구성
                const formData = new FormData();
                formData.append('decision', decision);
                formData.append('comment', comment);
                formData.append('approverId', documentData.approverId);
                
                console.log('API 호출:', API_URLS.PROCESS(documentData.docId));
                console.log('전송할 데이터:', {
                    decision: formData.get('decision'),
                    comment: formData.get('comment'),
                    approverId: formData.get('approverId')
                });
                
                // API 호출 - ApiUtil 사용
                const response = await ApiUtil.post(API_URLS.PROCESS(documentData.docId), formData);
                
                // 로딩 종료
                loading.close();
                
                console.log('API 응답:', response);
                
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
     * 결재선 테이블에서 결재자 ID 찾기
     * '대기' 상태인 행에서 결재자 ID를 찾습니다.
     * 
     * @returns {Promise<string|null>} 결재자 ID 또는 null
     */
    async function findApproverIdFromTable() {
        try {
            console.log('결재선 테이블에서 결재자 ID 찾기 시작');
            
            // 여러 방법으로 결재선 테이블 찾기 시도
            let approvalTable = document.getElementById('approvalLinesTable');
            
            // ID로 찾지 못한 경우 클래스 선택자로 시도
            if (!approvalTable) {
                console.log('approvalLinesTable ID로 테이블을 찾지 못해 대체 선택자 사용');
                approvalTable = document.querySelector('table.table-bordered:not(.text-center)');
            }
            
            // 그래도 찾지 못한 경우 모든 테이블 시도
            if (!approvalTable) {
                console.log('클래스 선택자로도 찾지 못해 모든 테이블 중 첫 번째 시도');
                approvalTable = document.querySelector('table.table-bordered');
            }
            
            if (!approvalTable) {
                console.warn('어떤 방법으로도 결재선 테이블을 찾을 수 없습니다.');
                return null;
            }
            
            console.log('결재선 테이블 찾음:', approvalTable);
            
            // 1. 대기 상태인 행에서 data-approver-id 속성 확인
            const pendingRow = approvalTable.querySelector('tr[data-approval-status="대기"]');
            if (pendingRow) {
                const approverId = pendingRow.getAttribute('data-approver-id');
                if (approverId) {
                    console.log(`테이블 행의 data-approver-id 속성에서 결재자 ID 찾음: ${approverId}`);
                    return approverId;
                }
            }
            
			// 2. 대기 상태가 있는 행 찾기
			            const rows = approvalTable.querySelectorAll('tbody tr');
			            console.log(`테이블에서 ${rows.length}개 행 찾음`);
			            
			            for (let i = 0; i < rows.length; i++) {
			                console.log(`${i+1}번째 행 검사 중:`, rows[i]);
			                
			                // 결재 상태 셀 찾기
			                const statusCell = rows[i].querySelector('td:nth-child(5) span');
			                console.log('상태 셀:', statusCell ? statusCell.textContent : '없음');
			                
			                if (statusCell && statusCell.textContent.trim() === '대기') {
			                    console.log('대기 상태인 행 찾음:', rows[i]);
			                    
			                    // 결재자 ID를 data-approver-id 속성에서 가져오기
			                    const approverId = rows[i].getAttribute('data-approver-id');
			                    if (approverId) {
			                        console.log(`테이블 대기 행에서 결재자 ID 찾음: ${approverId}`);
			                        return approverId;
			                    }
			                    
			                    // 결재자 셀에서 data-approver-id 속성 확인
			                    const approverCell = rows[i].querySelector('td:nth-child(2)');
			                    if (approverCell) {
			                        console.log('결재자 셀 찾음:', approverCell);
			                        
			                        const cellApproverId = approverCell.getAttribute('data-approver-id');
			                        if (cellApproverId) {
			                            console.log(`결재자 셀에서 ID 찾음: ${cellApproverId}`);
			                            return cellApproverId;
			                        }
			                        
			                        // 결재자 이름 추출 (마지막 대안)
			                        const approverName = approverCell.textContent.trim();
			                        if (approverName) {
			                            console.log(`결재자 이름으로 대체 ID 생성: ${approverName}`);
			                            return approverName;
			                        }
			                    }
			                    
			                    // 행 인덱스 + 1을 ID로 사용 (최후의 대안)
			                    const fallbackId = `approver-${i+1}`;
			                    console.log(`행 인덱스로 대체 ID 생성: ${fallbackId}`);
			                    return fallbackId;
			                }
			            }
			            
			            // DOM에서 hidden input 확인
			            const approverIdInput = document.getElementById('approverId');
			            if (approverIdInput && approverIdInput.value) {
			                console.log(`hidden input에서 결재자 ID 찾음: ${approverIdInput.value}`);
			                return approverIdInput.value;
			            }
			            
			            console.warn('결재선 테이블에서 대기 상태인 결재자 ID를 찾을 수 없습니다.');
			            
			            // 3. URL에서 임시 ID 생성
			            const urlParts = window.location.pathname.split('/');
			            const docIdFromUrl = urlParts[urlParts.length - 1] || 'unknown';
			            const fallbackId = `fallback-${docIdFromUrl}`;
			            console.warn(`모든 방법 실패: 임시 결재자 ID 생성 - ${fallbackId}`);
			            return fallbackId;
			        } catch (error) {
			            console.error('결재자 ID 찾기 중 오류:', error);
			            return 'error-fallback-id';
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
			        goToDocumentList,   // 문서 목록으로 이동
			        findApproverIdFromTable // 테이블에서 결재자 ID 찾기
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