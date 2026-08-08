// Small fetch wrapper shared by every typed endpoint function in this
// directory. The base URL is configurable via VITE_API_BASE_URL so the app
// can point at different backends without a code change.

export const API_BASE_URL: string =
  import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

const AUTH_TOKEN_STORAGE_KEY = "boulderguide.authToken";

export function getStoredToken(): string | null {
  return localStorage.getItem(AUTH_TOKEN_STORAGE_KEY);
}

export function setStoredToken(token: string): void {
  localStorage.setItem(AUTH_TOKEN_STORAGE_KEY, token);
}

export function clearStoredToken(): void {
  localStorage.removeItem(AUTH_TOKEN_STORAGE_KEY);
}

export class ApiError extends Error {
  readonly status: number;

  constructor(status: number, message?: string) {
    super(message && message.length > 0 ? message : `Request failed with status ${status}`);
    this.name = "ApiError";
    this.status = status;
  }
}

async function readErrorMessage(response: Response): Promise<string> {
  try {
    const text = await response.text();
    return text;
  } catch {
    return "";
  }
}

export async function apiRequest<T>(path: string, init?: RequestInit): Promise<T> {
  const token = getStoredToken();

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...init,
    headers: {
      Accept: "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...init?.headers,
    },
  });

  if (!response.ok) {
    throw new ApiError(response.status, await readErrorMessage(response));
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return (await response.json()) as T;
}
