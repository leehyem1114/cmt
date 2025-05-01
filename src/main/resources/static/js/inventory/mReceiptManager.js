/**
 * 원자재 입고관리 - 입고 정보 관리 모듈
 * 
 * 원자재 입고정보의 조회, 확정, 검수 기능을 담당하는 관리 모듈입니다.
 * 
 * @version 3.0.0
 * @since 2025-04-24
 */
const MaterialReceiptManager = (function() {
    // =============================
    // 모듈 내부 변수 - 필요에 따라 변경하세요
    // =============================

    // 그리드 인스턴스 참조
    let mReceiptGrid;
    
    // 현재 선택된 입고정보
    let selectedReceipt = null;
    
    // 탭 컨트롤러 참조
    let tabController = null;

    // API URL 상수 정의
    const API_URLS = {
        LIST: '/api/materialreceipt/list',               // 입고 목록 조회
        DETAIL: (receiptNo) => `/api/materialreceipt/detail/${receiptNo}`,  // 상세 정보 조회
        HISTORY: (receiptNo) => `/api/materialreceipt/history/${receiptNo}`,  // 이력 정보 조회
        INSPECTION: `/api/materialreceipt/inspection`,   // 검수 등록
        INSPECTION_INFO: (receiptNo) => `/api/materialreceipt/inspection/${receiptNo}`,  // 검수 정보 조회
        CONFIRM: '/api/materialreceipt/confirm',         // 입고 확정
        EXCEL: {
            DOWNLOAD: '/api/materialreceipt/excel/download'  // 엑셀 다운로드 API URL
        }
    };
    
    // 입고 상태 정의
    const RECEIPT_STATUS = {
        WAITING: '입고대기',
        INSPECTING: '검수중',
		INSPECT_PASSED: '검사 합격',
		INSPECT_FAILED: '검사 불합격',
        COMPLETED: '입고완료',
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
            console.log('원자재 입고관리 초기화를 시작합니다.');

            // 그리드 초기화
            await initGrid();

            // 이벤트 리스너 등록
            await registerEvents();
            
            // 그리드 검색 초기화
            initGridSearch();
            
            // 엑셀 기능 초기화
            initExcelFeatures();

            console.log('원자재 입고관리 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('초기화 중 오류 발생:', error);
            await AlertUtil.showError('초기화 오류', '원자재 입고관리 초기화 중 오류가 발생했습니다.');
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
                'mReceiptConfirmBtn': confirmReceipt,        // 입고확정 버튼
                'mReceiptInspectionBtn': openInspectionModal, // 검수등록 버튼
                'mReceiptSearchBtn': searchData              // 데이터 검색 버튼
            });

            // 엔터키 검색 이벤트 등록
            await UIUtil.bindEnterKeySearch('mReceiptInput', searchData);
            
            // 탭 이벤트 등록 - 이력정보 탭
            const historyTab = document.getElementById('history-tab');
            if (historyTab) {
                historyTab.addEventListener('shown.bs.tab', function(e) {
                    if (selectedReceipt) {
                        loadHistoryData(selectedReceipt.RECEIPT_NO);
                    }
                });
            }
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
                gridId: 'mReceiptGrid',
                searchInputId: 'mReceiptInput',
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
                buttonId: 'mReceiptExcelDownBtn', 
                gridId: 'mReceiptGrid',
                fileName: 'material-receipt-data.xlsx',
                sheetName: '원자재입고정보',
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
            const gridElement = document.getElementById('mReceiptGrid');
            if (!gridElement) {
                throw new Error('mReceiptGrid 요소를 찾을 수 없습니다. HTML을 확인해주세요.');
            }

            // 서버에서 받은 데이터 활용
            const gridData = window.mReceipt || [];
            console.log('초기 데이터:', gridData ? gridData.length : 0, '건');

            // 그리드 생성 - GridUtil 사용
            mReceiptGrid = GridUtil.registerGrid({
                id: 'mReceiptGrid',
                columns: [
                    {
                        header: '입고 코드',
                        name: 'RECEIPT_CODE',
                        sortable: true
                    },
                    {
                        header: '발주 코드',
                        name: 'PO_CODE',
                        sortable: true
                    },
                    {
                        header: '자재 코드',
                        name: 'MTL_CODE',
                        sortable: true
                    },
                    {
                        header: '자재명',
                        name: 'MTL_NAME',
                        sortable: true
                    },
                    {
                        header: 'LOT 번호',
                        name: 'LOT_NO',
                        sortable: true
                    },
                    {
                        header: '입고일',
                        name: 'RECEIPT_DATE',
                        sortable: true,
                        formatter: function(obj) {
                            if (!obj.value) return '-';
                            const date = new Date(obj.value);
                            return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
                        }
                    },
                    {
                        header: '입고 수량',
                        name: 'RECEIVED_QTY',
                        sortable: true
                    },
                    {
                        header: '입고 상태',
                        name: 'RECEIPT_STATUS',
                        sortable: true,
                        formatter: function(obj) {
                            const status = obj.value || RECEIPT_STATUS.WAITING;
                            let badgeClass = 'badge-waiting';
                            
                            if (status === RECEIPT_STATUS.INSPECTING) {
                                badgeClass = 'badge-inspecting';
                            } else if (status === RECEIPT_STATUS.COMPLETED) {
                                badgeClass = 'badge-completed';
                            } else if(status === RECEIPT_STATUS.INSPECT_PASSED){
								badgeClass = 'badge-inspectPass'
							}else if(status === RECEIPT_STATUS.INSPECT_FAILED){
								badgeClass = 'badge-canceled';
							} else if (status === RECEIPT_STATUS.CANCELED) {
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
                        header: '위치 코드',
                        name: 'LOCATION_CODE',
                        sortable: true
                    },
                    {
                        header: '담당자',
                        name: 'RECEIVER',
                        sortable: true
                    },
                    {
                        header: '입고번호',
                        name: 'RECEIPT_NO',
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
            const rows = mReceiptGrid.getData();
            rows.forEach((row, index) => {
                if (!row.RECEIPT_STATUS) {
                    mReceiptGrid.setValue(row.rowKey, 'RECEIPT_STATUS', RECEIPT_STATUS.WAITING);
                }
            });
            
            // 행 클릭 이벤트 등록 - 상세 정보 표시
            GridUtil.onRowClick('mReceiptGrid', async function(rowData, rowKey, columnName) {
                if (!rowData) return;
                
                // 현재 선택된 입고 정보 저장
                selectedReceipt = rowData;
                
                // 상세 정보 조회 및 표시
                await loadReceiptDetail(rowData.RECEIPT_NO);
            });
            
            // 그리드 원본 데이터 저장 (검색 기능 위해 추가)
            GridSearchUtil.updateOriginalData('mReceiptGrid', gridData);

            console.log('그리드 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('그리드 초기화 오류:', error);
            throw error;
        }
    }

    // =============================
    // 상세 정보 관련 함수
    // =============================
    
    /**
     * 입고 상세 정보 로드 함수
     * 선택된 입고 정보의 상세 내용을 조회하고 표시합니다.
     * 
     * @param {number} receiptNo - 입고 번호
     */
    async function loadReceiptDetail(receiptNo) {
        try {
            // 로딩 표시
            await UIUtil.toggleLoading(true, '상세 정보를 불러오는 중...');
            
            // API 호출
            const response = await ApiUtil.get(API_URLS.DETAIL(receiptNo));
            
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
     * @param {Object} detailData - 입고 상세 정보 데이터
     */
    function displayDetailInfo(detailData) {
        // 기본 정보 탭 데이터 설정
        document.getElementById('receiptNo').textContent = detailData.RECEIPT_NO || '';
        document.getElementById('receiptCode').textContent = detailData.RECEIPT_CODE || '';
        document.getElementById('mtlCode').textContent = detailData.MTL_CODE || '';
        document.getElementById('mtlName').textContent = detailData.MTL_NAME || '';
        document.getElementById('receivedQty').textContent = detailData.RECEIVED_QTY || '';
        
        // 날짜 포맷팅
        let receiptDate = '-';
        if (detailData.RECEIPT_DATE) {
            const date = new Date(detailData.RECEIPT_DATE);
            receiptDate = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
        }
        document.getElementById('receiptDate').textContent = receiptDate;
        
        // 상태에 따라 배지 스타일 적용
        const status = detailData.RECEIPT_STATUS || RECEIPT_STATUS.WAITING;
        let badgeClass = 'badge-waiting';
        
        if (status === RECEIPT_STATUS.INSPECTING) {
            badgeClass = 'badge-inspecting';
        } else if (status === RECEIPT_STATUS.COMPLETED) {
            badgeClass = 'badge-completed';
        } else if (status === RECEIPT_STATUS.INSPECT_PASSED){
            badgeClass = 'badge-inspectPass';
		} else if (status === RECEIPT_STATUS.INSPECT_FAILED){
            badgeClass = 'badge-canceled';
		} else if (status === RECEIPT_STATUS.CANCELED) {
            badgeClass = 'badge-canceled';
		}
        
        document.getElementById('receiptStatus').innerHTML = `<span class="badge ${badgeClass}">${status}</span>`;
        document.getElementById('poCode').textContent = detailData.PO_CODE || '';
        document.getElementById('warehouseInfo').textContent = 
            `${detailData.WAREHOUSE_CODE || ''} / ${detailData.LOCATION_CODE || ''}`;
        document.getElementById('receiver').textContent = detailData.RECEIVER || '';
        
//        // 검수 정보 탭 설정
//        if (detailData.hasInspection) {
//            // 검수 정보가 있는 경우 검수 정보 표시
//            displayInspectionInfo(detailData.inspectionData);
//        } else {
//            // 검수 정보가 없는 경우 안내 메시지 표시
//            document.getElementById('inspectionContent').innerHTML = `
//                <div class="alert alert-info">
//                    <i class="bi bi-info-circle"></i> 검수 정보가 없습니다. 검수등록 버튼을 클릭하여 검수 정보를 등록해주세요.
//                </div>
//            `;
//        }
//        
//        // LOT 정보 탭 설정
//        if (detailData.lotData && detailData.lotData.length > 0) {
//            displayLotInfo(detailData.lotData);
//        } else {
//            document.getElementById('lotInfoBody').innerHTML = `
//                <tr>
//                    <td colspan="4" class="text-center">LOT 정보가 없습니다.</td>
//                </tr>
//            `;
//        }
//        
//        // 위치 정보 탭 설정
//        if (detailData.locationData && detailData.locationData.length > 0) {
//            displayLocationInfo(detailData.locationData);
//        } else {
//            document.getElementById('locationInfoBody').innerHTML = `
//                <tr>
//                    <td colspan="5" class="text-center">위치 정보가 없습니다.</td>
//                </tr>
//            `;
//        }
        
        // 이력 정보는 탭 선택 시 별도 로드
    }
    
    /**
     * 검수 정보 표시 함수
     * 
     * @param {Object} inspectionData - 검수 정보 데이터
     */
    function displayInspectionInfo(inspectionData) {
        if (!inspectionData) {
            return;
        }
        
        // 검수 정보 HTML 구성
        const inspectionHtml = `
            <table class="table table-bordered">
                <tr>
                    <th style="width: 20%">검수번호</th>
                    <td>${inspectionData.INSP_NO || ''}</td>
                    <th style="width: 20%">검수일자</th>
                    <td>${formatDate(inspectionData.INSP_DATE)}</td>
                </tr>
                <tr>
                    <th>검수수량</th>
                    <td>${inspectionData.INSP_QTY || ''}</td>
                    <th>검수자</th>
                    <td>${inspectionData.INSPECTOR || ''}</td>
                </tr>
                <tr>
                    <th>합격수량</th>
                    <td>${inspectionData.PASS_QTY || ''}</td>
                    <th>불합격수량</th>
                    <td>${inspectionData.FAIL_QTY || ''}</td>
                </tr>
                <tr>
                    <th>검수결과</th>
                    <td colspan="3">${getInspectionResultText(inspectionData.INSP_RESULT)}</td>
                </tr>
                <tr>
                    <th>검수의견</th>
                    <td colspan="3">${inspectionData.INSP_COMMENTS || '-'}</td>
                </tr>
            </table>
        `;
        
        document.getElementById('inspectionContent').innerHTML = inspectionHtml;
    }
    
    /**
     * LOT 정보 표시 함수
     * 
     * @param {Array} lotData - LOT 정보 데이터 배열
     */
    function displayLotInfo(lotData) {
        if (!lotData || !lotData.length) {
            return;
        }
        
        // LOT 정보 HTML 구성
        let html = '';
        lotData.forEach(lot => {
            html += `
                <tr>
                    <td>${lot.LOT_NO || ''}</td>
                    <td>${lot.LOT_QTY || ''}</td>
                    <td>${formatDate(lot.LOT_DATE)}</td>
                    <td>${lot.LOT_STATUS || ''}</td>
                </tr>
            `;
        });
        
        document.getElementById('lotInfoBody').innerHTML = html;
    }
    
    /**
     * 위치 정보 표시 함수
     * 
     * @param {Array} locationData - 위치 정보 데이터 배열
     */
    function displayLocationInfo(locationData) {
        if (!locationData || !locationData.length) {
            return;
        }
        
        // 위치 정보 HTML 구성
        let html = '';
        locationData.forEach(loc => {
            html += `
                <tr>
                    <td>${loc.WAREHOUSE_CODE || ''}</td>
                    <td>${loc.WAREHOUSE_NAME || ''}</td>
                    <td>${loc.LOCATION_CODE || ''}</td>
                    <td>${loc.LOCATION_NAME || ''}</td>
                    <td>${loc.STOCK_QTY || ''}</td>
                </tr>
            `;
        });
        
        document.getElementById('locationInfoBody').innerHTML = html;
    }
    
	/**
	     * 이력 정보 로드 함수
	     * 선택된 입고의 이력 정보를 로드하고 표시합니다.
	     * 
	     * @param {number} receiptNo - 입고 번호
	     * @returns {Promise<boolean>} 로드 성공 여부
	     */
	    async function loadHistoryData(receiptNo) {
	        try {
	            // 로딩 표시
	            await UIUtil.toggleLoading(true, '이력 정보를 불러오는 중...');
	            
	            // API 호출
	            const response = await ApiUtil.get(API_URLS.HISTORY(receiptNo));
	            
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
	        
	        // 선택된 입고 정보 초기화
	        selectedReceipt = null;
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
	            const keyword = document.getElementById('mReceiptInput').value;
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
	            const grid = GridUtil.getGrid('mReceiptGrid');
	            if (grid) {
	                grid.resetData(data);
	                
	                // 그리드 원본 데이터 업데이트 (검색 기능 위해 추가)
	                GridSearchUtil.updateOriginalData('mReceiptGrid', data);
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
	     * 입고 확정 함수 - 검수 완료된 항목만 처리
	     * 선택된 입고 정보를 확정 처리합니다.
	     */
	    async function confirmReceipt() {
	        try {
	            // 선택된 행 ID 확인
	            const grid = GridUtil.getGrid('mReceiptGrid');
	            const selectedRowKeys = grid.getCheckedRowKeys();
	            
	            if (selectedRowKeys.length === 0) {
	                await AlertUtil.showWarning('알림', '확정할 입고 항목을 선택해주세요.');
	                return false;
	            }
	            
	            // 선택된 입고 정보 수집 및 유효성 검증
	            const validItems = [];
	            const invalidItems = [];
	            
	            for (const rowKey of selectedRowKeys) {
	                const receiptData = grid.getRow(rowKey);
	                
	                // 이미 확정된 항목 제외
	                if (receiptData.RECEIPT_STATUS === RECEIPT_STATUS.COMPLETED) {
	                    invalidItems.push({
	                        code: receiptData.RECEIPT_CODE,
	                        reason: '이미 입고 완료된 항목'
	                    });
	                    continue;
	                }
	                
	                // 취소된 항목 제외
	                if (receiptData.RECEIPT_STATUS === RECEIPT_STATUS.CANCELED) {
	                    invalidItems.push({
	                        code: receiptData.RECEIPT_CODE,
	                        reason: '취소된 항목'
	                    });
	                    continue;
	                }
	                
	                // 검수가 완료된 항목만 입고완료 할수있도록 확인
	                // 검수중인 항목은 검수 정보 확인
	                if (receiptData.RECEIPT_STATUS === RECEIPT_STATUS.INSPECT_PASSED) {
	                    try {
	                        // 검수 완료 여부 확인
	                        const response = await ApiUtil.get(
	                            API_URLS.INSPECTION_INFO(receiptData.RECEIPT_NO)
	                        );
	                        
	                        if (!response.success || !response.data) {
	                            invalidItems.push({
	                                code: receiptData.RECEIPT_CODE,
	                                reason: '검수 정보가 없는 항목'
	                            });
	                            continue;
	                        }
	                        
	                        // 검수 결과 확인 (합격 또는 조건부 합격만 처리 가능)
	                        const inspResult = response.data.INSP_RESULT;
	                        if (inspResult !== 'PASS' && inspResult !== 'CONDITIONAL_PASS') {
	                            invalidItems.push({
	                                code: receiptData.RECEIPT_CODE,
	                                reason: '검수 중이거나 불합격 항목'
	                            });
	                            continue;
	                        }
	                    } catch (error) {
	                        console.error('검수 정보 확인 중 오류:', error);
	                        invalidItems.push({
	                            code: receiptData.RECEIPT_CODE,
	                            reason: '검수 정보 확인 중 오류 발생'
	                        });
	                        continue;
	                    }
	                }
	                
	                // 입고대기 상태인 경우(검수 없이 바로 입고하는 경우)는 경고 추가
	                if (receiptData.RECEIPT_STATUS === RECEIPT_STATUS.WAITING) {
	                    invalidItems.push({
	                        code: receiptData.RECEIPT_CODE,
	                        reason: '검수가 완료되지 않은 항목'
	                    });
	                    continue;
	                }
	                
	                validItems.push({
	                    receiptNo: receiptData.RECEIPT_NO,
	                    receiptCode: receiptData.RECEIPT_CODE,
	                    receiptStatus: RECEIPT_STATUS.COMPLETED,
	                    updatedBy: 'SYSTEM' // TODO: 로그인 사용자 정보로 대체
	                });
	            }
	            
	            if (validItems.length === 0) {
	                // 선택된 모든 항목이 유효하지 않은 경우
	                let errorMessage = '다음 이유로 입고 확정할 수 없습니다:\n\n';
	                invalidItems.forEach(item => {
	                    errorMessage += `- ${item.code}: ${item.reason}\n`;
	                });
	                
	                await AlertUtil.showWarning('알림', errorMessage);
	                return false;
	            }
	            
	            // 일부 항목이 유효하지 않은 경우
	            if (invalidItems.length > 0) {
	                let warningMessage = '다음 항목은 입고 확정이 불가능합니다:\n\n';
	                invalidItems.forEach(item => {
	                    warningMessage += `- ${item.code}: ${item.reason}\n`;
	                });
	                
	                warningMessage += '\n유효한 항목만 확정 처리하시겠습니까?';
	                
	                const confirmed = await AlertUtil.showConfirm({
	                    title: "입고 확정",
	                    text: warningMessage,
	                    icon: "warning"
	                });
	                
	                if (!confirmed) {
	                    return false;
	                }
	            }
	            
	            // 확인 대화상자 표시
	            const confirmed = await AlertUtil.showConfirm({
	                title: "입고 확정",
	                text: `선택한 ${validItems.length}개 항목을 입고 확정 처리하시겠습니까?`,
	                icon: "question"
	            });
	            
	            if (!confirmed) {
	                return false;
	            }
	            
	            // API 호출 처리
	            const response = await ApiUtil.processRequest(
	                () => ApiUtil.post(API_URLS.CONFIRM, { items: validItems }), 
	                {
	                    loadingMessage: '입고 확정 처리 중...',
	                    successMessage: `${validItems.length}개 항목이 입고 확정되었습니다.`,
	                    errorMessage: "입고 확정 처리 중 오류가 발생했습니다.",
	                    successCallback: async () => {
	                        // 목록 갱신
	                        await searchData();
	                        
	                        // 상세 정보 영역 숨기기
	                        hideDetailSection();
	                    }
	                }
	            );
	            
	            return response.success;
	        } catch (error) {
	            console.error('입고 확정 처리 오류:', error);
	            await AlertUtil.showError('처리 오류', '입고 확정 처리 중 오류가 발생했습니다.');
	            return false;
	        }
	    }
	    
	    /**
	     * 검수 등록 함수 - 다건 등록 지원
	     * 선택한 입고 항목들을 검수 등록합니다.
	     */
	    async function openInspectionModal() {
	        try {
	            // 선택된 행 ID 확인
	            const grid = GridUtil.getGrid('mReceiptGrid');
	            const selectedRowKeys = grid.getCheckedRowKeys();
	            
	            if (selectedRowKeys.length === 0) {
	                await AlertUtil.showWarning('알림', '검수 등록할 입고 항목을 선택해주세요.');
	                return false;
	            }
	            
	            // 다건 등록 지원
	            // 모든 선택된 항목의 유효성 검증
	            const invalidItems = [];
	            const validItems = [];
	            
	            for (const rowKey of selectedRowKeys) {
	                const receiptData = grid.getRow(rowKey);
	                
	                // 입고 완료된 항목 체크
	                if (receiptData.RECEIPT_STATUS === RECEIPT_STATUS.COMPLETED) {
	                    invalidItems.push({
	                        code: receiptData.RECEIPT_CODE,
	                        reason: '이미 입고 완료된 항목'
	                    });
	                    continue;
	                }
	                
	                // 취소된 항목 체크
	                if (receiptData.RECEIPT_STATUS === RECEIPT_STATUS.CANCELED) {
	                    invalidItems.push({
	                        code: receiptData.RECEIPT_CODE,
	                        reason: '취소된 항목'
	                    });
	                    continue;
	                }
	                
	                // 검수중인 항목 체크
	                if (receiptData.RECEIPT_STATUS === RECEIPT_STATUS.INSPECTING) {
	                    invalidItems.push({
	                        code: receiptData.RECEIPT_CODE,
	                        reason: '이미 검수중인 항목'
	                    });
	                    continue;
	                }
	                
	                // 유효한 항목은 별도 목록에 추가
	                validItems.push(receiptData);
	            }
	            
	            // 유효하지 않은 항목이 있는 경우 알림
	            if (invalidItems.length > 0) {
	                let errorMessage = '다음 항목은 검수 등록이 불가능합니다:\n\n';
	                invalidItems.forEach(item => {
	                    errorMessage += `- ${item.code}: ${item.reason}\n`;
	                });
	                
	                if (validItems.length === 0) {
	                    await AlertUtil.showWarning('알림', errorMessage);
	                    return false;
	                } else {
	                    errorMessage += '\n나머지 유효한 항목만 검수 등록을 진행하시겠습니까?';
	                    const confirmed = await AlertUtil.showConfirm({
	                        title: "검수 등록",
	                        text: errorMessage,
	                        icon: "question"
	                    });
	                    
	                    if (!confirmed) {
	                        return false;
	                    }
	                }
	            }
	            
	            if (validItems.length === 0) {
	                await AlertUtil.showWarning('알림', '검수 등록 가능한 항목이 없습니다.');
	                return false;
	            }
	            
	            // 검수 항목 확인 대화상자
	            let confirmMessage = `선택한 ${validItems.length}개 항목을 검수 등록하시겠습니까?\n\n`;
	            
	            // 최대 5개 항목만 표시 (너무 많으면 UI가 복잡해짐)
	            const displayItems = validItems.slice(0, 5);
	            displayItems.forEach(item => {
	                confirmMessage += `- ${item.RECEIPT_CODE}: ${item.MTL_NAME} (${item.RECEIVED_QTY})\n`;
	            });
	            
	            // 표시되지 않은 항목이 있는 경우 추가 메시지
	            if (validItems.length > 5) {
	                confirmMessage += `\n외 ${validItems.length - 5}개 항목`;
	            }
	            
	            const confirmed = await AlertUtil.showConfirm({
	                title: "검수 등록",
	                text: confirmMessage,
	                icon: "question"
	            });
	            
	            if (!confirmed) {
	                return false;
	            }
	            
	            // 검수 등록 API 호출 (다건 처리)
	            const inspectionItems = validItems.map(item => ({
	                receiptNo: item.RECEIPT_NO,
	                receiptCode: item.RECEIPT_CODE,
	                receiptStatus: RECEIPT_STATUS.INSPECTING,
	                updatedBy: 'SYSTEM' // TODO: 로그인 사용자 정보로 대체
	            }));
	            
	            // API 호출 처리
	            const response = await ApiUtil.processRequest(
	                () => ApiUtil.post(API_URLS.INSPECTION, { items: inspectionItems }), 
	                {
	                    loadingMessage: '검수 등록 중...',
	                    successMessage: `${validItems.length}개 항목의 검수 등록이 완료되었습니다. 검수 담당자가 검사를 진행합니다.`,
	                    errorMessage: "검수 등록 중 오류가 발생했습니다.",
	                    successCallback: async () => {
	                        // 목록 갱신
	                        await searchData();
	                        
	                        // 상세 정보 영역 숨기기
	                        hideDetailSection();
	                    }
	                }
	            );
	            
	            return response.success;
	        } catch (error) {
	            console.error('검수 등록 오류:', error);
	            await AlertUtil.showError('처리 오류', '검수 등록 중 오류가 발생했습니다.');
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
	     * 검수 결과 텍스트 변환 함수
	     * 
	     * @param {string} resultCode - 검수 결과 코드
	     * @returns {string} 검수 결과 표시 텍스트
	     */
	    function getInspectionResultText(resultCode) {
	        if (!resultCode) return '-';
	        
	        const resultMap = {
	            'PASS': '<span class="badge badge-completed">합격</span>',
	            'CONDITIONAL_PASS': '<span class="badge badge-inspecting">조건부 합격</span>',
	            'FAIL': '<span class="badge badge-canceled">불합격</span>'
	        };
	        
	        return resultMap[resultCode] || resultCode;
	    }
	    
	    /**
	     * 그리드 인스턴스 반환 함수
	     * 외부에서 그리드 인스턴스에 직접 접근할 수 있습니다.
	     * 
	     * @returns {Object} 그리드 인스턴스
	     */
	    function getGrid() {
	        return mReceiptGrid;
	    }

	    // =============================
	    // 공개 API - 외부에서 접근 가능한 메서드
	    // =============================
	    return {
	        // 초기화 및 기본 기능
	        init,                // 모듈 초기화

	        // 데이터 관련 함수
	        searchData,          // 데이터 검색
	        confirmReceipt,      // 입고 확정
	        openInspectionModal, // 검수 등록 모달 
	        loadHistoryData,     // 이력 정보 로드
	        
	        // 유틸리티 함수
	        getGrid,             // 그리드 인스턴스 반환
	    };
	})();

	// =============================
	// DOM 로드 시 초기화
	// =============================
	document.addEventListener('DOMContentLoaded', async function() {
	    try {
	        // 원자재 입고관리 모듈 초기화
	        await MaterialReceiptManager.init();
	    } catch (error) {
	        console.error('초기화 중 오류 발생:', error);
	        if (window.AlertUtil) {
	            await AlertUtil.showError('초기화 오류', '원자재 입고관리 초기화 중 오류가 발생했습니다.');
	        } else {
	            alert('원자재 입고관리 초기화 중 오류가 발생했습니다.');
	        }
	    }
	});