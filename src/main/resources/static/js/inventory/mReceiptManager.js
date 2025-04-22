/**
 * 창고 관리자 - 창고 정보 관리 모듈
 * 
 * 단일 데이터 테이블의 CRUD 기능을 담당하는 창고 관리 모듈입니다.
 * 
 * @version 2.1.0
 * @since 2025-04-22
 */
const WareHouseManager = (function() {
    // =============================
    // 모듈 내부 변수 - 필요에 따라 변경하세요
    // =============================

    // 그리드 인스턴스 참조
    let mReceiptGrid;

    // API URL 상수 정의
    const API_URLS = {
        LIST: '/inventory/api/warehouses/list',              // 데이터 목록 조회
        BATCH: '/api/warehouses/batch',                      // 데이터 배치 저장
        DELETE: (whsNo) => `/api/warehouses/${whsNo}`,       // 데이터 삭제
        EXCEL: {
            UPLOAD: '/api/warehouses/excel/upload',          // 엑셀 업로드 API URL
            DOWNLOAD: '/api/warehouses/excel/download'       // 엑셀 다운로드 API URL
        }
    };
	// ====================================================== 추가
    // 입고 상태 정의 - 공통 컴포넌트 사용을 위한 설정
    const RECEIPT_STATUS_CONFIG = {
        states: ['대기', '진행중', '완료', '취소'],
        styles: {
            '대기': 'btn-secondary',
            '진행중': 'btn-primary',
            '완료': 'btn-success',
            '취소': 'btn-danger'
        },
        defaultState: '대기',
        gridId: 'mReceiptGrid',
        updateRowType: true,
        buttonStyle: {
            width: '100%',
            height: '26px',
            fontSize: '12px',
            padding: '2px 5px'
        },
        // 선택적으로 상태 변경 콜백 설정 가능
        onStateChange: (rowKey, oldState, newState, grid) => {
            console.log(`입고 상태 변경 콜백: ${oldState} -> ${newState} (행 ${rowKey})`);
            // 필요 시 추가 로직 구현
        }
    };
	// ====================================================== 추가
    // =============================
    // 초기화 및 이벤트 처리 함수
    // =============================

    /**
     * 모듈 초기화 함수
     * 관리 모듈의 초기 설정 및 이벤트 바인딩을 수행합니다.
     */
    async function init() {
        try {
            console.log('창고 관리자 초기화를 시작합니다.');

            // 그리드 초기화
            await initGrid();

            // 이벤트 리스너 등록
            await registerEvents();
            
            // 그리드 검색 초기화
            initGridSearch();
            
            // 엑셀 기능 초기화
            initExcelFeatures();

            console.log('창고 관리자 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('초기화 중 오류 발생:', error);
            await AlertUtil.showError('초기화 오류', '창고 관리자 초기화 중 오류가 발생했습니다.');
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
                'mReceiptAppendBtn': appendRow,              // 행 추가 버튼
                'mReceiptSaveBtn': saveData,                 // 데이터 저장 버튼
                'mReceiptDeleteBtn': deleteRows,             // 데이터 삭제 버튼
                'mReceiptSearchBtn': searchData              // 데이터 검색 버튼
                // 엑셀 버튼 이벤트는 ExcelUtil에서 별도로 처리됩니다
            });

            // 엔터키 검색 이벤트 등록
            await UIUtil.bindEnterKeySearch('mReceiptInput', searchData);
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
                }
            });
        } catch (error) {
            console.error('그리드 검색 초기화 중 오류:', error);
        }
    }
    
    /**
     * 엑셀 기능 초기화 함수
     * ExcelUtil을 사용하여 엑셀 다운로드/업로드 기능을 설정합니다.
     */
    function initExcelFeatures() {
        try {
            console.log('엑셀 기능 초기화');
            
            // 엑셀 다운로드 버튼 설정 - HTML에 버튼 추가 필요
            ExcelUtil.setupExcelDownloadButton({
                buttonId: 'mReceiptExcelDownBtn', 
                gridId: 'mReceiptGrid',
                fileName: 'warehouse-data.xlsx',
                sheetName: '창고정보',
                beforeDownload: function() {
                    console.log('엑셀 다운로드 시작');
                    return true;
                },
                afterDownload: function() {
                    console.log('엑셀 다운로드 완료');
                }
            });
            
            // 엑셀 업로드 버튼 설정 - HTML에 입력필드와 버튼 추가 필요
            ExcelUtil.setupExcelUploadButton({
                fileInputId: 'mReceiptFileInput', 
                uploadButtonId: 'mReceiptExcelUpBtn', 
                gridId: 'mReceiptGrid',
                apiUrl: API_URLS.EXCEL.UPLOAD,
                headerMapping: {
                    '창고코드': 'WHS_CODE',
                    '창고명': 'WHS_NAME',
                    '창고 유형': 'WHS_TYPE',
                    '위치': 'WHS_LOCATION',
                    '용량': 'WHS_CAPACITY',
                    '사용 정보': 'WHS_USED',
                    '비고': 'WHS_COMMENTS',
                    '관리자': 'WHS_MANAGER',
                    '사용여부': 'IS_ACTIVE'
                },
                beforeLoad: function() {
                    console.log('엑셀 업로드 시작');
                    return true;
                },
                afterLoad: function(data, saveResult) {
                    console.log('엑셀 업로드 완료, 결과:', data.length, '건, 저장:', saveResult);
                    if (saveResult) {
                        // 그리드 원본 데이터 업데이트
                        GridSearchUtil.updateOriginalData('mReceiptGrid', data);
                    }
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
			// ====================================================== 추가
            // 상태 포맷터 생성
            const statusFormatter = GridUtil.createStatusFormatter(RECEIPT_STATUS_CONFIG);
			// ====================================================== 추가
            // 그리드 생성 - GridUtil 사용
            mReceiptGrid = GridUtil.registerGrid({
                id: 'mReceiptGrid',
                columns: [
                    {
                        header: '입고 코드',
                        name: 'RECEIPT_CODE',
                        editor: 'text'
                    },
                    {
                        header: '발주 코드',
                        name: 'PO_CODE',
                        editor: 'text'
                    },
                    {
                        header: '자재 코드',
                        name: 'MTL_CODE',
                        editor: 'text'
                    },
                    {
                        header: 'LOT 번호',
                        name: 'LOT_NO',
                        editor: 'text'
                    },
                    {
                        header: '자재명',
                        name: 'MTL_NAME',
                        editor: 'text'
                    },
                    {
                        header: '입고일',
                        name: 'RECEIPT_DATE',
                        editor: 'text',
                        formatter: function(obj) {
                            if (!obj.value) return '-';
                            const date = new Date(obj.value);
                            return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
                        }
                    },
                    {
                        header: '입고 수량',
                        name: 'RECEIVED_QTY',
                        editor: 'text'
                    },
                    {
                        header: '입고 상태',
                        name: 'RECEIPT_STATUS',
						// ====================================================== 추가
                        // 단순 formatter로 표시만 처리
                        formatter: statusFormatter
						// ====================================================== 추가
                    },
                    {
                        header: '창고 코드',
                        name: 'WAREHOUSE_CODE',
                        editor: 'text',
                    },
                    {
                        header: '위치 코드',
                        name: 'LOCATION_CODE',
                        editor: 'text',
                    },
                    {
                        header: '입고 담당자',
                        name: 'RECEIVER',
                        editor: 'text',
                    },
                    {
                        header: '타입',
                        name: 'ROW_TYPE'
                    } // 조회/추가 구분
                ],
                columnOptions: {
                    resizable: true
                },
                pageOptions: {
                    useClient: true,
                    perPage: 10
                },
                data: gridData,
                draggable: true,
                displayColumnName: 'SORT_ORDER',
                hiddenColumns: ['ROW_TYPE'],
                gridOptions: {
                    rowHeaders: ['rowNum', 'checkbox']
                }
            });
			// ====================================================== 추가
            // 그리드 데이터 정리
            // 빈 상태 값 초기화
            const rows = mReceiptGrid.getData();
            rows.forEach((row, index) => {
                if (!row.RECEIPT_STATUS) {
                    mReceiptGrid.setValue(row.rowKey, 'RECEIPT_STATUS', RECEIPT_STATUS_CONFIG.defaultState);
                }
            });
			// ====================================================== 추가
            // 편집 완료 이벤트 처리 - 변경된 행 추적
            mReceiptGrid.on('editingFinish', function(ev) {
                const rowKey = ev.rowKey;
                const row = mReceiptGrid.getRow(rowKey);
                
                // 원래 값과 변경된 값이 다른 경우에만 ROW_TYPE 업데이트
                if (row.ROW_TYPE !== 'insert' && ev.value !== ev.prevValue) {
                    mReceiptGrid.setValue(rowKey, 'ROW_TYPE', 'update');
                    console.log(`행 ${rowKey}의 ROW_TYPE을 'update'로 변경했습니다.`);
                }
            });

            // 키 컬럼 제어 설정 - PK 컬럼 편집 제어
            GridUtil.setupKeyColumnControl('mReceiptGrid', 'WHS_CODE');
			// ====================================================== 추가
            // 상태 변경 이벤트 설정
            GridUtil.setupStatusChangeEvent('mReceiptGrid', 'RECEIPT_STATUS', RECEIPT_STATUS_CONFIG);
			// ====================================================== 추가
            // 그리드 원본 데이터 저장 (검색 기능 위해 추가)
            GridSearchUtil.updateOriginalData('mReceiptGrid', gridData);

            console.log('그리드 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('그리드 초기화 오류:', error);
            throw error;
        }
    }

    /**
     * 데이터 행 추가 함수
     * 그리드에 새로운 행을 추가합니다.
     */
    async function appendRow() {
        try {
            console.log('행 추가');

            const newRowData = {
                WHS_CODE: '',
                WHS_NAME: '',
                WHS_TYPE: '',
                WHS_LOCATION: '',
                WHS_CAPACITY: '',
                WHS_USED: '',
                WHS_COMMENTS: '',
                WHS_MANAGER: '',
                IS_ACTIVE: 'Y',
				// ====================================================== 추가
                RECEIPT_STATUS: RECEIPT_STATUS_CONFIG.defaultState // 기본 상태 설정
				// ====================================================== 추가
                // ROW_TYPE은 GridUtil.addNewRow()에서 자동으로 추가됨
            };

            // 그리드에 새 행 추가
            await GridUtil.addNewRow('mReceiptGrid', newRowData, {
                at: 0,
                focus: true
            });
        } catch (error) {
            console.error('행 추가 중 오류:', error);
            await AlertUtil.showError('행 추가 오류', '행 추가 중 오류가 발생했습니다.');
        }
    }

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

            console.log('데이터 검색 완료. 결과:', data.length, '건');
            return data;
        } catch (error) {
            console.error('데이터 검색 중 오류:', error);
            await AlertUtil.showError('검색 오류', '데이터 검색 중 오류가 발생했습니다.');
            throw error;
        }
    }

    // =============================
    // CRUD 처리 함수
    // =============================

    /**
     * 데이터 저장 함수
     * 그리드의 변경된 데이터를 저장합니다.
     */
    async function saveData() {
        try {
            console.log('데이터 저장 시작');

            const grid = GridUtil.getGrid('mReceiptGrid');
            if (!grid) {
                throw new Error('mReceiptGrid를 찾을 수 없습니다.');
            }

            // 마지막으로 입력한 셀에 대한 값 반영을 위해 포커스 해제
            grid.blur();

            // 변경된 데이터 추출
            const changes = await GridUtil.extractChangedData('mReceiptGrid');
            const modifiedData = [...changes.insert, ...changes.update];

            if (modifiedData.length === 0) {
                await AlertUtil.showWarning("알림", "수정된 내용이 없습니다.");
                return false;
            }

            // 저장할 데이터 준비
            const batchData = modifiedData.map(row => ({
                whsCode: row.WHS_CODE,
                whsName: row.WHS_NAME,
                whsType: row.WHS_TYPE || '',
                whsLocation: row.WHS_LOCATION || '',
                whsCapacity: row.WHS_CAPACITY || '',
                whsUsed: row.WHS_USED || '',
                whsComments: row.WHS_COMMENTS || '',
                whsManager: row.WHS_MANAGER || '',
                isActive: row.IS_ACTIVE,
				// ====================================================== 추가
                receiptStatus: row.RECEIPT_STATUS || RECEIPT_STATUS_CONFIG.defaultState, // 입고 상태 추가
				// ====================================================== 추가
                action: row.ROW_TYPE // insert, update, delete
            }));

            // 유효성 검사
            for (const item of batchData) {
                if (ValidationUtil.isEmpty(item.whsCode)) {
                    await AlertUtil.notifyValidationError("유효성 오류", "창고코드는 필수입니다.");
                    return false;
                }
                if (ValidationUtil.isEmpty(item.whsName)) {
                    await AlertUtil.notifyValidationError("유효성 오류", "창고명은 필수입니다.");
                    return false;
                }
                if (ValidationUtil.isEmpty(item.isActive)) {
                    await AlertUtil.notifyValidationError("유효성 오류", "사용여부는 필수입니다.");
                    return false;
                }
            }

            // API 호출 처리
            const response = await ApiUtil.processRequest(
                () => ApiUtil.post(API_URLS.BATCH, batchData), 
                {
                    loadingMessage: '데이터 저장 중...',
                    successMessage: "데이터가 저장되었습니다.",
                    errorMessage: "데이터 저장 중 오류가 발생했습니다.",
                    successCallback: searchData
                }
            );
            
            if(response.success){
                await AlertUtil.showSuccess('저장 완료', '데이터가 성공적으로 저장되었습니다.');
                return true;
            } else {
                return false;
            }
            
        } catch (error) {
            console.error('데이터 저장 오류:', error);
            await AlertUtil.notifySaveError("저장 실패", "데이터 저장 중 오류가 발생했습니다.");
            return false;
        }
    }

    /**
     * 데이터 삭제 함수
     * 선택된 행을 삭제합니다.
     */
    async function deleteRows() {
        try {
            // 선택된 행 ID 확인
            const grid = GridUtil.getGrid('mReceiptGrid');
            const selectedRowKeys = grid.getCheckedRowKeys();
            
            if (selectedRowKeys.length === 0) {
                await AlertUtil.showWarning('알림', '삭제할 항목을 선택해주세요.');
                return false;
            }
            
            // 선택된 데이터 ID 목록 생성
            const selectedDataIds = [];
            for (const rowKey of selectedRowKeys) {
                const whsNo = grid.getValue(rowKey, "WHS_NO");
                if (whsNo) selectedDataIds.push(whsNo);
            }
            
            if (selectedDataIds.length === 0) {
                await AlertUtil.showWarning('알림', '유효한 창고번호를 찾을 수 없습니다.');
                return false;
            }
            
            // GridUtil.deleteSelectedRows 사용 (UI 측면의 삭제 확인 및 행 제거)
            const result = await GridUtil.deleteSelectedRows('mReceiptGrid', {
                confirmTitle: "삭제 확인",
                confirmMessage: "선택한 항목을 삭제하시겠습니까?",
                onBeforeDelete: async () => {
                    // 삭제 전 처리 - 여기서 true 반환해야 삭제 진행
                    return true;
                },
                onAfterDelete: async () => {
                    // 삭제 API 호출 및 처리
                    try {
                        // 삭제 요청 생성
                        const deleteRequests = selectedDataIds.map(whsNo => 
                            async () => ApiUtil.del(API_URLS.DELETE(whsNo))
                        );
                        
                        // 일괄 삭제 요청 실행
                        await ApiUtil.withLoading(async () => {
                            await Promise.all(deleteRequests.map(req => req()));
                        }, '데이터 삭제 중...');
                        
                        // 삭제 성공 메시지
                        await AlertUtil.notifyDeleteSuccess('삭제 완료', '데이터가 삭제되었습니다.');
                        
                        // 목록 갱신
                        await searchData();
                    } catch (apiError) {
                        console.error('삭제 API 호출 중 오류:', apiError);
                        await AlertUtil.notifyDeleteError('삭제 실패', '데이터 삭제 중 API 오류가 발생했습니다.');
                    }
                }
            });
            
            return result;
        } catch (error) {
            console.error('데이터 삭제 오류:', error);
            await AlertUtil.notifyDeleteError('삭제 실패', '데이터 삭제 중 오류가 발생했습니다.');
            return false;
        }
    }

    // =============================
    // 유틸리티 함수
    // =============================

    /**
     * 그리드 인스턴스 반환 함수
     * 외부에서 그리드 인스턴스에 직접 접근할 수 있습니다.
     * 
     * @returns {Object} 그리드 인스턴스
     */
    function getGrid() {
        return mReceiptGrid;
    }

    /**
     * 검색 조건 저장 함수
     * 현재 검색 조건을 로컬 스토리지에 저장합니다.
     */
    async function saveSearchCondition() {
        try {
            const searchCondition = document.getElementById('mReceiptInput')?.value || '';

            localStorage.setItem('warehouseSearchCondition', searchCondition);
            console.log('검색 조건이 저장되었습니다.');

            await AlertUtil.showSuccess("저장 완료", "검색 조건이 저장되었습니다.");
            return true;
        } catch (error) {
            console.error('검색 조건 저장 오류:', error);
            await AlertUtil.showError('저장 오류', '검색 조건 저장 중 오류가 발생했습니다.');
            return false;
        }
    }

    /**
     * 저장된 검색 조건 로드 함수
     * 로컬 스토리지에서 저장된 검색 조건을 불러와 적용합니다.
     * 
     * @returns {boolean} 로드 성공 여부
     */
    async function loadSearchCondition() {
        try {
            const savedCondition = localStorage.getItem('warehouseSearchCondition');

            if (!savedCondition) {
                console.log('저장된 검색 조건이 없습니다.');
                return false;
            }

            // 검색 조건 설정
            const searchInput = document.getElementById('mReceiptInput');
            if (searchInput) {
                searchInput.value = savedCondition;
            }

            // 검색 실행
            await searchData();

            console.log('검색 조건이 로드되었습니다.');
            return true;
        } catch (error) {
            console.error('검색 조건 로드 오류:', error);
            await AlertUtil.showError('로드 오류', '검색 조건 로드 중 오류가 발생했습니다.');
            return false;
        }
    }
    
    /**
     * 로컬 검색 함수
     * 그리드 내 로컬 데이터를 대상으로 검색을 수행합니다.
     */
    function performLocalSearch() {
        try {
            const keyword = document.getElementById('mReceiptInput').value.toLowerCase();
            
            // 원본 데이터 가져오기
            GridSearchUtil.resetToOriginalData('mReceiptGrid');
            const grid = GridUtil.getGrid('mReceiptGrid');
            const originalData = grid.getData();
            
            // 필터링
            const filtered = originalData.filter(row => {
                return Object.values(row).some(val => {
                    if (val == null) return false;
                    return String(val).toLowerCase().includes(keyword);
                });
            });
            
            // 그리드 업데이트
            grid.resetData(filtered);
            console.log('로컬 검색 완료, 결과:', filtered.length, '건');
            
            return filtered;
        } catch (error) {
            console.error('로컬 검색 중 오류:', error);
            return [];
        }
    }

    // =============================
    // 공개 API - 외부에서 접근 가능한 메서드
    // =============================
    return {
        // 초기화 및 기본 기능
        init,           // 모듈 초기화

        // 데이터 관련 함수
        searchData,     // 데이터 검색
        appendRow,      // 행 추가
        saveData,       // 데이터 저장
        deleteRows,     // 데이터 삭제

        // 유틸리티 함수
        getGrid,               // 그리드 인스턴스 반환
        saveSearchCondition,   // 검색 조건 저장
        loadSearchCondition,   // 저장된 검색 조건 로드
        performLocalSearch     // 로컬 검색 실행
    };
})();

// =============================
// DOM 로드 시 초기화
// =============================
document.addEventListener('DOMContentLoaded', async function() {
    try {
        // 창고 관리자 초기화
        await WareHouseManager.init();

        // 저장된 검색 조건 로드 (필요 시 활성화)
        // await WareHouseManager.loadSearchCondition();
    } catch (error) {
        console.error('초기화 중 오류 발생:', error);
        if (window.AlertUtil) {
            await AlertUtil.showError('초기화 오류', '창고 관리자 초기화 중 오류가 발생했습니다.');
        } else {
            alert('창고 관리자 초기화 중 오류가 발생했습니다.');
        }
    }
});