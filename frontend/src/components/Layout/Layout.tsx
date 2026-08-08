import { Link, Outlet } from "react-router-dom";
import "./Layout.css";

export function Layout() {
  return (
    <div className="layout">
      <header className="layout__header">
        <div className="layout__header-inner">
          <Link to="/" className="layout__brand">
            <svg className="layout__brand-icon" viewBox="0 0 32 32" aria-hidden="true">
              <path d="M2 26 L11 10 L16 17 L21 7 L30 26 Z" fill="currentColor" />
              <circle cx="24.5" cy="6" r="2" fill="var(--color-accent)" />
            </svg>
            <span>Crag Guide</span>
          </Link>
        </div>
      </header>
      <main className="layout__main">
        <Outlet />
      </main>
      <footer className="layout__footer">
        <p>Built for climbers, by climbers. Grades are approximate — send at your own risk.</p>
      </footer>
    </div>
  );
}
