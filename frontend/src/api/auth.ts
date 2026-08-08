import { apiRequest } from "./client";
import type { AuthResult, CurrentUser, LoginRequest, SignupRequest } from "./types";

/**
 * POST /api/auth/signup
 * Create an account and receive a bearer token.
 */
export function signup(request: SignupRequest): Promise<AuthResult> {
  return apiRequest<AuthResult>("/api/auth/signup", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(request),
  });
}

/**
 * POST /api/auth/login
 * Log in with email and password, receive a bearer token.
 */
export function login(request: LoginRequest): Promise<AuthResult> {
  return apiRequest<AuthResult>("/api/auth/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(request),
  });
}

/**
 * GET /api/auth/me
 * Get the currently authenticated user (requires a stored token).
 */
export function getCurrentUser(): Promise<CurrentUser> {
  return apiRequest<CurrentUser>("/api/auth/me");
}
