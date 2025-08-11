/**
 * ==========================================================================
 * 상품 목록 페이지 JavaScript (메인 기능)
 * 이미지 갤러리, 장바구니 기능, 모달 관리 등
 * @history
 *  - 250729 | sehui | 에코마켓 목록 비동기 요청하여 조회 후 렌더링 기능 추가
 *  - 250729 | sehui | 에코마켓 목록 페이징 처리 기능 추가
 *  - 250805 | sehui | 에코마켓 목록 페이지에서 사용하지 않는 기능 삭제
 *  - 250805 | sehui | 에코마켓 목록 한 페이지당 상품 개수 10개 -> 12개로 변경
 *  - 250808 | sehui | 페이드인 효과 및 검색창 개선 기능 추가
 */

/* ==========================================================================
   DOM 요소 참조
   ========================================================================== */
// 헤더 관련 요소
const header = document.getElementById('header');
const hamburger = document.getElementById('hamburger');
const navMenu = document.getElementById('navMenu');

// 상품 목록 관련 요소
const pagination = document.getElementById('pagination');           // 페이지네이션 컨테이너

/* ==========================================================================
   페이지 초기화 및 이벤트 리스너 설정
   ========================================================================== */

// 페이지 로드 완료 시 실행
document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 상품 목록 페이지 메인 기능 로딩 시작');

    initializePage();       // 페이지 기본 설정 초기화
    setupFadeInAnimation(); // 페이드인 애니메이션 설정
    loadItems();           // 상품 데이터 로드
    setupFilterForm();      //검색 필터 이벤트 리스너

    console.log('✅ 상품 목록 페이지 메인 기능 로딩 완료!');
});

/* ==========================================================================
   페이지 초기화 함수
   ========================================================================== */

function initializePage() {
    console.log('⚙️ 페이지 기본 설정 초기화...');

    // 헤더 스크롤 효과 설정
    window.addEventListener('scroll', () => {
        if (window.scrollY > 100) {
            header && header.classList.add('scrolled');
        } else {
            header && header.classList.remove('scrolled');
        }
    });

    // 모바일 햄버거 메뉴 토글
    if (hamburger && navMenu) {
        hamburger.addEventListener('click', () => {
            hamburger.classList.toggle('active');
            navMenu.classList.toggle('active');
        });

        // 네비게이션 링크 클릭         // 네비게이션 링크 클릭 시 모바일 메뉴 닫기
        document.querySelectorAll('.nav-menu a').forEach(link => {
            link.addEventListener('click', () => {
                hamburger.classList.remove('active');
                navMenu.classList.remove('active');
            });
        });
    }
}

/* ==========================================================================
   상품 데이터 로드 및 렌더링
   ========================================================================== */

// 상품 목록 로드
function loadItems(pageNum = 1,  itemNm = "", category = "") {
    console.log('⚙️ 상품 목록 로드...');

    const url = new URL("/api/eco/list", window.location.origin);
    url.searchParams.set('pageNum', pageNum);
    url.searchParams.set("amount", 12);

    if (itemNm) {
        url.searchParams.set("itemNm", itemNm);
    }
    if (category) {
        url.searchParams.set("category", category);
    }

    //fetch 함수로 GET 요청 보냄
    fetch(url)
        .then(response => response.json())
        .then(data => {
            renderItemList(data.itemList);
            renderPagination(data.pageMaker, itemNm, category);

            console.log('✅ 기본 폼 데이터 로드 완료');
        })
        .catch(error => console.error('❌ 상품 목록 조회 실패: ', error));
}

//상품 목록 렌더링
function renderItemList(itemList) {
    const itemsGrid = document.getElementById('itemsGrid');

    //기존 목록 초기화
    itemsGrid.innerHTML = '';

    if(!itemList || itemList.length === 0) {
        //상품이 존재하지 않는 경우
        itemsGrid.innerHTML = `
                    <div class="no-products">
                        <div class="no-products-content">
                            <h3>😔 검색 조건에 맞는 상품이 없습니다</h3>
                            <p>다른 조건으로 검색해보세요!</p>
                        </div>
                    </div>
                `;
        return;
    }

    //상품이 존재하는 경우
    itemList.forEach(product => {
        const productCard = document.createElement('div');
        productCard.classList.add('product-card');

        //클릭 시 상세 페이지 이동
        productCard.style.cursor = 'pointer';
        productCard.onclick = () => {
            location.href = `/eco/${product.itemId}`;
        };

        const imgUrl = product.imgUrl || "https://via.placeholder.com/200x150?text=No+Image";

        //상태 배지 클래스 및 텍스트 설정
        let statusClass = '';
        let statusText = '';

        switch (product.itemSellStatus) {
            case 'SELL':
                statusClass = 'available';
                statusText = '판매중';
                break;
            case 'SOLD_OUT':
                statusClass = 'completed';
                statusText = '판매완료';
                break;
            default:
                statusClass = '';
                statusText = product.itemSellStatus;
        }

        productCard.innerHTML = `
            <div class="item-card">
                <div class="item-image">
                    <img src="${imgUrl}" alt="${product.itemNm}" />  
                    <div class="item-status ${statusClass}">${statusText}</div>  
                </div>
                <div class="item-info">
                    <h3 class="item-title">${product.itemNm}</h3>
                    <div class="item-meta">
                        <span class="item-category">${product.category}</span>
                    </div>
                </div>
            </div>
        `;

        itemsGrid.appendChild(productCard);
    });
}

/* ==========================================================================
   페이지네이션 관련 함수
   ========================================================================== */

//페이징 렌더링
function renderPagination(pageMaker, itemNm, category) {
    const pagination = document.getElementById("pagination");
    pagination.innerHTML = "";

    const current = pageMaker.cri.pageNum;
    const total = Math.ceil(pageMaker.total / pageMaker.cri.amount);

    //이전 페이지 버튼
    pageMaker.prev = current > 1;
    const prevBtn  = createPaginationButton("<", current -1, !pageMaker.prev, false, itemNm, category);
    pagination.appendChild(prevBtn);

    //페이지 번호들
    const pages = generatePageNumbers(current,total);

    pages.forEach(p => {
        if(p === "..."){
            const ellipsis = document.createElement("span");
            ellipsis.className = "pagination-ellipsis";
            ellipsis.textContent = "...";
            pagination.appendChild(ellipsis);
        }else {
            const btn = createPaginationButton(p, p, false, p === current, itemNm, category);
            pagination.appendChild(btn);
        }
    });

    //다음 페이지 버튼
    pageMaker.next = current < total;
    const nextBtn = createPaginationButton("›", current + 1, !pageMaker.next, false, itemNm, category);
    pagination.appendChild(nextBtn);
}

// 페이지네이션 버튼 생성
function createPaginationButton(text, pageNum, disabled = false, active = false, itemNm = "", category = "") {
    const button = document.createElement('button');
    button.className = `pagination-btn ${active ? 'active' : ''} ${disabled ? 'disabled' : ''}`;
    button.textContent = text;

    // 비활성 상태가 아닌 경우 클릭 이벤트
    button.addEventListener('click', () => {
        if(disabled) return;         //내부에서 클릭제한
        loadItems(pageNum, itemNm, category);
        window.scrollTo({ top: 0, behavior: 'smooth' }); // 페이지 상단으로 스크롤
    });

    return button;
}

// 페이지 번호 생성 (스마트 페이지네이션)
function generatePageNumbers(current, total) {
    const pages = [];
    const maxVisible = 7; // 최대 표시할 페이지 수

    if (total <= maxVisible) {
        // 총 페이지가 7개 이하면 모두 표시
        for (let i = 1; i <= total; i++) {
            pages.push(i);
        }
    } else {
        // 총 페이지가 7개 초과인 경우 스마트 표시
        if (current <= 4) {
            // 현재 페이지가 앞쪽에 있을 때
            for (let i = 1; i <= 5; i++) {
                pages.push(i);
            }
            pages.push('...');
            pages.push(total);
        } else if (current >= total - 3) {
            // 현재 페이지가 뒤쪽에 있을 때
            pages.push(1);
            pages.push('...');
            for (let i = total - 4; i <= total; i++) {
                pages.push(i);
            }
        } else {
            // 현재 페이지가 중간에 있을 때
            pages.push(1);
            pages.push('...');
            for (let i = current - 1; i <= current + 1; i++) {
                pages.push(i);
            }
            pages.push('...');
            pages.push(total);
        }
    }

    return pages;
}

/* ==========================================================================
   이벤트 리스너 설정
   ========================================================================== */
//검색 필터 이벤트 리스너
function setupFilterForm(){
    console.log('🔧 검색 필터 이벤트 리스너 실행...');

    const filterForm = document.getElementById("filterForm");
    if(!filterForm) return;

    filterForm.addEventListener("submit", function (e){
        e.preventDefault();      //기본 동작 막기

        const searchType = document.getElementById("searchType").value;
        const keyword = document.getElementById("searchInput").value.trim();

        //검색 기준에 따라 itemNm 또는 category에 값 전달
        let itemNm = "";
        let category = "";

        if(searchType === "itemNm") {
            itemNm = keyword;
        } else if(searchType === "category") {
            category = keyword;
        } else {
            itemNm = keyword;
            category = keyword;
        }

        loadItems(1, itemNm, category);      //1페이지부터 검색 조건 반영
    });
}

/* ==========================================================================
   알림 시스템
   ========================================================================== */

// 알림 메시지 표시
function showNotification(message, type = 'success') {
    // 기존 알림이 있으면 제거
    const existingNotification = document.querySelector('.notification');
    if (existingNotification) {
        existingNotification.remove();
    }

    // 새 알림 요소 생성
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;

    // 알림 스타일 설정
    notification.style.cssText = `
        position: fixed;
        top: 100px;
        right: 20px;
        background: ${type === 'success' ? 'var(--primary-green)' : type === 'error' ? '#dc3545' : 'var(--accent-green)'};
        color: white;
        padding: 15px 20px;
        border-radius: 8px;
        box-shadow: 0 4px 20px rgba(0,0,0,0.1);
        z-index: 10000;
        transform: translateX(400px);
        transition: transform 0.3s ease;
        max-width: 300px;
        font-weight: 500;
    `;

    document.body.appendChild(notification);

    // 애니메이션으로 표시
    setTimeout(() => {
        notification.style.transform = 'translateX(0)';
    }, 100);

    // 3초 후 자동 제거
    setTimeout(() => {
        notification.style.transform = 'translateX(400px)';
        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 300);
    }, 3000);
}

/* ==========================================================================
   유틸리티 함수
   ========================================================================== */

// 디바운스 함수 (검색 입력 최적화용)
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

/* ==========================================================================
   페이드인 애니메이션 설정 (새로 추가)
   ========================================================================== */

function setupFadeInAnimation() {
    console.log('🎭 페이드인 애니메이션 설정...');

    // Intersection Observer 설정
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    };

    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('visible');
            }
        });
    }, observerOptions);

    // 페이드인 효과를 적용할 요소들 관찰
    document.querySelectorAll('.fade-in').forEach(el => {
        observer.observe(el);
    });
}