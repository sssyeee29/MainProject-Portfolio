// ✅ 전역 변수로 분류 옵션을 저장할 변수 선언
let classificationOptionsByRegion = {};
// ✅ lastDisposalHistoryId를 전역 스코프에 선언
let lastDisposalHistoryId = null;


/**
 * 로딩 메시지 배열 (분석 중 순차적으로 표시)
 */
const LOADING_MESSAGES = [
    "이미지를 분석하고 있어요",
    "AI가 물체를 인식하고 있어요",
    "분류 카테고리를 결정하고 있어요",
    "신뢰도를 계산하고 있어요",
    "결과를 준비하고 있어요"
];

document.addEventListener("DOMContentLoaded", async() => {
    const imageInput = document.getElementById('imageInput');
    const uploadZone = document.getElementById('uploadZone');
    const uploadPreview = document.getElementById('uploadPreview');
    const previewImage = document.getElementById('previewImage');
    const resetBtn = document.getElementById('resetBtn');

    // 🔐 form 기본 제출 방지
    document.getElementById("uploadForm").addEventListener("submit", function (e) {
        e.preventDefault();
    });

    // 이미지 미리보기
    imageInput.addEventListener('change', function (event) {
        const file = event.target.files[0];
        if (!file) return;

        // 미리보기 표시
        uploadZone.style.display = 'none';
        uploadPreview.style.display = 'block';
        resetBtn.style.display = 'inline-block';


        const reader = new FileReader();
        reader.onload = function (e) {
            previewImage.src = e.target.result;
        };
        reader.readAsDataURL(file);
    });

    // 업로드 리셋
    window.resetUpload = function () {
        imageInput.value = '';
        uploadZone.style.display = 'block';
        uploadPreview.style.display = 'none';
        resetBtn.style.display = 'none';
    };

    // 드래그 앤 드롭
    uploadZone.addEventListener('dragover', function (e) {
        e.preventDefault();
        uploadZone.classList.add('dragover');
    });

    uploadZone.addEventListener('dragleave', function (e) {
        e.preventDefault();
        uploadZone.classList.remove('dragover');
    });

    uploadZone.addEventListener('drop', function (e) {
        e.preventDefault();
        uploadZone.classList.remove('dragover');
        const file = e.dataTransfer.files[0];
        if (file) {
            imageInput.files = e.dataTransfer.files;
            imageInput.dispatchEvent(new Event('change'));
        }
    });

    // 수수료 확인 버튼 클릭 (AI 서버 연동 예시)
    window.checkFeeInfo = async function () {
        const file = imageInput.files[0];
        const district = document.getElementById("districtSelect").value;
        const memberIdInput = document.querySelector("#memberId");

        if (!file || !district) {
            alert("이미지와 지역을 모두 선택해주세요.");
            return;
        }

        const formData = new FormData();
        formData.append("file", file);
        formData.append("regionGu", district);
        if (memberIdInput && memberIdInput.value && memberIdInput.value !== "undefined") {
            formData.append("memberId", memberIdInput.value);
            console.log("✅ memberId appended:", memberIdInput.value);
        } else {
            console.log("🚫 memberId 생략됨:", memberIdInput?.value);
        }

        for (let pair of formData.entries()) {
            console.log(pair[0] + ': ' + pair[1]);
        }

        try {
            const response = await fetch("/api/disposal/initial-request", {
                method: "POST",
                body: formData
            });

            if (!response.ok) throw new Error("AI 서버 응답 실패");

            const result = await response.json();
            console.log("AI 예측 결과:", result); // ✅ 추가
            lastDisposalHistoryId = result.disposalHistoryId; // ✅ 저장

            // 아래 값이 null 또는 undefined일 경우를 대비한 방어 로직
            if (!result.aiPrediction) {
                alert("AI 예측값이 없습니다.");
                return;
            }
            // 예시: 모달에 데이터 띄우기
            openClassificationModal(result.aiPrediction, result.regionGu);
        } catch (error) {
            console.error("AI 분석 오류:", error);
            alert("AI 분석 중 오류가 발생했습니다.");
            //window.location.href = "/disposalMain"; // 메인으로 이동
        }
    };

    // ✅ JSON 파일에서 분류 옵션 데이터를 불러옴
    try {
        const response = await fetch('/classificationOptionsByRegion.json'); // ✅ 경로 확인: js 파일과 json 파일이 같은 디렉토리에 있을 때
        if (!response.ok) {
            throw new Error(`HTTP 오류! 상태: ${response.status}`);
        }
        classificationOptionsByRegion = await response.json();
        console.log("분류 옵션 데이터 로드 완료:", classificationOptionsByRegion);
    } catch (error) {
        console.error('분류 옵션을 불러오는 중 오류 발생:', error);
        alert("폐기물 분류 데이터를 불러오는데 실패했습니다. 페이지를 새로고침 해주세요.");
    }

    function openClassificationModal(category, district) {
        const modal = document.getElementById("classificationModal");
        const optionsContainer = document.getElementById("classificationOptions");

        // ✅ 제목에 AI 예측 결과 표시
        document.getElementById("aiPredictionTitle").innerText =
            `AI 예측 결과: ${category}`;

        // 기존 옵션 초기화
        optionsContainer.innerHTML = "";

        const regionOptions = classificationOptionsByRegion[district] || {};
        const options = regionOptions[category] || [];

        options.forEach(item => {
            const label = document.createElement("label");
            label.style.display = "block";

            const input = document.createElement("input");
            input.type = "radio";
            input.name = "finalItem";
            input.value = item;

            label.appendChild(input);
            label.append(` ${item}`);

            optionsContainer.appendChild(label);
        });

        modal.classList.add("show"); // display: flex로 가운데 정렬됨
    }

    window.confirmAlert = async function () {
        const selectedCategory = document.querySelector("input[name='finalItem']:checked")?.value;

        if (!selectedCategory) {
            alert("분류를 선택해주세요.");
            return;
        }

        if (!lastDisposalHistoryId) {
            alert("처리 이력이 없습니다. 다시 시도해주세요.");
            return;
        }

        try {
            const response = await fetch("/api/disposal/finalize-request", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    disposalHistoryId: lastDisposalHistoryId,
                    selectedFinalItem: selectedCategory // ✅ DTO의 필드명과 정확히 일치
                })
            });

            if (!response.ok) throw new Error("최종 분류 저장 실패");

            const { disposalHistoryId } = await response.json();
            window.location.href = `/disposal/${disposalHistoryId}`;
        } catch (error) {
            console.error("최종 분류 저장 오류:", error);
            alert("저장 중 오류가 발생했습니다.");
        }
    };



    window.closeAlert = function () {
        document.getElementById("classificationModal").classList.remove("show"); // 다시 숨김
    };
});