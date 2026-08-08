import { apiRequest } from "./client";
import type { MountainSearchResult } from "./types";

/**
 * GET /api/mountains?name=
 * Search mountains by name (with nested rocks/problems).
 */
export function searchMountains(name: string): Promise<MountainSearchResult[]> {
  const params = new URLSearchParams({ name });
  return apiRequest<MountainSearchResult[]>(`/api/mountains?${params.toString()}`);
}
