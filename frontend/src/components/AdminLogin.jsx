import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { storeAuthSession } from "../utils/auth";
import "../styles/LoginPage.css";

const AdminLogin = () => {
  const navigate = useNavigate();
  const [isLoaded, setIsLoaded] = useState(false);
  const [email, setEmail] = useState("admin@sidel.com");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    setIsLoaded(true);
  }, []);

  const handleAdminLogin = async (event) => {
    event.preventDefault();

    if (!email || !password) {
      setError("Please fill in admin email and password.");
      return;
    }

    setLoading(true);
    setError("");

    try {
      const response = await fetch("http://localhost:8080/api/users/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, password }),
      });

      const contentType = response.headers.get("content-type") || "";
      const data = contentType.includes("application/json")
        ? await response.json()
        : await response.text();

      if (!response.ok) {
        throw new Error(typeof data === "string" ? data : data.message || "Invalid admin credentials");
      }

      const loggedUser = data?.user || data;

      if (!loggedUser.isAdmin) {
        throw new Error("This account is not an admin account.");
      }

      storeAuthSession(data);
      navigate("/admin/dashboard");
    } catch (err) {
      setError(err.message || "Admin login failed.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={`login-container ${isLoaded ? "loaded" : ""}`}>
      <div className="login-card">
        <h1 className="login-title">Admin Login</h1>
        <p className="login-subtitle">Manage provider applications and sideL activity</p>

        <div className="divider">
          <span className="line"></span>
          <span className="or-text">Admin access</span>
          <span className="line"></span>
        </div>

        {error && <p className="error-message">{error}</p>}

        <form className="login-form" onSubmit={handleAdminLogin}>
          <input
            type="email"
            placeholder="Admin email"
            value={email}
            onChange={(event) => setEmail(event.target.value)}
          />

          <input
            type="password"
            placeholder="Admin password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
          />

          <button type="submit" disabled={loading}>
            {loading ? "Checking..." : "Login as Admin"}
          </button>
        </form>

        <p className="footer-text">
          Client or provider? <Link to="/login">Go to user login</Link>
        </p>
      </div>
    </div>
  );
};

export default AdminLogin;
