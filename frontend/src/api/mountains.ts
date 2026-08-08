import { apiRequest } from "./client";
import type { MountainSearchResult } from "./types";

/**
 * GET /api/mountains?q=
 * Search by mountain name, problem name, or problem grade — results show
 * which mountain/rock a matching problem belongs to.
 */
export function searchMountains(query: string): Promise<MountainSearchResult[]> {
  const params = new URLSearchParams({ q: query });
  return apiRequest<MountainSearchResult[]>(`/api/mountains?${params.toString()}`);
}
