// [1] 시스템 주요 설정 값: 전역 상수들을 정의합니다.
const CONSTANTS = { // 설정값 객체
    MAX_MESSAGES: 50,         // 채팅 창에 표시할 최대 메시지 개수
    TRANSLATION_DELAY: 300,   // 번역 실행 전 대기 시간 (밀리초)
    MAX_CACHE_SIZE: 100,      // 번역 캐시가 저장할 최대 항목 수
    API_ENDPOINT: 'http://localhost:8082/api/chat/send'  // 메시지 전송에 사용할 API URL
};

// [2] 문서 표시 형식 초기화: 마크다운 렌더링 옵션을 설정하는 함수입니다.
const configureMarked = () => { // configureMarked 함수 시작 (참조: 이전 코드 [2])
    marked.setOptions({ // marked 라이브러리 옵션 설정
        highlight: (code, language) => { // 코드 하이라이팅 함수: 코드와 언어 정보를 입력받음
            try { // 코드 강조 시도
                if (language && hljs.getLanguage(language)) { // 지정 언어 유효성 확인
                    return hljs.highlight(code, { language }).value; // 지정 언어로 코드 강조 적용
                }
                return hljs.highlightAuto(code).value; // 자동 감지로 코드 강조 적용
            } catch (error) { // 오류 발생 시
                console.error('코드 강조 처리 중 오류 발생:', error); // 오류 로그 출력
                return code; // 원본 코드 반환
            }
        },
        breaks: true, // 줄바꿈 문법 활성화
        gfm: true     // GitHub Flavored Markdown(GFM) 사용
    });
};

// [3] 번역 캐시 시스템: 번역 결과를 저장하고 관리하는 캐시 클래스입니다.
class TranslationCache {
    constructor(maxSize) { // 생성자: 최대 캐시 크기 설정
        this.cache = new Map(); // [3-1] 캐시 초기화: 번역 결과 저장용 Map 생성
        this.maxSize = maxSize; // 최대 캐시 크기를 설정 (입력받은 값 사용)
    }

    get(key) { // [3-2] 캐시 조회: 키에 해당하는 번역 결과 반환
        return this.cache.get(key);
    }

    set(key, value) { // [3-3] 캐시 저장: 번역 결과를 캐시에 추가
        if (this.cache.size >= this.maxSize) { // 캐시 크기가 최대치 도달 시
            const firstKey = this.cache.keys().next().value; // 가장 오래된 항목의 키 확인
            this.cache.delete(firstKey); // 오래된 항목 삭제
        }
        this.cache.set(key, value); // 새 번역 결과 저장
    }

    has(key) { // [3-4] 캐시 존재 확인: 특정 키의 존재 여부 확인
        return this.cache.has(key);
    }
}

// [4] 채팅 시스템 코어: 채팅 인터페이스와 상호작용을 관리하는 주요 클래스입니다.
class ChatUIManager {
    constructor() { // [4-1] 컴포넌트 초기화: HTML 요소 및 부가 기능 설정
        this.chatHistory = document.getElementById('chat-history');        // 원본 채팅 메시지 표시 영역
        this.translationHistory = document.getElementById('translation-history');  // 번역 메시지 표시 영역
        this.chatInput = document.getElementById('chat-input');           // 사용자 입력 필드
        this.sendButton = document.getElementById('send-message-btn');     // 메시지 전송 버튼
        this.modelSelect = document.getElementById('ai-model');           // AI 모델 선택 드롭다운
        this.sessionId = document.getElementById('chat-session');          // 채팅 세션 ID 저장용 요소

        // 부가 기능 초기화
        this.translationCache = new TranslationCache(CONSTANTS.MAX_CACHE_SIZE); // 번역 결과 캐시 생성
        this.translationTimeout = null;  // 번역 실행 타이머 초기화 (추후 사용)
        this.messageQueue = [];          // [4-1 추가] 메시지 큐 초기화: 전송할 메시지 순서를 관리
        this.messageFragment = document.createDocumentFragment(); // [4-1 추가] DocumentFragment 생성 (효율적 DOM 조작)
    }

    init() { // [4-2] 시스템 시작: 초기 설정 실행 및 이벤트 리스너 등록
        configureMarked();  // 마크다운 렌더링 옵션 설정 (참조: [2])
        this.sessionId.value = this.generateSessionId();  // 고유 채팅 세션 ID 생성 후 할당
        this.setupEventListeners();  // 이벤트 핸들러 등록
    }

    generateSessionId() { // [4-3] 세션 ID 생성: 고유 채팅 세션 식별자 생성
        return `session_${Date.now()}_${Math.random().toString(36).slice(2, 11)}`;
    }

    setupEventListeners() { // [4-4] 이벤트 핸들러 등록: 사용자 입력 및 버튼 클릭 처리 (참조: 이전 코드 [4-4])
        // Enter 키 이벤트 처리
        this.chatInput.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' && !e.shiftKey) { // Enter 키 단독 입력 시
                e.preventDefault(); // 기본 줄바꿈 동작 방지
                this.sendMessage(); // 메시지 전송 호출
            }
        });

        // 전송 버튼 클릭 이벤트 처리 (추가)
        this.sendButton.addEventListener('click', () => {
            this.sendMessage(); // 전송 버튼 클릭 시 메시지 전송 호출
        });
    }

    sanitizeHTML(text) { // [4-?] HTML 살균 함수: 텍스트를 안전한 HTML로 변환
        const temp = document.createElement('div'); // 임시 div 생성
        temp.textContent = text; // 텍스트 노드로 설정 (HTML 태그 무시)
        return temp.innerHTML; // 안전한 HTML 문자열 반환
    }

    createMessageBubble(isAI = false) { // [4-5] 메시지 버블 생성: 사용자 또는 AI 메시지용 말풍선 생성
        const bubble = document.createElement('div'); // 새 div 요소 생성
        bubble.className = `chat-bubble chat-bubble-${isAI ? 'ai' : 'user'}`; // 역할에 따라 클래스 설정
        bubble.setAttribute('role', isAI ? 'assistant' : 'user'); // 접근성 향상 위한 role 속성 설정
        return bubble; // 생성된 버블 반환
    }

    appendToHistory(element, container) { // [4-6] 채팅 기록 관리: 메시지 추가 및 스크롤 처리
        // DocumentFragment를 사용하여 DOM 조작 최적화 (참조: messageFragment)
        const fragment = this.messageFragment.cloneNode(false); // 새 프래그먼트 복제 (빈 상태)
        fragment.appendChild(element); // 프래그먼트에 메시지 요소 추가

        if (container.children.length > CONSTANTS.MAX_MESSAGES) { // 최대 메시지 수 초과 시
            container.removeChild(container.firstChild); // 가장 오래된 메시지 삭제
        }
        container.appendChild(fragment); // 프래그먼트를 컨테이너에 추가
        container.scrollTop = container.scrollHeight; // 스크롤을 최신 메시지 위치로 이동
    }

    updateAIMessage(bubble, content) { // [4-7] AI 응답 업데이트: 실시간으로 AI 메시지를 갱신합니다.
        requestAnimationFrame(() => { // 브라우저 다음 리페인트 시 실행
            const sanitizedContent = this.sanitizeHTML(content); // 사용자 입력을 안전하게 변환
            const formattedContent = marked.parse(sanitizedContent); // 마크다운 파싱하여 HTML 생성
            bubble.innerHTML = DOMPurify.sanitize(formattedContent); // 보안 처리를 거쳐 HTML 삽입

            setTimeout(() => { // 코드 하이라이팅 적용을 위한 지연 실행
                bubble.querySelectorAll('pre code').forEach(hljs.highlightBlock); // 코드 블록에 하이라이팅 적용
            }, 0);

            this.translateContent(content); // 번역 처리를 위해 텍스트 전달
        });
    }

    extractTranslatableText(content) { // [4-8] 번역 대상 텍스트 추출: 코드 블록 제거 후 순수 텍스트 추출
        return content
            .replace(/```[\s\S]*?```/g, '')  // 멀티라인 코드 블록 제거 (수정된 정규식 사용)
            .replace(/`.*?`/g, '')            // 인라인 코드 제거
            .trim();                         // 앞뒤 공백 제거 후 반환
    }

    async fetchTranslation(text) { // [4-9] 번역 API 호출: 외부 API를 사용하여 텍스트 번역
        const url = `https://translate.googleapis.com/translate_a/single?client=gtx&sl=en&tl=ko&dt=t&q=${encodeURIComponent(text)}`;
        const response = await fetch(url); // API 요청 전송 및 응답 대기

        if (!response.ok) throw new Error('번역 요청 실패'); // 응답 오류 처리

        const data = await response.json(); // 응답 JSON 파싱
        return data[0].map(x => x[0]).join(''); // 번역 결과 문자열로 결합하여 반환
    }

    async translateContent(content) { // [4-?] 번역 내용 처리: 텍스트를 번역하고 결과를 표시
        try {
            if (this.translationCache.has(content)) { // 캐시에 이미 번역된 결과가 있다면
                this.appendTranslation(this.translationCache.get(content)); // 캐시 결과를 바로 사용
                return;
            }

            clearTimeout(this.translationTimeout); // 기존 타이머 클리어

            this.translationTimeout = setTimeout(async () => { // 일정 지연 후 번역 실행
                const textToTranslate = this.extractTranslatableText(content); // 번역 대상 텍스트 추출
                if (!textToTranslate) return; // 번역할 텍스트가 없으면 종료

                const translatedText = await this.fetchTranslation(textToTranslate); // 번역 API 호출
                const finalText = this.reconstructWithCodeBlocks(translatedText, content); // 코드 블록 재결합

                this.translationCache.set(content, finalText); // 캐시에 번역 결과 저장
                this.appendTranslation(finalText); // 번역 결과를 UI에 표시
            }, CONSTANTS.TRANSLATION_DELAY);
        } catch (error) {
            console.error('번역 오류:', error); // 번역 오류 발생 시 콘솔 출력
        }
    }

    reconstructWithCodeBlocks(translatedText, originalContent) { // [4-10] 코드 블록 재결합: 번역된 텍스트와 원본 코드 블록 합침
        const codeBlocks = originalContent.match(/```[\s\S]*?```/g); // 원본에서 코드 블록 추출
        return codeBlocks ?
            `${translatedText}\n\n${codeBlocks.join('\n\n')}` : // 번역문과 코드 블록 결합
            translatedText; // 코드 블록이 없으면 번역문만 반환
    }

    appendTranslation(translatedText) { // [4-11] 번역 결과 표시: 번역 메시지를 별도의 영역에 추가
        const bubble = this.createMessageBubble(true); // AI 메시지 스타일 버블 생성
        bubble.innerHTML = marked.parse(translatedText); // 번역문을 마크다운으로 변환하여 HTML 적용
        bubble.querySelectorAll('pre code').forEach(hljs.highlightBlock); // 코드 하이라이팅 적용
        this.appendToHistory(bubble, this.translationHistory); // 번역 메시지 기록 영역에 추가
    }

    appendUserMessage(content) { // [4-12] 사용자 메시지 추가: 입력한 메시지를 채팅 창에 표시
        const bubble = this.createMessageBubble(false); // 사용자 메시지 버블 생성
        const pre = document.createElement('pre');       // 코드 블록 컨테이너용 pre 요소 생성
        const code = document.createElement('code');     // 메시지 텍스트용 code 요소 생성
        code.textContent = this.sanitizeHTML(content);     // 사용자 입력을 안전하게 처리하여 설정
        pre.appendChild(code);                             // pre 요소에 code 추가
        bubble.appendChild(pre);                           // 버블에 pre 요소 추가
        this.appendToHistory(bubble, this.chatHistory);    // 채팅 기록 영역에 사용자 메시지 추가
    }

    async sendMessage() { // [4-13] 메시지 전송 프로세스: 메시지 전송 후 AI 응답 수신 처리
        const message = this.chatInput.value.trim(); // 입력 필드의 내용을 공백 제거 후 저장
        if (!message) return; // 빈 메시지인 경우 전송 중단

        // [4-13-1] 메시지 큐에 현재 메시지 추가
        this.messageQueue.push(message);

        this.appendUserMessage(message); // 사용자 메시지 화면에 표시
        this.setUIState(false);          // 전송 중 UI 비활성화 (중복 전송 방지)
        this.chatInput.value = '';       // 입력 필드 초기화

        const aiBubble = this.createMessageBubble(true); // AI 응답용 버블 생성
        aiBubble.innerHTML = '<div class="typing-indicator">답변 생성 중...</div>'; // 타이핑 중 표시 설정
        this.appendToHistory(aiBubble, this.chatHistory); // 채팅 기록에 AI 버블 추가

        try {
            await this.streamResponse(aiBubble); // [4-14] 스트리밍 방식으로 AI 응답 수신
        } catch (error) {
            console.error('메시지 전송 오류:', error); // 전송 오류 발생 시 로그 출력
            this.updateAIMessage(aiBubble, `오류 발생: ${error.message}`); // 오류 메시지를 AI 버블에 업데이트
        } finally {
            this.setUIState(true);      // 전송 완료 후 UI 활성화
            this.messageQueue.shift();  // 처리 완료된 메시지를 큐에서 제거
        }
    }

    async streamResponse(bubble) { // [4-14] 실시간 응답 처리: API 응답 스트림을 읽어 UI에 실시간 반영
        const response = await fetch(CONSTANTS.API_ENDPOINT, { // API 엔드포인트에 POST 요청 전송
            method: 'POST', // HTTP POST 방식 사용
            headers: { 'Content-Type': 'application/json' }, // JSON 형식 전송
            body: JSON.stringify({ // 요청 본문 구성
                message: this.messageQueue[0],  // 메시지 큐의 첫 번째 메시지 사용
                model: this.modelSelect.value,  // 선택된 AI 모델
                sessionId: this.sessionId.value // 현재 채팅 세션 ID
            })
        });

        if (!response.ok) throw new Error('네트워크 응답 오류'); // 응답 오류 발생 시 예외 처리

        const reader = response.body.getReader(); // 응답 스트림에서 reader 생성
        const decoder = new TextDecoder();        // 바이트 데이터를 문자열로 변환하는 디코더 생성
        let fullResponse = '';                      // 누적 응답 저장 변수 초기화

        while (true) { // 응답 스트림 처리 루프 시작
            const { done, value } = await reader.read(); // 스트림에서 청크 읽기
            if (done) break; // 더 이상 읽을 데이터가 없으면 루프 종료

            fullResponse += decoder.decode(value); // 청크를 문자열로 변환 후 누적
            this.updateAIMessage(bubble, fullResponse); // 누적된 응답을 AI 버블에 업데이트
        }
    }

    setUIState(enabled) { // [4-15] UI 상태 제어: 입력 필드와 전송 버튼 활성/비활성 전환
        this.chatInput.disabled = !enabled; // enabled이면 입력 필드 활성화, 아니면 비활성화
        this.sendButton.disabled = !enabled;  // enabled이면 전송 버튼 활성화, 아니면 비활성화
        if (enabled) this.chatInput.focus();  // 활성화 시 입력 필드 포커스 설정
    }
}

// [5] 시스템 시작점: DOM이 완전히 로드되면 채팅 시스템을 초기화합니다.
document.addEventListener('DOMContentLoaded', () => { // DOMContentLoaded 이벤트 발생 시 실행
    const chatUI = new ChatUIManager(); // ChatUIManager 클래스 인스턴스 생성
    chatUI.init();                      // 채팅 시스템 초기화 (설정 및 이벤트 핸들러 등록)
});