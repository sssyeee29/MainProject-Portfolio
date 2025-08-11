/**
 * GreenCycle 환경톡톡 게시글 등록 스크립트
 * 게시글 등록 페이지에서 카테고리 로딩, 유효성 검사, 이미지 삽입, 미리보기 등
 * 다양한 작성 인터랙션 기능을 제공함
 *
 * @author : yukyeong
 * @fileName : notice-register.js
 * @since : 250801
 * @history
 *   - 250801 | yukyeong | 게시글 작성 페이지 전용 스크립트 최초 작성:
 *                         - Toast UI Editor 설정 (이미지 업로드 제거, 툴바 제한)
 *                         - 카테고리 목록 동적 렌더링(loadCategories)
 *                         - 제목/내용 실시간 글자 수 카운터 기능 추가
 *                         - 필수 항목 유효성 검사(validateForm)
 *                         - 미리보기 모달 기능 구현(previewPost)
 *                         - 발행 버튼 클릭 시 JSON 전송 처리(submitPost)
 *                         - 초기화 버튼 클릭 시 폼/에디터 초기화 처리 포함
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

    // ✅ 초기화 버튼 이벤트 바인딩
    const resetBtn = document.getElementById("resetBtn");
    if (resetBtn) {
        resetBtn.addEventListener("click", function () {
            if (confirm("작성 중인 내용을 모두 초기화하시겠습니까?")) {
                document.getElementById("writeForm").reset();        // 입력 필드 초기화
                editor.setHTML('');                                  // 에디터 내용 초기화
                document.getElementById("titleCounter").textContent = "0";
                document.getElementById("contentCount").textContent = "0";
            }
        });
    }
});

// 이미지 업로드 제거된 에디터 설정
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
}

// 카테고리 로딩
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
        select.appendChild(option);
    });
}

// 글자 수 카운터
function setupCharacterCounters() {
    const titleInput = document.getElementById("title");
    titleInput.addEventListener("input", () => {
        document.getElementById("titleCounter").textContent = titleInput.value.length;
    });

    // Toast UI Editor 기반으로 본문 글자 수 계산
    editor.on("change", () => {
        const contentLength = editor.getHTML().replace(/<[^>]*>/g, "").length;
        document.getElementById("contentCount").textContent = contentLength;
    });
}

// 유효성 검사
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

// 폼 제출 이벤트
function setupFormSubmit() {
    const form = document.getElementById("writeForm");
    form.addEventListener("submit", function (e) {
        e.preventDefault();

        // 에디터 내용 → hidden input에 복사
        document.getElementById("hiddenContent").value = editor.getHTML();

        submitPost();
    });
}

// 미리보기 모달
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


// 게시글 등록 처리
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

    fetch("/api/notice/register", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(noticeDto)
    })
        .then(response => {
            if (!response.ok) throw new Error("등록 실패");
            return response.json();
        })
        .then(data => {
            alert("게시글이 성공적으로 등록되었습니다.");
            window.location.href = "/notice/list";
        })
        .catch(error => {
            console.error("등록 중 오류:", error);
            alert("게시글 등록에 실패했습니다.");
        });
}
