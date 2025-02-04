// 마크다운 및 문법 강조 설정
marked.setOptions({
    highlight: function(code, language) {
        try {
            if (language && hljs.getLanguage(language)) {
                return hljs.highlight(code, { language }).value;
            }
            return hljs.highlightAuto(code).value;
        } catch (error) {
            console.error('Highlighting error:', error);
            return code;
        }
    },
    breaks: true,
    gfm: true
});

const messageFragment = document.createDocumentFragment();
let messageQueue = [];
const MAX_MESSAGES = 50;
const translationCache = new Map();
let translationTimeout;

document.addEventListener('DOMContentLoaded', function() {
    const chatSession = document.getElementById('chat-session');
    const chatInput = document.getElementById('chat-input');

    chatSession.value = generateSessionId();
    
    chatInput.addEventListener('keydown', function(e) {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            sendMessage();
        }
    });
});

function generateSessionId() {
    return `session_${Date.now()}_${Math.random().toString(36).slice(2, 11)}`;
}

function sanitizeHTML(text) {
    const temp = document.createElement('div');
    temp.textContent = text;
    return temp.innerHTML;
}

function appendMessage(content, isUser) {
    const chatHistory = document.getElementById('chat-history');
    
    if (chatHistory.children.length > MAX_MESSAGES) {
        chatHistory.removeChild(chatHistory.firstChild);
    }

    const messageBubble = document.createElement('div');
    messageBubble.className = `chat-bubble ${isUser ? 'chat-bubble-user' : 'chat-bubble-ai'}`;
    messageBubble.setAttribute('role', isUser ? 'user' : 'assistant');

    if (isUser) {
        // 코드 블록으로 감싸기
        const preElement = document.createElement('pre');
        const codeElement = document.createElement('code');
        // textContent를 사용하여 HTML을 그대로 표시
        codeElement.textContent = content;
        preElement.appendChild(codeElement);
        messageBubble.appendChild(preElement);
    } else {
        requestAnimationFrame(() => {
            const formattedContent = marked.parse(content);
            messageBubble.innerHTML = formattedContent;
            
            setTimeout(() => {
                messageBubble.querySelectorAll('pre code').forEach((block) => {
                    hljs.highlightBlock(block);
                });
            }, 0);
        });
    }

    messageFragment.appendChild(messageBubble);
    
    requestAnimationFrame(() => {
        chatHistory.appendChild(messageFragment);
        chatHistory.scrollTop = chatHistory.scrollHeight;
    });

    if (!isUser) {
        translateAndAppend(content);
    }
}

async function translateAndAppend(content) {
    try {
        if (translationCache.has(content)) {
            appendTranslation(translationCache.get(content));
            return;
        }

        clearTimeout(translationTimeout);
        translationTimeout = setTimeout(async () => {
            const textToTranslate = content
                .replace(/```[\s\S]*?```/g, '')
                .replace(/`.*?`/g, '')
                .trim();

            if (!textToTranslate) return;

            const url = `https://translate.googleapis.com/translate_a/single?client=gtx&sl=en&tl=ko&dt=t&q=${encodeURIComponent(textToTranslate)}`;
            const response = await fetch(url);
            
            if (!response.ok) throw new Error('Translation request failed');
            
            const data = await response.json();
            let translatedText = data[0].map(x => x[0]).join('');

            const codeBlocks = content.match(/```[\s\S]*?```/g);
            if (codeBlocks) {
                translatedText += '\n\n' + codeBlocks.join('\n\n');
            }

            translationCache.set(content, translatedText);
            
            if (translationCache.size > 100) {
                const firstKey = translationCache.keys().next().value;
                translationCache.delete(firstKey);
            }

            appendTranslation(translatedText);
        }, 300);

    } catch (error) {
        console.error('Translation error:', error);
    }
}

function appendTranslation(translatedText) {
    const translationHistory = document.getElementById('translation-history');
    const translationBubble = document.createElement('div');
    translationBubble.className = 'chat-bubble chat-bubble-ai';
    translationBubble.innerHTML = marked.parse(translatedText);

    translationBubble.querySelectorAll('pre code').forEach((block) => {
        hljs.highlightBlock(block);
    });

    requestAnimationFrame(() => {
        translationHistory.appendChild(translationBubble);
        translationHistory.scrollTop = translationHistory.scrollHeight;
    });
}

async function sendMessage() {
    const chatInput = document.getElementById('chat-input');
    const sendButton = document.getElementById('send-message-btn');
    const modelSelect = document.getElementById('ai-model');
    const sessionId = document.getElementById('chat-session').value;
    
    const message = chatInput.value.trim();
    if (!message) return;
    
    appendMessage(message, true);
    chatInput.value = '';
    chatInput.disabled = true;
    sendButton.disabled = true;
    
    try {
        const response = await fetch('http://localhost:8080/api/chat/send', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ message, model: modelSelect.value, sessionId })
        });
        
        if (!response.ok) throw new Error('Network response was not ok');
        const data = await response.text();
        const cleanedData = data.replace(/\\[rnt]/g, ' ').replace(/\"/g, '"').replace(/\\/g, '\\');
        appendMessage(cleanedData, false);
    } catch (error) {
        console.error('Send message error:', error);
        appendMessage(`오류: ${error.message}`, false);
    } finally {
        chatInput.disabled = false;
        sendButton.disabled = false;
        chatInput.focus();
    }
}
