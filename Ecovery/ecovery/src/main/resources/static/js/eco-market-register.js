// =========================
// @history
//  - 250730 | sehui | 지역 선택, 폼 제출 기능 삭제
//  - 250730 | sehui | 에코마켓 상품 등록 페이지 요청 기능 추가
//  - 250730 | sehui | 에코마켓 상품 등록 버튼 클릭 시 등록 요청 기능 추가
//  - 250730 | sehui | 취소 버튼 클릭 시 목록 페이지 이동 요청 기능 추가
// =========================

// =========================
// 전역 변수 (페이지 전체에서 사용)
// =========================

// 업로드된 이미지들을 저장할 배열
let uploadedImages = [];

// =========================
// 페이지가 로드되면 실행되는 함수
// =========================
document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 상품 등록 페이지 메인 기능 로딩 시작');

    setCurrentDate();           // 현재 날짜 자동 입력
    initializePage();           //페이지 기본 설정 초기화
    initFormData();             //폼 초기화
    setupEventListeners();      // 각종 이벤트 리스너 등록

    console.log('✅ 상품 등록 페이지 메인 기능 로딩 완료!');

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
// 페이지 초기화 함수
// =========================
function initializePage() {
    console.log('⚙️ 페이지 기본 설정 초기화...');

    // 페이드인 애니메이션 적용
    setTimeout(function() {
        const formContainer = document.querySelector('.form-container');
        if (formContainer) {
            formContainer.classList.add('fade-in');
        }
    }, 200);
}

// =========================
// 폼 초기화 함수
// =========================
function initFormData(){
    console.log('⚙️ 폼 초기화...');
    
    fetch("/api/eco/new")
        .then(response => {
            if(response.status === 403) {
                alert("권한이 없습니다. 관리자만 접근 가능합니다.");
                window.location.href = "/eco/list";
                throw new Error("권한 없음");
            }
            if(!response.ok) {
                throw new Error("서버 에러");
            }
            return response.json();
        })
        .then(data => {
            //응답객체 data.itemFormDto, data.categories 사용
            //카테고리 select 박스에 옵션 추가
            const categorySelect = document.getElementById("category");
            categorySelect.innerHTML = '<option value="">카테고리를 선택해주세요</option>';

            data.categories.forEach(category => {
                const option = document.createElement("option");
                option.value = category.categoryId;
                option.textContent = category.categoryName;

                categorySelect.appendChild(option);
            });

            console.log('✅ 기본 폼 데이터 로드 완료');
        })
        .catch(error => {
            console.error("❌상품 등록 폼 데이터 로드 실패 : ", error);
            showNotification('상품 등록 폼을 불러오지 못했습니다.', 'warning');
        });

}


// =========================
// 이벤트 리스너 설정 함수
// =========================
function setupEventListeners() {
    console.log('🔧 이벤트 리스너 설정...');
    
    // 이미지 업로드 관련
    const imageUploadArea = document.getElementById('imageUploadArea');
    const imageInput = document.getElementById('imageInput');
    
    if (imageUploadArea && imageInput) {
        // 업로드 영역 클릭시
        imageUploadArea.addEventListener('click', function(e) {
            e.preventDefault();     //이벤트 전파 막기
            imageInput.click();
        });
        
        // 파일이 선택되었을 때
        imageInput.addEventListener('change', handleImageSelect);
        
        // 드래그 앤 드롭 관련
        setupDragAndDrop(imageUploadArea);
    }
    
    // 실시간 유효성 검사
    setupRealtimeValidation();
    
    // 카테고리 변경시 도움말 표시
    const categorySelect = document.getElementById('category');
    if (categorySelect) {
        categorySelect.addEventListener('change', handleCategoryChange);
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

    //상품 등록 관련 이벤트
    setupRegistrationEvents();
}

/**
 * 상품 등록 이벤트 함수
 * @param event
 */
function setupRegistrationEvents() {
    console.log("🔧 상품 등록 버튼 이벤트 실행");
    
    const form = document.getElementById("registrationForm");
    const cancelBtn = document.getElementById("cancelBtn");
    let isSubmitting = false;

    //beforeunload 경고창
    function beforeUnloadHandler(event) {
        if (isSubmitting) return;       //등록 중일 때는 경고창 안 띄움

        if (checkFormHasContent()) {
            event.preventDefault();
            event.returnValue = '';
            return '';
        }
    }


    if (form) {
        // beforeunload 등록
        window.addEventListener("beforeunload", beforeUnloadHandler);

        form.addEventListener("submit", async (e) => {
            e.preventDefault();     //form의 기본 동작 막기
            isSubmitting = true;    //제출 직전에 플래그 설정

            //입력값 수집
            const itemFormDto = {
                itemNm: document.getElementById("productName").value,
                price: document.getElementById("price").value,
                stockNumber: document.getElementById("stockNumber").value,
                categoryId: document.getElementById("category").value,
                itemDetail: document.getElementById("description").value,
                itemSellStatus: document.getElementById("condition").value,
            };

            //FormData 구성
            const formData = new FormData();
            formData.append("itemFormDto", new Blob([JSON.stringify(itemFormDto)], {
                type: "application/json"
            }));

            //이미지 파일 추가 (전역 변수 uploadedImages 사용)
            uploadedImages.forEach(img => {
                formData.append("itemImgFile", img.file);
            });

            //서버에 POST 요청
            try {
                const response = await fetch("/api/eco/new", {
                    method: "POST",
                    body: formData
                });

                if (response.status === 201) {
                    //경고창 제거
                    window.removeEventListener("beforeunload", beforeUnloadHandler);
                    showNotification('상품이 성공적으로 등록되었습니다.', 'success');
                    console.log('✅ 상품 등록 완료');
                    setTimeout(() => {
                        window.location.href = "/eco/list";
                    }, 2000);
                } else {
                    isSubmitting = false;           //실패 시 플래그 복구
                    const errorData = await response.json();
                    showNotification(`상품 등록 중 오류가 발생했습니다.`, 'warning');
                    console.error('❌ 상품 등록 중 오류가 발생했습니다 : ${errorData.errorMessage}');
                }
            } catch (error) {
                isSubmitting = false;       //실패 시 플래그 복구
                showNotification('서버와 통신 중 문제가 발생했습니다.', 'error');
                console.error("❌ 상품 등록 요청 실패:", error);
            }
        });
    }

    //취소 버튼 클릭 시
    if (cancelBtn) {
        cancelBtn.addEventListener("click", function () {
            //checkFormHasContent() : 입력값/이미지 있는지 확인하는 함수
            if (checkFormHasContent()) {
                const confirmLeave = confirm("작성한 내용이 삭제됩니다. 정말 나가시겠습니까?");
                if (confirmLeave) {
                    //경고창 제거
                    window.removeEventListener("beforeunload", beforeUnloadHandler);
                    setTimeout(() => {
                        window.location.href = "/eco/list";
                    }, 200);
                }
            //입력값이 없는 경우
            } else {
                setTimeout(() => {
                    window.location.href = "/eco/list";
                }, 200);
            }
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
            displayImagePreview(imageData);
        };
        reader.readAsDataURL(file);
    });
}

// 이미지 미리보기 표시
function displayImagePreview(imageData) {
    const previewContainer = document.getElementById('imagePreview');
    if (!previewContainer) return;
    
    // 미리보기 아이템 생성
    const previewItem = document.createElement('div');
    previewItem.className = 'preview-item';
    previewItem.innerHTML = `
        <img src="${imageData.src}" alt="미리보기" class="preview-image">
        <button type="button" class="remove-image" onclick="removeImage('${imageData.id}')">×</button>
    `;
    
    previewContainer.appendChild(previewItem);
}

// 이미지 삭제
function removeImage(imageId) {
    // 배열에서 해당 이미지 제거
    uploadedImages = uploadedImages.filter(function(img) {
        return img.id != imageId;
    });
    
    // 미리보기 다시 그리기
    updateImagePreview();
}

// 이미지 미리보기 업데이트
function updateImagePreview() {
    const previewContainer = document.getElementById('imagePreview');
    if (!previewContainer) return;
    
    // 기존 미리보기 모두 제거
    previewContainer.innerHTML = '';
    
    // 현재 업로드된 이미지들로 다시 생성
    uploadedImages.forEach(function(imageData) {
        displayImagePreview(imageData);
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
// 카테고리 관련 함수
// =========================

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

function getNotificationColor(type) {
    const colors = {
        'success': '#27ae60',   // 초록
        'error': '#e74c3c',     // 빨강
        'warning': '#f39c12',   // 주황
        'cart': '#8e44ad',      // 보라 (장바구니 전용 색상)
        'info': '#3498db'       // 파랑
    };
    return colors[type] || colors.info; // 정의되지 않은 타입은 info 색상 사용
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
    
    // 서버에 에러 리포트 전송
    fetch('/api/errors', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            error: event.error?.message || 'Unknown error',
            stack: event.error?.stack,
            url: window.location.href,
            timestamp: new Date().toISOString()
        })
    }).catch(console.error);
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
console.log('   - 이미지 드래그 앤 드롭');
console.log('   - 실시간 유효성 검사');
console.log('   - 키보드 단축키 (Ctrl+S, ESC)');
console.log('   - 접근성 지원');