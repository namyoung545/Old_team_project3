// [1] 시스템 주요 설정 값: 전역 상수들을 정의합니다.
const CONSTANTS = { // 설정값 객체
    MAX_MESSAGES: 50,         // 채팅 창에 표시할 최대 메시지 개수
    TRANSLATION_DELAY: 300,   // 번역 실행 전 대기 시간 (밀리초)
    MAX_CACHE_SIZE: 100,      // 번역 캐시가 저장할 최대 항목 수
    API_ENDPOINT: 'http://localhost:8082/api/chat/send'  // 메시지 전송에 사용할 API URL
};

// [2] 문서 표시 형식 초기화: 마크다운 렌더링 옵션을 설정하는 함수입니다. (참조: 4-2)
const configureMarked = () => { // configureMarked 함수 시작
    marked.setOptions({ // marked 라이브러리 옵션 설정
        highlight: (code, language) => { // 코드 하이라이팅 함수: 코드와 언어 정보를 입력받음
            try { // 하이라이팅 시도
                if (language && hljs.getLanguage(language)) { // 지정 언어가 유효한지 확인
                    return hljs.highlight(code, { language }).value; // 지정 언어로 코드 강조 적용
                }
                return hljs.highlightAuto(code).value; // 자동 감지로 코드 강조 적용
            } catch (error) { // 오류 발생 시
                console.error('코드 강조 처리 중 오류 발생:', error); // 오류 로그 출력
                return code; // 원본 코드를 그대로 반환
            }
        },
        breaks: true, // 줄바꿈 문법 활성화
        gfm: true     // GitHub Flavored Markdown(GFM) 사용
    }); // marked 옵션 설정 종료
}; // configureMarked 함수 종료

// [3] 번역 캐시 시스템: 번역 결과를 저장하고 관리하는 캐시 클래스입니다.
class TranslationCache {
    constructor(maxSize) { // 생성자: 최대 캐시 크기 설정
        // [3-1] 캐시 초기화: 새 Map 객체를 생성하여 캐시 저장소로 사용
        this.cache = new Map(); // 번역 결과 저장용 Map 생성
        this.maxSize = maxSize; // 최대 캐시 크기를 할당
    }

    // [3-2] 캐시 조회: 지정 키의 번역 결과를 반환합니다.
    get(key) { // get 메소드: 키에 해당하는 값을 반환
        return this.cache.get(key); // Map에서 키에 해당하는 값을 가져옴
    }

    // [3-3] 캐시 저장: 새로운 번역 결과를 캐시에 추가합니다.
    set(key, value) { // set 메소드: 키와 값을 받아 캐시에 저장
        if (this.cache.size >= this.maxSize) { // 캐시 크기가 최대치에 도달하면
            const firstKey = this.cache.keys().next().value; // 가장 오래된 항목의 키를 확인
            this.cache.delete(firstKey); // 오래된 항목 삭제하여 공간 확보
        }
        this.cache.set(key, value); // 새 번역 결과를 캐시에 저장
    }

    // [3-4] 캐시 존재 확인: 특정 키가 캐시에 있는지 검사합니다.
    has(key) { // has 메소드: 키 존재 여부를 확인
        return this.cache.has(key); // Map의 has() 메소드로 결과 반환
    }
}

// [4] 채팅 시스템 코어: 채팅 인터페이스와 상호작용을 관리하는 주요 클래스입니다.
class ChatUIManager {
    // [4-1] 컴포넌트 초기화: HTML 요소 및 부가 기능들을 설정합니다.
    constructor() { // 생성자 함수 시작
        this.chatHistory = document.getElementById('chat-history');        // 원본 채팅 메시지 표시 영역 선택
        this.translationHistory = document.getElementById('translation-history');  // 번역 메시지 표시 영역 선택
        this.chatInput = document.getElementById('chat-input');           // 사용자 입력 필드 선택
        this.sendButton = document.getElementById('send-message-btn');     // 메시지 전송 버튼 선택
        this.modelSelect = document.getElementById('ai-model');           // AI 모델 선택 드롭다운 선택
        this.sessionId = document.getElementById('chat-session');          // 채팅 세션 ID 저장용 요소 선택

        // 부가 기능 초기화
        this.translationCache = new TranslationCache(CONSTANTS.MAX_CACHE_SIZE);  // 번역 결과 캐시 생성
        this.translationTimeout = null;  // 번역 실행 타이머 (추후 사용)
    }

    // [4-2] 시스템 시작: 초기 설정 실행 및 이벤트 리스너 등록 (참조: 2)
    init() { // 초기화 함수 시작
        configureMarked();  // 마크다운 렌더링 옵션 설정
        this.sessionId.value = this.generateSessionId();  // 고유 채팅 세션 ID 생성 후 할당
        this.setupEventListeners();  // 사용자 이벤트 리스너 등록
    }

    // [4-3] 세션 ID 생성: 고유한 채팅 세션 식별자를 생성합니다.
    generateSessionId() { // 세션 ID 생성 함수
        return `session_${Date.now()}_${Math.random().toString(36).slice(2, 11)}`; // 시간과 무작위 문자열로 ID 생성
    }

    // [4-4] 이벤트 핸들러 등록: 사용자 입력 이벤트를 처리할 리스너를 등록합니다. (참조: 4-13)
    setupEventListeners() { // 이벤트 리스너 등록 함수
        this.chatInput.addEventListener('keydown', (e) => { // 입력 필드에 키 입력 이벤트 감지
            if (e.key === 'Enter' && !e.shiftKey) { // Enter 키 단독 입력 시 (Shift 없이)
                e.preventDefault(); // 기본 줄바꿈 동작 방지
                this.sendMessage(); // 메시지 전송 함수 호출
            }
        });
    }

    // [4-5] 메시지 UI 생성: 사용자 또는 AI 메시지용 버블(말풍선) 요소를 생성합니다. (참조: 4-12, 4-14, 4-11)
    createMessageBubble(isAI = false) { // 메시지 버블 생성 함수 (매개변수로 AI 여부 지정)
        const bubble = document.createElement('div'); // 새 div 요소 생성
        bubble.className = `chat-bubble chat-bubble-${isAI ? 'ai' : 'user'}`; // 클래스 이름을 AI 또는 사용자 스타일로 설정
        bubble.setAttribute('role', isAI ? 'assistant' : 'user'); // 접근성 향상을 위한 role 속성 설정
        return bubble; // 생성된 버블 요소 반환
    }

    // [4-6] 채팅 기록 관리: 새 메시지를 추가하고, 최대치 초과 시 오래된 메시지는 제거합니다.
    appendToHistory(element, container) { // 메시지 요소를 지정된 컨테이너에 추가하는 함수
        if (container.children.length > CONSTANTS.MAX_MESSAGES) { // 메시지 수가 최대치를 초과하면
            container.removeChild(container.firstChild); // 가장 오래된 메시지 삭제
        }
        container.appendChild(element); // 새 메시지 요소 추가
        container.scrollTop = container.scrollHeight; // 스크롤을 최신 메시지 위치로 이동
    }

    // [4-7] AI 응답 업데이트: AI의 응답 내용을 실시간으로 갱신합니다. (참조: 3-3, 4-14)
    updateAIMessage(bubble, content) { // AI 메시지 버블과 업데이트할 내용을 받아 실행
        requestAnimationFrame(() => { // 브라우저 다음 리페인트 시 실행
            const formattedContent = marked.parse(content); // 입력 내용을 마크다운으로 파싱하여 HTML 생성
            bubble.innerHTML = DOMPurify.sanitize(formattedContent); // 보안 처리를 거쳐 HTML 삽입
            setTimeout(() => { // 코드 하이라이팅 적용을 위한 지연 실행
                bubble.querySelectorAll('pre code').forEach(hljs.highlightBlock); // 모든 코드 블록에 하이라이팅 적용
            }, 0);
            this.translateContent(content); // 번역 처리를 위한 텍스트 전달 (추가 구현 필요)
        });
    }

    // [4-8] 번역 대상 텍스트 추출: 코드 블록을 제외한 순수 텍스트만 추출합니다.
    extractTranslatableText(content) { // 번역 전 텍스트 처리 함수
        return content
            .replace(/``````/g, '')  // 멀티라인 코드 블록 제거
            .replace(/`.*?`/g, '')   // 인라인 코드 제거
            .trim();                 // 앞뒤 공백 제거 후 반환
    }

    // [4-9] 번역 API 호출: 외부 번역 API를 사용해 텍스트를 번역합니다.
    async fetchTranslation(text) { // 비동기 번역 함수 시작
        const url = `https://translate.googleapis.com/translate_a/single?client=gtx&sl=en&tl=ko&dt=t&q=${encodeURIComponent(text)}`; // API URL 구성 (영어→한국어)
        const response = await fetch(url); // API 요청 전송 및 응답 대기

        if (!response.ok) throw new Error('번역 요청 실패'); // 응답이 정상적이지 않으면 오류 발생

        const data = await response.json(); // 응답 데이터를 JSON으로 파싱
        return data[0].map(x => x[0]).join('');  // 번역 결과 배열을 문자열로 결합하여 반환
    }

    // [4-10] 코드 블록 재결합: 번역된 텍스트와 원본 코드 블록을 다시 하나로 합칩니다.
    reconstructWithCodeBlocks(translatedText, originalContent) { // 번역문과 코드 블록 결합 함수
        const codeBlocks = originalContent.match(/``````/g); // 원본 텍스트에서 코드 블록 검색
        return codeBlocks ?
            `${translatedText}\n\n${codeBlocks.join('\n\n')}` :  // 번역문과 코드 블록 결합 후 반환
            translatedText; // 코드 블록이 없으면 번역문만 반환
    }

    // [4-11] 번역 결과 표시: 번역된 메시지를 별도의 영역에 표시합니다. (참조: 4-5)
    appendTranslation(translatedText) { // 번역 결과 UI 추가 함수
        const bubble = this.createMessageBubble(true); // AI 메시지 스타일의 버블 생성
        bubble.innerHTML = marked.parse(translatedText);  // 번역문을 마크다운으로 변환하여 HTML 적용
        bubble.querySelectorAll('pre code').forEach(hljs.highlightBlock);  // 코드 블록에 하이라이팅 적용
        this.appendToHistory(bubble, this.translationHistory);  // 번역 메시지를 번역 기록 영역에 추가
    }

    // [4-12] 사용자 메시지 추가: 사용자가 입력한 메시지를 채팅 창에 표시합니다. (참조: 4-5, 4-6)
    appendUserMessage(content) { // 사용자 메시지 추가 함수
        const bubble = this.createMessageBubble(false); // 사용자 메시지 스타일의 버블 생성
        const pre = document.createElement('pre');       // pre 요소 생성 (코드 블록 컨테이너)
        const code = document.createElement('code');     // code 요소 생성 (메시지 텍스트용)
        code.textContent = content; // 사용자 메시지 텍스트 설정
        pre.appendChild(code); // code 요소를 pre 요소에 추가
        bubble.appendChild(pre); // pre 요소를 버블에 추가
        this.appendToHistory(bubble, this.chatHistory);  // 완성된 버블을 채팅 기록 영역에 추가
    }

    // [4-13] 메시지 전송 프로세스: 사용자 메시지를 전송하고 AI 응답을 받아 처리합니다. (참조: 4-4)
    async sendMessage() { // 비동기 메시지 전송 함수 시작
        const message = this.chatInput.value.trim(); // 입력 필드의 내용을 공백 제거 후 저장
        if (!message) return;  // 빈 메시지인 경우 함수 종료

        this.appendUserMessage(message);  // 사용자 메시지를 화면에 추가
        this.setUIState(false);           // 전송 중 UI 비활성화 (중복 전송 방지)
        this.chatInput.value = '';        // 입력 필드 초기화

        const aiBubble = this.createMessageBubble(true); // AI 응답용 버블 생성
        aiBubble.innerHTML = '<div class="typing-indicator">답변 생성 중...</div>'; // 타이핑 중 표시 설정
        this.appendToHistory(aiBubble, this.chatHistory); // AI 버블을 채팅 기록에 추가

        try { // AI 응답 처리 시도
            await this.streamResponse(aiBubble);  // 스트리밍 방식으로 AI 응답 수신 (참조: 4-14)
        } catch (error) { // 오류 발생 시
            console.error('메시지 전송 오류:', error); // 오류 메시지 콘솔 출력
            this.updateAIMessage(aiBubble, `오류 발생: ${error.message}`);  // 오류 메시지를 AI 버블에 업데이트
        } finally {
            this.setUIState(true);  // 전송 완료 후 UI 활성화
        }
    }

    // [4-14] 실시간 응답 처리: API 응답 스트림을 읽어와 UI에 실시간으로 반영합니다. (참조: 4-7)
    async streamResponse(bubble) { // 비동기 스트리밍 응답 함수 시작
        const response = await fetch(CONSTANTS.API_ENDPOINT, { // API 엔드포인트에 POST 요청 전송
            method: 'POST', // HTTP POST 방식 사용
            headers: { 'Content-Type': 'application/json' }, // JSON 형식 데이터 전송
            body: JSON.stringify({ // 요청 본문 구성
                message: this.chatInput.value.trim(),  // 사용자 메시지 (입력 필드 내용)
                model: this.modelSelect.value,         // 선택한 AI 모델
                sessionId: this.sessionId.value        // 현재 채팅 세션 ID
            })
        });

        if (!response.ok) throw new Error('네트워크 응답 오류'); // 응답 상태 비정상 시 오류 발생

        const reader = response.body.getReader();  // 응답 스트림에서 reader 생성
        const decoder = new TextDecoder();         // 텍스트 디코더 생성 (바이너리 → 문자열 변환)
        let fullResponse = '';                     // 누적 응답 저장용 변수 초기화

        // 실시간 데이터 처리 루프: 스트림에서 청크를 읽어와 누적 처리
        while (true) { // 무한 루프 시작
            const { done, value } = await reader.read(); // 스트림에서 데이터 청크 읽기
            if (done) break;  // 더 이상 읽을 데이터가 없으면 루프 종료

            fullResponse += decoder.decode(value);  // 청크를 문자열로 변환 후 누적
            this.updateAIMessage(bubble, fullResponse);  // 누적 응답을 AI 버블에 업데이트
        }
    }

    // [4-15] UI 상태 제어: 입력 필드와 전송 버튼의 활성/비활성 상태를 전환합니다. (참조: 4-13)
    setUIState(enabled) { // UI 활성화 여부를 설정하는 함수
        this.chatInput.disabled = !enabled;   // enabled이면 입력 필드 활성화, 아니면 비활성화
        this.sendButton.disabled = !enabled;    // enabled이면 전송 버튼 활성화, 아니면 비활성화
        if (enabled) this.chatInput.focus();    // 활성화 시 입력 필드에 포커스 설정
    }
}

// [5] 시스템 시작점: DOM이 완전히 로드되면 채팅 시스템을 초기화합니다. (참조: 4-2)
document.addEventListener('DOMContentLoaded', () => { // DOMContentLoaded 이벤트 발생 시 실행
    const chatUI = new ChatUIManager();  // ChatUIManager 클래스 인스턴스 생성
    chatUI.init();                       // 채팅 시스템 초기화 (설정 및 이벤트 리스너 등록)
});
