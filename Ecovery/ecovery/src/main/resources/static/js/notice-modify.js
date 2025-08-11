/**
 * 공지사항 게시글 수정 스크립트
 * 게시글 수정 페이지에서 에디터 초기화, 기존 데이터 바인딩, 유효성 검사, 미리보기 등
 * 다양한 편집 인터랙션 기능을 제공함
 *
 * @author : yukyeong
 * @fileName : notice-modify.js
 * @since : 250802
 * @history
 *   - 250802 | yukyeong | 공지사항 수정 페이지 전용 스크립트 최초 작성:
 *                        - Toast UI Editor 초기화 및 기존 content 바인딩
 *                        - 카테고리 옵션 렌더링 및 기존 선택값 유지
 *                        - 제목/내용 글자 수 실시간 카운트 기능 적용
 *                        - 유효성 검사 및 PUT API 연동 처리
 *                        - 미리보기 모달 구현 및 초기화 버튼 동작 추가
 */

document.addEventListener("DOMContentLoaded", function () {
    window.onbeforeunload = null; // 제출 성공 시 호출하면 창 닫아도 경고 안뜸
    localStorage.removeItem("draft"); // 임시글 제거

    setupEditor(); // 에디터 초기화
    loadCategories(); // 카테고리 옵션 추가
    setupCharacterCounters(); // 글자 수 표시
    setupFormSubmit(); // 유효성 검사 후 submit

    // ✅ 미리보기 버튼 이벤트 바인딩
    const previewBtn = document.getElementById("previewBtn");
    if (previewBtn) {
        previewBtn.addEventListener("click", previewPost);
    }

    // ✅ 초기화
    const resetBtn = document.getElementById("resetBtn");
    if (resetBtn) {
        resetBtn.addEventListener("click", function () {
            if (confirm("작성 중인 내용을 모두 초기화하시겠습니까?")) {
                document.getElementById("modifyForm").reset();
                editor.setHTML(initialContent || "");
                document.getElementById("titleCounter").textContent = document.getElementById("title").value.length;
                document.getElementById("contentCount").textContent = editor.getHTML().replace(/<[^>]*>/g, "").length;
                document.getElementById("category").value = noticeCategory || "";
            }
        });
    }
});

// ✅ Toast UI Editor 초기화 (수정용)
function setupEditor() {
    window.editor = new toastui.Editor({
        el: document.querySelector('#editor'),
        height: '500px',
        initialEditType: 'wysiwyg',
        previewStyle: 'vertical',
        toolbarItems: [
            ['heading', 'bold', 'italic', 'strike'],
            ['ul', 'ol', 'hr'],
            ['link'],
            ['code', 'codeblock']
        ]
    });

    // 초기 내용 세팅
    if (window.initialContent) {
        editor.setHTML(window.initialContent);
    }
}

// ✅ 카테고리 옵션 렌더링 + 기존 선택값 유지
function loadCategories() {
    const categories = [
        { id: "important", name: "중요 공지" },
        { id: "service", name: "서비스" },
        { id: "maintenance", name: "점검" },
        { id: "event", name: "이벤트" },
        { id: "general", name: "일반" }
    ];

    const select = document.getElementById("category");
    categories.forEach(category => {
        const option = document.createElement("option");
        option.value = category.id;
        option.textContent = category.name;
        if (category.id === window.noticeCategory) {
            option.selected = true;
        }
        select.appendChild(option);
    });
}

// ✅ 글자 수 카운터
function setupCharacterCounters() {
    const titleInput = document.getElementById("title");
    document.getElementById("titleCounter").textContent = titleInput.value.length;

    titleInput.addEventListener("input", () => {
        document.getElementById("titleCounter").textContent = titleInput.value.length;
    });

    document.getElementById("contentCount").textContent =
        editor.getHTML().replace(/<[^>]*>/g, "").length;

    editor.on("change", () => {
        const contentLength = editor.getHTML().replace(/<[^>]*>/g, "").length;
        document.getElementById("contentCount").textContent = contentLength;
    });
}

// ✅ 유효성 검사
function validateForm() {
    let isValid = true;

    const title = document.getElementById("title");
    const contentHtml = editor.getHTML();
    const contentText = contentHtml.replace(/<[^>]*>/g, '').replace(/&nbsp;/g, '').trim();
    const category = document.getElementById("category");

    document.getElementById("titleError").textContent = "";
    document.getElementById("contentError").textContent = "";
    document.getElementById("categoryError").textContent = "";

    if (title.value.trim() === "") {
        document.getElementById("titleError").textContent = "제목을 입력해주세요.";
        isValid = false;
    }
    if (contentText === "") {
        document.getElementById("contentError").textContent = "내용을 입력해주세요.";
        isValid = false;
    }
    if (!category.value) {
        document.getElementById("categoryError").textContent = "카테고리를 선택해주세요.";
        isValid = false;
    }

    return isValid;
}

// ✅ 폼 제출 이벤트 처리
function setupFormSubmit() {
    const form = document.getElementById("modifyForm");
    form.addEventListener("submit", function (e) {
        e.preventDefault();
        document.getElementById("hiddenContent").value = editor.getHTML();
        submitPost();
    });
}

// ✅ 미리보기 모달
function previewPost() {
    const title = document.getElementById("title").value.trim();
    const content = editor.getHTML();

    if (!title || content.replace(/<[^>]*>/g, "").trim() === "") {
        alert("미리보기를 위해 제목과 내용을 입력해주세요.");
        return;
    }

    const previewHtml = `
        <h2>${title}</h2>
        ${content}
    `;

    document.getElementById("previewContent").innerHTML = previewHtml;
    document.getElementById("previewModal").style.display = "block";
}

function closePreview() {
    document.getElementById("previewModal").style.display = "none";
}


// ✅ 게시글 수정 처리
function submitPost() {
    if (!validateForm()) return;

    const title = document.getElementById("title").value.trim();
    const content = editor.getHTML();
    const categoryId = document.getElementById("category").value;

    window.onbeforeunload = null;

    const noticeDto = {
        title,
        content,
        category: categoryId
    };

    fetch(`/api/notice/modify/${window.noticeId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(noticeDto)
    })
        .then(response => {
            if (!response.ok) throw new Error("수정 실패");
            return response.json();
        })
        .then(data => {
            alert("게시글이 성공적으로 수정되었습니다.");
            window.location.href = "/notice/list";
        })
        .catch(error => {
            console.error("수정 중 오류:", error);
            alert("게시글 수정에 실패했습니다.");
        });
}
