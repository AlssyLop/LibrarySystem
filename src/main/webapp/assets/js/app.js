document.addEventListener('DOMContentLoaded', () => {
    // Initialize Lucide icons
    lucide.createIcons();

    // Theme Toggle Logic
    const themeToggleBtn = document.getElementById('theme-toggle');
    const themeIcon = document.getElementById('theme-icon');
    const themeText = document.getElementById('theme-text');

    if (themeToggleBtn) {
        // Initialize UI derived from the anti-flicker script in head
        const currentTheme = document.documentElement.getAttribute('data-theme');
        if (currentTheme === 'dark') {
            themeIcon.setAttribute('data-lucide', 'sun');
            themeText.textContent = 'Modo Claro';
            lucide.createIcons();
        }

        themeToggleBtn.addEventListener('click', () => {
            const isDark = document.documentElement.getAttribute('data-theme') === 'dark';
            const newTheme = isDark ? 'light' : 'dark';
            
            document.documentElement.setAttribute('data-theme', newTheme);
            localStorage.setItem('theme', newTheme);
            
            // Update UI dynamically
            if (newTheme === 'dark') {
                themeIcon.setAttribute('data-lucide', 'sun');
                themeText.textContent = 'Modo Claro';
            } else {
                themeIcon.setAttribute('data-lucide', 'moon');
                themeText.textContent = 'Modo Oscuro';
            }
            lucide.createIcons();
        });
    }

    // Modal logic
    const modalContainers = document.querySelectorAll('.modal-overlay');
    const closeBtns = document.querySelectorAll('.close-modal');

    // Close Modals
    closeBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            btn.closest('.modal-overlay').classList.remove('active');
        });
    });

    // Close when clicking outside
    modalContainers.forEach(container => {
        container.addEventListener('click', (e) => {
            if (e.target === container) {
                container.classList.remove('active');
            }
        });
    });

    // Confirmation delete alert
    const deleteBtns = document.querySelectorAll('.btn-delete');
    deleteBtns.forEach(btn => {
        btn.addEventListener('click', (e) => {
            if (!confirm('¿Estás seguro de que deseas eliminar este registro?')) {
                e.preventDefault();
            }
        });
    });

    // Tabs logic
    const tabBtns = document.querySelectorAll('.tab');
    tabBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            // Remove active from all siblings
            const parent = btn.parentElement;
            parent.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
            btn.classList.add('active');

            // Hide all tab contents
            const targetId = btn.getAttribute('data-target');
            document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
            document.getElementById(targetId).classList.add('active');
        });
    });

    // Autocomplete Logic
    const autoInputs = document.querySelectorAll('.autocomplete-input');
    autoInputs.forEach(input => {
        let timeout = null;
        const targetId = input.getAttribute('data-target');
        const endpoint = input.getAttribute('data-endpoint');
        const suggestionsBox = input.parentElement.querySelector('.autocomplete-suggestions');

        input.addEventListener('input', function () {
            clearTimeout(timeout);
            const query = this.value;

            document.getElementById(targetId).value = ""; // reset hidden on type

            if (query.trim() === '') {
                suggestionsBox.classList.remove('active');
                return;
            }

            timeout = setTimeout(() => {
                fetch(`${endpoint}&query=${encodeURIComponent(query)}`)
                    .then(r => r.json())
                    .then(data => {
                        suggestionsBox.innerHTML = '';
                        if (data.length > 0) {
                            data.forEach(item => {
                                const div = document.createElement('div');
                                div.className = 'autocomplete-suggestion-item';
                                div.textContent = item.text;
                                div.addEventListener('click', () => {
                                    input.value = item.text;
                                    document.getElementById(targetId).value = item.id;
                                    suggestionsBox.classList.remove('active');
                                });
                                suggestionsBox.appendChild(div);
                            });
                            suggestionsBox.classList.add('active');
                        } else {
                            suggestionsBox.classList.remove('active');
                        }
                    }).catch(err => {
                        console.error('Error fetching autocomplete:', err);
                    });
            }, 300);
        });

        // Hide when clicking outside and validate selection
        input.addEventListener('blur', () => {
            setTimeout(() => {
                if (document.getElementById(targetId).value === '') {
                    input.value = '';
                }
            }, 200);
        });

        document.addEventListener('click', (e) => {
            if (e.target !== input) {
                const suggestionsBox = input.parentElement.querySelector('.autocomplete-suggestions');
                if (suggestionsBox) suggestionsBox.classList.remove('active');
            }
        });
    });
});

// Function to open specific modal
function openModal(modalId) {
    document.getElementById(modalId).classList.add('active');
}

// User Modal Population
function openUserEditModal(id, name, email, phone) {
    document.getElementById('editUserId').value = id;
    document.getElementById('editUserName').value = name;
    document.getElementById('editUserEmail').value = email;
    document.getElementById('editUserPhone').value = phone;
    openModal('modalEditUser');
}

// Author Modal Population
function openAuthorEditModal(id, name, nationality) {
    document.getElementById('editAuthorId').value = id;
    document.getElementById('editAuthorName').value = name;
    document.getElementById('editAuthorNationality').value = nationality;
    openModal('modalEditAuthor');
}

// Book Modal Population
function openBookEditModal(id, title, isbn, year, authorId) {
    document.getElementById('editBookId').value = id;
    document.getElementById('editBookTitle').value = title;
    document.getElementById('editBookIsbn').value = isbn;
    document.getElementById('editBookYear').value = year;
    document.getElementById('editBookAuthorId').value = authorId;
    openModal('modalEditBook');
}

// Return Loan Population
function openReturnLoanModal(id) {
    document.getElementById('returnLoanId').value = id;
    openModal('modalReturnLoan');
}

// AJAX Form Submission Handler
function setupAjaxForm(formId, modalId) {
    const form = document.getElementById(formId);
    if (!form) return;

    form.addEventListener('submit', function (e) {
        e.preventDefault();

        const submitBtn = form.querySelector('button[type="submit"]');
        const originalText = submitBtn.textContent;
        submitBtn.disabled = true;
        submitBtn.textContent = 'Procesando...';

        const formData = new URLSearchParams(new FormData(form));
        const fetchUrl = form.getAttribute('action');

        fetch(fetchUrl, {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: formData.toString()
        })
            .then(r => r.text())
            .then(text => {
                submitBtn.disabled = false;
                submitBtn.textContent = originalText;

                let data;
                data = JSON.parse(text);

                if (data.status === 'success') {
                    document.getElementById(modalId).classList.remove('active');
                    window.location.reload();
                } else {
                    alert('Error: ' + data.message);
                }
            })
            .catch(err => {
                submitBtn.disabled = false;
                submitBtn.textContent = originalText;
                alert('Error de conexión con el servidor.');
                console.error(err);
            });
    });
}

// Initialize AJAX forms
setupAjaxForm('formCreateBook', 'modalCreateBook');
setupAjaxForm('formEditBook', 'modalEditBook');
setupAjaxForm('formCreateLoan', 'modalCreateLoan');
