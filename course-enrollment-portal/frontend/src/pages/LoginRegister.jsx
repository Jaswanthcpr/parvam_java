import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { apiFetch, setRole, setToken } from "../lib/api.js";

export default function LoginRegister() {
  const navigate = useNavigate();
  const [reg, setReg] = useState({ name: "", email: "", phone: "" });
  const [identifier, setIdentifier] = useState("");
  const [channel, setChannel] = useState("EMAIL");
  const [otp, setOtp] = useState("");
  const [status, setStatus] = useState({ type: "", message: "" });
  const [otpRequested, setOtpRequested] = useState(false);

  async function doRegister(e) {
    e.preventDefault();
    setStatus({ type: "", message: "" });
    try {
      const res = await apiFetch("/api/auth/register", {
        method: "POST",
        body: JSON.stringify(reg)
      });
      setStatus({ type: "success", message: `Registered. Student ID: ${res.studentId}` });
    } catch (err) {
      setStatus({ type: "error", message: err.message });
    }
  }

  async function requestOtp(e) {
    e.preventDefault();
    setStatus({ type: "", message: "" });
    try {
      await apiFetch("/api/auth/request-otp", {
        method: "POST",
        body: JSON.stringify({ identifier, channel })
      });
      setOtpRequested(true);
      setStatus({ type: "success", message: "OTP sent. Enter it to login." });
    } catch (err) {
      setStatus({ type: "error", message: err.message });
    }
  }

  async function verifyOtp(e) {
    e.preventDefault();
    setStatus({ type: "", message: "" });
    try {
      const res = await apiFetch("/api/auth/verify-otp", {
        method: "POST",
        body: JSON.stringify({ identifier, otp })
      });
      setToken(res.token);
      setRole(res.role);
      navigate("/courses");
    } catch (err) {
      setStatus({ type: "error", message: err.message });
    }
  }

  return (
    <div className="grid">
      <div className="col-6 card">
        <h2 style={{ marginTop: 0 }}>Student Register</h2>
        <form onSubmit={doRegister} className="grid">
          <div className="col-12">
            <label className="muted">Name</label>
            <input value={reg.name} onChange={(e) => setReg({ ...reg, name: e.target.value })} required />
          </div>
          <div className="col-12">
            <label className="muted">Email</label>
            <input value={reg.email} onChange={(e) => setReg({ ...reg, email: e.target.value })} />
          </div>
          <div className="col-12">
            <label className="muted">Phone</label>
            <input value={reg.phone} onChange={(e) => setReg({ ...reg, phone: e.target.value })} />
          </div>
          <div className="col-12">
            <button type="submit">Register</button>
          </div>
        </form>
      </div>

      <div className="col-6 card">
        <h2 style={{ marginTop: 0 }}>Login with OTP</h2>
        <form onSubmit={requestOtp} className="grid">
          <div className="col-12">
            <label className="muted">Email or Phone</label>
            <input value={identifier} onChange={(e) => setIdentifier(e.target.value)} required />
          </div>
          <div className="col-12">
            <label className="muted">Channel</label>
            <select value={channel} onChange={(e) => setChannel(e.target.value)}>
              <option value="EMAIL">Email</option>
              <option value="SMS">SMS</option>
              <option value="EMAIL_SMS">Email + SMS</option>
            </select>
          </div>
          <div className="col-12">
            <button type="submit">Request OTP</button>
          </div>
        </form>

        {otpRequested ? (
          <form onSubmit={verifyOtp} className="grid" style={{ marginTop: 14 }}>
            <div className="col-12">
              <label className="muted">OTP</label>
              <input value={otp} onChange={(e) => setOtp(e.target.value)} required />
            </div>
            <div className="col-12">
              <button type="submit">Verify & Login</button>
            </div>
          </form>
        ) : null}

        {status.message ? (
          <div className={status.type === "error" ? "error" : "success"} style={{ marginTop: 14 }}>
            {status.message}
          </div>
        ) : null}
      </div>
    </div>
  );
}

