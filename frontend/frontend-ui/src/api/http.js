import axios from "axios";

/**
 * Pre-configured Axios instance shared across all API modules.
 * - Sends cookies with every request (required for Spring Session).
 * - Intercepts 401 responses and redirects to the login page so
 *   any view can trigger a re-login without ad-hoc redirect logic.
 */
export const http = axios.create({
  baseURL: "/api",
  withCredentials: true,
  headers: {
    "Content-Type": "application/json",
    Accept: "application/json",
  },
});

http.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      const currentPath = window.location.pathname;
      if (currentPath !== "/login" && currentPath !== "/signup") {
        window.location.href = "/login";
      }
    }
    return Promise.reject(error);
  }
);

/**
 * Extracts a human-readable error message from an Axios error.
 * Priority: server `message` field → server `error` field → generic fallback.
 *
 * @param {unknown} error - The caught error object.
 * @param {string}  [fallback] - Message to show when no server detail is available.
 * @returns {string}
 */
export function extractError(error, fallback = "An unexpected error occurred.") {
  if (axios.isAxiosError(error) && error.response?.data) {
    const data = error.response.data;
    return data.message || data.error || fallback;
  }
  return fallback;
}
