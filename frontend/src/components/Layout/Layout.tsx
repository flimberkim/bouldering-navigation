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
            <span>볼더가이드</span>
          </Link>
        </div>
      </header>
      <main className="layout__main">
        <Outlet />
      </main>
      <footer className="layout__footer">
        <p>클라이머가 클라이머를 위해 만들었습니다. 등급은 참고용이며, 등반은 본인 책임입니다.</p>
      </footer>
    </div>
  );
}
