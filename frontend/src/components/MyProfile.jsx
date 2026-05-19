import React, { useCallback, useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import "../styles/dashboard.css";

const API_BASE_URL = "http://localhost:8080/api";

const emptyProfile = {
  firstname: "",
  lastname: "",
  email: "",
  phoneNumber: "",
  address: "",
  bio: "",
};

const MyProfile = () => {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [profileForm, setProfileForm] = useState(emptyProfile);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  const request = useCallback(async (path, options = {}) => {
    const response = await fetch(`${API_BASE_URL}${path}`, {
      ...options,
      headers: {
        "Content-Type": "application/json",
        ...(options.headers || {}),
      },
    });

    const contentType = response.headers.get("content-type") || "";
    const data = contentType.includes("application/json")
      ? await response.json()
      : await response.text();

    if (!response.ok) {
      throw new Error(typeof data === "string" ? data : data.message || "Request failed");
    }

    return data;
  }, []);

  useEffect(() => {
    const savedUser = localStorage.getItem("loggedUser");
    if (!savedUser) {
      navigate("/login");
      return;
    }

    const parsedUser = JSON.parse(savedUser);

    const loadProfile = async () => {
      try {
        const freshUser = await request(`/users/${parsedUser.userID}`);
        setUser(freshUser);
        setProfileForm({
          firstname: freshUser.firstname || "",
          lastname: freshUser.lastname || "",
          email: freshUser.email || "",
          phoneNumber: freshUser.phoneNumber || "",
          address: freshUser.address || "",
          bio: freshUser.bio || "",
        });
        localStorage.setItem("loggedUser", JSON.stringify(freshUser));
      } catch (err) {
        setError(err.message || "Could not load profile.");
      } finally {
        setLoading(false);
      }
    };

    loadProfile();
  }, [navigate, request]);

  const updateProfile = async (event) => {
    event.preventDefault();
    if (!user?.userID) return;

    if (!profileForm.firstname || !profileForm.lastname || !profileForm.email) {
      setError("Please keep your first name, last name, and email filled in.");
      return;
    }

    setSaving(true);
    setError("");
    setMessage("");

    try {
      const updatedUser = await request(`/users/${user.userID}`, {
        method: "PUT",
        body: JSON.stringify(profileForm),
      });

      setUser(updatedUser);
      localStorage.setItem("loggedUser", JSON.stringify(updatedUser));
      setMessage("Profile updated successfully.");
    } catch (err) {
      setError(err.message || "Unable to update profile.");
    } finally {
      setSaving(false);
    }
  };

  const isProvider = Boolean(user?.isProvider);

  return (
    <main className="profile-page">
      <header className="profile-page-header">
        <Link className="brand-mark" to="/dashboard" aria-label="Go to dashboard">
          sideL
        </Link>
        <Link className="secondary-inline" to="/dashboard">
          Back to dashboard
        </Link>
      </header>

      <section className="dashboard-panel profile-page-card">
        <div className="section-heading">
          <div>
            <p className="dashboard-kicker">My Profile</p>
            <h1>{isProvider ? "Provider details" : "Client details"}</h1>
          </div>
          <span className="status-chip">{user?.providerStatus || "CLIENT"}</span>
        </div>

        {loading && <p className="status-line">Loading profile...</p>}
        {error && <p className="error-banner">{error}</p>}
        {message && <p className="success-banner">{message}</p>}

        {!loading && (
          <>
            <div className="profile-shortcuts">
              <Link className="primary-action" to="/bookings">
                My bookings
              </Link>
            </div>

            <form className="profile-edit-form" onSubmit={updateProfile}>
              <label>
                First name
                <input
                  type="text"
                  value={profileForm.firstname}
                  onChange={(event) => setProfileForm({ ...profileForm, firstname: event.target.value })}
                />
              </label>

              <label>
                Last name
                <input
                  type="text"
                  value={profileForm.lastname}
                  onChange={(event) => setProfileForm({ ...profileForm, lastname: event.target.value })}
                />
              </label>

              <label>
                Email
                <input
                  type="email"
                  value={profileForm.email}
                  onChange={(event) => setProfileForm({ ...profileForm, email: event.target.value })}
                />
              </label>

              <label>
                Phone number
                <input
                  type="text"
                  value={profileForm.phoneNumber}
                  onChange={(event) => setProfileForm({ ...profileForm, phoneNumber: event.target.value })}
                />
              </label>

              <label className="wide-field">
                Address
                <input
                  type="text"
                  value={profileForm.address}
                  onChange={(event) => setProfileForm({ ...profileForm, address: event.target.value })}
                />
              </label>

              <label className="wide-field">
                {isProvider ? "Provider description" : "About me"}
                <textarea
                  placeholder={isProvider ? "Describe your skills, service area, and experience." : "Add a short profile note."}
                  value={profileForm.bio}
                  onChange={(event) => setProfileForm({ ...profileForm, bio: event.target.value })}
                />
              </label>

              <button className="primary-action" type="submit" disabled={saving}>
                {saving ? "Saving..." : "Save profile"}
              </button>
            </form>
          </>
        )}
      </section>
    </main>
  );
};

export default MyProfile;
