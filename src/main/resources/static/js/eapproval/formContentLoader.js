
// 페이지 로드 시 양식 선택 이벤트 설정
$(document).ready(function() {
    // 양식 선택 변경 이벤트
    $('#formId').on('change', function() {
        const formId = $(this).val();
        if (!formId) return;
        
        console.log(`양식 ID ${formId} 선택됨, 내용 로드 중...`);
        
        // 로딩 표시
        const $contentCard = $('#contentEditor').closest('.card');
        $contentCard.append('<div class="loading-overlay"><div class="spinner-border text-primary" role="status"><span class="visually-hidden">로딩 중...</span></div></div>');
        
        // AJAX 요청으로 양식 내용 가져오기
        $.ajax({
            url: '/api/eapproval/form/' + formId,
            type: 'GET',
            dataType: 'json',
            success: function(response) {
                console.log('양식 응답:', response);
                
                // 로딩 표시 제거
                $contentCard.find('.loading-overlay').remove();
                
                if (response && response.success && response.data) {
                    // FORM_CONTENT 필드에서 내용 가져오기
                    const formContent = response.data.FORM_CONTENT;
                    
                    if (formContent) {
                        // Summernote 에디터에 내용 설정
                        $('#contentEditor').summernote('code', formContent);
                        console.log('양식 내용이 에디터에 로드되었습니다.');
                    } else {
                        console.warn('양식 내용이 비어 있습니다.');
                        AlertUtil.showWarning('양식 내용 없음', '선택한 양식에 기본 내용이 없습니다.');
                    }
                } else {
                    console.warn('양식 로드 실패:', response.message);
                    AlertUtil.showWarning('양식 로드 실패', response.message || '양식을 불러올 수 없습니다.');
                }
            },
            error: function(xhr, status, error) {
                console.error('양식 로드 AJAX 오류:', error);
                $contentCard.find('.loading-overlay').remove();
                AlertUtil.showWarning('양식 로드 오류', '서버 통신 중 오류가 발생했습니다.');
            }
        });
    });
    
    // 로딩 오버레이 스타일 추가
    const style = `
        <style>
            .loading-overlay {
                position: absolute;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: rgba(255, 255, 255, 0.7);
                display: flex;
                justify-content: center;
                align-items: center;
                z-index: 1000;
            }
        </style>
    `;
    $(style).appendTo('head');
    
    // 에디터 컨테이너에 상대 위치 설정 (오버레이 위치 지정용)
    $('#contentEditor').closest('.card').css('position', 'relative');
});