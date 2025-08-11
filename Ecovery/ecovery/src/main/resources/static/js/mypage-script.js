/**
 * ==========================================================================
 * ECOVERY 개선된 마이페이지 JavaScript
 * 4x4 그리드 레이아웃과 회원정보 수정 모달창 포함
 * ==========================================================================
 */

// ==========================================================================
// 전역 변수 선언
// ==========================================================================
let isInitialized = false;               // 초기화 상태 플래그
let animationObserver;                   // 스크롤 애니메이션 관찰자
let profileModalElement;                 // 프로필 모달 요소
let isFormDirty = false;                // 폼 수정 상태 플래그

// ==========================================================================
// 페이지 초기화 - DOMContentLoaded 이벤트 리스너
// ==========================================================================

function initializeHeader() {}
function initializeCart() {}
function initializeCounters() {}
function initializeObserver() {}
function initializeSettings() {}
function initializeInteractions() {}
function initializePageLifecycle() {}
function adjustLayoutForScreenSize() {}
function enhanceAccessibility() {}
function loadUserPreferences() {}

document.addEventListener('DOMContentLoaded', function() {
    try {
        console.log('🌱 ECOVERY 개선된 마이페이지 초기화를 시작합니다...');

        // 핵심 기능 초기화
        initializeHeader();              // 헤더 기능 초기화
        initializeCart();                // 장바구니 기능 초기화
        initializeCounters();            // 카운터 애니메이션 초기화
        initializeObserver();            // Intersection Observer 초기화
        initializeSettings();            // 설정 기능 초기화
        initializeInteractions();        // 인터랙션 초기화
        initializeKeyboardShortcuts();   // 키보드 단축키 초기화
        initializePageLifecycle();       // 페이지 라이프사이클 관리

        // 모달 초기화
        initializeModal();

        // 성능 최적화
        optimizePerformance();

        // 초기 레이아웃 조정
        adjustLayoutForScreenSize();

        // 접근성 기능 향상
        enhanceAccessibility();

        // 사용자 설정 로드
        loadUserPreferences();

        isInitialized = true;
        console.log('🌱 ECOVERY 개선된 마이페이지가 성공적으로 초기화되었습니다.');

        // 환영 메시지 표시 (1초 후)
        setTimeout(() => {
            showNotification('ECOVERY 마이페이지에 오신 것을 환영합니다! 🌱', 'success');
        }, 1000);

    } catch (error) {
        handleError(error, 'Page initialization');
    }
});

// ==========================================================================
// 모달창 관련 기능
// ==========================================================================

/**
 * 모달창 초기화
 */
function initializeModal() {
    profileModalElement = document.getElementById('profileModal');

    if (!profileModalElement) {
        console.warn('프로필 모달 요소를 찾을 수 없습니다.');
        return;
    }

    // ESC 키로 모달 닫기
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape' && profileModalElement.classList.contains('active')) {
            closeProfileModal();
        }
    });

    // 폼 변경 감지
    const formInputs = profileModalElement.querySelectorAll('input');
    formInputs.forEach(input => {
        input.addEventListener('input', () => {
            isFormDirty = true;
        });
    });

    console.log('✅ 모달창이 초기화되었습니다.');
}

/**
 * 프로필 수정 모달 열기
 */
function openProfileModal() {
    if (!profileModalElement) {
        console.error('프로필 모달 요소를 찾을 수 없습니다.');
        return;
    }

    // 현재 사용자 정보로 폼 채우기
    populateModalForm();

    // 모달 표시
    profileModalElement.classList.add('active');
    document.body.style.overflow = 'hidden'; // 스크롤 방지

    // 첫 번째 입력 필드에 포커스
    const firstInput = profileModalElement.querySelector('input');
    if (firstInput) {
        setTimeout(() => firstInput.focus(), 100);
    }

    // 폼 변경 상태 초기화
    isFormDirty = false;

    console.log('프로필 수정 모달을 열었습니다.');
    showNotification('회원정보를 수정할 수 있습니다.', 'info');
}

/**
 * 프로필 수정 모달 닫기
 */
function closeProfileModal() {
    if (!profileModalElement) return;

    // 폼이 수정된 경우 확인 메시지
    if (isFormDirty) {
        if (!confirm('변경사항을 저장하시겠습니까?')) {
            return;
        }
    }

    // 모달 숨기기
    profileModalElement.classList.remove('active');
    document.body.style.overflow = ''; // 스크롤 복구

    // 폼 리셋
    resetModalForm();
    isFormDirty = false;

    console.log('프로필 수정 모달을 닫았습니다.');
}

/**
 * 모달 폼에 현재 사용자 정보 채우기
 */
function populateModalForm() {
    // 전역 변수에서 사용자 데이터 가져오기 (Thymeleaf에서 설정)
    if (typeof window.userData !== 'undefined' && window.userData) {
        const nicknameInput = document.getElementById('editNickname');
        const emailInput = document.getElementById('editEmail');

        if (nicknameInput && window.userData.nickname) {
            nicknameInput.value = window.userData.nickname;
        }
        if (emailInput && window.userData.email) {
            emailInput.value = window.userData.email;
        }
    }
}

/**
 * 모달 폼 리셋
 */
function resetModalForm() {
    const form = document.getElementById('profileEditForm');
    if (form) {
        form.reset();
    }

    // 검증 메시지 제거
    clearValidationMessages();
}

/**
 * 프로필 변경사항 저장
 */
function saveProfileChanges() {

    const currentPwInput = document.getElementById('currentPassword');
    const newPwInput = document.getElementById('newPassword');
    const confirmPwInput = document.getElementById('confirmPassword');

    const currentPwError = document.getElementById('currentPasswordError');
    const newPwError = document.getElementById('newPasswordError');
    const confirmPwError = document.getElementById('confirmPasswordError');

    // 에러 초기화
    if (currentPwError) {
        currentPwError.classList.add('error-hidden');
        currentPwError.textContent = ''; // 이전 메시지 내용을 비워줍니다.
    }
    if (newPwError) {
        newPwError.classList.add('error-hidden');
        newPwError.textContent = '';
    }
    if (confirmPwError) {
        confirmPwError.classList.add('error-hidden');
        confirmPwError.textContent = '';
    }

    if (currentPwInput) currentPwInput.classList.remove('error');
    if (newPwInput) newPwInput.classList.remove('error');
    if (confirmPwInput) confirmPwInput.classList.remove('error');

    // 2. 입력 필드 값 가져오기
    const currentPassword = document.getElementById('currentPassword')?.value || '';
    const newPassword = document.getElementById('newPassword')?.value || '';
    const confirmPassword = document.getElementById('confirmPassword')?.value || '';


    // 유효성 검사
    let isValid = true;

    // 현재 비밀번호 입력 확인
    if (!currentPassword) {
        if (currentPwError) {
            currentPwError.classList.remove('error-hidden'); // 에러 메시지 보이기
            currentPwError.textContent = '현재 비밀번호를 입력해주세요.';
        }
        if (currentPwInput) currentPwInput.classList.add('error');
        isValid = false;
    }

    // 새 비밀번호 형식 검사
    const hasLetter = /[a-zA-Z]/.test(newPassword);
    const hasNumber = /\d/.test(newPassword);
    const hasSpecial = /[!@#$%^&*(),.?":{}|<>]/.test(newPassword);
    if (!newPassword || newPassword.length < 8 || !hasLetter || !hasNumber || !hasSpecial) {
        if (newPwError) {
            newPwError.classList.remove('error-hidden'); // 에러 메시지 보이기
            newPwError.textContent = '8자 이상, 영문/숫자/특수문자를 포함해야 합니다.';
        }
        if (newPwInput) newPwInput.classList.add('error');
        isValid = false;
    }

    // 새 비밀번호 일치 확인
    if (newPassword !== confirmPassword) {
        if (confirmPwError) {
            confirmPwError.classList.remove('error-hidden'); // 에러 메시지 보이기
            confirmPwError.textContent = '비밀번호가 일치하지 않습니다.';
        }
        if (confirmPwInput) confirmPwInput.classList.add('error');
        isValid = false;
    }

    if (!isValid) {
        return;
    }

    // 서버에 fetch 요청
    fetch("/mypage/update", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded",
            "X-Requested-With": "XMLHttpRequest"
        },
        body: new URLSearchParams({
            currentPassword: currentPassword,
            password: newPassword
        })
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                showNotification(data.message, "success");
                closeProfileModal();
            } else {
                // 서버에서 오류 응답을 받았을 때
                if (data.errorCode === "WRONG_PASSWORD") {
                    if (currentPwError) {
                        currentPwError.classList.remove('error-hidden'); // 에러 메시지 보이기
                        currentPwError.textContent = data.message;
                    }
                    if (currentPwInput) currentPwInput.classList.add('error');
                } else {
                    showNotification(data.message, "error");
                }
            }
        })
        .catch(error => {
            console.error("비밀번호 변경 오류:", error);
            showNotification("비밀번호 변경 중 오류가 발생했습니다.", "error");
        });
}

/**
 * 프로필 폼 검증
 */
function validateProfileForm() {
    const currentPassword = document.getElementById('currentPassword').value;
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    let isValid = true;

    // 비밀번호 변경 시 검증
    if (newPassword || currentPassword) {
        if (!currentPassword) {
            showNotification('현재 비밀번호가 일치하지 않습니다.', 'error');
            isValid = false;
        }

        if (newPassword !== confirmPassword) {
            showNotification('새 비밀번호가 일치하지 않습니다.', 'error');
            isValid = false;
        }

        if (newPassword.length < 8) {
            showNotification('새 비밀번호는 8자 이상이어야 합니다.', 'error');
            isValid = false;
        }


    }

    return isValid;
}

/**
 * 표시된 사용자 정보 업데이트
 */
function updateDisplayedUserInfo() {
    const nickname = document.getElementById('editNickname').value.trim();
    const email = document.getElementById('editEmail').value.trim();

    // 페이지의 사용자 이름 업데이트
    const userNameElement = document.querySelector('.user-name');
    if (userNameElement && nickname) {
        userNameElement.textContent = nickname + '님';
    }

    // 페이지의 이메일 업데이트
    const userEmailElement = document.querySelector('.user-email');
    if (userEmailElement && email) {
        userEmailElement.textContent = '이메일 : ' + email;
    }
}

// ==========================================================================
// 대시보드 카드 이벤트 초기화
// ==========================================================================

/**
 * 대시보드 카드들의 이벤트 초기화
 */
function initializeDashboardCards() {
    // 구매 내역 아이템 클릭 이벤트
    const purchaseItems = document.querySelectorAll('.purchase-item');
    purchaseItems.forEach(item => {
        item.addEventListener('click', handlePurchaseItemClick);
        item.addEventListener('keydown', handleKeyboardActivation);
        item.setAttribute('tabindex', '0');
        item.setAttribute('role', 'button');
        item.setAttribute('aria-label', '구매 내역 상세보기');
    });

    // 나눔 아이템 클릭 이벤트
    const sharingItems = document.querySelectorAll('.sharing-item');
    sharingItems.forEach(item => {
        item.addEventListener('click', handleSharingItemClick);
        item.addEventListener('keydown', handleKeyboardActivation);
        item.setAttribute('tabindex', '0');
        item.setAttribute('role', 'button');
        item.setAttribute('aria-label', '나눔 내역 상세보기');
    });

    // 분리배출 아이템 클릭 이벤트
    const wasteItems = document.querySelectorAll('.waste-item');
    wasteItems.forEach(item => {
        item.addEventListener('click', handleWasteItemClick);
        item.addEventListener('keydown', handleKeyboardActivation);
        item.setAttribute('tabindex', '0');
        item.setAttribute('role', 'button');
        item.setAttribute('aria-label', '분리배출 내역 상세보기');
    });

    // 포인트 아이템 클릭 이벤트
    const pointsItems = document.querySelectorAll('.points-item');
    pointsItems.forEach(item => {
        item.addEventListener('click', handlePointsItemClick);
        item.addEventListener('keydown', handleKeyboardActivation);
        item.setAttribute('tabindex', '0');
        item.setAttribute('role', 'button');
        item.setAttribute('aria-label', '포인트 내역 상세보기');
    });



    console.log('✅ 대시보드 카드 이벤트가 초기화되었습니다.');
}

/**
 * 구매 내역 아이템 클릭 처리
 */
function handlePurchaseItemClick(event) {
    const item = event.currentTarget;
    const orderId = item.getAttribute('data-order-id');
    const itemName = item.querySelector('.item-name').textContent;

    // 클릭 애니메이션
    animateClickEffect(item);

    if (orderId) {
        showNotification(`"${itemName}" 주문 상세 페이지로 이동합니다.`, 'info');
        setTimeout(() => {
            // 실제 구현에서는 주문 상세 페이지로 이동
            window.location.href = `/orders/detail/${orderId}`;
        }, 800);
    } else {
        showNotification(`"${itemName}" 상세 정보를 확인합니다.`, 'info');
    }

    console.log(`구매 내역 클릭: ${itemName}, 주문번호: ${orderId}`);
}

/**
 * 나눔 아이템 클릭 처리
 */
function handleSharingItemClick(event) {
    const item = event.currentTarget;
    const itemName = item.querySelector('.item-name').textContent;

    animateClickEffect(item);
    showNotification(`"${itemName}" 나눔 상세 정보를 확인합니다.`, 'info');

    console.log(`나눔 내역 클릭: ${itemName}`);
}

/**
 * 분리배출 아이템 클릭 처리
 */
function handleWasteItemClick(event) {
    const item = event.currentTarget;
    const itemName = item.querySelector('.item-name').textContent;

    animateClickEffect(item);
    showNotification(`"${itemName}" 분리배출 상세 내역을 확인합니다.`, 'info');

    console.log(`분리배출 내역 클릭: ${itemName}`);
}

/**
 * 포인트 아이템 클릭 처리
 */
function handlePointsItemClick(event) {
    const item = event.currentTarget;
    const itemDesc = item.querySelector('.item-desc').textContent;

    animateClickEffect(item);
    showNotification(`"${itemDesc}" 포인트 상세 내역을 확인합니다.`, 'info');

    console.log(`포인트 내역 클릭: ${itemDesc}`);
}




// ==========================================================================
// 빠른 액션 함수들
// ==========================================================================

/**
 * 장바구니 페이지로 이동
 */
function goToCart() {
    showNotification('장바구니 페이지로 이동합니다! 🛒', 'info');
    setTimeout(() => {
        window.location.href = '/cart/list';
    }, 800);
    console.log('장바구니 페이지로 이동');
}

/**
 * 새 나눔 등록
 */
function createSharing() {
    showNotification('새 나눔 등록 페이지로 이동합니다! 🤝', 'info');
    setTimeout(() => {
        window.location.href = '/free-sharing/register';
    }, 800);
    console.log('새 나눔 등록');
}

/**
 * 분리배출 빠른 실행
 */
function quickWasteSorting() {
    showNotification('AI 분리배출 페이지로 이동합니다! ♻️', 'info');
    setTimeout(() => {
        window.location.href = '/waste-sorting';
    }, 800);
    console.log('분리배출 빠른 실행');
}

/**
 * 포인트 내역 전체보기
 */
function showPointHistory() {
    showNotification('포인트 내역 전체보기 페이지로 이동합니다! 💎', 'info');
    setTimeout(() => {
        window.location.href = '/points/history';
    }, 800);
    console.log('포인트 내역 전체보기');
}

// ==========================================================================
// 애니메이션 및 효과
// ==========================================================================

/**
 * 애니메이션 초기화
 */
function initializeAnimations() {
    // Intersection Observer로 스크롤 애니메이션
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    };

    animationObserver = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('animate-in');

                // 통계 숫자 애니메이션
                const statNumbers = entry.target.querySelectorAll('.stat-number');
                statNumbers.forEach((stat, index) => {
                    setTimeout(() => {
                        animateNumber(stat);
                    }, index * 200);
                });

                animationObserver.unobserve(entry.target);
            }
        });
    }, observerOptions);

    // 관찰할 요소들 등록
    const animateElements = document.querySelectorAll('.dashboard-card');
    animateElements.forEach(el => {
        el.style.opacity = '0';
        el.style.transform = 'translateY(30px)';
        el.style.transition = 'all 0.6s ease';
        animationObserver.observe(el);
    });

    console.log('✅ 애니메이션이 초기화되었습니다.');
}

/**
 * 숫자 애니메이션
 */
function animateNumber(element) {
    const targetText = element.textContent;
    const targetNumber = parseFloat(targetText.replace(/[^\d.]/g, ''));

    if (isNaN(targetNumber)) return;

    let current = 0;
    const increment = targetNumber / 30;
    const timer = setInterval(() => {
        current += increment;
        if (current >= targetNumber) {
            current = targetNumber;
            clearInterval(timer);
        }

        // 숫자 포맷팅 유지
        if (targetText.includes('₩')) {
            element.textContent = '₩' + Math.floor(current).toLocaleString();
        } else if (targetText.includes('P')) {
            element.textContent = Math.floor(current).toLocaleString() + 'P';
        } else if (targetText.includes('kg')) {
            element.textContent = current.toFixed(1) + 'kg';
        } else {
            element.textContent = Math.floor(current);
        }
    }, 50);
}

/**
 * 클릭 효과 애니메이션
 */
function animateClickEffect(element) {
    element.style.transform = 'scale(0.98)';
    setTimeout(() => {
        element.style.transform = '';
    }, 150);
}

// ==========================================================================
// 폼 검증 및 유틸리티
// ==========================================================================

/**
 * 폼 검증 초기화
 */
function initializeFormValidation() {
    // 실시간 입력 검증
    const inputs = document.querySelectorAll('#profileEditForm input');
    inputs.forEach(input => {
        input.addEventListener('blur', validateInput);
        input.addEventListener('input', clearInputValidation);
    });

    console.log('폼 검증이 초기화되었습니다.');
}

/**
 * 입력 필드 검증
 */
function validateInput(event) {
    const input = event.target;
    const value = input.value.trim();

    switch(input.id) {

        case 'newPassword':
            if (value && value.length < 8) {
                showValidationMessage(input, '비밀번호는 8자 이상이어야 합니다.', 'error');
            }
            break;
        case 'confirmPassword':
        case 'confirmPassword':textContent
            const newPassword = document.getElementById('newPassword').value;
            if (value && value !== newPassword) {
                showValidationMessage(input, '비밀번호가 일치하지 않습니다.', 'error');
            }
            break;
    }
}

/**
 * 입력 필드 검증 메시지 제거
 */
function clearInputValidation(event) {
    const input = event.target;
    clearValidationMessage(input);
}

/**
 * 검증 메시지 표시
 */
function showValidationMessage(input, message, type) {
    clearValidationMessage(input);

    const messageElement = document.createElement('div');
    messageElement.className = `validation-message validation-${type}`;
    messageElement.textContent = message;
    messageElement.style.cssText = `
        font-size: 12px;
        margin-top: 5px;
        padding: 5px 8px;
        border-radius: 4px;
        transition: all 0.3s ease;
    `;

    if (type === 'error') {
        messageElement.style.background = 'rgba(220, 53, 69, 0.1)';
        messageElement.style.color = '#dc3545';
        input.style.borderColor = '#dc3545';
    } else if (type === 'success') {
        messageElement.style.background = 'rgba(40, 167, 69, 0.1)';
        messageElement.style.color = '#28a745';
        input.style.borderColor = '#28a745';
    } else {
        messageElement.style.background = 'rgba(23, 162, 184, 0.1)';
        messageElement.style.color = '#17a2b8';
        input.style.borderColor = '#17a2b8';
    }

    input.parentNode.appendChild(messageElement);
}

/**
 * 검증 메시지 제거
 */
function clearValidationMessage(input) {
    const existingMessage = input.parentNode.querySelector('.validation-message');
    if (existingMessage) {
        existingMessage.remove();
    }
    input.style.borderColor = '';
}

/**
 * 모든 검증 메시지 제거
 */
function clearValidationMessages() {
    const messages = document.querySelectorAll('.validation-message');
    messages.forEach(message => {
        message.classList.add('error-hidden');
        message.textContent = '';
    });

    const inputs = document.querySelectorAll('#profileEditForm input');
    inputs.forEach(input => {
        input.style.borderColor = '';
        input.classList.remove('error');
    });
}

// ==========================================================================
// 키보드 단축키 및 접근성
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

        switch(e.key.toLowerCase()) {
            case 'e':
                if (e.ctrlKey || e.metaKey) {
                    e.preventDefault();
                    openProfileModal();
                }
                break;
            case 'escape':
                if (profileModalElement && profileModalElement.classList.contains('active')) {
                    closeProfileModal();
                }
                break;
        }
    });

    console.log('✅ 키보드 단축키가 초기화되었습니다.');
}

/**
 * 키보드 활성화 처리
 */
function handleKeyboardActivation(event) {
    if (event.key === 'Enter' || event.key === ' ') {
        event.preventDefault();
        event.currentTarget.click();
    }
}

/**
 * 접근성 기능 초기화
 */
function initializeAccessibility() {
    // ARIA 라벨 추가
    const dashboardCards = document.querySelectorAll('.dashboard-card');
    dashboardCards.forEach(card => {
        const title = card.querySelector('h3')?.textContent;
        if (title) {
            card.setAttribute('aria-label', `섹션: ${title}`);
            card.setAttribute('role', 'region');
        }
    });

    // 포커스 관리
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Tab') {
            document.body.classList.add('keyboard-navigation');
        }
    });

    document.addEventListener('mousedown', () => {
        document.body.classList.remove('keyboard-navigation');
    });

    console.log('✅ 접근성 기능이 초기화되었습니다.');
}

// ==========================================================================
// 성능 최적화 및 반응형
// ==========================================================================

/**
 * 성능 최적화
 */
function optimizePerformance() {
    // 이미지 지연 로딩
    const images = document.querySelectorAll('img[data-src]');
    if (images.length > 0) {
        const imageObserver = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    const img = entry.target;
                    img.src = img.dataset.src;
                    img.removeAttribute('data-src');
                    imageObserver.unobserve(img);
                }
            });
        });

        images.forEach(img => imageObserver.observe(img));
    }

    console.log('✅ 성능 최적화가 적용되었습니다.');
}

/**
 * 반응형 레이아웃 조정
 */
function adjustResponsiveLayout() {
    const handleResize = () => {
        const width = window.innerWidth;

        // 모바일에서 모달 크기 조정
        if (profileModalElement) {
            if (width < 768) {
                profileModalElement.style.padding = '10px';
            } else {
                profileModalElement.style.padding = '20px';
            }
        }
    };

    window.addEventListener('resize', handleResize);
    handleResize(); // 초기 실행

    console.log('✅ 반응형 레이아웃이 초기화되었습니다.');
}

// ==========================================================================
// 사용자 설정 및 유틸리티
// ==========================================================================

/**
 * 사용자 설정 로드
 */
function loadUserPreferences() {
    try {
        const preferences = JSON.parse(localStorage.getItem('mypagePreferences') || '{}');

        // 설정 적용
        if (preferences.theme) {
            document.body.setAttribute('data-theme', preferences.theme);
        }

        console.log('✅ 사용자 설정이 로드되었습니다.');
    } catch (error) {
        console.warn('사용자 설정 로드 실패:', error);
    }
}

/**
 * 사용자 설정 저장
 */
function saveUserPreferences() {
    const preferences = {
        theme: document.body.getAttribute('data-theme') || 'light',
        lastVisit: new Date().toISOString()
    };

    try {
        localStorage.setItem('mypagePreferences', JSON.stringify(preferences));
        console.log('사용자 설정이 저장되었습니다.');
    } catch (error) {
        console.warn('사용자 설정 저장 실패:', error);
    }
}

/**
 * API 호출 시뮬레이션
 */
function simulateApiCall(callback, delay = 1000) {
    setTimeout(() => {
        try {
            callback();
        } catch (error) {
            console.error('API 호출 시뮬레이션 오류:', error);
            showNotification('처리 중 오류가 발생했습니다.', 'error');
        }
    }, delay);
}

/**
 * 알림 시스템
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
        z-index: 10001;
        transform: translateX(400px);
        transition: transform 0.4s cubic-bezier(0.4, 0, 0.2, 1);
        max-width: 350px;
        min-width: 280px;
        overflow: hidden;
    `;

    const content = notification.querySelector('.notification-content');
    content.style.cssText = `
        display: flex;
        align-items: center;
        padding: 15px 20px;
        gap: 10px;
    `;

    const icon = notification.querySelector('.notification-icon');
    icon.style.cssText = `font-size: 18px; flex-shrink: 0;`;

    const text = notification.querySelector('.notification-text');
    text.style.cssText = `flex: 1; font-weight: 500; font-size: 14px; line-height: 1.4;`;

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

    // 자동 숨김
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
 * 에러 처리
 */
function handleError(error, context = '') {
    console.error(`Error in ${context}:`, error);
    showNotification(`오류가 발생했습니다: ${error.message}`, 'error');
}

// ==========================================================================
// 전역 함수 노출
// ==========================================================================
window.openProfileModal = openProfileModal;
window.closeProfileModal = closeProfileModal;
window.saveProfileChanges = saveProfileChanges;
window.createSharing = createSharing;
window.quickWasteSorting = quickWasteSorting;
window.showPointHistory = showPointHistory;
window.showNotification = showNotification;

// 페이지 언로드 시 설정 저장
window.addEventListener('beforeunload', saveUserPreferences);

// 전역 에러 핸들러
window.addEventListener('error', (e) => {
    handleError(e.error, 'Global error');
});

window.addEventListener('unhandledrejection', (e) => {
    handleError(new Error(e.reason), 'Unhandled promise rejection');
});

console.log('🌱 ECOVERY 개선된 마이페이지 JavaScript가 로드되었습니다.');