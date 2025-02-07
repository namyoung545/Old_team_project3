// [1] 시스템 주요 설정 값: 전역 상수들을 정의합니다.
const CONSTANTS = {
    MAX_MESSAGES: 50,
    TRANSLATION_DELAY: 300,
    MAX_CACHE_SIZE: 100,
    API_ENDPOINT: 'http://localhost:8080/api/chat/send'
};

// [2] 문서 표시 형식 초기화: 마크다운 렌더링 옵션을 설정하는 함수입니다.
const configureMarked = () => {
    marked.setOptions({
        highlight: (code, language) => {
            try {
                if (language && hljs.getLanguage(language)) {
                    return hljs.highlight(code, { language }).value;
                }
                return hljs.highlightAuto(code).value;
            } catch (error) {
                console.error('코드 강조 처리 중 오류 발생:', error);
                return code;
            }
        },
        breaks: true,
        gfm: true
    });
};

// [3] 번역 캐시 시스템
class TranslationCache {
    constructor(maxSize) {
        this.cache = new Map();
        this.maxSize = maxSize;
    }

    get(key) {
        return this.cache.get(key);
    }

    set(key, value) {
        if (this.cache.size >= this.maxSize) {
            const firstKey = this.cache.keys().next().value;
            this.cache.delete(firstKey);
        }
        this.cache.set(key, value);
    }

    has(key) {
        return this.cache.has(key);
    }
}

// [4] 채팅 시스템 코어
class ChatUIManager {
    constructor() {
        // UI 요소 초기화
        this.chatHistory = document.getElementById('chat-history');
        this.chatInput = document.getElementById('chat-input');
        this.sendButton = document.getElementById('send-message-btn');
        this.modelSelect = document.getElementById('ai-model');
        this.sessionId = document.getElementById('chat-session');
        this.translationSwitch = document.getElementById('translation-switch');

        // 상태 및 유틸리티 초기화
        this.isTranslationMode = false;
        this.translationCache = new TranslationCache(CONSTANTS.MAX_CACHE_SIZE);
        this.translationTimeout = null;
        this.messageQueue = [];
        this.messageFragment = document.createDocumentFragment();
    }

    init() {
        configureMarked();
        this.sessionId.value = this.generateSessionId();
        this.setupEventListeners();
    }

    generateSessionId() {
        return `session_${Date.now()}_${Math.random().toString(36).slice(2, 11)}`;
    }

    setupEventListeners() {
        // 메시지 입력 이벤트
        this.chatInput.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                this.sendMessage();
            }
        });

        // 전송 버튼 이벤트
        this.sendButton.addEventListener('click', () => {
            this.sendMessage();
        });

        // 번역 토글 이벤트
        this.translationSwitch.addEventListener('change', () => {
            this.isTranslationMode = this.translationSwitch.checked;
            this.toggleTranslationView();
        });
    }

    sanitizeHTML(text) {
        const temp = document.createElement('div');
        temp.textContent = text;
        return temp.innerHTML;
    }

    createMessageBubble(isAI = false) {
        const bubble = document.createElement('div');
        bubble.className = `chat-bubble chat-bubble-${isAI ? 'ai' : 'user'}`;
        
        if (isAI) {
            const originalContent = document.createElement('div');
            originalContent.className = 'message-content original-content';
            
            const translatedContent = document.createElement('div');
            translatedContent.className = 'message-content translated-content';
            
            bubble.appendChild(originalContent);
            bubble.appendChild(translatedContent);
        }
        
        return bubble;
    }

    appendToHistory(element) {
        const fragment = this.messageFragment.cloneNode(false);
        fragment.appendChild(element);

        if (this.chatHistory.children.length > CONSTANTS.MAX_MESSAGES) {
            this.chatHistory.removeChild(this.chatHistory.firstChild);
        }
        this.chatHistory.appendChild(fragment);
        this.chatHistory.scrollTop = this.chatHistory.scrollHeight;
    }

    async updateAIMessage(bubble, content) {
        const originalContent = bubble.querySelector('.original-content');
        const translatedContent = bubble.querySelector('.translated-content');

        // 원본 내용 업데이트
        requestAnimationFrame(() => {
            const sanitizedContent = this.sanitizeHTML(content);
            const formattedContent = marked.parse(sanitizedContent);
            originalContent.innerHTML = DOMPurify.sanitize(formattedContent);

            setTimeout(() => {
                originalContent.querySelectorAll('pre code').forEach(hljs.highlightBlock);
            }, 0);
        });

        // 번역 처리
        try {
            const translatedText = await this.translateContent(content);
            requestAnimationFrame(() => {
                const formattedTranslation = marked.parse(translatedText);
                translatedContent.innerHTML = DOMPurify.sanitize(formattedTranslation);

                setTimeout(() => {
                    translatedContent.querySelectorAll('pre code').forEach(hljs.highlightBlock);
                }, 0);
            });
        } catch (error) {
            console.error('번역 오류:', error);
        }
    }

    extractTranslatableText(content) {
        return content
            .replace(/```[\s\S]*?```/g, '')
            .replace(/`.*?`/g, '')
            .trim();
    }

    async fetchTranslation(text) {
        const url = `https://translate.googleapis.com/translate_a/single?client=gtx&sl=auto&tl=ko&dt=t&q=${encodeURIComponent(text)}`;
        const response = await fetch(url);

        if (!response.ok) throw new Error('번역 요청 실패');

        const data = await response.json();
        return data[0].map(x => x[0]).join('');
    }

    async translateContent(content) {
        if (this.translationCache.has(content)) {
            return this.translationCache.get(content);
        }

        const textToTranslate = this.extractTranslatableText(content);
        if (!textToTranslate) return content;

        try {
            const translatedText = await this.fetchTranslation(textToTranslate);
            const finalText = this.reconstructWithCodeBlocks(translatedText, content);
            this.translationCache.set(content, finalText);
            return finalText;
        } catch (error) {
            console.error('번역 오류:', error);
            return content;
        }
    }

    reconstructWithCodeBlocks(translatedText, originalContent) {
        const codeBlocks = originalContent.match(/```[\s\S]*?```/g);
        return codeBlocks ?
            `${translatedText}\n\n${codeBlocks.join('\n\n')}` :
            translatedText;
    }

    appendUserMessage(content) {
        const bubble = this.createMessageBubble(false);
        const pre = document.createElement('pre');
        const code = document.createElement('code');
        code.textContent = this.sanitizeHTML(content);
        pre.appendChild(code);
        bubble.appendChild(pre);
        this.appendToHistory(bubble);
    }

    toggleTranslationView() {
        if (this.isTranslationMode) {
            this.chatHistory.classList.add('translation-mode');
        } else {
            this.chatHistory.classList.remove('translation-mode');
        }
    }

    async sendMessage() {
        const message = this.chatInput.value.trim();
        if (!message) return;

        this.messageQueue.push(message);
        this.appendUserMessage(message);
        this.setUIState(false);
        this.chatInput.value = '';

        const aiBubble = this.createMessageBubble(true);
        aiBubble.querySelector('.original-content').innerHTML = '<div class="typing-indicator">답변 생성 중...</div>';
        aiBubble.querySelector('.translated-content').innerHTML = '<div class="typing-indicator">답변 생성 중...</div>';
        this.appendToHistory(aiBubble);

        try {
            await this.streamResponse(aiBubble);
        } catch (error) {
            console.error('메시지 전송 오류:', error);
            this.updateAIMessage(aiBubble, `오류 발생: ${error.message}`);
        } finally {
            this.setUIState(true);
            this.messageQueue.shift();
        }
    }

    async streamResponse(bubble) {
        const response = await fetch(CONSTANTS.API_ENDPOINT, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                message: this.messageQueue[0],
                model: this.modelSelect.value,
                sessionId: this.sessionId.value
            })
        });

        if (!response.ok) throw new Error('네트워크 응답 오류');

        const reader = response.body.getReader();
        const decoder = new TextDecoder();
        let fullResponse = '';

        while (true) {
            const { done, value } = await reader.read();
            if (done) break;

            fullResponse += decoder.decode(value);
            this.updateAIMessage(bubble, fullResponse);
        }
    }

    setUIState(enabled) {
        this.chatInput.disabled = !enabled;
        this.sendButton.disabled = !enabled;
        if (enabled) this.chatInput.focus();
    }
}

// [5] 시스템 시작점
document.addEventListener('DOMContentLoaded', () => {
    const chatUI = new ChatUIManager();
    chatUI.init();
});