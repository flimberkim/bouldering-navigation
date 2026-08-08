import { createContext } from "react";
import type { CurrentUser } from "../api/types";

export interface AuthContextValue {
  user: CurrentUser | null;
  isLoading: boolean;
  login: (email: string, password: string) => Promise<void>;
  signup: (email: string, password: string, nickname: string) => Promise<void>;
  logout: () => void;
}

export const AuthContext = createContext<AuthContextValue | undefined>(undefined);
