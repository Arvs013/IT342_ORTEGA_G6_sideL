import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import "../styles/SignupPage.css";

const SignupPage = () => {
  const navigate = useNavigate();

  const API_BASE_URL = "http://localhost:8080/api/users/register";

  const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: "",
    phoneNumber: "",
    address: "",
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [isLoaded, setIsLoaded] = useState(false);

  useEffect(() => {
    setIsLoaded(true);
  }, []);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSignup = async (e) => {
    e.preventDefault();

    if (loading) return;

    if (!formData.name || !formData.email || !formData.password) {
      setError("Please fill name, email, and password.");
      return;
    }

    setLoading(true);
    setError("");
    setSuccess("");

    try {
      const [firstname, ...rest] = formData.name.split(" ");
      const lastname = rest.join(" ");

      const payload = {
        firstname: firstname || "",
        lastname: lastname || "",
        email: formData.email,
        password: formData.password,
        phoneNumber: formData.phoneNumber,
        address: formData.address,
      };

      const response = await fetch(API_BASE_URL, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(payload),
      });

      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.message || "Signup failed");
      }

      setSuccess("Account created successfully!");

      localStorage.setItem("loggedUser", JSON.stringify(data));

      setTimeout(() => {
        navigate("/login");
      }, 1000);

    } catch (err) {
      setError(err.message || "An error occurred during signup.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={`signup-container ${isLoaded ? "loaded" : ""}`}>
      <div className="signup-card">

        <Link className="auth-logo" to="/">side<span>L</span></Link>

        <p className="auth-kicker">Create account</p>
        <h1 className="signup-title">Join sideL</h1>
        <p className="signup-subtitle">
          Book trusted services, track requests, or apply as a provider.
        </p>

        <div className="divider">
          <span className="line"></span>
          <span className="or-text">Sign up with Email</span>
          <span className="line"></span>
        </div>

        {error && <p className="error-message">{error}</p>}
        {success && <p className="success-message">{success}</p>}

        <div className="form-group">
          <input
            type="text"
            name="name"
            placeholder="Full Name"
            value={formData.name}
            onChange={handleChange}
          />

          <input
            type="email"
            name="email"
            placeholder="Email"
            value={formData.email}
            onChange={handleChange}
          />

          <input
            type="password"
            name="password"
            placeholder="Password"
            value={formData.password}
            onChange={handleChange}
          />

          <input
            type="text"
            name="phoneNumber"
            placeholder="Phone Number"
            value={formData.phoneNumber}
            onChange={handleChange}
          />

          <input
            type="text"
            name="address"
            placeholder="Address"
            value={formData.address}
            onChange={handleChange}
          />
        </div>

        <button onClick={handleSignup} disabled={loading}>
          {loading ? "Signing Up..." : "Sign Up"}
        </button>

        <p className="terms-text">
          By clicking "Sign up", you agree to our Terms, Privacy Policy and Cookies Policy.
        </p>

        <p className="footer-text">
          Already have an account? <Link to="/login">Log in</Link>
        </p>

      </div>
    </div>
  );
};

export default SignupPage;
