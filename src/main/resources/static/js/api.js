const API_BASE = '/api';

function getToken() {
    return localStorage.getItem('xp_token');
}

function getUser() {
    const u = localStorage.getItem('xp_user');
    return u ? JSON.parse(u) : null;
}

async function apiFetch(path, options = {}) {
    const token = getToken();
    const res = await fetch(API_BASE + path, {
        ...options,
        headers: {
            'Content-Type': 'application/json',
            ...(token ? { 'Authorization': 'Bearer ' + token } : {}),
            ...(options.headers || {})
        }
    });

    if (res.status === 401) {
        localStorage.clear();
        window.location.href = '/login.html';
        return;
    }

    const data = res.headers.get('content-type')?.includes('application/json')
        ? await res.json()
        : null;

    if (!res.ok) {
        throw new Error(data?.error || 'Request failed');
    }

    return data;
}

// Auth
async function login(username, password) {
    const data = await apiFetch('/auth/login', {
        method: 'POST',
        body: JSON.stringify({ username, password })
    });
    localStorage.setItem('xp_token', data.token);
    localStorage.setItem('xp_user', JSON.stringify(data));
    return data;
}

function logout() {
    localStorage.clear();
    window.location.href = '/login.html';
}

// Users
async function createUser(payload) {
    return apiFetch('/users', { method: 'POST', body: JSON.stringify(payload) });
}

async function getAllUsers() {
    return apiFetch('/users');
}

async function getUsersByRole(role) {
    return apiFetch('/users/role/' + role);
}

async function deactivateUser(id) {
    return apiFetch('/users/' + id, { method: 'DELETE' });
}

// Branches
async function getBranches() {
    return apiFetch('/branches');
}

// Fuel Types
async function getFuelTypes() {
    return apiFetch('/fuel-types');
}

// GRN
async function createGrn(payload) {
    return apiFetch('/grn', { method: 'POST', body: JSON.stringify(payload) });
}

async function getAllGrns() {
    return apiFetch('/grn');
}

async function getGrnsByBranch(branchId) {
    return apiFetch('/grn/branch/' + branchId);
}

// Stock
async function getAllStock() {
    return apiFetch('/stock');
}

async function getStockByBranch(branchId) {
    return apiFetch('/stock/branch/' + branchId);
}

// Attendance
async function getAllAttendance() {
    return apiFetch('/attendance');
}

async function getPendingAttendance() {
    return apiFetch('/attendance/pending');
}

async function getBranchAttendance() {
    return apiFetch('/attendance/branch');
}

async function approveAttendance(id) {
    return apiFetch('/attendance/' + id + '/approve', { method: 'PUT' });
}

async function rejectAttendance(id, note) {
    return apiFetch('/attendance/' + id + '/reject', {
        method: 'PUT',
        body: JSON.stringify({ note: note || '' })
    });
}

async function clockIn() {
    return apiFetch('/attendance/clock-in', { method: 'POST' });
}

async function clockOut() {
    return apiFetch('/attendance/clock-out', { method: 'PUT' });
}

async function getMyActiveSession() {
    const res = await fetch(API_BASE + '/attendance/my/active', {
        headers: { 'Authorization': 'Bearer ' + getToken() }
    });
    if (res.status === 401) { localStorage.clear(); window.location.href = '/login.html'; return; }
    if (res.status === 204) return null;
    return res.json();
}

async function getMyAttendance() {
    return apiFetch('/attendance/my');
}

// Daily Sales
async function recordDailySale(payload) {
    return apiFetch('/sales', { method: 'POST', body: JSON.stringify(payload) });
}

async function getAllSales() {
    return apiFetch('/sales');
}

async function getSalesByBranch(branchId) {
    return apiFetch('/sales/branch/' + branchId);
}

// ---- Form Validation Helpers ----

function setFieldError(inputId, message) {
    const input = document.getElementById(inputId);
    const errorEl = document.getElementById(inputId + '-error');
    if (!input) return;
    input.classList.add('invalid');
    input.classList.remove('valid');
    if (errorEl) {
        errorEl.textContent = message;
        errorEl.classList.add('show');
    }
}

function setFieldValid(inputId) {
    const input = document.getElementById(inputId);
    const errorEl = document.getElementById(inputId + '-error');
    if (!input) return;
    input.classList.remove('invalid');
    input.classList.add('valid');
    if (errorEl) errorEl.classList.remove('show');
}

function clearFieldState(inputId) {
    const input = document.getElementById(inputId);
    const errorEl = document.getElementById(inputId + '-error');
    if (!input) return;
    input.classList.remove('invalid', 'valid');
    if (errorEl) errorEl.classList.remove('show');
}

function clearAllFields(ids) {
    ids.forEach(clearFieldState);
}

/**
 * Validate create-user form fields.
 * Returns true if valid, false if any field fails.
 * @param {boolean} requireBranch - whether branchId is required
 */
function validateUserForm(requireBranch = true) {
    let valid = true;

    // Role
    const role = document.getElementById('cu-role')?.value;
    if (role !== undefined) {
        if (!role) {
            setFieldError('cu-role', 'Please select a role.');
            valid = false;
        } else {
            setFieldValid('cu-role');
        }
    }

    // Branch
    if (requireBranch) {
        const branch = document.getElementById('cu-branch')?.value;
        if (branch !== undefined) {
            if (!branch) {
                setFieldError('cu-branch', 'Please select a branch.');
                valid = false;
            } else {
                setFieldValid('cu-branch');
            }
        }
    }

    // Full Name
    const fullName = document.getElementById('cu-fullName').value.trim();
    if (!fullName) {
        setFieldError('cu-fullName', 'Full name is required.');
        valid = false;
    } else {
        setFieldValid('cu-fullName');
    }

    // Username
    const username = document.getElementById('cu-username').value.trim();
    if (!username) {
        setFieldError('cu-username', 'Username is required.');
        valid = false;
    } else if (username.length < 3) {
        setFieldError('cu-username', 'Username must be at least 3 characters.');
        valid = false;
    } else if (username.length > 50) {
        setFieldError('cu-username', 'Username must be at most 50 characters.');
        valid = false;
    } else {
        setFieldValid('cu-username');
    }

    // Password
    const password = document.getElementById('cu-password').value;
    if (!password) {
        setFieldError('cu-password', 'Password is required.');
        valid = false;
    } else if (password.length < 6) {
        setFieldError('cu-password', 'Password must be at least 6 characters.');
        valid = false;
    } else {
        setFieldValid('cu-password');
    }

    // Email (optional but validate format if provided)
    const email = document.getElementById('cu-email').value.trim();
    if (email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
        setFieldError('cu-email', 'Enter a valid email address.');
        valid = false;
    } else {
        setFieldValid('cu-email');
    }

    return valid;
}
