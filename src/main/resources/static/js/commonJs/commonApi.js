/**
 * 리팩토링된 ApiUtil - 비동기 API 통신 공통 라이브러리 (async/await 기반)
 * 
 * 모든 API 호출 함수를 async/await 패턴으로 통일하여 비동기 코드의 가독성과 유지보수성을 높입니다.
 * 오류 처리를 try-catch 블록으로 일관되게 구현하고, 로딩 표시와 결과 처리를 자동화합니다.
 * 
 * @version 1.0.0
 * @since 2025-03-24
 */

const ApiUtil = (function() {
    // =============================
    // 기본 HTTP 요청 함수
    // =============================

    /**
     * 기본 API 호출 내부 함수 (모든 API 요청의 기반)
     * 
     * @param {Object} options - API 호출 옵션
     * @param {string} options.url - API URL
     * @param {string} options.method - HTTP 메서드 (GET, POST, PUT, DELETE)
     * @param {Object} [options.data] - 요청 데이터
     * @param {boolean} [options.isJson=true] - JSON 요청 여부
     * @param {Function} [options.beforeSend] - 요청 전 실행할 함수
     * @returns {Promise<any>} API 응답을 담은 Promise
     * @private
     */
    async function _callApi(options) {
        // 요청 시작 로깅
        console.log(`API 호출: ${options.method} ${options.url}`, options.data || '');

        // AJAX 요청 옵션 구성
        const ajaxOptions = {
            url: options.url,
            type: options.method,
            beforeSend: options.beforeSend
        };

        // JSON 요청인 경우 Content-Type 설정 및 데이터 직렬화
        if (options.isJson !== false && options.data) {
            ajaxOptions.contentType = "application/json";
            ajaxOptions.data = JSON.stringify(options.data);
        } else if (options.data) {
            ajaxOptions.data = options.data;
        }

        try {
            // jQuery의 ajax 메서드를 Promise로 감싸서 사용
            return await new Promise((resolve, reject) => {
                $.ajax({
                    ...ajaxOptions,
                    success: function(response) {
                        console.log(`API 응답(${options.url}):`, response);
                        resolve(response);
                    },
                    error: function(error) {
                        console.error(`API 오류(${options.url}):`, error);
                        reject(error);
                    }
                });
            });
        } catch (error) {
            // 오류 로깅 및 재 throw
            console.error(`API 호출 중 오류 발생(${options.url}):`, error);
            throw error; // 상위 호출자가 try/catch로 처리할 수 있도록 오류 전파
        }
    }

    /**
     * GET 요청 함수
     * 
     * @param {string} url - API URL
     * @param {Object} [params] - 쿼리 파라미터
     * @param {Object} [options] - 추가 옵션
     * @returns {Promise<any>} API 응답을 담은 Promise
     */
    async function get(url, params, options = {}) {
        return await _callApi({
            url: url,
            method: 'GET',
            data: params,
            isJson: false,
            ...options
        });
    }

    /**
     * POST 요청 함수
     * 
     * @param {string} url - API URL
     * @param {Object} data - 요청 데이터
     * @param {Object} [options] - 추가 옵션
     * @returns {Promise<any>} API 응답을 담은 Promise
     */
    async function post(url, data, options = {}) {
        return await _callApi({
            url: url,
            method: 'POST',
            data: data,
            ...options
        });
    }

    /**
     * PUT 요청 함수
     * 
     * @param {string} url - API URL
     * @param {Object} data - 요청 데이터
     * @param {Object} [options] - 추가 옵션
     * @returns {Promise<any>} API 응답을 담은 Promise
     */
    async function put(url, data, options = {}) {
        return await _callApi({
            url: url,
            method: 'PUT',
            data: data,
            ...options
        });
    }

    /**
     * DELETE 요청 함수
     * 
     * @param {string} url - API URL
     * @param {Object} [params] - 요청 파라미터
     * @param {Object} [options] - 추가 옵션
     * @returns {Promise<any>} API 응답을 담은 Promise
     */
    async function del(url, params, options = {}) {
        return await _callApi({
            url: url,
            method: 'DELETE',
            data: params,
            isJson: !!params && typeof params === 'object', // 객체인 경우만 JSON으로 처리
            ...options
        });
    }

    // =============================
    // 로딩 표시 관련 함수
    // =============================

    /**
     * 로딩 표시와 함께 API 호출을 수행하는 함수
     * 
     * @param {Function} apiCall - API 호출 함수 (Promise 반환)
     * @param {string} [loadingMessage='처리 중...'] - 로딩 메시지
     * @returns {Promise<any>} API 응답을 담은 Promise
     */
    async function withLoading(apiCall, loadingMessage = '처리 중...') {
        try {
            if (window.UIUtil) {
                await UIUtil.toggleLoading(true, loadingMessage);
            }

            return await apiCall();
        } finally {
            if (window.UIUtil) {
                await UIUtil.toggleLoading(false);
            }
        }
    }

    /**
     * GET 요청 (로딩 표시 포함)
     * 
     * @param {string} url - API URL
     * @param {Object} [params] - 쿼리 파라미터
     * @param {string} [loadingMessage='불러오는 중...'] - 로딩 메시지
     * @param {Object} [options] - 추가 옵션
     * @returns {Promise<any>} API 응답을 담은 Promise
     */
    async function getWithLoading(url, params, loadingMessage = '불러오는 중...', options) {
        return await withLoading(() => get(url, params, options), loadingMessage);
    }

    /**
     * POST 요청 (로딩 표시 포함)
     * 
     * @param {string} url - API URL
     * @param {Object} data - 요청 데이터
     * @param {string} [loadingMessage='저장 중...'] - 로딩 메시지
     * @param {Object} [options] - 추가 옵션
     * @returns {Promise<any>} API 응답을 담은 Promise
     */
    async function postWithLoading(url, data, loadingMessage = '저장 중...', options) {
        return await withLoading(() => post(url, data, options), loadingMessage);
    }

    /**
     * PUT 요청 (로딩 표시 포함)
     * 
     * @param {string} url - API URL
     * @param {Object} data - 요청 데이터
     * @param {string} [loadingMessage='수정 중...'] - 로딩 메시지
     * @param {Object} [options] - 추가 옵션
     * @returns {Promise<any>} API 응답을 담은 Promise
     */
    async function putWithLoading(url, data, loadingMessage = '수정 중...', options) {
        return await withLoading(() => put(url, data, options), loadingMessage);
    }

    /**
     * DELETE 요청 (로딩 표시 포함)
     * 
     * @param {string} url - API URL
     * @param {Object} [params] - 요청 파라미터
     * @param {string} [loadingMessage='삭제 중...'] - 로딩 메시지
     * @param {Object} [options] - 추가 옵션
     * @returns {Promise<any>} API 응답을 담은 Promise
     */
    async function delWithLoading(url, params, loadingMessage = '삭제 중...', options) {
        return await withLoading(() => del(url, params, options), loadingMessage);
    }

    // =============================
    // 오류 처리 및 결과 처리 함수
    // =============================

    /**
     * API 에러 처리 함수
     * 
     * @param {Object} error - 에러 객체
     * @param {string} [title='오류가 발생했습니다'] - 알림창 제목
     * @param {Function} [callback] - 에러 처리 후 콜백
     */
	async function handleApiError(error, title = '오류가 발생했습니다', callback) {
	    console.error('API 에러:', error);

	    // 응답 상세 정보 로깅
	    if (error.responseJSON) {
	        console.error('에러 응답:', error.responseJSON);
	    } else if (error.responseText) {
	        console.error('에러 텍스트:', error.responseText);
	    }

	    // 알림창으로 오류 표시
	    await AlertUtil.notifySaveError(title, error.message || null, callback);
	}

    /**
     * 완전한 API 요청 처리 함수 (로딩, 결과 알림, 오류 처리 포함)
     * 
     * @param {Function} apiCall - API 호출 함수 (Promise 반환)
     * @param {Object} options - 옵션
     * @param {string} [options.loadingMessage='처리 중...'] - 로딩 메시지
     * @param {string} [options.successMessage] - 성공 메시지 (설정 시 알림창 표시)
     * @param {string} [options.errorMessage='오류가 발생했습니다'] - 오류 메시지
     * @param {Function} [options.successCallback] - 성공 시 콜백
     * @param {Function} [options.errorCallback] - 오류 시 콜백
     * @returns {Promise<any>} 처리 결과 데이터
     */
	async function processRequest(apiCall, options = {}) {
	    const {
	        loadingMessage = '처리 중...',
	        successMessage,
	        errorMessage = '오류가 발생했습니다',
	        successCallback,
	        errorCallback
	    } = options;

	    // 로딩 객체 참조 저장
	    let loadingRef = null;

	    try {
	        // 로딩 표시 시작
	        loadingRef = AlertUtil.showLoading(loadingMessage);

	        // API 호출 실행
	        const response = await apiCall();

	        // 비즈니스 로직 성공 여부 확인 (response.success 필드 사용)
	        if (response.success) {
	            // 성공 메시지가 지정된 경우 알림창 표시
	            if (successMessage) {
	                await AlertUtil.notifySaveSuccess('저장 성공', successMessage, successCallback);
	            } else if (successCallback) {
	                // 알림창 없이 콜백만 실행
	                successCallback(response);
	            }
	        } else {
	            // API는 성공했지만 비즈니스 로직 실패
	            const message = response.message || errorMessage;
	            await AlertUtil.showWarning("처리 실패", message, errorCallback);
	        }

	        return response;
	    } catch (error) {
	        // API 호출 자체가 실패한 경우
	        await handleApiError(error, errorMessage, errorCallback);
	        throw error; // 추가 처리를 위해 오류 다시 throw
	    } finally {
	        // 로딩 표시 종료
	        if (loadingRef) {
	            loadingRef.close();
	        }
	    }
	}

    // =============================
    // 일괄 처리 관련 함수
    // =============================

    /**
     * API 요청을 순차적으로 실행하는 일괄 처리 함수
     * 
     * @param {Array<Function>} requests - API 요청 함수 배열 (각 함수는 Promise 반환)
     * @param {Object} [options] - 옵션
     * @param {boolean} [options.stopOnError=true] - 오류 발생 시 중단 여부
     * @param {string} [options.loadingMessage='일괄 처리 중...'] - 로딩 메시지
     * @returns {Promise<Array>} 각 요청의 결과 배열
     */
    async function batch(requests, options = {}) {
        const {
            stopOnError = true,
            loadingMessage = '일괄 처리 중...'
        } = options;

        if (!Array.isArray(requests) || requests.length === 0) {
            return [];
        }

        try {
            // 로딩 표시 시작
            if (window.UIUtil) {
                await UIUtil.toggleLoading(true, loadingMessage);
            }

            const results = [];

            // 요청을 순차적으로 처리
            for (const request of requests) {
                try {
                    const result = await request();
                    results.push({
                        success: true,
                        data: result
                    });
                } catch (error) {
                    results.push({
                        success: false,
                        error
                    });

                    // 오류 발생 시 중단 옵션에 따라 처리
                    if (stopOnError) {
                        throw {
                            partialResults: results,
                            error
                        };
                    }
                    // stopOnError가 false면 계속 진행
                }
            }

            return results;
        } finally {
            // 로딩 표시 종료
            if (window.UIUtil) {
                await UIUtil.toggleLoading(false);
            }
        }
    }

    /**
     * API 요청을 병렬로 실행하는 함수
     * 
     * @param {Array<Function>} requests - API 요청 함수 배열 (각 함수는 Promise 반환)
     * @param {string} [loadingMessage='데이터 처리 중...'] - 로딩 메시지
     * @returns {Promise<Array>} 각 요청의 결과 배열
     */
    async function parallel(requests, loadingMessage = '데이터 처리 중...') {
        if (!Array.isArray(requests) || requests.length === 0) {
            return [];
        }

        try {
            // 로딩 표시 시작
            if (window.UIUtil) {
                await UIUtil.toggleLoading(true, loadingMessage);
            }

            // 모든 요청을 Promise.all로 병렬 처리
            // 개별 요청 실패 시에도 전체가 실패하지 않도록 에러 처리
            const promises = requests.map(request =>
                request().catch(error => ({
                    error
                }))
            );

            return await Promise.all(promises);
        } finally {
            // 로딩 표시 종료
            if (window.UIUtil) {
                await UIUtil.toggleLoading(false);
            }
        }
    }

    // 공개 API - 모듈의 공개 인터페이스
    return {
        // 기본 HTTP 요청 함수
        get,                // HTTP GET 요청
        post,               // HTTP POST 요청
        put,                // HTTP PUT 요청
        del,                // HTTP DELETE 요청

        // 로딩 표시 함수
        withLoading,        // 커스텀 API 호출 + 로딩 표시
        getWithLoading,     // GET + 로딩 표시
        postWithLoading,    // POST + 로딩 표시
        putWithLoading,     // PUT + 로딩 표시
        delWithLoading,     // DELETE + 로딩 표시

        // 통합 API 처리 함수
        processRequest,     // 완전한 API 요청 처리 (로딩+결과알림+오류처리)
        batch,              // 여러 API 요청 순차 처리
        parallel,           // 여러 API 요청 병렬 처리

        // 유틸리티 함수
        handleApiError      // API 오류 처리
    };
})();