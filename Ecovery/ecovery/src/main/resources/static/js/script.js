// ECOVERY 스마트 환경 플랫폼 JavaScript

// Global variables
let currentSlide = 0;
let totalSlides = 4;
let slideInterval;
let activityInterval;
let resizeTimeout;

// 가라데이터 정의
const DEMO_DATA = {
    // 메인 통계 데이터
    stats: {
        monthlyWaste: 15680,      // 월간 처리량 (톤)
        activeUsers: 12340,       // 활성 사용자
        accuracy: 99.3,           // 분류 정확도 (%)
        uptime: 24                // 시간 무중단 운영
    },

    // 실시간 임팩트 데이터
    impact: {
        carbonSaved: 1247,        // 오늘 절약된 탄소 (kg CO₂)
        wasteRecycled: 834,       // 재활용된 폐기물 (톤)
        sharingCompleted: 156,    // 무료나눔 완료 (개)
        ecoProducts: 89           // 친환경 제품 판매 (건)
    },

    // 오늘의 활동 현황
    dailyActivity: {
        disposal: 1247,           // 분리배출
        sharing: 156,             // 무료나눔
        ecoShopping: 189,         // 친환경 구매
        community: 234            // 커뮤니티 참여
    }
};

// DOM Elements
const header = document.getElementById('header');
const hamburger = document.getElementById('hamburger');
const navMenu = document.getElementById('navMenu');

// 푸터 로드 함수
async function loadFooter() {
    try {
        const response = await fetch('footer.html');
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const footerHTML = await response.text();
        const footerContainer = document.getElementById('footer-container');
        if (footerContainer) {
            footerContainer.innerHTML = footerHTML;
            console.log('✅ 푸터가 성공적으로 로드되었습니다.');
        }
    } catch (error) {
        console.error('❌ 푸터 로드 실패:', error);
        // 푸터 로드 실패 시 기본 푸터 표시
        showFallbackFooter();
    }
}

// 푸터 로드 실패 시 기본 푸터 표시
function showFallbackFooter() {
    const footerContainer = document.getElementById('footer-container');
    if (footerContainer) {
        footerContainer.innerHTML = `
            <div class="container">
                <div class="footer-content">
                    <div class="footer-section">
                        <div class="logo" style="margin-bottom: 20px;">
                            <span class="logo-icon">🌱</span>
                            <span class="logo-text">ECOVERY</span>
                        </div>
                        <p>첨단 기술로 실현하는 스마트하고 지속 가능한 환경 플랫폼</p>
                    </div>
                    <div class="footer-section">
                        <h4>서비스</h4>
                        <ul>
                            <li><a href="/disposal/disposalMain">스마트 분리배출</a></li>
                            <li><a href="/free/list">무료나눔</a></li>
                            <li><a href="/eco/list">에코마켓</a></li>
                            <li><a href="/env/list">환경톡톡</a></li>
                        </ul>
                    </div>
                    <div class="footer-section">
                        <h4>고객지원</h4>
                        <ul>
                            <li><a href="#">문의하기</a></li>
                            <li><a href="#">이용가이드</a></li>
                            <li><a href="#">기술지원</a></li>
                            <li><a href="#">고객센터</a></li>
                        </ul>
                    </div>
                </div>
                <div class="footer-bottom">
                    <p>&copy; 2025 ECOVERY. All rights reserved.</p>
                    <div class="footer-links">
                        <a href="#">개인정보처리방침</a>
                        <a href="#">이용약관</a>
                    </div>
                </div>
            </div>
        `;
        console.log('⚠️ 기본 푸터를 표시했습니다.');
    }
}

// Screen size detection
function getScreenSize() {
    const width = window.innerWidth;
    if (width >= 1600) return 'ultra-wide';
    if (width >= 1400) return 'extra-large';
    if (width >= 1200) return 'large';
    if (width >= 768) return 'tablet';
    return 'mobile';
}

// Header scroll effect
window.addEventListener('scroll', () => {
    if (window.scrollY > 100) {
        header.classList.add('scrolled');
    } else {
        header.classList.remove('scrolled');
    }
});

// Mobile menu toggle
if (hamburger && navMenu) {
    hamburger.addEventListener('click', () => {
        hamburger.classList.toggle('active');
        navMenu.classList.toggle('active');

        // Animate hamburger
        const spans = hamburger.querySelectorAll('span');
        if (hamburger.classList.contains('active')) {
            spans[0].style.transform = 'rotate(45deg) translate(5px, 5px)';
            spans[1].style.opacity = '0';
            spans[2].style.transform = 'rotate(-45deg) translate(7px, -6px)';
        } else {
            spans[0].style.transform = 'none';
            spans[1].style.opacity = '1';
            spans[2].style.transform = 'none';
        }
    });
}

// Close mobile menu when clicking on a link
function closeMobileMenu() {
    if (hamburger && navMenu) {
        hamburger.classList.remove('active');
        navMenu.classList.remove('active');

        const spans = hamburger.querySelectorAll('span');
        spans[0].style.transform = 'none';
        spans[1].style.opacity = '1';
        spans[2].style.transform = 'none';
    }
}

// Smooth scrolling
function scrollToSection(sectionId) {
    const element = document.getElementById(sectionId);
    if (element) {
        const headerHeight = header ? header.offsetHeight : 0;
        const elementPosition = element.offsetTop - headerHeight;

        window.scrollTo({
            top: elementPosition,
            behavior: 'smooth'
        });
    }
}

// Navigation link smooth scrolling
document.querySelectorAll('.nav-menu a').forEach(link => {
    link.addEventListener('click', (e) => {
        const href = link.getAttribute('href');

        if (href && href.startsWith('#')) {
            e.preventDefault();
            scrollToSection(href.substring(1));
            closeMobileMenu();
        }
    });
});

// Services Slider Functions
function updateSliderForScreenSize() {
    const screenSize = getScreenSize();
    const slidesWrapper = document.getElementById('slidesWrapper');

    switch (screenSize) {
        case 'ultra-wide':
        case 'extra-large':
            totalSlides = 4;
            if (slidesWrapper) {
                slidesWrapper.style.width = '400%';
            }
            break;
        default:
            totalSlides = 4;
            if (slidesWrapper) {
                slidesWrapper.style.width = '400%';
            }
    }

    if (currentSlide >= totalSlides) {
        currentSlide = 0;
    }
    showSlide(currentSlide);
}

function showSlide(slideIndex) {
    const slidesWrapper = document.getElementById('slidesWrapper');
    if (slidesWrapper) {
        const translateX = -slideIndex * 25;
        slidesWrapper.style.transform = `translateX(${translateX}%)`;
    }
    currentSlide = slideIndex;
}

function nextSlide() {
    currentSlide = (currentSlide + 1) % totalSlides;
    showSlide(currentSlide);
}

function prevSlide() {
    currentSlide = (currentSlide - 1 + totalSlides) % totalSlides;
    showSlide(currentSlide);
}

function startAutoSlide() {
    slideInterval = setInterval(nextSlide, 4000); // 4초마다 자동 슬라이드
}

function stopAutoSlide() {
    if (slideInterval) {
        clearInterval(slideInterval);
    }
}

// Initialize slider
function initializeSlider() {
    let startX = 0;
    let endX = 0;
    let isDragging = false;

    const sliderContainer = document.querySelector('.services-slider');
    const slidesWrapper = document.getElementById('slidesWrapper');

    if (sliderContainer && slidesWrapper) {
        // Mouse events
        sliderContainer.addEventListener('mousedown', (e) => {
            isDragging = true;
            startX = e.clientX;
            sliderContainer.style.cursor = 'grabbing';
            slidesWrapper.style.transition = 'none';
        });

        sliderContainer.addEventListener('mousemove', (e) => {
            if (!isDragging) return;
            e.preventDefault();

            const currentX = e.clientX;
            const diff = startX - currentX;
            const currentTransform = -currentSlide * 25;
            const newTransform = currentTransform - (diff / sliderContainer.offsetWidth) * 25;

            slidesWrapper.style.transform = `translateX(${newTransform}%)`;
        });

        sliderContainer.addEventListener('mouseup', (e) => {
            if (!isDragging) return;
            isDragging = false;
            sliderContainer.style.cursor = 'grab';
            slidesWrapper.style.transition = 'transform 0.8s cubic-bezier(0.23, 1, 0.320, 1)';

            endX = e.clientX;
            handleSwipe();
        });

        sliderContainer.addEventListener('mouseleave', () => {
            if (isDragging) {
                isDragging = false;
                sliderContainer.style.cursor = 'grab';
                slidesWrapper.style.transition = 'transform 0.8s cubic-bezier(0.23, 1, 0.320, 1)';
                showSlide(currentSlide);
            }
        });

        // Touch events
        sliderContainer.addEventListener('touchstart', (e) => {
            startX = e.touches[0].clientX;
            slidesWrapper.style.transition = 'none';
        });

        sliderContainer.addEventListener('touchmove', (e) => {
            const currentX = e.touches[0].clientX;
            const diff = startX - currentX;
            const currentTransform = -currentSlide * 25;
            const newTransform = currentTransform - (diff / sliderContainer.offsetWidth) * 25;

            slidesWrapper.style.transform = `translateX(${newTransform}%)`;
        });

        sliderContainer.addEventListener('touchend', (e) => {
            slidesWrapper.style.transition = 'transform 0.8s cubic-bezier(0.23, 1, 0.320, 1)';
            endX = e.changedTouches[0].clientX;
            handleSwipe();
        });

        function handleSwipe() {
            const swipeThreshold = 50;
            const diff = startX - endX;

            if (Math.abs(diff) > swipeThreshold) {
                stopAutoSlide();
                if (diff > 0) {
                    nextSlide();
                } else {
                    prevSlide();
                }
                startAutoSlide();
            } else {
                showSlide(currentSlide);
            }
        }

        sliderContainer.style.cursor = 'grab';
    }

    startAutoSlide();

    const sliderSection = document.querySelector('.services-slider-container');
    if (sliderSection) {
        sliderSection.addEventListener('mouseenter', stopAutoSlide);
        sliderSection.addEventListener('mouseleave', startAutoSlide);
    }
}

// Enhanced Counter animation
function animateCounter(element, target, duration = 2000) {
    const start = 0;
    const startTime = performance.now();
    const isDecimal = target % 1 !== 0;

    function updateCounter(currentTime) {
        const elapsed = currentTime - startTime;
        const progress = Math.min(elapsed / duration, 1);
        const easeOutQuart = 1 - Math.pow(1 - progress, 4);
        const current = start + (target - start) * easeOutQuart;

        if (isDecimal) {
            element.textContent = current.toFixed(1);
        } else {
            element.textContent = Math.floor(current).toLocaleString();
        }

        if (progress < 1) {
            requestAnimationFrame(updateCounter);
        } else {
            if (isDecimal) {
                element.textContent = target.toFixed(1);
            } else {
                element.textContent = target.toLocaleString();
            }
        }
    }

    requestAnimationFrame(updateCounter);
}

// Enhanced Intersection Observer for animations
const observerOptions = {
    threshold: 0.1,
    rootMargin: '0px 0px -50px 0px'
};

const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
        if (entry.isIntersecting) {
            entry.target.classList.add('fade-in');

            // Trigger counter animation for stats
            if (entry.target.classList.contains('stats')) {
                const statNumbers = entry.target.querySelectorAll('.stat-number');
                statNumbers.forEach((stat, index) => {
                    const dataCount = stat.getAttribute('data-count');
                    if (dataCount) {
                        const target = parseFloat(dataCount);
                        setTimeout(() => {
                            animateCounter(stat, target);
                        }, index * 200);
                    }
                });
            }

            // Animate story cards
            if (entry.target.classList.contains('story-card')) {
                entry.target.style.transform = 'translateY(0)';
                entry.target.style.opacity = '1';
            }
        }
    });
}, observerOptions);

// Enhanced Notification system
function showNotification(message, type = 'success') {
    const existingNotification = document.querySelector('.notification');
    if (existingNotification) {
        existingNotification.remove();
    }

    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;

    document.body.appendChild(notification);

    setTimeout(() => {
        notification.classList.add('show');
    }, 100);

    setTimeout(() => {
        notification.classList.remove('show');
        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 300);
    }, 3000);
}

// Impact Dashboard Functions
function initializeImpactDashboard() {
    // 가라데이터로 임팩트 숫자 업데이트
    updateImpactNumbers();

    const impactNumbers = document.querySelectorAll('.impact-number');

    const impactObserver = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                const target = parseFloat(entry.target.getAttribute('data-count'));
                animateCounter(entry.target, target, 2000);
                impactObserver.unobserve(entry.target);
            }
        });
    }, { threshold: 0.5 });

    impactNumbers.forEach(number => {
        impactObserver.observe(number);
    });

    setTimeout(() => {
        animateProgressBars();
    }, 1000);

    setTimeout(() => {
        animateChartBars();
    }, 1500);
}

function updateImpactNumbers() {
    // 임팩트 데이터 업데이트
    const impactNumbers = document.querySelectorAll('.impact-number');
    const impactData = [
        DEMO_DATA.impact.carbonSaved,
        DEMO_DATA.impact.wasteRecycled,
        DEMO_DATA.impact.sharingCompleted,
        DEMO_DATA.impact.ecoProducts
    ];

    impactNumbers.forEach((number, index) => {
        if (impactData[index] !== undefined) {
            number.setAttribute('data-count', impactData[index]);
        }
    });
}

function animateProgressBars() {
    const progressBars = document.querySelectorAll('.progress-fill');
    progressBars.forEach((bar, index) => {
        setTimeout(() => {
            const width = bar.style.width;
            bar.style.width = '0%';
            setTimeout(() => {
                bar.style.width = width;
            }, 100);
        }, index * 200);
    });
}

function animateChartBars() {
    const chartBars = document.querySelectorAll('.chart-bar');
    const heights = ['65%', '78%', '92%', '100%']; // 4개 값 모두 있음

    console.log('📊 차트 바 개수:', chartBars.length); // 디버깅용

    chartBars.forEach((bar, index) => {
        bar.style.height = '0%';
        bar.style.transition = 'height 0.8s ease-in-out';

        setTimeout(() => {
            const targetHeight = heights[index]; // undefined 체크 제거
            bar.style.height = targetHeight;
            console.log(`차트 바 ${index + 1}: ${targetHeight}`); // 디버깅용
        }, 200 + (index * 300)); // 시작 지연 추가
    });

    console.log('📊 차트 애니메이션 실행됨');
}

// Activity Feed Functions
function initializeActivityFeed() {
    updateDailyStats();
    startActivityFeed();
}

function updateDailyStats() {
    // 오늘의 활동 현황 업데이트
    const statMinis = document.querySelectorAll('.stat-mini .stat-number');
    const dailyData = [
        DEMO_DATA.dailyActivity.disposal,
        DEMO_DATA.dailyActivity.sharing,
        DEMO_DATA.dailyActivity.ecoShopping,
        DEMO_DATA.dailyActivity.community
    ];

    statMinis.forEach((stat, index) => {
        if (dailyData[index] !== undefined) {
            stat.textContent = dailyData[index].toLocaleString();
        }
    });
}

function startActivityFeed() {
    activityInterval = setInterval(() => {
        addNewActivity();
    }, 8000); // 8초마다 새로운 활동 추가
}

function stopActivityFeed() {
    if (activityInterval) {
        clearInterval(activityInterval);
    }
}

function addNewActivity() {
    const activities = [
        {
            icon: 'waste',
            text: '<strong>김○○님</strong>이 캔을 정확히 분리배출했습니다',
            reward: '+15P 적립',
            type: 'reward'
        },
        {
            icon: 'sharing',
            text: '<strong>부산 해운대구</strong>에서 아이 장난감 무료나눔이 시작되었습니다',
            location: '📍 좌동',
            type: 'location'
        },
        {
            icon: 'market',
            text: '<strong>정○○님</strong>이 대나무 칫솔을 구매하여 <strong>2kg CO₂</strong>를 절약했습니다',
            impact: '🌱 환경 기여도 +20P',
            type: 'impact'
        },
        {
            icon: 'community',
            text: '<strong>환경톡톡</strong>에 "일회용품 줄이기 실천법" 새 글이 등록되었습니다',
            engagement: '👍 8개 좋아요',
            type: 'engagement'
        },
        {
            icon: 'achievement',
            text: '<strong>한○○님</strong>이 "분리배출 마스터" 배지를 획득했습니다',
            badge: '🎖️ 100회 연속 정확 분류',
            type: 'badge'
        }
    ];

    const activityList = document.getElementById('activityList');
    if (!activityList) return;

    const randomActivity = activities[Math.floor(Math.random() * activities.length)];

    const activityItem = document.createElement('div');
    activityItem.className = 'activity-item';
    activityItem.style.opacity = '0';

    let extraContent = '';
    if (randomActivity.reward) extraContent = `<span class="activity-reward">${randomActivity.reward}</span>`;
    if (randomActivity.location) extraContent = `<span class="activity-location">${randomActivity.location}</span>`;
    if (randomActivity.impact) extraContent = `<span class="activity-impact">${randomActivity.impact}</span>`;
    if (randomActivity.engagement) extraContent = `<span class="activity-engagement">${randomActivity.engagement}</span>`;
    if (randomActivity.badge) extraContent = `<span class="activity-badge">${randomActivity.badge}</span>`;

    activityItem.innerHTML = `
        <div class="activity-time">방금 전</div>
        <div class="activity-content">
            <div class="activity-icon ${randomActivity.icon}">
                ${randomActivity.icon === 'waste' ? '⚡' :
        randomActivity.icon === 'sharing' ? '🤝' :
            randomActivity.icon === 'market' ? '🛒' :
                randomActivity.icon === 'community' ? '💬' : '🏆'}
            </div>
            <div class="activity-text">
                ${randomActivity.text}
                ${extraContent}
            </div>
        </div>
    `;

    activityList.insertBefore(activityItem, activityList.firstChild);

    setTimeout(() => {
        activityItem.style.opacity = '1';
    }, 100);

    // 기존 시간 업데이트
    const timeElements = activityList.querySelectorAll('.activity-time');
    timeElements.forEach((element, index) => {
        if (index === 0) return;

        const currentText = element.textContent;
        if (currentText === '방금 전') {
            element.textContent = '2분 전';
        } else if (currentText.includes('분 전')) {
            const minutes = parseInt(currentText) + 2;
            element.textContent = `${minutes}분 전`;
        }
    });

    // 최대 6개까지만 유지
    while (activityList.children.length > 6) {
        activityList.removeChild(activityList.lastChild);
    }

    updateMiniStats();
}

function updateMiniStats() {
    const statNumbers = document.querySelectorAll('.stat-mini .stat-number');
    statNumbers.forEach(stat => {
        const current = parseInt(stat.textContent.replace(',', ''));
        const increment = Math.floor(Math.random() * 3) + 1;
        const newValue = current + increment;
        stat.textContent = newValue.toLocaleString();
    });
}

// Service Previews Functions
function initializeServicePreviews() {
    // 큰 데모 버튼 상호작용
    const largeDemoBtns = document.querySelectorAll('.demo-btn-large');
    largeDemoBtns.forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.preventDefault();
            const originalText = btn.textContent;
            btn.textContent = '분석 중...';
            btn.disabled = true;
            btn.style.background = 'linear-gradient(135deg, #6c757d, #495057)';

            btn.innerHTML = '분석 중... <span class="loading-dots">●●●</span>';

            const loadingDots = btn.querySelector('.loading-dots');
            if (loadingDots) {
                let dotCount = 0;
                const loadingInterval = setInterval(() => {
                    dotCount = (dotCount + 1) % 4;
                    loadingDots.textContent = '●'.repeat(dotCount);
                }, 300);

                setTimeout(() => {
                    clearInterval(loadingInterval);
                    showNotification('AI 분석이 완료되었습니다! 🤖', 'success');
                    btn.textContent = '다시 체험하기';
                    btn.disabled = false;
                    btn.style.background = 'linear-gradient(135deg, var(--primary-green), var(--accent-green))';

                    const resultSection = document.querySelector('.demo-result-large');
                    if (resultSection) {
                        resultSection.style.transform = 'scale(0.95)';
                        resultSection.style.opacity = '0.7';
                        setTimeout(() => {
                            resultSection.style.transform = 'scale(1)';
                            resultSection.style.opacity = '1';
                            resultSection.style.transition = 'all 0.5s ease';
                        }, 100);
                    }
                }, 2500);
            }
        });
    });

    // 일반 데모 버튼
    const demoBtns = document.querySelectorAll('.demo-btn');
    demoBtns.forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.preventDefault();
            btn.textContent = '분석 중...';
            btn.disabled = true;

            setTimeout(() => {
                showNotification('AI 분석이 완료되었습니다! 🤖', 'success');
                btn.textContent = '다시 체험하기';
                btn.disabled = false;
            }, 2000);
        });
    });

    // 미리보기 더보기 버튼
    const previewBtns = document.querySelectorAll('.preview-more');
    previewBtns.forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.preventDefault();
            const text = btn.textContent;
            showNotification(`${text} 페이지로 이동합니다!`, 'info');

            btn.style.transform = 'scale(0.95)';
            setTimeout(() => {
                btn.style.transform = 'scale(1)';
            }, 150);
        });
    });

    // 나눔 아이템 상호작용
    const sharingItems = document.querySelectorAll('.sharing-item');
    sharingItems.forEach(item => {
        item.addEventListener('click', () => {
            const title = item.querySelector('h4').textContent;
            showNotification(`"${title}" 상세 정보를 확인합니다.`, 'info');

            item.style.background = 'rgba(45, 90, 61, 0.1)';
            item.style.borderRadius = '8px';
            setTimeout(() => {
                item.style.background = '';
                item.style.borderRadius = '';
            }, 200);
        });

        item.addEventListener('mouseenter', () => {
            item.style.transform = 'translateX(5px)';
            item.style.transition = 'transform 0.2s ease';
        });

        item.addEventListener('mouseleave', () => {
            item.style.transform = 'translateX(0)';
        });
    });

    // 제품 아이템 상호작용
    const productItems = document.querySelectorAll('.product-item');
    productItems.forEach(item => {
        item.addEventListener('click', () => {
            const title = item.querySelector('h4').textContent;
            showNotification(`"${title}" 장바구니에 추가되었습니다! 🛒`, 'success');

            item.style.transform = 'scale(0.95)';
            item.style.boxShadow = '0 0 20px rgba(45, 90, 61, 0.3)';
            setTimeout(() => {
                item.style.transform = 'scale(1)';
                item.style.boxShadow = '';
            }, 200);
        });

        item.addEventListener('mouseenter', () => {
            item.style.transform = 'translateY(-3px)';
            item.style.transition = 'transform 0.2s ease';
            item.style.boxShadow = '0 4px 15px rgba(45, 90, 61, 0.2)';
        });

        item.addEventListener('mouseleave', () => {
            item.style.transform = 'translateY(0)';
            item.style.boxShadow = '';
        });
    });

    // 게시글 아이템 상호작용
    const postItems = document.querySelectorAll('.post-item');
    postItems.forEach(item => {
        item.addEventListener('click', () => {
            const title = item.querySelector('h4').textContent;
            showNotification(`"${title}" 게시글을 읽습니다.`, 'info');

            item.style.opacity = '0.7';
            setTimeout(() => {
                item.style.opacity = '1';
            }, 150);
        });

        item.addEventListener('mouseenter', () => {
            if (!item.classList.contains('hot')) {
                item.style.background = 'rgba(45, 90, 61, 0.05)';
                item.style.borderRadius = '8px';
                item.style.transform = 'translateX(3px)';
                item.style.transition = 'all 0.2s ease';
            }
        });

        item.addEventListener('mouseleave', () => {
            if (!item.classList.contains('hot')) {
                item.style.background = '';
                item.style.borderRadius = '';
                item.style.transform = 'translateX(0)';
            }
        });
    });

    // 드래그 앤 드롭 기능
    const uploadZones = document.querySelectorAll('.upload-zone-large, .upload-zone');
    uploadZones.forEach(zone => {
        ['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
            zone.addEventListener(eventName, preventDefaults, false);
            document.body.addEventListener(eventName, preventDefaults, false);
        });

        ['dragenter', 'dragover'].forEach(eventName => {
            zone.addEventListener(eventName, highlight, false);
        });

        ['dragleave', 'drop'].forEach(eventName => {
            zone.addEventListener(eventName, unhighlight, false);
        });

        zone.addEventListener('drop', handleDrop, false);

        function preventDefaults(e) {
            e.preventDefault();
            e.stopPropagation();
        }

        function highlight(e) {
            zone.style.borderColor = 'var(--primary-green)';
            zone.style.background = 'rgba(45, 90, 61, 0.1)';
        }

        function unhighlight(e) {
            zone.style.borderColor = 'var(--accent-green)';
            zone.style.background = 'rgba(111, 167, 118, 0.05)';
        }

        function handleDrop(e) {
            const dt = e.dataTransfer;
            const files = dt.files;

            if (files.length > 0) {
                const file = files[0];
                if (file.type.startsWith('image/')) {
                    showNotification(`이미지 "${file.name}"이 업로드되었습니다! 분석을 시작합니다...`, 'info');

                    setTimeout(() => {
                        showNotification('AI 분석이 완료되었습니다! 🤖', 'success');
                    }, 2000);
                } else {
                    showNotification('이미지 파일만 업로드 가능합니다.', 'error');
                }
            }
        }
    });

    // 애니메이션을 위한 Intersection Observer
    const serviceObserver = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '1';
                entry.target.style.transform = 'translateY(0)';
            }
        });
    }, {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    });

    document.querySelectorAll('.ai-demo-large, .other-services-row .preview-card').forEach(card => {
        card.style.opacity = '0';
        card.style.transform = 'translateY(30px)';
        card.style.transition = 'all 0.6s ease';
        serviceObserver.observe(card);
    });
}

// 뷰포트 및 레이아웃 관리
function handleViewportChange() {
    const vw = Math.max(document.documentElement.clientWidth || 0, window.innerWidth || 0);
    const vh = Math.max(document.documentElement.clientHeight || 0, window.innerHeight || 0);

    document.documentElement.style.setProperty('--vw', `${vw}px`);
    document.documentElement.style.setProperty('--vh', `${vh}px`);

    const screenSize = getScreenSize();
    document.body.className = document.body.className.replace(/screen-\w+/g, '');
    document.body.classList.add(`screen-${screenSize}`);

    if (vw >= 1600) {
        document.documentElement.style.setProperty('--container-max-width', '1600px');
        document.documentElement.style.setProperty('--container-padding', '40px');
    } else if (vw >= 1200) {
        document.documentElement.style.setProperty('--container-max-width', '1400px');
        document.documentElement.style.setProperty('--container-padding', '30px');
    } else {
        document.documentElement.style.setProperty('--container-max-width', '1200px');
        document.documentElement.style.setProperty('--container-padding', '20px');
    }
}

// 윈도우 리사이즈 핸들러
window.addEventListener('resize', () => {
    clearTimeout(resizeTimeout);
    resizeTimeout = setTimeout(() => {
        handleViewportChange();
        updateSliderForScreenSize();
        adjustGridLayouts();

        if (window.innerWidth >= 1200) {
            triggerLargeScreenAnimations();
        }
    }, 250);
});

// 그리드 레이아웃 동적 조정
function adjustGridLayouts() {
    const screenSize = getScreenSize();

    const impactGrid = document.querySelector('.impact-grid');
    if (impactGrid) {
        switch (screenSize) {
            case 'ultra-wide':
            case 'extra-large':
                impactGrid.style.gridTemplateColumns = 'repeat(4, 1fr)';
                impactGrid.style.gap = '50px';
                break;
            case 'large':
                impactGrid.style.gridTemplateColumns = 'repeat(4, 1fr)';
                impactGrid.style.gap = '40px';
                break;
            default:
                impactGrid.style.gridTemplateColumns = '';
                impactGrid.style.gap = '';
        }
    }
}

// 큰 화면용 애니메이션
function triggerLargeScreenAnimations() {
    const impactCards = document.querySelectorAll('.impact-card');
    impactCards.forEach((card, index) => {
        setTimeout(() => {
            card.style.transform = 'translateY(0) scale(1)';
            card.style.opacity = '1';
        }, index * 100);
    });

    const chartBars = document.querySelectorAll('.chart-bar');
    chartBars.forEach((bar, index) => {
        const height = bar.style.height;
        bar.style.height = '0%';
        setTimeout(() => {
            bar.style.height = height;
        }, 500 + (index * 200));
    });
}

// 스크롤 투 탑 버튼 생성
function createScrollToTopButton() {
    const scrollButton = document.createElement('button');
    scrollButton.innerHTML = '↑';
    scrollButton.className = 'scroll-to-top';
    scrollButton.style.cssText = `
        position: fixed;
        bottom: 30px;
        right: 30px;
        width: 50px;
        height: 50px;
        border-radius: 50%;
        background: var(--primary-green);
        color: white;
        border: none;
        cursor: pointer;
        font-size: 20px;
        font-weight: bold;
        opacity: 0;
        visibility: hidden;
        transition: all 0.3s ease;
        z-index: 1000;
        box-shadow: 0 4px 20px rgba(45, 90, 61, 0.3);
    `;

    scrollButton.addEventListener('click', () => {
        window.scrollTo({
            top: 0,
            behavior: 'smooth'
        });
    });

    document.body.appendChild(scrollButton);

    window.addEventListener('scroll', () => {
        if (window.scrollY > 500) {
            scrollButton.style.opacity = '1';
            scrollButton.style.visibility = 'visible';
        } else {
            scrollButton.style.opacity = '0';
            scrollButton.style.visibility = 'hidden';
        }
    });
}

// 호버 효과 추가
function addHoverEffects() {
    const impactCards = document.querySelectorAll('.impact-card');
    impactCards.forEach(card => {
        card.addEventListener('mouseenter', () => {
            if (window.innerWidth >= 1200) {
                card.style.transform = 'translateY(-10px) scale(1.02)';
            }
        });

        card.addEventListener('mouseleave', () => {
            card.style.transform = 'translateY(0) scale(1)';
        });
    });

    const activityItems = document.querySelectorAll('.activity-item');
    activityItems.forEach(item => {
        item.addEventListener('mouseenter', () => {
            if (window.innerWidth >= 1200) {
                item.style.backgroundColor = 'rgba(45, 90, 61, 0.05)';
                item.style.borderRadius = '8px';
                item.style.transform = 'translateX(5px)';
            }
        });

        item.addEventListener('mouseleave', () => {
            item.style.backgroundColor = 'transparent';
            item.style.transform = 'translateX(0)';
        });
    });
}

// 가라데이터 초기화
function initializeDemoData() {
    // 메인 통계 숫자 업데이트
    const statNumbers = document.querySelectorAll('.stat-number');
    const statsData = [
        DEMO_DATA.stats.monthlyWaste,
        DEMO_DATA.stats.activeUsers,
        DEMO_DATA.stats.accuracy,
        DEMO_DATA.stats.uptime
    ];

    statNumbers.forEach((stat, index) => {
        if (statsData[index] !== undefined) {
            stat.setAttribute('data-count', statsData[index]);
        }
    });
}

// 히어로 슬라이더 클래스
class HeroSlider {
    constructor() {
        this.slides = document.querySelectorAll('.hero-slide');
        this.currentSlide = 0;
        this.slideInterval = null;
        this.isPlaying = true;

        this.init();
    }

    init() {
        if (this.slides.length === 0) return;

        this.showSlide(0);
        this.startAutoPlay();

        document.addEventListener('visibilitychange', () => {
            if (document.hidden) {
                this.pauseAutoPlay();
            } else {
                this.startAutoPlay();
            }
        });

        window.addEventListener('focus', () => {
            if (this.isPlaying) {
                this.startAutoPlay();
            }
        });

        window.addEventListener('blur', () => {
            this.pauseAutoPlay();
        });

        const heroSection = document.querySelector('.hero');
        if (heroSection) {
            heroSection.addEventListener('mouseenter', () => {
                this.pauseAutoPlay();
            });

            heroSection.addEventListener('mouseleave', () => {
                if (this.isPlaying) {
                    this.startAutoPlay();
                }
            });
        }
    }

    showSlide(index) {
        this.slides.forEach(slide => {
            slide.classList.remove('active');
        });

        if (this.slides[index]) {
            this.slides[index].classList.add('active');
        }

        this.currentSlide = index;
    }

    nextSlide() {
        const nextIndex = (this.currentSlide + 1) % this.slides.length;
        this.showSlide(nextIndex);
    }

    startAutoPlay() {
        this.pauseAutoPlay();

        if (this.slides.length > 1) {
            this.slideInterval = setInterval(() => {
                this.nextSlide();
            }, 5000);
        }
    }

    pauseAutoPlay() {
        if (this.slideInterval) {
            clearInterval(this.slideInterval);
            this.slideInterval = null;
        }
    }

    play() {
        this.isPlaying = true;
        this.startAutoPlay();
    }

    pause() {
        this.isPlaying = false;
        this.pauseAutoPlay();
    }
}

// 페이지 기능 초기화
function initializePage() {
    // 푸터 로드
    loadFooter();

    // 가라데이터 초기화
    initializeDemoData();

    handleViewportChange();
    updateSliderForScreenSize();
    adjustGridLayouts();

    initializeSlider();
    initializeImpactDashboard();
    initializeActivityFeed();
    initializeServicePreviews();

    createScrollToTopButton();
    addHoverEffects();

    // 관찰자 설정
    document.querySelectorAll('.stats, .hero-content, .impact-card, .story-card').forEach(el => {
        observer.observe(el);
    });

    // 키보드 네비게이션 지원
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            if (navMenu && navMenu.classList.contains('active')) {
                closeMobileMenu();
            }
        }

        if (e.key === 'ArrowLeft') {
            stopAutoSlide();
            prevSlide();
            startAutoSlide();
        } else if (e.key === 'ArrowRight') {
            stopAutoSlide();
            nextSlide();
            startAutoSlide();
        }
    });

    // 페이지 가시성 변경 처리
    document.addEventListener('visibilitychange', () => {
        if (document.hidden) {
            stopAutoSlide();
            stopActivityFeed();
        } else {
            startAutoSlide();
            startActivityFeed();
        }
    });

    window.addEventListener('resize', handleViewportChange);

    console.log('🌱 ECOVERY 스마트 환경 플랫폼이 초기화되었습니다.');
    console.log(`📱 현재 화면 크기: ${getScreenSize()}`);
}

// DOM 로드 완료 시 초기화
document.addEventListener('DOMContentLoaded', () => {
    initializePage();

    setTimeout(() => {
        document.body.classList.add('loaded');
    }, 500);

    // 스토리 카드 초기 설정
    const storyCards = document.querySelectorAll('.story-card');
    storyCards.forEach(card => {
        card.style.transform = 'translateY(30px)';
        card.style.opacity = '0';
        card.style.transition = 'all 0.３s ease';
    });

    // 히어로 슬라이더 초기화
    setTimeout(() => {
        window.heroSlider = new HeroSlider();
    }, 100);
});

// 페이지 로드 완료 시
window.addEventListener('load', () => {
    setTimeout(() => {
        if (window.innerWidth >= 1200) {
            triggerLargeScreenAnimations();
        }

        if ('requestIdleCallback' in window) {
            requestIdleCallback(() => {
                console.log('💚 ECOVERY 대형 화면 최적화 완료');
            });
        }
    }, 1000);
});

// 에러 처리
window.addEventListener('error', (e) => {
    console.warn('페이지 오류:', e.error);
});

// 전역 함수 노출
window.scrollToSection = scrollToSection;
window.showNotification = showNotification;

// 개발자 도구용 제어 함수
window.egoveryControls = {
    heroSlider: {
        next: () => window.heroSlider?.nextSlide(),
        play: () => window.heroSlider?.play(),
        pause: () => window.heroSlider?.pause()
    },
    servicesSlider: {
        next: () => nextSlide(),
        prev: () => prevSlide(),
        goto: (index) => showSlide(index)
    },
    notifications: {
        success: (msg) => showNotification(msg, 'success'),
        error: (msg) => showNotification(msg, 'error'),
        info: (msg) => showNotification(msg, 'info')
    },
    data: DEMO_DATA
};