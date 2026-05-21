import React from "react";
import { Link, Navigate, Route, Routes } from "react-router-dom";
import Home from "./pages/Home.jsx";
import LoginRegister from "./pages/LoginRegister.jsx";
import CourseCatalog from "./pages/CourseCatalog.jsx";
import MyCourses from "./pages/MyCourses.jsx";
import AdminDashboard from "./pages/AdminDashboard.jsx";
import { setRole, setToken } from "./lib/api.js";
import { useAuth } from "./lib/useAuth.js";

function Nav() {
  const auth = useAuth();

  function logout() {
    setToken("");
    setRole("");
  }

  return (
    <div className="nav">
      <div className="nav-inner">
        <Link to="/">
          <strong>Course Portal</strong>
        </Link>
        <div className="nav-links">
          <Link to="/courses" className="badge">
            Course Catalog
          </Link>
          {auth.isAuthed ? (
            <>
              <Link to="/my-courses" className="badge">
                My Courses
              </Link>
              {auth.isAdmin ? (
                <Link to="/admin" className="badge">
                  Admin Dashboard
                </Link>
              ) : null}
              <button className="secondary" style={{ width: "auto" }} onClick={logout}>
                Logout
              </button>
            </>
          ) : (
            <Link to="/login" className="badge">
              Login / Register
            </Link>
          )}
        </div>
      </div>
    </div>
  );
}

function RequireAuth({ children }) {
  const auth = useAuth();
  if (!auth.isAuthed) return <Navigate to="/login" replace />;
  return children;
}

function RequireAdmin({ children }) {
  const auth = useAuth();
  if (!auth.isAuthed) return <Navigate to="/login" replace />;
  if (!auth.isAdmin) return <Navigate to="/" replace />;
  return children;
}

export default function App() {
  return (
    <>
      <Nav />
      <div className="container">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<LoginRegister />} />
          <Route path="/courses" element={<CourseCatalog />} />
          <Route
            path="/my-courses"
            element={
              <RequireAuth>
                <MyCourses />
              </RequireAuth>
            }
          />
          <Route
            path="/admin"
            element={
              <RequireAdmin>
                <AdminDashboard />
              </RequireAdmin>
            }
          />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </div>
    </>
  );
}

