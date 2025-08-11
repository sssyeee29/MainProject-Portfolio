/**
 * 환경톡톡 게시글 등록 스크립트
 * 게시글 등록 페이지에서 카테고리 로딩, 유효성 검사, 이미지 삽입, 미리보기 등
 * 다양한 작성 인터랙션 기능을 제공함
 *
 * @author : yukyeong
 * @fileName : env-register.js
 * @since : 250730
 * @history
 *   - 250730 | yukyeong | 게시글 작성 페이지 전용 스크립트 최초 작성
                          - 카테고리 동적 로딩(loadCategories)
                          - 제목/내용 글자 수 카운팅(setupCharacterCounters)
                          - 유효성 검사 및 등록 처리(submitPost)
                          - 이미지 첨부 시 <img> 태그 본문 삽입 처리
                          - 임시 이미지 업로드 API 연동(/api/env/upload-temp)
                          - 미리보기 모달 기능(previewPost)
*   - 250731 | yukyeong | Toast UI Editor 적용(editor.getHTML, getMarkdown 사용)
                          - 첨부파일 삽입 기능 제거 및 주석 처리
                          - 등록 처리 시 FormData에 JSON DTO만 전송
                          - 수정일 추가하고, 수정을 하면 작성일이 수정일로 변경되게 설정
 *   - 250801 | yukyeong | 본문 이미지 src 추출 기능 추가(getImageSrcListFromEditor)
 *                        - submitPost() 수정: contentImgUrls 필드로 본문 이미지 리스트 포함 전송
 *                        - 이미지 등록 시 DB 저장 누락되는 오류 수정 반영
 *   - 250811 | yukyeong | 본문 이미지 개수 제한 기능 추가
 *                         - MAX_CONTENT_IMAGES 상수 도입 (기본 5장)
 *                         - getImageSrcListFromEditor()로 추출 후 Set을 이용해 중복 제거
 *                         - submitPost()에서 개수 초과 시 등록 중단 처리
 */

document.addEventListener("DOMContentLoaded", function () {
    window.onbeforeunload = null; // 제출 성공 시 호출하면 창 닫아도 경고 안뜸
    localStorage.removeItem("draft"); // 임시글 제거

    loadCategories(); // 카테고리 옵션 추가
    setupCharacterCounters(); // 글자 수 표시
    setupFormSubmit(); // 유효성 검사 후 submit
    // setupImageInsert(); // 첨부파일 이미지 삽입
});

// 카테고리 로딩
function loadCategories() {
    const categories = [
        { id: "policy", name: "정책/제도" },
        { id: "news", name: "환경 뉴스" },
        { id: "issue", name: "환경 이슈" },
        { id: "tips", name: "생활 속 팁" }
    ];

    const select = document.getElementById("categorySelect");
    categories.forEach(category => {
        const option = document.createElement("option");
        option.value = category.id;
        option.textContent = category.name;
        select.appendChild(option);
    });
}

// 글자 수 카운터
function setupCharacterCounters() {
    const titleInput = document.getElementById("postTitle");
    titleInput.addEventListener("input", () => {
        document.getElementById("titleCount").textContent = titleInput.value.length;
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

    const title = document.getElementById("postTitle");
    const contentText = editor.getMarkdown().trim();
    const category = document.getElementById("categorySelect");

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
    const title = document.getElementById("postTitle").value.trim();
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

// // ✅ 이미지 첨부 시 <img src="...">
// function setupImageInsert() {
//     const fileInput = document.getElementById("fileInput");
//
//     fileInput.addEventListener("change", function () {
//         const files = fileInput.files;
//         const formData = new FormData();
//
//         for (let i = 0; i < files.length; i++) {
//             formData.append("imageFiles", files[i]); // ✅ 백엔드에서 받을 필드명에 맞춰야 함
//         }
//
//         // ✅ 업로드용 임시 API 호출 (등록용이 아님!)
//         fetch("/api/env/upload-temp", {
//             method: "POST",
//             body: formData
//         })
//             .then(res => res.json())
//             .then(fileNames => {
//                 fileNames.forEach(fileName => {
//                     const imageUrl = `/ecovery/env/${fileName}`;
//                     editor.insertText(`\n<img src="${imageUrl}" alt="첨부 이미지">\n`);
//                 });
//             })
//             .catch(err => {
//                 console.error("임시 이미지 업로드 실패:", err);
//                 alert("이미지 업로드 중 오류 발생");
//             });
//     });
// }

// HTML에서 <img> 태그의 src 속성만 뽑아 리스트로 반환
function getImageSrcListFromEditor() {
    const editorContent = editor.getHTML(); // 에디터 HTML 가져오기
    const parser = new DOMParser();
    const doc = parser.parseFromString(editorContent, "text/html");
    const imgTags = doc.querySelectorAll("img"); // 모든 <img> 태그 선택
    return Array.from(imgTags).map(img => img.getAttribute("src")); // src만 뽑기
}

// 게시글 등록 처리
function submitPost() {
    if (!validateForm()) return;

    const title = document.getElementById("postTitle").value.trim();
    const content = editor.getHTML();
    const categoryId = document.getElementById("categorySelect").value;


    // ✅ 본문 이미지 추출 + 중복 제거
    const contentImgUrls = [...new Set(getImageSrcListFromEditor())];

    // ✅ 제출 시 최종 개수 제한
    if (typeof MAX_CONTENT_IMAGES !== 'undefined' && contentImgUrls.length > MAX_CONTENT_IMAGES) {
        alert(`본문 이미지는 최대 ${MAX_CONTENT_IMAGES}장까지 가능합니다.`);
        return; // 등록 중단
    }

    window.onbeforeunload = null;

    const formData = new FormData();

    formData.append("envFormDto", new Blob([
        JSON.stringify({
            envDto: { title, content, category: categoryId },
            contentImgUrls: contentImgUrls // ✅ 본문 이미지 리스트 함께 보냄
        })
    ], { type: "application/json" }));

    // const fileInput = document.getElementById("fileInput");
    // if (fileInput.files.length > 0) {
    //     for (let i = 0; i < fileInput.files.length; i++) {
    //         formData.append("envImgFiles", fileInput.files[i]);
    //     }
    // } else {
    //     formData.append("envImgFiles", new Blob());
    // }

    fetch("/api/env/register", {
        method: "POST",
        body: formData
    })
        .then(response => {
            if (!response.ok) throw new Error("등록 실패");
            return response.json();
        })
        .then(data => {
            alert("게시글이 성공적으로 등록되었습니다.");
            window.location.href = "/env/list";
        })
        .catch(error => {
            console.error("등록 중 오류:", error);
            alert("게시글 등록에 실패했습니다.");
        });
}