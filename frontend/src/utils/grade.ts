// Bouldering grades in the wild come in a few common notations (V-scale,
// Fontainebleau/French scale, sometimes a plain beginner label). We don't
// know in advance which one the backend will hand us for a given problem,
// so this maps as many of them as we can onto a small set of difficulty
// "tiers" used purely for badge coloring. The raw grade string is always
// displayed verbatim — this never rewrites the grade itself.

export type GradeTier = 0 | 1 | 2 | 3 | 4 | 5;

const TIER_COUNT = 6;

// Fontainebleau grade -> approximate V-scale number, for coloring only.
const FONT_TO_V: Record<string, number> = {
  "3": -2, "4": -2, "5": -1, "5+": 0,
  "6A": 0, "6A+": 1, "6B": 2, "6B+": 3, "6C": 4, "6C+": 5,
  "7A": 6, "7A+": 7, "7B": 8, "7B+": 9, "7C": 10, "7C+": 11,
  "8A": 12, "8A+": 13, "8B": 14, "8B+": 15, "8C": 16, "8C+": 17,
  "9A": 17,
};

function tierFromVNumber(v: number): GradeTier {
  if (v <= 0) return 0;
  if (v <= 2) return 1;
  if (v <= 4) return 2;
  if (v <= 6) return 3;
  if (v <= 8) return 4;
  return 5;
}

/** Classifies a grade string into a difficulty tier (0 = easiest, 5 = hardest). */
export function gradeTier(grade: string): GradeTier {
  const normalized = grade.trim().toUpperCase();

  const vMatch = normalized.match(/^VB$|^V\s*(-?\d+)/);
  if (vMatch) {
    if (normalized.startsWith("VB")) return 0;
    return tierFromVNumber(Number(vMatch[1]));
  }

  const fontMatch = normalized.replace(/\s+/g, "");
  if (fontMatch in FONT_TO_V) {
    return tierFromVNumber(FONT_TO_V[fontMatch]);
  }

  return 2; // unknown notation: neutral mid tier rather than guessing wrong
}

const TIER_CLASS_NAMES: Record<GradeTier, string> = {
  0: "grade-tier-0",
  1: "grade-tier-1",
  2: "grade-tier-2",
  3: "grade-tier-3",
  4: "grade-tier-4",
  5: "grade-tier-5",
};

export function gradeTierClassName(grade: string): string {
  const tier = gradeTier(grade);
  return TIER_CLASS_NAMES[tier % TIER_COUNT as GradeTier];
}
