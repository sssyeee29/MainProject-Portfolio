let cartItems = [];
let cartTotal = 0;

document.addEventListener('DOMContentLoaded', function () {
    initializeCartItemsFromDOM();
    initializeEventListeners();
    bindDynamicEventListeners();
});

function initializeCartItemsFromDOM() {
    cartItems = [];
    document.querySelectorAll('.cart-item').forEach(itemEl => {
        const cartItemId = parseInt(itemEl.dataset.itemId);
        const realItemId = parseInt(itemEl.dataset.realItemId);
        const name = itemEl.querySelector('.item-name').textContent.trim();
        const unitPrice = parseInt(itemEl.dataset.unitPrice);
        const qtyInput = itemEl.querySelector('.qty-input');
        const count = parseInt(qtyInput.value);
        const stock = parseInt(qtyInput.getAttribute('max'));
        const selected = itemEl.querySelector('.item-checkbox').checked;

        cartItems.push({
            cartItemId,
            realItemId,
            itemNm: name,
            price: unitPrice,
            quantity: count,
            stockNumber: stock,
            selected
        });
    });
    updateSelectedCount();
    updateCartSummary();
}

function initializeEventListeners() {
    document.getElementById('selectAll')?.addEventListener('change', toggleSelectAll);
}

function bindDynamicEventListeners() {
    document.querySelectorAll('.item-checkbox').forEach((cb, index) => {
        cb.addEventListener('change', () => toggleItemSelection(index));
    });

    document.querySelectorAll('.qty-btn.plus').forEach(btn => {
        btn.addEventListener('click', () => {
            const itemEl = btn.closest('.cart-item');
            updateQuantity(parseInt(itemEl.dataset.itemId), 1, parseInt(itemEl.dataset.realItemId));
        });
    });

    document.querySelectorAll('.qty-btn.minus').forEach(btn => {
        btn.addEventListener('click', () => {
            const itemEl = btn.closest('.cart-item');
            updateQuantity(parseInt(itemEl.dataset.itemId), -1, parseInt(itemEl.dataset.realItemId));
        });
    });
}

function updateQuantity(cartItemIdToUpdate, change, realItemId) {
    const item = cartItems.find(item => item.cartItemId === cartItemIdToUpdate);
    if (item) {
        const newQuantity = item.quantity + change;
        if (newQuantity > 0 && newQuantity <= item.stockNumber) {
            item.quantity = newQuantity;
            document.getElementById(`qty-${cartItemIdToUpdate}`).value = newQuantity;
            const itemElement = document.querySelector(`[data-item-id="${cartItemIdToUpdate}"]`);
            itemElement.querySelector('.sale-price').textContent = formatPrice(item.price * newQuantity);
            updateCartSummary();

            fetch(`/cart/update?cartItemId=${cartItemIdToUpdate}&count=${newQuantity}&itemId=${realItemId}`, {
                method: 'PUT'
            }).then(response => {
                if (response.ok) {
                    showNotification(`수량이 ${newQuantity}개로 변경되었습니다.`, 'success');
                } else {
                    showNotification('수량 변경 실패! 서버 오류.', 'error');
                }
            }).catch(() => {
                showNotification('수량 변경 중 문제가 발생했습니다.', 'error');
            });
        } else {
            showNotification("더 이상 수량을 변경할 수 없습니다.", 'warning');
        }
    } else {
        showNotification("장바구니 아이템 정보를 찾을 수 없습니다.", 'error');
    }
}

function deleteSelected() {
    const selectedCartItemIds = cartItems.filter(item => item.selected).map(item => item.cartItemId);

    if (selectedCartItemIds.length === 0) {
        showNotification("삭제할 상품을 선택해주세요.", "warning");
        return;
    }

    // 모든 삭제 요청을 Promise 배열로 만들어 실행
    const deletePromises = selectedCartItemIds.map(id => {
        return fetch(`/cart/delete/${id}`, { method: 'DELETE' });
    });

    Promise.all(deletePromises)
        .then(responses => {
            // 응답 중 실패가 있는지 체크
            if (responses.every(response => response.ok)) {
                // 모두 성공 시 클라이언트 상태 및 DOM 업데이트
                cartItems = cartItems.filter(item => !selectedCartItemIds.includes(item.cartItemId));
                selectedCartItemIds.forEach(id => {
                    const el = document.querySelector(`.cart-item[data-item-id="${id}"]`);
                    if (el) el.remove();
                });
                updateSelectedCount();
                updateCartSummary();
                showNotification(`${selectedCartItemIds.length}개의 상품이 삭제되었습니다.`, 'success');
            } else {
                showNotification('일부 상품 삭제에 실패했습니다.', 'error');
            }
        })
        .catch(() => {
            showNotification('상품 삭제 중 오류가 발생했습니다.', 'error');
        });
}



function toggleSelectAll() {
    const isChecked = document.getElementById('selectAll').checked;
    cartItems.forEach(item => item.selected = isChecked);
    document.querySelectorAll('.item-checkbox').forEach(cb => cb.checked = isChecked);
    updateSelectedCount();
    updateCartSummary();
}

function toggleItemSelection(index) {
    cartItems[index].selected = !cartItems[index].selected;
    updateSelectedCount();
    updateCartSummary();
    document.getElementById('selectAll').checked = cartItems.every(i => i.selected);
}

function updateCartSummary() {
    const selectedItems = cartItems.filter(item => item.selected);
    const subtotal = selectedItems.reduce((sum, item) => sum + item.price * item.quantity, 0);
    document.getElementById('subtotal').textContent = formatPrice(subtotal);
    document.getElementById('discount').textContent = formatPrice(0); // 할인 없음
    document.getElementById('total').textContent = formatPrice(subtotal);
    document.getElementById('pricetotal').textContent = formatPrice(subtotal);
    cartTotal = subtotal;
}

function updateSelectedCount() {
    const selectedCount = cartItems.filter(item => item.selected).length;
    document.getElementById('selectedCount').textContent = selectedCount;
}

function formatPrice(price) {
    return new Intl.NumberFormat('ko-KR').format(price) + '원';
}

function showNotification(message, type = 'success') {
    const existing = document.querySelector('.notification');
    if (existing) existing.remove();

    const n = document.createElement('div');
    n.className = `notification ${type}`;
    n.textContent = message;
    n.style.cssText = `
        position: fixed;
        top: 100px;
        right: 20px;
        background: ${getNotificationColor(type)};
        color: white;
        padding: 12px 20px;
        border-radius: 8px;
        box-shadow: 0 4px 20px rgba(0,0,0,0.1);
        z-index: 10000;
        max-width: 300px;
    `;
    document.body.appendChild(n);
    setTimeout(() => n.remove(), 3000);
}

function getNotificationColor(type) {
    switch (type) {
        case 'success': return '#2d5a3d';
        case 'error': return '#dc3545';
        case 'warning': return '#ffc107';
        case 'info': return '#6fa776';
        default: return '#2d5a3d';
    }
}

function goToMarket() {
    window.location.href = "/eco/list";
}

// ✅ 주문 form 동적 구성 및 제출 함수 (최종 수정)
function submitOrderForm() {
    const selectedItems = cartItems.filter(item => item.selected);
    if (selectedItems.length === 0) {
        showNotification("주문할 상품을 선택해주세요.", "warning");
        return;
    }

    // `OrderViewController.preparePage`가 기대하는 `OrderItemRequestDto` 형식에 맞게
    // 첫 번째 아이템의 정보만 추출하여 폼을 동적으로 생성
    const firstItem = selectedItems[0];

    // 기존의 form이 있다면 제거하고 새로 생성
    let form = document.getElementById('tempOrderForm');
    if (form) {
        form.remove();
    }

    form = document.createElement('form');
    form.method = 'POST';
    form.action = '/order/prepare';
    form.id = 'tempOrderForm';
    form.style.display = 'none';

    // hidden input 필드 추가
    const itemIdInput = document.createElement('input');
    itemIdInput.type = 'hidden';
    itemIdInput.name = 'itemId';
    itemIdInput.value = firstItem.realItemId;
    form.appendChild(itemIdInput);

    const countInput = document.createElement('input');
    countInput.type = 'hidden';
    countInput.name = 'count';
    countInput.value = firstItem.quantity;
    form.appendChild(countInput);

    document.body.appendChild(form);

    // 주문 페이지로 이동하기 전, 모든 상품 정보를 sessionStorage에 저장
    const orderItemRequests = selectedItems.map(item => ({
        itemId: item.realItemId,
        count: item.quantity
    }));
    sessionStorage.setItem("allSelectedItems", JSON.stringify(orderItemRequests));

    // 폼 제출 (POST 요청)
    form.submit();
}