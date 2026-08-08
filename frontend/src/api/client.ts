// Small fetch wrapper shared by every typed endpoint function in this
// directory. The base URL is configurable via VITE_API_BASE_URL so the app
// can point at different backends without a code change.

export const API_BASE_URL: string =
  import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

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
  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...init,
    headers: {
      Accept: "application/json",
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
