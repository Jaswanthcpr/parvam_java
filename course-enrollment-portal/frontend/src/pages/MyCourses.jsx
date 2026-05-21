import React, { useEffect, useState } from "react";
import { apiFetch } from "../lib/api.js";

export default function MyCourses() {
  const [data, setData] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  async function load() {
    setLoading(true);
    setError("");
    try {
      const res = await apiFetch("/api/enrollments/me");
      setData(res);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
  }, []);

  async function cancel(id) {
    setError("");
    try {
      await apiFetch(`/api/enrollments/${id}`, { method: "DELETE" });
      await load();
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <div className="grid">
      <div className="col-12 card">
        <h2 style={{ marginTop: 0 }}>My Courses</h2>
        <p className="muted" style={{ marginTop: 6 }}>
          Total fee is calculated from active enrollments.
        </p>
        {error ? <div className="error">{error}</div> : null}
      </div>

      <div className="col-12 card">
        {loading ? (
          <p className="muted" style={{ margin: 0 }}>
            Loading...
          </p>
        ) : (
          <>
            <div className="row" style={{ justifyContent: "space-between", marginBottom: 10, flexWrap: "wrap" }}>
              <div className="badge">
                Active enrollments: {data?.enrollments?.length || 0}
              </div>
              <div className="badge">
                Total fee: ₹{Number(data?.totalFee || 0).toFixed(2)}
              </div>
            </div>

            <table className="table">
              <thead>
                <tr>
                  <th>Course</th>
                  <th>Fee</th>
                  <th>Duration</th>
                  <th>Enrolled At</th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {(data?.enrollments || []).map((e) => (
                  <tr key={e.id}>
                    <td>
                      <strong>{e.courseName}</strong>
                    </td>
                    <td>₹{Number(e.courseFee).toFixed(2)}</td>
                    <td>{e.courseDuration}</td>
                    <td>{new Date(e.enrolledAt).toLocaleString()}</td>
                    <td style={{ width: 170 }}>
                      <button className="danger" onClick={() => cancel(e.id)}>
                        Cancel
                      </button>
                    </td>
                  </tr>
                ))}
                {(data?.enrollments || []).length === 0 ? (
                  <tr>
                    <td colSpan={5} className="muted">
                      No active enrollments.
                    </td>
                  </tr>
                ) : null}
              </tbody>
            </table>
          </>
        )}
      </div>
    </div>
  );
}

