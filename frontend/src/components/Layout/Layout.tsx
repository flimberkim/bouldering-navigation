import { Link, Outlet } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth";
import "./Layout.css";

export function Layout() {
  const { user, isLoading, logout } = useAuth();

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

          {!isLoading && (
            <nav className="layout__auth-nav">
              {user ? (
                <>
                  <span className="layout__auth-nickname">{user.nickname}님</span>
                  <button type="button" className="layout__auth-link" onClick={logout}>
                    로그아웃
                  </button>
                </>
              ) : (
                <>
                  <Link to="/login" className="layout__auth-link">
                    로그인
                  </Link>
                  <Link to="/signup" className="layout__auth-link">
                    회원가입
                  </Link>
                </>
              )}
            </nav>
          )}
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
