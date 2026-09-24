/**
 * DHARWINMART - Client Side JavaScript
 * Quantity controls, delete confirmations, and UI helpers
 */

document.addEventListener('DOMContentLoaded', () => {
    // Quantity Selector Controls
    document.querySelectorAll('.qty-decrement').forEach(button => {
        button.addEventListener('click', (e) => {
            const input = button.parentElement.querySelector('.qty-input');
            if (input) {
                let currentVal = parseInt(input.value, 10) || 1;
                const min = parseInt(input.getAttribute('min'), 10) || 1;
                if (currentVal > min) {
                    input.value = currentVal - 1;
                    triggerChange(input);
                }
            }
        });
    });

    document.querySelectorAll('.qty-increment').forEach(button => {
        button.addEventListener('click', (e) => {
            const input = button.parentElement.querySelector('.qty-input');
            if (input) {
                let currentVal = parseInt(input.value, 10) || 1;
                const max = parseInt(input.getAttribute('max'), 10) || 999;
                if (currentVal < max) {
                    input.value = currentVal + 1;
                    triggerChange(input);
                }
            }
        });
    });

    function triggerChange(element) {
        element.dispatchEvent(new Event('change', { bubbles: true }));
    }

    // Auto-dismiss alert messages after 5 seconds
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.5s ease';
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 500);
        }, 5000);
    });
});

/**
 * Prompt user confirmation before executing deletion
 */
function confirmAction(event, message) {
    if (!confirm(message || 'Are you sure you want to perform this action?')) {
        event.preventDefault();
        return false;
    }
    return true;
}

/**
 * Handle AJAX Wishlist Toggle with instant optimistic UI update
 */
async function handleWishlistToggle(button) {
    const productId = button.getAttribute('data-product-id');
    if (!productId) return;

    const heartIcon = button.querySelector('.heart-icon');
    const wishlistText = button.querySelector('.wishlist-text');

    const formData = new URLSearchParams();
    formData.append('productId', productId);

    try {
        const response = await fetch('/wishlist/toggle', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: formData.toString()
        });

        if (response.status === 401) {
            window.location.href = '/login?role=BUYER&error=auth_required';
            return;
        }

        if (response.status === 403) {
            const data = await response.json();
            alert(data.message || 'Access Denied: Only Buyers can add items to a wishlist.');
            return;
        }

        const data = await response.json();
        if (data.success) {
            if (data.wishlisted) {
                button.classList.add('active');
                if (heartIcon) heartIcon.textContent = '♥';
                if (wishlistText) wishlistText.textContent = 'Wishlisted';
            } else {
                button.classList.remove('active');
                if (heartIcon) heartIcon.textContent = '♡';
                if (wishlistText) wishlistText.textContent = 'Wishlist';
            }

            // Update navbar badge if present
            const navBadge = document.getElementById('navWishlistBadge');
            if (navBadge) {
                navBadge.textContent = data.count;
            }
        } else {
            alert(data.message || 'Error updating wishlist.');
        }
    } catch (err) {
        console.error('Wishlist error:', err);
    }
}

