/**
 * 공지사항 게시글 단건 조회 스크립트
 * URL의 noticeId를 기반으로 API를 통해 게시글 상세 정보를 비동기로 불러와 화면에 표시함
 * 제목, 작성자, 작성일, 조회수, 내용을 동적으로 렌더링
 *
 * @author : yukyeong
 * @fileName : notice-get.js
 * @since : 250722
 * @history
 *   - 250722 | yukyeong | 게시글 단건 조회 비동기 처리 기능 최초 작성
 *   - 250801 | yukyeong | 공지사항 전용으로 구조 개편:
 *                         - noticeId 기반 데이터 조회 및 렌더링
 *                         - 카테고리 코드 매핑(categoryMap) 적용
 *                         - 수정일/작성일 구분 표시 기능 추가
 *                         - 관리자용 글 수정/삭제 버튼 처리 기능 추가
 *                         - 삭제 후 목록 이동 처리 추가
     - 250802 | yukyeong | 작성자 정보 렌더링 개선:
                            - roleMap 추가로 관리자/사용자 역할 표시
                            - avatar 기본 이모지 설정
                            - notice-role, notice-avatar 요소에 값 바인딩 추가
     - 250806 | yukyeong | 목록 이동 시 검색어·카테고리·페이지 상태 유지 처리
                            - backToListBtn 클릭 시 URL 파라미터 기반 이동

 */


const categoryMap = {
    "important": "중요 공지",
    "service": "서비스",
    "maintenance": "점검",
    "event": "이벤트",
    "general": "일반"
};

document.addEventListener("DOMContentLoaded", function () {
    const urlParams = new URLSearchParams(window.location.search);
    const noticeId = urlParams.get("noticeId");

    // ✅ 목록으로 돌아가기 버튼 처리
    const backBtn = document.getElementById("backToListBtn");
    if (backBtn) {
        const pageNum = urlParams.get("pageNum") || 1;
        const keyword = urlParams.get("keyword") || '';
        const type = urlParams.get("type") || '';
        const category = urlParams.get("category") || '';

        const backUrl = `/notice/list?pageNum=${pageNum}&keyword=${encodeURIComponent(keyword)}&type=${type}&category=${category}`;
        backBtn.addEventListener("click", function () {
            window.location.href = backUrl;
        });
    }

    if (!noticeId) {
        alert("잘못된 접근입니다.");
        return;
    }

    fetch(`/api/notice/get/${noticeId}`)
        .then(res => {
            if (!res.ok) throw new Error("데이터 조회 실패");
            return res.json();
        })
        .then(data => {
            // 날짜 포맷 처리
            const displayDate = (data.updatedAt && data.updatedAt !== data.createdAt)
                ? formatDate(data.updatedAt)
                : formatDate(data.createdAt);

            const dateLabel = document.querySelector(".notice-date span:first-child");
            if (data.updatedAt && data.updatedAt !== data.createdAt) {
                dateLabel.textContent = "수정일:";
            }

            const views = new Intl.NumberFormat().format(data.viewCount) + '회';

            const roleMap = {
                "ADMIN": "관리자",
                "USER": "사용자"
            };

            // 게시글 정보 렌더링
            document.getElementById("notice-title").textContent = data.title;
            document.getElementById("notice-author").textContent = data.nickname ?? "알 수 없음";
            document.getElementById("notice-avatar").textContent = data.avatar ?? '👨‍💼';
            document.getElementById("notice-role").textContent = roleMap[data.role] ?? data.role ?? "";
            document.getElementById("notice-date").textContent = displayDate;
            document.getElementById("notice-views").textContent = views;
            document.getElementById("notice-content").innerHTML = data.content;

            // ✅ JS에서 category 코드 → 표시 이름 매핑
            if (data.category) {
                const displayName = categoryMap[data.category] || "기타";
                document.getElementById("notice-category").textContent = displayName;
                document.getElementById("notice-category-wrapper").style.display = 'block';
            }

            // ✅ 글 수정 버튼 href 설정
            const editBtn = document.getElementById("editBtn");
            if (editBtn) {
                editBtn.href = `/notice/modify/${data.noticeId}`;
            }

            // ✅ 글 삭제 버튼 이벤트 바인딩
            const deleteBtn = document.getElementById("deleteBtn");
            if (deleteBtn) {
                deleteBtn.addEventListener("click", function () {
                    deletePost(data.noticeId);
                });
            }
        })
        .catch(err => {
            console.error("조회 실패", err);
            alert("게시글을 불러오는 중 오류가 발생했습니다.");
        });

});

function formatDate(dateTime) {
    const date = new Date(dateTime);
    const y = date.getFullYear();
    const m = ("0" + (date.getMonth() + 1)).slice(-2);
    const d = ("0" + date.getDate()).slice(-2);
    return `${y}.${m}.${d}`;
}

function deletePost(noticeId) {
    if (!confirm("정말 삭제하시겠습니까?")) return;

    fetch(`/api/notice/remove/${noticeId}`, {
        method: 'DELETE'
    })
        .then(res => {
            if (!res.ok) throw new Error("삭제 실패");
            return res.json();
        })
        .then(data => {
            alert(data.message); // "삭제되었습니다."
            window.location.href = "/notice/list";
        })
        .catch(err => {
            console.error("삭제 오류", err);
            alert("삭제 중 오류가 발생했습니다.");
        });
}

