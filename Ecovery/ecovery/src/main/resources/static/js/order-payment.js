/**
 * 에코마켓 주문/결제 페이지 JavaScript
 * 주문 상품 정보를 JSON으로 가져와 주문 정보를 표시하고
 * 결제한 상품과 주문 정보를 서버에 저장하는 기능을 제공합니다.
 * @history
 *  - 250801 | sehui | 장바구니 기능 삭제
 *  - 250801 | sehui | 주문 상태, 배송 상태, 결제 정보 기능 삭제
 *  - 250801 | sehui | 배송조회, 상품 후기 작성, 재주문, 주문 문의, URL 파라미터 처리 함수 기능 삭제
 *  - 250802 | sehui | 카카오 주소 API 모달창 실행 함수 추가
 *  - 250802 | sehui | 이벤트 핸들러 함수 추가
 *  - 250802 | sehui | 결제 버튼 클릭 시 주문 정보 요청 함수 추가
 *  - 250802 | sehui | 결제 정보 금액 렌더링 함수 추가
 *  - 250802 | sehui | 입력값 유효성 겁사 함수 추가
 *  - 250804 | sehui | 포트원 결제 초기화 함수 추가
 *  - 250805 | sehui | 입력값 유효성 검사에 전화번호, 상세주소 형식 검증 추가
 *  - 250805 | sehui | 이벤트 핸들러 함수에 전화번호 자동 포맷팅 기능 추가
 *  - 250805 | sehui | 장바구니 기능 삭제
 *  - 250805 | sehui | 결제 취소할 경우 페이지 새로고침 기능 추가
 */

// ==========================================================================
// 전역 변수 선언
// ==========================================================================
let isInitialized = false;          // 초기화 상태 플래그

// DOM 요소들
const header = document.getElementById('header');
const hamburger = document.getElementById('hamburger');
const navMenu = document.getElementById('navMenu');

// ==========================================================================
// 페이지 초기화 - DOMContentLoaded 이벤트 리스너
// ==========================================================================
document.addEventListener('DOMContentLoaded', async function() {
    try {
        console.log('🛍️ GreenCycle 주문상세 페이지 초기화를 시작합니다...');

        // 핵심 기능 초기화
        initializeHeader();              // 헤더 기능 초기화
        initializeInteractions();        // 인터랙션 초기화
        initializeKeyboardShortcuts();   // 키보드 단축키 초기화
        adjustLayoutForScreenSize();     // 반응형 레이아웃 조정
        initializePortOne();             //포트원 결제 초기화

        //주문 데이터 확인
        await validateOrderData();

        isInitialized = true;

        setupEventListeners();      //이벤트 리스너 등록

    } catch (error) {
        handleError(error, '📄 주문 상세 페이지 초기화 중 오류 발생');
        window.location.href = '/mypage';
    }
});

// ==========================================================================
// 헤더 기능 초기화 (마이페이지와 동일)
// ==========================================================================
/**
 * 헤더 기능 초기화
 * 스크롤 효과, 모바일 메뉴 토글 등을 설정합니다
 */
function initializeHeader() {
    // 스크롤 시 헤더 효과 (디바운싱 적용)
    let scrollTimeout;
    window.addEventListener('scroll', () => {
        clearTimeout(scrollTimeout);
        scrollTimeout = setTimeout(() => {
            if (window.scrollY > 100) {
                header.classList.add('scrolled');
            } else {
                header.classList.remove('scrolled');
            }
        }, 10);
    });

    // 모바일 메뉴 토글 기능
    if (hamburger && navMenu) {
        hamburger.addEventListener('click', toggleMobileMenu);

        // 메뉴 링크 클릭 시 메뉴 닫기
        document.querySelectorAll('.nav-menu a').forEach(link => {
            link.addEventListener('click', closeMobileMenu);
        });

        // 메뉴 외부 클릭 시 닫기
        document.addEventListener('click', (e) => {
            if (!hamburger.contains(e.target) && !navMenu.contains(e.target)) {
                closeMobileMenu();
            }
        });
    }

    console.log('✅ 헤더 기능이 초기화되었습니다.');
}

/**
 * 모바일 메뉴 토글 함수
 */
function toggleMobileMenu() {
    const isActive = hamburger.classList.contains('active');

    if (isActive) {
        closeMobileMenu();
    } else {
        openMobileMenu();
    }
}

/**
 * 모바일 메뉴 열기
 */
function openMobileMenu() {
    hamburger.classList.add('active');
    navMenu.classList.add('active');
    document.body.style.overflow = 'hidden'; // 스크롤 방지

    // 햄버거 아이콘 애니메이션
    const spans = hamburger.querySelectorAll('span');
    spans[0].style.transform = 'rotate(45deg) translate(5px, 5px)';
    spans[1].style.opacity = '0';
    spans[2].style.transform = 'rotate(-45deg) translate(7px, -6px)';
}

/**
 * 모바일 메뉴 닫기
 */
function closeMobileMenu() {
    if (!hamburger || !navMenu) return;

    hamburger.classList.remove('active');
    navMenu.classList.remove('active');
    document.body.style.overflow = ''; // 스크롤 복구

    // 햄버거 아이콘 원상복구
    const spans = hamburger.querySelectorAll('span');
    spans[0].style.transform = 'none';
    spans[1].style.opacity = '1';
    spans[2].style.transform = 'none';
}

// ==========================================================================
// 포트원 결제 초기화
// ==========================================================================
/**
 * 포트원 결제 API 초기화 (페이지에서 1번만 실행)
  */
function initializePortOne(){
    if (typeof IMP !== 'undefined') {
        IMP.init("imp70187520");     //가맹점 식별코드
        console.log("✅ 포트원 결제 API 초기화 완료");
    } else {
        console.warn("❌ 포트원 결제 객체(IMP)를 찾을 수 없습니다.");
    }
}


// ==========================================================================
// 주문 데이터 로드 및 표시
// ==========================================================================
/**
 * 주문 데이터 확인
 */
async function validateOrderData(){
    const orderData = await loadOrderData();    //주문 데이터 로드

    if(!orderData || !orderData.orderUuid) {
        showNotification('잘못된 접근입니다. 마이페이지로 이동합니다.', 'warning');
        setTimeout(() => {
            window.location.href = '/mypage';
        }, 2000);
        throw new Error("❌ 유효하지 않은 주문 접근");
    }

    //주문 ID를 저장하고 안내 메시지
    const currentOrderUuid = orderData.orderUuid;
    console.log('🛍️ 주문상세 페이지가 성공적으로 초기화되었습니다.');

    // 환영 메시지 표시 (1초 후)
    setTimeout(() => {
        showNotification(`📋 주문번호 ${currentOrderUuid} 상세 정보를 불러왔습니다! `, 'success');
    }, 1000);

    return orderData;

}

/**
 * 주문 데이터를 로드하고 화면에 표시합니다
 */
async function loadOrderData() {
    try {
        console.log('🚀 주문 데이터 로드 시작...');

        let orderItemRequests;
        const allSelectedItems = sessionStorage.getItem("allSelectedItems");

        // 1. 장바구니에서 온 경우: sessionStorage의 데이터를 사용
        if (allSelectedItems) {
            orderItemRequests = JSON.parse(allSelectedItems);
            // 사용 후 데이터 삭제
            sessionStorage.removeItem("allSelectedItems");

            console.log('✅ sessionStorage에서 장바구니 주문 데이터를 불러왔습니다.');
        }
        // 2. 상세 페이지에서 온 경우: hidden input의 데이터를 사용
        else {
            const json = document.getElementById('orderItemRequests')?.value;
            if (!json || json.trim().length === 0) {
                throw new Error('❌ 주문 상품 정보를 찾을 수 없습니다.');
            }
            orderItemRequests = JSON.parse(json);

            console.log('✅ HTML hidden input에서 바로 구매 주문 데이터를 불러왔습니다.');
        }

        // OrderApiController.prepareOrder API 호출 (두 경우 모두 동일하게 동작)
        const response = await fetch('/api/order/prepare', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(orderItemRequests)
        });

        if (!response.ok) {
            throw new Error('❌ 주문 데이터를 불러오는 데 실패했습니다.');
        }

        const orderData = await response.json();
        window.orderDtoFromServer = orderData;
        displayOrderData(orderData);
        console.log(`✅ 주문 데이터 로드 완료`);

        return orderData;

    } catch (error) {
        handleError(error, 'Order data loading fail');
        // 에러 발생 시 마이페이지로 리다이렉트
        setTimeout(() => {
            window.location.href = '/mypage';
        }, 3000);
    }
}

/**
 * 주문 데이터를 화면에 표시합니다
 * @param {Object} data - 주문 데이터 객체
 */
function displayOrderData(data) {
    try {
        // 주문 기본 정보 표시
        displayBasicOrderInfo(data);

        // 주문 상품 정보 표시
        displayOrderProducts(data.orderItems);

        //결제 정보 표시
        displayPaymentSummary(data.orderItems);

        console.log('✅ 주문 정보 표시 완료');

    } catch (error) {
        handleError(error, 'Displaying order data');
    }
}

/**
 * 주문 기본 정보를 표시합니다
 * @param {Object} data - 주문 데이터
 */
function displayBasicOrderInfo(data) {
    // 주문번호
    const orderNumberEl = document.getElementById('orderNumber');
    if (orderNumberEl) orderNumberEl.textContent = data.orderUuid;

    // 주문일자
    const orderDateEl = document.getElementById('orderDate');
    if (orderDateEl) {
        const now = new Date();
        const formattedDate = `${now.getFullYear()}년 ${now.getMonth() + 1}월 ${now.getDate()}일`;
        orderDateEl.textContent = formattedDate;
    }
}

/**
 * 주문 상품 정보를 표시합니다
 * @param {Array} products - 상품 배열
 */
function displayOrderProducts(products) {

    const productCountEl = document.getElementById('productCount');
    const productListEl = document.getElementById('productList');

    if (!productListEl) return;

    // 상품 개수 표시
    if (productCountEl) {
        productCountEl.textContent = `총 ${products.length}개 상품`;
    }

    // 상품 목록 생성
    productListEl.innerHTML = products.map(product => `
        <div class="product-item">
            <input type="hidden" class="item-id" value="${product.itemId}">
            <input type="hidden" class="item-img-id" value="${product.itemImgId || ''}">
        
            <div class="product-image">
                <img src="${product.imgUrl}" alt="${product.imgName}">
            </div>
            <div class="product-details">
                <h3 class="product-name">${product.itemName}</h3>
                <p class="product-description">${product.itemDetail}</p>
            </div>
            <div class="product-quantity">
                <span class="quantity-label">수량</span>
                <span class="quantity-value">${product.count}개</span>
            </div>
            <div class="product-price">
                <span class="unit-price">${product.price.toLocaleString()}원</span>
                <span class="total-price">${product.orderPrice.toLocaleString()}원</span>
            </div>
        </div>
    `).join('');
}

/**
 * 결제 금액 정보
 * @param {Array} products - 주문 상품 배열 (각 상품의 orderPrice 등 포함)
 */
function displayPaymentSummary(products) {

    const productAmountContainer = document.getElementById('productAmountContainer');
    const finalAmount = document.getElementById('finalAmount');

    if(!productAmountContainer || !finalAmount) return;

    productAmountContainer.innerHTML = products.map(product => `
        <div class="summary-row">
            <span class="summary-label">상품금액</span>
            <span class="summary-value">${product.orderPrice.toLocaleString()}원</span>
        </div>
    `).join('');

    //총 합계 계산 후 렌더링
    const totalSum = products.reduce((sum, p) => sum + p.orderPrice, 0);
    finalAmount.textContent = `${totalSum.toLocaleString()}원`;
}


// ==========================================================================
// 카카오 주소 API
// ==========================================================================

/**
 * 우편번호 검색 모달 띄우기
 */
function postcodeModal() {
    console.log("🔧 우편번호 검색 이벤트 동작...");
    new daum.Postcode({
        oncomplete: function (data) {
            //도로명 주소, 우편번호 세팅
            document.getElementById('postcode').value = data.zonecode;
            document.getElementById('roadAddress').value = data.roadAddress;

            //상세 주소 입력란에 포커스 이동
            const detailInput = document.getElementById('detailAddress');
            if (detailInput) detailInput.focus();
        }
    }).open();
}

// ==========================================================================
// 결제 기능들
// ==========================================================================

/**
 * 결제 버튼 클릭 시 실행
 * 서버에 최종 주문정보 보내고, 결제 API 호출
 */
async function handleOrderPayment(){
    try{
        console.log("🔧 결제 버튼 이벤트 실행");

        //전체 유효성 검사 실행
        if (!validateForm()) {
            console.warn("❌ 유효성 검사 실패 - 입력값 누락");
            showNotification(`필수 입력값을 입력하세요.`,'error');
            return;
        }

        //서버에서 받은 기존 주문 정보
        const orderDto = window.orderDtoFromServer || {};

        //사용자 입력값
        const name = document.getElementById('orderName').value.trim();
        const phoneNumber = document.getElementById('phoneNumber').value.trim();
        const zipcode = document.getElementById('postcode').value.trim();
        const roadAddress = document.getElementById('roadAddress').value.trim();
        const detailAddress = document.getElementById('detailAddress').value.trim();

        //동적으로 추가된 상품 요소들 가져옴
        const productEls = document.querySelectorAll('#productList .product-item');

        //주문 상품 정보 추출
        const orderItems = Array.from(productEls).map(el => ({
            itemId: parseInt(el.querySelector('.item-id').value),
            itemImgId: el.querySelector('.item-img-id').value || null,
            imgUrl: el.querySelector('.product-image img').src,
            imgName: el.querySelector('.product-image img').alt,
            itemName: el.querySelector('.product-name').textContent.trim(),
            itemDetail: el.querySelector('.product-description').textContent.trim(),
            count: parseInt(el.querySelector('.quantity-value').textContent.replace('개', '').trim()),
            price: parseInt(el.querySelector('.unit-price').textContent.replace(/원|,/g, '').trim()),
            orderPrice: parseInt(el.querySelector('.total-price').textContent.replace(/원|,/g, '').trim())
        }));

        const totalPriceText = document.getElementById('finalAmount')?.textContent.replace(/원|,/g, '').trim() || '0';
        const totalPrice = Number(totalPriceText);      //숫자로 변환

        //최종 주문 정보 생성
        const finalOrderDto = {
            ...orderDto,
            name,
            phoneNumber,
            zipcode,
            roadAddress,
            detailAddress,
            orderItems,
            totalPrice
        };

        //서버에 최종 주문정보 저장 비동기 요청
        const response = await fetch('/api/order/save', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(finalOrderDto)
        });

        if(response.status !== 201){
            throw new Error('주문 정보 저장에 실패했습니다.');
        }

        const savedOrder = await response.json();

        //대표 상품명
        const representativeItemName = orderItems.length > 0 ? orderItems[0].itemName : '상품';

        //결제 API 호출
        requestPortOnePayment(savedOrder, totalPrice, representativeItemName);

    }catch (error) {
        console.error('❌ 결제 처리 중 오류 발생: ', error);
        showNotification(`결제 처리 중 오류가 발생했습니다. 다시 시도해주세요.`, 'error');
    }
}

/**
 * 결제 API창 띄우고 결제 결과 서버에 전달하는 함수
 */
function requestPortOnePayment(savedOrder, totalPrice, itemName) {

    const orderUuid = savedOrder.orderUuid;

    if(typeof IMP === 'undefined') {
        throw new Error("❌ 결제 모듈을 불러오지 못했습니다.");
    }

    //결제 수단 기본값
    const pg = 'kakaopay.TC0ONETIME';

    //포트원 결제 요청
    IMP.request_pay({
        pg: pg,
        amount: totalPrice,
        merchant_uid: orderUuid,
        name: itemName          //결제창에 표시될 상품명
    }, async function(response) {
        //결제 실패
        if(response.error_code) {
            showNotification(`❌ 결제가 실패했습니다: ${response.error_msg}`, 'error');

            //새로고침하여 주문 정보 다시 로드
            setTimeout(() => {window.location.reload();}, 2000);
            return;
        }

        //사용자가 결제 취소할 경우
        if(response.success === false){
            showNotification(`❌ 결제가 실패했습니다: ${response.error_msg}`, 'error');

            //새로고침하여 주문 정보 다시 로드
            setTimeout(() => {window.location.reload();}, 2000);
            return;
        }

        let raqPayMethod = response.pay_method || response.payMethod || 'UNKNOWN';

        //테스트 환경에서 payMethod = "point"인 경우, 'KAKAOPAY'로 매핑
        let payMethod;
        switch (raqPayMethod.toLowerCase()) {
            case 'kakaopay':
            case 'point':
                payMethod = 'KAKAOPAY';
                break;
            case 'tosspay':
                payMethod = 'TOSSPAY';
                break;
            default:
                payMethod = 'UNKNOWN';
        }

        // 결제 성공 시 백엔드로 전달할 데이터
        const paymentResult = {
            paymentKey: response.imp_uid,
            orderUuid,
            amount: totalPrice,
            payMethod
        };

        try {
            const completeResponse = await fetch('/api/payment/success', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(paymentResult)
            });

            if (!completeResponse.ok) {
                throw new Error('결제 정보 저장 실패');
            }

            // 결제 정보 저장한 경우 주문 완료 페이지로 이동
            const orderId = await completeResponse.json();

            showNotification(`✅ 결제가 완료되었습니다! 주문 완료 페이지로 이동합니다.`, 'success');

            setTimeout(() => {
                const completeURL = `/order/complete/${orderId}`;
                window.location.href = completeURL;
            }, 3000);

        } catch (err) {
            console.error('❌ 결제 정보 전달 중 오류 발생:', err);
            showNotification(`결제 결과 저장 중 오류가 발생했습니다.`, 'error');
            //실패 시 redirection 코드 추가해야 함
        }

    });
}


// ==========================================================================
// 주문 관리 기능들
// ==========================================================================

/**
 * 뒤로가기 버튼 클릭 처리
 */
function goBack() {
    //브라우저 히스토리에 이전 페이지가 있으면 뒤로가기
    if (window.history.length > 1) {
        window.history.back();
    } else {
        // 없으면 마이페이지로 이동
        window.location.href = '/mypage';
    }

}

// ==========================================================================
// 인터랙션 초기화
// ==========================================================================
/**
 * 페이지 인터랙션 초기화
 * 각종 클릭 이벤트와 호버 효과를 설정합니다
 */
function initializeInteractions() {
    // 카드 호버 효과
    const cards = document.querySelectorAll('.order-info-card, .order-product-card, .delivery-info-card, .payment-info-card, .order-actions-card');
    cards.forEach(card => {
        card.addEventListener('mouseenter', handleCardHover);
    });

    console.log('✅ 인터랙션 이벤트가 초기화되었습니다.');
}

/**
 * 카드 호버 처리
 * @param {Event} event - 마우스 엔터 이벤트
 */
function handleCardHover(event) {
    const card = event.currentTarget;

    // 미묘한 그림자 효과
    card.style.transition = 'all 0.3s ease';
    card.style.boxShadow = '0 20px 60px rgba(45, 90, 61, 0.15)';
}

// ==========================================================================
// 키보드 단축키
// ==========================================================================
/**
 * 키보드 단축키 초기화
 */
function initializeKeyboardShortcuts() {
    document.addEventListener('keydown', (e) => {
        // 입력 필드에서는 단축키 비활성화
        if (e.target.tagName === 'INPUT' || e.target.tagName === 'TEXTAREA') {
            return;
        }

        switch(e.key) {
            case 'Escape':
                closeMobileMenu();
                // 모든 알림 닫기
                document.querySelectorAll('.notification').forEach(notification => {
                    notification.remove();
                });
                break;
            case 'b':
            case 'B':
                // 뒤로가기 (b키)
                if (e.shiftKey) {
                    e.preventDefault();
                    goBack();
                }
                break;
            case '?':
                // 도움말 표시
                showKeyboardShortcuts();
                break;
        }
    });

    console.log('✅ 키보드 단축키가 초기화되었습니다.');
}

/**
 * 키보드 단축키 도움말 표시
 */
function showKeyboardShortcuts() {
    const shortcuts = [
        'Esc: 메뉴 닫기 / 알림 닫기',
        'Shift + B: 뒤로가기',
        'Shift + R: 재주문',
        'Shift + T: 배송조회',
        '?: 이 도움말 표시'
    ];

    const helpMessage = '키보드 단축키:\n' + shortcuts.join('\n');
    showNotification(helpMessage.replace(/\n/g, '<br>'), 'info');
}

// ==========================================================================
// 반응형 레이아웃
// ==========================================================================
/**
 * 화면 크기에 따른 레이아웃 조정
 */
function adjustLayoutForScreenSize() {
    const width = window.innerWidth;

    if (width < 768) {
        adjustMobileLayout();
    } else if (width < 1200) {
        adjustTabletLayout();
    } else {
        adjustDesktopLayout();
    }
}

/**
 * 모바일 레이아웃 조정
 */
function adjustMobileLayout() {
    const deliveryDetails = document.querySelector('.delivery-details');
    const paymentDetails = document.querySelector('.payment-details');

    if (deliveryDetails) {
        deliveryDetails.style.gridTemplateColumns = '1fr';
    }

    if (paymentDetails) {
        paymentDetails.style.gridTemplateColumns = '1fr';
    }
}

/**
 * 태블릿 레이아웃 조정
 */
function adjustTabletLayout() {
    const deliveryDetails = document.querySelector('.delivery-details');
    const paymentDetails = document.querySelector('.payment-details');

    if (deliveryDetails) {
        deliveryDetails.style.gridTemplateColumns = '1fr';
    }

    if (paymentDetails) {
        paymentDetails.style.gridTemplateColumns = '1fr';
    }
}

/**
 * 데스크탑 레이아웃 조정
 */
function adjustDesktopLayout() {
    const deliveryDetails = document.querySelector('.delivery-details');
    const paymentDetails = document.querySelector('.payment-details');

    if (deliveryDetails) {
        deliveryDetails.style.gridTemplateColumns = '1fr 1fr';
    }

    if (paymentDetails) {
        paymentDetails.style.gridTemplateColumns = '1fr 1fr';
    }
}

// 윈도우 리사이즈 이벤트 리스너
window.addEventListener('resize', () => {
    let resizeTimeout;
    clearTimeout(resizeTimeout);
    resizeTimeout = setTimeout(() => {
        adjustLayoutForScreenSize();
    }, 250);
});

// ==========================================================================
// 유틸리티 함수들
// ==========================================================================

/**
 * 알림 표시 함수
 * @param {string} message - 알림 메시지
 * @param {string} type - 알림 타입 (success, error, warning, info)
 */
function showNotification(message, type = 'success') {
    // 기존 알림 제거
    const existingNotification = document.querySelector('.notification');
    if (existingNotification) {
        existingNotification.remove();
    }

    // 새 알림 생성
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.innerHTML = `
        <div class="notification-content">
            <span class="notification-icon">${getNotificationIcon(type)}</span>
            <span class="notification-text">${message}</span>
            <button class="notification-close" onclick="this.parentElement.parentElement.remove()">×</button>
        </div>
    `;

    // 스타일 적용
    notification.style.cssText = `
        position: fixed;
        top: 100px;
        right: 20px;
        background: ${getNotificationColor(type)};
        color: white;
        padding: 0;
        border-radius: 12px;
        box-shadow: 0 8px 32px rgba(0,0,0,0.2);
        z-index: 10000;
        transform: translateX(400px);
        transition: transform 0.4s cubic-bezier(0.4, 0, 0.2, 1);
        max-width: 350px;
        min-width: 280px;
        overflow: hidden;
    `;

    // 내부 콘텐츠 스타일
    const content = notification.querySelector('.notification-content');
    content.style.cssText = `
        display: flex;
        align-items: center;
        padding: 15px 20px;
        gap: 10px;
    `;

    // 아이콘 스타일
    const icon = notification.querySelector('.notification-icon');
    icon.style.cssText = `
        font-size: 18px;
        flex-shrink: 0;
    `;

    // 텍스트 스타일
    const text = notification.querySelector('.notification-text');
    text.style.cssText = `
        flex: 1;
        font-weight: 500;
        font-size: 14px;
        line-height: 1.4;
    `;

    // 닫기 버튼 스타일
    const closeBtn = notification.querySelector('.notification-close');
    closeBtn.style.cssText = `
        background: rgba(255, 255, 255, 0.2);
        border: none;
        color: white;
        border-radius: 50%;
        width: 24px;
        height: 24px;
        cursor: pointer;
        font-size: 16px;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-shrink: 0;
        transition: background 0.3s ease;
    `;

    closeBtn.addEventListener('mouseenter', () => {
        closeBtn.style.background = 'rgba(255, 255, 255, 0.3)';
    });

    closeBtn.addEventListener('mouseleave', () => {
        closeBtn.style.background = 'rgba(255, 255, 255, 0.2)';
    });

    document.body.appendChild(notification);

    // 알림 표시
    setTimeout(() => {
        notification.style.transform = 'translateX(0)';
    }, 100);

    // 자동 숨김 (에러가 아닌 경우)
    if (type !== 'error') {
        setTimeout(() => {
            if (notification.parentNode) {
                notification.style.transform = 'translateX(400px)';
                setTimeout(() => {
                    if (notification.parentNode) {
                        notification.remove();
                    }
                }, 400);
            }
        }, 4000);
    }
}

/**
 * 알림 아이콘 반환
 * @param {string} type - 알림 타입
 * @returns {string} 아이콘
 */
function getNotificationIcon(type) {
    switch(type) {
        case 'success': return '✅';
        case 'error': return '❌';
        case 'warning': return '⚠️';
        case 'info': return 'ℹ️';
        default: return '✅';
    }
}

/**
 * 알림 색상 반환
 * @param {string} type - 알림 타입
 * @returns {string} 색상
 */
function getNotificationColor(type) {
    switch(type) {
        case 'success': return 'linear-gradient(135deg, #2d5a3d, #6fa776)';
        case 'error': return 'linear-gradient(135deg, #dc3545, #e85967)';
        case 'warning': return 'linear-gradient(135deg, #ffc107, #ffcd39)';
        case 'info': return 'linear-gradient(135deg, #17a2b8, #20c997)';
        default: return 'linear-gradient(135deg, #2d5a3d, #6fa776)';
    }
}

/**
 * 에러 처리 함수
 * @param {Error} error - 발생한 에러
 * @param {string} context - 에러 발생 컨텍스트
 */
function handleError(error, context = '') {
    console.error(`Error in ${context}:`, error);
    showNotification(`오류가 발생했습니다: ${error.message}`, 'error');
}

/* ==========================================================================
   이벤트 리스너 설정
   ========================================================================== */

function setupEventListeners() {
    console.log('🔧 이벤트 리스너 설정...');

    //주소 검색 버튼
    const searchAddressBtn = document.getElementById('btn-search-postcode');
    if(searchAddressBtn) {
        searchAddressBtn.addEventListener('click', postcodeModal);
    }

    //결제 버튼
    const orderBtn = document.getElementById('orderBtn');
    if(orderBtn){
        orderBtn.addEventListener('click', handleOrderPayment);
    }

    //전화번호 자동 포맷팅 이벤트
    const phoneInput = document.getElementById('phoneNumber');
    if(phoneInput){
        phoneInput.addEventListener('input', formatPhoneNumber);
    }
}

// ==========================================================================
// 입력값 유효성 겁사 함수
// ==========================================================================

// 개별 필드 유효성 검사
function validateField(field) {
    const value = field.value.trim();
    const isRequired = field.hasAttribute('required');
    const id = field.id;

    if (isRequired && !value) {
        showFieldError(field, '⚠️ 필수 입력 항목입니다.');
        return false;
    }

    //추가 유효성 검사
    if(value) {
        if(id === 'phoneNumber') {
            const phonePattern = /^01[016789]-?\d{3,4}-?\d{4}$/;        //010으로 시작하고 숫자만 10~11자리인지 검사(하이픈 유무 모두 허용)
            if(!phonePattern.test(value)) {
                showFieldError(field, '⚠️ 유효한 전화번호 형식이 아닙니다.');
                return false;
            }
        }

        if(id === 'detailAddress') {
            if(value.length < 2) {
                showFieldError(field, '⚠️ 상세 주소를 조금 더 자세히 입력해 주세요.');
                return false;
            }
        }

        //성공 시 처리
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
    const requiredFields = ['orderName', 'phoneNumber', 'postcode', 'roadAddress', 'detailAddress', 'delivery-memo'];

    requiredFields.forEach(function(fieldId) {
        const field = document.getElementById(fieldId);
        if (field && !validateField(field)) {
            isValid = false;
        }
    });

    return isValid;
}

// ==========================================================================
// 기타 함수
// ==========================================================================
/**
 * 전화번호 자동 포맷팅 함수
 * 입력 중 자동으로 하이픈을 넣는 기능을 하는 함수
 * @param event
 */
function formatPhoneNumber(event) {
    let value = event.target.value.replace(/\D/g, '');
    if (value.length > 3 && value.length <= 7) {
        value = value.substring(0, 3) + '-' + value.substring(3);
    } else if (value.length > 7) {
        value = value.substring(0, 3) + '-' + value.substring(3, 7) + '-' + value.substring(7, 11);
    }
    event.target.value = value;
}

// ==========================================================================
// 전역 함수 노출 및 에러 핸들러 설정
// ==========================================================================

// 전역 함수 노출 (HTML에서 호출되는 함수들)
window.goBack = goBack;
window.showNotification = showNotification;
window.validateForm = validateForm;

// 전역 에러 핸들러
window.addEventListener('error', (e) => {
    handleError(e.error, 'Global error');
});

// 프로미스 거부 핸들러
window.addEventListener('unhandledrejection', (e) => {
    handleError(new Error(e.reason), 'Unhandled promise rejection');
});

console.log('🛍️ GreenCycle 주문상세 페이지 JavaScript가 로드되었습니다.');