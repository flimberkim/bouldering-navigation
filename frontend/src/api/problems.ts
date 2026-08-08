import { apiRequest } from "./client";
import type { ProblemDetail } from "./types";

/**
 * GET /api/problems/{id}
 * Get problem detail with grade and completion video links.
 */
export function getProblem(id: string | number): Promise<ProblemDetail> {
  return apiRequest<ProblemDetail>(`/api/problems/${encodeURIComponent(id)}`);
}
