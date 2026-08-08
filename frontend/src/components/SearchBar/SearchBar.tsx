import { useState, type FormEvent } from "react";
import "./SearchBar.css";

interface SearchBarProps {
  initialValue?: string;
  onSearch: (value: string) => void;
  placeholder?: string;
}

export function SearchBar({ initialValue = "", onSearch, placeholder }: SearchBarProps) {
  const [value, setValue] = useState(initialValue);

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    onSearch(value.trim());
  }

  return (
    <form className="search-bar" onSubmit={handleSubmit} role="search">
      <svg className="search-bar__icon" viewBox="0 0 20 20" aria-hidden="true">
        <circle cx="9" cy="9" r="6.5" fill="none" stroke="currentColor" strokeWidth="1.7" />
        <path d="M14 14l4.5 4.5" stroke="currentColor" strokeWidth="1.7" strokeLinecap="round" />
      </svg>
      <input
        type="search"
        className="search-bar__input"
        value={value}
        onChange={(event) => setValue(event.target.value)}
        placeholder={placeholder ?? "Search mountains by name…"}
        aria-label="Search mountains by name"
      />
      <button type="submit" className="search-bar__button">
        Search
      </button>
    </form>
  );
}
