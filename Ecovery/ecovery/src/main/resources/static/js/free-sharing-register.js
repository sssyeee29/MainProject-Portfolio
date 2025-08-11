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


// 업로드된 이미지들을 저장할 배열
let uploadedImages = [];

// =========================
// 페이지가 로드되면 실행되는 함수
// =========================
document.addEventListener('DOMContentLoaded', function() {
    console.log('페이지가 로드되었습니다!');

// 현재 날짜를 등록일에 자동으로 입력
    setCurrentDate();

// 각종 이벤트 리스너 등록
    setupEventListeners();

// 페이드인 애니메이션 적용
    setTimeout(function() {
        const formContainer = document.querySelector('.form-container');
        if (formContainer) {
            formContainer.classList.add('fade-in');
        }
    }, 200);
});

// =========================
// 현재 날짜 설정 함수
// =========================
function setCurrentDate() {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const day = String(today.getDate()).padStart(2, '0');
    const dateString = `${year}.${month}.${day}`;
    const regDateInput = document.getElementById('regDate');
    if (regDateInput) {
        regDateInput.value = dateString;
    }
}

// =========================
// 이벤트 리스너 설정 함수
// =========================
function setupEventListeners() {
// 시/도 선택이 바뀔 때
    const region1Select = document.getElementById('region1');
    if (region1Select) {
        region1Select.addEventListener('change', handleRegion1Change);
    }
// 이미지 업로드 관련
    const imageUploadArea = document.getElementById('imageUploadArea');
    const imageInput = document.getElementById('imageInput');
    if (imageUploadArea && imageInput && !imageUploadArea.dataset.listenerAttached) { // imageInput 대신 imageUploadArea 확인
        imageUploadArea.addEventListener('click', () => imageInput.click());
        imageUploadArea.dataset.listenerAttached = 'true'; // 💡 imageUploadArea에 플래그 설정
        imageInput.addEventListener('change', handleImageSelect);
        setupDragAndDrop(imageUploadArea);
    }

// 폼 제출 이벤트
    const form = document.getElementById('registrationForm');
    if (form) {
        form.addEventListener('submit', handleFormSubmit);
    }

// 실시간 유효성 검사
    setupRealtimeValidation();

// 카테고리 변경시 도움말 표시
    const categorySelect = document.getElementById('category');
    if (categorySelect) {
        categorySelect.addEventListener('change', handleCategoryChange);
    }

// 상품 상태 변경시 미리보기 표시
    const conditionSelect = document.getElementById('condition');
    if (conditionSelect) {
        conditionSelect.addEventListener('change', handleConditionChange);
    }

// 제목 글자수 제한
    const titleInput = document.getElementById('title');
    if (titleInput) {
        titleInput.addEventListener('input', function() {
            limitCharacters(this, 50, '제목');
        });
    }

// 설명 글자수 제한 및 카운터
    const descriptionInput = document.getElementById('description');
    if (descriptionInput) {
        descriptionInput.addEventListener('input', function() {
            limitCharacters(this, 1000, '설명');
            updateCharacterCounter(this, 1000);
        });
    }
}

// =========================
// 지역 선택 관련 함수
// =========================
// 구가 변경되었을 때 실행
function handleRegion1Change() {
    const region1Select = document.getElementById('region1');
    const region2Select = document.getElementById('region2');

    if (!region1Select || !region2Select) return;
    const selectedRegion = region1Select.value;

// 동 선택박스 초기화
    region2Select.innerHTML = '<option value="">동</option>';

// 선택된 구에 해당하는 동 추가
    if (selectedRegion && regionData[selectedRegion]) {
        regionData[selectedRegion].forEach(function(district) {
            const option = document.createElement('option');
            option.value = district;
            option.textContent = district;
            region2Select.appendChild(option);
        });
    }
}

// =========================
// 이미지 업로드 관련 함수
// =========================
// 파일이 선택되었을 때
function handleImageSelect(event) {
    const files = event.target.files;
    handleImageFiles(Array.from(files));

// 같은 파일 선택 시에도 change 이벤트가 발생하도록 value 초기화
    event.target.value = '';
}

// 드래그 앤 드롭 설정
function setupDragAndDrop(uploadArea) {
// 드래그 오버
    uploadArea.addEventListener('dragover', function(event) {
        event.preventDefault();
        uploadArea.style.borderColor = 'var(--primary-green)';
        uploadArea.style.background = 'rgba(45, 90, 61, 0.1)';
    });

// 드래그 나감
    uploadArea.addEventListener('dragleave', function() {
        uploadArea.style.borderColor = 'var(--accent-green)';
        uploadArea.style.background = 'rgba(111, 167, 118, 0.05)';
    });

// 드롭
    uploadArea.addEventListener('drop', function(event) {
        event.preventDefault();

// 스타일 원복
        uploadArea.style.borderColor = 'var(--accent-green)';
        uploadArea.style.background = 'rgba(111, 167, 118, 0.05)';
        const files = event.dataTransfer.files;
        handleImageFiles(Array.from(files));
    });
}

// 이미지 파일들 처리
function handleImageFiles(files) {
// 이미지 파일만 필터링
    const imageFiles = files.filter(function(file) {
        return file.type.startsWith('image/');
    });

// 최대 5개까지만 허용
    if (uploadedImages.length + imageFiles.length > 5) {
        showNotification('최대 5개의 이미지만 업로드할 수 있습니다.', 'error');
        return;
    }

// 각 이미지 파일 처리
    imageFiles.forEach(function(file) {

// 파일 크기 체크 (10MB)
        if (file.size > 10 * 1024 * 1024) {
            showNotification(file.name + '은 10MB를 초과합니다.', 'error');
            return;
        }

// 파일을 읽어서 미리보기 생성
        const reader = new FileReader();
        reader.onload = function(event) {
            const imageData = {
                file: file,
                src: event.target.result,
                id: Date.now() + Math.random()
            };
            uploadedImages.push(imageData);
            displayImagePreview(imageData, uploadedImages.length - 1);
        };
        reader.readAsDataURL(file);
    });
}

// 이미지 미리보기 표시
function displayImagePreview(imageData, index) {
    const previewContainer = document.getElementById('imagePreview');
    if (!previewContainer) return;

// 미리보기 아이템 생성
    const previewItem = document.createElement('div');
    previewItem.className = 'preview-item';
    previewItem.innerHTML = `<img src="${imageData.src}" alt="미리보기" class="preview-image">
<button type="button" class="remove-image" onclick="removeImage(${index})">×</button>`;
    previewContainer.appendChild(previewItem);
}

// 이미지 삭제
function removeImage(index) {
    const imageInput = document.getElementById('imageInput');
    const dt = new DataTransfer();
    const files = Array.from(imageInput.files);
    files.splice(index, 1);
    files.forEach(file => dt.items.add(file));
    imageInput.files = dt.files;
    uploadedImages.splice(index, 1);
    updateImagePreview();
// 미리보기 다시 생성
    const changeEvent = new Event('change', { bubbles: true });
    imageInput.dispatchEvent(changeEvent);
}

// 이미지 미리보기 업데이트
function updateImagePreview() {
    const previewContainer = document.getElementById('imagePreview');
    if (!previewContainer) return;

// 기존 미리보기 모두 제거
    previewContainer.innerHTML = '';

// 현재 업로드된 이미지들로 다시 생성
    uploadedImages.forEach(function(imageData, index) {
        displayImagePreview(imageData, index);
    });
}

// =========================
// 폼 유효성 검사 함수
// =========================
// 실시간 유효성 검사 설정
function setupRealtimeValidation() {
    const inputs = document.querySelectorAll('.form-input, .form-select');
    inputs.forEach(function(input) {
// 포커스를 잃었을 때
        input.addEventListener('blur', function() {
            validateField(input);
        });
// 내용이 변경될 때
        input.addEventListener('input', function() {
            if (input.classList.contains('error') && input.value.trim()) {
                clearFieldError(input);
                input.classList.add('success');
            }
        });
    });
}

// 개별 필드 유효성 검사
function validateField(field) {
    const value = field.value.trim();
    const isRequired = field.hasAttribute('required');
    if (isRequired && !value) {
        showFieldError(field, '필수 입력 항목입니다.');
        return false;
    } else if (value) {
        clearFieldError(field);
        field.classList.add('success');
        return true;
    }
    return true;
}

// 필드 에러 표시
function showFieldError(field, message) {
    field.classList.add('error');
    field.classList.remove('success');
// 기존 에러 메시지 제거
    const existingError = field.parentNode.querySelector('.error-message');
    if (existingError) {
        existingError.remove();
    }

// 새 에러 메시지 생성
    const errorDiv = document.createElement('div');
    errorDiv.className = 'error-message';
    errorDiv.textContent = message;
    field.parentNode.appendChild(errorDiv);
}

// 필드 에러 제거
function clearFieldError(field) {
    field.classList.remove('error');
    const errorMessage = field.parentNode.querySelector('.error-message');
    if (errorMessage) {
        errorMessage.remove();
    }
}

// 전체 폼 유효성 검사
function validateForm() {
    let isValid = true;
    const requiredFields = ['title', 'condition', 'region1', 'region2', 'category', 'description'];
    requiredFields.forEach(function(fieldId) {
        const field = document.getElementById(fieldId);
        if (field && !validateField(field)) {
            isValid = false;
        }
    });
    return isValid;
}

// =========================
// 폼 제출 관련 함수
// =========================

// 폼 제출 처리
// free-sharing-register.js 파일 내에서
// 이전에 정의된 handleFormSubmit 함수가 있다면 완전히 대체해야 합니다.
async function handleFormSubmit(event) { // 'async' 키워드 유지
    event.preventDefault(); // 기본 폼 제출 방지
    console.log('폼 제출 시도');
// 폼 유효성 검사
    if (!validateForm()) { // 이 validateForm 함수는 별도로 정의되어 있어야 합니다.
        showNotification('필수 입력 항목을 모두 작성해주세요.', 'error'); // showNotification 함수도 별도로 정의
        return;
    }

// 제출 버튼 상태 변경
    const submitBtn = document.querySelector('.btn-submit');
    const originalText = submitBtn.textContent;
    submitBtn.textContent = '등록 중...';
    submitBtn.disabled = true;

// FormData 객체 생성
    const formData = new FormData(); // formData는 여기서 한 번만 선언됩니다.

// freeDto를 JSON 문자열로 만들어 Blob으로 감싸서 첨부
    const freeDtoJson = {
        title: document.getElementById('title').value,
        nickname: document.getElementById('author').value, // HTML에서 author id의 input을 사용한다면
        memberId: document.getElementById('memberId').value,
        itemCondition: document.getElementById('condition').value,
        regionGu: document.getElementById('region1').value,
        regionDong: document.getElementById('region2').value,
        category: document.getElementById('category').value,
        content: document.getElementById('description').value,

// ⭐️ 대표 이미지 필드에 임시 값 채우기 (가장 중요한 변경사항)
        imgUrl: uploadedImages.length > 0 ? 'placeholder' : '',
        dealStatus: 'ONGOING'
    };
    formData.append("freeDto", new Blob([JSON.stringify(freeDtoJson)], { type: "application/json" }));

// 이미지 파일들 추가

// uploadedImages는 전역적으로 관리되는 이미지 배열이어야 합니다.
    uploadedImages.forEach(function(imageData) {
        formData.append("imgFile", imageData.file);
    });

// 서버에 전송
    try { // fetch를 await과 함께 사용하므로 try-catch 문으로 감쌉니다.
        const response = await fetch('/api/free/register', { // 정확한 API 엔드포인트 확인 (HTML action과 일치하는지)
            method: 'POST',
            body: formData
        });
        const text = await response.text(); // 응답을 텍스트로 먼저 받기

// 상태 코드로 성공 여부 판단
        if (response.ok) {
            showNotification('나눔 등록이 완료되었습니다! 🎉', 'success');
            setTimeout(function () {
                if (confirm('나눔 목록 페이지로 이동하시겠습니까?')) {
                    window.location.href = '/free/list';
                }
            }, 500); // 0.5초 후 확인 메시지 표시
        } else {
// 에러 메시지 보여주기
            showNotification(text || '등록 중 오류가 발생했습니다.', 'error');
        }
    } catch (error) {
        console.error('등록 오류:', error);
        showNotification('등록 중 오류가 발생했습니다.', 'error');
    } finally { // 성공/실패 여부와 관계없이 버튼 상태 초기화
        submitBtn.textContent = originalText;
        submitBtn.disabled = false;
    }
}



// 폼 제출 이벤트 리스너는 문서 로드 시 한 번만 등록되어야 합니다.
// 예를 들어 setupEventListeners 함수 안에 있거나, DOMContentLoaded 리스너 안에 있어야 합니다.
// const registrationForm = document.getElementById('registrationForm');
// if (registrationForm) {
// registrationForm.addEventListener('submit', handleFormSubmit);
// }

// =========================
// 상품 상태 관련 함수
// =========================

// 상품 상태 변경시 미리보기 표시

function handleConditionChange() {

    const conditionSelect = document.getElementById('condition');

    const conditionPreview = document.getElementById('conditionPreview');



    if (!conditionSelect || !conditionPreview) return;



    const selectedCondition = conditionSelect.value;



// 미리보기 초기화

    conditionPreview.className = 'condition-preview';

    conditionPreview.style.display = 'none';



    if (selectedCondition) {

        let previewText = '';

        let previewClass = '';



        switch(selectedCondition) {

            case 'HIGH':

                previewText = '👍 상태가 매우 좋은 상품입니다';

                previewClass = 'good';

                break;

            case 'MEDIUM':

                previewText = '👌 일반적인 사용감이 있는 상품입니다';

                previewClass = 'fair';

                break;

            case 'LOW':

                previewText = '⚠️ 사용감이 많이 있는 상품입니다';

                previewClass = 'poor';

                break;

        }



// 미리보기 표시

        conditionPreview.textContent = previewText;

        conditionPreview.classList.add(previewClass);

        conditionPreview.style.display = 'block';



// 애니메이션 효과

        setTimeout(function() {

            conditionPreview.style.opacity = '1';

            conditionPreview.style.transform = 'translateY(0)';

        }, 100);

    }

}



// 카테고리 변경시 도움말 표시

function handleCategoryChange() {

    const categorySelect = document.getElementById('category');

    const descriptionInput = document.getElementById('description');



    if (!categorySelect || !descriptionInput) return;



    const category = categorySelect.value;



// 카테고리별 도움말

    const helpTexts = {

        '가구': '가구는 크기와 무게를 미리 안내해주세요.',

        '전자제품': '정상 작동 여부와 구매 시기를 명시해주세요.',

        '의류': '사이즈와 계절 정보를 포함해주세요.',

        '도서': '전집인지 단행본인지, 출간년도를 알려주세요.',

        '육아용품': '사용 기간과 안전성을 중점적으로 설명해주세요.'

    };



// 설명란이 비어있을 때만 도움말 추가

    if (helpTexts[category] && !descriptionInput.value) {

        const helpText = helpTexts[category];

        descriptionInput.placeholder = `물품에 대한 자세한 설명을 적어주세요\n\n💡 ${helpText}\n\n예시:\n- 사용 기간\n- 구매 시기\n- 특이사항 등`;

    }

}



// =========================

// 글자수 제한 관련 함수

// =========================



// 글자수 제한

function limitCharacters(input, maxLength, fieldName) {

    if (input.value.length > maxLength) {

        input.value = input.value.substring(0, maxLength);

        showNotification(`${fieldName}은 ${maxLength}자까지 입력 가능합니다.`, 'error');

    }

}



// 글자수 카운터 업데이트

function updateCharacterCounter(input, maxLength) {

    const currentLength = input.value.length;



// 기존 카운터 찾기 또는 생성

    let counter = input.parentNode.querySelector('.char-counter');

    if (!counter) {

        counter = document.createElement('div');

        counter.className = 'char-counter';

        counter.style.cssText = 'font-size: 12px; color: var(--medium-gray); text-align: right; margin-top: 5px;';

        input.parentNode.appendChild(counter);

    }



// 카운터 텍스트 업데이트

    counter.textContent = `${Math.min(currentLength, maxLength)}/${maxLength}`;



// 글자수가 많아지면 색상 변경

    if (currentLength > maxLength * 0.9) {

        counter.style.color = 'var(--error-red)';

    } else {

        counter.style.color = 'var(--medium-gray)';

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
    setTimeout(function() {
        notification.classList.add('show');
    }, 100);


// 3초 후 자동 제거
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
// 뒤로가기 함수
function goBack() {

// 입력된 내용이 있는지 확인
    const hasContent = checkFormHasContent();
    if (hasContent) {
        if (confirm('입력한 내용이 사라집니다. 정말로 나가시겠습니까?')) {
            window.history.back();
        }
    } else {
        window.history.back();
    }
}

// 폼에 내용이 있는지 확인
function checkFormHasContent() {
    const inputs = document.querySelectorAll('.form-input, .form-select');
    let hasContent = false;
    inputs.forEach(function(input) {

// 작성자와 등록일은 제외
        if (input.id !== 'author' && input.id !== 'regDate') {
            if (input.value.trim()) {
                hasContent = true;
            }
        }
    });

// 업로드된 이미지가 있는지도 확인
    if (uploadedImages.length > 0) {
        hasContent = true;
    }
    return hasContent;
}

// =========================
// 키보드 단축키
// =========================
// 키보드 이벤트 처리
document.addEventListener('keydown', function(event) {

// Ctrl + S: 폼 저장 (제출)
    if (event.ctrlKey && event.key === 's') {
        event.preventDefault();
        const form = document.getElementById('registrationForm');
        if (form) {
            form.dispatchEvent(new Event('submit'));
        }
    }

// ESC: 취소
    if (event.key === 'Escape') {
        goBack();
    }
});

// =========================
// 페이지 초기화 완료 후 실행
// =========================
// DOM이 완전히 로드된 후 추가 설정
document.addEventListener('DOMContentLoaded', function() {
});

// =========================
// 전역 함수로 노출 (HTML에서 onclick 등으로 사용)
// =========================
// HTML의 onclick에서 사용할 수 있도록 전역으로 노출
window.goBack = goBack;
window.removeImage = removeImage;

// 기타 유용한 전역 함수들
window.showNotification = showNotification;
window.validateForm = validateForm;

// =========================
// 에러 핸들링
// =========================
// 전역 에러 처리
window.addEventListener('error', function(event) {
    console.error('페이지 오류:', event.error);
});

// Promise 거부 처리
window.addEventListener('unhandledrejection', function(event) {
    console.error('처리되지 않은 Promise 거부:', event.reason);
    showNotification('작업 처리 중 오류가 발생했습니다.', 'error');
});

// =========================
// 최종 로그
// =========================
console.log('🤝 무료나눔 등록 페이지 JavaScript가 로드되었습니다.');
console.log('📝 사용 가능한 기능:');
console.log(' - 지역 연동 선택');
console.log(' - 이미지 드래그 앤 드롭');
console.log(' - 실시간 유효성 검사');
console.log(' - 자동 저장/복원');
console.log(' - 키보드 단축키 (Ctrl+S, ESC)');
console.log(' - 접근성 지원');