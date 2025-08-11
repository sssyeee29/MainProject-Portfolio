// =========================
// @history
//  - 250801 | sehui | DTO 필드명과 컨트롤러에 맞게 key, 타입 수정 적용
//  - 250802 | gemini | 이미지 삭제 기능 및 유효성 검사 로직 재정비
// =========================

// =========================
// 전역 변수 (페이지 전체에서 사용)
// =========================
let uploadedImages = [];         // 새로 업로드한 이미지 파일 데이터
let deletedImageIds = [];        // 삭제 표시한 기존 이미지 ID들
let existingImages = [];         // 페이지 로드 시 불러온 기존 이미지 정보 (ID, URL 등)
const productId = window.location.pathname.split('/').pop(); // URL에서 itemId 추출

// =========================
// 페이지 로드 시 실행
// =========================
document.addEventListener('DOMContentLoaded', () => {
    console.log('상품 수정 페이지가 로드되었습니다!');

    fetchEditPageData();
    setupEventListeners();

    setTimeout(() => {
        const formContainer = document.querySelector('.form-container');
        if (formContainer) formContainer.classList.add('fade-in');
    }, 200);
});

// =========================
// 상품 및 카테고리 데이터 불러오기
// =========================
function fetchEditPageData() {
    fetch(`/api/eco/modify/${productId}`)
        .then(response => {
            if (response.status === 403) {
                alert("권한이 없습니다. 관리자만 접근 가능합니다.");
                window.location.href = "/eco/list";
                throw new Error("권한 없음");
            }
            if (!response.ok) throw new Error("서버 에러");
            return response.json();
        })
        .then(data => {
            fillEditForm(data.item, data.categories || []);
        })
        .catch(error => {
            console.error("상품 정보 불러오기 오류:", error);
            alert("상품 정보를 불러오지 못했습니다.");
            window.location.href = "/eco/list";
        });
}

// =========================
// 폼에 데이터 채우기 (DTO 필드명 기준)
// =========================
function fillEditForm(item, categories) {
    document.getElementById('itemNm').value = item.itemNm || '';
    document.getElementById('itemDetail').value = item.itemDetail || '';
    document.getElementById('price').value = item.price ?? '';
    document.getElementById('stockNumber').value = item.stockNumber ?? '';
    document.getElementById('itemSellStatus').value = item.itemSellStatus || '';

    const categorySelect = document.getElementById('categoryId');
    categorySelect.innerHTML = '<option value="">카테고리를 선택해주세요</option>';
    categories.forEach(c => {
        const option = document.createElement('option');
        option.value = c.categoryId;
        option.textContent = c.categoryName;
        if (c.categoryId === item.categoryId) option.selected = true;
        categorySelect.appendChild(option);
    });

    // 기존 이미지 정보 저장 및 렌더링
    existingImages = item.itemImgDtoList || [];
    renderAllImages();

    console.log('existingImages (after load):', existingImages); // 여기!
}

// =========================
// 이미지 미리보기 및 삭제 처리
// =========================
function renderAllImages() {
    const previewContainer = document.getElementById('imagePreview');
    if (!previewContainer) return;

    // 기존 내용 모두 초기화 후 다시 그리기
    previewContainer.innerHTML = '';

    // 기존 이미지 렌더링
    existingImages.forEach(imgDto => {
        const isDeleted = deletedImageIds.includes(String(imgDto.itemImgId));
        const div = document.createElement('div');
        div.className = 'preview-item existing-image-item';
        div.style.opacity = isDeleted ? '0.5' : '1';
        div.dataset.id = imgDto.itemImgId;

        const buttonText = isDeleted ? '삭제됨' : '×';
        const buttonDisabled = isDeleted ? 'disabled' : '';

        div.innerHTML = `
            <img src="${imgDto.imgUrl}" alt="등록된 이미지" class="preview-image" style="width:100px; height:auto;">
            <button type="button" class="btn-delete-existing" data-id="${imgDto.itemImgId}" style="margin-left:5px; cursor:pointer;" ${buttonDisabled}>
                ${buttonText}
            </button>
        `;
        previewContainer.appendChild(div);

        console.log(
            '이미지ID:', imgDto.itemImgId,
            'isDeleted?', deletedImageIds.includes(String(imgDto.itemImgId)),
            'deletedImageIds:', deletedImageIds
        );
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

    // 삭제 버튼 클릭 이벤트 재등록 (기존 이미지 삭제)
    previewContainer.querySelectorAll('.btn-delete-existing').forEach(btn => {
        btn.addEventListener('click', () => {
            const id = btn.getAttribute('data-id');
            if (!deletedImageIds.includes(String(id))) {
                deletedImageIds.push(String(id));
                console.log('deletedImageIds (delete):', deletedImageIds);
            }
            renderAllImages();
        });
    });
}

// =========================
// 이미지 삭제 (새로 업로드된 이미지 제거)
// =========================
function removeImage(imageId) {
    uploadedImages = uploadedImages.filter(img => String(img.id) !== String(imageId));
    renderAllImages();
}

// =========================
// 이미지 업로드 및 드래그앤드롭 처리
// =========================
function handleImageSelect(event) {
    handleImageFiles(Array.from(event.target.files));
}

function setupDragAndDrop(uploadArea) {
    uploadArea.addEventListener('dragover', e => {
        e.preventDefault();
        uploadArea.style.borderColor = 'var(--primary-green)';
        uploadArea.style.background = 'rgba(45, 90, 61, 0.1)';
    });
    uploadArea.addEventListener('dragleave', () => {
        uploadArea.style.borderColor = 'var(--accent-green)';
        uploadArea.style.background = 'rgba(111, 167, 118, 0.05)';
    });
    uploadArea.addEventListener('drop', e => {
        e.preventDefault();
        uploadArea.style.borderColor = 'var(--accent-green)';
        uploadArea.style.background = 'rgba(111, 167, 118, 0.05)';
        handleImageFiles(Array.from(e.dataTransfer.files));
    });
}

function handleImageFiles(files) {
    const images = files.filter(file => file.type.startsWith('image/'));
    const imagesToKeepCount = existingImages.filter(img => !deletedImageIds.includes(String(img.itemImgId))).length;

    if (imagesToKeepCount + uploadedImages.length + images.length > 5) {
        showNotification('최대 5개의 이미지만 업로드할 수 있습니다.', 'error');
        return;
    }

    images.forEach(file => {
        if (file.size > 10 * 1024 * 1024) {
            showNotification(`${file.name}은 10MB를 초과합니다.`, 'error');
            return;
        }
        const reader = new FileReader();
        reader.onload = e => {
            const imageData = {
                file,
                src: e.target.result,
                id: Date.now() + Math.random()
            };
            uploadedImages.push(imageData);
            renderAllImages();
        };
        reader.readAsDataURL(file);
    });
}

// =========================
// 이벤트 리스너 등록
// =========================
function setupEventListeners() {
    const imageUploadArea = document.getElementById('imageUploadArea');
    const imageInput = document.getElementById('imageInput');
    if (imageUploadArea && imageInput) {
        imageUploadArea.addEventListener('click', e => {
            e.preventDefault();
            imageInput.click();
        });
        imageInput.addEventListener('change', handleImageSelect);
        setupDragAndDrop(imageUploadArea);
    }

    setupRealtimeValidation();

    const categorySelect = document.getElementById('categoryId');
    if (categorySelect) categorySelect.addEventListener('change', handleCategoryChange);

    const conditionSelect = document.getElementById('itemSellStatus');
    if (conditionSelect) conditionSelect.addEventListener('change', handleConditionChange);

    const titleInput = document.getElementById('itemNm');
    if (titleInput) {
        titleInput.addEventListener('input', () => limitCharacters(titleInput, 50, '상품명'));
    }

    const descriptionInput = document.getElementById('itemDetail');
    if (descriptionInput) {
        descriptionInput.addEventListener('input', () => {
            limitCharacters(descriptionInput, 1000, '상품설명');
            updateCharacterCounter(descriptionInput, 1000);
        });
    }

    const form = document.getElementById('editForm');
    if (form) {
        form.addEventListener('submit', handleFormSubmit);
    }

    const cancelBtn = document.getElementById('cancelBtn');
    if (cancelBtn) {
        cancelBtn.addEventListener('click', () => {
            if (checkFormHasContent()) {
                if (confirm("작성한 내용이 삭제됩니다. 정말 나가시겠습니까?")) {
                    window.location.href = "/eco/list";
                }
            } else {
                window.location.href = "/eco/list";
            }
        });
    }
}

// =========================
// 판매 상태 변경 시 미리보기 업데이트
// =========================
function handleConditionChange() {
    const conditionSelect = document.getElementById('itemSellStatus');
    const preview = document.getElementById('conditionPreview');
    if (!conditionSelect || !preview) return;

    const val = conditionSelect.value;
    let text = '';
    preview.className = 'condition-preview'; // 초기화

    switch(val) {
        case 'SELL':
            text = '판매중입니다.';
            preview.classList.add('excellent');
            break;
        case 'SOLD_OUT':
            text = '품절된 상품입니다.';
            preview.classList.add('poor');
            break;
        default:
            text = '';
    }

    preview.textContent = text;
    if (text) preview.classList.add('show'); else preview.classList.remove('show');
}

// =========================
// 실시간 유효성 검사 등록
// =========================
function setupRealtimeValidation() {
    const inputs = document.querySelectorAll('.form-input, .form-select');
    inputs.forEach(input => {
        input.addEventListener('blur', () => validateField(input));
        input.addEventListener('input', () => {
            if (input.classList.contains('error') && input.value.trim()) {
                clearFieldError(input);
                input.classList.add('success');
            }
        });
    });
}

function validateField(field) {
    const val = field.value.trim();
    const req = field.hasAttribute('required');
    if (req && !val) {
        showFieldError(field, '필수 입력 항목입니다.');
        return false;
    }
    clearFieldError(field);
    field.classList.add('success');
    return true;
}

function showFieldError(field, message) {
    field.classList.add('error');
    field.classList.remove('success');
    const existing = field.parentNode.querySelector('.error-message');
    if (existing) existing.remove();
    const errDiv = document.createElement('div');
    errDiv.className = 'error-message';
    errDiv.textContent = message;
    field.parentNode.appendChild(errDiv);
}

function clearFieldError(field) {
    field.classList.remove('error');
    const msg = field.parentNode.querySelector('.error-message');
    if (msg) msg.remove();
}

// =========================
// validateForm 함수 (전역)
// =========================
function validateForm() {
    let valid = true;
    ['itemNm', 'itemSellStatus', 'categoryId', 'itemDetail', 'price', 'stockNumber'].forEach(id => {
        const f = document.getElementById(id);
        if (f && !validateField(f)) valid = false;
    });
    return valid;
}

// =========================
// 폼 제출 처리 (상품 수정 요청)
// =========================
async function handleFormSubmit(e) {
    e.preventDefault();

    if (!validateForm()) {
        showNotification('필수 입력항목을 확인해주세요.', 'error');
        return;
    }

    const imagesToKeep = existingImages.filter(img => !deletedImageIds.includes(String(img.itemImgId))).length;
    const imagesToAdd = uploadedImages.length;

    if (imagesToKeep + imagesToAdd === 0) {
        showNotification('최소 1개 이상의 상품 이미지가 필요합니다.', 'error');
        return;
    }
    const remainingImages = existingImages
        .filter(img => !deletedImageIds.includes(String(img.itemImgId)));

    remainingImages.forEach((img, idx) => {
        img.repImgYn = (idx === 0) ? 'Y' : 'N';
    });

    const itemFormDto = {
        itemId: productId,
        itemNm: document.getElementById('itemNm').value,
        price: parseInt(document.getElementById('price').value),
        stockNumber: parseInt(document.getElementById('stockNumber').value),
        categoryId: document.getElementById('categoryId').value,
        itemDetail: document.getElementById('itemDetail').value,
        itemSellStatus: document.getElementById('itemSellStatus').value,
        itemImgDtoList: remainingImages.map(img => ({
            id: img.itemImgId,
            imgUrl: img.imgUrl,
            imgName: img.imgName,
            oriImgName: img.oriImgName,
            repImgYn: img.repImgYn
        }))
    };

    const formData = new FormData();
    formData.append('itemFormDto', new Blob([JSON.stringify(itemFormDto)], { type: "application/json" }));
    uploadedImages.forEach(img => formData.append('itemImgFile', img.file));

    try {
        const response = await fetch(`/api/eco/modify/${productId}`, {
            method: 'PUT',
            body: formData
        });
        if (response.ok) {
            alert('상품이 성공적으로 수정되었습니다.');
            window.location.href = '/eco/list';
        } else {
            const errText = await response.text();
            alert(errText || '상품 수정 중 오류가 발생했습니다.');
        }
    } catch (err) {
        alert('서버와 통신 중 문제가 발생했습니다.');
    }
}

// =========================
// 카테고리 변경 시 도움말 표시
// =========================
function handleCategoryChange() {
    const categorySelect = document.getElementById('categoryId');
    const descriptionInput = document.getElementById('itemDetail');
    if (!categorySelect || !descriptionInput) return;

    const category = categorySelect.options[categorySelect.selectedIndex]?.text || '';
    const helpTexts = {
        '가구': '가구는 크기와 무게를 미리 안내해주세요.',
        '전자제품': '정상 작동 여부와 구매 시기를 명시해주세요.',
        '의류': '사이즈와 계절 정보를 포함해주세요.',
        '도서': '전집인지 단행본인지, 출간년도를 알려주세요.',
        '육아용품': '사용 기간과 안전성을 중점적으로 설명해주세요.'
    };

    if (helpTexts[category] && !descriptionInput.value) {
        descriptionInput.placeholder = `물품에 대한 자세한 설명을 적어주세요\n\n💡 ${helpTexts[category]}\n\n예시:\n- 사용 기간\n- 구매 시기\n- 특이사항 등`;
    }
}

// =========================
// 글자수 제한 및 카운터
// =========================
function limitCharacters(input, maxLength, fieldName) {
    if (input.value.length > maxLength) {
        input.value = input.value.substring(0, maxLength);
        showNotification(`${fieldName}은 ${maxLength}자까지 입력 가능합니다.`, 'error');
    }
}

function updateCharacterCounter(input, maxLength) {
    const len = input.value.length;
    let counter = input.parentNode.querySelector('.char-counter');
    if (!counter) {
        counter = document.createElement('div');
        counter.className = 'char-counter';
        counter.style.cssText = 'font-size: 12px; color: var(--medium-gray); text-align: right; margin-top: 5px;';
        input.parentNode.appendChild(counter);
    }
    counter.textContent = `${Math.min(len, maxLength)}/${maxLength}`;
    counter.style.color = (len > maxLength * 0.9) ? 'var(--error-red)' : 'var(--medium-gray)';
}

// =========================
// 알림 표시
// =========================
function showNotification(message, type = 'success') {
    const existing = document.querySelector('.notification');
    if (existing) existing.remove();

    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;
    document.body.appendChild(notification);

    setTimeout(() => notification.classList.add('show'), 100);
    setTimeout(() => {
        notification.classList.remove('show');
        setTimeout(() => {
            if (notification.parentNode) notification.parentNode.removeChild(notification);
        }, 300);
    }, 3000);
}

// =========================
// 뒤로가기 및 입력 내용 체크
// =========================
function goBack() {
    if (checkFormHasContent()) {
        if (confirm('입력한 내용이 사라집니다. 정말로 나가시겠습니까?')) {
            window.history.back();
        }
    } else {
        window.history.back();
    }
}

function checkFormHasContent() {
    const inputs = document.querySelectorAll('.form-input, .form-select');
    let hasContent = false;
    inputs.forEach(input => {
        if (input.id !== 'author' && input.id !== 'regDate' && input.value.trim()) {
            hasContent = true;
        }
    });
    if (uploadedImages.length > 0) hasContent = true;
    return hasContent;
}

// =========================
// 전역 함수 노출
// =========================
window.goBack = goBack;
window.removeImage = removeImage;
window.showNotification = showNotification;
window.validateForm = validateForm;

// =========================
// 전역 에러 핸들링 (필요 시 수정 또는 삭제 가능)
// =========================
window.addEventListener('error', event => {
    fetch('/api/errors', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            error: event.error?.message || 'Unknown error',
            stack: event.error?.stack,
            url: window.location.href,
            timestamp: new Date().toISOString()
        })
    }).catch(console.error);
});

window.addEventListener('unhandledrejection', event => {
    showNotification('작업 처리 중 오류가 발생했습니다.', 'error');
});

// =========================
// 최종 로그
// =========================
console.log('✏️ 에코마켓 상품 수정 페이지 JavaScript가 로드되었습니다.');
console.log('existingImages:', existingImages);
console.log('deletedImageIds:', deletedImageIds);
console.log('uploadedImages:', uploadedImages);