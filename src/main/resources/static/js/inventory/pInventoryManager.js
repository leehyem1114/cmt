/**
 * 제품 재고관리 - 재고 정보 관리 모듈
 * 
 * 제품 재고 정보의 조회, FIFO 기능을 담당하는 관리 모듈입니다.
 * 
 * @version 2.0.0
 * @since 2025-04-29
 */
const ProductsInventoryManager = (function() {
    // =============================
    // 모듈 내부 변수 - 필요에 따라 변경하세요
    // =============================

    // 그리드 인스턴스 참조
    let pInventoryGrid;

    // API URL 상수 정의
    const API_URLS = {
        LIST: '/api/productsinventory/list',
        SAVE: '/api/productsinventory/save',
        DELETE: '/api/productsinventory/delete',
        FIFO: '/api/productsinventory/fifo',
        FIFO_HISTORY: '/api/productsinventory/fifo-history',
        CONSUME: '/api/productsinventory/consume',
        EXCEL: {
            UPLOAD: '/api/productsinventory/excel/upload',
            DOWNLOAD: '/api/productsinventory/excel/download'
        }
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
            console.log('제품 재고관리 초기화를 시작합니다.');

            // 그리드 초기화
            await initGrid();

            // 이벤트 리스너 등록
            await registerEvents();
            
            // 그리드 검색 초기화
            initGridSearch();
            
            // 엑셀 기능 초기화
            initExcelFeatures();

            console.log('제품 재고관리 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('초기화 중 오류 발생:', error);
            await AlertUtil.showError('초기화 오류', '제품 재고관리 초기화 중 오류가 발생했습니다.');
        }
    }

    /**
     * 이벤트 리스너 등록 함수
     * 버튼 클릭 및 엔터키 검색 등의 이벤트 리스너를 등록합니다.
     */
    async function registerEvents() {
        try {
            // UIUtil을 사용하여 이벤트 리스너 등록
            await UIUtil.registerEventListeners({
                'pInventorySearchBtn': searchData,
				'pInventoryGenerateBtn': generateInventoryData // 기본 재고 생성 버튼 (추가)
            });

            // 엔터키 검색 이벤트 등록
            await UIUtil.bindEnterKeySearch('pInventoryInput', searchData);
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
                gridId: 'pInventoryGrid',
                searchInputId: 'pInventoryInput',
                autoSearch: true, // 입력 시 자동 검색
                beforeSearch: function() {
                    console.log('그리드 검색 시작');
                    return true;
                },
                afterSearch: function(filteredData) {
                    console.log('그리드 검색 완료, 결과:', filteredData.length, '건');
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
                buttonId: 'pInventoryExcelDownBtn', 
                gridId: 'pInventoryGrid',
                fileName: 'products-inventory-data.xlsx',
                sheetName: '제품재고정보',
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
            const gridElement = document.getElementById('pInventoryGrid');
            if (!gridElement) {
                throw new Error('pInventoryGrid 요소를 찾을 수 없습니다. HTML을 확인해주세요.');
            }

            // 서버에서 받은 데이터 활용
            const gridData = window.pInventoryList || [];
            console.log('초기 데이터:', gridData.length, '건');

            // 그리드 생성 - GridUtil 사용
            pInventoryGrid = GridUtil.registerGrid({
                id: 'pInventoryGrid',
                columns: [
                    {
                        header: '제품코드',
                        name: 'PDT_CODE',
                        editor: false,
                        sortable: true
                    },
                    {
                        header: '제품명',
                        name: 'PDT_NAME',
                        editor: false,
                        sortable: true
                    },
                    {
                        header: '창고코드',
                        name: 'WAREHOUSE_CODE',
                        editor: false,
                        sortable: true
                    },
                    {
                        header: '위치코드',
                        name: 'LOCATION_CODE',
                        editor: false,
                        sortable: true
                    },
                    {
                        header: '현재수량',
                        name: 'CURRENT_QTY',
                        editor: false,
                        sortable: true,
                        align: 'right'
                    },
                    {
                        header: '할당수량',
                        name: 'ALLOCATED_QTY',
                        editor: false,
                        sortable: true,
                        align: 'right'
                    },
                    {
                        header: '가용수량',
                        name: 'AVAILABLE_QTY',
                        editor: false, // 자동 계산되므로 편집 불가
                        sortable: true,
                        align: 'right',
                        formatter: function({ value }) {
                          const isLow = parseInt(value) <= 100;
                          const style = isLow ? 'color:red; font-weight:bold;' : 'color:black;';
                          return `<span style="${style}">${value}</span>`;
                        }
                    },
                    {
                        header: '마지막이동일',
                        name: 'LAST_MOVEMENT_DATE',
                        editor: false,
                        sortable: true,
                        formatter: function({ value }) {
                            if (!value) return '';
                            const date = new Date(value);
                            return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
                        }
                    },
                    {
                        header: 'FIFO',
                        name: 'fifoAction',
                        width: 100,
                        align: 'center',
                        formatter: function({row}) {
                            const pdtCode = row.PDT_CODE;
                            return `<button class="btn btn-outline-primary btn-sm fifo-btn" 
                                    data-pdt-code="${pdtCode}">
                                    상세
                                 </button>`;
                        }
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
                    rowHeaders: ['rowNum']
                }
            });
            
            // FIFO 버튼 클릭 이벤트 처리
            pInventoryGrid.on('click', function(ev) {
                console.log('그리드 클릭 이벤트:', ev);
                
                const event = ev.nativeEvent || ev.originalEvent || ev.event || ev;
                console.log('event 객체:', event);
                
                const targetElement = event.target || event.srcElement;
                console.log('targetElement:', targetElement);
                
                if (targetElement && targetElement.classList.contains('fifo-btn')) {
                    const { rowKey } = ev;
                    const row = pInventoryGrid.getRow(rowKey);
                    const pdtCode = row.PDT_CODE;
                    
                    console.log('FIFO 버튼 클릭됨:', pdtCode);
                    ProductsInventoryManager.showFIFODetail(pdtCode);
                }
            });
            
            // 그리드 원본 데이터 저장 (검색 기능 위해)
            GridSearchUtil.updateOriginalData('pInventoryGrid', gridData);

            console.log('그리드 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('그리드 초기화 오류:', error);
            throw error;
        }
    }

    // =============================
    // FIFO 관련 함수
    // =============================

    /**
     * FIFO 상세 정보 표시
     */
    async function showFIFODetail(pdtCode) {
        console.log('showFIFODetail 호출됨:', pdtCode);
        console.log('API URL:', `${API_URLS.FIFO}/${pdtCode}`);
        
        try {
            document.getElementById('selectedPdtCode').textContent = pdtCode;
            console.log('selectedPdtCode 설정 완료');

            // FIFO 상세 정보 조회
            const response = await ApiUtil.get(`${API_URLS.FIFO}/${pdtCode}`);
            console.log('API 응답:', response);

            if (response.success) {
                console.log('response.data.INVENTORY:', response.data.INVENTORY);
                console.log('response.data.STOCK_LIST:', response.data.STOCK_LIST);
                
                displayFIFODetail(response.data);
                
                // FIFO 상세 영역 표시
                const fifoDetailSection = document.getElementById('fifoDetailSection');
                if (fifoDetailSection) {
                    fifoDetailSection.style.display = 'block';
                    console.log('fifoDetailSection 표시됨');
                    // 스크롤 이동
                    fifoDetailSection.scrollIntoView({
                        behavior: 'smooth'
                    });
                } else {
                    console.error('fifoDetailSection 요소를 찾을 수 없음');
                }
            } else {
                console.log('FIFO 조회 실패:', response);
                await AlertUtil.showError('FIFO 조회 실패', 'FIFO 정보 조회에 실패했습니다.');
            }
        } catch (error) {
            console.error('showFIFODetail 에러 상세:', error);
            console.error('Error stack:', error.stack);
            await AlertUtil.showError('오류', 'FIFO 정보 조회 중 오류가 발생했습니다.');
        }
    }

    /**
     * FIFO 상세 정보 표시
     */
    function displayFIFODetail(data) {
        console.log('displayFIFODetail 실행됨, data:', data);
        
        // FIFO 큐 시각화
        const queueVisual = document.getElementById('queueVisual');
        if (!queueVisual) {
            console.error('queueVisual 요소를 찾을 수 없음');
            return;
        }
        queueVisual.innerHTML = '';

        if (data.STOCK_LIST && data.STOCK_LIST.length > 0) {
            console.log('STOCK_LIST 개수:', data.STOCK_LIST.length);
            data.STOCK_LIST.forEach((stock, index) => {
                console.log(`Stock ${index}:`, stock);
                
                const remaining = parseFloat(stock.ISSUED_QTY);
                const isActive = stock.STATUS === '사용중';
                const isNext = !isActive && remaining > 0 && index === 1;

                const queueItem = document.createElement('div');
                queueItem.className = `queue-item ${isActive ? 'active' : ''} ${isNext ? 'next' : ''}`;

                queueItem.innerHTML = `
                    <div class="queue-number">${stock.FIFO_ORDER}순위</div>
                    <div class="queue-date">${formatDate(stock.ISSUE_DATE)}</div>
                    <div class="queue-progress">
                        <div>입고LOT번호: ${stock.ISSUE_NO}</div>
                        <small class="text-muted">남은수량: ${Number(remaining).toLocaleString()}</small>
                        <div class="progress-bar-custom">
                            <div class="${isActive ? 'progress-fill-blue' : 'progress-fill-yellow'}" 
                                 style="width: ${remaining > 0 ? '100%' : '0%'};"></div>
                        </div>
                    </div>
                    <div style="color: ${isActive ? '#0d6efd' : isNext ? '#ffc107' : '#6c757d'};">
                        ${stock.STATUS}
                    </div>
                `;

                queueVisual.appendChild(queueItem);
            });
            console.log('queueVisual 업데이트 완료');
        } else {
            console.log('STOCK_LIST가 없거나 비어있음');
        }

        // 상세 테이블 표시
        const tableBody = document.getElementById('fifoDetailTableBody');
        if (!tableBody) {
            console.error('fifoDetailTableBody 요소를 찾을 수 없음');
            return;
        }
        tableBody.innerHTML = '';

        if (data.STOCK_LIST && data.STOCK_LIST.length > 0) {
            data.STOCK_LIST.forEach(stock => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${stock.FIFO_ORDER}</td>
                    <td>${formatDate(stock.ISSUE_DATE)}</td>
                    <td>${stock.ISSUE_NO}</td>
                    <td>${Number(stock.ISSUED_QTY).toLocaleString()}</td>
                    <td>${Number(stock.ISSUED_QTY).toLocaleString()}</td>
                    <td>
                        <span class="badge bg-${getBadgeColor(stock.STATUS)}">${stock.STATUS}</span>
                    </td>
                `;
                tableBody.appendChild(row);
            });
            console.log('테이블 업데이트 완료');
        } else {
            console.log('테이블에 표시할 데이터가 없음');
        }
    }

    /**
     * FIFO 이력 로드
     */
    async function loadFIFOHistory(pdtCode) {
        try {
            const response = await ApiUtil.get(`${API_URLS.FIFO_HISTORY}/${pdtCode}`);

            if (response.success) {
                displayFIFOHistory(response.data);
            } else {
                await AlertUtil.showError('이력 조회 실패', 'FIFO 이력 조회에 실패했습니다.');
            }
        } catch (error) {
            console.error('FIFO 이력 조회 오류:', error);
            await AlertUtil.showError('오류', 'FIFO 이력 조회 중 오류가 발생했습니다.');
        }
    }

    /**
     * FIFO 이력 표시
     */
    function displayFIFOHistory(historyData) {
        const tableBody = document.getElementById('fifoHistoryTableBody');
        tableBody.innerHTML = '';

        if (historyData && historyData.length > 0) {
            historyData.forEach(history => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${formatDateTime(history.UPDATED_DATE)}</td>
                    <td>${history.ACTION_TYPE}</td>
                    <td>${history.ACTION_DESCRIPTION}</td>
                    <td>${history.UPDATED_BY || '-'}</td>
                `;
                tableBody.appendChild(row);
            });
        } else {
            tableBody.innerHTML = '<tr><td colspan="4" class="text-center">이력 정보가 없습니다.</td></tr>';
        }
    }

    /**
     * 탭 전환
     */
    function switchTab(tabName) {
        // 탭 버튼 상태 변경
        document.querySelectorAll('.tab-button').forEach(btn => {
            btn.classList.remove('active');
        });
        event.target.classList.add('active');

        // 탭 콘텐츠 표시
        document.getElementById('queueTab').style.display = tabName === 'queue' ? 'block' : 'none';
        document.getElementById('historyTab').style.display = tabName === 'history' ? 'block' : 'none';

        // 이력 탭으로 전환 시 데이터 로드
        if (tabName === 'history') {
            const pdtCode = document.getElementById('selectedPdtCode').textContent;
            loadFIFOHistory(pdtCode);
        }
    }

    // =============================
    // 유틸리티 함수
    // =============================

    /**
     * 날짜 포맷팅
     */
    function formatDate(dateString) {
        if (!dateString) return '-';
        const date = new Date(dateString);
        return date.toISOString().split('T')[0];
    }

    /**
     * 날짜시간 포맷팅
     */
    function formatDateTime(dateString) {
        if (!dateString) return '-';
        const date = new Date(dateString);
        return date.toLocaleString('ko-KR');
    }

    /**
     * 상태별 배지 색상 반환
     */
    function getBadgeColor(status) {
        switch (status) {
            case '사용중':
                return 'primary';
            case '대기':
                return 'warning';
            case '소진':
                return 'secondary';
            default:
                return 'light';
        }
    }

    /**
     * 데이터 검색 함수
     */
    async function searchData() {
        try {
            const keyword = document.getElementById('pInventoryInput').value;
            console.log('데이터 검색 시작. 검색어:', keyword);

            // API 호출
            const response = await ApiUtil.getWithLoading(
                API_URLS.LIST, 
                {
                    keyword: keyword
                },
                '데이터 검색 중...'
            );

            // 응답 데이터 처리
            const data = Array.isArray(response) ? response : (response.data || []);

            // 그리드 데이터 설정
            const grid = GridUtil.getGrid('pInventoryGrid');
            if (grid) {
                grid.resetData(data);
                
                // 그리드 원본 데이터 업데이트
                GridSearchUtil.updateOriginalData('pInventoryGrid', data);
            }

            console.log('데이터 검색 완료. 결과:', data.length, '건');
            return data;
        } catch (error) {
            console.error('데이터 검색 중 오류:', error);
            await AlertUtil.showError('검색 오류', '데이터 검색 중 오류가 발생했습니다.');
            throw error;
        }
    }
	
	// 기본 재고 데이터 생성 함수 
	async function generateInventoryData() {
	    try {
	        // 확인 대화상자 표시
	        const confirmed = await AlertUtil.showConfirm(
	            '기본 재고 데이터 생성 확인',
	            '아직 재고 정보가 없는 제품에 대한 기본 재고 데이터를 생성하시겠습니까?\n(이미 재고 정보가 있는 제품은 영향 없음)'
	        );
	        
	        if (!confirmed) return false;
	        
	        // API 호출
	        const response = await ApiUtil.processRequest(
	            () => ApiUtil.post('/api/productsinventory/generate-data'), {
	                loadingMessage: '재고 데이터 생성 중...',
	                successMessage: "기본 재고 데이터 생성이 완료되었습니다.",
	                errorMessage: "재고 데이터 생성 중 오류가 발생했습니다.",
	                successCallback: searchData // 생성 후 목록 새로고침
	            }
	        );
	        
	        if (response.success) {
	            // 상세 결과 표시
	            const created = response.data.createdCount || 0;
	            const failed = (response.data.failedItems || []).length;
	            
	            await AlertUtil.showSuccess(
	                '기본 재고 생성 완료', 
	                `${created}개 제품의 기본 재고 정보가 생성되었습니다.${failed > 0 ? ` (${failed}개 실패)` : ''}`
	            );
	            return true;
	        } else {
	            return false;
	        }
	    } catch (error) {
	        console.error('재고 데이터 생성 중 오류:', error);
	        await AlertUtil.showError('생성 오류', '재고 데이터 생성 중 오류가 발생했습니다.');
	        return false;
	    }
	}

    /**
     * 그리드 인스턴스 반환 함수
     */
    function getGrid() {
        return pInventoryGrid;
    }

    // =============================
    // 공개 API - 외부에서 접근 가능한 메서드
    // =============================
    return {
        // 초기화 및 기본 기능
        init,
        searchData,
		generateInventoryData,
        getGrid,
        showFIFODetail,
        loadFIFOHistory,
        switchTab
    };
})();

// =============================
// DOM 로드 시 초기화
// =============================
document.addEventListener('DOMContentLoaded', async function() {
    try {
        // 제품 재고관리 초기화
        await ProductsInventoryManager.init();
    } catch (error) {
        console.error('초기화 중 오류 발생:', error);
        if (window.AlertUtil) {
            await AlertUtil.showError('초기화 오류', '제품 재고관리 초기화 중 오류가 발생했습니다.');
        } else {
            alert('제품 재고관리 초기화 중 오류가 발생했습니다.');
        }
    }
});