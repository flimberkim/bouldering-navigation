import { gradeTierClassName } from "../../utils/grade";
import "./GradeBadge.css";

interface GradeBadgeProps {
  grade: string;
  size?: "sm" | "md" | "lg";
}

export function GradeBadge({ grade, size = "md" }: GradeBadgeProps) {
  return (
    <span className={`grade-badge grade-badge--${size} ${gradeTierClassName(grade)}`}>
      {grade}
    </span>
  );
}
