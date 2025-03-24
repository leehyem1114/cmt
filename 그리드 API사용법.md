# REST API vs 일반 컨트롤러 방식 비교 가이드

이 가이드는 SimpleGridManager 템플릿에서 REST API와 일반 컨트롤러 두 가지 방식의 차이점을 설명합니다.

## 1. API URL 구조 차이

### REST API 방식
REST API는 자원(resource)을 중심으로 URL을 구성하고, HTTP 메서드(GET, POST, PUT, DELETE)로 동작을 표현합니다.

```javascript
const API_URLS = {
    LIST: '/api/items',                  // GET 요청으로 목록 조회
    BATCH: '/api/items/batch',           // POST 요청으로 일괄 저장
    DELETE: (code) => `/api/items/${code}` // DELETE 요청으로 항목 삭제
};
```

### 일반 컨트롤러 방식
일반 컨트롤러는 작업(operation)을 URL에 직접 표현하고, 주로 GET/POST 메서드만 사용합니다.

```javascript
const API_URLS = {
    LIST: '/controller/item/list',       // 목록 조회
    SAVE: '/controller/item/save',       // 저장 (삽입/수정 모두)
    DELETE: '/controller/item/delete'    // 삭제
};
```

## 2. 데이터 전송 방식 차이

### REST API 방식
- 각 항목을 개별 객체로 구성하여 배열로 전송
- HTTP 메서드(GET, POST, PUT, DELETE)를 적절히 사용
- 리소스 ID를 URL에 포함 (예: `/api/items/123`)

```javascript
// 저장 요청 예시
const batchData = modifiedData.map(row => ({
    code: row.CODE,               
    name: row.NAME,
    // ... 다른 필드들
    action: row.ROW_TYPE  // insert, update, delete 구분
}));

await ApiUtil.post(API_URLS.BATCH, batchData);

// 삭제 요청 예시
await ApiUtil.del(API_URLS.DELETE(code));  // DELETE 메서드 사용
```

### 일반 컨트롤러 방식
- 데이터를 상위 객체로 감싸서 전송
- 주로 POST 메서드 사용
- 리소스 ID를 요청 본문에 포함

```javascript
// 저장 요청 예시
const saveData = {
    dataList: modifiedData.map(row => ({
        code: row.CODE,               
        name: row.NAME,
        // ... 다른 필드들
        action: row.ROW_TYPE
    }))
};

await ApiUtil.post(API_URLS.SAVE, saveData);

// 삭제 요청 예시
await ApiUtil.post(API_URLS.DELETE, { codeList: selectedCodes });  // POST 메서드 사용
```

## 3. 응답 형식 차이

### REST API 방식
```json
// 목록 조회 응답 예시 - 보통 데이터 배열을 직접 반환
[
  { "code": "001", "name": "항목1", "isActive": "Y" },
  { "code": "002", "name": "항목2", "isActive": "Y" }
]

// 또는 최소한의 메타데이터와 함께 반환
{
  "data": [
    { "code": "001", "name": "항목1", "isActive": "Y" },
    { "code": "002", "name": "항목2", "isActive": "Y" }
  ],
  "totalCount": 2
}
```

### 일반 컨트롤러 방식
```json
// 보통 성공/실패 여부와 메시지를 포함한 통일된 응답 형식 사용
{
  "success": true,
  "message": "정상적으로 처리되었습니다",
  "data": [
    { "code": "001", "name": "항목1", "isActive": "Y" },
    { "code": "002", "name": "항목2", "isActive": "Y" }
  ],
  "totalCount": 2
}
```

## 4. 응답 처리 코드 차이

### REST API 방식
```javascript
// 응답이 직접 배열이거나 data 필드에 배열이 있는 경우
const data = Array.isArray(response) ? response : (response.data || []);
grid.resetData(data);
```

### 일반 컨트롤러 방식
```javascript
// success 필드로 성공 여부 확인
if (response.success) {
    const data = response.data || [];
    grid.resetData(data);
} else {
    await AlertUtil.showWarning('실패', response.message);
}
```

## 5. 프로젝트에 맞는 방식 선택하기

### REST API 방식 선택 시기
- 표준 REST API 규칙을 따르는 백엔드 시스템과 연동할 때
- 마이크로서비스 아키텍처나 API 게이트웨이를 사용하는 프로젝트
- 다양한 HTTP 메서드를 지원하는 환경

### 일반 컨트롤러 방식 선택 시기
- 전통적인 서버 렌더링 기반 MVC 프레임워크(Spring, JSP 등) 사용 시
- 레거시 시스템과의 통합이 필요한 경우
- HTTP 메서드 제한이 있는 환경(POST/GET만 지원)

## 6. 템플릿 적용 방법

1. 프로젝트 환경에 맞는 방식 선택 (REST API 또는 일반 컨트롤러)
2. 선택한 방식의 코드 블록 주석 해제 및 미사용 방식 주석 처리
3. URL과 요청/응답 구조를 실제 백엔드 API에 맞게 수정
4. 필요한 필드명과 유효성 검사 로직 추가

## 7. 예시: 방식 전환하기

REST API 방식에서 일반 컨트롤러 방식으로 전환:

1. API URL 상수 변경
```javascript
// 이 부분 주석 처리
// const API_URLS = {
//     LIST: '/api/items',
//     BATCH: '/api/items/batch',
//     DELETE: (code) => `/api/items/${code}`
// };

// 이 부분 주석 해제
const API_URLS = {
    LIST: '/controller/item/list',
    SAVE: '/controller/item/save',
    DELETE: '/controller/item/delete'
};
```

2. 저장 함수 데이터 처리 부분 변경
```javascript
// 이 부분 주석 처리
// const batchData = modifiedData.map(row => ({
//     code: row.CODE,
//     ...
// }));

// 이 부분 주석 해제
const saveData = {
    dataList: modifiedData.map(row => ({
        code: row.CODE,
        ...
    }))
};
```

3. API 호출 코드 변경
```javascript
// 이 부분 주석 처리
// await ApiUtil.post(API_URLS.BATCH, batchData);

// 이 부분 주석 해제
await ApiUtil.post(API_URLS.SAVE, saveData);
```

4. 응답 처리 코드 변경
```javascript
// 이 부분 주석 처리
// const data = Array.isArray(response) ? response : (response.data || []);

// 이 부분 주석 해제
let data = [];
if (response.success) {
    data = response.data || [];
} else {
    await AlertUtil.showWarning('실패', response.message);
}
```
