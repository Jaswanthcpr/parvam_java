import React, { useEffect, useState } from "react";
import { apiFetch } from "../lib/api.js";
import { useAuth } from "../lib/useAuth.js";

export default function CourseCatalog() {
  const auth = useAuth();
  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [searchQuery, setSearchQuery] = useState("");
  const [searchNote, setSearchNote] = useState("");

  const [chatMessage, setChatMessage] = useState("");
  const [chatReply, setChatReply] = useState("");

  async function load() {
    setLoading(true);
    setError("");
    try {
      const data = await apiFetch("/api/courses");
      setCourses(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
  }, []);

  async function enroll(courseId) {
    setError("");
    try {
      await apiFetch("/api/enrollments", {
        method: "POST",
        body: JSON.stringify({ courseId })
      });
      await load();
    } catch (err) {
      setError(err.message);
    }
  }

  async function smartSearch(e) {
    e.preventDefault();
    setError("");
    setSearchNote("");
    try {
      const res = await apiFetch("/api/ai/search", {
        method: "POST",
        body: JSON.stringify({ query: searchQuery })
      });
      setCourses(res.courses || []);
      setSearchNote(res.note || "");
    } catch (err) {
      setError(err.message);
    }
  }

  async function sendChat(e) {
    e.preventDefault();
    setError("");
    setChatReply("");
    try {
      const res = await apiFetch("/api/ai/chat", {
        method: "POST",
        body: JSON.stringify({ message: chatMessage })
      });
      setChatReply(res.reply || "");
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <div className="grid">
      <div className="col-12 card">
        <h2 style={{ marginTop: 0 }}>Course Catalog</h2>
        <p className="muted" style={{ marginTop: 6 }}>
          Browse courses and enroll. Seat availability updates automatically.
        </p>
        {error ? <div className="error">{error}</div> : null}
      </div>

      {auth.isAuthed ? (
        <>
          <div className="col-6 card">
            <h3 style={{ marginTop: 0 }}>AI Smart Search</h3>
            <form onSubmit={smartSearch} className="grid">
              <div className="col-12">
                <input
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  placeholder='Try: "low fee courses" or "best programming course"'
                  required
                />
              </div>
              <div className="col-12">
                <button type="submit">Search</button>
              </div>
            </form>
            {searchNote ? <p className="muted">{searchNote}</p> : null}
          </div>

          <div className="col-6 card">
            <h3 style={{ marginTop: 0 }}>AI Chatbot</h3>
            <form onSubmit={sendChat} className="grid">
              <div className="col-12">
                <textarea
                  value={chatMessage}
                  onChange={(e) => setChatMessage(e.target.value)}
                  placeholder="Ask about fees, duration, seats, or how enrollment works"
                  required
                />
              </div>
              <div className="col-12">
                <button type="submit">Ask</button>
              </div>
            </form>
            {chatReply ? <div className="card" style={{ marginTop: 12 }}>{chatReply}</div> : null}
          </div>
        </>
      ) : (
        <div className="col-12 card">
          <p className="muted" style={{ margin: 0 }}>
            Login to use AI search, chatbot, and enroll.
          </p>
        </div>
      )}

      <div className="col-12 card">
        {loading ? (
          <p className="muted" style={{ margin: 0 }}>
            Loading...
          </p>
        ) : (
          <table className="table">
            <thead>
              <tr>
                <th>Course</th>
                <th>Fee</th>
                <th>Duration</th>
                <th>Seats</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {courses.map((c) => (
                <tr key={c.id}>
                  <td>
                    <strong>{c.name}</strong>
                  </td>
                  <td>₹{Number(c.fee).toFixed(2)}</td>
                  <td>{c.duration}</td>
                  <td>
                    {c.availableSeats} / {c.seats}
                  </td>
                  <td style={{ width: 160 }}>
                    {auth.isAuthed && !auth.isAdmin ? (
                      <button
                        onClick={() => enroll(c.id)}
                        disabled={c.availableSeats <= 0}
                        title={c.availableSeats <= 0 ? "No seats available" : "Enroll"}
                      >
                        Enroll
                      </button>
                    ) : null}
                  </td>
                </tr>
              ))}
              {courses.length === 0 ? (
                <tr>
                  <td colSpan={5} className="muted">
                    No courses found.
                  </td>
                </tr>
              ) : null}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}

