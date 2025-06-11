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