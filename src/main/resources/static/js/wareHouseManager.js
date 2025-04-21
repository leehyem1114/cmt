/**
 * 창고 관리자 - 창고 정보 관리 모듈
 * 
 * 창고 정보의 CRUD 기능을 담당하며, 리팩토링된 유틸리티 모듈(GridUtil, ApiUtil 등)을
 * 활용하여 일관된 async/await 패턴으로 구현합니다.
 * 
 * @version 1.0.0
 * @since 2025-04-17
 */
const WareHouseManager = (function() {
    // =============================
    // 모듈 내부 변수
    // =============================

    // 그리드 인스턴스 참조
    let wareHouseGrid;

    // API URL 상수 정의
    const API_URLS = {
        WAREHOUSE: {
            LIST: '/api/warehouses/list',
            BATCH: '/api/warehouses/batch',
            DELETE: (whsNo) => `/api/warehouses/${whsNo}`
        }
    };

    // =============================
    // 초기화 및 이벤트 처리 함수
    // =============================

    /**
     * 모듈 초기화 함수
     * 창고 관리 모듈의 초기 설정 및 이벤트 바인딩을 수행합니다.
     */
    async function init() {
        try {
            console.log('창고 관리자 초기화를 시작합니다.');

            // 그리드 초기화
            await initGrids();

            // 이벤트 리스너 등록
            await registerEvents();

            console.log('창고 관리자 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('초기화 중 오류 발생:', error);
            await AlertUtil.showError('초기화 오류', '창고 관리자 초기화 중 오류가 발생했습니다.');
        }
    }

    /**
     * 그리드 초기화 함수
     * 창고 그리드를 초기화합니다.
     */
    async function initGrids() {
        // 창고 그리드 초기화
        await initWareHouseGrid();
    }

    /**
     * 이벤트 리스너 등록 함수
     * 버튼 클릭 및 엔터키 검색 등의 이벤트 리스너를 등록합니다.
     */
    async function registerEvents() {
        try {
            // UIUtil을 사용하여 이벤트 리스너 등록 - 리팩토링된 UIUtil 사용
            await UIUtil.registerEventListeners({
                'wareHouseAppendBtn': appendWareHouseRow,
                'wareHouseSaveBtn': saveWareHouseData,
                'wareHouseDeleteBtn': deleteWareHouseRows,
                'wareHouseSearchBtn': searchWareHouse
            });

            // 엔터키 검색 이벤트 등록
            await UIUtil.bindEnterKeySearch('wareHouseInput', searchWareHouse);
        } catch (error) {
            console.error('이벤트 리스너 등록 중 오류:', error);
            throw error;
        }
    }

    // =============================
    // 창고 그리드 관련 함수
    // =============================

    /**
     * 창고 그리드 초기화 함수
     * 창고 그리드를 생성하고 초기 데이터를 로드합니다.
     */
    async function initWareHouseGrid() {
        try {
            console.log('창고 그리드 초기화를 시작합니다.');

            // DOM 요소 존재 확인
            const wareHouseElement = document.getElementById('wareHouseGrid');
            if (!wareHouseElement) {
                throw new Error('wareHouseGrid 요소를 찾을 수 없습니다. HTML을 확인해주세요.');
            }

            // 서버에서 받은 데이터 활용
            const gridData = window.wareHouseList || [];
            console.log('창고 초기 데이터:', gridData.length, '건');

            // 창고 그리드 생성 - 리팩토링된 GridUtil 사용
            wareHouseGrid = GridUtil.registerGrid({
                id: 'wareHouseGrid',
                columns: [
                    {
                        header: '창고 번호(PK)',
                        name: 'WHS_NO',
                        editor: 'text',
                        width: 100
                    },
                    {
                        header: '창고 코드',
                        name: 'WHS_CODE',
                        editor: 'text',
                        width: 120
                    },
                    {
                        header: '창고명',
                        name: 'WHS_NAME',
                        editor: 'text',
                        width: 150
                    },
                    {
                        header: '창고 유형',
                        name: 'WHS_TYPE',
                        editor: 'text',
                        width: 120
                    },
                    {
                        header: '위치',
                        name: 'WHS_LOCATION',
                        editor: 'text',
                        width: 150
                    },
                    {
                        header: '관리자',
                        name: 'WHS_MANAGER',
                        editor: 'text',
                        width: 120
                    },
                    {
                        header: '용량',
                        name: 'WHS_CAPACITY',
                        editor: 'text',
                        width: 100
                    },
                    {
                        header: '비고',
                        name: 'WHS_COMMENTS',
                        editor: 'text',
                        width: 200
                    },
                    {
                        header: '사용 정보',
                        name: 'WHS_USED',
                        editor: 'text',
                        width: 150
                    },
                    {
                        header: '사용여부',
                        name: 'USE_YN',
                        editor: GridUtil.createYesNoEditor(),
                        width: 100
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

            // 행 더블클릭 이벤트 처리 - 키 컬럼 제어
            GridUtil.setupKeyColumnControl('wareHouseGrid', 'WHS_NO');

            // 편집 종료 이벤트 처리 - ROW_TYPE 업데이트
            wareHouseGrid.on('editingFinish', function(ev) {
                const rowKey = ev.rowKey;
                const row = wareHouseGrid.getRow(rowKey);
                
                // 원래 값과 변경된 값이 다른 경우에만 ROW_TYPE 업데이트
                if (row.ROW_TYPE !== 'insert' && ev.value !== ev.prevValue) {
                    wareHouseGrid.setValue(rowKey, 'ROW_TYPE', 'update');
                    console.log(`창고 행 ${rowKey}의 ROW_TYPE을 'update'로 변경했습니다.`);
                }
            });

            console.log('창고 그리드 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('창고 그리드 초기화 오류:', error);
            throw error;
        }
    }

    /**
     * 창고 행 추가 함수
     * 창고 그리드에 새로운 행을 추가합니다.
     */
    async function appendWareHouseRow() {
        try {
            console.log('창고 행 추가');

            const newRowData = {
                WHS_NO: '',
                WHS_CODE: '',
                WHS_NAME: '',
                WHS_TYPE: '',
                WHS_LOCATION: '',
                WHS_MANAGER: '',
                WHS_CAPACITY: '',
                WHS_COMMENTS: '',
                WHS_USED: '',
                USE_YN: 'Y'
                // ROW_TYPE은 리팩토링된 GridUtil.addNewRow()에서 자동으로 추가됨
            };

            // 리팩토링된 GridUtil 사용
            await GridUtil.addNewRow('wareHouseGrid', newRowData, {
                at: 0,
                focus: true
            });
        } catch (error) {
            console.error('행 추가 중 오류:', error);
            await AlertUtil.showError('행 추가 오류', '창고 행 추가 중 오류가 발생했습니다.');
        }
    }

    /**
     * 창고 검색 함수
     * 검색어를 이용하여 창고를 검색하고 그리드에 결과를 표시합니다.
     */
    async function searchWareHouse() {
        try {
            const keyword = document.getElementById('wareHouseInput').value;
            console.log('창고 검색 시작. 검색어:', keyword);

            // API 호출 - 리팩토링된 ApiUtil 사용
            const response = await ApiUtil.getWithLoading(
                API_URLS.WAREHOUSE.LIST, {
                    keyword: keyword
                },
                '창고 검색 중...'
            );

            // 응답 데이터 처리
            const data = Array.isArray(response) ? response : (response.data || []);

            // 그리드 데이터 설정 - 리팩토링된 GridUtil 참조
            const grid = GridUtil.getGrid('wareHouseGrid');
            if (grid) {
                grid.resetData(data);
            }

            return data;
        } catch (error) {
            console.error('창고 검색 중 오류:', error);
            await AlertUtil.showError('검색 오류', '창고 검색 중 오류가 발생했습니다.');
            throw error;
        }
    }

    // =============================
    // 창고 CRUD 처리 함수
    // =============================

    /**
     * 창고 저장 함수
     * 창고 그리드의 변경된 데이터를 저장합니다.
     */
    async function saveWareHouseData() {
        try {
            console.log('창고 저장 시작');

            const grid = GridUtil.getGrid('wareHouseGrid');
            if (!grid) {
                throw new Error('wareHouseGrid 그리드를 찾을 수 없습니다.');
            }

            // 마지막으로 입력한 셀에 대한 값 반영을 위해 포커스 해제
            grid.blur();

            // 변경된 데이터 추출 - 리팩토링된 GridUtil 사용
            const changes = await GridUtil.extractChangedData('wareHouseGrid');
            const modifiedData = [...changes.insert, ...changes.update];

            if (modifiedData.length === 0) {
                await AlertUtil.showWarning("알림", "수정된 내용이 없습니다.");
                return false;
            }

            // 저장할 데이터 준비
            const batchData = modifiedData.map(row => ({
                whsNo: row.WHS_NO,
                whsCode: row.WHS_CODE,
                whsName: row.WHS_NAME,
                whsType: row.WHS_TYPE || '',
                whsLocation: row.WHS_LOCATION || '',
                whsManager: row.WHS_MANAGER || '',
                whsCapacity: row.WHS_CAPACITY || '',
                whsComments: row.WHS_COMMENTS || '',
                whsUsed: row.WHS_USED || '',
                useYn: row.USE_YN,
                action: row.ROW_TYPE // insert, update, delete
            }));

            // 유효성 검사
            for (const item of batchData) {
                if (ValidationUtil.isEmpty(item.whsCode)) {
                    await AlertUtil.notifyValidationError("유효성 오류", "창고 코드는 필수입니다.");
                    return false;
                }
                if (ValidationUtil.isEmpty(item.whsName)) {
                    await AlertUtil.notifyValidationError("유효성 오류", "창고명은 필수입니다.");
                    return false;
                }
                if (ValidationUtil.isEmpty(item.whsType)) {
                    await AlertUtil.notifyValidationError("유효성 오류", "창고 유형은 필수입니다.");
                    return false;
                }
                if (ValidationUtil.isEmpty(item.useYn)) {
                    await AlertUtil.notifyValidationError("유효성 오류", "사용여부는 필수입니다.");
                    return false;
                }
            }

            // 리팩토링된 ApiUtil의 processRequest 사용
            const response = await ApiUtil.processRequest(
                () => ApiUtil.post(API_URLS.WAREHOUSE.BATCH, batchData), {
                    loadingMessage: '창고 저장 중...',
                    successMessage: "창고가 저장되었습니다.",
                    errorMessage: "창고 저장 중 오류가 발생했습니다.",
                    successCallback: searchWareHouse
                }
            );
            
            if(response.success){
                await AlertUtil.showSuccess('저장 완료', '데이터가 성공적으로 저장되었습니다.');
                return true;
            } else {
                return false;
            }
            
        } catch (error) {
            console.error('창고 저장 오류:', error);
            await AlertUtil.notifySaveError("저장 실패", "창고 저장 중 오류가 발생했습니다.");
            return false;
        }
    }

    /**
     * 창고 삭제 함수
     * 선택된 창고 행을 삭제합니다.
     */
    async function deleteWareHouseRows() {
        try {
            // 선택된 행 ID 확인
            const grid = GridUtil.getGrid('wareHouseGrid');
            const selectedRowKeys = grid.getCheckedRowKeys();
            
            if (selectedRowKeys.length === 0) {
                await AlertUtil.showWarning('알림', '삭제할 항목을 선택해주세요.');
                return false;
            }
            
            // 선택된 코드 목록 생성
            const selectedIds = [];
            for (const rowKey of selectedRowKeys) {
                const whsNo = grid.getValue(rowKey, "WHS_NO");
                if (whsNo) selectedIds.push(whsNo);
            }
            
            if (selectedIds.length === 0) {
                await AlertUtil.showWarning('알림', '유효한 창고 ID를 찾을 수 없습니다.');
                return false;
            }
            
            // GridUtil.deleteSelectedRows 사용 (UI 측면의 삭제 확인 및 행 제거)
            const result = await GridUtil.deleteSelectedRows('wareHouseGrid', {
                confirmTitle: "삭제 확인",
                confirmMessage: "선택한 창고를 삭제하시겠습니까?",
                onBeforeDelete: async () => {
                    // 삭제 전 처리 - 여기서 true 반환해야 삭제 진행
                    return true;
                },
                onAfterDelete: async () => {
                    // 삭제 API 호출 및 처리
                    try {
                        // 삭제 요청 생성
                        const deleteRequests = selectedIds.map(warehouseId => 
                            async () => ApiUtil.del(API_URLS.WAREHOUSE.DELETE(warehouseId))
                        );
                        
                        // 일괄 삭제 요청 실행
                        await ApiUtil.withLoading(async () => {
                            await Promise.all(deleteRequests.map(req => req()));
                        }, '창고 삭제 중...');
                        
                        // 삭제 성공 메시지
                        await AlertUtil.notifyDeleteSuccess('삭제 완료', '창고가 삭제되었습니다.');
                        
                        // 목록 갱신
                        await searchWareHouse();
                    } catch (apiError) {
                        console.error('삭제 API 호출 중 오류:', apiError);
                        await AlertUtil.notifyDeleteError('삭제 실패', '창고 삭제 중 API 오류가 발생했습니다.');
                    }
                }
            });
            
            return result;
        } catch (error) {
            console.error('창고 삭제 오류:', error);
            await AlertUtil.notifyDeleteError('삭제 실패', '창고 삭제 중 오류가 발생했습니다.');
            return false;
        }
    }

    // =============================
    // 유틸리티 함수
    // =============================

    /**
     * 창고 그리드 인스턴스 반환 함수
     * 외부에서 창고 그리드 인스턴스에 직접 접근할 수 있습니다.
     * 
     * @returns {Object} 창고 그리드 인스턴스
     */
    function getWareHouseGrid() {
        return wareHouseGrid;
    }

    /**
     * 검색 조건 저장 함수
     * 현재 검색 조건을 로컬 스토리지에 저장합니다.
     */
    async function saveSearchCondition() {
        try {
            const wareHouseCondition = document.getElementById('wareHouseInput')?.value || '';

            const searchCondition = {
                wareHouse: wareHouseCondition
            };

            localStorage.setItem('wareHouseSearchCondition', JSON.stringify(searchCondition));
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
            const savedData = localStorage.getItem('wareHouseSearchCondition');

            if (!savedData) {
                console.log('저장된 검색 조건이 없습니다.');
                return false;
            }

            const searchCondition = JSON.parse(savedData);

            // 검색 조건 설정
            const wareHouseInput = document.getElementById('wareHouseInput');

            if (wareHouseInput && searchCondition.wareHouse) {
                wareHouseInput.value = searchCondition.wareHouse;
            }

            // 창고 검색 실행
            if (searchCondition.wareHouse) {
                await searchWareHouse();
            }

            console.log('검색 조건이 로드되었습니다.');
            return true;
        } catch (error) {
            console.error('검색 조건 로드 오류:', error);
            await AlertUtil.showError('로드 오류', '검색 조건 로드 중 오류가 발생했습니다.');
            return false;
        }
    }

    // =============================
    // 공개 API - 외부에서 접근 가능한 메서드
    // =============================
    return {
        // 초기화 및 기본 기능
        init, // 모듈 초기화

        // 창고 관련 함수
        searchWareHouse, // 창고 검색
        appendWareHouseRow, // 창고 행 추가
        saveWareHouseData, // 창고 저장
        deleteWareHouseRows, // 창고 삭제

        // 유틸리티 함수
        getWareHouseGrid, // 창고 그리드 인스턴스 반환
        saveSearchCondition, // 검색 조건 저장
        loadSearchCondition // 저장된 검색 조건 로드
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