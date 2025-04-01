/**
 * documentList.js - 전자결재 통합 문서함 관리
 * 
 * 탭 별 그리드 초기화 및 검색, 이벤트 처리 기능을 제공합니다.
 * 
 * @version 1.0.0
 * @since 2025-04-01
 */

// 전역 변수 선언
let myDraftGrid, pendingGrid, completedGrid, rejectedGrid;
let currentTab = 'mydraft'; // 현재 활성화된 탭 ID

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', async function() {
    console.log('문서함 페이지 초기화 시작');
    
    try {
        // 그리드 초기화
        initGrids();
        
        // 이벤트 리스너 등록
        registerEvents();
        
        // 초기 데이터 로드 (서버에서 받은 데이터 사용)
        loadInitialData();
        
        console.log('문서함 페이지 초기화 완료');
    } catch (error) {
        console.error('초기화 중 오류 발생:', error);
        await AlertUtil.showError('초기화 오류', '페이지 초기화 중 오류가 발생했습니다.');
    }
});

/**
 * 그리드 초기화 함수
 * 각 탭에 대한 그리드를 초기화합니다.
 */
function initGrids() {
    // 공통 컬럼 정의
    const commonColumns = [
        { 
            header: '문서번호', 
            name: 'docNumber', 
            width: 150,
            sortable: true
        },
        { 
            header: '제목', 
            name: 'title', 
            width: 350,
            sortable: true
        },
        { 
            header: '양식', 
            name: 'formId', 
            width: 120,
            sortable: true
        },
        { 
            header: '기안일', 
            name: 'draftDate', 
            width: 120,
            sortable: true,
            formatter: function(value) {
                if (!value.value) return '';
                const date = new Date(value.value);
                return date.toLocaleDateString();
            }
        },
        { 
            header: '상태', 
            name: 'docStatus', 
            width: 100,
            sortable: true,
            formatter: function(value) {
                const status = value.value;
                let className = '';
                if (status === '진행중') className = 'badge bg-primary';
                else if (status === '완료') className = 'badge bg-success';
                else if (status === '반려') className = 'badge bg-danger';
                else if (status === '임시저장') className = 'badge bg-secondary';
                
                return `<span class="${className}">${status}</span>`;
            }
        }
    ];
    
    // 내 기안 문서 그리드
    myDraftGrid = GridUtil.registerGrid({
        id: 'myDraftGrid',
        columns: commonColumns,
        data: [],
        gridOptions: {
            rowHeaders: ['rowNum'],
            bodyHeight: 450
        }
    });
    
    // 결재 대기 그리드 - 기안자 컬럼 추가
    const pendingColumns = [
        ...commonColumns.slice(0, 3),
        { 
            header: '기안자', 
            name: 'drafterName', 
            width: 100,
            sortable: true
        },
        ...commonColumns.slice(3)
    ];
    
    pendingGrid = GridUtil.registerGrid({
        id: 'pendingGrid',
        columns: pendingColumns,
        data: [],
        gridOptions: {
            rowHeaders: ['rowNum'],
            bodyHeight: 450
        }
    });
    
    // 완료 문서 그리드
    completedGrid = GridUtil.registerGrid({
        id: 'completedGrid',
        columns: pendingColumns,
        data: [],
        gridOptions: {
            rowHeaders: ['rowNum'],
            bodyHeight: 450
        }
    });
    
    // 반려 문서 그리드
    rejectedGrid = GridUtil.registerGrid({
        id: 'rejectedGrid',
        columns: pendingColumns,
        data: [],
        gridOptions: {
            rowHeaders: ['rowNum'],
            bodyHeight: 450
        }
    });
    
    // 모든 그리드에 더블클릭 이벤트 등록
    setupGridEvents('myDraftGrid');
    setupGridEvents('pendingGrid');
    setupGridEvents('completedGrid');
    setupGridEvents('rejectedGrid');
}

/**
 * 그리드 이벤트 설정 함수
 * 
 * @param {string} gridId - 그리드 ID
 */
function setupGridEvents(gridId) {
    GridUtil.onDblClick(gridId, function(rowData) {
        if (rowData && rowData.docId) {
            location.href = `/eapproval/document/view/${rowData.docId}`;
        }
    });
}

/**
 * 이벤트 리스너 등록 함수
 */
function registerEvents() {
    // 탭 전환 이벤트
    const tabs = document.querySelectorAll('button[data-bs-toggle="tab"]');
    tabs.forEach(tab => {
        tab.addEventListener('shown.bs.tab', function(event) {
            const targetId = event.target.getAttribute('data-bs-target').substring(1);
            currentTab = targetId;
            console.log('탭 전환:', currentTab);
        });
    });
    
    // 검색 버튼 클릭 이벤트
    document.getElementById('searchBtn').addEventListener('click', function() {
        searchDocuments();
    });
    
    // 초기화 버튼 클릭 이벤트
    document.getElementById('resetBtn').addEventListener('click', function() {
        resetSearch();
    });
    
    // 검색어 입력 필드 엔터키 이벤트
    document.getElementById('keyword').addEventListener('keyup', function(e) {
        if (e.key === 'Enter') {
            searchDocuments();
        }
    });
}

/**
 * 초기 데이터 로드 함수
 * Thymeleaf에서 전달받은 데이터를 그리드에 설정합니다.
 */
function loadInitialData() {
    if (window.documentData) {
        // 내 기안 문서
        if (window.documentData.myDrafts && window.documentData.myDrafts.length > 0) {
            myDraftGrid.resetData(window.documentData.myDrafts);
        }
        
        // 결재 대기 문서
        if (window.documentData.pendingDocs && window.documentData.pendingDocs.length > 0) {
            pendingGrid.resetData(window.documentData.pendingDocs);
        }
        
        // 완료 문서
        if (window.documentData.completedDocs && window.documentData.completedDocs.length > 0) {
            completedGrid.resetData(window.documentData.completedDocs);
        }
        
        // 반려 문서
        if (window.documentData.rejectedDocs && window.documentData.rejectedDocs.length > 0) {
            rejectedGrid.resetData(window.documentData.rejectedDocs);
        }
    } else {
        console.log('서버에서 받은 초기 데이터가 없습니다.');
    }
}

/**
 * 문서 검색 함수
 * 현재 활성화된 탭에 따라 적절한 API를 호출합니다.
 */
async function searchDocuments() {
    try {
        // 검색 파라미터 수집
        const docStatus = document.getElementById('docStatus').value;
        const searchType = document.getElementById('searchType').value;
        const keyword = document.getElementById('keyword').value;
        
        // 현재 탭에 따라 다른 API 엔드포인트 사용
        let apiUrl = '';
        let params = { docStatus, searchType, keyword };
        
        switch (currentTab) {
            case 'mydraft':
                apiUrl = '/api/eapproval/drafts';
                break;
            case 'pending':
                apiUrl = '/api/eapproval/pending';
                break;
            case 'completed':
                apiUrl = '/api/eapproval/documents/status/완료';
                break;
            case 'rejected':
                apiUrl = '/api/eapproval/documents/status/반려';
                break;
        }
        
        // API 호출
        const response = await ApiUtil.getWithLoading(
            apiUrl,
            params,
            '문서 검색 중...'
        );
        
        // 결과 처리
        let documents = [];
        if (response.success && response.data) {
            documents = response.data;
        } else if (Array.isArray(response)) {
            documents = response;
        }
        
        // 현재 탭의 그리드에 데이터 설정
        updateGridData(currentTab, documents);
        
    } catch (error) {
        console.error('문서 검색 중 오류:', error);
        await AlertUtil.showError('검색 오류', '문서 검색 중 오류가 발생했습니다.');
    }
}

/**
 * 그리드 데이터 업데이트 함수
 * 
 * @param {string} tab - 탭 ID
 * @param {Array} data - 표시할 데이터
 */
function updateGridData(tab, data) {
    switch (tab) {
        case 'mydraft':
            myDraftGrid.resetData(data);
            break;
        case 'pending':
            pendingGrid.resetData(data);
            break;
        case 'completed':
            completedGrid.resetData(data);
            break;
        case 'rejected':
            rejectedGrid.resetData(data);
            break;
    }
}

/**
 * 검색 초기화 함수
 */
async function resetSearch() {
    // 검색 필드 초기화
    document.getElementById('docStatus').selectedIndex = 0;
    document.getElementById('searchType').selectedIndex = 0;
    document.getElementById('keyword').value = '';
    
    // 다시 검색 실행
    await searchDocuments();
}