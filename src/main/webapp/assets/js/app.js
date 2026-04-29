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
    // Event Delegation for Modals and Actions
    document.body.addEventListener('click', (e) => {
        // Open Generic Modal
        const openModalBtn = e.target.closest('.js-open-modal');
        if (openModalBtn) {
            const targetId = openModalBtn.getAttribute('data-target');
            if (targetId) document.getElementById(targetId).classList.add('active');
            return;
        }

        // Edit User
        const editUserBtn = e.target.closest('.js-edit-user');
        if (editUserBtn) {
            document.getElementById('editUserId').value = editUserBtn.getAttribute('data-id');
            document.getElementById('editUserName').value = editUserBtn.getAttribute('data-name');
            document.getElementById('editUserEmail').value = editUserBtn.getAttribute('data-email');
            document.getElementById('editUserPhone').value = editUserBtn.getAttribute('data-phone');
            document.getElementById('modalEditUser').classList.add('active');
            return;
        }

        // Edit Author
        const editAuthorBtn = e.target.closest('.js-edit-author');
        if (editAuthorBtn) {
            document.getElementById('editAuthorId').value = editAuthorBtn.getAttribute('data-id');
            document.getElementById('editAuthorName').value = editAuthorBtn.getAttribute('data-name');
            document.getElementById('editAuthorNationality').value = editAuthorBtn.getAttribute('data-nationality');
            document.getElementById('modalEditAuthor').classList.add('active');
            return;
        }

        // Edit Book
        const editBookBtn = e.target.closest('.js-edit-book');
        if (editBookBtn) {
            document.getElementById('editBookId').value = editBookBtn.getAttribute('data-id');
            document.getElementById('editBookTitle').value = editBookBtn.getAttribute('data-title');
            document.getElementById('editBookIsbn').value = editBookBtn.getAttribute('data-isbn');
            document.getElementById('editBookYear').value = editBookBtn.getAttribute('data-year');
            document.getElementById('editBookAuthorId').value = editBookBtn.getAttribute('data-authorid');
            document.getElementById('modalEditBook').classList.add('active');
            return;
        }

        // Return Loan
        const returnLoanBtn = e.target.closest('.js-return-loan');
        if (returnLoanBtn) {
            document.getElementById('returnLoanId').value = returnLoanBtn.getAttribute('data-id');
            document.getElementById('modalReturnLoan').classList.add('active');
            return;
        }
    });

    // Initialize AJAX forms
    setupAjaxForm('formCreateUser', 'modalCreateUser');
    setupAjaxForm('formEditUser', 'modalEditUser');
    setupAjaxForm('formCreateAuthor', 'modalCreateAuthor');
    setupAjaxForm('formEditAuthor', 'modalEditAuthor');
    setupAjaxForm('formCreateBook', 'modalCreateBook');
    setupAjaxForm('formEditBook', 'modalEditBook');
    setupAjaxForm('formCreateLoan', 'modalCreateLoan');
    setupAjaxForm('formReturnLoan', 'modalReturnLoan');
});

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
            .then(r => r.json())
            .then(data => {
                submitBtn.disabled = false;
                submitBtn.textContent = originalText;

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



