function showAlert(type, title, message, duration = 2500) {
    const modal = document.getElementById('customAlert');
    const iconContainer = document.getElementById('alertIcon');
    const alertTitle = document.getElementById('alertTitle');
    const alertMessage = document.getElementById('alertMessage');

    // Limpiar icono previo
    iconContainer.innerHTML = '';

    // Seleccionar color e ícono
    let iconHTML = '';
    let colorClass = '';

    if (type === 'success') {
        iconHTML = `
        <svg xmlns="http://www.w3.org/2000/svg" class="w-12 h-12 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
        </svg>`;
        colorClass = 'text-green-600';
    } else if (type === 'error') {
        iconHTML = `
        <svg xmlns="http://www.w3.org/2000/svg" class="w-12 h-12 text-red-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
        </svg>`;
        colorClass = 'text-red-600';
    } else if (type === 'info') {
        iconHTML = `
        <svg xmlns="http://www.w3.org/2000/svg" class="w-12 h-12 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M12 20h.01" />
        </svg>`;
        colorClass = 'text-blue-600';
    } else if (type === 'warning') {
        iconHTML = `
        <svg xmlns="http://www.w3.org/2000/svg" class="w-12 h-12 text-yellow-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01M12 5l7 14H5l7-14z" />
        </svg>`;
        colorClass = 'text-yellow-600';
    }

    iconContainer.innerHTML = iconHTML;
    alertTitle.className = `text-xl font-semibold mb-2 ${colorClass}`;
    alertTitle.innerText = title;
    alertMessage.innerText = message;

    // Mostrar modal
    modal.classList.remove('hidden');

    // Ocultar automáticamente
    setTimeout(() => {
        modal.classList.add('hidden');
    }, duration);

}