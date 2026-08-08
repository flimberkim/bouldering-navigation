// Types mirroring the schemas defined in api-spec.yaml.
// Keep these in sync with the backend contract — do not add fields the spec
// doesn't define.

export interface ProblemSummary {
  problemId: number;
  name: string;
  grade: string;
}

export interface RockSearchResult {
  rockId: number;
  name: string;
  problems: ProblemSummary[];
}

export interface MountainSearchResult {
  mountainId: number;
  name: string;
  regionName: string;
  rocks: RockSearchResult[];
}

export type VideoPlatform = "YOUTUBE" | "INSTAGRAM" | "OTHER";

export interface Video {
  videoId: number;
  url: string;
  platform: VideoPlatform;
}

export interface ProblemDetail {
  problemId: number;
  name: string;
  grade: string;
  rockName: string;
  mountainName: string;
  regionName: string;
  videos: Video[];
}

export interface SignupRequest {
  email: string;
  password: string;
  nickname: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResult {
  token: string;
  tokenType: string;
  userId: number;
  nickname: string;
}

export interface CurrentUser {
  id: number;
  email: string;
  nickname: string;
}
