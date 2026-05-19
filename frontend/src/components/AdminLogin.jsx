import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
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

      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.message || "Invalid admin credentials");
      }

      if (!data.isAdmin) {
        throw new Error("This account is not an admin account.");
      }

      localStorage.setItem("loggedUser", JSON.stringify(data));
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
