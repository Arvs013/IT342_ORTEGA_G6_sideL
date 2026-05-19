import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
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

      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.message || "Invalid email or password");
      }

      localStorage.setItem("loggedUser", JSON.stringify(data));

      navigate(data.isAdmin ? "/admin/dashboard" : "/dashboard");

    } catch (err) {
      setError(err.message || "Login failed. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={`login-container ${isLoaded ? "loaded" : ""}`}>
      <div className="login-card">

        <h1 className="login-title">Get Started</h1>
        <p className="login-subtitle">
          Welcome to sideL. Let’s continue your session
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
          By clicking Sign in, you agree to our Terms, Privacy Policy and Cookies Policy.
        </p>

        <p className="footer-text">
          Don’t have an account? <Link to="/signup">Sign up</Link>
        </p>

      </div>
    </div>
  );
};

export default LoginPage;
