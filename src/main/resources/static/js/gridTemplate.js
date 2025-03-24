/**
 * SimpleGridManager - 단일 그리드 관리 모듈 기본 템플릿
 * 
 * 이 템플릿은 CRUD 기능을 가진 단일 그리드 모듈을 쉽게 구현할 수 있는 기본 골격입니다.
 * REST API와 일반 컨트롤러 방식 모두 지원합니다.
 * 
 * @version 1.0.0
 */
const SimpleGridManager = (function() {
    //===========================================================================
    // 모듈 내부 변수 - 필요에 맞게 수정하세요.
    //===========================================================================

    // 그리드 인스턴스 참조 - 그대로 사용하세요
    let gridInstance;

    // API URL 상수 정의 
    // [✓] 수정 필요: 프로젝트에 맞는 방식을 선택하고 URL을 변경하세요
    
    // 방법 1: REST API 방식 (사용하지 않을 경우 주석 처리)
    const API_URLS = {
        LIST: '/api/items',                  // 목록 조회 API
        BATCH: '/api/items/batch',           // 일괄 저장 API
        DELETE: (code) => `/api/items/${code}` // 삭제 API (함수 형태)
    };
    
    /*
    // 방법 2: 일반 컨트롤러 방식 (사용할 경우 주석 해제)
    const API_URLS = {
        LIST: '/controller/item/list',       // 목록 조회 컨트롤러 URL
        SAVE: '/controller/item/save',       // 저장 컨트롤러 URL (삽입/수정 모두)
        DELETE: '/controller/item/delete'    // 삭제 컨트롤러 URL
    };
    */

    //===========================================================================
    // 초기화 및 이벤트 처리 함수 - 기본 구조는 유지하고 필요시 확장하세요
    //===========================================================================

    /**
     * 모듈 초기화 함수
     * 그리드 초기화 및 이벤트 등록을 수행합니다.
     * 
     * [✓] 수정 방법: init 함수 자체는 수정하지 않고, 호출하는 함수들을 수정하세요.
     */
    async function init() {
        try {
            console.log('모듈 초기화를 시작합니다.');

            // 그리드 초기화 - initGrid 함수에서 그리드 컬럼 등을 수정하세요
            await initGrid();

            // 이벤트 리스너 등록 - registerEvents 함수에서 필요한 이벤트를 추가하세요
            await registerEvents();

            console.log('모듈 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('초기화 중 오류 발생:', error);
            await AlertUtil.showError('초기화 오류', '모듈 초기화 중 오류가 발생했습니다.');
        }
    }

    /**
     * 이벤트 리스너 등록 함수
     * 버튼 클릭 및 엔터키 검색 등의 이벤트 리스너를 등록합니다.
     * 
     * [✓] 수정 방법: 버튼 ID와 연결할 함수를 수정하세요.
     * 예: 'exportBtn': exportData와 같이 새로운 버튼과 함수를 연결할 수 있습니다.
     */
    async function registerEvents() {
        try {
            // UIUtil을 사용하여 이벤트 리스너 등록
            // HTML에 있는 버튼 ID를 왼쪽에, 연결할 함수를 오른쪽에 작성합니다.
            await UIUtil.registerEventListeners({
                'appendBtn': appendRow,     // 행 추가 버튼
                'saveBtn': saveData,        // 저장 버튼
                'deleteBtn': deleteRows,    // 삭제 버튼
                'searchBtn': searchData,    // 검색 버튼
                'resetBtn': resetForm       // 초기화 버튼
                
                // [✓] 확장 지점: 추가 버튼이 필요한 경우 여기에 등록하세요
                // 'exportBtn': exportData,    // 예: 엑셀 내보내기 버튼
                // 'printBtn': printData,      // 예: 인쇄 버튼
            });

            // 엔터키 검색 이벤트 등록 - searchInput은 검색 입력 필드의 ID입니다.
            // [✓] 수정 방법: 검색 입력 필드의 ID가 다른 경우 변경하세요.
            await UIUtil.bindEnterKeySearch('searchInput', searchData);
            
            // [✓] 확장 지점: 다른 검색 필드가 있는 경우 추가 등록하세요
            // await UIUtil.bindEnterKeySearch('codeInput', searchByCode);
        } catch (error) {
            console.error('이벤트 리스너 등록 중 오류:', error);
            throw error;
        }
    }

    /**
     * 그리드 초기화 함수
     * 그리드를 생성하고 초기 데이터를 로드합니다.
     * 
     * [✓] 수정 필요: 프로젝트에 맞게 그리드 컬럼을 수정하세요.
     */
    async function initGrid() {
        try {
            console.log('그리드 초기화를 시작합니다.');

            // DOM 요소 존재 확인
            // [✓] 수정 방법: HTML의 그리드 요소 ID가 'dataGrid'가 아니라면 여기를 변경하세요.
            const gridElement = document.getElementById('dataGrid');
            if (!gridElement) {
                throw new Error('dataGrid 요소를 찾을 수 없습니다. HTML을 확인해주세요.');
            }

            // 그리드 컬럼 정의
            // [✓] 수정 필요: 프로젝트에 필요한 컬럼으로 변경하세요.
            const columns = [
                {
                    header: '코드',           // 컬럼 헤더 텍스트
                    name: 'CODE',            // 데이터 필드명 (대소문자 주의)
                    editor: 'text'           // 에디터 타입 ('text', 'checkbox', 'select' 등)
                },
                {
                    header: '이름',
                    name: 'NAME',
                    editor: 'text'
                },
                {
                    header: '설명',
                    name: 'DESCRIPTION',
                    editor: 'text'
                },
                {
                    header: '사용여부',
                    name: 'IS_ACTIVE',
                    editor: GridUtil.createYesNoEditor()  // Y/N 선택 에디터
                },
                {
                    header: '정렬',
                    name: 'SORT_ORDER',
                    editor: 'text'
                },
                // [✓] 확장 지점: 필요한 컬럼을 추가하세요
                // {
                //     header: '등록일자',
                //     name: 'REG_DATE',
                //     editor: false  // 편집 불가능한 컬럼
                // },
                
                {
                    header: '타입',
                    name: 'ROW_TYPE'         // ROW_TYPE은 필수 필드입니다 (insert, update, select 구분)
                }
            ];

            // 그리드 생성 - GridUtil 사용
            // [✓] 수정 방법: 필요에 따라 옵션을 조정하세요.
            gridInstance = GridUtil.registerGrid({
                id: 'dataGrid',              // HTML 그리드 요소 ID
                columns: columns,            // 위에서 정의한 컬럼
                data: [],                    // 초기 데이터는 빈 배열
                draggable: true,             // 드래그 가능 여부 (false로 변경 가능)
                displayColumnName: 'SORT_ORDER', // 드래그 시 자동 정렬에 사용할 컬럼명
                hiddenColumns: ['ROW_TYPE'], // 숨길 컬럼명 배열
                gridOptions: {
                    rowHeaders: ['rowNum', 'checkbox'], // 행 헤더 옵션 
                    // [✓] 확장 지점: 그리드 옵션을 추가하려면 여기에 작성하세요
                    // header: {
                    //     height: 40,        // 헤더 높이
                    //     complexColumns: [] // 복합 컬럼 정의
                    // },
                    // bodyHeight: 400,       // 그리드 본문 높이 (자동 스크롤)
                    // minBodyHeight: 200     // 최소 본문 높이
                }
            });

            // 행 더블클릭 이벤트 처리 - 키 컬럼 제어
            // [✓] 수정 방법: 키 컬럼명이 'CODE'가 아니라면 변경하세요.
            GridUtil.setupKeyColumnControl('dataGrid', 'CODE');
            
            // [✓] 확장 지점: 그리드 이벤트 핸들러 추가
            // 행 클릭 이벤트 등록 예시
            // GridUtil.onRowClick('dataGrid', function(rowData) {
            //     console.log('선택된 행:', rowData);
            //     // 행 선택 시 처리할 로직 작성
            // });

            // 초기 데이터 로드
            await searchData();

            console.log('그리드 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('그리드 초기화 오류:', error);
            throw error;
        }
    }

    //===========================================================================
    // 데이터 처리 함수 - 비즈니스 로직에 맞게 수정하세요
    //===========================================================================

    /**
     * 행 추가 함수
     * 그리드에 새로운 행을 추가합니다.
     * 
     * [✓] 수정 필요: 신규 행의 기본값을 프로젝트에 맞게 설정하세요.
     */
    async function appendRow() {
        try {
            console.log('행 추가');

            // 신규 행 데이터
            // [✓] 수정 필요: 필드명과 기본값을 프로젝트에 맞게 변경하세요.
            const newRowData = {
                CODE: '',                // 코드 (빈 값)
                NAME: '',                // 이름 (빈 값)
                DESCRIPTION: '',         // 설명 (빈 값)
                IS_ACTIVE: 'Y',          // 사용여부 (기본값 'Y')
                SORT_ORDER: ''           // 정렬 (빈 값)
                // [✓] 확장 지점: 필요한 필드를 추가하세요
                // REG_DATE: new Date().toISOString().split('T')[0]  // 등록일자 (오늘 날짜)
                
                // ROW_TYPE은 GridUtil.addNewRow()에서 자동으로 'insert'로 추가됩니다
            };

            // GridUtil 사용하여 행 추가
            // [✓] 수정 방법: 행 추가 위치나 포커스 설정을 변경할 수 있습니다.
            await GridUtil.addNewRow('dataGrid', newRowData, {
                at: 0,           // 위치 (0: 최상단, -1: 최하단)
                focus: true      // 추가 후 포커스 설정 (false로 변경 가능)
            });
        } catch (error) {
            console.error('행 추가 중 오류:', error);
            await AlertUtil.showError('행 추가 오류', '행 추가 중 오류가 발생했습니다.');
        }
    }

    /**
     * 데이터 검색 함수
     * API를 호출하여 데이터를 검색하고 그리드에 표시합니다.
     * 
     * [✓] 수정 필요: 검색 파라미터와 API 응답 처리를 프로젝트에 맞게 수정하세요.
     */
    async function searchData() {
        try {
            // 검색 조건 수집
            // [✓] 수정 필요: 검색 폼의 구조에 맞게 수정하세요.
            const searchParam = {};
            
            // 검색어가 있는 경우
            const searchInput = document.getElementById('searchInput');
            if (searchInput && searchInput.value) {
                searchParam.keyword = searchInput.value;
            }
            
            // 검색 유형이 있는 경우
            const searchTypeSelect = document.getElementById('searchType');
            if (searchTypeSelect) {
                searchParam.searchType = searchTypeSelect.value;
            }
            
            // [✓] 확장 지점: 추가 검색 조건이 있는 경우 여기에 작성하세요
            // 날짜 범위 검색 예시
            // const startDate = document.getElementById('startDate');
            // const endDate = document.getElementById('endDate');
            // if (startDate && startDate.value) {
            //     searchParam.startDate = startDate.value;
            // }
            // if (endDate && endDate.value) {
            //     searchParam.endDate = endDate.value;
            // }
            
            console.log('데이터 검색 시작. 검색 조건:', searchParam);

            // API 호출 - ApiUtil 사용
            // [✓] 수정 방법: API 호출 경로나 로딩 메시지를 변경할 수 있습니다.
            const response = await ApiUtil.getWithLoading(
                API_URLS.LIST,           // API 경로
                searchParam,             // 검색 파라미터
                '데이터 검색 중...'      // 로딩 메시지
            );

            // [✓] 수정 필요: API 응답 구조에 맞게 수정하세요
            
            // 방법 1: REST API 응답 처리 (보통 데이터 배열을 직접 반환)
            const data = Array.isArray(response) ? response : (response.data || []);
            
            /*
            // 방법 2: 일반 컨트롤러 응답 처리 (보통 success, message, data 객체 형태로 반환)
            let data = [];
            if (response.success) {
                data = response.data || [];
            } else {
                await AlertUtil.showWarning('검색 실패', response.message || '데이터 조회 중 오류가 발생했습니다.');
            }
            */

            // 그리드 데이터 설정
            const grid = GridUtil.getGrid('dataGrid');
            if (grid) {
                grid.resetData(data);
            }

            // [✓] 확장 지점: 검색 결과에 대한 추가 처리가 필요한 경우 여기에 작성하세요
            // 예: 검색 결과 카운트 표시
            // updateResultCount(data.length);
            
            console.log('검색 완료. 결과:', data.length, '건');
            return data;
        } catch (error) {
            console.error('검색 중 오류:', error);
            await AlertUtil.showError('검색 오류', '데이터 검색 중 오류가 발생했습니다.');
            
            // 그리드 초기화
            const grid = GridUtil.getGrid('dataGrid');
            if (grid) {
                grid.resetData([]);
            }
            
            throw error;
        }
    }

    /**
     * 데이터 저장 함수
     * 그리드의 변경된 데이터를 수집하여 API로 저장합니다.
     * 
     * [✓] 수정 필요: API 요청 방식과 데이터 구조를 프로젝트에 맞게 수정하세요.
     */
    async function saveData() {
        try {
            console.log('데이터 저장 시작');

            const grid = GridUtil.getGrid('dataGrid');
            if (!grid) {
                throw new Error('dataGrid를 찾을 수 없습니다.');
            }

            // 마지막으로 입력한 셀에 대한 값 반영을 위해 포커스 해제 - 반드시 필요합니다
            grid.blur();

            // 변경된 데이터 추출 - GridUtil 사용
            const changes = await GridUtil.extractChangedData('dataGrid');
            const modifiedData = [...changes.insert, ...changes.update];

            if (modifiedData.length === 0) {
                await AlertUtil.showWarning("알림", "수정된 내용이 없습니다.");
                return false;
            }

            // [✓] 수정 필요: API 요청 방식에 맞게 수정하세요
            
            // 방법 1: REST API 방식 (각 항목을 개별 객체로 전송)
            const batchData = modifiedData.map(row => ({
                code: row.CODE,               
                name: row.NAME,               
                description: row.DESCRIPTION || '',
                isActive: row.IS_ACTIVE,
                sortOrder: row.SORT_ORDER,
                action: row.ROW_TYPE          // insert, update, delete 구분 (필수)
            }));
            
            /*
            // 방법 2: 일반 컨트롤러 방식 (변경 데이터를 배열로 묶어 전송)
            const saveData = {
                dataList: modifiedData.map(row => ({
                    code: row.CODE,               
                    name: row.NAME,               
                    description: row.DESCRIPTION || '',
                    isActive: row.IS_ACTIVE,
                    sortOrder: row.SORT_ORDER,
                    action: row.ROW_TYPE
                }))
            };
            */

            // 유효성 검사
            // [✓] 수정 필요: 필요한 유효성 검사 규칙을 추가하세요.
            for (const item of batchData) {
                // 코드는 필수항목
                if (ValidationUtil.isEmpty(item.code)) {
                    await AlertUtil.notifyValidationError("유효성 오류", "코드는 필수입니다.");
                    return false;
                }
                // 이름은 필수항목
                if (ValidationUtil.isEmpty(item.name)) {
                    await AlertUtil.notifyValidationError("유효성 오류", "이름은 필수입니다.");
                    return false;
                }
                // 사용여부는 필수항목
                if (ValidationUtil.isEmpty(item.isActive)) {
                    await AlertUtil.notifyValidationError("유효성 오류", "사용여부는 필수입니다.");
                    return false;
                }
                
                // [✓] 확장 지점: 추가 유효성 검사가 필요한 경우 여기에 작성하세요
                // 코드 형식 검사 예시 
                // if (!ValidationUtil.matches(item.code, /^[A-Z]{2}-\d{4}$/)) {
                //     await AlertUtil.notifyValidationError("유효성 오류", "코드 형식이 올바르지 않습니다. (예: AB-1234)");
                //     return false;
                // }
            }

            // [✓] 수정 필요: API 호출 코드를 프로젝트에 맞게 수정하세요
            
            // 방법 1: REST API 방식
            const response = await ApiUtil.processRequest(
                () => ApiUtil.post(API_URLS.BATCH, batchData), {
                    loadingMessage: '데이터 저장 중...',
                    successMessage: "데이터가 저장되었습니다.",
                    errorMessage: "데이터 저장 중 오류가 발생했습니다.",
                    successCallback: searchData
                }
            );
            
            /*
            // 방법 2: 일반 컨트롤러 방식
            const response = await ApiUtil.processRequest(
                () => ApiUtil.post(API_URLS.SAVE, saveData), {
                    loadingMessage: '데이터 저장 중...',
                    successMessage: "데이터가 저장되었습니다.",
                    errorMessage: "데이터 저장 중 오류가 발생했습니다.",
                    successCallback: searchData
                }
            );
            */

            return response.success;
        } catch (error) {
            console.error('데이터 저장 오류:', error);
            await AlertUtil.notifySaveError("저장 실패", "데이터 저장 중 오류가 발생했습니다.");
            return false;
        }
    }

    /**
     * 행 삭제 함수
     * 선택된 행을 삭제합니다.
     * 
     * [✓] 수정 필요: 삭제 API 호출 부분을 프로젝트에 맞게 수정하세요.
     */
    async function deleteRows() {
        try {
            // 선택된 행 ID 확인
            const grid = GridUtil.getGrid('dataGrid');
            const selectedRowKeys = grid.getCheckedRowKeys();
            
            if (selectedRowKeys.length === 0) {
                await AlertUtil.showWarning('알림', '삭제할 항목을 선택해주세요.');
                return false;
            }
            
            // 선택된 코드 목록 생성
            // [✓] 수정 필요: 삭제 시 사용할 키 필드명을 변경하세요. (기본값: 'CODE')
            const selectedCodes = [];
            for (const rowKey of selectedRowKeys) {
                const code = grid.getValue(rowKey, "CODE");
                if (code) selectedCodes.push(code);
            }
            
            if (selectedCodes.length === 0) {
                await AlertUtil.showWarning('알림', '유효한 코드를 찾을 수 없습니다.');
                return false;
            }
            
            // 행 삭제 UI 처리 (두 방식 모두 동일)
            const result = await GridUtil.deleteSelectedRows('dataGrid', {
                confirmTitle: "삭제 확인",
                confirmMessage: "선택한 항목을 삭제하시겠습니까?",
                onBeforeDelete: async () => {
                    // [✓] 확장 지점: 삭제 전 검증이 필요한 경우 여기에 작성하세요
                    // 예: 사용 중인 항목 검증
                    // const usageCheck = await checkItemUsage(selectedCodes);
                    // if (usageCheck.inUse) {
                    //     await AlertUtil.showWarning('삭제 불가', '사용 중인 항목은 삭제할 수 없습니다.');
                    //     return false;
                    // }
                    return true; // 삭제 진행
                },
                onAfterDelete: async () => {
                    // [✓] 수정 필요: API 호출 코드를 프로젝트에 맞게 수정하세요
                    
                    // 방법 1: REST API 방식 (각 항목별로 DELETE 요청)
                    try {
                        const deleteRequests = selectedCodes.map(code => 
                            async () => ApiUtil.del(API_URLS.DELETE(code))
                        );
                        
                        await ApiUtil.withLoading(async () => {
                            await Promise.all(deleteRequests.map(req => req()));
                        }, '데이터 삭제 중...');
                        
                        await AlertUtil.notifyDeleteSuccess('삭제 완료', '데이터가 삭제되었습니다.');
                        await searchData();
                    } catch (apiError) {
                        console.error('삭제 API 호출 중 오류:', apiError);
                        await AlertUtil.notifyDeleteError('삭제 실패', '데이터 삭제 중 API 오류가 발생했습니다.');
                    }
                    
                    /*
                    // 방법 2: 일반 컨트롤러 방식 (여러 코드를 한 번에 전송)
                    try {
                        const response = await ApiUtil.postWithLoading(
                            API_URLS.DELETE,
                            { codeList: selectedCodes },
                            '데이터 삭제 중...'
                        );
                        
                        if (response.success) {
                            await AlertUtil.notifyDeleteSuccess('삭제 완료', '데이터가 삭제되었습니다.');
                            await searchData();
                        } else {
                            await AlertUtil.notifyDeleteError('삭제 실패', response.message || '데이터 삭제 중 오류가 발생했습니다.');
                        }
                    } catch (apiError) {
                        console.error('삭제 API 호출 중 오류:', apiError);
                        await AlertUtil.notifyDeleteError('삭제 실패', '데이터 삭제 중 API 오류가 발생했습니다.');
                    }
                    */
                }
            });
            
            return result;
        } catch (error) {
            console.error('데이터 삭제 오류:', error);
            await AlertUtil.notifyDeleteError('삭제 실패', '데이터 삭제 중 오류가 발생했습니다.');
            return false;
        }
    }

    /**
     * 검색 폼 초기화 함수
     * 검색 조건을 초기화하고 그리드를 비웁니다.
     * 
     * [✓] 수정 필요: 검색 폼 구조에 맞게 수정하세요.
     */
    async function resetForm() {
        try {
            // SearchUtil 사용하여 검색 폼 초기화
            // [✓] 수정 방법: 검색 폼 ID가 'searchForm'이 아니라면 변경하세요.
            const searchForm = document.getElementById('searchForm');
            if (searchForm) {
                await SearchUtil.resetSearchForm({
                    formId: 'searchForm',
                    gridId: 'dataGrid',
                    callback: () => {
                        console.log('검색 폼 초기화 완료');
                        // [✓] 확장 지점: 폼 초기화 후 추가 작업이 필요한 경우 여기에 작성하세요
                    }
                });
            } else {
                // 검색 폼이 없는 경우 기본 초기화
                // [✓] 수정 필요: 초기화할 필드 ID를 프로젝트에 맞게 변경하세요.
                const searchInput = document.getElementById('searchInput');
                if (searchInput) {
                    searchInput.value = '';
                }
                
                const searchTypeSelect = document.getElementById('searchType');
                if (searchTypeSelect) {
                    searchTypeSelect.selectedIndex = 0;
                }
                
                // [✓] 확장 지점: 추가 필드 초기화가 필요한 경우 여기에 작성하세요
                // const startDate = document.getElementById('startDate');
                // if (startDate) {
                //     startDate.value = '';
                // }
                
                // 그리드 초기화
                const grid = GridUtil.getGrid('dataGrid');
                if (grid) {
                    grid.resetData([]);
                }
            }
            
            // 초기 데이터 재검색
            await searchData();
            
            return true;
        } catch (error) {
            console.error('폼 초기화 오류:', error);
            await AlertUtil.showError('초기화 오류', '검색 폼 초기화 중 오류가 발생했습니다.');
            return false;
        }
    }

    /**
     * 그리드 인스턴스 반환 함수
     * 외부에서 그리드 인스턴스에 직접 접근할 수 있습니다.
     * 
     * [✓] 수정 방법: 이 함수는 수정할 필요가 없습니다.
     */
    function getGridInstance() {
        return gridInstance;
    }
    
    /**
     * CSV 내보내기 함수 - 예시 함수
     * 그리드 데이터를 CSV 파일로 내보냅니다.
     * 
     * [✓] 확장 지점: 필요할 경우 이 함수를 구현하세요.
     */
    // async function exportToCsv() {
    //     try {
    //         const grid = GridUtil.getGrid('dataGrid');
    //         if (!grid) {
    //             throw new Error('dataGrid를 찾을 수 없습니다.');
    //         }
    //         
    //         // 그리드 데이터 가져오기
    //         const data = grid.getData();
    //         if (data.length === 0) {
    //             await AlertUtil.showWarning('알림', '내보낼 데이터가 없습니다.');
    //             return false;
    //         }
    //         
    //         // CSV 생성 로직 구현
    //         // ... 
    //         
    //         return true;
    //     } catch (error) {
    //         console.error('CSV 내보내기 오류:', error);
    //         await AlertUtil.showError('내보내기 오류', 'CSV 내보내기 중 오류가 발생했습니다.');
    //         return false;
    //     }
    // }

    //===========================================================================
    // 공개 API - 외부에서 접근 가능한 메서드
    //===========================================================================
    // [✓] 수정 방법: 외부에 공개할 함수를 추가하거나 제거하세요.
    return {
        // 초기화 및 기본 기능
        init,                // 모듈 초기화
        
        // 데이터 처리 함수
        searchData,          // 데이터 검색
        appendRow,           // 행 추가
        saveData,            // 데이터 저장
        deleteRows,          // 행 삭제
        resetForm,           // 검색 폼 초기화
        
        // 유틸리티 함수
        getGridInstance      // 그리드 인스턴스 반환
        
        // [✓] 확장 지점: 외부에 공개할 추가 함수를 여기에 작성하세요
        // exportToCsv          // CSV 내보내기 (구현 후 주석 해제)
    };
})();

//===========================================================================
// DOM 로드 시 초기화 - 이 부분은 수정하지 마세요
//===========================================================================
document.addEventListener('DOMContentLoaded', async function() {
    try {
        // 모듈 초기화
        await SimpleGridManager.init();
    } catch (error) {
        console.error('초기화 중 오류 발생:', error);
        if (window.AlertUtil) {
            await AlertUtil.showError('초기화 오류', '모듈 초기화 중 오류가 발생했습니다.');
        } else {
            alert('모듈 초기화 중 오류가 발생했습니다.');
        }
    }
});