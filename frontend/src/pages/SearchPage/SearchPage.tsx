import { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { ApiError } from "../../api/client";
import { searchMountains } from "../../api/mountains";
import type { MountainSearchResult } from "../../api/types";
import { SearchBar } from "../../components/SearchBar/SearchBar";
import { MountainCard } from "../../components/MountainCard/MountainCard";
import "./SearchPage.css";

type Status = "idle" | "loading" | "error" | "success";

export function SearchPage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const query = searchParams.get("q") ?? "";

  const [status, setStatus] = useState<Status>("idle");
  const [results, setResults] = useState<MountainSearchResult[]>([]);
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    if (query.trim() === "") {
      setStatus("idle");
      setResults([]);
      return;
    }

    let cancelled = false;
    setStatus("loading");

    searchMountains(query)
      .then((mountains) => {
        if (cancelled) return;
        setResults(mountains);
        setStatus("success");
      })
      .catch((error: unknown) => {
        if (cancelled) return;
        const message =
          error instanceof ApiError
            ? error.message
            : "검색 중 문제가 발생했습니다. 다시 시도해주세요.";
        setErrorMessage(message);
        setStatus("error");
      });

    return () => {
      cancelled = true;
    };
  }, [query]);

  function handleSearch(value: string) {
    if (value === "") {
      setSearchParams({});
    } else {
      setSearchParams({ q: value });
    }
  }

  return (
    <div className="search-page">
      <section className="search-page__intro">
        <h1>다음 도전할 문제를 찾아보세요</h1>
        <p>산 이름, 문제 이름, 또는 난이도로 검색해서 바위와 문제를 둘러보세요.</p>
        <SearchBar initialValue={query} onSearch={handleSearch} />
      </section>

      {status === "idle" && (
        <div className="search-page__state">
          <p>
            산 이름, 문제 이름, 난이도로 검색해보세요. 예: &ldquo;수락산&rdquo;, &ldquo;크랙&rdquo;,
            &ldquo;V4&rdquo;
          </p>
        </div>
      )}

      {status === "loading" && (
        <div className="search-page__state" role="status">
          <div className="search-page__spinner" aria-hidden="true" />
          <p>검색 중&hellip;</p>
        </div>
      )}

      {status === "error" && (
        <div className="search-page__state search-page__state--error" role="alert">
          <p>{errorMessage}</p>
        </div>
      )}

      {status === "success" && results.length === 0 && (
        <div className="search-page__state">
          <p>&ldquo;{query}&rdquo;에 대한 검색 결과가 없습니다.</p>
        </div>
      )}

      {status === "success" && results.length > 0 && (
        <ul className="search-page__results">
          {results.map((mountain) => (
            <li key={mountain.mountainId}>
              <MountainCard mountain={mountain} />
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
