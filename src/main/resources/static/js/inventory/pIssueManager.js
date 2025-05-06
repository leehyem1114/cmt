/**
 * 제품 출고관리 - 출고 정보 관리 모듈
 * 
 * 제품 출고정보의 조회, 요청, 확정, 취소 기능을 담당하는 관리 모듈입니다.

 * 
 * @version 2.1.0
 * @since 2025-04-30
 */
const ProductsIssueManager = (function() {
    // =============================
    // 모듈 내부 변수 - 필요에 따라 변경하세요
    // =============================

    // 그리드 인스턴스 참조
    let pIssueGrid;
    let soDetailGrid;

    // 현재 선택된 출고정보
    let selectedIssue = null;

    // API URL 상수 정의
    const API_URLS = {
        LIST: '/api/productsissue/list', // 출고 목록 조회
        DETAIL: (issueNo) => `/api/productsissue/detail/${issueNo}`, // 상세 정보 조회
        HISTORY: (issueNo) => `/api/productsissue/history/${issueNo}`, // 이력 정보 조회
        REQUEST: '/api/productsissue/request', // 출고 요청
        REQUEST_BATCH: '/api/productsissue/request-batch', // 다건 출고 요청
        REQUEST_BY_STATUS: '/api/productsissue/request/by-status', // 상태별 출고 요청
        INSPECTION: '/api/productsissue/inspection', // 검수 등록 API
        INSPECTION_BATCH: '/api/productsissue/inspection-batch', // 다건 검수 등록 API
        PROCESS: '/api/productsissue/process', // 출고 처리
        PROCESS_BATCH: '/api/productsissue/process-batch', // 다건 출고 처리
        CANCEL: '/api/productsissue/cancel', // 출고 취소
        SALES_ORDER: '/api/productsissue/sales-orders', // 수주 목록 조회 API
        EXCEL: {
            DOWNLOAD: '/api/productsissue/excel/download' // 엑셀 다운로드 API URL

        }
    };

    // 출고 상태 정의
    const ISSUE_STATUS = {
        WAITING: '출고대기',
        INSPECTING: '검수중',
        INSPECT_PASSED: '검사 합격',
        INSPECT_FAILED: '검사 불합격',
        COMPLETED: '출고완료',
        CANCELED: '취소'
    };

    // =============================
    // 초기화 및 이벤트 처리 함수
    // =============================

    /**
     * 모듈 초기화 함수
     * 관리 모듈의 초기 설정 및 이벤트 바인딩을 수행합니다.
     */
    async function init() {
        try {
            console.log('제품 출고관리 초기화를 시작합니다.');

            // 그리드 초기화
            await initGrid();




            // 이벤트 리스너 등록
            await registerEvents();

            // 그리드 검색 초기화
            initGridSearch();

            // 엑셀 기능 초기화
            initExcelFeatures();

            // 모달 초기화
            initModal();

            console.log('제품 출고관리 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('초기화 중 오류 발생:', error);
            await AlertUtil.showError('초기화 오류', '제품 출고관리 초기화 중 오류가 발생했습니다.');
        }
    }

    /**
     * 이벤트 리스너 등록 함수
     * 버튼 클릭 및 엔터키 검색 등의 이벤트 리스너를 등록합니다.
     */
    async function registerEvents() {
        try {
            // UIUtil을 사용하여 이벤트 리스너 등록 - 버튼 이벤트
            await UIUtil.registerEventListeners({
                'pIssueRequestBtn': openIssueRequestModal, // 출고 요청 모달 열기
                'pIssueInspectionBtn': registerInspection, // 검수 등록 버튼 - 추가됨
                'pIssueConfirmBtn': confirmIssue, // 출고 확정 버튼
                'pIssueCancelBtn': cancelIssue, // 출고 취소 버튼
                'pIssueSearchBtn': searchData, // 데이터 검색 버튼
                'createIssueBtn': createIssueRequest // 출고 요청 생성 버튼
            });

            // 엔터키 검색 이벤트 등록
            await UIUtil.bindEnterKeySearch('pIssueInput', searchData);

            // 탭 이벤트 등록 - 이력정보 탭
            const historyTab = document.getElementById('history-tab');
            if (historyTab) {
                historyTab.addEventListener('shown.bs.tab', function(e) {
                    if (selectedIssue) {
                        loadHistoryData(selectedIssue.ISSUE_NO);


                    }
                });
            }

            // 상태별 출고 요청 버튼들 이벤트 등록
            document.querySelectorAll('.issue-by-status').forEach(item => {
                item.addEventListener('click', function(e) {
                    e.preventDefault();
                    const status = this.getAttribute('data-status');
                    createIssueRequestsByStatus(status);
                });
            });
        } catch (error) {
            console.error('이벤트 리스너 등록 중 오류:', error);
            throw error;
        }
    }

    /**
     * 그리드 검색 초기화 함수
     * GridSearchUtil을 사용하여 그리드 검색 기능을 설정합니다.
     */
    function initGridSearch() {
        try {
            console.log('그리드 검색 초기화');

            // 그리드 검색 설정
            GridSearchUtil.setupGridSearch({
                gridId: 'pIssueGrid',
                searchInputId: 'pIssueInput',
                autoSearch: true, // 입력 시 자동 검색
                beforeSearch: function() {
                    console.log('그리드 검색 시작');
                    return true;
                },
                afterSearch: function(filteredData) {
                    console.log('그리드 검색 완료, 결과:', filteredData.length, '건');

                    // 상세 정보 영역 초기화
                    hideDetailSection();
                }
            });
        } catch (error) {
            console.error('그리드 검색 초기화 중 오류:', error);
        }
    }

    /**
     * 엑셀 기능 초기화 함수
     * ExcelUtil을 사용하여 엑셀 다운로드 기능을 설정합니다.
     */
    function initExcelFeatures() {
        try {
            console.log('엑셀 기능 초기화');

            // 엑셀 다운로드 버튼 설정
            ExcelUtil.setupExcelDownloadButton({
                buttonId: 'pIssueExcelDownBtn',
                gridId: 'pIssueGrid',
                fileName: 'products-issue-data.xlsx',
                sheetName: '제품출고정보',
                beforeDownload: function() {
                    console.log('엑셀 다운로드 시작');
                    return true;
                },
                afterDownload: function() {
                    console.log('엑셀 다운로드 완료');
                }
            });
        } catch (error) {
            console.error('엑셀 기능 초기화 중 오류:', error);
        }
    }

    /**
     * 모달 초기화 함수
     * 모달 다이얼로그 관련 초기화를 수행합니다.
     */
    function initModal() {
        try {
            console.log('모달 초기화');

            // 모달 닫힐 때 이벤트 - 모달 내용 초기화
            const issueRequestModal = document.getElementById('issueRequestModal');
            if (issueRequestModal) {
                issueRequestModal.addEventListener('hidden.bs.modal', function() {
                    // 그리드 초기화
                    if (soDetailGrid) {
                        soDetailGrid.resetData([]);
                    }
                });

                // 모달이 완전히 보여진 후에 그리드를 초기화하도록 이벤트 추가
                issueRequestModal.addEventListener('shown.bs.modal', function() {
                    // 그리드가 제대로 보이지 않는 문제 해결을 위한 그리드 갱신
                    if (soDetailGrid) {
                        soDetailGrid.refreshLayout();




                    }
                });
            }

            // 수주 상세 그리드 초기화
            initSoDetailGrid();
        } catch (error) {
            console.error('모달 초기화 중 오류:', error);
        }
    }

    // =============================
    // 그리드 관련 함수
    // =============================

    /**
     * 그리드 초기화 함수
     * 그리드를 생성하고 초기 데이터를 로드합니다.
     */
    async function initGrid() {
        try {
            console.log('그리드 초기화를 시작합니다.');

            // DOM 요소 존재 확인
            const gridElement = document.getElementById('pIssueGrid');
            if (!gridElement) {
                throw new Error('pIssueGrid 요소를 찾을 수 없습니다. HTML을 확인해주세요.');
            }

            // 서버에서 받은 데이터 활용
            const gridData = window.pIssueList || [];
            console.log('초기 데이터:', gridData ? gridData.length : 0, '건');

            // 그리드 생성 - GridUtil 사용
            pIssueGrid = GridUtil.registerGrid({
                id: 'pIssueGrid',
                columns: [{
                        header: '출고 코드',
                        name: 'ISSUE_CODE',
                        sortable: true
                    },
                    {
                        header: '제품 코드',
                        name: 'PDT_CODE',
                        sortable: true


                    },
                    {
                        header: '제품명',
                        name: 'PDT_NAME',
                        sortable: true


                    },
                    {
                        header: '요청일',
                        name: 'REQUEST_DATE',

                        sortable: true,
                        formatter: function(obj) {
                            if (!obj.value) return '-';
                            const date = new Date(obj.value);
                            return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
                        }
                    },
                    {
                        header: '출고일',
                        name: 'ISSUE_DATE',

                        sortable: true,
                        formatter: function(obj) {
                            if (!obj.value) return '-';
                            const date = new Date(obj.value);
                            return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
                        }
                    },
                    {
                        header: '요청 수량',
                        name: 'REQUEST_QTY',
                        sortable: true



                    },
                    {
                        header: '출고 수량',
                        name: 'ISSUED_QTY',
                        sortable: true


                    },
                    {
                        header: '출고 상태',
                        name: 'ISSUE_STATUS',

                        sortable: true,
                        formatter: function(obj) {
                            const status = obj.value || ISSUE_STATUS.WAITING;
                            let badgeClass = 'badge-waiting';

                            if (status === ISSUE_STATUS.INSPECTING) {
                                badgeClass = 'badge-inspecting';
                            } else if (status === ISSUE_STATUS.INSPECT_PASSED) {
                                badgeClass = 'badge-inspectPass';
                            } else if (status === ISSUE_STATUS.INSPECT_FAILED) {
                                badgeClass = 'badge-inspectFail';
                            } else if (status === ISSUE_STATUS.COMPLETED) {
                                badgeClass = 'badge-completed';
                            } else if (status === ISSUE_STATUS.CANCELED) {
                                badgeClass = 'badge-canceled';
                            }

                            return `<span class="badge ${badgeClass}">${status}</span>`;
                        }
                    },
                    {
                        header: '창고 코드',
                        name: 'WAREHOUSE_CODE',
                        sortable: true


                    },
                    {
                        header: '담당자',
                        name: 'ISSUER',
                        sortable: true



                    },
                    {
                        header: '출고번호',
                        name: 'ISSUE_NO',




                        hidden: true
                    }
                ],
                columnOptions: {
                    resizable: true
                },
                pageOptions: {
                    useClient: true,
                    perPage: 10
                },
                data: gridData,
                draggable: false,

                gridOptions: {
                    rowHeaders: ['rowNum', 'checkbox']
                }

            });

            // 그리드 데이터 정리 - 빈 상태 값 초기화
            const rows = pIssueGrid.getData();
            rows.forEach((row, index) => {
                if (!row.ISSUE_STATUS) {
                    pIssueGrid.setValue(row.rowKey, 'ISSUE_STATUS', ISSUE_STATUS.WAITING);




                }
            });

            // 행 클릭 이벤트 등록 - 상세 정보 표시
            GridUtil.onRowClick('pIssueGrid', async function(rowData, rowKey, columnName) {
                if (!rowData) return;


                // 현재 선택된 출고 정보 저장
                selectedIssue = rowData;




                // 상세 정보 조회 및 표시
                await loadIssueDetail(rowData.ISSUE_NO);


            });

            // 그리드 원본 데이터 저장 (검색 기능 위해 추가)
            GridSearchUtil.updateOriginalData('pIssueGrid', gridData);

            console.log('그리드 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('그리드 초기화 오류:', error);
            throw error;
        }
    }

    /**
     * 수주 상세 그리드 초기화 함수
     * 모달 내에서 사용할 수주 상세 그리드를 초기화합니다.
     */
    function initSoDetailGrid() {
        try {
            console.log('수주 상세 그리드 초기화');

            // DOM 요소 확인
            const gridElement = document.getElementById('soDetailGrid');
            if (!gridElement) {
                console.warn('soDetailGrid 요소를 찾을 수 없습니다.');
                return;
            }

            // 그리드 생성
            soDetailGrid = GridUtil.registerGrid({
                id: 'soDetailGrid',
                columns: [{
                        header: '수주 코드',
                        name: 'SO_CODE',
                        sortable: true
                    },
                    {
                        header: '제품 코드',
                        name: 'PDT_CODE',
                        sortable: true
                    },
                    {
                        header: '제품명',
                        name: 'PDT_NAME',
                        sortable: true
                    },
                    {
                        header: '수주 수량',
                        name: 'SO_QTY',
                        sortable: true
                    },
                    {
                        header: '수주일',
                        name: 'SO_DATE',
                        sortable: true,
                        formatter: function(obj) {
                            if (!obj.value) return '-';
                            // 날짜 포맷팅
                            return formatDate(obj.value);
                        }
                    },
                    {
                        header: '출하일',
                        name: 'SHIP_DATE',
                        sortable: true,
                        formatter: function(obj) {
                            if (!obj.value) return '-';
                            // 날짜 포맷팅
                            return formatDate(obj.value);
                        }
                    },
                    {
                        header: '납기일',
                        name: 'SO_DUE_DATE',
                        sortable: true,
                        formatter: function(obj) {
                            if (!obj.value) return '-';
                            // 날짜 포맷팅
                            return formatDate(obj.value);
                        }
                    },
                    {
                        header: '상태',
                        name: 'SO_STATUS',
                        sortable: true
                    },
                    {
                        header: '창고 코드',
                        name: 'WHS_CODE',
                        sortable: true
                    }
                ],
                data: [],
                pageOptions: {
                    useClient: true,
                    perPage: 5
                },
                gridOptions: {
                    rowHeaders: ['checkbox']
                }
            });

            console.log('수주 상세 그리드 초기화 완료');
        } catch (error) {
            console.error('수주 상세 그리드 초기화 중 오류:', error);

        }
    }

    // =============================
    // 상세 정보 관련 함수
    // =============================

    /**
     * 출고 상세 정보 로드 함수
     * 선택된 출고 정보의 상세 내용을 조회하고 표시합니다.
     * 
     * @param {number} issueNo - 출고 번호
     */
    async function loadIssueDetail(issueNo) {
        try {
            // 로딩 표시
            await UIUtil.toggleLoading(true, '상세 정보를 불러오는 중...');

            // API 호출
            const response = await ApiUtil.get(API_URLS.DETAIL(issueNo));

            // 로딩 종료
            await UIUtil.toggleLoading(false);

            if (!response.success) {
                throw new Error('상세 정보를 가져오는데 실패했습니다: ' + response.message);
            }

            const detailData = response.data;

            // 상세 정보 표시
            displayDetailInfo(detailData);

            // 상세 정보 영역 표시
            showDetailSection();

            return true;
        } catch (error) {
            console.error('상세 정보 로드 중 오류:', error);
            await UIUtil.toggleLoading(false);

            // 오류 알림
            await AlertUtil.showError('조회 오류', '상세 정보를 불러오는데 실패했습니다.');

            return false;
        }
    }

    /**
     * 상세 정보 표시 함수
     * 
     * @param {Object} detailData - 출고 상세 정보 데이터
     */
    function displayDetailInfo(detailData) {
        // 기본 정보 탭 데이터 설정
        document.getElementById('issueNo').textContent = detailData.ISSUE_NO || '';
        document.getElementById('issueCode').textContent = detailData.ISSUE_CODE || '';
        document.getElementById('pdtCode').textContent = detailData.PDT_CODE || '';
        document.getElementById('pdtName').textContent = detailData.PDT_NAME || '';
        document.getElementById('requestQty').textContent = detailData.REQUEST_QTY || '';

        // 날짜 포맷팅
        document.getElementById('requestDate').textContent = formatDate(detailData.REQUEST_DATE);
        document.getElementById('issueDate').textContent = formatDate(detailData.ISSUE_DATE);

        // 상태에 따라 배지 스타일 적용
        const status = detailData.ISSUE_STATUS || ISSUE_STATUS.WAITING;
        let badgeClass = 'badge-waiting';

        if (status === ISSUE_STATUS.INSPECTING) {
            badgeClass = 'badge-inspecting';
        } else if (status === ISSUE_STATUS.INSPECT_PASSED) {
            badgeClass = 'badge-inspectPass';
        } else if (status === ISSUE_STATUS.INSPECT_FAILED) {
            badgeClass = 'badge-inspectFail';
        } else if (status === ISSUE_STATUS.COMPLETED) {
            badgeClass = 'badge-completed';
        } else if (status === ISSUE_STATUS.CANCELED) {
            badgeClass = 'badge-canceled';
        }

        document.getElementById('issueStatus').innerHTML = `<span class="badge ${badgeClass}">${status}</span>`;
        document.getElementById('warehouseInfo').textContent = detailData.WAREHOUSE_CODE || '';
        document.getElementById('issuer').textContent = detailData.ISSUER || '';

        // 이력 정보는 탭 선택 시 별도 로드
    }

    /**
     * 이력 정보 로드 함수
     * 선택된 출고의 이력 정보를 로드하고 표시합니다.
     * 
     * @param {number} issueNo - 출고 번호
     * @returns {Promise<boolean>} 로드 성공 여부
     */
    async function loadHistoryData(issueNo) {
        try {
            // 로딩 표시
            await UIUtil.toggleLoading(true, '이력 정보를 불러오는 중...');

            // API 호출
            const response = await ApiUtil.get(API_URLS.HISTORY(issueNo));

            // 로딩 종료
            await UIUtil.toggleLoading(false);

            if (!response.success) {
                throw new Error('이력 정보를 가져오는데 실패했습니다: ' + response.message);
            }

            const historyData = response.data || [];

            // 이력 정보 표시
            displayHistoryInfo(historyData);

            return true;




        } catch (error) {
            console.error('이력 정보 로드 중 오류:', error);
            await UIUtil.toggleLoading(false);

            // 오류 시 빈 테이블 표시
            displayHistoryInfo([]);

            return false;
        }
    }

    /**
     * 이력 정보 표시 함수
     * 
     * @param {Array} historyData - 이력 정보 데이터 배열
     */
    function displayHistoryInfo(historyData) {
        const historyInfoBody = document.getElementById('historyInfoBody');

        if (!historyInfoBody) {
            console.error('이력 정보 테이블 요소를 찾을 수 없습니다.');
            return;
        }

        if (!historyData || historyData.length === 0) {
            historyInfoBody.innerHTML = `
                <tr>
                    <td colspan="4" class="text-center">이력 정보가 없습니다.</td>
                </tr>
            `;
            return;
        }

        // 이력 정보 HTML 구성
        let html = '';
        historyData.forEach(history => {
            html += `
                <tr>
                    <td>${formatDateTime(history.ACTION_DATE)}</td>
                    <td>${history.ACTION_TYPE || ''}</td>
                    <td>${history.ACTION_DESCRIPTION || ''}</td>
                    <td>${history.ACTION_USER || ''}</td>
                </tr>
            `;
        });

        historyInfoBody.innerHTML = html;
    }

    /**
     * 상세 정보 영역 표시 함수
     */
    function showDetailSection() {
        const detailSection = document.getElementById('detailSection');
        if (detailSection) {
            detailSection.style.display = 'block';

            try {
                // Bootstrap 5 방식으로 첫 번째 탭 활성화
                const firstTab = document.querySelector('#basic-info-tab');
                if (firstTab && window.bootstrap) {
                    const tab = new bootstrap.Tab(firstTab);
                    tab.show();
                    console.log('기본 탭이 활성화되었습니다.');
                }
            } catch (error) {
                console.error('탭 초기화 오류:', error);
            }
        }
    }

    /**
     * 상세 정보 영역 숨김 함수
     */
    function hideDetailSection() {
        const detailSection = document.getElementById('detailSection');
        if (detailSection) {
            detailSection.style.display = 'none';
        }

        // 선택된 출고 정보 초기화
        selectedIssue = null;
    }

    // =============================
    // 업무 처리 함수
    // =============================

    /**
     * 데이터 검색 함수
     * 검색어를 이용하여 데이터를 검색하고 그리드에 결과를 표시합니다.
     */
    async function searchData() {
        try {
            const keyword = document.getElementById('pIssueInput').value;
            console.log('데이터 검색 시작. 검색어:', keyword);

            // API 호출
            const response = await ApiUtil.getWithLoading(
                API_URLS.LIST, {
                    keyword: keyword
                },
                '데이터 검색 중...'
            );

            // 응답 데이터 처리
            const data = Array.isArray(response) ? response : (response.data || []);

            // 그리드 데이터 설정
            const grid = GridUtil.getGrid('pIssueGrid');
            if (grid) {
                grid.resetData(data);

                // 그리드 원본 데이터 업데이트 (검색 기능 위해 추가)
                GridSearchUtil.updateOriginalData('pIssueGrid', data);
            }

            // 상세 정보 영역 숨기기
            hideDetailSection();

            console.log('데이터 검색 완료. 결과:', data.length, '건');
            return data;
        } catch (error) {
            console.error('데이터 검색 중 오류:', error);
            await AlertUtil.showError('검색 오류', '데이터 검색 중 오류가 발생했습니다.');
            throw error;
        }
    }

    /**
     * 출고 요청 모달 열기 함수
     */
    async function openIssueRequestModal() {
        try {
            console.log('출고 요청 모달 열기');

            // 모달 표시
            const modal = new bootstrap.Modal(document.getElementById('issueRequestModal'));
            modal.show();

            // 모달이 표시된 후 수주 목록 로드
            await loadAllSalesOrders();

            return true;
        } catch (error) {
            console.error('출고 요청 모달 열기 중 오류:', error);
            await AlertUtil.showError('오류', '출고 요청 모달을 열 수 없습니다.');
            return false;
        }
    }

    /**
     * 모든 수주 목록 로드 함수
     * 출고 요청 가능한 모든 수주 목록을 로드하여 그리드에 직접 표시합니다.
     */
    async function loadAllSalesOrders() {
        try {
            console.log('모든 수주 목록 로드');
            await UIUtil.toggleLoading(true, '수주 정보를 불러오는 중...');

            // API 호출 - 수주 목록 조회
            const response = await ApiUtil.get(API_URLS.SALES_ORDER, {
                status: 'SO_CONFIRMED,SO_PLANNED,SO_COMPLETED' // 출고 가능한 상태
            });

            await UIUtil.toggleLoading(false);

            if (!response.success) {
                throw new Error('수주 목록을 가져오는데 실패했습니다: ' + response.message);
            }

            const salesOrders = response.data || [];

            // 그리드에 바로 표시
            if (soDetailGrid) {
                soDetailGrid.resetData(salesOrders);

                // 그리드가 제대로 표시되지 않는 문제 해결을 위한 딜레이 후 레이아웃 갱신
                setTimeout(() => {
                    soDetailGrid.refreshLayout();
                }, 100);
            }

            console.log('수주 목록 로드 완료:', salesOrders.length, '건');
            return true;
        } catch (error) {
            console.error('수주 목록 로드 중 오류:', error);
            await UIUtil.toggleLoading(false);
            await AlertUtil.showError('조회 오류', '수주 목록을 불러오는데 실패했습니다.');
            return false;
        }
    }

    /**
     * 출고 요청 생성 함수
     * 선택한 수주 정보를 바탕으로 출고 요청을 생성합니다.
     */
    async function createIssueRequest() {
        try {
            console.log('출고 요청 생성');

            // 수주 상세 그리드에서 선택된 항목 확인
            if (!soDetailGrid) {
                await AlertUtil.showWarning('알림', '수주 상세 정보가 로드되지 않았습니다.');
                return false;
            }

            const checkedRows = soDetailGrid.getCheckedRows();

            if (checkedRows.length === 0) {
                await AlertUtil.showWarning('알림', '출고 요청할 수주를 선택해주세요.');
                return false;




            }

            // 처리할 요청 목록 구성
            const requestItems = checkedRows.map(row => ({
                SO_CODE: row.SO_CODE,
                PDT_CODE: row.PDT_CODE,
                SO_QTY: row.SO_QTY,
                WHS_CODE: row.WHS_CODE
            }));

            // 출고 요청 처리
            const confirmed = await AlertUtil.showConfirm({
                title: '출고 요청 확인',
                text: `선택한 ${requestItems.length}건의 수주에 대해 출고 요청을 생성하시겠습니까?`,
                icon: 'question'
            });

            if (!confirmed) {
                return false;
            }

            // API 호출
            await UIUtil.toggleLoading(true, '출고 요청 처리 중...');

            let response;
            if (requestItems.length === 1) {
                // 단일 항목 처리
                response = await ApiUtil.post(API_URLS.REQUEST, requestItems[0]);


            } else {
                // 다건 처리
                response = await ApiUtil.post(API_URLS.REQUEST_BATCH, {
                    items: requestItems
                });
            }

            await UIUtil.toggleLoading(false);

            if (!response.success) {
                throw new Error('출고 요청에 실패했습니다: ' + response.message);
            }

            // 성공 처리
            await AlertUtil.showSuccess('출고 요청 완료', response.message || '출고 요청이 등록되었습니다.');

            // 모달 닫기
            const modal = bootstrap.Modal.getInstance(document.getElementById('issueRequestModal'));
            if (modal) {
                modal.hide();
            }

            // 목록 갱신
            await searchData();

            return true;
        } catch (error) {
            console.error('출고 요청 생성 중 오류:', error);
            await UIUtil.toggleLoading(false);
            await AlertUtil.showError('처리 오류', '출고 요청 처리 중 오류가 발생했습니다: ' + error.message);
            return false;
        }
    }

    /**
     * 상태별 출고요청 생성 함수
     * 
     * @param {string} status - 수주 상태 코드(쉼표로 구분된 여러 상태 가능)
     */
    async function createIssueRequestsByStatus(status) {
        try {
            console.log('상태별 출고요청 생성 시작. 상태:', status);

            // 확인 대화상자 표시
            const statusText = {
                'SO_CONFIRMED': '확정된',
                'SO_PLANNED': '계획된',
                'SO_COMPLETED': '완료된',
                'SO_CONFIRMED,SO_PLANNED,SO_COMPLETED': '모든 활성'
            } [status] || '';

            const confirmed = await AlertUtil.showConfirm({
                title: '상태별 출고요청',
                text: `${statusText} 수주에 대해 출고요청을 일괄 생성하시겠습니까?`,
                icon: 'question'
            });

            if (!confirmed) return false;

            // API 호출
            const response = await ApiUtil.processRequest(
                () => ApiUtil.post(API_URLS.REQUEST_BY_STATUS, {
                    status
                }), {
                    loadingMessage: '출고요청 생성 중...',
                    successMessage: '출고요청이 생성되었습니다.',
                    errorMessage: '출고요청 생성 중 오류가 발생했습니다.',
                    successCallback: async () => {
                        // 목록 갱신
                        await searchData();
                    }




                }
            );

            return response.success;
        } catch (error) {
            console.error('상태별 출고요청 생성 중 오류:', error);
            await AlertUtil.showError('처리 오류', '출고요청 생성 중 오류가 발생했습니다.');
            return false;
        }
    }

    /**
     * 검수 등록 함수
     * 선택한 출고 항목의 검수 등록을 처리합니다.
     */
    async function registerInspection() {
        try {
            console.log('검수 등록 처리');

            // 그리드에서 선택된 행 확인
            const grid = GridUtil.getGrid('pIssueGrid');
            if (!grid) {
                await AlertUtil.showWarning('알림', '그리드가 초기화되지 않았습니다.');
                return false;
            }

            const selectedRowKeys = grid.getCheckedRowKeys();

            if (selectedRowKeys.length === 0) {
                await AlertUtil.showWarning('알림', '검수 등록할 항목을 선택해주세요.');
                return false;
            }

            // 선택된 항목이 처리 가능한지 확인
            const validItems = [];
            const invalidItems = [];

            for (const rowKey of selectedRowKeys) {
                const row = grid.getRow(rowKey);

                // 출고대기 상태만 검수 처리 가능
                if (row.ISSUE_STATUS !== ISSUE_STATUS.WAITING) {
                    invalidItems.push({
                        code: row.ISSUE_CODE,
                        status: row.ISSUE_STATUS
                    });
                    continue;
                }

                // 유효한 항목 추가
                validItems.push({
                    issueNo: row.ISSUE_NO,
                    issueCode: row.ISSUE_CODE,
                    pdtCode: row.PDT_CODE,
                    updatedBy: 'SYSTEM' // 실제 구현에서는 로그인 사용자 ID로 변경
                });
            }

            if (validItems.length === 0) {
                let message = '선택한 항목 중 검수 등록할 수 있는 항목이 없습니다.\n';
                message += '출고대기 상태인 항목만 검수 등록할 수 있습니다.\n\n';

                message += '처리할 수 없는 항목:\n';
                invalidItems.forEach(item => {
                    message += `- ${item.code}: ${item.status}\n`;
                });

                await AlertUtil.showWarning('알림', message);
                return false;
            }

            // 확인 메시지
            let confirmMsg = `선택한 ${validItems.length}건의 항목을 검수 등록하시겠습니까?`;
            if (invalidItems.length > 0) {
                confirmMsg += `\n(${invalidItems.length}건은 처리할 수 없는 상태입니다.)`;
            }




            const confirmed = await AlertUtil.showConfirm({
                title: '검수 등록',
                text: confirmMsg,
                icon: 'question'



            });

            if (!confirmed) {
                return false;
            }

            // API 호출
            await UIUtil.toggleLoading(true, '검수 등록 처리 중...');

            let response;
            if (validItems.length === 1) {
                // 단일 항목 처리
                response = await ApiUtil.post(API_URLS.INSPECTION, validItems[0]);
            } else {
                // 다건 처리
                response = await ApiUtil.post(API_URLS.INSPECTION_BATCH, {
                    items: validItems
                });
            }

            await UIUtil.toggleLoading(false);

            if (!response.success) {
                throw new Error('검수 등록에 실패했습니다: ' + response.message);
            }

            // 성공 처리
            await AlertUtil.showSuccess(
                '검수 등록 완료',
                response.message || `${validItems.length}건의 검수 등록이 완료되었습니다. 검수 담당자가 검사를 진행합니다.`
            );

            // 목록 갱신
            await searchData();

            // 상세 정보 영역 숨기기
            hideDetailSection();

            return true;
        } catch (error) {
            console.error('검수 등록 처리 중 오류:', error);
            await UIUtil.toggleLoading(false);
            await AlertUtil.showError('처리 오류', '검수 등록 처리 중 오류가 발생했습니다: ' + error.message);
            return false;
        }
    }




    /**
     * 출고 확정 함수
     * 선택한 출고 요청을 확정하고 재고를 처리합니다.
     */
    async function confirmIssue() {
        try {
            console.log('출고 확정 처리');

            // 그리드에서 선택된 행 확인
            const grid = GridUtil.getGrid('pIssueGrid');
            if (!grid) {
                await AlertUtil.showWarning('알림', '그리드가 초기화되지 않았습니다.');
                return false;
            }

            const selectedRowKeys = grid.getCheckedRowKeys();

            if (selectedRowKeys.length === 0) {
                await AlertUtil.showWarning('알림', '출고 확정할 항목을 선택해주세요.');
                return false;
            }

            // 선택된 항목이 처리 가능한지 확인
            const validItems = [];
            const invalidItems = [];

            for (const rowKey of selectedRowKeys) {
                const row = grid.getRow(rowKey);

                // 검사합격 또는 출고대기 상태만 처리 가능
                if (row.ISSUE_STATUS !== ISSUE_STATUS.INSPECT_PASSED && row.ISSUE_STATUS !== ISSUE_STATUS.WAITING) {
                    invalidItems.push({
                        code: row.ISSUE_CODE,
                        status: row.ISSUE_STATUS
                    });
                    continue;
                }

                // 유효한 항목 추가
                validItems.push({
                    issueNo: row.ISSUE_NO,
                    updatedBy: 'SYSTEM' // 실제 구현에서는 로그인 사용자 ID로 변경
                });
            }

            if (validItems.length === 0) {
                let message = '선택한 항목 중 출고 확정할 수 있는 항목이 없습니다.\n';
                message += '검사합격 또는 출고대기 상태인 항목만 확정할 수 있습니다.\n\n';

                message += '처리할 수 없는 항목:\n';
                invalidItems.forEach(item => {
                    message += `- ${item.code}: ${item.status}\n`;
                });

                await AlertUtil.showWarning('알림', message);
                return false;
            }

            // 확인 메시지
            let confirmMsg = `선택한 ${validItems.length}건의 항목을 출고 확정하시겠습니까?`;
            if (invalidItems.length > 0) {
                confirmMsg += `\n(${invalidItems.length}건은 처리할 수 없는 상태입니다.)`;
            }

            const confirmed = await AlertUtil.showConfirm({
                title: '출고 확정',
                text: confirmMsg,
                icon: 'question'
            });

            if (!confirmed) {
                return false;
            }

            // API 호출
            await UIUtil.toggleLoading(true, '출고 확정 처리 중...');

            let response;
            if (validItems.length === 1) {
                // 단일 항목 처리
                response = await ApiUtil.post(API_URLS.PROCESS, validItems[0]);
            } else {
                // 다건 처리
                response = await ApiUtil.post(API_URLS.PROCESS_BATCH, {
                    items: validItems
                });
            }

            await UIUtil.toggleLoading(false);

            if (!response.success) {
                throw new Error('출고 확정에 실패했습니다: ' + response.message);
            }

            // 성공 처리
            await AlertUtil.showSuccess('출고 확정 완료', response.message || '출고 확정이 완료되었습니다.');

            // 목록 갱신
            await searchData();

            return true;
        } catch (error) {
            console.error('출고 확정 처리 중 오류:', error);
            await UIUtil.toggleLoading(false);
            await AlertUtil.showError('처리 오류', '출고 확정 처리 중 오류가 발생했습니다: ' + error.message);
            return false;
        }
    }

    /**
     * 출고 취소 함수
     * 선택한 출고 요청을 취소합니다.


     */
    async function cancelIssue() {
        try {
            console.log('출고 취소 처리');

            // 그리드에서 선택된 행 확인
            const grid = GridUtil.getGrid('pIssueGrid');
            if (!grid) {
                await AlertUtil.showWarning('알림', '그리드가 초기화되지 않았습니다.');
                return false;
            }

            const selectedRowKeys = grid.getCheckedRowKeys();

            if (selectedRowKeys.length === 0) {
                await AlertUtil.showWarning('알림', '취소할 출고 요청 항목을 선택해주세요.');
                return false;
            }

            // 선택된 항목이 취소 가능한지 확인
            const validItems = [];
            const invalidItems = [];

            for (const rowKey of selectedRowKeys) {
                const row = grid.getRow(rowKey);

                // 출고대기 상태만 취소 가능
                if (row.ISSUE_STATUS !== ISSUE_STATUS.WAITING) {
                    invalidItems.push({
                        code: row.ISSUE_CODE,
                        status: row.ISSUE_STATUS
                    });
                    continue;
                }

                // 유효한 항목 추가
                validItems.push({
                    issueNo: row.ISSUE_NO,
                    updatedBy: 'SYSTEM' // 실제 구현에서는 로그인 사용자 ID로 변경
                });
            }

            if (validItems.length === 0) {
                let message = '선택한 항목 중 취소할 수 있는 항목이 없습니다.\n';
                message += '출고대기 상태인 항목만 취소할 수 있습니다.\n\n';

                message += '처리할 수 없는 항목:\n';
                invalidItems.forEach(item => {
                    message += `- ${item.code}: ${item.status}\n`;
                });

                await AlertUtil.showWarning('알림', message);
                return false;
            }

            // 확인 메시지
            let confirmMsg = `선택한 ${validItems.length}건의 출고 요청을 취소하시겠습니까?`;
            if (invalidItems.length > 0) {
                confirmMsg += `\n(${invalidItems.length}건은 취소할 수 없는 상태입니다.)`;
            }

            const confirmed = await AlertUtil.showConfirm({
                title: '출고 취소',
                text: confirmMsg,
                icon: 'question'
            });

            if (!confirmed) {
                return false;


            }

            // API 호출
            await UIUtil.toggleLoading(true, '출고 취소 처리 중...');

            // 취소 처리
            // 1건씩 처리
            const results = [];
            for (const item of validItems) {
                try {
                    const response = await ApiUtil.post(API_URLS.CANCEL, item);
                    results.push({
                        issueNo: item.issueNo,
                        success: response.success,
                        message: response.message
                    });
                } catch (itemError) {
                    results.push({
                        issueNo: item.issueNo,
                        success: false,
                        message: itemError.message
                    });
                }
            }

            await UIUtil.toggleLoading(false);

            // 결과 처리
            const successCount = results.filter(r => r.success).length;

            if (successCount > 0) {
                let message = `${successCount}건의 출고 요청이 취소되었습니다.`;
                if (successCount < validItems.length) {
                    message += `\n(${validItems.length - successCount}건은 취소에 실패했습니다.)`;
                }

                await AlertUtil.showSuccess('출고 취소 완료', message);

                // 목록 갱신
                await searchData();
            } else {
                await AlertUtil.showError('취소 실패', '모든 항목의 취소가 실패했습니다.');
            }

            return successCount > 0;
        } catch (error) {
            console.error('출고 취소 처리 중 오류:', error);
            await UIUtil.toggleLoading(false);
            await AlertUtil.showError('처리 오류', '출고 취소 처리 중 오류가 발생했습니다: ' + error.message);
            return false;
        }
    }

    // =============================
    // 유틸리티 함수
    // =============================

    /**
     * 날짜 포맷팅 함수

     * 
     * @param {string|Date} date - 날짜 문자열 또는 Date 객체
     * @returns {string} 포맷팅된 날짜 문자열
     */
    function formatDate(date) {
        if (!date) return '-';




        try {
            const dateObj = new Date(date);
            return `${dateObj.getFullYear()}-${String(dateObj.getMonth() + 1).padStart(2, '0')}-${String(dateObj.getDate()).padStart(2, '0')}`;




        } catch (error) {
            return '-';


        }
    }

    /**
     * 날짜/시간 포맷팅 함수

     * 
     * @param {string|Date} datetime - 날짜/시간 문자열 또는 Date 객체
     * @returns {string} 포맷팅된 날짜/시간 문자열
     */
    function formatDateTime(datetime) {
        if (!datetime) return '-';

        try {
            const dateObj = new Date(datetime);
            return `${dateObj.getFullYear()}-${String(dateObj.getMonth() + 1).padStart(2, '0')}-${String(dateObj.getDate()).padStart(2, '0')} ${String(dateObj.getHours()).padStart(2, '0')}:${String(dateObj.getMinutes()).padStart(2, '0')}`;




        } catch (error) {
            return '-';


        }
    }

    /**
     * 그리드 인스턴스 반환 함수
     * 외부에서 그리드 인스턴스에 직접 접근할 수 있습니다.
     * 
     * @returns {Object} 그리드 인스턴스
     */
    function getGrid() {
        return pIssueGrid;




    }

    // =============================
    // 공개 API - 외부에서 접근 가능한 메서드
    // =============================
    return {
        // 초기화 및 기본 기능
        init, // 모듈 초기화

        // 데이터 관련 함수
        searchData, // 데이터 검색
        openIssueRequestModal, // 출고 요청 모달 열기
        createIssueRequest, // 출고 요청 생성
        createIssueRequestsByStatus, // 상태별 출고 요청 생성
        registerInspection, // 검수 등록
        confirmIssue, // 출고 확정
        cancelIssue, // 출고 취소
        loadHistoryData, // 이력 정보 로드
        loadAllSalesOrders, // 모든 수주 목록 로드

        // 유틸리티 함수
        getGrid, // 그리드 인스턴스 반환



    };
})();

// =============================
// DOM 로드 시 초기화
// =============================
document.addEventListener('DOMContentLoaded', async function() {
    try {
        // 제품 출고관리 모듈 초기화
        await ProductsIssueManager.init();



    } catch (error) {
        console.error('초기화 중 오류 발생:', error);
        if (window.AlertUtil) {
            await AlertUtil.showError('초기화 오류', '제품 출고관리 초기화 중 오류가 발생했습니다.');
        } else {
            alert('제품 출고관리 초기화 중 오류가 발생했습니다.');
        }
    }
});