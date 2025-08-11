/**
 * ==========================================================================
 * 상품 상세 페이지 JavaScript (메인 기능)
 * 이미지 갤러리, 장바구니 기능, 모달 관리 등
 * 외부 레이아웃과의 충돌 방지를 위해 선택자 수정
 * @history
 *  - 250731 | sehui | 에코마켓 상품 상세 페이지 요청 기능 추가
 *  - 250731 | sehui | 헤더/푸터 충돌 방지 및 이미지 처리 개선
 *  - 250801 | sehui | 주문 수량 유효성 검사 JS 코드 추가
 *  - 250801 | sehui | 구매하기 purchaseProduct() 함수 수정
 *  - 250801 | sehui | 에코마켓과 구매하기 버튼 제거
 *  - 250801 | sehui | 주문 수량 오류 메시지 위치 수정
 *  - 250801 | sehui | 주요 오류 수정 및 안정성 개선
 *  - 250805 | sehui | 장바구니 사용자ID별 생성할 수 있도록 개선
 *  - 250805 | sehui | 상품의 판매상태가 "품절"일 경우 장바구니 담기 및 구매하기 기능 비활성화
 * ==========================================================================
 */

/* ==========================================================================
   전역 변수 및 설정
   ========================================================================== */
// 이미지 관련 변수 (백엔드 데이터와 연동)
let currentImageIndex = 0;
let productImages = [];

// 장바구니 관련 변수
let cartItems = [];

// 로그인 상태 관리
let isLoggedIn = !!document.body.dataset.memberId;
let currentUser = document.body.dataset.memberId || null;

/* ==========================================================================
   페이지 초기화
   ========================================================================== */

document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 상품 상세 페이지 메인 기능 로딩 시작');
    
    // 기본 초기화
    initializePage();

    //상품 상세 데이터 로드
    loadProductDetails();
    
    // 장바구니 데이터 로드
    loadCartFromStorage();

    // 로그인 상태 확인
    checkLoginStatus();

    // 이벤트 리스너 설정
    setupEventListeners();
    
    // 실시간 유효성 검사 설정
    setupRealtimeValidation();

    console.log('✅ 상품 상세 페이지 메인 기능 로딩 완료!');
});

function initializePage() {
    console.log('⚙️ 페이지 기본 설정 초기화...');
    
    // 모바일 환경에서의 터치 이벤트 처리
    if ('ontouchstart' in window) {
        document.body.classList.add('touch-device');
    }
    
    // 페이지 로드 완료 후 이미지 갤러리 초기화
    setTimeout(() => {
        initializeImageGallery();
    }, 500);
}

/* ==========================================================================
   상품 상세 정보 로드
   ========================================================================== */
/**
 * 상품 상세 정보 요청 함수
 */
function loadProductDetails(){
    //서버가 model로 전달한 itemId 가져오기
    const itemId = document.getElementById('itemId').value;

    if (!itemId) {
        console.error('❌ 상품 ID가 없습니다.');
        showNotification('상품 정보를 찾을 수 없습니다.', 'error');
        return;
    }

    console.log('📦 상품 ID:', itemId);

    //API 호출하여 상품 데이터 가져오기
    fetch(`/api/eco/${itemId}`)
        .then(response => {
            if(!response.ok) {
                throw new Error(`네트워크 응답 오류: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            console.log('✅ 서버 응답 데이터:', data);

            const item = data.item;
            const role = data.role;
            const categories = data.categories;

            // 기본 정보 검증
            if (!item) {
                throw new Error('상품 데이터가 없습니다.');
            }

            // 상품명, 가격, 재고 수량, 판매 상태 설정
            updateProductBasicInfo(item);

            // 카테고리 정보 설정
            updateProductCategory(item, categories);

            // 상품 이미지 설정
            updateProductImages(item);

            // 상품 설명 설정
            updateProductDescription(item);

            // 관리자 메뉴 표시 여부 결정
            updateAdminMenu(role);

            console.log('✅ 상품 상세 정보 로딩 완료!');
        })
        .catch(error => {
            console.error("❌ 상품 상세 정보 데이터 로드 실패:", error);
            showNotification("상품 정보를 불러오지 못했습니다.", 'error');

            // 에러 시 기본 정보라도 표시
            setDefaultProductInfo();
        });

}

/**
 * 상품 기본 정보 업데이트
 */
function updateProductBasicInfo(item) {
    try {
        // 상품명
        const titleElement = document.getElementById('productTitle');
        if (titleElement) {
            titleElement.textContent = item.itemNm || '상품명 없음';
        }

        // 가격
        const priceElement = document.getElementById('currentPrice');
        if (priceElement && item.price !== undefined) {
            priceElement.textContent = item.price.toLocaleString() + '원';
        }

        // 재고 수량
        const stockElement = document.getElementById('stockNumber');
        if (stockElement && item.stockNumber !== undefined) {
            stockElement.textContent = item.stockNumber.toLocaleString() + '개';
        }

        // 판매 상태
        const statusElement = document.getElementById('productStatus');
        if (statusElement) {
            const statusText = getStatusText(item.itemSellStatus);
            const statusClass = getStatusClass(item.itemSellStatus);

            statusElement.textContent = statusText;
            statusElement.className = `product-status-badge ${statusClass}`;
        }

        console.log('✅ 기본 정보 업데이트 완료');
    } catch (error) {
        console.error('❌ 기본 정보 업데이트 실패:', error);
    }
}

/**
 * 상품 카테고리 정보 업데이트
 */
function updateProductCategory(item, categories) {
    try {
        const categoryElement = document.getElementById('productCategory');
        const breadcrumbCategoryElement = document.getElementById('breadcrumbCategory');

        if (!categories || !Array.isArray(categories)) {
            console.warn('⚠️ 카테고리 데이터가 없습니다.');
            return;
        }

        // item.categoryId와 일치하는 categoryName 찾기
        const matchedCategory = categories.find(cat => cat.categoryId === item.categoryId);
        const categoryName = matchedCategory ? matchedCategory.categoryName : "알 수 없음";

        if (categoryElement) {
            categoryElement.textContent = categoryName;
        }

        if (breadcrumbCategoryElement) {
            breadcrumbCategoryElement.textContent = categoryName;
        }

        console.log('✅ 카테고리 정보 업데이트 완료:', categoryName);
    } catch (error) {
        console.error('❌ 카테고리 업데이트 실패:', error);
    }
}

/**
 * 상품 이미지 업데이트
 */
function updateProductImages(item) {
    try {
        const imgList = item.itemImgDtoList;
        const mainImageTag = document.getElementById('mainImageTag');
        const mainImageContainer = document.getElementById('mainImage');
        const thumbnailList = document.getElementById('thumbnailList');

        if (!imgList || !Array.isArray(imgList) || imgList.length === 0) {
            console.warn('⚠️ 상품 이미지가 없습니다.');
            setDefaultImage(mainImageContainer);
            return;
        }

        // 메인 이미지로 첫 번째 이미지 사용
        const firstImage = imgList[0];
        if (mainImageTag && firstImage && firstImage.imgUrl) {
            mainImageTag.src = firstImage.imgUrl;
            mainImageTag.alt = firstImage.oriImgName || '상품 메인 이미지';
            mainImageTag.style.display = 'block';

            // 이미지 로드 완료 시 컨테이너 스타일 조정
            mainImageTag.onload = function() {
                if (mainImageContainer) {
                    mainImageContainer.style.fontSize = '0'; // 이모지 숨기기
                }
            };

            // 이미지 로드 실패 시 기본 이미지 표시
            mainImageTag.onerror = function() {
                console.warn('⚠️ 메인 이미지 로드 실패:', firstImage.imgUrl);
                setDefaultImage(mainImageContainer);
            };
        }

        // 썸네일 이미지 생성 (두 번째 이미지부터)
        if (thumbnailList) {
            thumbnailList.innerHTML = '';

            // 전체 이미지 배열을 전역 변수에 저장 (모달용)
            window.productImages = imgList.map(img => img.imgUrl);
            window.currentImageIndex = 0;

            // 썸네일 생성 (전체 이미지를 썸네일로 사용)
            for(let i = 0; i < imgList.length; i++) {
                const imgDto = imgList[i];
                const thumbContainer = document.createElement('div');
                thumbContainer.classList.add('thumbnail');
                if (i === 0) thumbContainer.classList.add('active');

                const thumbImg = document.createElement('img');
                thumbImg.src = imgDto.imgUrl;
                thumbImg.alt = imgDto.oriImgName || '상품 썸네일';
                thumbImg.style.width = '100%';
                thumbImg.style.height = '100%';
                thumbImg.style.objectFit = 'cover';

                // 썸네일 클릭 시 메인 이미지 변경
                thumbContainer.addEventListener('click', () => {
                    changeMainImage(i, imgList);
                    updateThumbnailActive(i);
                });

                // 이미지 로드 실패 시 기본 아이콘 표시
                thumbImg.onerror = function() {
                    thumbContainer.innerHTML = '🖼️';
                    thumbContainer.style.fontSize = '24px';
                };

                thumbContainer.appendChild(thumbImg);
                thumbnailList.appendChild(thumbContainer);
            }
        }

        console.log('✅ 이미지 업데이트 완료:', imgList.length + '개 이미지');
    } catch (error) {
        console.error('❌ 이미지 업데이트 실패:', error);
        setDefaultImage(document.getElementById('mainImage'));
    }
}

/**
 * 메인 이미지 변경
 */
function changeMainImage(index, imgList) {
    try {
        const mainImageTag = document.getElementById('mainImageTag');
        const mainImageContainer = document.getElementById('mainImage');

        if (mainImageTag && imgList[index]) {
            window.currentImageIndex = index;
            mainImageTag.src = imgList[index].imgUrl;
            mainImageTag.alt = imgList[index].oriImgName || '상품 이미지';

            mainImageTag.onload = function() {
                if (mainImageContainer) {
                    mainImageContainer.style.fontSize = '0';
                }
            };

            mainImageTag.onerror = function() {
                setDefaultImage(mainImageContainer);
            };
        }
    } catch (error) {
        console.error('❌ 메인 이미지 변경 실패:', error);
    }
}

/**
 * 썸네일 활성화 상태 업데이트
 */
function updateThumbnailActive(activeIndex) {
    try {
        const thumbnails = document.querySelectorAll('.thumbnail');
        thumbnails.forEach((thumb, index) => {
            thumb.classList.toggle('active', index === activeIndex);
        });
    } catch (error) {
        console.error('❌ 썸네일 활성화 업데이트 실패:', error);
    }
}

/**
 * 기본 이미지 설정
 */
function setDefaultImage(container) {
    if (container) {
        container.innerHTML = '📦';
        container.style.fontSize = '120px';
        const img = container.querySelector('img');
        if (img) {
            img.style.display = 'none';
        }
    }
}

/**
 * 상품 설명 업데이트 - 이미지 정렬 CSS 개선
 */
function updateProductDescription(item) {
    try {
        const desc = item.itemDetail || '';
        const container = document.getElementById('productDescription');

        if (!container) {
            console.warn('⚠️ 상품 설명 컨테이너를 찾을 수 없습니다.');
            return;
        }

        container.innerHTML = '';

        if (!desc.trim()) {
            container.innerHTML = '<p>상품 설명이 없습니다.</p>';
            return;
        }

        // 줄바꿈 기준으로 나눔
        const paragraphs = desc.split('\n');

        // 각 줄마다 <p> 태그로 감싸서 추가
        paragraphs.forEach(line => {
            const p = document.createElement('p');
            p.textContent = line.trim();
            if (line.trim()) { // 빈 줄이 아닌 경우만 추가
                container.appendChild(p);
            }
        });

        // 추가 이미지가 있는 경우 설명에 추가 - 이미지 정렬 및 스타일 개선
        if (item.itemImgDtoList && item.itemImgDtoList.length > 1) {
            const additionalImages = item.itemImgDtoList.slice(1); // 첫 번째 제외

            additionalImages.forEach((imgDto, index) => {
                if (imgDto.imgUrl) {
                    // 이미지 컨테이너 div 생성 - 중앙 정렬을 위해
                    const imageContainer = document.createElement('div');
                    imageContainer.style.cssText = `
                        text-align: center;
                        margin: 30px 0;
                        padding: 10px;
                    `;

                    // 이미지 요소 생성
                    const img = document.createElement('img');
                    img.src = imgDto.imgUrl;
                    img.alt = imgDto.oriImgName || `상품 이미지 ${index + 2}`;
                    img.classList.add('product-description-image');

                    // 이미지 스타일 설정 - 반응형 및 중앙 정렬
                    img.style.cssText = `
                        max-width: 100%;
                        height: auto;
                        border-radius: 12px;
                        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
                        display: block;
                        margin: 0 auto;
                        transition: transform 0.3s ease, box-shadow 0.3s ease;
                        cursor: pointer;
                    `;

                    // 이미지 호버 효과 추가
                    img.addEventListener('mouseenter', function() {
                        this.style.transform = 'scale(1.02)';
                        this.style.boxShadow = '0 8px 30px rgba(0, 0, 0, 0.2)';
                    });

                    img.addEventListener('mouseleave', function() {
                        this.style.transform = 'scale(1)';
                        this.style.boxShadow = '0 4px 20px rgba(0, 0, 0, 0.15)';
                    });

                    // 이미지 클릭 시 확대 기능 (기존 모달 재사용)
                    img.addEventListener('click', function() {
                        if (window.openImageModal) {
                            // 클릭한 이미지의 인덱스 설정
                            window.currentImageIndex = index + 1; // 첫 번째 이미지 다음부터
                            window.openImageModal();
                        }
                    });

                    // 이미지 로드 실패 시 처리
                    img.onerror = function() {
                        // 로드 실패 시 컨테이너 자체를 숨김
                        imageContainer.style.display = 'none';
                        console.warn('⚠️ 상품 설명 이미지 로드 실패:', imgDto.imgUrl);
                    };

                    // 이미지를 컨테이너에 추가
                    imageContainer.appendChild(img);

                    // 컨테이너를 설명 영역에 추가
                    container.appendChild(imageContainer);
                }
            });
        }

        console.log('✅ 상품 설명 업데이트 완료 - 이미지 정렬 개선됨');
    } catch (error) {
        console.error('❌ 상품 설명 업데이트 실패:', error);
    }
}

/* ==========================================================================
   추가 기능 관련 함수
   ========================================================================== */
/**
 * 관리자 메뉴 표시 업데이트
 */
function updateAdminMenu(role) {
    try {
        const productMenu = document.querySelector('.product-menu');

        if (productMenu) {
            // ROLE_ADMIN인 경우에만 메뉴 표시
            if (role === 'ADMIN') {
                productMenu.style.display = 'block';
                console.log('✅ 관리자 메뉴 표시');
            } else {
                productMenu.style.display = 'none';
                console.log('ℹ️ 관리자 메뉴 숨김');
            }
        }
    } catch (error) {
        console.error('❌ 관리자 메뉴 업데이트 실패:', error);
    }
}

/**
 * 판매 상태 텍스트 반환
 */
function getStatusText(status) {
    switch(status) {
        case 'SELL': return '판매중';
        case 'SOLD_OUT': return '품절';
        default: return '판매 상태';
    }
}

/**
 * 판매 상태 CSS 클래스 반환
 */
function getStatusClass(status) {
    switch(status) {
        case 'SELL': return 'available';
        case 'SOLD_OUT': return 'sold';
        default: return 'available';
    }
}

/**
 * 기본 상품 정보 설정 (에러 시)
 */
function setDefaultProductInfo() {
    try {
        const titleElement = document.getElementById('productTitle');
        if (titleElement) {
            titleElement.textContent = '상품 정보를 불러올 수 없습니다';
        }

        const priceElement = document.getElementById('currentPrice');
        if (priceElement) {
            priceElement.textContent = '가격 정보 없음';
        }

        const stockElement = document.getElementById('stockNumber');
        if (stockElement) {
            stockElement.textContent = '0개';
        }

        const categoryElement = document.getElementById('productCategory');
        if (categoryElement) {
            categoryElement.textContent = '카테고리 없음';
        }

        setDefaultImage(document.getElementById('mainImage'));

        console.log('⚠️ 기본 상품 정보 설정 완료');
    } catch (error) {
        console.error('❌ 기본 상품 정보 설정 실패:', error);
    }
}


/* ==========================================================================
   이미지 갤러리 관리 (백엔드 데이터와 연동)
   ========================================================================== */

function initializeImageGallery() {
    // productImages는 백엔드에서 로드된 데이터 사용
    if (window.productImages && window.productImages.length > 0) {
        productImages = window.productImages;
        currentImageIndex = window.currentImageIndex || 0;
        
        setupImageEvents();
        console.log('🖼️ 이미지 갤러리 초기화 완료:', productImages.length + '개 이미지');
    } else {
        console.log('ℹ️ 이미지 데이터 대기 중...');
        // 데이터가 로드될 때까지 재시도 (최대 10회)
        let retryCount = 0;
        const maxRetries = 10;
        
        const retryInterval = setInterval(() => {
            retryCount++;
            if (window.productImages && window.productImages.length > 0) {
                productImages = window.productImages;
                currentImageIndex = window.currentImageIndex || 0;
                setupImageEvents();
                console.log('🖼️ 이미지 갤러리 초기화 완료:', productImages.length + '개 이미지');
                clearInterval(retryInterval);
            } else if (retryCount >= maxRetries) {
                console.warn('⚠️ 이미지 데이터 로드 시간 초과');
                clearInterval(retryInterval);
            }
        }, 1000);
    }
}

function setupImageEvents() {
    // 이미지 확대 버튼 이벤트
    const imageZoomBtn = document.getElementById('imageZoomBtn');
    if (imageZoomBtn) {
        imageZoomBtn.addEventListener('click', openImageModal);
    }
    
    // 메인 이미지 클릭 시 확대
    const mainImage = document.getElementById('mainImage');
    if (mainImage) {
        mainImage.addEventListener('click', openImageModal);
        mainImage.style.cursor = 'zoom-in';
    }
    
    // 모달 관련 이벤트
    setupModalEvents();
}

function setupModalEvents() {
    // 모달 닫기 버튼
    const closeImageModal = document.getElementById('closeImageModal');
    if (closeImageModal) {
        closeImageModal.addEventListener('click', closeImageModalHandler);
    }
    
    // 이미지 네비게이션 버튼
    const prevImageBtn = document.getElementById('prevImageBtn');
    const nextImageBtn = document.getElementById('nextImageBtn');
    
    if (prevImageBtn) {
        prevImageBtn.addEventListener('click', showPreviousImage);
    }
    
    if (nextImageBtn) {
        nextImageBtn.addEventListener('click', showNextImage);
    }
    
    // 모달 외부 클릭 시 닫기
    const imageModal = document.getElementById('imageModal');
    if (imageModal) {
        imageModal.addEventListener('click', (e) => {
            if (e.target === imageModal) {
                closeImageModalHandler();
            }
        });
    }
    
    // 키보드 이벤트
    document.addEventListener('keydown', handleKeyboardNavigation);
}

/* ==========================================================================
   이미지 모달 기능
   ========================================================================== */

function openImageModal() {
    const imageModal = document.getElementById('imageModal');
    if (imageModal && productImages && productImages.length > 0) {
        imageModal.classList.add('show');
        document.body.style.overflow = 'hidden';
        updateModalImage();
        updateImageNavigation();
        console.log('🖼️ 이미지 모달 열기');
    } else {
        console.warn('⚠️ 이미지 모달을 열 수 없습니다. 이미지 데이터를 확인하세요.');
    }
}

function closeImageModalHandler() {
    const imageModal = document.getElementById('imageModal');
    if (imageModal) {
        imageModal.classList.remove('show');
        document.body.style.overflow = 'auto';
        console.log('🖼️ 이미지 모달 닫기');
    }
}

function updateModalImage() {
    const modalMainImage = document.getElementById('modalMainImage');
    const imageCounter = document.getElementById('imageCounter');
    
    if (modalMainImage && productImages && productImages[currentImageIndex]) {
        const img = modalMainImage.querySelector('img');
        if (img) {
            img.src = productImages[currentImageIndex];
            img.style.display = 'block';
            img.alt = `상품 이미지 ${currentImageIndex + 1}`;
        } else {
            modalMainImage.innerHTML = `<img src="${productImages[currentImageIndex]}" alt="상품 이미지 ${currentImageIndex + 1}" style="width: 100%; height: 100%; object-fit: contain;">`;
        }
    }
    
    if (imageCounter && productImages) {
        imageCounter.textContent = `${currentImageIndex + 1} / ${productImages.length}`;
    }
}

function showPreviousImage() {
    if (productImages && currentImageIndex > 0) {
        currentImageIndex--;
        updateModalImage();
        updateImageNavigation();
        
        // 메인 이미지도 업데이트
        if (window.changeMainImage && typeof window.changeMainImage === 'function') {
            window.changeMainImage(currentImageIndex, productImages);
        }
        if (window.updateThumbnailActive && typeof window.updateThumbnailActive === 'function') {
            window.updateThumbnailActive(currentImageIndex);
        }
        
        console.log('⬅️ 이전 이미지:', currentImageIndex + 1);
    }
}

function showNextImage() {
    if (productImages && currentImageIndex < productImages.length - 1) {
        currentImageIndex++;
        updateModalImage();
        updateImageNavigation();
        
        // 메인 이미지도 업데이트
        if (window.changeMainImage && typeof window.changeMainImage === 'function') {
            window.changeMainImage(currentImageIndex, productImages);
        }
        if (window.updateThumbnailActive && typeof window.updateThumbnailActive === 'function') {
            window.updateThumbnailActive(currentImageIndex);
        }
        
        console.log('➡️ 다음 이미지:', currentImageIndex + 1);
    }
}

function updateImageNavigation() {
    const prevImageBtn = document.getElementById('prevImageBtn');
    const nextImageBtn = document.getElementById('nextImageBtn');
    
    if (prevImageBtn) {
        prevImageBtn.disabled = currentImageIndex === 0;
    }
    if (nextImageBtn && productImages) {
        nextImageBtn.disabled = currentImageIndex === productImages.length - 1;
    }
}

function handleKeyboardNavigation(e) {
    const imageModal = document.getElementById('imageModal');
    if (imageModal && imageModal.classList.contains('show')) {
        switch (e.key) {
            case 'ArrowLeft':
                e.preventDefault();
                showPreviousImage();
                break;
            case 'ArrowRight':
                e.preventDefault();
                showNextImage();
                break;
            case 'Escape':
                e.preventDefault();
                closeImageModalHandler();
                break;
        }
    }
}

/* ==========================================================================
   폼 유효성 검사 함수
   ========================================================================== */

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
    
    // 주문 수량 입력창에 실시간 검증 추가
    const orderInput = document.getElementById('orderNumber');
    if (orderInput) {
        orderInput.addEventListener('input', function() {
            // 숫자만 입력 허용
            this.value = this.value.replace(/[^0-9]/g, '');
            
            // 실시간 유효성 검사
            if (this.value && parseInt(this.value) > 0) {
                validateOrderQuantity();
            }
        });
    }
}

// 개별 필드 유효성 검사
function validateField(field) {
    if (!field) return false;
    
    const value = field.value.trim();
    const isRequired = field.hasAttribute('required');

    if (isRequired && !value) {
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
    if (!field) return;
    
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
    
    // 부모 노드에 추가
    if (field.parentNode) {
        field.parentNode.appendChild(errorDiv);
    }
}

// 필드 에러 제거
function clearFieldError(field) {
    if (!field) return;
    
    field.classList.remove('error');

    const errorMessage = field.parentNode ? field.parentNode.querySelector('.error-message') : null;
    if (errorMessage) {
        errorMessage.remove();
    }
}

// 주문 수량 검증 (재고 수량 초과 여부) - 오류 메시지 위치 수정
function validateOrderQuantity() {
    const orderInput = document.getElementById('orderNumber');
    const stockNumberElement = document.getElementById('stockNumber');
    
    if (!orderInput || !stockNumberElement) {
        console.warn('⚠️ 주문 수량 또는 재고 수량 요소를 찾을 수 없습니다.');
        return false;
    }
    
    const stockText = stockNumberElement.textContent || '0';
    const stock = parseInt(stockText.replace(/[^0-9]/g, '')) || 0;
    const value = parseInt(orderInput.value) || 0;

    // 기존 에러 메시지 먼저 제거
    clearOrderError(orderInput);

    if (value < 1) {
        showOrderError(orderInput, "1개 이상 입력해주세요.");
        return false;
    }

    if (value > stock) {
        showOrderError(orderInput, "재고 수량을 초과할 수 없습니다.");
        return false;
    }

    // 성공 시 에러 메시지 제거
    clearOrderError(orderInput);
    orderInput.classList.add('success');
    return true;
}

// 주문 수량 전용 에러 표시 함수 - 올바른 위치에 에러 메시지 표시
function showOrderError(field, message) {
    if (!field) return;
    
    field.classList.add('error');
    field.classList.remove('success');

    // .form-input-section 컨테이너 찾기
    const inputSection = field.closest('.form-input-section');
    if (!inputSection) {
        console.warn('⚠️ .form-input-section 컨테이너를 찾을 수 없습니다.');
        return;
    }

    // stockError ID를 가진 요소 찾기
    const existingError = inputSection.querySelector('#stockError');
    if (existingError) {
        existingError.textContent = message;
        existingError.style.display = 'block';
    } else {
        console.warn('⚠️ #stockError 요소를 찾을 수 없습니다.');
    }
}

// 주문 수량 전용 에러 제거 함수
function clearOrderError(field) {
    if (!field) return;
    
    field.classList.remove('error');

    // .form-input-section 컨테이너 찾기
    const inputSection = field.closest('.form-input-section');
    if (!inputSection) return;

    // stockError 요소 숨기기
    const errorElement = inputSection.querySelector('#stockError');
    if (errorElement) {
        errorElement.textContent = '';
        errorElement.style.display = 'none';
    }
}

// 전체 폼 유효성 검사
function validateForm() {
    let isValid = true;
    const requiredFields = ['orderNumber'];

    requiredFields.forEach(function(fieldId) {
        const field = document.getElementById(fieldId);
        if (field && !validateField(field)) {
            isValid = false;
        }
    });

    // 주문 수량 유효성 체크
    if (!validateOrderQuantity()) {
        isValid = false;
    }

    return isValid;
}

/* ==========================================================================
   장바구니 기능
   ========================================================================== */

/**
 * 사용자별 장바구니 키
 */
function getCartKey(){
    if(!currentUser) {
        throw new Error("⚠️ 로그인한 사용자만 장바구니를 사용할 수 있습니다.");
    }
    return `ecomarket_cart_${currentUser}`;
}

function loadCartFromStorage() {
    try {
        // localStorage 사용 가능 여부 확인
        if (typeof Storage === "undefined") {
            console.warn('⚠️ 브라우저가 localStorage를 지원하지 않습니다.');
            cartItems = [];
            return;
        }

        const key = getCartKey();
        const savedCart = localStorage.getItem(key);

        if (savedCart) {
            cartItems = JSON.parse(savedCart);
            console.log(`🛒 장바구니(${key}) 로드 완료:`, cartItems.length + '개 상품');
        } else {
            cartItems = [];
        }
    } catch (error) {
        console.error('❌ 장바구니 로드 실패:', error);
        cartItems = [];
    }
}

function saveCartToStorage() {
    try {
        if (typeof Storage === "undefined") {
            console.warn('⚠️ 브라우저가 localStorage를 지원하지 않습니다.');
            return;
        }

        const key = getCartKey();
        localStorage.setItem(key, JSON.stringify(cartItems));
        console.log(`💾 장바구니(${key}) 저장 완료`);
    } catch (error) {
        console.error('❌ 장바구니 저장 실패:', error);
    }
}

async function addToCart() {
    try {
        console.log("🔧 장바구니 담기 버튼 이벤트 실행");
        // 로그인 확인
        if (!isLoggedIn) {
            showNotification('로그인 후 이용해주세요.', 'warning');
            return;
        }

        //판매 상태 확인
        const sellStatusEl = document.getElementById('productStatus');
        const sellStatus = sellStatusEl?.textContent.trim();

        if(!sellStatus || sellStatus === "품절"){
            showNotification("이 상품은 판매가 완료되었습니다.", 'warning');
            return;
        }

        // 상품 정보 확인
        const itemId = document.getElementById('itemId');
        const productTitle = document.getElementById('productTitle');
        const stockNumber = document.getElementById('stockNumber');
        const orderNumber = document.getElementById('orderNumber');

        if (!itemId || !itemId.value) {
            showNotification('상품 정보를 불러오는 중입니다...', 'warning');
            return;
        }

        if (!productTitle || !productTitle.textContent) {
            showNotification('상품 정보를 불러오는 중입니다...', 'warning');
            return;
        }

        if (!orderNumber || !orderNumber.value) {
            showNotification('주문 수량을 입력해주세요.', 'warning');
            return;
        }

        // 재고 확인
        const stockText = stockNumber ? stockNumber.textContent : '0';
        const stock = parseInt(stockText.replace(/[^0-9]/g, '')) || 0;
        if (stock <= 0) {
            showNotification('재고가 부족합니다.', 'error');
            return;
        }

        // 폼 유효성 검사
        if (!validateForm()) {
            showNotification("주문 수량을 다시 확인해주세요.", 'error');
            return;
        }

        // 장바구니에 추가 - 서버 요청
        const response = await fetch('/cart/add', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: new URLSearchParams({
                itemId: itemId.value,
                count: orderNumber.value
            })
        });

        if (response.ok) {
            const result = await response.text();
            showNotification(`🛒 장바구니에 담았습니다!\n"${productTitle.textContent}"`, 'cart');
            console.log('🛒 장바구니 추가 : ', result);

            // 로컬 스토리지에도 추가
            cartItems.push({
                id: itemId.value,
                title: productTitle.textContent,
                count: orderNumber.value
            });
            saveCartToStorage();
        } else {
            const errText = await response.text();
            throw new Error(errText || '서버 오류');
        }

        // 버튼 애니메이션
        const cartBtn = document.getElementById('cartBtn');
        if (cartBtn) {
            cartBtn.classList.add('animate');
            setTimeout(() => cartBtn.classList.remove('animate'), 600);
        }

    } catch (error) {
        console.error('❌ 장바구니 추가 실패:', error);
        showNotification('장바구니 추가 중 오류가 발생했습니다.', 'error');
    }
}

/* ==========================================================================
   구매하기 기능
   ========================================================================== */

async function purchaseProduct(e) {
    if (e) {
        e.preventDefault();
    }

    console.log("🔧 구매하기 버튼 이벤트 실행");

    try {
        // 로그인 확인
        if (!isLoggedIn) {
            showNotification('로그인 후 이용해주세요.', 'warning');
            return;
        }

        //판매 상태 확인
        const sellStatusEl = document.getElementById('productStatus');
        const sellStatus = sellStatusEl?.textContent.trim();

        if(!sellStatus || sellStatus === "품절"){
            showNotification("이 상품은 판매가 완료되었습니다.", 'warning');
            return;
        }

        const orderForm = document.getElementById('form-body');
        const itemId = document.getElementById('itemId');
        const productTitle = document.getElementById('productTitle');
        const productPrice = document.getElementById('currentPrice');
        const stockNumber = document.getElementById('stockNumber');
        
        if (!itemId || !itemId.value || !productTitle || !productTitle.textContent) {
            showNotification('상품 정보를 불러오는 중입니다.', 'warning');
            return;
        }
        
        // 재고 확인
        const stockText = stockNumber ? stockNumber.textContent : '0';
        const stock = parseInt(stockText.replace(/[^0-9]/g, '')) || 0;
        if (stock <= 0) {
            showNotification('재고가 부족합니다.', 'error');
            return;
        }

        // 폼 유효성 검사
        if (!validateForm()) {
            showNotification("주문 수량을 다시 확인해주세요.", 'error');
            return;
        }
        
        const priceText = productPrice ? productPrice.textContent : '가격 정보 없음';
        const confirmPurchase = confirm(`상품명: ${productTitle.textContent}\n가격: ${priceText}\n\n구매하시겠습니까?`);

        if (confirmPurchase) {
            showNotification('구매 절차를 진행합니다...', 'info');
            console.log('💰 구매 진행:', productTitle.textContent);
            
            // 실제 결제 페이지로 이동
            if (orderForm) {
                orderForm.method = "POST";
                orderForm.action = "/order/prepare";
                orderForm.submit();
            } else {
                console.error('❌ 주문 폼을 찾을 수 없습니다.');
                showNotification('주문 처리 중 오류가 발생했습니다.', 'error');
            }
        }
        
    } catch (error) {
        console.error('❌ 구매 처리 실패:', error);
        showNotification('구매 처리 중 오류가 발생했습니다.', 'error');
    }
}

/* ==========================================================================
   수정하기 버튼
   ========================================================================== */
function handleEditButtonClick() {
    const editBtn = document.getElementById('editProduct');
    const itemId = editBtn.getAttribute('data-item-id');

    if (itemId) {
        window.location.href = `/eco/modify/${itemId}`;
    } else {
        alert("상품 ID를 찾을 수 없습니다.");
    }
}

/* ==========================================================================
   로그인 상태 관리
   ========================================================================== */

function checkLoginStatus() {
    // 서버사이드에서 전달된 로그인 정보 확인
    const actionButtons = document.getElementById('actionButtons');
    if (actionButtons) {
        const isLoggedInFromServer = actionButtons.getAttribute('data-logged-in') === 'true';
        
        if (isLoggedInFromServer) {
            isLoggedIn = true;
            console.log('✅ 로그인 상태 확인됨');
        } else {
            isLoggedIn = false;
            console.log('ℹ️ 비로그인 상태');
        }
    } else {
        console.warn('⚠️ actionButtons 요소를 찾을 수 없습니다.');
        isLoggedIn = false;
    }
    
    updateActionButtons();
}

function updateActionButtons() {
    const cartBtn = document.getElementById('cartBtn');
    const buyBtn = document.getElementById('buyBtn');
    
    if (!isLoggedIn) {
        // 비로그인 시 버튼 클릭 시 로그인 페이지로 이동
        [cartBtn, buyBtn].forEach(btn => {
            if (btn) {
                btn.addEventListener('click', (e) => {
                    e.preventDefault();
                    showNotification('로그인이 필요한 서비스입니다.', 'warning');
                    setTimeout(() => {
                        window.location.href = '/members/login';
                    }, 1500);
                });
            }
        });
    }
}

/* ==========================================================================
   이벤트 리스너 설정
   ========================================================================== */

function setupEventListeners() {
    console.log('🔧 이벤트 리스너 설정...');
    
    // 장바구니 버튼
    const cartBtn = document.getElementById('cartBtn');
    if (cartBtn && isLoggedIn) {
        cartBtn.addEventListener('click', addToCart);
    }
    
    // 구매하기 버튼
    const buyBtn = document.getElementById('buyBtn');
    if (buyBtn && isLoggedIn) {
        buyBtn.addEventListener('click', purchaseProduct);
    }
    
    // 공유 버튼들
    const shareButtons = document.querySelectorAll('.share-btn');
    shareButtons.forEach(btn => {
        btn.addEventListener('click', handleShare);
    });
    
    // 상품 메뉴 (관리자용)
    const menuToggle = document.getElementById('menuToggle');
    const menuDropdown = document.getElementById('menuDropdown');
    
    if (menuToggle && menuDropdown) {
        menuToggle.addEventListener('click', (e) => {
            e.stopPropagation();
            menuDropdown.classList.toggle('show');
        });
        
        // 메뉴 외부 클릭 시 닫기
        document.addEventListener('click', () => {
            menuDropdown.classList.remove('show');
        });
    }

    //상품 수정 버튼
    const editBtn = document.getElementById('editProduct');
    if(editBtn) {
        editBtn.addEventListener('click', handleEditButtonClick);
    }
    
    console.log('✅ 이벤트 리스너 설정 완료');
}

/* ==========================================================================
   공유 기능
   ========================================================================== */

function handleShare(e) {
    const shareType = e.target.getAttribute('data-type');
    const currentUrl = window.location.href;
    const productTitle = document.getElementById('productTitle');
    const title = productTitle ? productTitle.textContent : '상품 정보';
    
    console.log('📤 공유하기:', shareType);
    
    switch (shareType) {
        case 'link':
            if (navigator.clipboard) {
                navigator.clipboard.writeText(currentUrl).then(() => {
                    showNotification('링크가 복사되었습니다! 🔗', 'success');
                }).catch(() => {
                    showNotification('링크 복사에 실패했습니다.', 'error');
                });
            } else {
                // 클립보드 API를 지원하지 않는 브라우저
                showNotification('브라우저에서 지원하지 않는 기능입니다.', 'warning');
            }
            break;
            
        case 'kakao':
            // 카카오 공유 API 사용 (실제 구현시 카카오 SDK 필요)
            showNotification('카카오톡으로 공유됩니다...', 'info');
            console.log('💬 카카오톡 공유:', { url: currentUrl, title });
            break;
            
        case 'facebook':
            const facebookUrl = `https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(currentUrl)}`;
            window.open(facebookUrl, '_blank', 'width=600,height=400');
            break;
            
        case 'twitter':
            const twitterUrl = `https://twitter.com/intent/tweet?url=${encodeURIComponent(currentUrl)}&text=${encodeURIComponent(title)}`;
            window.open(twitterUrl, '_blank', 'width=600,height=400');
            break;
            
        default:
            console.warn('지원하지 않는 공유 타입:', shareType);
    }
}

/* ==========================================================================
   유틸리티 함수
   ========================================================================== */

function showNotification(message, type = 'info') {
    console.log('🔔 알림:', message, '(' + type + ')');
    
    // 기존 알림 제거
    const existingNotification = document.querySelector('.notification');
    if (existingNotification) {
        existingNotification.remove();
    }
    
    // 새 알림 생성
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    
    // 멀티라인 메시지 처리
    const lines = message.split('\n');
    if (lines.length > 1) {
        lines.forEach((line, index) => {
            const p = document.createElement('p');
            p.textContent = line;
            p.style.margin = index === 0 ? '0 0 5px 0' : '0';
            notification.appendChild(p);
        });
    } else {
        notification.textContent = message;
    }
    
    // 스타일 적용
    notification.style.cssText = `
        position: fixed;
        top: 100px;
        right: 20px;
        padding: 15px 20px;
        border-radius: 8px;
        box-shadow: 0 4px 20px rgba(0,0,0,0.1);
        z-index: 10000;
        transform: translateX(400px);
        transition: transform 0.3s ease;
        max-width: 300px;
        font-weight: 500;
        font-size: 14px;
        color: white;
        background: ${getNotificationColor(type)};
    `;
    
    document.body.appendChild(notification);
    
    // 애니메이션 표시
    setTimeout(() => {
        notification.style.transform = 'translateX(0)';
    }, 100);
    
    // 자동 제거
    const displayTime = type === 'cart' ? 5000 : 3000;
    setTimeout(() => {
        notification.style.transform = 'translateX(400px)';
        setTimeout(() => {
            if (notification && notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 300);
    }, displayTime);
}

function getNotificationColor(type) {
    const colors = {
        'success': '#27ae60',
        'error': '#e74c3c',
        'warning': '#f39c12',
        'cart': '#8e44ad',
        'info': '#3498db'
    };
    return colors[type] || colors.info;
}

/* ==========================================================================
   전역 함수 등록 (개발자 도구 및 다른 스크립트에서 사용)
   ========================================================================== */

// 전역 함수로 등록 - 안전하게 등록
window.updateProductBasicInfo = updateProductBasicInfo;
window.updateProductImages = updateProductImages;
window.changeMainImage = changeMainImage;
window.updateThumbnailActive = updateThumbnailActive;

if (typeof window !== 'undefined') {
    window.showNotification = showNotification;
    window.addToCart = addToCart;
    window.purchaseProduct = purchaseProduct;
    window.openImageModal = openImageModal;
    window.closeImageModalHandler = closeImageModalHandler;
    window.handleShare = handleShare;
    window.validateForm = validateForm;
    window.validateOrderQuantity = validateOrderQuantity;

    // 공유 함수 (HTML에서 직접 호출)
    window.shareProduct = function(type) {
        handleShare({ target: { getAttribute: () => type } });
    };
}

console.log('✅ 상품 상세 페이지 메인 스크립트 로딩 완료!');
console.log('='.repeat(60));
console.log('🔧 개발자 도구 명령어:');
console.log('• showNotification("메시지", "타입") - 알림 표시');
console.log('• addToCart() - 장바구니에 추가');
console.log('• purchaseProduct() - 상품 구매');
console.log('• openImageModal() - 이미지 모달 열기');
console.log('• validateForm() - 폼 유효성 검사');
console.log('='.repeat(60));