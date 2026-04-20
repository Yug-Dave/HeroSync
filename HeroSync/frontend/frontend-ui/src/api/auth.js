import { http, extractError } from "./http";

/**
 * Registers a new user account and triggers a verification email.
 * On success the user is NOT yet logged in — they must verify their email first.
 * @param {{ name: string, email: string, password: string }} payload
 * @returns {Promise<AuthResponse>}
 * @throws {string} Human-readable error message on failure.
 */
export async function register(payload) {
  try {
    const res = await http.post("/api/auth/register", payload);
    return res.data;
  } catch (error) {
    throw extractError(error, "Registration failed. Please try again.");
  }
}

/**
 * Logs in with email and password, creating an HTTP session.
 * Throws "EMAIL_NOT_VERIFIED" if the account exists but the email is unverified.
 * @param {{ email: string, password: string }} payload
 * @returns {Promise<AuthResponse>}
 * @throws {string} Human-readable error message on failure.
 */
export async function login(payload) {
  try {
    const res = await http.post("/api/auth/login", payload);
    return res.data;
  } catch (error) {
    throw extractError(error, "Login failed. Please check your credentials.");
  }
}

/**
 * Verifies an email address using the one-time token from the verification link.
 * @param {string} token
 * @returns {Promise<{ message: string }>}
 * @throws {string} Human-readable error message on failure.
 */
export async function verifyEmail(token) {
  try {
    const res = await http.get("/api/auth/verify", { params: { token } });
    return res.data;
  } catch (error) {
    throw extractError(error, "Verification failed. The link may have expired.");
  }
}

/**
 * Re-sends the verification email to the provided address.
 * @param {string} email
 * @returns {Promise<{ message: string }>}
 * @throws {string} Human-readable error message on failure.
 */
export async function resendVerification(email) {
  try {
    const res = await http.post("/api/auth/resend-verification", { email });
    return res.data;
  } catch (error) {
    throw extractError(error, "Could not resend verification email. Please try again.");
  }
}

/**
 * Returns the currently authenticated user's summary (id, name, email).
 * @returns {Promise<UserSummaryDto>}
 */
export async function me() {
  const res = await http.get("/api/auth/me", {
    headers: {
      "Cache-Control": "no-cache",
      Pragma: "no-cache",
      Expires: "0",
    },
  });
  return res.data;
}

/**
 * Updates the authenticated user's profile (name, bio, avatar).
 * @param {{ name?: string, bio?: string, avatar?: string }} payload
 * @returns {Promise<ProfileDto>}
 * @throws {string} Human-readable error message on failure.
 */
export async function updateProfile(payload) {
  try {
    const res = await http.put("/api/profile/me", payload);
    return res.data;
  } catch (error) {
    throw extractError(error, "Profile update failed. Please try again.");
  }
}

/**
 * Terminates the current session by calling the Spring Security logout endpoint.
 */
export async function logout() {
  await http.post("/api/auth/logout");
}
