// Toggle password visibility function
function togglePasswordVisibility() {
    console.log('Password toggle clicked');
    const passwordInput = document.getElementById('password-input');
    const toggleIcon = document.getElementById('toggle-icon');
    
    if (passwordInput && toggleIcon) {
        if (passwordInput.type === 'password') {
            passwordInput.type = 'text';
            toggleIcon.className = 'bi bi-eye-slash toggle-password'; 
            console.log('Password shown');
        } else {
            passwordInput.type = 'password';
            toggleIcon.className = 'bi bi-eye toggle-password'; 
            console.log('Password hidden');
        }
    } else {
        console.log('Elements not found');
    }
}

// Show login required alert and open popup
function showLoginRequired() {
    alert('You need to log in to book an appointment.');
    openLoginPopup();
    return false;
}

// Open login popup
function openLoginPopup() {
    if (!document.getElementById('login-popup')) {
        fetch('client/components/login.html')
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP ${response.status}`);
                }
                return response.text();
            })
            .then(html => {
                document.getElementById('login-popup-container').innerHTML = html;
                document.getElementById('login-popup').style.display = 'flex';
                
                // Initialize login functionality after the component is loaded
                initializeLoginEvents();
            })
            .catch(error => {
                console.error('Error loading login form:', error);
                alert('Could not load login form. Please refresh the page.');
            });
    } else {
        document.getElementById('login-popup').style.display = 'flex';
    }
}

// Close login popup
function closeLoginPopup() {
    if (document.getElementById('login-popup')) {
        document.getElementById('login-popup').style.display = 'none';
    }
}

// Initialize login events after component is loaded
function initializeLoginEvents() {
    console.log('Initializing login events');
    
    // Setup form submission
    const loginForm = document.getElementById('login-form');
    if (loginForm) {
        loginForm.removeEventListener('submit', handleFormSubmit);
        loginForm.addEventListener('submit', handleFormSubmit);
    }
}

// Handle form submission
function handleFormSubmit(e) {
    e.preventDefault();
    console.log('Form submitted');
    
    const formData = new FormData(e.target);
    
    fetch('client/utils/login.php', {
        method: 'POST',
        body: formData
    })
    .then(response => {
        if (response.redirected) {
            window.location.reload();
            return;
        }
        return response.text();
    })
    .then(text => {
        if (text && text.includes('error=')) {
            const urlParams = new URLSearchParams(text.split('?')[1] || text);
            const error = urlParams.get('error');
            if (error) {
                showError(decodeURIComponent(error.replace(/\+/g, ' ')));
            }
        }
    })
    .catch(error => {
        console.error('Login error:', error);
        showError('Login failed. Please try again.');
    });
}

// Show error message
function showError(message) {
    const errorContainer = document.getElementById('error-container');
    if (errorContainer) {
        errorContainer.innerHTML = `<div class="error">${message}</div>`;
    }
}

// Initialize all login functionality when DOM is ready
document.addEventListener('DOMContentLoaded', function() {
    console.log('Login.js loaded');
    
    // Event delegation for password toggle (works with dynamically loaded content)
    document.addEventListener('click', function(e) {
        if (e.target && e.target.id === 'toggle-icon') {
            togglePasswordVisibility();
        }
    });
    
    // Event delegation for closing popup when clicking outside
    window.addEventListener('click', function(event) {
        var popup = document.getElementById('login-popup');
        if (popup && event.target === popup) {
            popup.style.display = "none";
        }
    });
});

// Make functions globally available
window.togglePasswordVisibility = togglePasswordVisibility;
window.showLoginRequired = showLoginRequired;
window.openLoginPopup = openLoginPopup;
window.closeLoginPopup = closeLoginPopup;
window.initializeLoginEvents = initializeLoginEvents;

console.log('Login.js functions loaded globally');