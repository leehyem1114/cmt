package com.example.cmtProject.constants;

/**
 * 경로 상수 정의
 * URL 및 뷰 경로를 관리하기 위한 상수 클래스
 */
public class PathConstants {
    // 기본 경로
    public static final String APPROVAL_BASE = "/eapproval";
    public static final String WAREHOUSE_BASE = "/warehouse";
    
    // URL 경로
    public static final String DOCUMENT_LIST = "/documents";
    public static final String DOCUMENT_NEW = "/document/new";
    public static final String DOCUMENT_EDIT = "/document/edit";
    public static final String DOCUMENT_VIEW = "/document/view";
    public static final String DOCUMENT_SAVE = "/document/save";
    public static final String DOCUMENT_APPROVE = "/document/{docId}/approve";
    public static final String PENDING = "/pending";
    public static final String COMPLETED = "/completed";
    public static final String REJECTED = "/rejected";
    public static final String FORM_BASE = "/forms";
    public static final String FORM_NEW = "/new";
    public static final String FORM_EDIT = "/edit";
    public static final String FORM_VIEW = "/view";
    public static final String WAREHOUSE_VIEW = "/view";
    
    
    // API 경로
    public static final String API_BASE = "/api/eapproval";
    public static final String API_DOCUMENT = "/document";
    public static final String API_DOCUMENTS = "/documents";
    public static final String API_FORM = "/form";
    public static final String API_APPROVERS = "/approvers";
    public static final String API_FORMS = "/forms";
    
    // 뷰 경로 (카멜케이스로 변경)
    public static final String VIEW_DOCUMENT_LIST = "erp/eapproval/documentList";
    public static final String VIEW_DOCUMENT_FORM = "erp/eapproval/documentForm";
    public static final String VIEW_DOCUMENT_VIEW = "erp/eapproval/documentView";
    public static final String VIEW_PENDING_LIST = "erp/eapproval/pendingList";
    public static final String VIEW_COMPLETED_LIST = "erp/eapproval/completedList";
    public static final String VIEW_REJECTED_LIST = "erp/eapproval/rejectedList";
    public static final String VIEW_FORM_LIST = "erp/eapproval/docFormList";
    public static final String VIEW_FORM_EDIT = "erp/eapproval/docFormEdit";
    public static final String VIEW_FORM_VIEW = "erp/eapproval/docFormView";
    public static final String VIEW_WAREHOUEW_VIEW ="mes/wareHouse/wareHouseList";
    
    // 리다이렉트 경로
    public static final String REDIRECT_DOCUMENT_LIST = "redirect:" + APPROVAL_BASE + DOCUMENT_LIST;
    public static final String REDIRECT_FORM_LIST = "redirect:" + APPROVAL_BASE + FORM_BASE;
}
