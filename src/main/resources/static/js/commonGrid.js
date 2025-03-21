/**
 * grid-common.js - 공통 그리드 유틸리티 라이브러리
 * 
 * Toast UI Grid를 쉽게 사용하기 위한 공통 기능들을 제공합니다.
 * 업무 로직을 제외한 순수 그리드 관련 기능들만 포함하고 있습니다.
 * 
 * @author [작성자명]
 * @version 1.0
 * @since [날짜]
 */

/**
 * GridUtil - 그리드 관련 공통 기능을 제공하는 유틸리티
 */
const GridUtil = (function() {
    /**
     * 그리드 인스턴스를 저장하는 객체
     * @private
     * @type {Object}
     */
    const _gridInstances = {};
    
    /**
     * CSRF 토큰 정보
     * @private
     * @type {Object}
     */
    const _csrf = {
        header: document.querySelector('meta[name="_csrf_header"]')?.content || '',
        token: document.querySelector('meta[name="_csrf"]')?.content || ''
    };
    
    /**
     * 그리드 생성 및 초기화 함수
     * 
     * @param {Object} options - 그리드 생성 옵션
     * @param {string} options.id - 그리드를 생성할 HTML 요소의 ID
     * @param {Array} options.columns - 컬럼 정의 배열
     * @param {Array} [options.data=[]] - 초기 데이터 배열
     * @param {Array} [options.hiddenColumns=[]] - 숨길 컬럼명 배열
     * @param {boolean} [options.draggable=false] - 행 드래그 가능 여부
     * @param {string} [options.displayColumnName=''] - 드래그 시 자동 정렬에 사용할 컬럼명
     * @param {Object} [options.events={}] - 이벤트 핸들러 정의 객체
     * @param {Object} [options.gridOptions={}] - Toast UI Grid 추가 옵션
     * 
     * @returns {Object} 생성된 그리드 인스턴스
     * 
     * @example
     * // 기본 그리드 생성
     * const grid = GridUtil.createGrid({
     *     id: 'myGrid',
     *     columns: [
     *         {header: '이름', name: 'name', editor: 'text'},
     *         {header: '나이', name: 'age', editor: 'text'},
     *         {header: '부서', name: 'department', editor: 'text'}
     *     ]
     * });
     * 
     * // 이벤트 핸들러가 있는 그리드 생성
     * const grid = GridUtil.createGrid({
     *     id: 'myGrid',
     *     columns: [...],
     *     events: {
     *         click: function(ev) {
     *             console.log('클릭한 행:', ev.rowKey);
     *         },
     *         dblclick: function(ev) {
     *             console.log('더블클릭한 행:', ev.rowKey);
     *         }
     *     }
     * });
     */
    function createGrid(options) {
        // 필수 옵션 확인
        if (!options.id || !options.columns) {
            console.error('그리드 ID와 컬럼 정의는 필수입니다.');
            return null;
        }
        
        const Grid = tui.Grid;
        
        // 기본 그리드 옵션
        const defaultOptions = {
            scrollX: true,
            scrollY: true,
            rowHeaders: ['rowNum'],
            selectionUnit: 'row',
            ...options.gridOptions
        };
        
        // 그리드 생성
        const gridInstance = new Grid({
            el: document.getElementById(options.id),
            columns: options.columns,
            data: options.data || [],
            draggable: options.draggable || false,
            ...defaultOptions
        });
        
        // 숨김 컬럼 처리
        if (options.hiddenColumns && options.hiddenColumns.length > 0) {
            options.hiddenColumns.forEach(column => {
                gridInstance.hideColumn(column);
            });
        }
        
        // 드래그앤드롭 이벤트 (순서 컬럼이 있는 경우)
        if (options.draggable && options.displayColumnName) {
            gridInstance.on('drop', () => {
                const rowData = gridInstance.getData();
                rowData.forEach((row, index) => {
                    gridInstance.setValue(row.rowKey, options.displayColumnName, index + 1);
                });
            });
        }
        
        // 사용자 정의 이벤트 등록
        if (options.events) {
            Object.keys(options.events).forEach(eventName => {
                if (typeof options.events[eventName] === 'function') {
                    gridInstance.on(eventName, options.events[eventName]);
                }
            });
        }
        
        // 인스턴스 저장
        _gridInstances[options.id] = gridInstance;
        
        return gridInstance;
    }
    
    /**
     * 행 추가 함수
     * 
     * @param {string} gridId - 그리드 ID
     * @param {Object} rowData - 추가할 행 데이터
     * @param {Object} [options] - 추가 옵션
     * @param {number} [options.at=0] - 추가할 위치 (기본값: 맨 위)
     * @param {boolean} [options.focus=true] - 추가 후 포커스 여부
     * @param {string} [options.rowType='insert'] - 추가되는 행의 타입 값 (ROW_TYPE 필드에 자동 설정)
     * 
     * @returns {number} 추가된 행의 rowKey
     * 
     * @example
     * // 기본 행 추가
     * GridUtil.appendRow('myGrid', {
     *     name: '',
     *     age: '',
     *     department: ''
     * });
     * 
     * // 특정 위치에 행 추가
     * GridUtil.appendRow('myGrid', { name: '', age: '' }, { at: 3, focus: false });
     */
    function appendRow(gridId, rowData, options = {}) {
        const grid = _gridInstances[gridId];
        if (!grid) {
            console.error(`ID가 '${gridId}'인 그리드를 찾을 수 없습니다.`);
            return -1;
        }
        
        const rowOptions = {
            at: options.at !== undefined ? options.at : 0,
            focus: options.focus !== undefined ? options.focus : true
        };
        
        // ROW_TYPE 필드 자동 설정 (지정되지 않은 경우)
        if (!rowData.ROW_TYPE && options.rowType) {
            rowData.ROW_TYPE = options.rowType;
        } else if (!rowData.ROW_TYPE) {
            rowData.ROW_TYPE = 'insert';
        }
        
        return grid.appendRow(rowData, rowOptions);
    }
    
    /**
     * 데이터 저장 함수
     * 
     * @param {Object} options - 저장 옵션
     * @param {string} options.gridId - 그리드 ID
     * @param {string} options.url - 저장 요청 URL
     * @param {Function} [options.beforeSave] - 저장 전 데이터 가공 함수
     * @param {Function} [options.onSuccess] - 저장 성공 시 콜백 함수
     * @param {Function} [options.onError] - 저장 실패 시 콜백 함수
     * @param {Object} [options.validation] - 유효성 검사 규칙
     * @param {boolean} [options.showAlert=true] - 알림창 표시 여부
     * 
     * @returns {Promise} 저장 작업 Promise
     * 
     * @example
     * // 기본 저장
     * GridUtil.saveGridData({
     *     gridId: 'myGrid',
     *     url: '/api/save'
     * });
     * 
     * // 유효성 검사 및 데이터 가공이 있는 저장
     * GridUtil.saveGridData({
     *     gridId: 'myGrid',
     *     url: '/api/save',
     *     validation: {
     *         name: { required: true, message: '이름은 필수입니다' }
     *     },
     *     beforeSave: function(data) {
     *         // 데이터 가공
     *         return data.map(row => ({
     *             ...row,
     *             timestamp: new Date().getTime()
     *         }));
     *     },
     *     onSuccess: function(response) {
     *         console.log('저장 성공:', response);
     *     }
     * });
     */
    function saveGridData(options) {
        const grid = _gridInstances[options.gridId];
        if (!grid) {
            console.error(`ID가 '${options.gridId}'인 그리드를 찾을 수 없습니다.`);
            return Promise.reject(new Error('그리드를 찾을 수 없습니다.'));
        }
        
        // 포커스 해제하여 마지막 입력값 반영
        grid.blur();
        
        // 수정된 데이터 가져오기
        const modifiedRows = grid.getModifiedRows();
        const createdRows = modifiedRows.createdRows;   // 신규 행
        const updatedRows = modifiedRows.updatedRows;   // 수정된 행
        const modifiedData = [...createdRows, ...updatedRows];
        
        // 수정된 데이터가 없으면 알림 표시 후 종료
        if (modifiedData.length === 0) {
            if (options.showAlert !== false) {
                alert('수정된 내용이 없습니다.');
            }
            return Promise.resolve();
        }
        
        // 저장할 데이터 준비
        let data = modifiedData;
        
        // 업데이트된 행의 ROW_TYPE 변경
        updatedRows.forEach(row => {
            if (!row.ROW_TYPE || row.ROW_TYPE === 'select') {
                row.ROW_TYPE = 'update';
            }
        });
        
        // 사용자 정의 데이터 가공 함수 적용
        if (typeof options.beforeSave === 'function') {
            data = options.beforeSave(data);
            
            // 가공 함수가 null/undefined를 반환하면 저장 취소
            if (!data) {
                console.info('beforeSave 함수에서 저장이 취소되었습니다.');
                return Promise.resolve();
            }
        }
        
        // 유효성 검사
        if (options.validation) {
            for (const row of data) {
                for (const [field, rule] of Object.entries(options.validation)) {
                    const value = row[field];
                    
                    // 필수 입력 검사
                    if (rule.required && (value === undefined || value === null || value === '')) {
                        if (options.showAlert !== false) {
                            alert(rule.message || `${field} 필드는 필수입니다.`);
                        }
                        return Promise.resolve();
                    }
                    
                    // 사용자 정의 유효성 검사
                    if (rule.validate && typeof rule.validate === 'function') {
                        const isValid = rule.validate(value, row);
                        if (!isValid) {
                            if (options.showAlert !== false) {
                                alert(rule.message || `${field} 필드가 유효하지 않습니다.`);
                            }
                            return Promise.resolve();
                        }
                    }
                }
            }
        }
        
        // AJAX 요청으로 서버에 저장
        return new Promise((resolve, reject) => {
            $.ajax({
                url: options.url,
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(data),
                beforeSend: function(xhr) {
                    if (_csrf.header && _csrf.token) {
                        xhr.setRequestHeader(_csrf.header, _csrf.token);
                    }
                },
                success: function(response) {
                    if (typeof options.onSuccess === 'function') {
                        options.onSuccess(response, data);
                    } else if (options.showAlert !== false) {
                        alert('저장되었습니다.');
                    }
                    resolve(response);
                },
                error: function(error) {
                    if (typeof options.onError === 'function') {
                        options.onError(error);
                    } else if (options.showAlert !== false) {
                        alert('저장 중 오류가 발생했습니다.');
                        console.error('저장 오류:', error);
                    }
                    reject(error);
                }
            });
        });
    }
    
    /**
     * 데이터 로드 함수
     * 
     * @param {Object} options - 로드 옵션
     * @param {string} options.gridId - 그리드 ID
     * @param {string} options.url - 데이터 요청 URL
     * @param {Object} [options.params={}] - 요청 파라미터
     * @param {Function} [options.beforeLoad] - 로드 전 처리 함수
     * @param {Function} [options.afterLoad] - 로드 후 처리 함수
     * 
     * @returns {Promise} 로드 작업 Promise
     * 
     * @example
     * // 기본 데이터 로드
     * GridUtil.loadGridData({
     *     gridId: 'myGrid',
     *     url: '/api/data'
     * });
     * 
     * // 파라미터가 있는 데이터 로드
     * GridUtil.loadGridData({
     *     gridId: 'myGrid',
     *     url: '/api/data',
     *     params: { deptCode: 'HR', active: true },
     *     afterLoad: function(response) {
     *         console.log('로드된 데이터 수:', response.length);
     *     }
     * });
     */
    function loadGridData(options) {
        const grid = _gridInstances[options.gridId];
        if (!grid) {
            console.error(`ID가 '${options.gridId}'인 그리드를 찾을 수 없습니다.`);
            return Promise.reject(new Error('그리드를 찾을 수 없습니다.'));
        }
        
        // 요청 파라미터 준비
        let params = options.params || {};
        
        // 사용자 정의 전처리 함수 적용
        if (typeof options.beforeLoad === 'function') {
            params = options.beforeLoad(params) || params;
        }
        
        // AJAX 요청으로 서버에서 데이터 로드
        return new Promise((resolve, reject) => {
            $.ajax({
                url: options.url,
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(params),
                beforeSend: function(xhr) {
                    if (_csrf.header && _csrf.token) {
                        xhr.setRequestHeader(_csrf.header, _csrf.token);
                    }
                },
                success: function(response) {
                    // 그리드 데이터 업데이트
                    grid.resetData(response);
                    
                    // 사용자 정의 후처리 함수 적용
                    if (typeof options.afterLoad === 'function') {
                        options.afterLoad(response);
                    }
                    
                    resolve(response);
                },
                error: function(error) {
                    console.error('데이터 로드 오류:', error);
                    reject(error);
                }
            });
        });
    }
    
    /**
     * 그리드 검색 설정 함수
     * 
     * @param {Object} options - 검색 옵션
     * @param {string} options.gridId - 그리드 ID
     * @param {string} options.url - 검색 요청 URL
     * @param {string|HTMLElement} options.inputElement - 검색어 입력 요소 또는 ID
     * @param {Function} [options.beforeSearch] - 검색 전 처리 함수
     * @param {Function} [options.afterSearch] - 검색 후 처리 함수
     * @param {boolean} [options.useEnter=true] - Enter 키 이벤트 사용 여부
     * 
     * @returns {Function} 검색 실행 함수
     * 
     * @example
     * // 기본 검색 설정
     * const search = GridUtil.setupSearch({
     *     gridId: 'myGrid',
     *     url: '/api/search',
     *     inputElement: 'searchInput'
     * });
     * 
     * // 검색 버튼에 이벤트 연결
     * document.getElementById('searchBtn').addEventListener('click', search);
     */
    function setupSearch(options) {
        const grid = _gridInstances[options.gridId];
        if (!grid) {
            console.error(`ID가 '${options.gridId}'인 그리드를 찾을 수 없습니다.`);
            return null;
        }
        
        // 입력 요소 가져오기
        const inputElement = typeof options.inputElement === 'string' 
            ? document.getElementById(options.inputElement) 
            : options.inputElement;
            
        if (!inputElement) {
            console.error('검색어 입력 요소를 찾을 수 없습니다.');
            return null;
        }
        
        // Enter 키 이벤트 처리
        if (options.useEnter !== false) {
            inputElement.addEventListener('keyup', function(e) {
                if (e.key === 'Enter') {
                    performSearch();
                }
            });
        }
        
        // 검색 실행 함수
        function performSearch() {
            const value = inputElement.value || '';
            
            // 검색 파라미터 준비
            let params = { value };
            
            // 사용자 정의 전처리 함수 적용
            if (typeof options.beforeSearch === 'function') {
                params = options.beforeSearch(params) || params;
            }
            
            // AJAX 요청으로 검색 결과 로드
            $.ajax({
                url: options.url,
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(params),
                beforeSend: function(xhr) {
                    if (_csrf.header && _csrf.token) {
                        xhr.setRequestHeader(_csrf.header, _csrf.token);
                    }
                },
                success: function(response) {
                    // 그리드 데이터 업데이트
                    grid.resetData(response);
                    
                    // 사용자 정의 후처리 함수 적용
                    if (typeof options.afterSearch === 'function') {
                        options.afterSearch(response, params);
                    }
                },
                error: function(error) {
                    console.error('검색 오류:', error);
                }
            });
        }
        
        // 검색 함수 반환
        return performSearch;
    }
    
    /**
     * 그리드 셀 비활성화 함수
     * 
     * @param {string} gridId - 그리드 ID
     * @param {number|Array} rowKey - 행 키 또는 행 키 배열
     * @param {string|Array} columnNames - 컬럼명 또는 컬럼명 배열
     * 
     * @example
     * // 단일 셀 비활성화
     * GridUtil.disableCell('myGrid', 0, 'name');
     * 
     * // 여러 셀 비활성화
     * GridUtil.disableCell('myGrid', 0, ['name', 'age']);
     * 
     * // 여러 행의 셀 비활성화
     * GridUtil.disableCell('myGrid', [0, 1, 2], 'name');
     */
    function disableCell(gridId, rowKey, columnNames) {
        const grid = _gridInstances[gridId];
        if (!grid) {
            console.error(`ID가 '${gridId}'인 그리드를 찾을 수 없습니다.`);
            return;
        }
        
        const rowKeys = Array.isArray(rowKey) ? rowKey : [rowKey];
        const columns = Array.isArray(columnNames) ? columnNames : [columnNames];
        
        rowKeys.forEach(key => {
            columns.forEach(column => {
                grid.disableCell(key, column);
            });
        });
    }
    
    /**
     * 그리드 셀 활성화 함수
     * 
     * @param {string} gridId - 그리드 ID
     * @param {number|Array} rowKey - 행 키 또는 행 키 배열
     * @param {string|Array} columnNames - 컬럼명 또는 컬럼명 배열
     * 
     * @example
     * // 단일 셀 활성화
     * GridUtil.enableCell('myGrid', 0, 'name');
     * 
     * // 여러 셀 활성화
     * GridUtil.enableCell('myGrid', 0, ['name', 'age']);
     */
    function enableCell(gridId, rowKey, columnNames) {
        const grid = _gridInstances[gridId];
        if (!grid) {
            console.error(`ID가 '${gridId}'인 그리드를 찾을 수 없습니다.`);
            return;
        }
        
        const rowKeys = Array.isArray(rowKey) ? rowKey : [rowKey];
        const columns = Array.isArray(columnNames) ? columnNames : [columnNames];
        
        rowKeys.forEach(key => {
            columns.forEach(column => {
                grid.enableCell(key, column);
            });
        });
    }
    
    /**
     * 날짜 에디터 생성 함수
     * 
     * @param {Object} [props={}] - 날짜 에디터 속성
     * @param {string} [props.format='yyyy-MM-dd'] - 날짜 형식
     * @param {Object} [props.options={}] - 추가 날짜 에디터 옵션
     * 
     * @returns {Object} 날짜 에디터 정의 객체
     * 
     * @example
     * // 컬럼 정의에서 사용
     * const columns = [
     *     {header: '이름', name: 'name', editor: 'text'},
     *     {header: '생년월일', name: 'birthDate', editor: GridUtil.createDateEditor()}
     * ];
     * 
     * // 형식을 지정한 날짜 에디터
     * {header: '입사일', name: 'hireDate', editor: GridUtil.createDateEditor({format: 'yyyy/MM/dd'})}
     */
    function createDateEditor(props = {}) {
        const defaultFormat = 'yyyy-MM-dd';
        const format = props.format || defaultFormat;
        
        return {
            type: 'datePicker',
            options: {
                format: format,
                language: 'ko',
                ...props.options
            }
        };
    }
    
    /**
     * 드롭다운 에디터 생성 함수
     * 
     * @param {Array} items - 드롭다운 항목 배열
     * @param {Object} [options={}] - 추가 옵션
     * 
     * @returns {Object} 드롭다운 에디터 정의 객체
     * 
     * @example
     * // 기본 드롭다운 에디터
     * const columns = [
     *     {header: '부서', name: 'department', editor: GridUtil.createDropdownEditor([
     *         {text: '인사', value: 'HR'},
     *         {text: '개발', value: 'DEV'},
     *         {text: '영업', value: 'SALES'}
     *     ])}
     * ];
     */
    function createDropdownEditor(items, options = {}) {
        return {
            type: 'select',
            options: { 
                listItems: items,
                ...options
            }
        };
    }
    
    /**
     * 행 추가(복수) 함수
     * 
     * @param {string} gridId - 그리드 ID
     * @param {Array} dataList - 추가할 행 데이터 배열
     * @param {Object} [options={}] - 추가 옵션
     * 
     * @example
     * // 여러 행 추가
     * GridUtil.appendRows('myGrid', [
     *     {name: '홍길동', age: 30},
     *     {name: '김철수', age: 25}
     * ]);
     */
    function appendRows(gridId, dataList, options = {}) {
        const grid = _gridInstances[gridId];
        if (!grid) {
            console.error(`ID가 '${gridId}'인 그리드를 찾을 수 없습니다.`);
            return;
        }
        
        if (!Array.isArray(dataList) || dataList.length === 0) {
            return;
        }
        
        // ROW_TYPE 자동 설정 (지정되지 않은 경우)
        const rowType = options.rowType || 'insert';
        
        dataList.forEach(rowData => {
            if (!rowData.ROW_TYPE) {
                rowData.ROW_TYPE = rowType;
            }
            
            grid.appendRow(rowData);
        });
    }
    
    /**
     * 그리드 인스턴스 가져오기
     * 
     * @param {string} gridId - 그리드 ID
     * @returns {Object} Toast UI Grid 인스턴스
     * 
     * @example
     * // 그리드 인스턴스 직접 접근
     * const grid = GridUtil.getGrid('myGrid');
     * if (grid) {
     *     // 인스턴스 메서드 직접 호출
     *     const data = grid.getData();
     * }
     */
    function getGrid(gridId) {
        return _gridInstances[gridId];
    }
    
    /**
     * 그리드 행 선택 이벤트 설정 함수
     * 
     * @param {string} gridId - 그리드 ID
     * @param {Function} callback - 행 선택 시 호출될 콜백 함수
     * 
     * @example
     * // 행 선택 시 처리
     * GridUtil.onRowClick('myGrid', function(rowData, rowKey) {
     *     console.log('선택된 행:', rowData);
     *     document.getElementById('selectedName').textContent = rowData.name;
     * });
     */
    function onRowClick(gridId, callback) {
        const grid = _gridInstances[gridId];
        if (!grid) {
            console.error(`ID가 '${gridId}'인 그리드를 찾을 수 없습니다.`);
            return;
        }
        
        grid.on('click', ev => {
            const rowData = grid.getRow(ev.rowKey);
            callback(rowData, ev.rowKey);
        });
    }
    
    /**
     * 그리드 행 더블클릭 이벤트 설정 함수
     * 
     * @param {string} gridId - 그리드 ID
     * @param {Function} callback - 행 더블클릭 시 호출될 콜백 함수
     * 
     * @example
     * // 행 더블클릭 시 처리
     * GridUtil.onRowDblClick('myGrid', function(rowData, rowKey) {
     *     console.log('더블클릭된 행:', rowData);
     *     openDetailPopup(rowData.id);
     * });
     */
    function onRowDblClick(gridId, callback) {
        const grid = _gridInstances[gridId];
        if (!grid) {
            console.error(`ID가 '${gridId}'인 그리드를 찾을 수 없습니다.`);
            return;
        }
        
        grid.on('dblclick', ev => {
            const rowData = grid.getRow(ev.rowKey);
            callback(rowData, ev.rowKey);
        });
    }
    
    /**
     * ROW_TYPE에 따라 특정 컬럼 비활성화/활성화 처리 함수
     * 조회 데이터('select')는 Key 컬럼 비활성화, 신규/수정 데이터는 활성화 처리
     * 
     * @param {string} gridId - 그리드 ID
     * @param {number} rowKey - 행 키
     * @param {string|Array} keyColumns - 비활성화할 키 컬럼명 또는 컬럼명 배열
     * 
     * @example
     * // ROW_TYPE에 따른 자동 처리 설정
     * GridUtil.setupKeyColumnControl('myGrid', 'id');
     * 
     * // 더블클릭 이벤트에서 사용
     * grid.on('dblclick', ev => {
     *     GridUtil.handleKeyColumnByRowType('myGrid', ev.rowKey, 'id');
     * });
     */
    function handleKeyColumnByRowType(gridId, rowKey, keyColumns) {
        const grid = _gridInstances[gridId];
        if (!grid) {
            console.error(`ID가 '${gridId}'인 그리드를 찾을 수 없습니다.`);
            return;
        }
        
        const rowType = grid.getValue(rowKey, "ROW_TYPE");
        const columns = Array.isArray(keyColumns) ? keyColumns : [keyColumns];
        
        if (rowType === "select") {
            // 조회 데이터는 키 컬럼 비활성화
            columns.forEach(column => {
                grid.disableCell(rowKey, column);
            });
        } else {
            // 신규/수정 데이터는 키 컬럼 활성화
            columns.forEach(column => {
                grid.enableCell(rowKey, column);
            });
        }
    }
    
    /**
     * 그리드에 더블클릭 시 ROW_TYPE 기반 키 컬럼 제어 설정 함수
     * 
     * @param {string} gridId - 그리드 ID
     * @param {string|Array} keyColumns - 키 컬럼명 또는 컬럼명 배열
     * 
     * @example
     * // 그리드 생성 후 키 컬럼 제어 설정
     * GridUtil.createGrid({...});
     * GridUtil.setupKeyColumnControl('myGrid', 'id');
     */
    function setupKeyColumnControl(gridId, keyColumns) {
        const grid = _gridInstances[gridId];
        if (!grid) {
            console.error(`ID가 '${gridId}'인 그리드를 찾을 수 없습니다.`);
            return;
        }
        
        grid.on('dblclick', ev => {
            handleKeyColumnByRowType(gridId, ev.rowKey, keyColumns);
        });
    }
    
    /**
     * 행 삭제 함수
     * 
     * @param {string} gridId - 그리드 ID
     * @param {number|Array} rowKeys - 삭제할 행 키 또는 행 키 배열
     * 
     * @example
     * // 단일 행 삭제
     * GridUtil.removeRow('myGrid', 0);
     * 
     * // 여러 행 삭제
     * GridUtil.removeRow('myGrid', [0, 1, 2]);
     */
    function removeRow(gridId, rowKeys) {
        const grid = _gridInstances[gridId];
        if (!grid) {
            console.error(`ID가 '${gridId}'인 그리드를 찾을 수 없습니다.`);
            return;
        }
        
        const keys = Array.isArray(rowKeys) ? rowKeys : [rowKeys];
        grid.removeRows(keys);
    }
    
    /**
     * 선택된 행 삭제 함수
     * 
     * @param {string} gridId - 그리드 ID
     * @param {boolean} [confirm=true] - 삭제 전 확인 여부
     * 
     * @returns {Promise} 결과 Promise (삭제 성공 시 true, 취소 시 false)
     * 
     * @example
     * // 선택된 행 삭제 (확인 대화상자 표시)
     * GridUtil.deleteSelectedRows('myGrid').then(result => {
     *     if (result) {
     *         console.log('삭제 완료');
     *     }
     * });
     * 
     * // 확인 없이 삭제
     * GridUtil.deleteSelectedRows('myGrid', false);
     */
    function deleteSelectedRows(gridId, confirm = true) {
        const grid = _gridInstances[gridId];
        if (!grid) {
            console.error(`ID가 '${gridId}'인 그리드를 찾을 수 없습니다.`);
            return Promise.resolve(false);
        }
        
        const selectedRowKeys = grid.getCheckedRowKeys();
        
        if (selectedRowKeys.length === 0) {
            alert('삭제할 항목을 선택해주세요.');
            return Promise.resolve(false);
        }
        
        if (confirm) {
            return new Promise(resolve => {
                if (window.confirm('선택한 항목을 삭제하시겠습니까?')) {
                    grid.removeRows(selectedRowKeys);
                    resolve(true);
                } else {
                    resolve(false);
                }
            });
        } else {
            grid.removeRows(selectedRowKeys);
            return Promise.resolve(true);
        }
    }
    
    /**
     * 셀 렌더러 생성 - 체크박스
     * 
     * @returns {Object} 체크박스 렌더러 정의
     * 
     * @example
     * // 컬럼 정의에서 사용
     * const columns = [
     *     {header: '선택', name: 'selected', renderer: GridUtil.createCheckboxRenderer()}
     * ];
     */
    function createCheckboxRenderer() {
        return {
            type: 'checkbox'
        };
    }
    
    /**
     * 셀 렌더러 생성 - 사용자 정의 HTML
     * 
     * @param {Function} renderer - 렌더링 함수 (props를 받아 HTML 문자열 반환)
     * @returns {Object} 사용자 정의 렌더러 정의
     * 
     * @example
     * // 버튼이 포함된 셀 렌더러
     * const buttonRenderer = GridUtil.createCustomRenderer(function(props) {
     *     const id = props.value;
     *     return '<button class="btn btn-sm btn-primary" onclick="viewDetail(' + id + ')">상세</button>';
     * });
     * 
     * const columns = [
     *     {header: '상세보기', name: 'id', renderer: buttonRenderer}
     * ];
     */
    function createCustomRenderer(renderer) {
        if (typeof renderer !== 'function') {
            console.error('렌더러는 함수여야 합니다.');
            return null;
        }
        
        return {
            type: 'html',
            options: {
                // eslint-disable-next-line
                formatter: renderer
            }
        };
    }
    
    /**
     * 그리드 테마 설정 함수
     * 
     * @param {string} theme - 테마명 ('default', 'striped', 'clean')
     * 
     * @example
     * // 줄무늬 테마 적용
     * GridUtil.setTheme('striped');
     */
    function setTheme(theme) {
        const Grid = tui.Grid;
        const validThemes = ['default', 'striped', 'clean'];
        
        if (validThemes.includes(theme)) {
            Grid.applyTheme(theme);
        } else {
            console.warn(`유효하지 않은 테마: ${theme}. 'default', 'striped', 'clean' 중 하나를 사용하세요.`);
        }
    }
    
    /**
     * 그리드 언어 설정 함수
     * 
     * @param {string} language - 언어 코드 ('ko', 'en')
     * 
     * @example
     * // 영어로 설정
     * GridUtil.setLanguage('en');
     */
    function setLanguage(language) {
        const Grid = tui.Grid;
        const validLanguages = ['ko', 'en'];
        
        if (validLanguages.includes(language)) {
            Grid.setLanguage(language);
        } else {
            console.warn(`유효하지 않은 언어: ${language}. 'ko', 'en' 중 하나를 사용하세요.`);
        }
    }
    
    // 공개 API
    return {
		createGrid,             // 새 그리드 생성 및 초기화
		appendRow,              // 단일 행 추가
		appendRows,             // 여러 행 한번에 추가
		saveGridData,           // 변경된 데이터 서버에 저장
		loadGridData,           // 서버에서 데이터 로드
		setupSearch,            // 검색 기능 설정
		disableCell,            // 셀 비활성화
		enableCell,             // 셀 활성화
		createDateEditor,       // 날짜 선택 에디터 생성
		createDropdownEditor,   // 드롭다운 에디터 생성
		getGrid,                // 그리드 인스턴스 가져오기
		onRowClick,             // 행 클릭 이벤트 처리
		onRowDblClick,          // 행 더블클릭 이벤트 처리
		handleKeyColumnByRowType, // ROW_TYPE에 따른 키 컬럼 제어
		setupKeyColumnControl,  // 키 컬럼 자동 제어 설정
		removeRow,              // 지정 행 삭제
		deleteSelectedRows,     // 선택된 행 삭제
		createCheckboxRenderer, // 체크박스 렌더러 생성
		createCustomRenderer,   // 사용자 정의 HTML 렌더러 생성
		setTheme,               // 그리드 테마 설정 ('default', 'striped', 'clean')
		setLanguage             // 그리드 언어 설정 ('ko', 'en')
    };
})();