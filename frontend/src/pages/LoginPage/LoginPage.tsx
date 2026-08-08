import { useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { ApiError } from "../../api/client";
import { useAuth } from "../../hooks/useAuth";
import "./LoginPage.css";

export function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setErrorMessage("");
    setIsSubmitting(true);
    try {
      await login(email, password);
      navigate("/");
    } catch (error) {
      const message = error instanceof ApiError ? error.message : "로그인 중 문제가 발생했습니다.";
      setErrorMessage(message);
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <div className="auth-page">
      <h1>로그인</h1>
      <form className="auth-page__form" onSubmit={handleSubmit}>
        <label className="auth-page__field">
          <span>이메일</span>
          <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
        </label>
        <label className="auth-page__field">
          <span>비밀번호</span>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </label>

        {errorMessage && (
          <p className="auth-page__error" role="alert">
            {errorMessage}
          </p>
        )}

        <button type="submit" className="auth-page__submit" disabled={isSubmitting}>
          {isSubmitting ? "로그인 중…" : "로그인"}
        </button>
      </form>
      <p className="auth-page__switch">
        아직 계정이 없으신가요? <Link to="/signup">회원가입</Link>
      </p>
    </div>
  );
}
