package com.example.cmtProject.constants;

/**
 * 경로 상수 정의
 * URL 및 뷰 경로를 관리하기 위한 상수 클래스
 */
public class PathConstants {
    // URL 경로
    public static final String APPROVAL_BASE = "/eapproval";
    public static final String APPROVAL_LIST = "/approvalList";
    public static final String DOCUMENT_NEW = "/document/new";
    public static final String DOCUMENT_EDIT = "/document/edit";
    public static final String DOCUMENT_VIEW = "/document/view";
    public static final String DOCUMENT_SAVE = "/document/save";
    public static final String DOCUMENT_APPROVE = "/document/{docId}/approve";
    public static final String PENDING = "/pending";
    public static final String COMPLETED = "/completed";
    public static final String REJECTED = "/rejected";
    
    // 뷰 경로
    public static final String VIEW_APPROVAL_LIST = "erp/eapproval/approvalList";
    public static final String VIEW_DOCUMENT_FORM = "erp/eapproval/documentForm";
    public static final String VIEW_DOCUMENT_VIEW = "erp/eapproval/document-view";
    public static final String VIEW_PENDING_LIST = "erp/eapproval/pending-list";
    public static final String VIEW_COMPLETED_LIST = "erp/eapproval/completed-list";
    public static final String VIEW_REJECTED_LIST = "erp/eapproval/rejected-list";
}