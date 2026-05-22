import React, { useCallback, useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { clearAuthSession, getAuthHeaders } from "../utils/auth";
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

const valueOrEmpty = (value) => value || "Not added yet";

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
        ...getAuthHeaders(),
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
    const savedToken = localStorage.getItem("authToken");
    if (!savedUser || !savedToken) {
      clearAuthSession();
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
  const fullName = `${user?.firstname || ""} ${user?.lastname || ""}`.trim() || "sideL user";
  const initials = `${user?.firstname?.[0] || ""}${user?.lastname?.[0] || ""}`.toUpperCase() || "SL";
  const accountStatus = user?.accountStatus || "ACTIVE";
  const providerStatus = user?.providerStatus || (isProvider ? "APPROVED" : "CLIENT");
  const roleLabel = user?.isAdmin ? "Admin" : isProvider ? "Provider" : "Client";

  return (
    <main className="profile-page">
      <header className="profile-page-header">
        <Link className="brand-mark" to="/dashboard" aria-label="Go to dashboard">
          sideL
        </Link>
        <div className="profile-header-actions">
          <Link className="secondary-inline" to="/bookings">
            My bookings
          </Link>
          <Link className="primary-action" to="/dashboard">
            Dashboard
          </Link>
        </div>
      </header>

      <section className="dashboard-panel profile-page-card">
        {loading && <p className="status-line">Loading profile...</p>}
        {error && <p className="error-banner">{error}</p>}
        {message && <p className="success-banner">{message}</p>}

        {!loading && (
          <>
            <div className="my-profile-hero">
              <div className="my-profile-avatar" aria-hidden="true">
                {initials}
              </div>
              <div className="my-profile-title">
                <p className="dashboard-kicker">My Profile</p>
                <h1>{fullName}</h1>
                <p>{isProvider ? "Manage your provider details and contact information." : "Manage your client profile and booking contact details."}</p>
              </div>
              <div className="my-profile-status">
                <span className={`status-chip ${accountStatus === "ACTIVE" ? "success-status" : "danger-status"}`}>
                  {accountStatus}
                </span>
                <span className={`status-chip ${isProvider ? "success-status" : "info-status"}`}>
                  {roleLabel}
                </span>
              </div>
            </div>

            <div className="profile-quick-grid">
              <article>
                <span>Email</span>
                <strong>{valueOrEmpty(user?.email)}</strong>
              </article>
              <article>
                <span>Phone</span>
                <strong>{valueOrEmpty(user?.phoneNumber)}</strong>
              </article>
              <article>
                <span>{isProvider ? "Provider status" : "Account role"}</span>
                <strong>{providerStatus}</strong>
              </article>
            </div>

            <div className="my-profile-layout">
              <aside className="profile-info-card">
                <div>
                  <p className="dashboard-kicker">Profile Snapshot</p>
                  <h2>{isProvider ? "Provider profile" : "Client profile"}</h2>
                </div>

                <div className="profile-detail-list">
                  <p>
                    <span>Full name</span>
                    <strong>{fullName}</strong>
                  </p>
                  <p>
                    <span>Address</span>
                    <strong>{valueOrEmpty(user?.address)}</strong>
                  </p>
                  <p>
                    <span>{isProvider ? "Provider description" : "About"}</span>
                    <strong>{valueOrEmpty(user?.bio)}</strong>
                  </p>
                </div>

                <div className="profile-shortcuts">
                  <Link className="secondary-inline" to="/bookings">
                    Booking history
                  </Link>
                  <Link className="secondary-inline" to="/dashboard">
                    Browse gigs
                  </Link>
                  {isProvider && (
                    <Link className="secondary-inline" to="/dashboard">
                      Manage provider tools
                    </Link>
                  )}
                </div>
              </aside>

              <form className="profile-edit-form profile-edit-card" onSubmit={updateProfile}>
                <div className="profile-form-heading">
                  <p className="dashboard-kicker">Edit Details</p>
                  <h2>Contact and profile information</h2>
                </div>

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

                <div className="form-actions">
                  <button className="primary-action" type="submit" disabled={saving}>
                    {saving ? "Saving..." : "Save profile"}
                  </button>
                </div>
              </form>
            </div>
          </>
        )}
      </section>
    </main>
  );
};

export default MyProfile;
