import React, { useCallback, useEffect, useMemo, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import "../styles/dashboard.css";

const API_BASE_URL = "http://localhost:8080/api";
const API_ORIGIN = API_BASE_URL.replace("/api", "");

const getApplicationImages = (imageText = "") => {
  if (!imageText) return [];

  const uploadMatches = imageText.match(/\/uploads\/gigs\/[^,\s;]+?\.(?:png|jpe?g|gif|webp)/gi);
  if (uploadMatches?.length) {
    return uploadMatches.slice(0, 5);
  }

  return imageText
    .split(/[\n,;]+/)
    .map((image) => image.trim())
    .filter(Boolean)
    .slice(0, 5);
};

const getImageSource = (image) =>
  image?.startsWith("/uploads/") ? `${API_ORIGIN}${image}` : image;

const AdminDashboard = () => {
  const navigate = useNavigate();
  const [admin, setAdmin] = useState(null);
  const [applicants, setApplicants] = useState([]);
  const [users, setUsers] = useState([]);
  const [gigs, setGigs] = useState([]);
  const [reports, setReports] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  const pendingCount = applicants.length;
  const providerCount = useMemo(
    () => users.filter((user) => user.isProvider).length,
    [users]
  );
  const clientCount = Math.max(users.length - providerCount, 0);

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

  const loadAdminDashboard = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const [pendingApplicants, allUsers, allGigs, allReports] = await Promise.all([
        request("/admin/applicants"),
        request("/users"),
        request("/gigs"),
        request("/admin/reports"),
      ]);

      setApplicants(pendingApplicants);
      setUsers(allUsers);
      setGigs(allGigs);
      setReports(allReports);
    } catch (err) {
      setError(err.message || "Could not load admin dashboard.");
    } finally {
      setLoading(false);
    }
  }, [request]);

  useEffect(() => {
    const savedUser = localStorage.getItem("loggedUser");

    if (!savedUser) {
      navigate("/admin/login");
      return;
    }

    const parsedUser = JSON.parse(savedUser);
    if (!parsedUser.isAdmin) {
      navigate("/admin/login");
      return;
    }

    setAdmin(parsedUser);
    loadAdminDashboard();
  }, [loadAdminDashboard, navigate]);

  const handleLogout = () => {
    localStorage.removeItem("loggedUser");
    navigate("/admin/login");
  };

  const updateApplicant = async (applicationId, status) => {
    try {
      setMessage("");
      setError("");
      await request(`/admin/applicants/${applicationId}/status?status=${status}`, {
        method: "PUT",
      });
      await loadAdminDashboard();
      setMessage(`Application ${status.toLowerCase()} successfully.`);
    } catch (err) {
      setError(err.message || "Unable to update applicant.");
    }
  };

  return (
    <main className="admin-shell">
      <aside className="dashboard-sidebar">
        <div>
          <p className="dashboard-kicker">sideL</p>
          <h1>Admin</h1>
        </div>

        <nav className="dashboard-nav" aria-label="Admin sections">
          <a href="#overview">Overview</a>
          <a href="#reports">Reports</a>
          <a href="#applicants">Applicants</a>
          <a href="#users">Users</a>
          <a href="#gigs">Gigs</a>
        </nav>

        <button className="secondary-action" type="button" onClick={handleLogout}>
          Logout
        </button>
      </aside>

      <section className="dashboard-content">
        <header className="admin-topbar">
          <div>
            <p className="dashboard-kicker">Admin dashboard</p>
            <h2>Welcome, {admin?.firstname || "Admin"}</h2>
          </div>
          <Link className="secondary-inline" to="/dashboard">
            View user dashboard
          </Link>
        </header>

        {loading && <p className="status-line">Loading admin dashboard...</p>}
        {error && <p className="error-banner">{error}</p>}
        {message && <p className="success-banner">{message}</p>}

        <section className="dashboard-panel" id="overview">
          <div className="section-heading">
            <div>
              <p className="dashboard-kicker">System overview</p>
              <h3>Current activity</h3>
            </div>
          </div>

          <div className="metrics-grid">
            <article>
              <strong>{pendingCount}</strong>
              <span>Pending applications</span>
            </article>
            <article>
              <strong>{providerCount}</strong>
              <span>Approved providers</span>
            </article>
            <article>
              <strong>{clientCount}</strong>
              <span>Client accounts</span>
            </article>
            <article>
              <strong>{gigs.length}</strong>
              <span>Posted gigs</span>
            </article>
            <article>
              <strong>{reports.length}</strong>
              <span>User reports</span>
            </article>
          </div>
        </section>

        <section className="dashboard-panel" id="reports">
          <div className="section-heading">
            <div>
              <p className="dashboard-kicker">Conflict reports</p>
              <h3>Client and provider reports</h3>
            </div>
            <span className="status-chip">{reports.length} reports</span>
          </div>

          <div className="data-list">
            {reports.map((report) => (
              <article className="data-row report-row" key={report.reportID}>
                <div>
                  <span className="pill">{report.reason}</span>
                  <h4>
                    {report.reporter?.firstname || "User"} reported {report.reportedUser?.firstname || "User"}
                  </h4>
                  <p>
                    Reporter: {report.reporter?.firstname} {report.reporter?.lastname} - {report.reporter?.email}
                  </p>
                  <p>
                    Reported: {report.reportedUser?.firstname} {report.reportedUser?.lastname} - {report.reportedUser?.email}
                  </p>
                  {report.booking && (
                    <p>
                      Booking: {report.booking.gig?.title || "Service"} - {report.booking.status}
                    </p>
                  )}
                  <p>{report.details}</p>
                </div>
                <span className="status-chip">{report.status}</span>
              </article>
            ))}
          </div>

          {!reports.length && !loading && (
            <p className="muted-text">No client or provider reports yet.</p>
          )}
        </section>

        <section className="dashboard-panel" id="applicants">
          <div className="section-heading">
            <div>
              <p className="dashboard-kicker">Provider review</p>
              <h3>Pending applicants</h3>
            </div>
          </div>

          <div className="data-list">
            {applicants.map((applicant) => {
              const images = getApplicationImages(applicant.image);

              return (
                <article className="data-row applicant-row" key={applicant.applicationID}>
                  <div className="applicant-details">
                    <span className="pill">{applicant.category}</span>
                    <h4>{applicant.title}</h4>
                    <p>{applicant.user?.firstname} {applicant.user?.lastname} - {applicant.email}</p>
                    <p>{applicant.address}</p>
                    <p>{applicant.description}</p>
                    {applicant.requirements && <p>Requirements: {applicant.requirements}</p>}

                    {images.length > 0 && (
                      <div className="application-images" aria-label="Provider application images">
                        {images.map((image, index) => (
                          <a
                            className="application-image-card"
                            href={getImageSource(image)}
                            key={`${applicant.applicationID}-${image}`}
                            target="_blank"
                            rel="noreferrer"
                          >
                            <img src={getImageSource(image)} alt={`${applicant.title} work ${index + 1}`} loading="lazy" />
                            <span>View image {index + 1}</span>
                          </a>
                        ))}
                      </div>
                    )}
                  </div>
                  <div className="button-group">
                    <button type="button" onClick={() => updateApplicant(applicant.applicationID, "APPROVED")}>
                      Approve
                    </button>
                    <button type="button" onClick={() => updateApplicant(applicant.applicationID, "REJECTED")}>
                      Reject
                    </button>
                  </div>
                </article>
              );
            })}
          </div>

          {!applicants.length && !loading && (
            <p className="muted-text">No pending provider applications.</p>
          )}
        </section>

        <section className="dashboard-panel" id="users">
          <div className="section-heading">
            <div>
              <p className="dashboard-kicker">Accounts</p>
              <h3>Registered users</h3>
            </div>
          </div>

          <div className="data-list">
            {users.map((user) => (
              <article className="data-row" key={user.userID}>
                <div>
                  <h4>{user.firstname} {user.lastname}</h4>
                  <p>{user.email}</p>
                </div>
                <span className="status-chip">
                  {user.isAdmin ? "ADMIN" : user.isProvider ? "PROVIDER" : "CLIENT"}
                </span>
              </article>
            ))}
          </div>
        </section>

        <section className="dashboard-panel" id="gigs">
          <div className="section-heading">
            <div>
              <p className="dashboard-kicker">Marketplace</p>
              <h3>Posted gigs</h3>
            </div>
          </div>

          <div className="data-list">
            {gigs.map((gig) => (
              <article className="data-row" key={gig.gigID}>
                <div>
                  <h4>{gig.title}</h4>
                  <p>{gig.category} - PHP {Number(gig.price || 0).toLocaleString()}</p>
                </div>
                <span className="pill">{gig.provider?.firstname || "Provider"}</span>
              </article>
            ))}
          </div>
        </section>
      </section>
    </main>
  );
};

export default AdminDashboard;
