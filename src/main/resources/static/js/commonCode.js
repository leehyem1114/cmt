// commonCode.js - 공통코드 그리드 생성
document.addEventListener('DOMContentLoaded', function() {
    // 콘솔에 데이터 출력하여 확인
    console.log("Grid 초기화 시 데이터:", window.commonList);
    
    // commonCodeGrid의 컬럼 정의
    const commonCodeColumns = [
        { header: '코드', name: 'CMN_CODE', width: 100 },
        { header: '코드명', name: 'CMN_NAME', width: 150 },
        { header: '설명', name: 'CMN_CONTENT' },
        { header: '사용여부', name: 'CMN_CODE_IS_ACTIVE', width: 100 }
    ];

    // commonCodeDeGrid의 컬럼 정의 (예시로 추가된 컬럼)
    const commonCodeDeColumns = [
        { header: '상세코드', name: 'CMN_DETAIL_CODE', width: 100 },
        { header: '코드', name: 'CMN_CODE', width: 200 }, // 새로운 컬럼 추가
        { header: '상세코드명', name: 'CMN_DETAIL_NAME', width: 200 }, // 새로운 컬럼 추가
        { header: '정렬', name: 'CMN_DETAIL_SORT_ORDER', width: 100 },
        { header: '설명', name: 'CMN_DETAIL_CONTENT' },
        { header: '사용여부', name: 'CMN_DETAIL_CODE_IS_ACTIVE', width: 100 }
    ];

    // commonCodeGrid 그리드 생성
    GridUtil.createGrid({
        id: 'commonCodeGrid',
        columns: commonCodeColumns, // commonCodeGrid에 맞는 컬럼 사용
        data: window.commonList || [], // Thymeleaf에서 전달된 데이터 사용
        gridOptions: {
            rowHeaders: ['checkbox']
        }
    });

    // commonCodeDeGrid 그리드 생성n
    GridUtil.createGrid({
        id: 'commonCodeDeGrid',
        columns: commonCodeDeColumns, // commonCodeDeGrid에 맞는 컬럼 사용
        data: window.commonDeList  || [], // Thymeleaf에서 전달된 데이터 사용
        gridOptions: {
            rowHeaders: ['checkbox']
        }
    });
});
