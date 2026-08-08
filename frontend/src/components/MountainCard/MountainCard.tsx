import { Link } from "react-router-dom";
import type { MountainSearchResult } from "../../api/types";
import { GradeBadge } from "../GradeBadge/GradeBadge";
import "./MountainCard.css";

interface MountainCardProps {
  mountain: MountainSearchResult;
}

export function MountainCard({ mountain }: MountainCardProps) {
  const rockCount = mountain.rocks.length;
  const problemCount = mountain.rocks.reduce((sum, rock) => sum + rock.problems.length, 0);

  return (
    <article className="mountain-card">
      <header className="mountain-card__header">
        <div>
          <h2 className="mountain-card__name">{mountain.name}</h2>
          <div className="mountain-card__region">
            <svg className="mountain-card__pin" viewBox="0 0 20 20" aria-hidden="true">
              <path
                d="M10 18s6-5.7 6-10.4A6 6 0 1 0 4 7.6C4 12.3 10 18 10 18Z"
                fill="currentColor"
              />
              <circle cx="10" cy="7.5" r="2.2" fill="var(--color-surface)" />
            </svg>
            {mountain.regionName}
          </div>
        </div>
        <div className="mountain-card__stats">
          <span>
            {rockCount} rock{rockCount === 1 ? "" : "s"}
          </span>
          <span aria-hidden="true">&middot;</span>
          <span>
            {problemCount} problem{problemCount === 1 ? "" : "s"}
          </span>
        </div>
      </header>

      {rockCount === 0 ? (
        <p className="mountain-card__empty">No rocks recorded for this mountain yet.</p>
      ) : (
        <div className="mountain-card__rocks">
          {mountain.rocks.map((rock) => (
            <section className="rock-block" key={rock.rockId}>
              <h3 className="rock-block__name">{rock.name}</h3>
              {rock.problems.length === 0 ? (
                <p className="mountain-card__empty">No problems recorded yet.</p>
              ) : (
                <ul className="rock-block__problems">
                  {rock.problems.map((problem) => (
                    <li key={problem.problemId}>
                      <Link
                        to={`/problems/${problem.problemId}`}
                        className="problem-chip"
                      >
                        <span className="problem-chip__name">{problem.name}</span>
                        <GradeBadge grade={problem.grade} size="sm" />
                      </Link>
                    </li>
                  ))}
                </ul>
              )}
            </section>
          ))}
        </div>
      )}
    </article>
  );
}
