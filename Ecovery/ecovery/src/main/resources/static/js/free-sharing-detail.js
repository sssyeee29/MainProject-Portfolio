/*
 * 무료나눔 게시글 상세 페이지 스크립트
 * 상세 페이지의 UI 렌더링, 댓글 처리, 게시글 상태 업데이트 등 다양한 인터랙션 기능을 제공
 *
 * [프론트엔드 기능 - by eunji]
 * - 대표 이미지 및 썸네일 이미지 렌더링
 * - 상세 정보 UI 구성 및 시간 포맷 처리 (formatTimeAgo)
 * - 댓글, 대댓글 입력창 및 입력/수정/삭제 UI 동작 처리
 * - 댓글 페이징 및 정렬 드롭다운, Enter 키 등록 이벤트 처리
 * - 게시글 수정/삭제 버튼 동작, 모달 UI 처리
 * - 알림 메시지(showNotification) 및 애니메이션 효과 처리
 * - 상태 배지 색상 변경 및 화면 실시간 반영 (updateProductStatus)
 * - 반응형 UI 및 사용자 경험 개선 요소 (fade-in, ESC로 모달 닫기 등)

 * [백엔드 연동 기능 - by yeonsu]
 * - 게시글 상세 정보 조회: GET /api/free/get/{freeId}
 * - 댓글 목록 조회: GET /api/replies/parent/{freeId}?sortType=&page=&amount=
 * - 댓글 등록: POST /api/replies/register
 * - 대댓글 등록: POST /api/replies/register/child
 * - 댓글 수정: PUT /api/replies/modify/{replyId}
 * - 댓글 삭제: DELETE /api/replies/remove/{replyId}
 * - 대댓글 조회: GET /api/replies/child/{parentId}
 * - 게시글 삭제: DELETE /api/free/remove/{freeId}
 * - 게시글 상태 수정(나눔중/완료): PUT /api/free/modify/{freeId} (multipart/form-data)
 * - 서버 전달 정보:
 *   - window.loginUserId, window.loginUserRole (백엔드에서 Thymeleaf로 전달)
 *   - 게시글 데이터(item), 이미지 리스트(item.imgList), 댓글(replyId 등)
 *
 * @author   : eunji, yeonsu
 * @fileName : free-sharing-detail.js
 * @since    : 250728
 */


/*서버에서 받은 데이터를 사용자가 읽기 좋게 바꿔주는 유틸리티 함수*/
// 거래상태
function getStatusText(status){
    switch (status){
        case 'ONGOING': return '나눔중';
        case 'DONE': return '나눔 완료';
        default: return '나눔중';
    }
}

// 상품상태
function getConditionText(condition){
    switch (condition){
        case 'HIGH': return '상 (매우 좋음)';
        case 'MEDIUM': return '중 (보통)';
        case 'LOW': return '하 (사용감 있음)';
        default: return '상 (매우 좋음)';
    }
}

// 등록된 시간이 현재 시간보다 얼마나 지났는지 계산
function formatTimeAgo(dateTime){
    const now = new Date();
    const created = (typeof dateTime === 'string')
        ? new Date(dateTime)
        : dateTime; // DAte 객체면 그대로

    const diff = Math.floor((now - created) / 1000); // 초단위

    if (diff < 60) return '방금 전';
    if (diff < 3600) return `${Math.floor(diff / 60)}분 전`;
    if (diff < 86400) return `${Math.floor(diff / 3600)}시간 전`;
    if (diff < 604800) return `${Math.floor(diff / 86400)}일 전`;
    return created.toLocaleDateString(); // ex: 2025.07.28
}

// 전역 변수 선언 (item을 여기에 선언)
let item = null; // 게시글 데이터를 저장할 전역 변수
let currentPage  = 1;
const amountPerPage = 10; // 한 페이지에 몇 개씩 보여줄지
let currentSortType = 'recent'; // 정렬 방식

const loginUserId = window.loginUserId ?? null;
const loginUserRole = window.loginUserRole ?? null;

// 현재 게시글의 freeId를 저장할 전역 변수
let currentFreeId = null;


// 이미지 렌더링 코드
function renderImages(images) {
    const mainImage = document.getElementById('mainImage');
    const thumbnailContainer = document.getElementById('thumbnailImages');

    if (!images || images.length === 0) {
        mainImage.alt = '이미지가 없습니다.';
        return;
    }

    // ✔️ 첫 번째 이미지를 메인 이미지로 설정
    mainImage.src = images[0].imgUrl;
    mainImage.alt = images[0].imgName;

    // ✔️ 썸네일 리스트 렌더링
    thumbnailContainer.innerHTML = '';
    images.forEach((img, index) => {
        const thumb = document.createElement('img');
        thumb.src = img.imgUrl;
        thumb.alt = img.imgName;
        thumb.classList.add('thumbnail');

        // 썸네일 클릭 시 메인 이미지 변경
        thumb.addEventListener('click', () => {
            mainImage.src = img.imgUrl;
            mainImage.alt = img.imgName;
        });

        thumbnailContainer.appendChild(thumb);
    });
}

// 댓글 등록 함수
function submitComment(e, freeId) {
    if(e) e.preventDefault(); // 폼 제출 방지

    // textarea 요소 존재 여부 확인
    const textarea = document.getElementById('commentContent');
    if (!textarea) {
        alert('로그인 후 댓글을 작성할 수 있습니다.');
        return;
    }

    const content = textarea.value.trim();
    if (content === '') {
        alert('댓글 내용을 입력해주세요.');
        return;
    }

    fetch(`/api/replies/register`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            freeId: freeId,
            content: content
        })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('서버 오류');
            }
            return response.text();
        })
        .then(data => {
            textarea.value = ''; // 입력창 초기화
            loadComments(freeId); // 부모 댓글 다시 불러오기
        })
        .catch(err => {
            console.error('댓글 등록 실패:', err);
            alert('댓글 등록 중 오류가 발생했습니다.');
        });
}

// ==============================
// 대댓글 입력창 생성 함수
// ==============================
function createChildReplyInput(parentId) {
    const container = document.getElementById(`childReplyBox-${parentId}`);
    if (!container) return;

    // textarea 생성
    const textarea = document.createElement('textarea');
    textarea.id = `childCommentInput-${parentId}`;
    textarea.classList.add('edit-textarea'); // ← 여기서 CSS 클래스 추가
    textarea.placeholder = '답글을 입력하세요';

    // 등록 버튼 생성
    const submitBtn = document.createElement('button');
    submitBtn.textContent = '등록';
    submitBtn.onclick = () => submitChildComment(parentId);

    // 기존 내용 비우고 새로 추가
    container.innerHTML = '';
    container.appendChild(textarea);
    container.appendChild(submitBtn);

    // Enter 키로 등록하는 이벤트 연결
    setupChildReplyEnterEvent(parentId);
}


// 대댓글 등록 함수
function submitChildComment(parentId) {
    const input = document.getElementById(`childCommentInput-${parentId}`);
    const content = input.value.trim();
    // const freeId = item?.freeId;
    // ❤️
    const freeId = typeof currentFreeId !== 'undefined' ? currentFreeId : null;

    if (!content) {
        alert('대댓글 내용을 입력해주세요.');
        return;
    }

    fetch(`/api/replies/register/child`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            freeId: freeId,
            parentId: parentId,
            content: content
        })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('서버 오류');
            }
            return response.text(); // ← 대댓글도 문자열 반환
        })
        .then(() => {
            input.value = '';
            loadChildReplies(parentId); // ❤️대댓글 등록 후 해당 부모 댓글의 대댓글 목록만 새로고침하여 열린 상태 유지
        })
        .catch(err => {
            console.error('대댓글 등록 실패:', err);
            alert('대댓글 등록 중 오류가 발생했습니다.');
        });
}

// 대댓글 입력창에서 Enter 키로 등록되도록 이벤트 연결
function setupChildReplyEnterEvent(parentId) {
    const input = document.getElementById(`childCommentInput-${parentId}`);
    if (!input) return;

    input.addEventListener('keydown', function (e) {
        // '수정 중' 상태인지 확인하는 변수
        const isEditing = input.dataset.isEditing === 'true';

        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault(); // 기본 동작(줄바꿈) 방지

            if (isEditing) {
                // 수정 중일 때는 수정 완료 함수 호출
                submitEditedChildComment(parentId); // 예시 함수
                input.dataset.isEditing = 'false'; // 상태 초기화
            } else {
                // 일반적인 대댓글 작성 중일 때는 등록 함수 호출
                submitChildComment(parentId);
            }
        }
    });
}

// 페이징 렌더링 함수
function renderReplyPagination(totalCount, freeId, sortType) {
    const totalPages = Math.ceil(totalCount / amountPerPage);
    const paginationContainer = document.getElementById('pagination');

    if (!paginationContainer) return;
    paginationContainer.innerHTML = ''; // 기존 페이징 버튼 초기화

    // 이전 버튼
    const prevBtn = document.createElement('button');
    prevBtn.textContent = '이전';
    prevBtn.classList.add('page-btn', 'prev-btn');
    prevBtn.disabled = currentPage  === 1; // 변수명 변경
    prevBtn.addEventListener('click', () => {
        if (currentPage  > 1) {
            currentPage --; // 변수명 변경
            loadComments(freeId, sortType, currentPage , amountPerPage);
        }
    });
    paginationContainer.appendChild(prevBtn);


    // 페이지 번호 버튼들
    for (let i = 1; i <= totalPages; i++) {
        const btn = document.createElement('button');
        btn.textContent = i;
        btn.classList.add('page-btn');
        btn.disabled = i === currentPage ; // 변수명 변경

        if (i === currentPage ) { // 변수명 변경
            btn.classList.add('active');
        }

        btn.addEventListener('click', () => {
            currentPage  = i; // 변수명 변경
            loadComments(freeId, sortType, currentPage , amountPerPage);
        });
        paginationContainer.appendChild(btn);
    }

    // 다음 버튼
    const nextBtn = document.createElement('button');
    nextBtn.textContent = '다음';
    nextBtn.classList.add('page-btn', 'next-btn');
    nextBtn.disabled = currentPage  === totalPages; // 변수명 변경
    nextBtn.addEventListener('click', () => {
        if (currentPage  < totalPages) {
            currentPage ++; // 변수명 변경
            loadComments(freeId, sortType, currentPage , amountPerPage);
        }
    });
    paginationContainer.appendChild(nextBtn);
}

// 댓글 목록 불러오는 함수
function loadComments(freeId, sortType = currentSortType, page = currentPage, amount = amountPerPage) {

    if (!freeId) {
        console.log("freeId가 null이거나 유효하지 않아 댓글을 불러올 수 없습니다.");
        return;
    }

    // 전역 변수 활용
    currentSortType = sortType;
    currentPage = page;
    currentFreeId = freeId;

    fetch(`/api/replies/parent/${freeId}?sortType=${sortType}&page=${page}&amount=${amount}`)
        .then(response => response.json())
        .then(data => {

            console.log('loginUserId:', loginUserId);
            console.log('loginUserRole:', loginUserRole);
            console.log('서버 응답 데이터:', data); // 서버 응답 데이터 확인용 로그

            const list = document.getElementById('commentList');
            if (!list) {
                console.error("commentList 요소를 찾을 수 없습니다.");
                return;
            }
            list.innerHTML = '';

            // 댓글이 없을 경우 처리
            if (data.list.length === 0) {
                list.innerHTML = '<p class="no-comments">아직 댓글이 없습니다.</p>';
            } else {
                data.list.forEach(parent => {
                    const parentDiv = document.createElement('div');
                    parentDiv.className = 'comment-item';
                    parentDiv.id = `comment-item-${parent.replyId}`;
                    parentDiv.innerHTML = `
                        <div class="comment-header">
                            <p class="comment-author">${parent.nickname}</p>
                        </div>
                        <p class="comment-content" id="content-${parent.replyId}">${parent.content}</p>
                        <textarea class="edit-textarea" id="edit-${parent.replyId}" style="display: none;">${parent.content}</textarea>
                        <p class="comment-date">${formatTimeAgo(parent.createdAt)}</p>

                        ${loginUserId === parent.memberId || loginUserRole === 'ADMIN' ? `
                            <div class="comment-actions">
                                <button class="comment-action-btn reply-btn" onclick="toggleEdit(${parent.replyId})">수정</button>
                                <button class="comment-action-btn" onclick="deleteComment(${parent.replyId}, false)">삭제</button>
                            </div>
                        ` : ''}

                        <div class="reply-toggle-container" id="reply-toggle-section-${parent.replyId}">
                            <button class="reply-toggle-btn" onclick="toggleChildReplies(${parent.replyId})">
                                대댓글 보기
                            </button>
                        </div>

                        <div class="child-reply-section" id="child-reply-section-${parent.replyId}" style="display: none;">
                            <div class="child-comments" id="child-${parent.replyId}"></div>
                            ${loginUserId ? `
                                <div class="reply-form">
                                    <textarea id="childCommentInput-${parent.replyId}" placeholder="대댓글을 입력하세요..."></textarea>
                                    <button onclick="submitChildComment(${parent.replyId})">답글등록</button>
                                </div>
                            ` : ''}
                        </div>
                    `;
                    list.appendChild(parentDiv);

                    if (loginUserId) {
                        setupChildReplyEnterEvent(parent.replyId);
                    }
                });
            }

            // // 페이지 이동 시 댓글 목록의 상단으로 스크롤
            // const commentList = document.getElementById('commentList');
            // if (commentList) {
            //     commentList.scrollIntoView({ behavior: 'smooth' });
            // }

            // ⭐ forEach 루프 밖에서 페이징 렌더링 함수를 한 번만 호출
            renderReplyPagination(data.total, freeId, sortType);
        })
        .catch(error => console.error('Error fetching comments:', error));
}

// 대댓글 목록 토글 함수
// - 고유 ID를 사용하여 특정 부모 댓글의 버튼을 정확히 선택
// - 대댓글 섹션을 열 때만 'loadChildReplies' 호출
function toggleChildReplies(parentId) {
    const childReplySection = document.getElementById(`child-reply-section-${parentId}`);
    // 고유 ID를 사용하여 정확한 버튼을 찾음
    const toggleBtn = document.querySelector(`#reply-toggle-section-${parentId} .reply-toggle-btn`);

    if (!childReplySection || !toggleBtn) {
        console.error(`대댓글 섹션 또는 토글 버튼을 찾을 수 없습니다: ${parentId}`);
        return;
    }

    if (childReplySection.style.display === 'none') {
        childReplySection.style.display = 'block';
        toggleBtn.textContent = '대댓글 숨기기';
        // 대댓글 열때만 대댓글을 불러오도록 호출
        loadChildReplies(parentId);
    } else {
        childReplySection.style.display = 'none';
        toggleBtn.textContent = '대댓글 보기';
    }
}

// ✨ 대댓글만 새로고침하는 함수 (새로 추가됨)
// - 특정 부모 댓글의 대댓글만 동적으로 불러와 HTML에 추가
function loadChildReplies(parentId) {
    const childContainer = document.getElementById(`child-${parentId}`);
    if (!childContainer) {
        console.error(`대댓글 컨테이너를 찾을 수 없습니다: child-${parentId}`);
        return;
    }

    // 대댓글 섹션 자체를 찾습니다. 이 섹션이 toggleChildReplies에 의해 열리고 닫힙니다.
    const childReplySection = document.getElementById(`child-reply-section-${parentId}`);
    if (!childReplySection) {
        console.error(`대댓글 섹션(child-reply-section-${parentId})을 찾을 수 없습니다.`);
        return;
    }


    childContainer.innerHTML = ''; // 기존 대댓글 초기화

    fetch(`/api/replies/child/${parentId}`)
        .then(res => res.json())
        .then(childReplies => {
            childReplies.forEach(child => {
                const childDiv = document.createElement('div');
                childDiv.className = 'child-comment-item';
                childDiv.innerHTML = `
                     <div class="child-comment-header"> <!-- ✨ 이 div를 추가합니다. -->
                        <p class="child-author">↳ ${child.nickname}</p>
                    </div>
                    <p class="child-content" id="content-${child.replyId}">${child.content}</p>
                    <textarea class="edit-textarea" id="edit-${child.replyId}" style="display: none;">${child.content}</textarea>
                    <p class="child-date">${formatTimeAgo(child.createdAt)}</p>

                    ${loginUserId === child.memberId || loginUserRole === 'ADMIN' ? `
                        <div class="comment-actions">
                            <button class="comment-action-btn reply-btn" onclick="toggleEdit(${child.replyId})">수정</button>
                            <button class="comment-action-btn" onclick="deleteComment(${child.replyId}, true)">삭제</button>
                        </div>
                    ` : ''}
                `;
                childContainer.appendChild(childDiv);
            });
            // 대댓글이  로딩이 완료된 후, 해당 섹션이 강제로 보이게
            childReplySection.style.display = 'block';

            // 또한, 토글 버튼의 텍스트도 '숨기기'로 업데이트하여 UI 일관성을 유지합니다.
            const toggleBtn = document.querySelector(`#reply-toggle-section-${parentId} .reply-toggle-btn`);
            if (toggleBtn) {
                toggleBtn.textContent = '대댓글 숨기기';
            }
        })
        .catch(error => console.error('Error fetching child replies:', error));
}


// 조회수 증가
function updateViewCount(freeId) {
    fetch(`/api/free/get/${freeId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        }
    })
        .then(response => response.json())
        .then(data => {
            const viewCount = data.free?.viewCount;
            if (viewCount !== undefined) {
                const viewCountElement = document.querySelector('.view-count');
                if (viewCountElement) {
                    viewCountElement.textContent = `👀 ${viewCount}`;
                }
            }
        })
        .catch(error => {
            console.error('조회수 증가 오류:', error);
        });
}


// =========================
// 페이지가 로드되면 실행되는 함수
// =========================
document.addEventListener('DOMContentLoaded', async function() {
    console.log('무료나눔 상세페이지가 로드되었습니다!');

    // 경로(path)에서 freeId 추출
    const pathParts = window.location.pathname.split('/');
    const freeId = pathParts[pathParts.length - 1]; // 마지막 segment가 freeId

    if (!freeId) {
        alert('잘못된 접근입니다.');
        return;
    }

    try {
        // 게시글 데이터 비동기 조회
        const response = await fetch(`/api/free/get/${freeId}`);
        if (!response.ok) throw new Error('게시글 정보를 불러올 수 없습니다.');
        const data = await response.json(); // 응답을 json으로 파싱

        console.log('서버 응답 전체 data:', data);         // 서버에서 받은 전체 JSON
        console.log('data.free:', data.free);               // free 객체만 추출
        console.log('freeId:', data.free?.freeId);          // freeId 값만

        item = data.free; // data.free -> 실제 게시글 정보 // 이 한줄로 전역 item에 저장

        console.log('📦 item 객체:', item);

        // 상세페이지 렌더링
        renderDetailPage(item);

        // 이미지 렌더링 코드
        renderImages(item.imgList);

        // 댓글 목록 기본 정렬 (최신순)
        loadComments(item.freeId, 'recent', currentPage, amountPerPage);

        // 댓글 등록 이벤트 연결
        const submitCommentBtn = document.getElementById('submitCommentBtn');
        if (submitCommentBtn) {
            submitCommentBtn.addEventListener('click', function (e){
                submitComment(e, item.freeId); // e와 item.freeId 전달
            });
        }
        // 댓글 입력창에서 Enter 키로 등록
        const commentTextarea = document.getElementById('commentContent');
        if (commentTextarea) {
            commentTextarea.addEventListener('keydown', function (e) {
                if (e.key === 'Enter' && !e.shiftKey) {
                    e.preventDefault(); // 줄바꿈 방지
                    submitComment(null, item.freeId); // Enter로 등록
                }
            });
        }

        // 댓글 정렬 셀렉트박스 이벤트
        const sortSelect = document.getElementById('sortSelect');
        if (sortSelect) {
            sortSelect.addEventListener('change', () => {
                currentSortType  = sortSelect.value; // 'recent' 또는 'oldest'
                loadComments(item.freeId, currentSortType, currentPage, amountPerPage); // 여기서 전달을 해야지 정렬됨!
            });
        }

        // fade-in 애니메이션
        setTimeout(function () {
            const detailContainer = document.querySelector('.detail-container');
            if (detailContainer) {
                detailContainer.classList.add('fade-in');
            }
        }, 200);


        // 수정 버튼 클릭 시 editPost() 호출
        const editBtn = document.getElementById('editBtn');
        if (editBtn) {
            editBtn.addEventListener('click', (e) => {
                e.preventDefault(); // a 태그 기본 동작 방지
                editPost(); // confirm + 알림 + 1초 후 이동
            });
        }

        // 삭제 버튼 클릭 시 deletePost() 호출
        const deleteBtn = document.getElementById('deleteBtn');
        if (deleteBtn) {
            deleteBtn.addEventListener('click', (e) => {
                e.preventDefault(); // 기본 링크 막기
                deletePost(item.freeId);
            });
        }

        // 모달 배경 클릭시 닫기
        const modals = document.querySelectorAll('.modal');
        modals.forEach(function (modal) {
            modal.addEventListener('click', function (event) {
                if (event.target === modal) {
                    closeModal(modal.id);
                }
            });
        });

    }catch (err) {
        console.error(err);
        alert('상세 정보를 불러오는 중 오류가 발생했습니다.')
    }

    setupEventListeners(); // 모든 이벤트 리스너를 설정하는 함수

});

// =========================
// 댓글 수정창 열기/닫기 및 버튼 텍스트 변경
// =========================
function toggleEdit(replyId) {
    const contentEl = document.getElementById(`content-${replyId}`);
    const textareaEl = document.getElementById(`edit-${replyId}`);

    if (!contentEl || !textareaEl) return;

    // ✨ 수정: contentEl의 부모 요소를 정확히 찾아옵니다.
    const currentCommentOrChildCommentItem = contentEl.closest('.comment-item, .child-comment-item');

    if (!currentCommentOrChildCommentItem) {
        console.error(`toggleEdit: 유효한 댓글/대댓글 컨테이너를 찾을 수 없습니다. replyId: ${replyId}`);
        return;
    }

    // ✨ 수정: 선언된 currentCommentOrChildCommentItem 변수를 사용합니다.
    const commentActionsDiv = currentCommentOrChildCommentItem.querySelector('.comment-actions');

    if (!commentActionsDiv) {
        console.error("comment-actions div를 찾을 수 없습니다:", replyId);
        return;
    }

    // 수정 또는 등록 버튼을 찾음
    const editButton = commentActionsDiv.querySelector('.comment-action-btn.reply-btn');

    if (!editButton) {
        console.error("수정 버튼(.comment-action-btn.reply-btn)을 찾을 수 없습니다:", replyId);
        return;
    }

    //  현재 댓글이 대댓글인지 여부를 확인
    // 'child-comment-item' 클래스를 가지고 있으면 대댓글
    const isChild = currentCommentOrChildCommentItem.classList.contains('child-comment-item');

    const isEditing = textareaEl.style.display === 'block';

    if (isEditing) {
        // 편집모드 종료
        textareaEl.style.display = 'none';
        contentEl.style.display = 'block';

        // '등록' 버튼 텍스트를 다시 '수정'으로 변경
        editButton.textContent = '수정';
        // '등록' 상태 스타일 클래스 제거
        editButton.classList.remove('is-submitting-edit');
        // 버튼 클릭 시 다시 toggleEdit 함수를 호출하도록 변경
        editButton.onclick = () => toggleEdit(replyId);

        // Enter 키 이벤트 리스너 제거 (중복 방지)
        textareaEl.removeEventListener('keydown', handleEnterKey);

    } else {
        // 편집모드 시작
        textareaEl.style.display = 'block';
        contentEl.style.display = 'none';
        textareaEl.focus();

        // '수정' 버튼 텍스트를 '등록'으로 변경
        editButton.textContent = '등록';
        // '등록' 상태 스타일 클래스 추가
        editButton.classList.add('is-submitting-edit');
        // 버튼 클릭 시 submitEdit 함수를 호출하도록 변경
        editButton.onclick = () => submitEdit(replyId, isChild);

        // Enter 키 이벤트 리스너 추가
        textareaEl.addEventListener('keydown', handleEnterKey);
    }
}

//️ Enter 키 이벤트 핸들러
function handleEnterKey(event) {
    if (event.key === 'Enter' && !event.shiftKey) {
        event.preventDefault(); // 기본 엔터 동작 방지

        const replyId = event.target.id.replace('edit-', '');
        const textareaEl = event.target; // 현재 이벤트가 발생한 textarea 요소

        // 현재 textarea가 속한 댓글/대댓글 컨테이너를 찾습니다.
        const currentCommentOrChildCommentItem = textareaEl.closest('.comment-item, .child-comment-item');

        if (!currentCommentOrChildCommentItem) {
            console.error(`handleEnterKey: 유효한 댓글/대댓글 컨테이너를 찾을 수 없습니다. replyId: ${replyId}`);
            return;
        }

        // 현재 댓글이 대댓글인지 여부를 확인합니다.
        const isChild = currentCommentOrChildCommentItem.classList.contains('child-comment-item');

        // submitEdit 함수에 isChild 값을 함께 넘겨줍니다.
        submitEdit(replyId, isChild); // Enter 키로 수정 완료
    }
}


// ️수정 내용을 서버에 전송하는 함수 (예시)
function submitEdit(replyId) {
    const textareaEl = document.getElementById(`edit-${replyId}`);
    const newContent = textareaEl.value.trim();
    // 현재 수정 중인 댓글이 대댓글인지 확인
    const isChild = textareaEl.closest('.child-comment-item') !== null;

    if (newContent === '') {
        alert('내용을 입력해주세요.');
        return;
    }


    fetch(`/api/replies/modify/${replyId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ content: newContent })
    })
        .then(res => {
            if (res.ok) {
                alert('댓글이 수정되었습니다.');
                // ✨ 이 부분이 변경되었습니다.
                // UI 즉시 업데이트
                const contentEl = document.getElementById(`content-${replyId}`);
                contentEl.textContent = newContent;
                toggleEdit(replyId); // 읽기 모드로 전환

                // 만약 대댓글을 수정했다면 부모 댓글의 대댓글 목록을 새로고침
                if (isChild) {
                    // ✨ 변경된 부분: 부모 댓글 컨테이너의 ID에서 직접 replyId를 추출합니다.
                    const parentCommentItem = textareaEl.closest('.comment-item');
                    if (parentCommentItem) {
                        const parentIdMatch = parentCommentItem.id.match(/comment-item-(\d+)/); // ✨ 정규식 변경
                        if (parentIdMatch && parentIdMatch[1]) {
                            const parentId = parentIdMatch[1];
                            loadChildReplies(parentId); // 해당 부모의 대댓글만 새로고침
                        } else {
                            console.error("부모 댓글 ID를 찾을 수 없습니다:", parentCommentItem.id);
                        }
                    } else {
                        console.error("대댓글의 부모 댓글 아이템을 찾을 수 없습니다.");
                    }
                } else {
                    // 일반 댓글 수정 시 전체 댓글 목록 새로고침
                    loadComments(currentFreeId, currentSortType, currentPage);
                }
            } else if (res.status === 403) {
                alert('수정 권한이 없습니다.');
            } else {
                alert('댓글 수정 실패');
            }
        })
        .catch(err => console.error('댓글 수정 오류:', err));
}


// =========================
// 댓글 수정 제출
// =========================
function submitEdit(replyId, isChild) {
    const textarea = document.getElementById(`edit-${replyId}`);
    if (!textarea) return;

    const updatedContent = textarea.value.trim();

    if (updatedContent === '') {
        alert('수정할 내용을 입력해주세요.');
        return;
    }

    fetch(`/api/replies/modify/${replyId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ content: updatedContent })
    })
        .then(res => {
            if (res.ok) {
                alert('댓글이 수정되었습니다.');

                // UI 즉시 업데이트
                const contentEl = document.getElementById(`content-${replyId}`);
                contentEl.textContent = updatedContent;
                toggleEdit(replyId); // 읽기 모드로 전환

                if (isChild) {
                    const parentCommentItem = textareaEl.closest('.comment-item');
                    if (parentCommentItem) {
                        const parentIdMatch = parentCommentItem.id.match(/comment-item-(\d+)/);
                        if (parentIdMatch && parentIdMatch[1]) {
                            const parentId = parentIdMatch[1];
                            // loadChildReplies만 호출
                            loadChildReplies(parentId);
                        }
                    }
                } else {
                    loadComments(currentFreeId, currentSortType, currentPage);
                }
            } else if (res.status === 403) {
                alert('수정 권한이 없습니다.');
            } else {
                alert('댓글 수정 실패');
            }
        })
        .catch(err => console.error('댓글 수정 오류:', err));
}
// =========================
// 댓글 삭제
// =========================
function deleteComment(replyId, isChild) {
    if (!confirm('정말 삭제하시겠습니까?')) return;

    fetch(`/api/replies/remove/${replyId}`, {
        method: 'DELETE'
    })
        .then(res => {
            if (res.ok) {
                alert('댓글이 삭제되었습니다.');
                location.reload();
            } else {
                alert('삭제 실패');
            }
        })
        .catch(err => console.error('댓글 삭제 오류:', err));
}



// =========================
// 상세 페이지 렌더링 함수
// =========================
function renderDetailPage(item) {
    // 제목
    document.getElementById('itemTitle').textContent = item.title;
    document.getElementById('detailTitle').textContent = item.title;

    // 작성자 닉네임
    document.getElementById('authorNickname').textContent = item.nickname;
    document.getElementById('detailAuthor').textContent = item.nickname;

    // 등록일 (예: 2025-07-29 형식으로 변환)
    // LocalDateTime → 문자열로 전달된 createdAt 값 (예: "2025-07-28 15:30:00")
    const rawCreatedAt = item.createdAt;

    // 1. "2025-07-28 15:30:00" → "2025-07-28T15:30:00"
    const isoString = rawCreatedAt.replace(' ', 'T');

    // 2. Date 객체 생성
    const createdDate = new Date(isoString);

    // 3. 날짜 출력
    document.getElementById('detailDate').textContent = createdDate.toLocaleDateString('ko-KR');

    // 4. 상대 시간 출력
    document.getElementById('createdAt').textContent = formatTimeAgo(isoString);

    // // 조회수
    // document.getElementById('detailViews').textContent = item.viewCount;

    // 상품 상태
    document.getElementById('detailCondition').textContent = getConditionText(item.itemCondition);

    // 나눔 상태
    document.getElementById('itemStatus').textContent = getStatusText(item.dealStatus); // 배지

    // 카테고리
    document.getElementById('detailCategory').textContent = item.category;

    // 나눔 지역
    document.getElementById('regionGu').textContent = item.regionGu;
    document.getElementById('regionDong').textContent = item.regionDong;

    // 상세 설명
    document.getElementById('detailContent').textContent = item.content;

    // 작성자 info 영역 (위쪽 카드)
    document.getElementById('authorNickname').textContent = item.nickname;
    document.getElementById('viewCount').textContent = '👀 ' + item.viewCount;
    document.getElementById('createdAt').textContent = formatTimeAgo(item.createdAt);
}

// =========================
// 이벤트 리스너 설정 함수
// =========================
function setupEventListeners() {

    // ESC 키로 모달 닫기 (드롭다운 관련 코드 제거)
    document.addEventListener('keydown', function (event) {
        if (event.key === 'Escape') {
            closeAllModals();
        }
    });

// =========================
// 이미지 관련 함수
// =========================

// 메인 이미지 변경 (썸네일 클릭시)
    function changeMainImage(thumbnail) {
        const mainImage = document.getElementById('mainImage');
        const allThumbnails = document.querySelectorAll('.thumbnail');

        if (mainImage && thumbnail) {
            // 메인 이미지 변경
            mainImage.src = thumbnail.src.replace('80x80', '500x400');

            // 기존 active 클래스 제거
            allThumbnails.forEach(function (thumb) {
                thumb.classList.remove('active');
            });

            // 클릭된 썸네일에 active 클래스 추가
            thumbnail.classList.add('active');

            // 애니메이션 효과
            mainImage.style.transform = 'scale(0.95)';
            setTimeout(function () {
                mainImage.style.transform = 'scale(1)';
            }, 150);

            console.log('메인 이미지가 변경되었습니다.');
        }
    }

// 특정 모달 닫기
    function closeModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.remove('show');
            document.body.style.overflow = '';
            console.log(modalId + ' 모달이 닫혔습니다.');
        }
    }

// 모든 모달 닫기
    function closeAllModals() {
        const modals = document.querySelectorAll('.modal');
        modals.forEach(function (modal) {
            modal.classList.remove('show');
        });
        document.body.style.overflow = '';
    }

// 시간 포맷팅 함수
    function formatTime(date) {
        const hours = date.getHours();
        const minutes = date.getMinutes();
        const ampm = hours >= 12 ? '오후' : '오전';
        const displayHours = hours % 12 || 12;
        const displayMinutes = minutes.toString().padStart(2, '0');

        return `${ampm} ${displayHours}:${displayMinutes}`;
    }

// =========================
// 게시글 관리 함수 (수정/삭제)
// =========================

// 게시글 수정
    function editPost() {

        if (confirm('게시글을 수정하시겠습니까?')) {
            showNotification('수정 페이지로 이동합니다.', 'success');

            setTimeout(function () {
                if (item && item.freeId) { // item이 로드되었는지 확인
                    window.location.href = '/free/modify/' + item.freeId;
                } else {
                    console.error('게시글 ID를 찾을 수 없습니다.');
                    showNotification('게시글 정보를 불러오는 중 오류가 발생했습니다.', 'error');
                }
            }, 1000);
        }
    }

// 게시글 삭제 함수 (API 컨트롤러 사용)
    function deletePost(freeId) {

        if (confirm('정말로 이 게시글을 삭제하시겠습니까?\n삭제된 게시글은 복구할 수 없습니다.')) {
            // 삭제 중 상태 표시
            showNotification('게시글을 삭제하는 중입니다...', 'info');

            // 서버에 삭제 요청
            fetch(`/api/free/remove/${freeId}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                }
            })
                .then(response => {
                    if (!response.ok) {
                        // 서버에서 오류 응답 보낸 경우
                        return response.text().then(text => {
                            throw new Error(text || '삭제 실패');
                        });
                    }
                    return response.text(); // 성공 메시지 텍스트
                })
                .then(message => {
                    showNotification(message || '게시글이 삭제되었습니다.', 'success');
                    setTimeout(() => {
                        window.location.href = '/free/list'; // 목록으로 이동
                    }, 1500);
                })
                .catch(error => {
                    console.error('게시글 삭제 오류:', error);
                    showNotification(error.message || '삭제 중 오류가 발생했습니다.', 'error');
                });
        }
    }

// =========================
// 알림 메시지 관련 함수
// =========================

// 알림 메시지 표시
    function showNotification(message, type) {
        type = type || 'success';

        // 기존 알림 제거
        const existingNotification = document.querySelector('.notification');
        if (existingNotification) {
            existingNotification.remove();
        }

        // 새 알림 생성
        const notification = document.createElement('div');
        notification.className = `notification ${type}`;
        notification.textContent = message;

        // 페이지에 추가
        document.body.appendChild(notification);

        // 애니메이션으로 표시
        setTimeout(function () {
            notification.classList.add('show');
        }, 100);

        // 3초 후 자동 제거
        setTimeout(function () {
            notification.classList.remove('show');
            setTimeout(function () {
                if (notification.parentNode) {
                    notification.parentNode.removeChild(notification);
                }
            }, 300);
        }, 3000);
    }

// =========================
// 기타 유틸리티 함수
// =========================


// 상품 상태 업데이트
    function updateProductStatus(newStatus) {

        console.log('🔄 상태 업데이트 실행됨:', newStatus);

        const formData = new FormData();

        // // 기존 게시글 데이터 포함 (freeDto)
        // const freeDto = {
        //     ...item,
        //     dealStatus: newStatus // 상태만 변경
        // };

        const freeDto = {
            freeId: item.freeId,
            title: item.title,
            content: item.content,
            category: item.category,
            regionGu: item.regionGu,
            regionDong: item.regionDong,
            itemCondition: item.itemCondition,
            dealStatus: newStatus
        };

        // JSON -> Blob 변환 후 추가
        formData.append("freeDto", new Blob([JSON.stringify(freeDto)], {type: "application/json"}));

        // 이미지 파일은 없는 경우 빈 배열 전달 또는 기존 이미지 유지
        const dummyFileList = []; // 이미지가 필요하면 추가 가능
        dummyFileList.forEach(file => {
            formData.append("imgFile", file); // 이미지 파일은 여럿일 수 있으니 반복
        });

        fetch(`/api/free/modify/${item.freeId}`, {
            method: 'PUT',
            body: formData
        })
            .then(response => response.text())
            .then(message => {
                if (message.includes("수정되었습니다.")) {
                    const statusBadge = document.querySelector('.status-badge');
                    if (statusBadge) {
                        statusBadge.textContent = newStatus;

                        // 상태에 따른 색상 변경
                        statusBadge.className = 'status-badge';
                        let displayText = '';

                        switch (newStatus) {
                            case 'ONGOING':
                                displayText = '나눔중';
                                statusBadge.style.background = 'var(--success-green)';
                                break;
                            case 'DONE':
                                displayText = '나눔완료';
                                statusBadge.style.background = 'var(--medium-gray)';
                                break;
                        }
                        // 상태 배지에 텍스트 적용
                        statusBadge.textContent = displayText;

                        // 클래스 초기화 (필요시 스타일 적용을 위해)
                        statusBadge.className = 'status-badge';
                    }
                    showNotification('상품 상태가 업데이트되었습니다.', 'success');
                } else {
                    showNotification(message || '상태 업데이트 중 오류가 발생했습니다.', 'error');
                }
            })
            .catch(error => {
                console.error('상태 업데이트 오류:', error);
                showNotification('상태 업데이트 중 오류가 발생했습니다.', 'error');
            });
    }


// =========================
// 전역 함수로 노출 (HTML에서 onclick 등으로 사용)
// =========================

// HTML의 onclick에서 사용할 수 있도록 전역으로 노출
    window.changeMainImage = changeMainImage;
    window.closeModal = closeModal;
    window.editPost = editPost;
    window.deletePost = deletePost;
    window.updateViewCount = updateViewCount;

// 기타 유용한 전역 함수들
    window.showNotification = showNotification;
    window.updateProductStatus = updateProductStatus;

// Promise 거부 처리
    window.addEventListener('unhandledrejection', function (event) {
        console.error('처리되지 않은 Promise 거부:', event.reason);
        showNotification('작업 처리 중 오류가 발생했습니다.', 'error');
    });

// =========================
// 최종 로그
// =========================

    console.log('🤝 무료나눔 상세페이지 JavaScript가 로드되었습니다.');
    console.log('📝 사용 가능한 기능:');
    console.log('   - 작성자 권한에 따른 수정/삭제 버튼');
    console.log('   - 드롭다운 메뉴 토글');
    console.log('   - 이미지 썸네일 변경');
    console.log('   - 연락처/채팅 모달');
    console.log('   - 키보드 단축키 지원');
    console.log('   - 반응형 UI');
}
