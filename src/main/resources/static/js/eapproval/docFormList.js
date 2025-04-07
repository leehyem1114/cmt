/**
 * docFormList.js - 문서 양식 관리 스크립트
 * 
 * 문서 양식 목록 조회 및 관리 기능을 제공합니다.
 * - 양식 목록 표시 및 관리
 * - 조회, 등록, 수정, 삭제 기능
 * - 검색 기능
 * 
 * @version 1.0.0
 * @since 2025-04-07
 */

const DocFormList = (function() {
    //===========================================================================
    // 모듈 내부 변수
    //===========================================================================

    /**
     * 그리드 인스턴스
     */
    let grid = null;

    /**
     * API URL 상수 정의
     */
    const API_URLS = {
        FORMS: '/api/eapproval/forms',
        FORM: (id) => `/api/eapproval/forms/${id}`
    };

    /**
     * 페이지 URL 상수 정의
     */
    const PAGE_URLS = {
        FORM_LIST: '/eapproval/forms',
        FORM_NEW: '/eapproval/forms/new',
        FORM_EDIT: (id) => `/eapproval/forms/edit/${id}`,
        FORM_VIEW: (id) => `/eapproval/forms/view/${id}`
    };

    //===========================================================================
    // 초기화 및 이벤트 처리 함수
    //===========================================================================

    /**
     * 모듈 초기화 함수
     * 모듈의 초기 상태 설정 및 이벤트 리스너 등록
     * 
     * @returns {Promise<void>}
     */
    async function initialize() {
        try {
            console.log('DocFormList 초기화를 시작합니다.');

            // 그리드 초기화
            await initializeGrid();

            // 이벤트 리스너 등록
            registerEventListeners();

            // 알림 메시지 자동 숨김 설정
            setupAlertAutoDismiss();

            console.log('DocFormList 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('초기화 중 오류 발생:', error);
            await AlertUtil.showError('초기화 오류', '페이지 초기화 중 오류가 발생했습니다.');
        }
    }

    /**
     * 그리드 초기화 함수
     * TOAST UI Grid 인스턴스 생성 및 설정
     * 
     * @returns {Promise<void>}
     */
    async function initializeGrid() {
        try {
            console.log('그리드 초기화를 시작합니다.');

            // 그리드 요소 확인
            const gridElement = document.getElementById('docFormGrid');
            if (!gridElement) {
                throw new Error('그리드 요소를 찾을 수 없습니다.');
            }

            // 초기 데이터 확인
            const initialData = window.docFormData?.forms || [];
            console.log(`초기 데이터 로드: ${initialData.length}개 양식`);
            console.log("양식 내용 ", docFormData);

            // 그리드 컬럼 정의
            const columns = [{
                    header: '양식 ID',
                    name: 'formId',
                    sortable: true,
                    width: 150
                },
                {
                    header: '생성자',
                    name: 'creatorId',
                    sortable: true,
                    width: 120
                },
                {
                    header: '생성일',
                    name: 'createDate',
                    sortable: true,
                    width: 120,
                    formatter: function(obj) {
                        if (!obj.value) return '-';
                        const date = new Date(obj.value);
                        return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
                    }
                },
                {
                    header: '최종 수정자',
                    name: 'updaterId',
                    sortable: true,
                    width: 120
                },
                {
                    header: '최종 수정일',
                    name: 'updateDate',
                    sortable: true,
                    width: 120,
                    formatter: function(obj) {
                        if (!obj.value) return '-';
                        const date = new Date(obj.value);
                        return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
                    }
                },
                {
                    header: '관리',
                    name: 'actions',
                    width: 120,
                    formatter: function() {
                        return `
                            <div class="d-flex justify-content-center">
                                <button type="button" class="btn btn-sm btn-primary me-1 btn-view">
                                    <i class="bi bi-eye"></i>
                                </button>
                                <button type="button" class="btn btn-sm btn-warning me-1 btn-edit">
								<i class="bi bi-pencil"></i>
								                                </button>
								                                <button type="button" class="btn btn-sm btn-danger btn-delete">
								                                    <i class="bi bi-trash"></i>
								                                </button>
								                            </div>
								                        `;
                    }
                }
            ];

            // 그리드 옵션 설정
            const gridOptions = {
                el: gridElement,
                data: initialData,
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

            // 그리드 인스턴스 생성
            grid = new tui.Grid(gridOptions);

            // 행 더블클릭 이벤트 처리
            grid.on('dblclick', (ev) => {
                if (ev.rowKey === undefined || ev.rowKey === null) return;

                const rowData = grid.getRow(ev.rowKey);
                if (rowData && rowData.FORM_ID) {
                    window.location.href = PAGE_URLS.FORM_VIEW(rowData.FORM_ID);
                }
            });

            // 버튼 클릭 이벤트 처리
            grid.on('click', (ev) => {
                if (ev.rowKey === undefined || ev.rowKey === null) return;

                const rowData = grid.getRow(ev.rowKey);
                if (!rowData || !rowData.FORM_ID) return;

                const formId = rowData.FORM_ID;

                // 액션 버튼 클릭 처리
                if (ev.columnName === 'actions') {
                    const target = ev.nativeEvent.target.closest('button');
                    if (!target) return;

                    if (target.classList.contains('btn-view')) {
                        window.location.href = PAGE_URLS.FORM_VIEW(formId);
                    } else if (target.classList.contains('btn-edit')) {
                        window.location.href = PAGE_URLS.FORM_EDIT(formId);
                    } else if (target.classList.contains('btn-delete')) {
                        deleteForm(formId);
                    }
                }
            });

            console.log('그리드 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('그리드 초기화 중 오류:', error);
            throw error;
        }
    }

    /**
     * 이벤트 리스너 등록 함수
     * 버튼 클릭 등의 이벤트 핸들러 등록
     */
    function registerEventListeners() {
        console.log('이벤트 리스너 등록을 시작합니다.');

        // 양식 등록 버튼
        const createBtn = document.getElementById('createBtn');
        if (createBtn) {
            createBtn.addEventListener('click', () => {
                window.location.href = PAGE_URLS.FORM_NEW;
            });
        }

        // 새로고침 버튼
        const refreshBtn = document.getElementById('refreshBtn');
        if (refreshBtn) {
            refreshBtn.addEventListener('click', () => {
                refreshData();
            });
        }

        // 검색 버튼
        const searchBtn = document.getElementById('searchBtn');
        if (searchBtn) {
            searchBtn.addEventListener('click', () => {
                searchForms();
            });
        }

        // 초기화 버튼
        const resetBtn = document.getElementById('resetBtn');
        if (resetBtn) {
            resetBtn.addEventListener('click', () => {
                resetSearch();
            });
        }

        // 검색어 입력 필드 엔터키 이벤트
        const keywordInput = document.getElementById('keyword');
        if (keywordInput) {
            keywordInput.addEventListener('keypress', (e) => {
                if (e.key === 'Enter') {
                    e.preventDefault();
                    searchForms();
                }
            });
        }

        console.log('이벤트 리스너 등록이 완료되었습니다.');
    }

    /**
     * 알림 메시지 자동 숨김 설정 함수
     * 성공/실패 알림 메시지를 일정 시간 후 자동으로 숨김 처리
     */
    function setupAlertAutoDismiss() {
        // 성공 알림 자동 숨김
        const successAlert = document.getElementById('successAlert');
        if (successAlert) {
            setTimeout(() => {
                successAlert.style.display = 'none';
            }, 5000); // 5초 후 숨김
        }

        // 오류 알림 자동 숨김
        const errorAlert = document.getElementById('errorAlert');
        if (errorAlert) {
            setTimeout(() => {
                errorAlert.style.display = 'none';
            }, 8000); // 8초 후 숨김
        }
    }

    //===========================================================================
    // 데이터 관리 함수
    //===========================================================================

    /**
     * 데이터 새로고침 함수
     * 서버에서 최신 데이터를 다시 로드
     * 
     * @returns {Promise<void>}
     */
    async function refreshData() {
        try {
            console.log('데이터 새로고침을 시작합니다.');

            // 그리드가 초기화되지 않은 경우
            if (!grid) {
                console.warn('그리드가 초기화되지 않았습니다.');
                return;
            }

            // 검색 필드 초기화
            resetSearch();

            // ApiUtil을 사용하여 데이터 로드
            const response = await ApiUtil.getWithLoading(
                API_URLS.FORMS,
                null,
                '양식 목록 로드 중...'
            );

            // 응답 확인
            if (!response.success) {
                throw new Error(response.message || '양식 목록을 불러올 수 없습니다.');
            }

            // 그리드 데이터 업데이트
            const formsList = response.data || [];
            grid.resetData(formsList);

            console.log(`데이터 새로고침 완료: ${formsList.length}개 양식`);
        } catch (error) {
            console.error('데이터 새로고침 중 오류:', error);
            await ApiUtil.handleApiError(error, '데이터 로드 실패');
        }
    }

    /**
     * 양식 삭제 함수
     * 
     * @param {string} formId - 삭제할 양식 ID
     * @returns {Promise<void>}
     */
    async function deleteForm(formId) {
        try {
            console.log(`양식 삭제 시작: ${formId}`);

            if (!formId) {
                throw new Error('삭제할 양식 ID가 지정되지 않았습니다.');
            }

            // 삭제 확인 대화상자
            const confirmed = await AlertUtil.showConfirm({
                title: '양식 삭제',
                text: `양식 ID "${formId}"를 삭제하시겠습니까?\n삭제된 양식은 복구할 수 없습니다.`,
                icon: 'warning',
                confirmButtonText: '삭제',
                cancelButtonText: '취소'
            });

            if (!confirmed) {
                console.log('사용자가 삭제를 취소했습니다.');
                return;
            }

            // API 호출
            const response = await ApiUtil.getWithLoading(
                API_URLS.FORM(formId),
                null,
                '양식 정보 확인 중...'
            );

            // 양식 정보 확인
            if (!response.success) {
                throw new Error('양식 정보를 확인할 수 없습니다.');
            }

            // 삭제 API 호출
            const deleteResponse = await ApiUtil.delWithLoading(
                API_URLS.FORM(formId),
                null,
                '양식 삭제 중...'
            );

            // 응답 확인
            if (!deleteResponse.success) {
                throw new Error(deleteResponse.message || '양식 삭제에 실패했습니다.');
            }

            // 성공 메시지 표시
            await AlertUtil.showSuccess('삭제 완료', '양식이 성공적으로 삭제되었습니다.');

            // 데이터 새로고침
            await refreshData();

            console.log(`양식 삭제 완료: ${formId}`);
        } catch (error) {
            console.error('양식 삭제 중 오류:', error);
            await ApiUtil.handleApiError(error, '양식 삭제 실패');
        }
    }

    /**
     * 양식 검색 함수
     * 
     * @returns {Promise<void>}
     */
    async function searchForms() {
        try {
            console.log('양식 검색을 시작합니다.');

            const searchType = document.getElementById('searchType').value;
            const keyword = document.getElementById('keyword').value.trim();

            // 검색어가 없으면 전체 조회
            if (!keyword) {
                await refreshData();
                return;
            }

            console.log(`검색 조건: ${searchType}=${keyword}`);

            // ApiUtil을 사용하여 데이터 로드
            const response = await ApiUtil.getWithLoading(
                API_URLS.FORMS,
                null,
                '양식 검색 중...'
            );

            // 응답 확인
            if (!response.success) {
                throw new Error(response.message || '양식 목록을 불러올 수 없습니다.');
            }

            // 그리드 데이터 업데이트 (클라이언트 사이드 필터링)
            const allForms = response.data || [];

            // 검색 조건에 맞는 항목 필터링
            const filteredForms = allForms.filter(form => {
                const fieldValue = String(form[searchType.toUpperCase()] || '').toLowerCase();
                return fieldValue.includes(keyword.toLowerCase());
            });

            // 그리드 업데이트
            grid.resetData(filteredForms);

            console.log(`검색 결과: ${filteredForms.length}개 항목`);
        } catch (error) {
            console.error('양식 검색 중 오류:', error);
            await ApiUtil.handleApiError(error, '검색 실패');
        }
    }

    /**
     * 검색 초기화 함수
     */
    function resetSearch() {
        console.log('검색 초기화를 시작합니다.');

        // 검색 필드 초기화
        const searchType = document.getElementById('searchType');
        if (searchType) searchType.selectedIndex = 0;

        const keyword = document.getElementById('keyword');
        if (keyword) keyword.value = '';

        // 모든 데이터 다시 로드
        refreshData();

        console.log('검색 초기화가 완료되었습니다.');
    }

    //===========================================================================
    // 공개 API
    //===========================================================================

    return {
        // 초기화 함수
        initialize,

        // 데이터 관리 함수
        refreshData,
        deleteForm,
        searchForms,
        resetSearch
    };
})();

// DOM 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    DocFormList.initialize();
});