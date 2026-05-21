import React from "react";
import { Link } from "react-router-dom";

export default function Home() {
  return (
    <div className="grid">
      <div className="col-12 card">
        <h1 style={{ marginTop: 0 }}>Course Enrollment Portal</h1>
        <p className="muted">
          Browse courses, enroll with seat checks, track your total fee, and use AI-powered search and chatbot support.
        </p>
        <div className="row" style={{ marginTop: 10, flexWrap: "wrap" }}>
          <Link to="/courses" className="badge">
            Explore Courses
          </Link>
          <Link to="/login" className="badge">
            Login / Register (OTP)
          </Link>
        </div>
      </div>

      <div className="col-6 card">
        <h3 style={{ marginTop: 0 }}>Student Features</h3>
        <ul style={{ margin: 0, paddingLeft: 18 }}>
          <li>Seat availability check</li>
          <li>No duplicate enrollment</li>
          <li>Total fee shown in My Courses</li>
          <li>AI smart search and chatbot</li>
        </ul>
      </div>

      <div className="col-6 card">
        <h3 style={{ marginTop: 0 }}>Admin Features</h3>
        <ul style={{ margin: 0, paddingLeft: 18 }}>
          <li>Manage courses (CRUD)</li>
          <li>AI analytics insights</li>
          <li>AI prediction for courses filling soon</li>
        </ul>
        <p className="muted" style={{ marginBottom: 0 }}>
          Default seeded admin: admin@course.local
        </p>
      </div>
    </div>
  );
}

