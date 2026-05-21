const API_BASE = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

export function getToken() {
  return localStorage.getItem("token") || "";
}

export function setToken(token) {
  if (!token) {
    localStorage.removeItem("token");
    window.dispatchEvent(new Event("authchange"));
    return;
  }
  localStorage.setItem("token", token);
  window.dispatchEvent(new Event("authchange"));
}

export function setRole(role) {
  if (!role) {
    localStorage.removeItem("role");
    window.dispatchEvent(new Event("authchange"));
    return;
  }
  localStorage.setItem("role", role);
  window.dispatchEvent(new Event("authchange"));
}

export function getRole() {
  return localStorage.getItem("role") || "";
}

export async function apiFetch(path, options = {}) {
  const headers = new Headers(options.headers || {});
  headers.set("Content-Type", "application/json");
  const token = getToken();
  if (token) {
    headers.set("Authorization", `Bearer ${token}`);
  }

  const res = await fetch(`${API_BASE}${path}`, { ...options, headers });
  if (res.status === 204) return null;
  const text = await res.text();
  const data = text ? JSON.parse(text) : null;
  if (!res.ok) {
    const message = data?.message || data?.error || `Request failed (${res.status})`;
    const err = new Error(message);
    err.status = res.status;
    err.data = data;
    throw err;
  }
  return data;
}
