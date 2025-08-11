// =========================
// 전역 변수 (페이지 전체에서 사용)
// =========================

// 지역 데이터 (구를 선택하면 동이 나타남)
const regionData = {
    "강남구": [
        "개포동", "논현동", "대치동", "도곡동", "삼성동", "세곡동", "신사동", "압구정동",
        "역삼동", "율현동", "일원동", "자동차운전면허시험장", "청담동"
    ],
    "강동구": [
        "강일동", "고덕동", "길동", "둔촌동", "명일동", "상일동", "성내동", "암사동", "천호동"
    ],
    "강북구": [
        "미아동", "번동", "수유동", "우이동"
    ],
    "강서구": [
        "가양동", "강서동", "개화동", "공항동", "과해동", "내발산동", "등촌동", "마곡동",
        "방화동", "염창동", "오곡동", "오쇠동", "외발산동", "화곡동"
    ],
    "관악구": [
        "남현동", "봉천동", "신림동"
    ],
    "광진구": [
        "광장동", "구의동", "군자동", "능동", "자양동", "중곡동", "화양동"
    ],
    "구로구": [
        "가리봉동", "개봉동", "고척동", "구로동", "궁동", "신도림동", "오류동", "온수동", "천왕동"
    ],
    "금천구": [
        "가산동", "독산동", "시흥동"
    ],
    "노원구": [
        "공릉동", "상계동", "월계동", "중계동", "하계동"
    ],
    "도봉구": [
        "도봉동", "방학동", "쌍문동", "창동"
    ],
    "동대문구": [
        "답십리동", "동답동", "장안동", "전농동", "제기동", "청량리동", "회기동", "휘경동", "이문동", "용두동"
    ],
    "동작구": [
        "노량진동", "본동", "사당동", "상도동", "신대방동", "흑석동"
    ],
    "마포구": [
        "공덕동", "구수동", "노고산동", "당인동", "대흥동", "도화동", "동교동", "마포동", "망원동", "상수동",
        "상암동", "서교동", "성산동", "신공덕동", "신수동", "아현동", "연남동", "염리동", "오른쪽여의도", "용강동", "중동", "창전동", "토정동", "합정동"
    ],
    "서대문구": [
        "남가좌동", "북가좌동", "북아현동", "신촌동", "연희동", "천연동", "충정로3가", "홍은동", "홍제동"
    ],
    "서초구": [
        "내곡동", "반포동", "반포본동", "방배동", "서초동", "신원동", "양재동", "염곡동", "우면동", "원지동", "잠원동"
    ],
    "성동구": [
        "금호동1가", "금호동2가", "금호동3가", "금호동4가", "도선동", "마장동", "사근동", "상왕십리동", "성수동1가",
        "성수동2가", "송정동", "옥수동", "응봉동", "하왕십리동", "행당동"
    ],
    "성북구": [
        "길음동", "돈암동", "동선동1가", "동선동2가", "동선동3가", "동소문동1가", "동소문동2가", "동소문동3가", "동소문동4가",
        "동소문동5가", "동소문동6가", "동소문동7가", "보문동1가", "보문동2가", "보문동3가", "보문동4가", "보문동5가", "보문동6가",
        "삼선동1가", "삼선동2가", "삼선동3가", "삼선동4가", "삼선동5가", "상월곡동", "성북동", "성북동1가", "성북동2가", "안암동1가",
        "안암동2가", "안암동3가", "안암동4가", "안암동5가", "장위동", "정릉동", "종암동", "하월곡동"
    ],
    "송파구": [
        "가락동", "거여동", "마천동", "문정동", "방이동", "삼전동", "석촌동", "송파동", "신천동", "오금동", "잠실동", "장지동", "풍납동"
    ],
    "양천구": [
        "목동", "신월동", "신정동"
    ],
    "영등포구": [
        "당산동1가", "당산동2가", "당산동3가", "당산동4가", "당산동5가", "당산동6가", "대림동", "도림동", "문래동1가",
        "문래동2가", "문래동3가", "문래동4가", "문래동5가", "문래동6가", "신길동", "양평동1가", "양평동2가", "양평동3가",
        "양평동4가", "양평동5가", "양평동6가", "여의도동", "영등포동", "영등포동1가", "영등포동2가", "영등포동3가",
        "영등포동4가", "영등포동5가", "영등포동6가"
    ],
    "용산구": [
        "갈월동", "남영동", "동빙고동", "동자동", "문배동", "보광동", "산천동", "서계동", "서빙고동", "신계동", "용문동", "용산동2가",
        "용산동3가", "용산동5가", "원효로1가", "원효로2가", "원효로3가", "원효로4가", "이촌동", "이태원동", "주성동", "청암동", "한강로1가",
        "한강로2가", "한강로3가", "한남동", "후암동"
    ],
    "은평구": [
        "갈현동", "구산동", "대조동", "불광동", "수색동", "신사동", "역촌동", "응암동", "증산동", "진관동"
    ],
    "종로구": [
        "가회동", "견지동", "경운동", "계동", "공평동", "관훈동", "교남동", "교북동", "구기동", "궁정동", "권농동", "낙원동",
        "내수동", "내자동", "누상동", "누하동", "당주동", "도렴동", "돈의동", "동숭동", "명륜1가", "명륜2가", "명륜3가",
        "명륜4가", "묘동", "봉익동", "부암동", "사간동", "사직동", "삼청동", "서린동", "세종로", "소격동", "송현동", "수송동",
        "숭인동", "신교동", "신문로1가", "신문로2가", "신영동", "안국동", "연건동", "연지동", "예지동", "옥인동", "와룡동",
        "운니동", "원남동", "원서동", "익선동", "인사동", "인의동", "장사동", "재동", "적선동", "종로1가", "종로2가",
        "종로3가", "종로4가", "종로5가", "종로6가", "중학동", "창성동", "창신동", "청진동", "청운동", "체부동", "충신동",
        "평동", "필운동", "홍지동", "홍파동", "화동", "효자동", "효제동", "훈정동"
    ],
    "중구": [
        "광희동1가", "광희동2가", "남대문로1가", "남대문로2가", "남대문로3가", "남대문로4가", "남산동1가", "남산동2가", "남산동3가",
        "남창동", "남학동", "다동", "만리동1가", "만리동2가", "명동1가", "명동2가", "무교동", "묵정동", "방산동", "봉래동1가",
        "봉래동2가", "북창동", "산림동", "삼각동", "서소문동", "소공동", "수표동", "순화동", "신당동", "쌍림동", "예관동",
        "예장동", "오장동", "을지로1가", "을지로2가", "을지로3가", "을지로4가", "을지로5가", "을지로6가", "을지로7가",
        "의주로1가", "의주로2가", "인현동1가", "인현동2가", "장교동", "장충동1가", "장충동2가", "저동1가", "저동2가",
        "정동", "주교동", "주자동", "중림동", "진관동", "초동", "충무로1가", "충무로2가", "충무로3가", "충무로4가",
        "충무로5가", "태평로1가", "태평로2가", "필동1가", "필동2가", "필동3가", "황학동", "회현동1가", "회현동2가", "회현동3가"
    ],
    "중랑구": [
        "망우동", "면목동", "묵동", "상봉동", "신내동", "중화동"
    ]
};

// 이미지 관리용 전역 변수
let uploadedImages = [];          // 새로 업로드된 이미지들 (file, src, id 포함)
let deletedImageIds = [];         // 삭제 표시한 기존 이미지 ID들
let existingImages = [];          // 서버에서 받아온 기존 이미지 정보

// =========================
// 페이지가 로드되면 실행되는 함수
// =========================
document.addEventListener('DOMContentLoaded', function() {
    console.log('무료나눔 수정 페이지가 로드되었습니다!');

    if (typeof window.free === 'undefined' || window.free === null) {
        console.error('오류: Thymeleaf에서 free 객체가 JavaScript로 전달되지 않았습니다. window.free를 확인해주세요.');
    }

    initializeFormData();
    setupEventListeners();

    setTimeout(function() {
        const formContainer = document.querySelector('.form-container');
        if (formContainer) {
            formContainer.classList.add('fade-in');
        }
    }, 200);
});

// =========================
// 폼 데이터 초기화 함수 (수정 페이지용)
// =========================
function initializeFormData() {
    const region1Select = document.getElementById('region1');
    const conditionSelect = document.getElementById('condition');
    const categorySelect = document.getElementById('category');

    const freeData = window.free;
    console.log('window.free 값 확인:', window.free);

    if (region1Select && freeData.regionGu) {
        region1Select.value = freeData.regionGu;
        populateRegion2(freeData.regionGu, freeData.regionDong);
    }

    if (conditionSelect && freeData.itemCondition) {
        conditionSelect.value = freeData.itemCondition;
        handleConditionChange();
    }

    if (categorySelect && freeData.category) {
        categorySelect.value = freeData.category;
        handleCategoryChange();
    }

    // 기존 이미지 정보 초기화
    if (freeData.imgList && freeData.imgList.length > 0) {
        existingImages = freeData.imgList.map(img => ({
            id: img.freeImgId,
            imgUrl: img.imgUrl,
            imgName: img.imgName,
            oriImgName: img.oriImgName,
            repImgYn: img.repImgYn
        }));
    }

    renderAllImages();
}

// =========================
// 이벤트 리스너 설정 함수
// =========================
function setupEventListeners() {
    const region1Select = document.getElementById('region1');
    if (region1Select) {
        region1Select.addEventListener('change', (event) => populateRegion2(event.target.value));
    }

    const imageUploadArea = document.getElementById('imageUploadArea');
    const imageInput = document.getElementById('imageInput');

    if (imageUploadArea && imageInput) {
        imageUploadArea.addEventListener('click', function () {
            imageInput.click();
        });

        imageInput.addEventListener('change', handleImageSelect);
        setupDragAndDrop(imageUploadArea);
    }

    const form = document.getElementById('modifyForm');
    if (form) {
        form.addEventListener('submit', handleFormSubmit);
    }

    setupRealtimeValidation();

    const categorySelect = document.getElementById('category');
    if (categorySelect) {
        categorySelect.addEventListener('change', handleCategoryChange);
    }

    const conditionSelect = document.getElementById('condition');
    if (conditionSelect) {
        conditionSelect.addEventListener('change', handleConditionChange);
    }

    const titleInput = document.getElementById('title');
    if (titleInput) {
        titleInput.addEventListener('input', function () {
            limitCharacters(this, 50, '제목');
        });
    }

    const descriptionInput = document.getElementById('description');
    if (descriptionInput) {
        descriptionInput.addEventListener('input', function () {
            limitCharacters(this, 1000, '설명');
            updateCharacterCounter(this, 1000);
        });
    }
}

// =========================
// 지역 선택 관련 함수
// =========================
function populateRegion2(selectedGu, selectedDong = null) {
    const region2Select = document.getElementById('region2');

    if (!region2Select) return;
    region2Select.innerHTML = '<option value="">동</option>';

    if (selectedGu && regionData[selectedGu]) {
        regionData[selectedGu].forEach(function(dong) {
            const option = document.createElement('option');
            option.value = dong;
            option.textContent = dong;
            region2Select.appendChild(option);
        });

        if (selectedDong) {
            region2Select.value = selectedDong;
        }
    }
}

// =========================
// 이미지 업로드 관련 함수
// =========================
function handleImageSelect(event) {
    const files = Array.from(event.target.files);

    // 파일 선택 후, 필드를 초기화하여 같은 파일 재선택 시에도 이벤트가 발생하도록 함
    event.target.value = '';

    const imagesToKeepCount = existingImages.filter(img => !deletedImageIds.includes(String(img.id))).length;
    const totalImages = imagesToKeepCount + uploadedImages.length;

    // 최대 업로드 가능한 이미지 수 체크
    if (totalImages + files.length > 5) {
        showNotification('사진은 최대 5개까지 업로드 가능합니다.', 'error');
        return;
    }

    handleImageFiles(files);
}

function setupDragAndDrop(uploadArea) {
    uploadArea.addEventListener('dragover', function(event) {
        event.preventDefault();
        uploadArea.style.borderColor = 'var(--primary-green)';
        uploadArea.style.background = 'rgba(45, 90, 61, 0.1)';
    });

    uploadArea.addEventListener('dragleave', function() {
        uploadArea.style.borderColor = 'var(--accent-green)';
        uploadArea.style.background = 'rgba(111, 167, 118, 0.05)';
    });

    uploadArea.addEventListener('drop', function(event) {
        event.preventDefault();
        uploadArea.style.borderColor = 'var(--accent-green)';
        uploadArea.style.background = 'rgba(111, 167, 118, 0.05)';
        const files = event.dataTransfer.files;

        const imagesToKeepCount = existingImages.filter(img => !deletedImageIds.includes(String(img.id))).length;
        const totalImages = imagesToKeepCount + uploadedImages.length;

        if (totalImages + files.length > 5) {
            showNotification('사진은 최대 5개까지 업로드 가능합니다.', 'error');
            return;
        }

        handleImageFiles(Array.from(files));
    });
}

function handleImageFiles(files) {
    const validImages = files.filter(file => file.type.startsWith('image/'));

    validImages.forEach(function(file) {
        if (file.size > 10 * 1024 * 1024) {
            showNotification(file.name + '은 10MB를 초과합니다.', 'error');
            return;
        }

        // 중복 파일 체크 로직 개선
        const isDuplicate = uploadedImages.some(img =>
            img.file.name === file.name && img.file.lastModified === file.lastModified
        );
        if (isDuplicate) {
            showNotification(file.name + '은 이미 추가된 파일입니다.', 'warning');
            return;
        }

        const reader = new FileReader();
        reader.onload = function (event) {
            const imageData = {
                file: file,
                src: event.target.result,
                id: Date.now() + Math.random().toString(36).substring(2, 9)
            };

            uploadedImages.push(imageData);
            renderAllImages();
        };
        reader.readAsDataURL(file);
    });
}

// =========================
// 이미지 미리보기 및 삭제 처리
// =========================
function renderAllImages() {
    const previewContainer = document.getElementById('imagePreview');
    if (!previewContainer) return;

    previewContainer.innerHTML = '';

    // 기존 이미지 렌더링
    existingImages.forEach(img => {
        // deletedImageIds에 포함되지 않은 이미지만 그립니다.
        const isDeleted = deletedImageIds.includes(String(img.id));
        if (!isDeleted) {
            const div = document.createElement('div');
            div.className = 'preview-item existing-image-item';
            div.dataset.id = img.id;

            div.innerHTML = `
                <img src="${img.imgUrl}" alt="등록된 이미지" class="preview-image" style="width:100px; height:auto;">
                <button type="button" class="btn-delete-existing remove-image" data-id="${img.id}">
                    ×
                </button>
            `;
            previewContainer.appendChild(div);
        }
    });

    // 새로 업로드된 이미지 렌더링
    uploadedImages.forEach(imageData => {
        const div = document.createElement('div');
        div.className = 'preview-item new-image-item';
        div.dataset.id = imageData.id;

        div.innerHTML = `
            <img src="${imageData.src}" alt="미리보기" class="preview-image" />
            <button type="button" class="remove-image" onclick="removeImage('${imageData.id}')">×</button>
        `;

        previewContainer.appendChild(div);
    });

    // 기존 이미지 삭제 버튼 이벤트 등록
    previewContainer.querySelectorAll('.btn-delete-existing').forEach(btn => {
        btn.addEventListener('click', () => {
            const id = btn.getAttribute('data-id');
            deletedImageIds.push(String(id));
            console.log('deletedImageIds:', deletedImageIds);

            // UI를 다시 렌더링하여 삭제 상태를 반영합니다.
            renderAllImages();
        });
    });
}

function removeImage(imageId) {
    uploadedImages = uploadedImages.filter(img => String(img.id) !== String(imageId));
    renderAllImages();
}

// =========================
// 폼 유효성 검사 함수
// =========================
function setupRealtimeValidation() {
    const inputs = document.querySelectorAll('.form-input, .form-select, .form-textarea');

    inputs.forEach(function(input) {
        input.addEventListener('blur', function() {
            validateField(input);
        });

        input.addEventListener('input', function() {
            if (!input.classList.contains('error-message')) {
                if (input.classList.contains('error') && input.value.trim()) {
                    clearFieldError(input);
                    input.classList.add('success');
                } else if (!input.value.trim() && input.hasAttribute('required')) {
                    showFieldError(input, '필수 입력 항목입니다.');
                } else {
                    clearFieldError(input);
                    if (input.value.trim()) {
                        input.classList.add('success');
                    } else {
                        input.classList.remove('success');
                    }
                }
            }
        });
    });
}

function validateField(field) {
    const value = field.value.trim();
    const isRequired = field.hasAttribute('required');

    if (isRequired && !value) {
        showFieldError(field, '필수 입력 항목입니다.');
        return false;
    } else {
        clearFieldError(field);
        if (value) {
            field.classList.add('success');
        } else {
            field.classList.remove('success');
        }
        return true;
    }
}

function showFieldError(field, message) {
    field.classList.add('error');
    field.classList.remove('success');

    const parent = field.parentNode;
    let existingError = parent.querySelector('.error-message');
    if (existingError && !existingError.hasAttribute('th:errors')) {
        existingError.remove();
    }

    const errorDiv = document.createElement('div');
    errorDiv.className = 'error-message';
    errorDiv.textContent = message;

    const thymeleafError = parent.querySelector('[th\\:errors]');
    if (thymeleafError) {
        parent.insertBefore(errorDiv, thymeleafError.nextSibling);
    } else {
        parent.appendChild(errorDiv);
    }
}

function clearFieldError(field) {
    field.classList.remove('error');

    const parent = field.parentNode;
    const errorMessages = parent.querySelectorAll('.error-message');
    errorMessages.forEach(msg => {
        if (!msg.hasAttribute('th:errors')) {
            msg.remove();
        }
    });
}

function validateForm() {
    let isValid = true;
    const requiredFields = ['title', 'condition', 'region1', 'region2', 'category', 'description'];

    requiredFields.forEach(function(fieldId) {
        const field = document.getElementById(fieldId);
        if (field && !validateField(field)) {
            isValid = false;
        }
    });

    // 이미지 개수 검사
    const imagesToKeep = existingImages.filter(img => !deletedImageIds.includes(String(img.id))).length;
    const totalImages = imagesToKeep + uploadedImages.length;

    if (totalImages === 0) {
        const imageUploadArea = document.getElementById('imageUploadArea');
        showFieldError(imageUploadArea, '사진은 최소 1개 이상 업로드해야 합니다.');
        isValid = false;
    } else {
        clearFieldError(document.getElementById('imageUploadArea'));
    }

    return isValid;
}

// =========================
// 폼 제출 처리 함수 (게시글 수정)
// =========================
async function handleFormSubmit(event) {
    event.preventDefault();

    console.log('폼 수정 제출 시도');

    if (!validateForm()) {
        showNotification('필수 입력 항목을 모두 작성하고, 사진을 1개 이상 첨부해주세요.', 'error');
        return;
    }

    const submitBtn = document.querySelector('.btn-submit');
    const originalText = submitBtn.textContent;
    submitBtn.textContent = '수정 중...';
    submitBtn.disabled = true;

    const freeId = document.querySelector('input[name="freeId"]').value;

    // 남겨둘 기존 이미지들에 대표 이미지 설정
    const imagesToKeep = existingImages.filter(img => !deletedImageIds.includes(String(img.id)));
    imagesToKeep.forEach((img, idx) => {
        img.repImgYn = (idx === 0) ? 'Y' : 'N';
    });

    // 새로 업로드된 이미지가 있으면 첫 번째 이미지를 대표 이미지로 설정
    if (imagesToKeep.length === 0 && uploadedImages.length > 0) {
        uploadedImages[0].repImgYn = 'Y';
    } else if (imagesToKeep.length > 0) {
        // 기존 이미지를 대표 이미지로 유지
    } else if (uploadedImages.length > 0) {
        // 새로 업로드된 이미지 중 첫 번째를 대표 이미지로
        uploadedImages[0].repImgYn = 'Y';
    }


    const freeDto = {
        freeId: Number(freeId),
        title: document.getElementById('title').value,
        content: document.getElementById('description').value,
        category: document.getElementById('category').value,
        regionGu: document.getElementById('region1').value,
        regionDong: document.getElementById('region2').value,
        itemCondition: document.getElementById('condition').value,
        nickname: document.getElementById('author').value,
        dealStatus: document.getElementById('dealStatus').value,

        imgList: imagesToKeep.map(img => ({
            freeImgId: img.id,
            imgUrl: img.imgUrl,
            imgName: img.imgName,
            oriImgName: img.oriImgName,
            repImgYn: img.repImgYn
        }))
    };

    const formData = new FormData();
    formData.append("freeDto", new Blob([JSON.stringify(freeDto)], { type: "application/json" }));

    // 새로 업로드된 이미지 파일들만 추가
    uploadedImages.forEach((imageData, index) => {
        // 대표 이미지 여부 설정 (첫 번째 파일만 Y)
        const isRep = (imagesToKeep.length === 0 && index === 0) ? 'Y' : 'N';
        formData.append("imgFile", imageData.file, imageData.file.name);
        formData.append("repImgYn", isRep);
    });

    // 삭제된 기존 이미지 ID들 추가
    deletedImageIds.forEach(id => {
        formData.append("deletedImageIds", id);
    });

    try {
        const response = await fetch(`/api/free/modify/${freeId}`, {
            method: 'POST',
            body: formData
        });

        const contentType = response.headers.get("content-type");
        let responseData;

        if (contentType?.includes("application/json")) {
            responseData = await response.json();
        } else {
            responseData = await response.text();
        }

        if (response.ok) {
            showNotification('나눔 정보가 성공적으로 수정되었습니다! 🎉', 'success');

            setTimeout(() => {
                if (confirm('수정된 나눔 게시글을 확인하시겠습니까?')) {
                    window.location.href = `/free/get/${freeId}`;
                } else {
                    window.location.href = '/free/list';
                }
            }, 500);
        } else {
            let errorMessage = '수정 중 오류가 발생했습니다.';
            if (typeof responseData === 'object' && responseData.message) {
                errorMessage = responseData.message;
            } else if (typeof responseData === 'string' && responseData.trim() !== '') {
                errorMessage = responseData;
            }

            showNotification(errorMessage, 'error');
            console.error('수정 실패 응답:', responseData);
        }
    } catch (error) {
        console.error('수정 요청 오류:', error);
        showNotification('네트워크 오류 또는 서버 통신 중 문제가 발생했습니다.', 'error');
    } finally {
        submitBtn.textContent = originalText;
        submitBtn.disabled = false;
    }
}

// =========================
// 상품 상태 관련 함수
// =========================
function handleConditionChange() {
    const conditionSelect = document.getElementById('condition');
    const conditionPreview = document.getElementById('conditionPreview');

    if (!conditionSelect || !conditionPreview) return;

    const selectedCondition = conditionSelect.value;

    conditionPreview.className = 'condition-preview';
    conditionPreview.style.display = 'none';

    if (selectedCondition) {
        let previewText = '';
        let previewClass = '';
        let stars = '';

        switch(selectedCondition) {
            case 'HIGH':
                previewText = '매우 좋음 - 거의 사용하지 않아 새 제품과 같은 상태';
                stars = '⭐⭐⭐⭐⭐';
                previewClass = 'good';
                break;
            case 'MEDIUM':
                previewText = '보통 - 일반적인 사용감이 있지만 기능상 문제없음';
                stars = '⭐⭐⭐';
                previewClass = 'fair';
                break;
            case 'LOW':
                previewText = '사용감 있음 - 오래 사용하여 흔적이 있지만 사용 가능';
                stars = '⭐⭐';
                previewClass = 'poor';
                break;
        }

        conditionPreview.innerHTML = `
            <div style="padding: 10px; background: #f5f5f5; border-radius: 5px; margin-top: 10px;">
                <span style="font-size: 16px;">${stars}</span>
                <span style="margin-left: 10px; color: #666;">${previewText}</span>
            </div>
        `;
        conditionPreview.style.display = 'block';
        conditionPreview.classList.add(previewClass);

        setTimeout(function() {
            conditionPreview.style.opacity = '1';
            conditionPreview.style.transform = 'translateY(0)';
        }, 100);
    }
}

function handleCategoryChange() {
    const categorySelect = document.getElementById('category');
    const descriptionInput = document.getElementById('description');

    if (!categorySelect || !descriptionInput) return;

    const category = categorySelect.value;

    const helpTexts = {
        '가구': '가구는 크기와 무게를 미리 안내해주세요.',
        '가전': '정상 작동 여부와 구매 시기를 명시해주세요.',
        '잡화': '재질, 브랜드, 사용 빈도 등을 명시해주세요.',
        '기타': '물품의 종류를 구체적으로 설명해주세요.'
    };

    const defaultPlaceholder = "물품에 대한 자세한 설명을 적어주세요";

    if (descriptionInput.value.trim() === '') {
        if (helpTexts[category]) {
            descriptionInput.placeholder = `${defaultPlaceholder}\n\n💡 ${helpTexts[category]}\n\n예시:\n- 사용 기간\n- 구매 시기\n- 특이사항 등`;
        } else {
            descriptionInput.placeholder = defaultPlaceholder;
        }
    }
}

// =========================
// 글자수 제한 관련 함수
// =========================
function limitCharacters(input, maxLength, fieldName) {
    if (input.value.length > maxLength) {
        input.value = input.value.substring(0, maxLength);
        showNotification(`${fieldName}은 ${maxLength}자까지 입력 가능합니다.`, 'error');
    }
}

function updateCharacterCounter(input, maxLength) {
    const currentLength = input.value.length;

    let counter = input.parentNode.querySelector('.char-counter');
    if (!counter) {
        counter = document.createElement('div');
        counter.className = 'char-counter';
        counter.style.cssText = 'font-size: 12px; color: var(--medium-gray); text-align: right; margin-top: 5px;';
        input.parentNode.appendChild(counter);
    }

    counter.textContent = `${Math.min(currentLength, maxLength)}/${maxLength}`;

    if (currentLength > maxLength * 0.9) {
        counter.style.color = 'var(--error-red)';
    } else {
        counter.style.color = 'var(--medium-gray)';
    }
}

// =========================
// 알림 메시지 관련 함수
// =========================
function showNotification(message, type = 'success') {
    const existingNotification = document.querySelector('.notification');
    if (existingNotification) {
        existingNotification.remove();
    }

    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;
    document.body.appendChild(notification);

    setTimeout(function() {
        notification.classList.add('show');
    }, 100);

    setTimeout(function() {
        notification.classList.remove('show');
        setTimeout(function() {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 300);
    }, 3000);
}

// =========================
// 기타 유틸리티 함수
// =========================
function goBack() {
    const formChanged = checkFormChanged();

    if (formChanged) {
        if (confirm('수정 중인 내용이 저장되지 않을 수 있습니다. 정말로 나가시겠습니까?')) {
            window.history.back();
        }
    } else {
        window.history.back();
    }
}

function checkFormChanged() {
    const inputs = document.querySelectorAll('.form-input, .form-select, .form-textarea');
    let hasChanged = false;

    inputs.forEach(function(input) {
        if (input.id !== 'author' && input.id !== 'regDate' && input.value.trim()) {
            hasChanged = true;
        }
    });

    if (uploadedImages.length > 0) {
        hasChanged = true;
    }

    if (deletedImageIds.length > 0) {
        hasChanged = true;
    }

    return hasChanged;
}

// =========================
// 키보드 단축키
// =========================
document.addEventListener('keydown', function(event) {
    if (event.ctrlKey && event.key === 's') {
        event.preventDefault();
        const form = document.getElementById('modifyForm');
        if (form) {
            form.dispatchEvent(new Event('submit'));
        }
    }

    if (event.key === 'Escape') {
        goBack();
    }
});

// // =========================
// // 페이지 이탈 경고
// // =========================
// window.addEventListener('beforeunload', function(event) {
//     if (checkFormChanged()) {
//         event.preventDefault();
//         event.returnValue = '';
//         return '';
//     }
// });

// =========================
// 전역 함수로 노출
// =========================
window.goBack = goBack;
window.removeImage = removeImage;
window.showNotification = showNotification;
window.validateForm = validateForm;

// =========================
// 에러 핸들링
// =========================
window.addEventListener('error', function(event) {
    console.error('페이지 오류:', event.error);
    showNotification('페이지를 처리하는 중 오류가 발생했습니다.', 'error');
});

window.addEventListener('unhandledrejection', function(event) {
    console.error('처리되지 않은 Promise 거부:', event.reason);
    showNotification('작업 처리 중 오류가 발생했습니다.', 'error');
});

console.log('🤝 무료나눔 수정 페이지 JavaScript가 로드되었습니다.');
console.log('📝 사용 가능한 기능:');
console.log('    - 지역 연동 선택 (기존 값 자동 채우기 포함)');
console.log('    - 이미지 드래그 앤 드롭 (새 이미지 추가)');
console.log('    - 기존 이미지 삭제 및 새 이미지 추가 통합');
console.log('    - 실시간 유효성 검사');
console.log('    - 키보드 단축키 (Ctrl+S, ESC)');
console.log('    - 접근성 지원');
// console.log('    - 페이지 이탈 시 변경사항 경고');