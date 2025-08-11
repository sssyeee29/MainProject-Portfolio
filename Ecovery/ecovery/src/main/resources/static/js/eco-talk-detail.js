/* =========================================
   환경톡톡 상세보기 페이지 JavaScript (사이드바 제거)
   ========================================= */

// 전역 변수
let isLiked = false;
let isBookmarked = false;

// DOM 요소 참조
const hamburger = document.getElementById('hamburger');
const navMenu = document.getElementById('navMenu');

/* =========================================
   초기화 함수
   ========================================= */

// DOM이 로드된 후 실행되는 초기화 함수
document.addEventListener('DOMContentLoaded', function() {
    initializeDetailPage();
});

// 페이지 기능 초기화
function initializeDetailPage() {
    console.log('📄 환경톡톡 상세페이지가 로드되었습니다.');

    // 기본 초기화
    initializePage();

    // 상세페이지 전용 기능들
    initScrollTopButton();
    setupKeyboardShortcuts();

    // 댓글 폼 이벤트 리스너
    const commentForm = document.querySelector('.comment-form');
    if (commentForm) {
        commentForm.addEventListener('submit', function(e) {
            if (!submitComment(this)) {
                e.preventDefault();
            }
        });
    }

    // 외부 링크 새 창에서 열기
    document.querySelectorAll('.post-article-content a[href^="http"]').forEach(link => {
        if (!link.hostname || link.hostname !== window.location.hostname) {
            link.target = '_blank';
            link.rel = 'noopener noreferrer';
        }
    });

    console.log('✅ 환경톡톡 상세페이지 초기화 완료');
}

// 기본 페이지 초기화
function initializePage() {
    // 모바일 메뉴 이벤트 리스너 등록
    if (hamburger && navMenu) {
        hamburger.addEventListener('click', toggleMobileMenu);
    }

    // 네비게이션 링크 클릭 시 모바일 메뉴 닫기
    document.querySelectorAll('.nav-menu a').forEach(link => {
        link.addEventListener('click', closeMobileMenu);
    });

    // 스크롤 시 헤더 효과
    window.addEventListener('scroll', handleHeaderScroll);

    // 페이드인 애니메이션 적용
    initializeFadeInAnimation();
}

/* =========================================
   모바일 메뉴 관련 함수
   ========================================= */

// 모바일 메뉴 토글
function toggleMobileMenu() {
    hamburger.classList.toggle('active');
    navMenu.classList.toggle('active');

    // 햄버거 아이콘 애니메이션
    const spans = hamburger.querySelectorAll('span');
    if (hamburger.classList.contains('active')) {
        spans[0].style.transform = 'rotate(45deg) translate(5px, 5px)';
        spans[1].style.opacity = '0';
        spans[2].style.transform = 'rotate(-45deg) translate(7px, -6px)';
    } else {
        spans[0].style.transform = 'none';
        spans[1].style.opacity = '1';
        spans[2].style.transform = 'none';
    }
}

// 모바일 메뉴 닫기
function closeMobileMenu() {
    if (hamburger && navMenu) {
        hamburger.classList.remove('active');
        navMenu.classList.remove('active');

        // 햄버거 아이콘 원상복구
        const spans = hamburger.querySelectorAll('span');
        spans[0].style.transform = 'none';
        spans[1].style.opacity = '1';
        spans[2].style.transform = 'none';
    }
}

/* =========================================
   UI 효과 함수
   ========================================= */

// 헤더 스크롤 효과
function handleHeaderScroll() {
    const header = document.getElementById('header');
    if (header) {
        if (window.scrollY > 100) {
            header.style.background = 'rgba(255, 255, 255, 0.98)';
        } else {
            header.style.background = 'rgba(255, 255, 255, 0.95)';
        }
    }
}

// 페이드인 애니메이션 초기화
function initializeFadeInAnimation() {
    const elements = document.querySelectorAll('.fade-in');
    elements.forEach((el, index) => {
        setTimeout(() => {
            el.style.opacity = '1';
            el.style.transform = 'translateY(0)';
        }, index * 200);
    });
}

/* =========================================
   알림 시스템
   ========================================= */

// 알림 메시지 표시
function showNotification(message, type = 'info') {
    // 기존 알림이 있으면 제거
    const existingNotification = document.querySelector('.notification');
    if (existingNotification) {
        existingNotification.remove();
    }

    // 새 알림 생성
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;

    document.body.appendChild(notification);

    // 알림 표시 애니메이션
    setTimeout(() => {
        notification.classList.add('show');
    }, 100);

    // 3초 후 알림 자동 숨김
    setTimeout(() => {
        notification.classList.remove('show');
        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 300);
    }, 3000);
}

/* =========================================
   스크롤 탑 버튼 관련 함수들
   ========================================= */

function initScrollTopButton() {
    // 스크롤 탑 버튼 생성
    if (!document.querySelector('.scroll-top-btn')) {
        const scrollTopBtn = document.createElement('button');
        scrollTopBtn.className = 'scroll-top-btn';
        scrollTopBtn.innerHTML = '↑';
        scrollTopBtn.title = '맨 위로 이동';
        scrollTopBtn.onclick = scrollToTop;
        document.body.appendChild(scrollTopBtn);
    }

    // 스크롤 이벤트 리스너
    window.addEventListener('scroll', toggleScrollTopButton);
}

function toggleScrollTopButton() {
    const scrollTopBtn = document.querySelector('.scroll-top-btn');
    if (scrollTopBtn) {
        if (window.pageYOffset > 300) {
            scrollTopBtn.classList.add('show');
        } else {
            scrollTopBtn.classList.remove('show');
        }
    }
}

function scrollToTop() {
    window.scrollTo({
        top: 0,
        behavior: 'smooth'
    });
}

/* =========================================
   공유 기능
   ========================================= */

// 게시글 공유 함수
function sharePost(postId, title) {
    const shareData = {
        title: title || '환경톡톡 게시글',
        text: 'GreenCycle 환경톡톡에서 환경 이야기를 확인해보세요!',
        url: window.location.href
    };

    if (navigator.share) {
        navigator.share(shareData)
            .then(() => {
                showNotification('게시글이 공유되었습니다! 📤', 'success');
                trackShareEvent(postId, 'native_share');
            })
            .catch((error) => {
                console.log('공유 취소됨:', error);
                fallbackShare();
            });
    } else {
        fallbackShare();
    }
}

// 공유 대체 방법 (클립보드 복사 + 소셜미디어 링크)
function fallbackShare() {
    const shareModal = createShareModal();
    document.body.appendChild(shareModal);

    // 모달 표시 애니메이션
    setTimeout(() => {
        shareModal.classList.add('show');
    }, 10);
}

function createShareModal() {
    const modal = document.createElement('div');
    modal.className = 'share-modal-overlay';
    modal.innerHTML = `
        <div class="share-modal">
            <div class="share-modal-header">
                <h3>게시글 공유하기</h3>
                <button class="share-modal-close" onclick="closeShareModal()">&times;</button>
            </div>
            <div class="share-modal-content">
                <div class="share-url-section">
                    <label>링크 주소</label>
                    <div class="share-url-container">
                        <input type="text" value="${window.location.href}" readonly id="shareUrlInput">
                        <button onclick="copyShareUrl()">복사</button>
                    </div>
                </div>
                <div class="share-social-section">
                    <label>소셜미디어로 공유</label>
                    <div class="share-social-buttons">
                        <button onclick="shareToKakao()" class="social-btn kakao">카카오톡</button>
                        <button onclick="shareToFacebook()" class="social-btn facebook">페이스북</button>
                        <button onclick="shareToTwitter()" class="social-btn twitter">트위터</button>
                        <button onclick="shareToLine()" class="social-btn line">라인</button>
                    </div>
                </div>
            </div>
        </div>
    `;

    // 모달 외부 클릭시 닫기
    modal.addEventListener('click', (e) => {
        if (e.target === modal) {
            closeShareModal();
        }
    });

    return modal;
}

function closeShareModal() {
    const modal = document.querySelector('.share-modal-overlay');
    if (modal) {
        modal.classList.remove('show');
        setTimeout(() => {
            modal.remove();
        }, 300);
    }
}

function copyShareUrl() {
    const input = document.getElementById('shareUrlInput');
    input.select();
    input.setSelectionRange(0, 99999); // 모바일 호환성

    if (navigator.clipboard) {
        navigator.clipboard.writeText(input.value).then(() => {
            showNotification('링크가 클립보드에 복사되었습니다! 📋', 'success');
        });
    } else {
        document.execCommand('copy');
        showNotification('링크가 복사되었습니다! 📋', 'success');
    }
}

// 소셜미디어 공유 함수들
function shareToKakao() {
    if (typeof Kakao !== 'undefined') {
        Kakao.Share.sendDefault({
            objectType: 'feed',
            content: {
                title: document.title,
                description: document.querySelector('meta[name="description"]')?.content || '',
                imageUrl: document.querySelector('meta[property="og:image"]')?.content || '',
                link: {
                    mobileWebUrl: window.location.href,
                    webUrl: window.location.href,
                },
            },
        });
    } else {
        showNotification('카카오톡 공유 기능은 실제 서비스에서 사용 가능합니다.', 'info');
    }
}

function shareToFacebook() {
    const url = `https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(window.location.href)}`;
    openShareWindow(url, 'Facebook');
}

function shareToTwitter() {
    const text = encodeURIComponent(document.title);
    const url = `https://twitter.com/intent/tweet?text=${text}&url=${encodeURIComponent(window.location.href)}`;
    openShareWindow(url, 'Twitter');
}

function shareToLine() {
    const text = encodeURIComponent(document.title);
    const url = `https://social-plugins.line.me/lineit/share?url=${encodeURIComponent(window.location.href)}&text=${text}`;
    openShareWindow(url, 'Line');
}

function openShareWindow(url, platform) {
    const width = 600;
    const height = 400;
    const left = (window.innerWidth - width) / 2;
    const top = (window.innerHeight - height) / 2;

    window.open(
        url,
        `share_${platform}`,
        `width=${width},height=${height},left=${left},top=${top},scrollbars=yes,resizable=yes`
    );
}

/* =========================================
   좋아요 및 북마크 기능
   ========================================= */

function toggleLike(postId) {
    const likeBtn = document.querySelector('.post-action-btn.like');
    if (!likeBtn) return;

    isLiked = !isLiked;

    if (isLiked) {
        likeBtn.innerHTML = '❤️ 좋아요 취소';
        likeBtn.classList.add('active');
        showNotification('좋아요를 눌렀습니다! ❤️', 'success');
        trackLikeEvent(postId, 'add');
    } else {
        likeBtn.innerHTML = '🤍 좋아요';
        likeBtn.classList.remove('active');
        showNotification('좋아요를 취소했습니다.', 'info');
        trackLikeEvent(postId, 'remove');
    }
}

function toggleBookmark(postId) {
    const bookmarkBtn = document.querySelector('.post-action-btn.bookmark');
    if (!bookmarkBtn) return;

    isBookmarked = !isBookmarked;

    if (isBookmarked) {
        bookmarkBtn.innerHTML = '⭐ 북마크 해제';
        bookmarkBtn.classList.add('active');
        showNotification('북마크에 추가되었습니다!', 'success');
        trackBookmarkEvent(postId, 'add');
    } else {
        bookmarkBtn.innerHTML = '☆ 북마크';
        bookmarkBtn.classList.remove('active');
        showNotification('북마크에서 제거되었습니다.', 'info');
        trackBookmarkEvent(postId, 'remove');
    }
}

/* =========================================
   댓글 좋아요 기능
   ========================================= */

function toggleCommentLike(commentId) {
    const commentLikeBtn = document.querySelector(`[onclick="toggleCommentLike(${commentId})"]`);
    if (!commentLikeBtn) return;

    const isCommentLiked = commentLikeBtn.classList.contains('active');

    if (isCommentLiked) {
        commentLikeBtn.classList.remove('active');
        const countSpan = commentLikeBtn.querySelector('span');
        if (countSpan) {
            const currentCount = parseInt(countSpan.textContent) || 0;
            countSpan.textContent = Math.max(0, currentCount - 1);
        }
        showNotification('댓글 좋아요를 취소했습니다.', 'info');
    } else {
        commentLikeBtn.classList.add('active');
        const countSpan = commentLikeBtn.querySelector('span');
        if (countSpan) {
            const currentCount = parseInt(countSpan.textContent) || 0;
            countSpan.textContent = currentCount + 1;
        }
        showNotification('댓글에 좋아요를 눌렀습니다! 👍', 'success');
    }
}

/* =========================================
   답글 기능
   ========================================= */

function showReplyForm(commentId) {
    // 기존 답글 폼이 있으면 제거
    const existingReplyForm = document.querySelector('.reply-form');
    if (existingReplyForm) {
        existingReplyForm.remove();
    }

    const commentItem = document.querySelector(`[onclick="showReplyForm(${commentId})"]`).closest('.comment-item');

    const replyForm = document.createElement('div');
    replyForm.className = 'reply-form';
    replyForm.innerHTML = `
        <div class="reply-input-group">
            <textarea placeholder="답글을 입력하세요..." rows="2" required></textarea>
            <div class="reply-actions">
                <button type="button" onclick="submitReply(${commentId})" class="reply-submit">답글 등록</button>
                <button type="button" onclick="cancelReply()" class="reply-cancel">취소</button>
            </div>
        </div>
    `;

    commentItem.appendChild(replyForm);
    replyForm.querySelector('textarea').focus();
}

function submitReply(commentId) {
    const replyForm = document.querySelector('.reply-form');
    const textarea = replyForm.querySelector('textarea');
    const content = textarea.value.trim();

    if (!content) {
        showNotification('답글 내용을 입력해주세요.', 'info');
        return;
    }

    // 시뮬레이션: 답글 추가
    showNotification('답글이 등록되었습니다! 💬', 'success');
    cancelReply();
}

function cancelReply() {
    const replyForm = document.querySelector('.reply-form');
    if (replyForm) {
        replyForm.remove();
    }
}

/* =========================================
   인쇄 기능
   ========================================= */

function printPost() {
    // 인쇄용 스타일 동적 추가
    const printStyles = `
        <style id="printStyles">
            @media print {
                body * { visibility: hidden; }
                .post-article, .post-article * { visibility: visible; }
                .post-article {
                    position: absolute;
                    left: 0;
                    top: 0;
                    width: 100%;
                    padding: 20px;
                    font-size: 12pt;
                    line-height: 1.6;
                }
                .post-actions-bottom,
                .back-btn,
                .share-modal-overlay,
                .scroll-top-btn,
                .header,
                .footer { display: none !important; }
                .post-article-title {
                    font-size: 18pt;
                    color: #000 !important;
                    margin-bottom: 20px;
                }
                .post-article-meta {
                    border: 1px solid #ccc;
                    padding: 15px;
                    margin-bottom: 20px;
                    background: #f8f9fa !important;
                }
                .post-article-content h3 {
                    font-size: 14pt;
                    color: #000 !important;
                    margin-top: 20px;
                    margin-bottom: 10px;
                }
                .tag {
                    border: 1px solid #000 !important;
                    color: #000 !important;
                    background: transparent !important;
                }
                .post-info-box {
                    border: 1px solid #333 !important;
                    background: #f5f5f5 !important;
                    page-break-inside: avoid;
                }
                a { color: #000 !important; text-decoration: underline !important; }
            }
        </style>
    `;

    // 기존 인쇄 스타일 제거 후 새로 추가
    const existingStyles = document.getElementById('printStyles');
    if (existingStyles) {
        existingStyles.remove();
    }
    document.head.insertAdjacentHTML('beforeend', printStyles);

    // 인쇄 실행
    window.print();

    showNotification('인쇄 준비가 완료되었습니다! 🖨️', 'info');
    trackPrintEvent(1); // 실제로는 postId 전달

    // 인쇄 완료 후 스타일 제거
    setTimeout(() => {
        const printStylesElement = document.getElementById('printStyles');
        if (printStylesElement) {
            printStylesElement.remove();
        }
    }, 2000);
}

/* =========================================
   댓글 관련 함수들
   ========================================= */

function submitComment(form) {
    const content = form.querySelector('textarea[name="content"]').value.trim();

    if (!content) {
        showNotification('댓글 내용을 입력해주세요.', 'info');
        return false;
    }

    if (content.length > 500) {
        showNotification('댓글은 500자 이하로 작성해주세요.', 'info');
        return false;
    }

    // 시뮬레이션: 댓글 추가
    const commentsList = document.querySelector('.comments-list');
    const newComment = document.createElement('div');
    newComment.className = 'comment-item';
    newComment.innerHTML = `
        <div class="comment-header">
            <span class="comment-author">환경지킴이</span>
            <span class="comment-date">${new Date().toLocaleString('ko-KR', {year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit'})}</span>
        </div>
        <div class="comment-content">${content}</div>
        <div class="comment-actions">
            <button class="comment-like-btn" onclick="toggleCommentLike(${Date.now()})">
                👍 <span>0</span>
            </button>
            <button class="comment-reply-btn" onclick="showReplyForm(${Date.now()})">
                💬 답글
            </button>
        </div>
    `;

    commentsList.appendChild(newComment);

    // 댓글 수 업데이트
    const commentCount = document.querySelector('.post-comments h3 span');
    if (commentCount) {
        const currentCount = parseInt(commentCount.textContent);
        commentCount.textContent = currentCount + 1;
    }

    // 폼 초기화
    form.reset();

    showNotification('댓글이 등록되었습니다! 💬', 'success');

    return false; // 폼 제출 방지
}

/* =========================================
   분석 및 추적 함수들
   ========================================= */

function trackShareEvent(postId, shareType) {
    // Google Analytics 또는 기타 분석 도구에 이벤트 전송
    if (typeof gtag !== 'undefined') {
        gtag('event', 'share', {
            'event_category': 'eco_talk_post',
            'event_label': `post_${postId}`,
            'custom_parameter': shareType
        });
    }
    console.log(`Share event tracked: ${postId}, ${shareType}`);
}

function trackLikeEvent(postId, action) {
    if (typeof gtag !== 'undefined') {
        gtag('event', 'like', {
            'event_category': 'eco_talk_post',
            'event_label': `post_${postId}`,
            'custom_parameter': action
        });
    }
    console.log(`Like event tracked: ${postId}, ${action}`);
}

function trackBookmarkEvent(postId, action) {
    if (typeof gtag !== 'undefined') {
        gtag('event', 'bookmark', {
            'event_category': 'eco_talk_post',
            'event_label': `post_${postId}`,
            'custom_parameter': action
        });
    }
    console.log(`Bookmark event tracked: ${postId}, ${action}`);
}

function trackPrintEvent(postId) {
    if (typeof gtag !== 'undefined') {
        gtag('event', 'print', {
            'event_category': 'eco_talk_post',
            'event_label': `post_${postId}`
        });
    }
    console.log(`Print event tracked: ${postId}`);
}

/* =========================================
   키보드 단축키
   ========================================= */

function setupKeyboardShortcuts() {
    document.addEventListener('keydown', function(e) {
        // Ctrl/Cmd + Shift + S: 공유
        if ((e.ctrlKey || e.metaKey) && e.shiftKey && e.key === 'S') {
            e.preventDefault();
            sharePost(1, '게시글 제목'); // 실제로는 동적으로 설정
        }

        // Ctrl/Cmd + Shift + P: 인쇄
        if ((e.ctrlKey || e.metaKey) && e.shiftKey && e.key === 'P') {
            e.preventDefault();
            printPost();
        }

        // Ctrl/Cmd + Shift + L: 좋아요 토글
        if ((e.ctrlKey || e.metaKey) && e.shiftKey && e.key === 'L') {
            e.preventDefault();
            toggleLike(1); // 실제로는 동적으로 설정
        }

        // Ctrl/Cmd + Shift + B: 북마크 토글
        if ((e.ctrlKey || e.metaKey) && e.shiftKey && e.key === 'B') {
            e.preventDefault();
            toggleBookmark(1); // 실제로는 동적으로 설정
        }

        // 백스페이스: 뒤로가기 (입력 필드에서는 제외)
        if (e.key === 'Backspace' &&
            !['INPUT', 'TEXTAREA'].includes(e.target.tagName) &&
            !e.target.isContentEditable) {
            e.preventDefault();
            history.back();
        }

        // ESC: 모달 닫기
        if (e.key === 'Escape') {
            closeShareModal();
            closeMobileMenu();
            cancelReply();
        }
    });
}

/* =========================================
   유틸리티 함수들
   ========================================= */

// 외부 링크 확인 및 처리
function handleExternalLinks() {
    document.querySelectorAll('a[href^="http"]').forEach(link => {
        if (!link.hostname || link.hostname !== window.location.hostname) {
            link.target = '_blank';
            link.rel = 'noopener noreferrer';

            // 외부 링크 클릭 시 확인
            link.addEventListener('click', function(e) {
                if (!confirm('외부 사이트로 이동하시겠습니까?')) {
                    e.preventDefault();
                }
            });
        }
    });
}

// 이미지 로딩 에러 처리
function handleImageErrors() {
    document.querySelectorAll('img').forEach(img => {
        img.addEventListener('error', function() {
            this.style.display = 'none';
            console.warn('이미지 로드 실패:', this.src);
        });
    });
}

// 텍스트 선택 방지 (필요시)
function preventTextSelection(element) {
    if (element) {
        element.style.userSelect = 'none';
        element.style.webkitUserSelect = 'none';
        element.style.mozUserSelect = 'none';
        element.style.msUserSelect = 'none';
    }
}

// 페이지 가시성 변경 감지
document.addEventListener('visibilitychange', function() {
    if (document.hidden) {
        console.log('페이지가 숨겨졌습니다.');
    } else {
        console.log('페이지가 다시 보입니다.');
    }
});

// 온라인/오프라인 상태 감지
window.addEventListener('online', function() {
    showNotification('인터넷 연결이 복구되었습니다.', 'success');
});

window.addEventListener('offline', function() {
    showNotification('인터넷 연결이 끊어졌습니다.', 'warning');
});

/* =========================================
   전역 함수 등록
   ========================================= */

// 전역 함수로 등록하여 HTML에서 호출 가능하게 함
window.toggleLike = toggleLike;
window.toggleBookmark = toggleBookmark;
window.sharePost = sharePost;
window.printPost = printPost;
window.closeShareModal = closeShareModal;
window.copyShareUrl = copyShareUrl;
window.shareToKakao = shareToKakao;
window.shareToFacebook = shareToFacebook;
window.shareToTwitter = shareToTwitter;
window.shareToLine = shareToLine;
window.submitComment = submitComment;
window.toggleCommentLike = toggleCommentLike;
window.showReplyForm = showReplyForm;
window.submitReply = submitReply;
window.cancelReply = cancelReply;
window.showNotification = showNotification;

console.log('📄 환경톡톡 상세페이지 JavaScript가 완전히 로드되었습니다.');