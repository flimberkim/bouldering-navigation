import { Route, Routes } from "react-router-dom";
import { Layout } from "./components/Layout/Layout";
import { SearchPage } from "./pages/SearchPage/SearchPage";
import { ProblemDetailPage } from "./pages/ProblemDetailPage/ProblemDetailPage";

function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route path="/" element={<SearchPage />} />
        <Route path="/problems/:id" element={<ProblemDetailPage />} />
      </Route>
    </Routes>
  );
}

export default App;
