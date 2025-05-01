/**
 * 원자재 재고관리 - 재고 정보 관리 모듈
 * 
 * 원자재 재고 정보의 조회, 추가, 수정, 삭제 기능을 담당하는 관리 모듈입니다.
 * 
 * @version 2.2.0
 * @since 2025-04-25
 */
const MaterialInventoryManager = (function() {
    // =============================
    // 모듈 내부 변수 - 필요에 따라 변경하세요
    // =============================

    // 그리드 인스턴스 참조
    let mInventoryGrid;

    // API URL 상수 정의
    const API_URLS = {
        LIST: '/api/materialInventory/list',            // 데이터 목록 조회
        SAVE: '/api/materialInventory/save',            // 데이터 저장
        DELETE: '/api/materialInventory/delete',        // 데이터 삭제
        EXCEL: {
            UPLOAD: '/api/materialInventory/excel/upload',  // 엑셀 업로드 API URL
            DOWNLOAD: '/api/materialInventory/excel/download' // 엑셀 다운로드 API URL
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
            console.log('원자재 재고관리 초기화를 시작합니다.');

            // 그리드 초기화
            await initGrid();

            // 이벤트 리스너 등록
            await registerEvents();
            
            // 그리드 검색 초기화
            initGridSearch();
            
            // 엑셀 기능 초기화
            initExcelFeatures();

            console.log('원자재 재고관리 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('초기화 중 오류 발생:', error);
            await AlertUtil.showError('초기화 오류', '원자재 재고관리 초기화 중 오류가 발생했습니다.');
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
                'mInventoryAppendBtn': appendRow,              // 행 추가 버튼
                'mInventorySaveBtn': saveData,                 // 데이터 저장 버튼
                'mInventoryDeleteBtn': deleteRows,             // 데이터 삭제 버튼
                'mInventorySearchBtn': searchData              // 데이터 검색 버튼
                // 엑셀 버튼 이벤트는 ExcelUtil에서 별도로 처리됩니다
            });

            // 엔터키 검색 이벤트 등록
            await UIUtil.bindEnterKeySearch('mInventoryInput', searchData);
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
                gridId: 'mInventoryGrid',
                searchInputId: 'mInventoryInput',
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
                buttonId: 'mInventoryExcelDownBtn', 
                gridId: 'mInventoryGrid',
                fileName: 'material-inventory-data.xlsx',
                sheetName: '원자재재고정보',
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
                fileInputId: 'mInventoryFileInput', 
                uploadButtonId: 'mInventoryExcelUpBtn', 
                gridId: 'mInventoryGrid',
                apiUrl: API_URLS.EXCEL.UPLOAD,
                headerMapping: {
                    '자재코드': 'MTL_CODE',
                    '자재명': 'MTL_NAME',
                    '창고코드': 'WAREHOUSE_CODE',
                    '위치코드': 'LOCATION_CODE',
                    '현재수량': 'CURRENT_QTY',
                    '할당수량': 'ALLOCATED_QTY',
                },
                beforeLoad: function() {
                    console.log('엑셀 업로드 시작');
                    return true;
                },
                afterLoad: function(data, saveResult) {
                    console.log('엑셀 업로드 완료, 결과:', data.length, '건, 저장:', saveResult);
                    if (saveResult) {
                        // 그리드 원본 데이터 업데이트
                        GridSearchUtil.updateOriginalData('mInventoryGrid', data);
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
            const gridElement = document.getElementById('mInventoryGrid');
            if (!gridElement) {
                throw new Error('mInventoryGrid 요소를 찾을 수 없습니다. HTML을 확인해주세요.');
            }

            // 서버에서 받은 데이터 활용
            const gridData = window.mInventoryList || [];
            console.log('초기 데이터:', gridData.length, '건');

            // 그리드 생성 - GridUtil 사용
            mInventoryGrid = GridUtil.registerGrid({
                id: 'mInventoryGrid',
                columns: [
                    {
                        header: '자재코드',
                        name: 'MTL_CODE',
                        editor: false,
                        sortable: true
                    },
                    {
                        header: '자재명',
                        name: 'MTL_NAME',
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
                        editor: false, // 트리거로 자동 계산됨
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
                        header: '타입',
                        name: 'ROW_TYPE',
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
                hiddenColumns: ['ROW_TYPE'],
                gridOptions: {
                    rowHeaders: ['rowNum']
                },
                toggleRowCheckedOnClick: false // 행 클릭 시 체크박스 토글 기능 활성화
            });
            
            // 편집 완료 이벤트 처리 - 변경된 행 추적
            mInventoryGrid.on('editingFinish', function(ev) {
                const rowKey = ev.rowKey;
                const row = mInventoryGrid.getRow(rowKey);
                
                // 원래 값과 변경된 값이 다른 경우에만 ROW_TYPE 업데이트
                if (row.ROW_TYPE !== 'insert' && ev.value !== ev.prevValue) {
                    mInventoryGrid.setValue(rowKey, 'ROW_TYPE', 'update');
                    console.log(`행 ${rowKey}의 ROW_TYPE을 'update'로 변경했습니다.`);
                }
            });

            // 키 컬럼 제어 설정 - PK 컬럼 편집 제어
            GridUtil.setupKeyColumnControl('mInventoryGrid', 'MTL_CODE');
            
            // 그리드 원본 데이터 저장 (검색 기능 위해 추가)
            GridSearchUtil.updateOriginalData('mInventoryGrid', gridData);

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
                MTL_CODE: '',
                MTL_NAME: '',
                WAREHOUSE_CODE: '',
                LOCATION_CODE: '',
                CURRENT_QTY: '0',
                ALLOCATED_QTY: '0',
                AVAILABLE_QTY: '0', // 트리거에 의해 자동 계산됨
                LAST_MOVEMENT_DATE: new Date()
                // ROW_TYPE은 GridUtil.addNewRow()에서 자동으로 추가됨
            };

            // 그리드에 새 행 추가
            await GridUtil.addNewRow('mInventoryGrid', newRowData, {
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
            const keyword = document.getElementById('mInventoryInput').value;
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
            const grid = GridUtil.getGrid('mInventoryGrid');
            if (grid) {
                grid.resetData(data);
                
                // 그리드 원본 데이터 업데이트 (검색 기능 위해 추가)
                GridSearchUtil.updateOriginalData('mInventoryGrid', data);
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

            const grid = GridUtil.getGrid('mInventoryGrid');
            if (!grid) {
                throw new Error('mInventoryGrid를 찾을 수 없습니다.');
            }

            // 마지막으로 입력한 셀에 대한 값 반영을 위해 포커스 해제
            grid.blur();

            // 변경된 데이터 추출
            const changes = await GridUtil.extractChangedData('mInventoryGrid');
            const modifiedData = [...changes.insert, ...changes.update];

            if (modifiedData.length === 0) {
                await AlertUtil.showWarning("알림", "수정된 내용이 없습니다.");
                return false;
            }

            // 유효성 검사
            for (const item of modifiedData) {
                if (ValidationUtil.isEmpty(item.MTL_CODE)) {
                    await AlertUtil.notifyValidationError("유효성 오류", "자재코드는 필수입니다.");
                    return false;
                }
                if (ValidationUtil.isEmpty(item.MTL_NAME)) {
                    await AlertUtil.notifyValidationError("유효성 오류", "자재명은 필수입니다.");
                    return false;
                }
                if (ValidationUtil.isEmpty(item.CURRENT_QTY)) {
                    await AlertUtil.notifyValidationError("유효성 오류", "현재수량은 필수입니다.");
                    return false;
                }
                if (ValidationUtil.isEmpty(item.ALLOCATED_QTY)) {
                    await AlertUtil.notifyValidationError("유효성 오류", "할당수량은 필수입니다.");
                    return false;
                }
                
                // 숫자 형식 검증
                if (isNaN(parseFloat(item.CURRENT_QTY))) {
                    await AlertUtil.notifyValidationError("유효성 오류", "현재수량은 숫자 형식이어야 합니다.");
                    return false;
                }
                if (isNaN(parseFloat(item.ALLOCATED_QTY))) {
                    await AlertUtil.notifyValidationError("유효성 오류", "할당수량은 숫자 형식이어야 합니다.");
                    return false;
                }
            }

            // API 호출 처리
            const response = await ApiUtil.processRequest(
                () => ApiUtil.post(API_URLS.SAVE, modifiedData), 
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
            const grid = GridUtil.getGrid('mInventoryGrid');
            const selectedRowKeys = grid.getCheckedRowKeys();
            
            if (selectedRowKeys.length === 0) {
                await AlertUtil.showWarning('알림', '삭제할 항목을 선택해주세요.');
                return false;
            }
            
            // 선택된 데이터 ID 목록 생성
            const selectedDataIds = [];
            for (const rowKey of selectedRowKeys) {
                const invNo = grid.getValue(rowKey, "INV_NO");
                const mtlCode = grid.getValue(rowKey, "MTL_CODE");
                
                // INV_NO가 없으면 MTL_CODE로 식별
                const identifier = invNo || mtlCode;
                if (identifier) selectedDataIds.push(identifier);
            }
            
            if (selectedDataIds.length === 0) {
                await AlertUtil.showWarning('알림', '유효한 재고번호 또는 자재코드를 찾을 수 없습니다.');
                return false;
            }
            
            // GridUtil.deleteSelectedRows 사용 (UI 측면의 삭제 확인 및 행 제거)
            const result = await GridUtil.deleteSelectedRows('mInventoryGrid', {
                confirmTitle: "삭제 확인",
                confirmMessage: "선택한 항목을 삭제하시겠습니까?",
                onBeforeDelete: async () => {
                    // 삭제 전 처리 - 여기서 true 반환해야 삭제 진행
                    return true;
                },
                onAfterDelete: async () => {
                    // 삭제 API 호출 및 처리
                    try {
                        // 일괄 삭제 요청 생성 - 한 번에 모든 데이터 삭제
                        await ApiUtil.withLoading(async () => {
                            await ApiUtil.post(API_URLS.DELETE, { ids: selectedDataIds });
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
			        return mInventoryGrid;
			    }

			    /**
			     * 검색 조건 저장 함수
			     * 현재 검색 조건을 로컬 스토리지에 저장합니다.
			     */
			    async function saveSearchCondition() {
			        try {
			            const searchCondition = document.getElementById('mInventoryInput')?.value || '';

			            localStorage.setItem('materialInventorySearchCondition', searchCondition);
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
			            const savedCondition = localStorage.getItem('materialInventorySearchCondition');

			            if (!savedCondition) {
			                console.log('저장된 검색 조건이 없습니다.');
			                return false;
			            }

			            // 검색 조건 설정
			            const searchInput = document.getElementById('mInventoryInput');
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
			            const keyword = document.getElementById('mInventoryInput').value.toLowerCase();
			            
			            // 원본 데이터 가져오기
			            GridSearchUtil.resetToOriginalData('mInventoryGrid');
			            const grid = GridUtil.getGrid('mInventoryGrid');
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
			        // 원자재 재고관리 초기화
			        await MaterialInventoryManager.init();

			        // 저장된 검색 조건 로드 (필요 시 활성화)
			        // await MaterialInventoryManager.loadSearchCondition();
			    } catch (error) {
			        console.error('초기화 중 오류 발생:', error);
			        if (window.AlertUtil) {
			            await AlertUtil.showError('초기화 오류', '원자재 재고관리 초기화 중 오류가 발생했습니다.');
			        } else {
			            alert('원자재 재고관리 초기화 중 오류가 발생했습니다.');
			        }
			    }
			});