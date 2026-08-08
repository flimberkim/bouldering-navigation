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
  const query = searchParams.get("name") ?? "";

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
            : "Something went wrong while searching. Please try again.";
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
      setSearchParams({ name: value });
    }
  }

  return (
    <div className="search-page">
      <section className="search-page__intro">
        <h1>Find your next problem</h1>
        <p>Search bouldering mountains by name to browse their rocks and problems.</p>
        <SearchBar initialValue={query} onSearch={handleSearch} />
      </section>

      {status === "idle" && (
        <div className="search-page__state">
          <p>Start by searching for a mountain, e.g. &ldquo;Fontainebleau&rdquo;.</p>
        </div>
      )}

      {status === "loading" && (
        <div className="search-page__state" role="status">
          <div className="search-page__spinner" aria-hidden="true" />
          <p>Searching&hellip;</p>
        </div>
      )}

      {status === "error" && (
        <div className="search-page__state search-page__state--error" role="alert">
          <p>{errorMessage}</p>
        </div>
      )}

      {status === "success" && results.length === 0 && (
        <div className="search-page__state">
          <p>No mountains found for &ldquo;{query}&rdquo;.</p>
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
