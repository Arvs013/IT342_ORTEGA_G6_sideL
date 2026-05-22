import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { storeAuthSession } from "../utils/auth";
import "../styles/LoginPage.css";

const LoginPage = () => {
  const [isLoaded, setIsLoaded] = useState(false);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const navigate = useNavigate();

  useEffect(() => {
    setIsLoaded(true);
  }, []);

  const handleLogin = async (e) => {
    e.preventDefault();

    if (!email || !password) {
      setError("Please fill in both email and password");
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
        throw new Error(typeof data === "string" ? data : data.message || "Invalid email or password");
      }

      const loggedUser = storeAuthSession(data);
      navigate(loggedUser.isAdmin ? "/admin/dashboard" : "/dashboard");
    } catch (err) {
      setError(err.message || "Login failed. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className={`login-container ${isLoaded ? "loaded" : ""}`}>
      <section className="login-card">
        <Link className="auth-logo" to="/">
          side<span>L</span>
        </Link>

        <p className="auth-kicker">Welcome back</p>
        <h1 className="login-title">Sign in to sideL</h1>
        <p className="login-subtitle">
          Continue to your dashboard, bookings, provider tools, and saved service requests.
        </p>

        <div className="divider">
          <span className="line"></span>
          <span className="or-text">Login with Email</span>
          <span className="line"></span>
        </div>

        {error && <p className="error-message">{error}</p>}

        <form className="login-form" onSubmit={handleLogin}>
          <input
            type="email"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />

          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />

          <button type="submit" disabled={loading}>
            {loading ? "Logging in..." : "Login"}
          </button>
        </form>

        <p className="terms-text">
          By clicking Login, you agree to our Terms, Privacy Policy and Cookies Policy.
        </p>

        <p className="footer-text">
          Don't have an account? <Link to="/signup">Sign up</Link>
        </p>
      </section>
    </main>
  );
};

export default LoginPage;
