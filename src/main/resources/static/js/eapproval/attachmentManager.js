///**
// * attachmentManager.js - 첨부파일 관리 모듈
// * 
// * 전자결재 첨부파일 관리를 위한 기능을 제공합니다.
// * - 파일 추가/삭제
// * - 파일 목록 관리
// * - 파일 업로드 준비
// * 
// * @version 1.0.0
// */
//
//// 즉시 실행 함수로 모듈 스코프 생성
//const AttachmentManager = (function() {
//    // 첨부 파일 목록
//    const fileList = [];
//    
//    /**
//     * 모듈 초기화
//     */
//    function initialize() {
//        console.log('AttachmentManager 초기화');
//        setupEventHandlers();
//        renderFileList();
//    }
//    
//    /**
//     * 이벤트 핸들러 설정
//     */
//    function setupEventHandlers() {
//        // 파일 선택 시 추가
//        $('#fileInput').on('change', function() {
//            addFiles(this.files);
//            $(this).val(''); // 파일 선택 초기화
//        });
//        
//        // 파일 추가 버튼 클릭
//        $('#btnAddFiles').on('click', function() {
//            $('#fileInput').click();
//        });
//        
//        // 파일 삭제 버튼 (동적 요소)
//        $(document).on('click', '.btn-remove-file', function() {
//            const index = $(this).data('index');
//            removeFile(index);
//        });
//    }
//    
//    /**
//     * 파일 추가
//     * @param {FileList} newFiles - 추가할 파일 목록
//     */
//    function addFiles(newFiles) {
//        if (!newFiles || newFiles.length === 0) return;
//        
//        // FileList 객체를 배열로 변환하여 추가
//        Array.from(newFiles).forEach(file => {
//            fileList.push(file);
//        });
//        
//        renderFileList();
//    }
//    
//    /**
//     * 파일 삭제
//     * @param {number} index - 삭제할 파일 인덱스
//     */
//    function removeFile(index) {
//        if (index >= 0 && index < fileList.length) {
//            fileList.splice(index, 1);
//            renderFileList();
//        }
//    }
//    
//    /**
//     * 파일 목록 렌더링
//     */
//    function renderFileList() {
//        const $fileList = $('#fileList');
//        
//        if (fileList.length === 0) {
//            $fileList.html('<div class="text-muted text-center">첨부된 파일이 없습니다</div>');
//            return;
//        }
//        
//        let fileListHtml = '';
//        fileList.forEach((file, index) => {
//            fileListHtml += `
//                <div class="d-flex align-items-center justify-content-between mb-2">
//                    <div>
//                        <i class="bi bi-file-earmark mr-2"></i>
//                        <span class="ms-2">${file.name} (${formatFileSize(file.size)})</span>
//                    </div>
//                    <button type="button" class="btn btn-sm btn-outline-danger btn-remove-file" data-index="${index}">
//                        <i class="bi bi-x"></i>
//                    </button>
//                </div>
//            `;
//        });
//        
//        $fileList.html(fileListHtml);
//    }
//    
//    /**
//     * 파일 크기 포맷팅
//     * @param {number} bytes - 바이트 단위 파일 크기
//     * @returns {string} 포맷팅된 파일 크기
//     */
//    function formatFileSize(bytes) {
//        if (bytes === 0) return '0 Bytes';
//        const k = 1024;
//        const sizes = ['Bytes', 'KB', 'MB', 'GB'];
//        const i = Math.floor(Math.log(bytes) / Math.log(k));
//        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
//    }
//    
//    /**
//     * 현재 파일 목록 반환
//     * @returns {Array} 파일 목록 배열
//     */
//    function getFiles() {
//        return fileList;
//    }
//    
//    /**
//     * 파일 폼데이터 추가
//     * @param {FormData} formData - 폼 데이터 객체
//     */
//    function appendFilesToFormData(formData) {
//        for (let i = 0; i < fileList.length; i++) {
//            formData.append('files', fileList[i]);
//        }
//    }
//    
//    // 공개 API
//    return {
//        initialize,
//        getFiles,
//        addFiles,
//        removeFile,
//        appendFilesToFormData
//    };
//})();