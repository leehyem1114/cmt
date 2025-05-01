package com.example.cmtProject.constants;

/**
 * 경로 상수 정의
 * URL 및 뷰 경로를 관리하기 위한 상수 클래스입니다.
 * 컨트롤러와 뷰 간의 일관된 경로 매핑을 위해 중앙 집중식으로 관리합니다.
 */
public class PathConstants {
    // ======================================================================
    // 기본 경로 (Base URI) - 모듈별 컨트롤러의 루트 URL 경로
    // ======================================================================
    /** 전자결재 모듈 기본 경로 */
    public static final String APPROVAL_BASE = "/eapproval";
    
    /** 창고관리 모듈 기본 경로 */
    public static final String WAREHOUSE_BASE = "/warehouse";
    
    /** 원자재 재고관리 모듈 기본 경로 */
    public static final String MATERIALINVENTORY_BASE = "/materialinventory";
    
    /** 원자재 입고관리 모듈 기본 경로 */
    public static final String MATERIALRECEIPT_BASE = "/materialreceipt";
    
    /** 제품 재고관리 모듈 기본 경로 */
    public static final String PRODUCTSINVENTORY_BASE = "/productsinventory";
    
    /** 제품 출고관리 모듈 기본 경로 */
    public static final String PRODUCTSISSUE_BASE = "/productsissue";
    
    /** 원자재 기준정보 모듈 기본 경로 */
    public static final String MATERIAL_INFO_BASE = "/material-info";
    
    // ======================================================================
    // URL 경로 (컨트롤러 매핑) - 각 컨트롤러 내부의 HTTP 요청 매핑 경로
    // ======================================================================
    /** 문서 목록 조회 경로 */
    public static final String DOCUMENT_LIST = "/documents";
    
    /** 새 문서 생성 경로 */
    public static final String DOCUMENT_NEW = "/document/new";
    
    /** 문서 편집 경로 */
    public static final String DOCUMENT_EDIT = "/document/edit";
    
    /** 문서 조회 경로 */
    public static final String DOCUMENT_VIEW = "/document/view";
    
    /** 문서 저장 경로 */
    public static final String DOCUMENT_SAVE = "/document/save";
    
    /** 문서 승인 경로 (패스 파라미터 포함) */
    public static final String DOCUMENT_APPROVE = "/document/{docId}/approve";
    
    /** 결재 대기 문서 경로 */
    public static final String PENDING = "/pending";
    
    /** 결재 완료 문서 경로 */
    public static final String COMPLETED = "/completed";
    
    /** 결재 반려 문서 경로 */
    public static final String REJECTED = "/rejected";
    
    /** 양식 기본 경로 */
    public static final String FORM_BASE = "/forms";
    
    /** 새 양식 경로 */
    public static final String FORM_NEW = "/new";
    
    /** 양식 편집 경로 */
    public static final String FORM_EDIT = "/edit";
    
    /** 양식 조회 경로 */
    public static final String FORM_VIEW = "/view";
    
    /** 창고 조회 경로 */
    public static final String WAREHOUSE_VIEW = "/view";
    
    /** 원자재 재고 조회 경로 */
    public static final String MATERIALINVENTORY_VIEW = "/view";
    
    /** 원자재 입고 조회 경로 */
    public static final String MATERIALRECEIPT_VIEW = "/view";
    
    /** 제품 재고 조회 경로 */
    public static final String PRODUCTSINVENTORY_VIEW = "/view";
    
    /** 제품 출고 조회 경로 */
    public static final String PRODUCTSISSUE_VIEW = "/view";
    
    /** 원자재 기준정보 조회 경로 */
    public static final String MATERIAL_INFO_VIEW = "/view";
    
    /** 원자재 기준정보 목록 경로 */
    public static final String MATERIAL_INFO_LIST = "/list";
    
    /** 원자재 기준정보 단일 조회 경로 (경로 변수 포함) */
    public static final String MATERIAL_INFO_SINGLE = "/{mtlCode}";
    
    /** 원자재 기준정보 일괄 저장 경로 */
    public static final String MATERIAL_INFO_BATCH = "/batch";
    
    // ======================================================================
    // API 경로 (REST 컨트롤러) - RESTful API 엔드포인트의 기본 경로
    // ======================================================================
    /** 전자결재 API 기본 경로 */
    public static final String API_BASE = "/api/eapproval";
    
    /** 문서 API 경로 (단일 문서) */
    public static final String API_DOCUMENT = "/document";
    
    /** 문서 API 경로 (다중 문서) */
    public static final String API_DOCUMENTS = "/documents";
    
    /** 양식 API 경로 */
    public static final String API_FORM = "/form";
    
    /** 결재자 API 경로 */
    public static final String API_APPROVERS = "/approvers";
    
    /** 양식 목록 API 경로 */
    public static final String API_FORMS = "/forms";
    
    /** 원자재 재고 API 기본 경로 */
    public static final String API_MATERIALINVENTORY_BASE = "/api/materialInventory";
    
    /** 원자재 입고 API 기본 경로 */
    public static final String API_MATERIALRECEIPT_BASE = "/api/materialreceipt";
    
    /** 제품 재고 API 기본 경로 */
    public static final String API_PRODUCTSINVENTORY_BASE = "/api/productsInventory";
    
    /** 제품 출고 API 기본 경로 */
    public static final String API_PRODUCTSISSUE_BASE = "/api/productsIssue";
    
    /** 원자재 기준정보 API 기본 경로 */
    public static final String API_MATERIAL_INFO_BASE = "/api/material-info";
    
    // ======================================================================
    // 뷰 경로 (Thymeleaf 템플릿) - 컨트롤러에서 반환하는 뷰 템플릿 경로
    // ======================================================================
    /** 문서 목록 뷰 경로 */
    public static final String VIEW_DOCUMENT_LIST = "erp/eapproval/documentList";
    
    /** 문서 양식 뷰 경로 */
    public static final String VIEW_DOCUMENT_FORM = "erp/eapproval/documentForm";
    
    /** 문서 상세 뷰 경로 */
    public static final String VIEW_DOCUMENT_VIEW = "erp/eapproval/documentView";
    
    /** 결재 대기 문서 목록 뷰 경로 */
    public static final String VIEW_PENDING_LIST = "erp/eapproval/pendingList";
    
    /** 결재 완료 문서 목록 뷰 경로 */
    public static final String VIEW_COMPLETED_LIST = "erp/eapproval/completedList";
    
    /** 결재 반려 문서 목록 뷰 경로 */
    public static final String VIEW_REJECTED_LIST = "erp/eapproval/rejectedList";
    
    /** 양식 목록 뷰 경로 */
    public static final String VIEW_FORM_LIST = "erp/eapproval/docFormList";
    
    /** 양식 편집 뷰 경로 */
    public static final String VIEW_FORM_EDIT = "erp/eapproval/docFormEdit";
    
    /** 양식 상세 뷰 경로 */
    public static final String VIEW_FORM_VIEW = "erp/eapproval/docFormView";
    
    /** 창고 목록 뷰 경로 */
    public static final String VIEW_WAREHOUSE_VIEW = "mes/wareHouse/wareHouseList";
    
    /** 원자재 재고 목록 뷰 경로 */
    public static final String VIEW_METINVENTORY_VIEW = "mes/inventory/metInventoryList";
    
    /** 원자재 입고 목록 뷰 경로 */
    public static final String VIEW_MATERIALRECEIPT_VIEW = "mes/inventory/materialReceipt";
    
    /** 제품 재고 목록 뷰 경로 */
    public static final String VIEW_PRODUCTSINVENTORY_VIEW = "mes/inventory/pInventoryList";
    
    /** 제품 출고 목록 뷰 경로 */
    public static final String VIEW_PRODUCTSISSUE_VIEW = "mes/inventory/productsIssue";
    
    /** 원자재 기준정보 목록 뷰 경로 */
    public static final String VIEW_MATERIAL_INFO = "mes/inventory/materialInfo";
    
    // ======================================================================
    // 리다이렉트 경로 - 리다이렉트에 사용되는 완전한 URL 경로
    // ======================================================================
    /** 문서 목록으로 리다이렉트 경로 */
    public static final String REDIRECT_DOCUMENT_LIST = "redirect:" + APPROVAL_BASE + DOCUMENT_LIST;
    
    /** 양식 목록으로 리다이렉트 경로 */
    public static final String REDIRECT_FORM_LIST = "redirect:" + APPROVAL_BASE + FORM_BASE;
    
    /** 원자재 기준정보 목록으로 리다이렉트 경로 */
    public static final String REDIRECT_MATERIAL_INFO = "redirect:" + MATERIAL_INFO_BASE;
}