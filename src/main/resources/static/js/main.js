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
