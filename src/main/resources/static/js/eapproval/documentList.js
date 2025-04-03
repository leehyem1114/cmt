/**
 * documentList.js - 전자결재 문서함 관리
 *
 * 전자결재 문서함 페이지의 기능을 담당하는 JavaScript 파일입니다.
 * - 탭별 문서 목록 표시
 * - 문서 검색 기능
 * - 문서 관련 이벤트 처리
 *
 * @version 1.0.0
 * @since 2025-04-03
 */

// 그리드 인스턴스 객체
const gridInstances = {};

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    console.log('문서함 페이지 초기화 시작');
    
    try {
        // 탭 이벤트 초기화
        initializeTabs();
        
        // 그리드 초기화
        initializeGrids();
        
        // 이벤트 리스너 등록
        setupEventListeners();
        
        // 성공 메시지가 있으면 표시
        checkSuccessMessage();
        
        console.log('문서함 페이지 초기화 완료');
    } catch (error) {
        console.error('초기화 중 오류 발생:', error);
        if (window.AlertUtil) {
            AlertUtil.showError('초기화 오류', '페이지를 불러오는 중 오류가 발생했습니다.');
        } else {
            alert('페이지를 불러오는 중 오류가 발생했습니다.');
        }
    }
});

/**
 * 성공 메시지 확인 및 표시
 */
function checkSuccessMessage() {
    // Thymeleaf에서 전달된 성공 메시지 확인
    const successMessage = document.querySelector('meta[name="successMessage"]')?.content;
    if (successMessage) {
        if (window.AlertUtil) {
            AlertUtil.showSuccess('성공', successMessage);
        } else {
            alert(successMessage);
        }
    }
    
    // URL 파라미터에서 메시지 확인
    const urlParams = new URLSearchParams(window.location.search);
    const msgParam = urlParams.get('msg');
    if (msgParam) {
        if (window.AlertUtil) {
            AlertUtil.showSuccess('성공', decodeURIComponent(msgParam));
        } else {
            alert(decodeURIComponent(msgParam));
        }
    }
}

/**
 * 탭 이벤트 초기화
 */
function initializeTabs() {
    // Bootstrap 5 탭 이벤트 처리
    const tabElements = document.querySelectorAll('#documentTab button[data-bs-toggle="tab"]');
    tabElements.forEach(tab => {
        tab.addEventListener('shown.bs.tab', function(event) {
            const targetId = event.target.getAttribute('data-bs-target').substring(1);
            console.log(`탭 전환: ${targetId}`);
            
            // 해당 탭의 그리드 새로고침 또는 처음 로딩하는 경우 초기화
            const gridId = getGridIdByTabId(targetId);
            if (gridInstances[gridId]) {
                refreshGridData(targetId);
            } else {
                initializeGrid(gridId, getDataByTabId(targetId));
            }
        });
    });
}

/**
 * 그리드 초기화
 */
function initializeGrids() {
    // 초기 활성 탭 확인
    const activeTab = document.querySelector('#documentTab button.active');
    if (activeTab) {
        const targetId = activeTab.getAttribute('data-bs-target').substring(1);
        const gridId = getGridIdByTabId(targetId);
        initializeGrid(gridId, getDataByTabId(targetId));
    } else {
        // 기본적으로 내 기안문서 탭 초기화
        initializeGrid('myDraftGrid', window.documentData?.myDrafts || []);
    }
}

/**
 * 개별 그리드 초기화
 * @param {string} gridId - 그리드 요소 ID
 * @param {Array} data - 그리드 초기 데이터
 */
function initializeGrid(gridId, data) {
    const gridElement = document.getElementById(gridId);
    if (!gridElement) {
        console.warn(`그리드 요소 없음: ${gridId}`);
        return;
    }
    
    try {
        // 그리드 이미 초기화되었는지 확인
        if (gridInstances[gridId]) {
            gridInstances[gridId].resetData(data || []);
            return;
        }
        
        // TOAST UI Grid 열 정의
        const columns = [
            {
                header: '문서번호',
                name: 'docNumber',
                width: 150,
                align: 'center'
            },
            {
                header: '양식',
                name: 'formId',
                width: 120,
                align: 'center'
            },
            {
                header: '제목',
                name: 'title',
                width: 'auto', // 나머지 공간 자동 채움
                minWidth: 200
            },
            {
                header: '기안자',
                name: 'drafterName',
                width: 100,
                align: 'center'
            },
            {
                header: '기안부서',
                name: 'draftDeptName',
                width: 120,
                align: 'center'
            },
            {
                header: '기안일자',
                name: 'draftDate',
                width: 120,
                align: 'center',
                formatter: function(obj) {
                    if (!obj.value) return '-';
                    const date = new Date(obj.value);
                    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
                }
            },
            {
                header: '문서상태',
                name: 'docStatus',
                width: 100,
                align: 'center',
                formatter: function(obj) {
                    let statusClass = '';
                    let statusText = obj.value || '';
                    
                    switch (statusText) {
                        case '진행중':
                            statusClass = 'badge bg-primary';
                            break;
                        case '완료':
                            statusClass = 'badge bg-success';
                            break;
                        case '반려':
                            statusClass = 'badge bg-danger';
                            break;
                        case '임시저장':
                            statusClass = 'badge bg-secondary';
                            break;
                        default:
                            statusClass = 'badge bg-secondary';
                    }
                    
                    return `<span class="${statusClass}">${statusText}</span>`;
                }
            }
        ];
        
        // 그리드 옵션
        const gridOptions = {
            el: gridElement,
            data: data || [],
            columns: columns,
            rowHeaders: ['rowNum'],
            bodyHeight: 500,
            minBodyHeight: 300,
            scrollX: true,
            scrollY: true,
            pageOptions: {
                useClient: true,
                perPage: 15
            }
        };
        
        // TOAST UI Grid 인스턴스 생성
        const grid = new tui.Grid(gridOptions);
        
        // 행 더블클릭 이벤트 처리
        grid.on('dblclick', (ev) => {
            if (ev.rowKey === undefined || ev.rowKey === null) return;
            
            const rowData = grid.getRow(ev.rowKey);
            if (rowData && rowData.docId) {
                window.location.href = `/eapproval/document/view/${rowData.docId}`;
            }
        });
        
        // 그리드 인스턴스 저장
        gridInstances[gridId] = grid;
        console.log(`그리드 초기화 완료: ${gridId}`);
        
    } catch (error) {
        console.error(`그리드 초기화 오류(${gridId}):`, error);
    }
}

/**
 * 그리드 데이터 새로고침
 * @param {string} tabId - 탭 ID
 */
function refreshGridData(tabId) {
    const gridId = getGridIdByTabId(tabId);
    const grid = gridInstances[gridId];
    
    if (!grid) {
        console.warn(`그리드 인스턴스 없음: ${gridId}`);
        return;
    }
    
    // 로딩 표시
    if (window.UIUtil) {
        UIUtil.toggleLoading(true, '문서 목록 로딩 중...');
    }
    
    // 검색 파라미터 구성
    const searchParams = new URLSearchParams();
    const docStatus = document.getElementById('docStatus')?.value;
    const searchType = document.getElementById('searchType')?.value;
    const keyword = document.getElementById('keyword')?.value;
    
    if (docStatus) searchParams.append('docStatus', docStatus);
    if (searchType && keyword) searchParams.append(searchType, keyword);
    
    // API 엔드포인트 결정
    let apiUrl = '/api/eapproval';
    
    switch (tabId) {
        case 'mydraft':
            apiUrl += '/drafts';
            break;
        case 'pending':
            apiUrl += '/pending';
            break;
        case 'completed':
            apiUrl += '/documents/status/완료';
            break;
        case 'rejected':
            apiUrl += '/documents/status/반려';
            break;
        default:
            apiUrl += '/drafts';
    }
    
    // API 호출
    fetch(`${apiUrl}?${searchParams.toString()}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`API 요청 실패: ${response.status}`);
            }
            return response.json();
        })
        .then(result => {
            // 로딩 종료
            if (window.UIUtil) {
                UIUtil.toggleLoading(false);
            }
            
            if (result.success) {
                // 그리드 데이터 갱신
                grid.resetData(result.data || []);
            } else {
                throw new Error(result.message || '문서 조회 중 오류가 발생했습니다.');
            }
        })
        .catch(error => {
            console.error('문서 목록 조회 오류:', error);
            
            // 로딩 종료
            if (window.UIUtil) {
                UIUtil.toggleLoading(false);
            }
            
            // 오류 알림
            if (window.AlertUtil) {
                AlertUtil.showError('조회 실패', error.message || '문서 목록을 불러오는 중 오류가 발생했습니다.');
            } else {
                alert('문서 목록을 불러오는 중 오류가 발생했습니다.');
            }
            
            // 캐시된 데이터 사용
            const cachedData = getDataByTabId(tabId);
            if (cachedData && cachedData.length > 0) {
                grid.resetData(cachedData);
            }
        });
}

/**
 * 이벤트 리스너 등록
 */
function setupEventListeners() {
    // 검색 버튼
    const searchBtn = document.getElementById('searchBtn');
    if (searchBtn) {
        searchBtn.addEventListener('click', performSearch);
    }
    
    // 초기화 버튼
    const resetBtn = document.getElementById('resetBtn');
    if (resetBtn) {
        resetBtn.addEventListener('click', resetSearch);
    }
    
    // 문서 작성 버튼
    const createBtn = document.getElementById('createBtn');
    if (createBtn) {
        createBtn.addEventListener('click', function() {
            window.location.href = '/eapproval/document/new';
        });
    }
    
    // 검색 필드 엔터키 이벤트
    const keywordInput = document.getElementById('keyword');
    if (keywordInput) {
        keywordInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                performSearch();
            }
        });
    }
    
    // 필터 변경 시 실시간 검색 (선택적)
    const docStatusSelect = document.getElementById('docStatus');
    if (docStatusSelect) {
        docStatusSelect.addEventListener('change', performSearch);
    }
}

/**
 * 검색 수행
 */
function performSearch() {
    const activeTab = document.querySelector('#documentTab button.active');
    if (!activeTab) return;
    
    const targetId = activeTab.getAttribute('data-bs-target').substring(1);
    console.log(`검색 수행: 탭=${targetId}`);
    
    refreshGridData(targetId);
}

/**
 * 검색 필드 초기화
 */
function resetSearch() {
    // 검색 필드 초기화
    const docStatusSelect = document.getElementById('docStatus');
    const searchTypeSelect = document.getElementById('searchType');
    const keywordInput = document.getElementById('keyword');
    
    if (docStatusSelect) docStatusSelect.value = '';
    if (searchTypeSelect) searchTypeSelect.value = 'title';
    if (keywordInput) keywordInput.value = '';
    
    // 검색 수행
    performSearch();
}

/**
 * 탭 ID에 해당하는 그리드 ID 반환
 * @param {string} tabId - 탭 ID
 * @returns {string} 그리드 ID
 */
function getGridIdByTabId(tabId) {
    switch (tabId) {
        case 'mydraft': return 'myDraftGrid';
        case 'pending': return 'pendingGrid';
        case 'completed': return 'completedGrid';
        case 'rejected': return 'rejectedGrid';
        default: return 'myDraftGrid';
    }
}

/**
 * 탭 ID에 해당하는 데이터 반환
 * @param {string} tabId - 탭 ID
 * @returns {Array} 문서 데이터
 */
function getDataByTabId(tabId) {
    if (!window.documentData) return [];
    
    switch (tabId) {
        case 'mydraft': return window.documentData.myDrafts || [];
        case 'pending': return window.documentData.pendingDocs || [];
        case 'completed': return window.documentData.completedDocs || [];
        case 'rejected': return window.documentData.rejectedDocs || [];
        default: return [];
    }
}