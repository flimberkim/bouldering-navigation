import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { ApiError } from "../../api/client";
import { getProblem } from "../../api/problems";
import type { ProblemDetail } from "../../api/types";
import { Breadcrumb } from "../../components/Breadcrumb/Breadcrumb";
import { GradeBadge } from "../../components/GradeBadge/GradeBadge";
import { VideoLink } from "../../components/VideoLink/VideoLink";
import "./ProblemDetailPage.css";

type Status = "loading" | "not-found" | "error" | "success";

export function ProblemDetailPage() {
  const { id } = useParams<{ id: string }>();
  const [status, setStatus] = useState<Status>("loading");
  const [problem, setProblem] = useState<ProblemDetail | null>(null);
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    if (!id) return;
    let cancelled = false;
    setStatus("loading");

    getProblem(id)
      .then((data) => {
        if (cancelled) return;
        setProblem(data);
        setStatus("success");
      })
      .catch((error: unknown) => {
        if (cancelled) return;
        if (error instanceof ApiError && error.status === 404) {
          setStatus("not-found");
          return;
        }
        const message =
          error instanceof ApiError
            ? error.message
            : "문제를 불러오는 중 문제가 발생했습니다.";
        setErrorMessage(message);
        setStatus("error");
      });

    return () => {
      cancelled = true;
    };
  }, [id]);

  if (status === "loading") {
    return (
      <div className="problem-page__state" role="status">
        <div className="search-page__spinner" aria-hidden="true" />
        <p>문제를 불러오는 중&hellip;</p>
      </div>
    );
  }

  if (status === "not-found") {
    return (
      <div className="problem-page__state">
        <h1>문제를 찾을 수 없습니다</h1>
        <p>해당 ID의 문제를 찾을 수 없습니다.</p>
        <Link to="/" className="problem-page__back">
          &larr; 검색으로 돌아가기
        </Link>
      </div>
    );
  }

  if (status === "error") {
    return (
      <div className="problem-page__state problem-page__state--error" role="alert">
        <p>{errorMessage}</p>
      </div>
    );
  }

  if (!problem) return null;

  return (
    <div className="problem-page">
      <Link to="/" className="problem-page__back">
        &larr; 검색으로 돌아가기
      </Link>

      <Breadcrumb items={[problem.regionName, problem.mountainName, problem.rockName]} />

      <header className="problem-page__header">
        <h1>{problem.name}</h1>
        <GradeBadge grade={problem.grade} size="lg" />
      </header>

      <section className="problem-page__videos">
        <h2>완등 영상</h2>
        {problem.videos.length === 0 ? (
          <p className="problem-page__empty">
            아직 등록된 완등 영상이 없습니다.
          </p>
        ) : (
          <ul className="problem-page__video-list">
            {problem.videos.map((video) => (
              <li key={video.videoId}>
                <VideoLink video={video} />
              </li>
            ))}
          </ul>
        )}
      </section>
    </div>
  );
}
