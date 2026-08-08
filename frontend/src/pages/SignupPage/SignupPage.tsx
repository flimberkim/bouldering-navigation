import { useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { ApiError } from "../../api/client";
import { useAuth } from "../../hooks/useAuth";
import "./SignupPage.css";

export function SignupPage() {
  const { signup } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [nickname, setNickname] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setErrorMessage("");
    setIsSubmitting(true);
    try {
      await signup(email, password, nickname);
      navigate("/");
    } catch (error) {
      const message = error instanceof ApiError ? error.message : "회원가입 중 문제가 발생했습니다.";
      setErrorMessage(message);
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <div className="auth-page">
      <h1>회원가입</h1>
      <form className="auth-page__form" onSubmit={handleSubmit}>
        <label className="auth-page__field">
          <span>이메일</span>
          <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
        </label>
        <label className="auth-page__field">
          <span>닉네임</span>
          <input
            type="text"
            value={nickname}
            onChange={(e) => setNickname(e.target.value)}
            required
          />
        </label>
        <label className="auth-page__field">
          <span>비밀번호 (8자 이상)</span>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            minLength={8}
            required
          />
        </label>

        {errorMessage && (
          <p className="auth-page__error" role="alert">
            {errorMessage}
          </p>
        )}

        <button type="submit" className="auth-page__submit" disabled={isSubmitting}>
          {isSubmitting ? "가입 중…" : "회원가입"}
        </button>
      </form>
      <p className="auth-page__switch">
        이미 계정이 있으신가요? <Link to="/login">로그인</Link>
      </p>
    </div>
  );
}
