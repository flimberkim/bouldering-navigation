import { Route, Routes } from "react-router-dom";
import { Layout } from "./components/Layout/Layout";
import { SearchPage } from "./pages/SearchPage/SearchPage";
import { ProblemDetailPage } from "./pages/ProblemDetailPage/ProblemDetailPage";
import { LoginPage } from "./pages/LoginPage/LoginPage";
import { SignupPage } from "./pages/SignupPage/SignupPage";

function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route path="/" element={<SearchPage />} />
        <Route path="/problems/:id" element={<ProblemDetailPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignupPage />} />
      </Route>
    </Routes>
  );
}

export default App;
