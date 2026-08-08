import { useEffect, useState, type ReactNode } from "react";
import { clearStoredToken, getCurrentUser, getStoredToken, login as loginRequest, setStoredToken, signup as signupRequest } from "../api";
import type { CurrentUser } from "../api/types";
import { AuthContext } from "./auth-context-value";

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<CurrentUser | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    if (!getStoredToken()) {
      setIsLoading(false);
      return;
    }

    getCurrentUser()
      .then(setUser)
      .catch(() => clearStoredToken())
      .finally(() => setIsLoading(false));
  }, []);

  async function login(email: string, password: string) {
    const result = await loginRequest({ email, password });
    setStoredToken(result.token);
    setUser(await getCurrentUser());
  }

  async function signup(email: string, password: string, nickname: string) {
    const result = await signupRequest({ email, password, nickname });
    setStoredToken(result.token);
    setUser(await getCurrentUser());
  }

  function logout() {
    clearStoredToken();
    setUser(null);
  }

  return (
    <AuthContext.Provider value={{ user, isLoading, login, signup, logout }}>
      {children}
    </AuthContext.Provider>
  );
}
