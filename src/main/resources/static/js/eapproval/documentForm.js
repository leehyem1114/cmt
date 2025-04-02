///**
// * documentForm.js - 통합 문서 폼 스크립트
// * 
// * 기안서 작성 페이지의 모든 모듈을 통합하고 초기화하는 스크립트입니다.
// * 
// * @version 1.0.0
// */
//
//// 페이지 로드 시 초기화
//$(async function() {
//    console.log('기안서 작성 페이지 초기화 시작');
//    
//    try {
//        // 문서 폼 관리자 초기화
//        DocumentFormManager.initialize();
//        
//        // 양식 내용 로더 초기화
//        FormContentLoader.initialize();
//        
//        // 에디터 초기화
//        FormContentLoader.initializeEditor();
//        
//        // 결재선 관리자 초기화
//        const initialApprovalLines = window.documentData?.approvalLines || [];
//        ApprovalLineManager.initialize(initialApprovalLines);
//        
//        // 첨부파일 관리자 초기화
//        AttachmentManager.initialize();
//        
//        console.log('기안서 작성 페이지 초기화 완료');
//    } catch (error) {
//        console.error('페이지 초기화 오류:', error);
//        await AlertUtil.showError('초기화 오류', '페이지를 불러오는 중 오류가 발생했습니다.');
//    }
//});