/**
 * ECOVERY 구매이력 페이지 JavaScript
 * 주문 목록 조회, 필터링, 검색, 페이지네이션 등의 기능을 제공합니다
 */

// ==========================================================================
// 전역 변수 선언
// ==========================================================================
let originalOrderList = [];      // 원본 주문 목록
let filteredOrderList = [];      // 필터링된 주문 목록
let displayedOrderList = [];     // 현재 페이지에 표시될 주문 목록
let currentPage = 1;             // 현재 페이지 번호
let itemsPerPage = 10;           // 페이지당 표시할 주문 수
let totalPages = 1;              // 전체 페이지 수
let isInitialized = false;       // 초기화 상태 플래그

// DOM 요소들
const statusFilter = document.getElementById('statusFilter');
const periodFilter = document.getElementById('periodFilter');
const searchInput = document.getElementById('searchInput');
const orderTableBody = document.getElementById('orderTableBody');
const orderCardsContainer = document.querySelector('.order-cards-container');
const pageNumbers = document.getElementById('pageNumbers');

// ==========================================================================
// 페이지 초기화 - DOMContentLoaded 이벤트 리스너
// ==========================================================================
document.addEventListener('DOMContentLoaded', function() {
    try {
        console.log('🛒 ECOVERY 구매이력 페이지 초기화를 시작합니다...');

        // 전역 변수로 전달받은 주문 데이터 초기화
        initializeOrderData();

        // 핵심 기능 초기화
        initializeFilters();           // 필터링 기능 초기화
        initializeSearch();            // 검색 기능 초기화
        initializePagination();        // 페이지네이션 초기화
        initializeResponsive();        // 반응형 기능 초기화
        initializeKeyboardShortcuts(); // 키보드 단축키 초기화

        // 초기 데이터 표시
        filterAndDisplayOrders();

        isInitialized = true;
        console.log('🛒 구매이력 페이지가 성공적으로 초기화되었습니다.');

        // 환영 메시지 표시 (1초 후)
        setTimeout(() => {
            showNotification('구매이력을 불러왔습니다! 📋', 'success');
        }, 1000);

    } catch (error) {
        handleError(error, 'Order history page initialization');
    }
});

// ==========================================================================
// 주문 데이터 초기화
// ==========================================================================
/**
 * 전역 변수로 전달받은 주문 데이터를 초기화합니다
 */
function initializeOrderData() {
    try {
        // Thymeleaf에서 전달받은 orderList 변수 사용
        if (typeof orderList !== 'undefined' && Array.isArray(orderList)) {
            originalOrderList = [...orderList];
            filteredOrderList = [...orderList];
            console.log(`주문 데이터 로드 완료: ${originalOrderList.length}건`);
        } else {
            originalOrderList = [];
            filteredOrderList = [];
            console.warn('주문 데이터가 없거나 올바르지 않습니다.');
        }
    } catch (error) {
        console.error('주문 데이터 초기화 중 오류:', error);
        originalOrderList = [];
        filteredOrderList = [];
    }
}

// ==========================================================================
// 필터링 기능 초기화
// ==========================================================================
/**
 * 주문 상태 및 기간 필터링 기능을 초기화합니다
 */
function initializeFilters() {
    if (statusFilter) {
        statusFilter.addEventListener('change', handleFilterChange);
    }

    if (periodFilter) {
        periodFilter.addEventListener('change', handleFilterChange);
    }

    console.log('✅ 필터링 기능이 초기화되었습니다.');
}

/**
 * 필터 변경 이벤트 처리
 */
function handleFilterChange() {
    currentPage = 1; // 필터 변경 시 첫 페이지로 이동
    filterAndDisplayOrders();
}

/**
 * 주문 목록 필터링 및 표시
 */
function filterAndDisplayOrders() {
    try {
        // 1단계: 상태 필터링
        let filtered = filterByStatus(originalOrderList);
        
        // 2단계: 기간 필터링
        filtered = filterByPeriod(filtered);
        
        // 3단계: 검색 필터링
        filtered = filterBySearch(filtered);

        // 필터링된 결과 저장
        filteredOrderList = filtered;

        // 페이지네이션 계산
        calculatePagination();

        // 현재 페이지에 해당하는 데이터 추출
        const startIndex = (currentPage - 1) * itemsPerPage;
        const endIndex = startIndex + itemsPerPage;
        displayedOrderList = filteredOrderList.slice(startIndex, endIndex);

        // 화면에 표시
        displayOrders();
        updatePagination();

        console.log(`필터링 완료: ${filteredOrderList.length}건 (전체 ${originalOrderList.length}건 중)`);

    } catch (error) {
        handleError(error, 'Filtering and displaying orders');
    }
}

/**
 * 주문 상태별 필터링
 * @param {Array} orders - 필터링할 주문 목록
 * @returns {Array} 필터링된 주문 목록
 */
function filterByStatus(orders) {
    const statusValue = statusFilter ? statusFilter.value : '';
    
    if (!statusValue) {
        return orders; // 전체 선택
    }

    return orders.filter(order => {
        const orderStatus = order.orderStatus && order.orderStatus.name ? order.orderStatus.name : order.orderStatus;
        return orderStatus === statusValue;
    });
}

/**
 * 기간별 필터링
 * @param {Array} orders - 필터링할 주문 목록
 * @returns {Array} 필터링된 주문 목록
 */
function filterByPeriod(orders) {
    const periodValue = periodFilter ? periodFilter.value : '';
    
    if (!periodValue) {
        return orders; // 전체 기간
    }

    const now = new Date();
    let startDate;

    switch (periodValue) {
        case '1month':
            startDate = new Date(now.getFullYear(), now.getMonth() - 1, now.getDate());
            break;
        case '3month':
            startDate = new Date(now.getFullYear(), now.getMonth() - 3, now.getDate());
            break;
        case '6month':
            startDate = new Date(now.getFullYear(), now.getMonth() - 6, now.getDate());
            break;
        case '1year':
            startDate = new Date(now.getFullYear() - 1, now.getMonth(), now.getDate());
            break;
        default:
            return orders;
    }

    return orders.filter(order => {
        const orderDate = new Date(order.createdAt);
        return orderDate >= startDate;
    });
}

/**
 * 검색어로 필터링
 * @param {Array} orders - 필터링할 주문 목록
 * @returns {Array} 필터링된 주문 목록
 */
function filterBySearch(orders) {
    const searchValue = searchInput ? searchInput.value.trim().toLowerCase() : '';
    
    if (!searchValue) {
        return orders; // 검색어 없음
    }

    return orders.filter(order => {
        // 주문번호로 검색
        const orderNumber = (order.orderUuid || '').toLowerCase();
        if (orderNumber.includes(searchValue)) {
            return true;
        }

        // 상품명으로 검색
        if (order.orderItems && Array.isArray(order.orderItems)) {
            return order.orderItems.some(item => {
                const itemName = (item.itemName || '').toLowerCase();
                return itemName.includes(searchValue);
            });
        }

        return false;
    });
}

// ==========================================================================
// 검색 기능 초기화
// ==========================================================================
/**
 * 검색 기능을 초기화합니다
 */
function initializeSearch() {
    if (searchInput) {
        // 실시간 검색 (디바운싱 적용)
        let searchTimeout;
        searchInput.addEventListener('input', () => {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(() => {
                currentPage = 1; // 검색 시 첫 페이지로 이동
                filterAndDisplayOrders();
            }, 300);
        });

        // 엔터키 검색
        searchInput.addEventListener('keydown', (e) => {
            if (e.key === 'Enter') {
                e.preventDefault();
                currentPage = 1;
                filterAndDisplayOrders();
            }
        });
    }

    console.log('✅ 검색 기능이 초기화되었습니다.');
}

/**
 * 검색 버튼 클릭 처리
 */
function searchOrders() {
    currentPage = 1;
    filterAndDisplayOrders();
}

// ==========================================================================
// 페이지네이션 기능
// ==========================================================================
/**
 * 페이지네이션을 초기화합니다
 */
function initializePagination() {
    // 모바일에서는 페이지당 아이템 수 조정
    if (window.innerWidth <= 767) {
        itemsPerPage = 5;
    } else {
        itemsPerPage = 10;
    }

    console.log('✅ 페이지네이션이 초기화되었습니다.');
}

/**
 * 페이지네이션 정보 계산
 */
function calculatePagination() {
    totalPages = Math.ceil(filteredOrderList.length / itemsPerPage);
    
    // 현재 페이지가 총 페이지 수를 초과하면 조정
    if (currentPage > totalPages && totalPages > 0) {
        currentPage = totalPages;
    } else if (currentPage < 1) {
        currentPage = 1;
    }
}

/**
 * 페이지네이션 UI 업데이트
 */
function updatePagination() {
    if (!pageNumbers) return;

    pageNumbers.innerHTML = '';

    // 페이지가 1개 이하면 페이지네이션 숨기기
    const paginationSection = document.querySelector('.pagination-section');
    if (totalPages <= 1) {
        if (paginationSection) {
            paginationSection.style.display = 'none';
        }
        return;
    } else {
        if (paginationSection) {
            paginationSection.style.display = 'flex';
        }
    }

    // 페이지 번호 생성 (최대 5개 표시)
    const maxVisiblePages = 5;
    let startPage = Math.max(1, currentPage - Math.floor(maxVisiblePages / 2));
    let endPage = Math.min(totalPages, startPage + maxVisiblePages - 1);

    // 시작 페이지 조정
    if (endPage - startPage + 1 < maxVisiblePages) {
        startPage = Math.max(1, endPage - maxVisiblePages + 1);
    }

    for (let i = startPage; i <= endPage; i++) {
        const pageButton = document.createElement('button');
        pageButton.className = `page-number ${i === currentPage ? 'active' : ''}`;
        pageButton.textContent = i;
        pageButton.onclick = () => goToPage(i);
        pageNumbers.appendChild(pageButton);
    }

    // 이전/다음 버튼 상태 업데이트
    const prevBtn = document.querySelector('.prev-btn');
    const nextBtn = document.querySelector('.next-btn');

    if (prevBtn) {
        prevBtn.disabled = currentPage <= 1;
    }

    if (nextBtn) {
        nextBtn.disabled = currentPage >= totalPages;
    }
}

/**
 * 특정 페이지로 이동
 * @param {number} page - 이동할 페이지 번호
 */
function goToPage(page) {
    if (page >= 1 && page <= totalPages && page !== currentPage) {
        currentPage = page;
        filterAndDisplayOrders();
        
        // 페이지 상단으로 스크롤
        const orderHistorySection = document.querySelector('.order-history-section');
        if (orderHistorySection) {
            orderHistorySection.scrollIntoView({ behavior: 'smooth' });
        }
    }
}

/**
 * 이전 페이지로 이동
 */
function previousPage() {
    goToPage(currentPage - 1);
}

/**
 * 다음 페이지로 이동
 */
function nextPage() {
    goToPage(currentPage + 1);
}

// ==========================================================================
// 주문 목록 표시
// ==========================================================================
/**
 * 주문 목록을 화면에 표시합니다
 */
function displayOrders() {
    // 데스크탑 테이블 업데이트
    if (orderTableBody) {
        updateOrderTable();
    }

    // 모바일 카드 업데이트
    if (orderCardsContainer) {
        updateOrderCards();
    }
}

/**
 * 주문 테이블 업데이트 (데스크탑)
 */
function updateOrderTable() {
    if (displayedOrderList.length === 0) {
        orderTableBody.innerHTML = `
            <tr>
                <td colspan="6" style="text-align: center; padding: 40px; color: var(--medium-gray);">
                    조건에 맞는 주문이 없습니다.
                </td>
            </tr>
        `;
        return;
    }

    orderTableBody.innerHTML = displayedOrderList.map(order => {
        const firstItem = order.orderItems && order.orderItems.length > 0 ? order.orderItems[0] : null;
        const itemCount = order.orderItems ? order.orderItems.length : 0;
        const orderStatus = getOrderStatusInfo(order.orderStatus);
        
        return `
            <tr class="order-row" data-order-id="${order.orderUuid}">
                <td class="order-number">
                    <a href="/orders/${order.orderUuid}/detail" class="order-link">
                        ${order.orderUuid}
                    </a>
                </td>
                <td class="order-date">
                    ${formatDate(order.createdAt)}
                    <small>${formatTime(order.createdAt)}</small>
                </td>
                <td class="order-items">
                    <div class="items-info">
                        <div class="first-item">
                            ${firstItem ? `
                                <img src="/api/images/${firstItem.itemImgId}" 
                                     alt="${firstItem.itemName}" 
                                     class="item-image"
                                     onerror="this.src='data:image/svg+xml,<svg xmlns=\\'http://www.w3.org/2000/svg\\' viewBox=\\'0 0 50 50\\'><rect fill=\\'%23e3f2fd\\' width=\\'50\\' height=\\'50\\'/><text x=\\'25\\' y=\\'30\\' font-size=\\'20\\' text-anchor=\\'middle\\'>📦</text></svg>'">
                                <div class="item-details">
                                    <span class="item-name">${firstItem.itemName}</span>
                                    ${itemCount > 1 ? `<span class="item-count">외 ${itemCount - 1}개</span>` : ''}
                                </div>
                            ` : '<span class="item-name">상품 정보 없음</span>'}
                        </div>
                    </div>
                </td>
                <td class="order-amount">
                    <span class="amount">${formatCurrency(order.payAmount)}</span>
                </td>
                <td class="order-status">
                    <span class="status-badge ${orderStatus.className}">${orderStatus.text}</span>
                </td>
                <td class="order-actions">
                    <div class="action-buttons">
                        <a href="/orders/${order.orderUuid}/detail" class="btn-action btn-detail">상세보기</a>
                        ${generateActionButtons(order)}
                    </div>
                </td>
            </tr>
        `;
    }).join('');
}

/**
 * 주문 카드 업데이트 (모바일)
 */
function updateOrderCards() {
    if (displayedOrderList.length === 0) {
        orderCardsContainer.innerHTML = `
            <div style="text-align: center; padding: 40px; color: var(--medium-gray);">
                조건에 맞는 주문이 없습니다.
            </div>
        `;
        return;
    }

    orderCardsContainer.innerHTML = displayedOrderList.map(order => {
        const firstItem = order.orderItems && order.orderItems.length > 0 ? order.orderItems[0] : null;
        const itemCount = order.orderItems ? order.orderItems.length : 0;
        const orderStatus = getOrderStatusInfo(order.orderStatus);
        
        return `
            <div class="order-card" data-order-id="${order.orderUuid}">
                <div class="card-header">
                    <div class="order-info">
                        <span class="order-number">${order.orderUuid}</span>
                        <span class="order-date">${formatDateTime(order.createdAt)}</span>
                    </div>
                    <span class="status-badge ${orderStatus.className}">${orderStatus.text}</span>
                </div>
                <div class="card-body">
                    <div class="items-section">
                        ${firstItem ? `
                            <img src="/api/images/${firstItem.itemImgId}" 
                                 alt="${firstItem.itemName}" 
                                 class="item-image"
                                 onerror="this.src='data:image/svg+xml,<svg xmlns=\\'http://www.w3.org/2000/svg\\' viewBox=\\'0 0 60 60\\'><rect fill=\\'%23e3f2fd\\' width=\\'60\\' height=\\'60\\'/><text x=\\'30\\' y=\\'35\\' font-size=\\'24\\' text-anchor=\\'middle\\'>📦</text></svg>'">
                            <div class="item-info">
                                <span class="item-name">${firstItem.itemName}</span>
                                ${itemCount > 1 ? `<span class="item-count">외 ${itemCount - 1}개</span>` : ''}
                            </div>
                        ` : '<span class="item-name">상품 정보 없음</span>'}
                    </div>
                    <div class="amount-section">
                        <span class="amount">${formatCurrency(order.payAmount)}</span>
                    </div>
                </div>
                <div class="card-footer">
                    <a href="/orders/${order.orderUuid}/detail" class="btn-detail">상세보기</a>
                    <div class="quick-actions">
                        ${generateQuickActionButtons(order)}
                    </div>
                </div>
            </div>
        `;
    }).join('');
}

// ==========================================================================
// 주문 관리 기능들
// ==========================================================================

/**
 * 주문 취소 기능
 * @param {string} orderUuid - 주문 UUID
 */
function cancelOrder(orderUuid) {
    if (!confirm(messages.confirmCancel)) {
        return;
    }

    showNotification('주문을 취소하고 있습니다...', 'info');

    // 실제 구현에서는 서버 API 호출
    // fetch(`${orderCancelUrl.replace('{id}', orderUuid)}`, {
    //     method: 'POST',
    //     headers: {
    //         'Content-Type': 'application/json',
    //         [csrfHeader]: csrfToken
    //     }
    // })

    // 모의 처리 (실제로는 서버 응답 처리)
    setTimeout(() => {
        // 로컬 데이터에서 주문 상태 업데이트
        const order = originalOrderList.find(o => o.orderUuid === orderUuid);
        if (order) {
            order.orderStatus = { name: 'CANCELLED' };
        }

        // 화면 새로고침
        filterAndDisplayOrders();
        
        showNotification(messages.cancelSuccess, 'success');
        console.log(`주문 취소 완료: ${orderUuid}`);
    }, 2000);
}

/**
 * 재주문 기능
 * @param {string} orderUuid - 주문 UUID
 */
function reorderItems(orderUuid) {
    const order = originalOrderList.find(o => o.orderUuid === orderUuid);
    
    if (!order || !order.orderItems) {
        showNotification('주문 정보를 찾을 수 없습니다.', 'error');
        return;
    }

    showNotification('장바구니에 상품을 담고 있습니다...', 'info');

    // 실제 구현에서는 서버 API 호출
    // fetch(`${reorderUrl.replace('{id}', orderUuid)}`, {
    //     method: 'POST',
    //     headers: {
    //         'Content-Type': 'application/json',
    //         [csrfHeader]: csrfToken
    //     }
    // })

    // 모의 처리
    setTimeout(() => {
        showNotification(messages.reorderSuccess, 'success');
        
        // 1초 후 장바구니 페이지로 이동
        setTimeout(() => {
            window.location.href = '/cart';
        }, 1000);
        
        console.log(`재주문 완료: ${orderUuid}`);
    }, 1500);
}

/**
 * 상품후기 작성
 * @param {string} orderUuid - 주문 UUID
 */
function writeReview(orderUuid) {
    showNotification('상품후기 작성 페이지로 이동합니다.', 'info');
    
    setTimeout(() => {
        window.location.href = reviewWriteUrl.replace('{id}', orderUuid);
    }, 800);
    
    console.log(`상품후기 작성: ${orderUuid}`);
}

// ==========================================================================
// 유틸리티 함수들
// ==========================================================================

/**
 * 주문 상태 정보 반환
 * @param {Object|string} orderStatus - 주문 상태 객체 또는 문자열
 * @returns {Object} 상태 정보 객체
 */
function getOrderStatusInfo(orderStatus) {
    const statusName = orderStatus && orderStatus.name ? orderStatus.name : orderStatus;
    
    switch (statusName) {
        case 'PAID':
            return { text: '결제완료', className: 'status-processing' };
        case 'PREPARING':
            return { text: '상품준비중', className: 'status-processing' };
        case 'SHIPPED':
            return { text: '배송중', className: 'status-shipping' };
        case 'DELIVERED':
            return { text: '배송완료', className: 'status-delivered' };
        case 'CANCELLED':
            return { text: '주문취소', className: 'status-cancelled' };
        default:
            return { text: '상태미확인', className: 'status-processing' };
    }
}

/**
 * 액션 버튼 생성 (데스크탑용)
 * @param {Object} order - 주문 객체
 * @returns {string} 버튼 HTML
 */
function generateActionButtons(order) {
    const statusName = order.orderStatus && order.orderStatus.name ? order.orderStatus.name : order.orderStatus;
    let buttons = [];

    if (statusName === 'DELIVERED') {
        buttons.push(`<button class="btn-action btn-review" onclick="writeReview('${order.orderUuid}')">후기작성</button>`);
        buttons.push(`<button class="btn-action btn-reorder" onclick="reorderItems('${order.orderUuid}')">재주문</button>`);
    }

    if (statusName === 'PAID' || statusName === 'PREPARING') {
        buttons.push(`<button class="btn-action btn-cancel" onclick="cancelOrder('${order.orderUuid}')">주문취소</button>`);
    }

    return buttons.join('');
}

/**
 * 빠른 액션 버튼 생성 (모바일용)
 * @param {Object} order - 주문 객체
 * @returns {string} 버튼 HTML
 */
function generateQuickActionButtons(order) {
    const statusName = order.orderStatus && order.orderStatus.name ? order.orderStatus.name : order.orderStatus;
    let buttons = [];

    if (statusName === 'DELIVERED') {
        buttons.push(`<button class="btn-quick btn-review" onclick="writeReview('${order.orderUuid}')">후기</button>`);
        buttons.push(`<button class="btn-quick btn-reorder" onclick="reorderItems('${order.orderUuid}')">재주문</button>`);
    }

    if (statusName === 'PAID' || statusName === 'PREPARING') {
        buttons.push(`<button class="btn-quick btn-cancel" onclick="cancelOrder('${order.orderUuid}')">취소</button>`);
    }

    return buttons.join('');
}

/**
 * 날짜 포맷팅
 * @param {string} dateString - 날짜 문자열
 * @returns {string} 포맷팅된 날짜
 */
function formatDate(dateString) {
    const date = new Date(dateString);
    return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')}`;
}

/**
 * 시간 포맷팅
 * @param {string} dateString - 날짜 문자열
 * @returns {string} 포맷팅된 시간
 */
function formatTime(dateString) {
    const date = new Date(dateString);
    return `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
}

/**
 * 날짜시간 포맷팅
 * @param {string} dateString - 날짜 문자열
 * @returns {string} 포맷팅된 날짜시간
 */
function formatDateTime(dateString) {
    return `${formatDate(dateString)} ${formatTime(dateString)}`;
}

/**
 * 통화 포맷팅
 * @param {number} amount - 금액
 * @returns {string} 포맷팅된 금액
 */
function formatCurrency(amount) {
    return `${amount.toLocaleString()}원`;
}

// ==========================================================================
// 반응형 기능
// ==========================================================================
/**
 * 반응형 기능을 초기화합니다
 */
function initializeResponsive() {
    // 윈도우 리사이즈 이벤트 리스너
    let resizeTimeout;
    window.addEventListener('resize', () => {
        clearTimeout(resizeTimeout);
        resizeTimeout = setTimeout(() => {
            adjustForScreenSize();
        }, 250);
    });

    // 초기 화면 크기 조정
    adjustForScreenSize();

    console.log('✅ 반응형 기능이 초기화되었습니다.');
}

/**
 * 화면 크기에 따른 조정
 */
function adjustForScreenSize() {
    const width = window.innerWidth;
    
    // 모바일에서는 페이지당 아이템 수 조정
    const newItemsPerPage = width <= 767 ? 5 : 10;
    
    if (newItemsPerPage !== itemsPerPage) {
        itemsPerPage = newItemsPerPage;
        currentPage = 1; // 첫 페이지로 리셋
        if (isInitialized) {
            filterAndDisplayOrders();
        }
    }
}

// ==========================================================================
// 키보드 단축키
// ==========================================================================
/**
 * 키보드 단축키를 초기화합니다
 */
function initializeKeyboardShortcuts() {
    document.addEventListener('keydown', (e) => {
        // 입력 필드에서는 단축키 비활성화
        if (e.target.tagName === 'INPUT' || e.target.tagName === 'TEXTAREA' || e.target.tagName === 'SELECT') {
            return;
        }

        switch(e.key) {
            case 'Escape':
                // 검색 초기화
                if (searchInput) {
                    searchInput.value = '';
                    filterAndDisplayOrders();
                }
                break;
            case 'f':
            case 'F':
                // 검색 포커스 (f키)
                if (e.ctrlKey || e.metaKey) {
                    e.preventDefault();
                    if (searchInput) {
                        searchInput.focus();
                    }
                }
                break;
            case 'ArrowLeft':
                // 이전 페이지 (좌측 화살표)
                if (e.shiftKey) {
                    e.preventDefault();
                    previousPage();
                }
                break;
            case 'ArrowRight':
                // 다음 페이지 (우측 화살표)
                if (e.shiftKey) {
                    e.preventDefault();
                    nextPage();
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
        'Esc: 검색 초기화',
        'Ctrl/Cmd + F: 검색 포커스',
        'Shift + ←/→: 이전/다음 페이지',
        '?: 이 도움말 표시'
    ];

    const helpMessage = '키보드 단축키:\n' + shortcuts.join('\n');
    showNotification(helpMessage.replace(/\n/g, '<br>'), 'info');
}

// ==========================================================================
// 알림 및 에러 처리
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

// ==========================================================================
// 전역 함수 노출 및 에러 핸들러 설정
// ==========================================================================

// 전역 함수 노출 (HTML에서 호출되는 함수들)
window.searchOrders = searchOrders;
window.cancelOrder = cancelOrder;
window.reorderItems = reorderItems;
window.writeReview = writeReview;
window.goToPage = goToPage;
window.previousPage = previousPage;
window.nextPage = nextPage;
window.showNotification = showNotification;

// 전역 에러 핸들러
window.addEventListener('error', (e) => {
    handleError(e.error, 'Global error');
});

// 프로미스 거부 핸들러
window.addEventListener('unhandledrejection', (e) => {
    handleError(new Error(e.reason), 'Unhandled promise rejection');
});

console.log('🛒 ECOVERY 구매이력 페이지 JavaScript가 로드되었습니다.');