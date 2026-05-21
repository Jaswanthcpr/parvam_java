import { useEffect, useState } from "react";
import { getRole, getToken } from "./api.js";

export function useAuth() {
  const [token, setToken] = useState(getToken());
  const [role, setRole] = useState(getRole());

  useEffect(() => {
    function onStorage() {
      setToken(getToken());
      setRole(getRole());
    }
    window.addEventListener("storage", onStorage);
    window.addEventListener("authchange", onStorage);
    return () => {
      window.removeEventListener("storage", onStorage);
      window.removeEventListener("authchange", onStorage);
    };
  }, []);

  return { token, role, isAuthed: Boolean(token), isAdmin: role === "ADMIN" };
}
