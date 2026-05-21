import React, { useEffect, useState } from "react";
import { apiFetch } from "../lib/api.js";

const emptyForm = { id: null, name: "", fee: "", duration: "", seats: "" };

export default function AdminDashboard() {
  const [courses, setCourses] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [error, setError] = useState("");
  const [status, setStatus] = useState("");
  const [insights, setInsights] = useState("");
  const [prediction, setPrediction] = useState(null);

  async function loadCourses() {
    setError("");
    try {
      const data = await apiFetch("/api/admin/courses");
      setCourses(data);
    } catch (err) {
      setError(err.message);
    }
  }

  useEffect(() => {
    loadCourses();
  }, []);

  async function saveCourse(e) {
    e.preventDefault();
    setError("");
    setStatus("");
    try {
      const payload = {
        name: form.name,
        fee: Number(form.fee),
        duration: form.duration,
        seats: Number(form.seats)
      };
      if (form.id) {
        await apiFetch(`/api/admin/courses/${form.id}`, { method: "PUT", body: JSON.stringify(payload) });
        setStatus("Course updated");
      } else {
        await apiFetch("/api/admin/courses", { method: "POST", body: JSON.stringify(payload) });
        setStatus("Course created");
      }
      setForm(emptyForm);
      await loadCourses();
    } catch (err) {
      setError(err.message);
    }
  }

  async function removeCourse(id) {
    setError("");
    setStatus("");
    try {
      await apiFetch(`/api/admin/courses/${id}`, { method: "DELETE" });
      setStatus("Course deleted");
      await loadCourses();
    } catch (err) {
      setError(err.message);
    }
  }

  async function loadInsights() {
    setError("");
    setInsights("");
    try {
      const res = await apiFetch("/api/ai/analytics");
      setInsights(res.insights || "");
    } catch (err) {
      setError(err.message);
    }
  }

  async function loadPrediction() {
    setError("");
    setPrediction(null);
    try {
      const res = await apiFetch("/api/ai/prediction");
      setPrediction(res);
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <div className="grid">
      <div className="col-12 card">
        <h2 style={{ marginTop: 0 }}>Admin Dashboard</h2>
        <p className="muted" style={{ marginTop: 6 }}>
          Manage courses and view AI insights.
        </p>
        {error ? <div className="error">{error}</div> : null}
        {status ? <div className="success">{status}</div> : null}
      </div>

      <div className="col-6 card">
        <h3 style={{ marginTop: 0 }}>{form.id ? "Edit Course" : "Create Course"}</h3>
        <form onSubmit={saveCourse} className="grid">
          <div className="col-12">
            <label className="muted">Name</label>
            <input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required />
          </div>
          <div className="col-12">
            <label className="muted">Fee</label>
            <input
              value={form.fee}
              onChange={(e) => setForm({ ...form, fee: e.target.value })}
              type="number"
              step="0.01"
              required
            />
          </div>
          <div className="col-12">
            <label className="muted">Duration</label>
            <input value={form.duration} onChange={(e) => setForm({ ...form, duration: e.target.value })} required />
          </div>
          <div className="col-12">
            <label className="muted">Seats</label>
            <input
              value={form.seats}
              onChange={(e) => setForm({ ...form, seats: e.target.value })}
              type="number"
              required
            />
          </div>
          <div className="col-12 row">
            <button type="submit">{form.id ? "Update" : "Create"}</button>
            {form.id ? (
              <button type="button" className="secondary" onClick={() => setForm(emptyForm)}>
                Cancel
              </button>
            ) : null}
          </div>
        </form>
      </div>

      <div className="col-6 card">
        <h3 style={{ marginTop: 0 }}>AI Analytics</h3>
        <div className="row" style={{ flexWrap: "wrap" }}>
          <button type="button" onClick={loadInsights} style={{ width: "auto" }}>
            Generate Insights
          </button>
          <button type="button" onClick={loadPrediction} className="secondary" style={{ width: "auto" }}>
            Predict Fill-Soon
          </button>
        </div>
        {insights ? <div className="card" style={{ marginTop: 12 }}>{insights}</div> : null}
        {prediction ? (
          <div className="card" style={{ marginTop: 12 }}>
            {prediction.note ? <p className="muted">{prediction.note}</p> : null}
            <strong>Likely to fill soon:</strong>
            <ul style={{ margin: "10px 0 0", paddingLeft: 18 }}>
              {(prediction.courses || []).map((c) => (
                <li key={c.id}>
                  {c.name} ({c.availableSeats}/{c.seats} available)
                </li>
              ))}
              {(prediction.courses || []).length === 0 ? <li className="muted">No results</li> : null}
            </ul>
          </div>
        ) : null}
      </div>

      <div className="col-12 card">
        <h3 style={{ marginTop: 0 }}>Courses</h3>
        <table className="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Fee</th>
              <th>Duration</th>
              <th>Seats</th>
              <th>Available</th>
              <th />
            </tr>
          </thead>
          <tbody>
            {courses.map((c) => (
              <tr key={c.id}>
                <td>{c.id}</td>
                <td>
                  <strong>{c.name}</strong>
                </td>
                <td>₹{Number(c.fee).toFixed(2)}</td>
                <td>{c.duration}</td>
                <td>{c.seats}</td>
                <td>{c.availableSeats}</td>
                <td style={{ width: 220 }}>
                  <div className="row">
                    <button
                      type="button"
                      className="secondary"
                      onClick={() =>
                        setForm({
                          id: c.id,
                          name: c.name,
                          fee: c.fee,
                          duration: c.duration,
                          seats: c.seats
                        })
                      }
                      style={{ width: "auto" }}
                    >
                      Edit
                    </button>
                    <button
                      type="button"
                      className="danger"
                      onClick={() => removeCourse(c.id)}
                      style={{ width: "auto" }}
                    >
                      Delete
                    </button>
                  </div>
                </td>
              </tr>
            ))}
            {courses.length === 0 ? (
              <tr>
                <td colSpan={7} className="muted">
                  No courses.
                </td>
              </tr>
            ) : null}
          </tbody>
        </table>
      </div>
    </div>
  );
}

