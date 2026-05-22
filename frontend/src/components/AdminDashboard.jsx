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

const getUserStatus = (user) => user?.accountStatus || "ACTIVE";
const getGigStatus = (gig) => gig?.status || "ACTIVE";
const formatStatus = (status = "") => status.replace(/_/g, " ");
const getStatusClass = (status = "") => {
  const normalized = status.toUpperCase();
  if (["ACTIVE", "APPROVED", "COMPLETED", "RESOLVED"].includes(normalized)) return "success-status";
  if (["DISABLED", "REJECTED", "CANCELLED"].includes(normalized)) return "danger-status";
  if (["OPEN", "PENDING"].includes(normalized)) return "warning-status";
  return "info-status";
};

const AdminDashboard = () => {
  const navigate = useNavigate();
  const [admin, setAdmin] = useState(null);
  const [applicants, setApplicants] = useState([]);
  const [users, setUsers] = useState([]);
  const [gigs, setGigs] = useState([]);
  const [reports, setReports] = useState([]);
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  const pendingCount = applicants.length;
  const providerCount = useMemo(
    () => users.filter((user) => user.isProvider).length,
    [users]
  );
  const clientCount = Math.max(users.length - providerCount, 0);
  const openReportCount = reports.filter((report) => report.status === "OPEN").length;

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
      const [pendingApplicants, allUsers, allGigs, allReports, allBookings] = await Promise.all([
        request("/admin/applicants"),
        request("/users"),
        request("/admin/gigs"),
        request("/admin/reports"),
        request("/admin/bookings"),
      ]);

      setApplicants(pendingApplicants);
      setUsers(allUsers);
      setGigs(allGigs);
      setReports(allReports);
      setBookings(allBookings);
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

  const updateReportStatus = async (reportId, status) => {
    try {
      setMessage("");
      setError("");
      await request(`/admin/reports/${reportId}/status?status=${status}`, { method: "PUT" });
      await loadAdminDashboard();
      setMessage(`Report marked as ${status.toLowerCase()}.`);
    } catch (err) {
      setError(err.message || "Unable to update report.");
    }
  };

  const updateUserStatus = async (userId, status) => {
    try {
      setMessage("");
      setError("");
      await request(`/admin/users/${userId}/status?status=${status}`, { method: "PUT" });
      await loadAdminDashboard();
      setMessage(`User marked as ${status.toLowerCase()}.`);
    } catch (err) {
      setError(err.message || "Unable to update user.");
    }
  };

  const updateGigStatus = async (gigId, status) => {
    try {
      setMessage("");
      setError("");
      await request(`/admin/gigs/${gigId}/status?status=${status}`, { method: "PUT" });
      await loadAdminDashboard();
      setMessage(`Gig marked as ${status.toLowerCase()}.`);
    } catch (err) {
      setError(err.message || "Unable to update gig.");
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
          <a href="#bookings">Bookings</a>
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
            <article>
              <strong>{bookings.length}</strong>
              <span>Total bookings</span>
            </article>
          </div>
        </section>

        <section className="dashboard-panel admin-section" id="reports">
          <div className="section-heading">
            <div>
              <p className="dashboard-kicker">Conflict reports</p>
              <h3>Client and provider reports</h3>
            </div>
            <span className="status-chip">{openReportCount} open</span>
          </div>

          <div className="data-list">
            {reports.map((report) => (
              <article className="data-row report-row" key={report.reportID}>
                <div className="admin-row-main">
                  <div className="inline-pills">
                    <span className="pill">{report.reason}</span>
                    <span className={`status-chip ${getStatusClass(report.status)}`}>{report.status}</span>
                  </div>
                  <h4>{report.reporter?.firstname || "User"} reported {report.reportedUser?.firstname || "User"}</h4>
                  <div className="admin-detail-grid">
                    <p><span>Reporter</span><strong>{report.reporter?.firstname} {report.reporter?.lastname}</strong></p>
                    <p><span>Reporter email</span><strong>{report.reporter?.email || "No email"}</strong></p>
                    <p><span>Reported</span><strong>{report.reportedUser?.firstname} {report.reportedUser?.lastname}</strong></p>
                    <p><span>Reported email</span><strong>{report.reportedUser?.email || "No email"}</strong></p>
                  </div>
                  {report.booking && (
                    <p className="admin-note">Booking: {report.booking.gig?.title || "Service"} - {formatStatus(report.booking.status)}</p>
                  )}
                  <p className="admin-note">{report.details}</p>
                </div>
                <div className="button-group admin-row-actions">
                  {["OPEN", "REVIEWED", "RESOLVED"].map((status) => (
                    <button
                      disabled={report.status === status}
                      key={status}
                      type="button"
                      onClick={() => updateReportStatus(report.reportID, status)}
                    >
                      {status}
                    </button>
                  ))}
                </div>
              </article>
            ))}
          </div>

          {!reports.length && !loading && (
            <div className="dashboard-empty-state admin-empty-state">
              <h4>No reports yet</h4>
              <p>Client and provider conflict reports will appear here for review.</p>
            </div>
          )}
        </section>

        <section className="dashboard-panel admin-section" id="bookings">
          <div className="section-heading">
            <div>
              <p className="dashboard-kicker">Booking monitor</p>
              <h3>All service bookings</h3>
            </div>
            <span className="status-chip">{bookings.length} bookings</span>
          </div>

          <div className="data-list">
            {bookings.map((booking) => (
              <article className="data-row admin-booking-row" key={booking.bookingID}>
                <div className="admin-row-main">
                  <div className="inline-pills">
                    <span className={`status-chip ${getStatusClass(booking.status)}`}>{formatStatus(booking.status)}</span>
                    <span className="pill">{booking.gig?.category || "Service"}</span>
                  </div>
                  <h4>{booking.gig?.title || "Booked service"}</h4>
                  <div className="admin-detail-grid">
                    <p><span>Client</span><strong>{booking.client?.firstname} {booking.client?.lastname}</strong></p>
                    <p><span>Provider</span><strong>{booking.gig?.provider?.firstname || "Provider"} {booking.gig?.provider?.lastname || ""}</strong></p>
                    <p><span>Schedule</span><strong>{booking.bookingDate ? new Date(booking.bookingDate).toLocaleString() : "No schedule"}</strong></p>
                    <p><span>Address</span><strong>{booking.serviceAddress || "No service address"}</strong></p>
                  </div>
                  {booking.receiptUrl && (
                    <a className="application-link" href={getImageSource(booking.receiptUrl)} target="_blank" rel="noreferrer">
                      View receipt
                    </a>
                  )}
                </div>
              </article>
            ))}
          </div>

          {!bookings.length && !loading && (
            <div className="dashboard-empty-state admin-empty-state">
              <h4>No bookings yet</h4>
              <p>Service bookings from clients will appear here once requests are created.</p>
            </div>
          )}
        </section>

        <section className="dashboard-panel admin-section" id="applicants">
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
            <div className="dashboard-empty-state admin-empty-state">
              <h4>No pending applications</h4>
              <p>Provider applications waiting for approval will appear here.</p>
            </div>
          )}
        </section>

        <section className="dashboard-panel admin-section" id="users">
          <div className="section-heading">
            <div>
              <p className="dashboard-kicker">Accounts</p>
              <h3>Registered users</h3>
            </div>
          </div>

          <div className="data-list">
            {users.map((user) => (
              <article className="data-row" key={user.userID}>
                <div className="admin-row-main">
                  <h4>{user.firstname} {user.lastname}</h4>
                  <div className="admin-detail-grid">
                    <p><span>Email</span><strong>{user.email}</strong></p>
                    <p><span>Phone</span><strong>{user.phoneNumber || "No phone"}</strong></p>
                    <p><span>Address</span><strong>{user.address || "No address"}</strong></p>
                    <p><span>Provider status</span><strong>{user.providerStatus || "NONE"}</strong></p>
                  </div>
                </div>
                <div className="button-group admin-row-actions">
                  <span className="status-chip">
                    {user.isAdmin ? "ADMIN" : user.isProvider ? "PROVIDER" : "CLIENT"}
                  </span>
                  <span className={`status-chip ${getStatusClass(getUserStatus(user))}`}>
                    {getUserStatus(user)}
                  </span>
                  {!user.isAdmin && (
                    <button
                      className={getUserStatus(user) === "DISABLED" ? "" : "danger-inline"}
                      type="button"
                      onClick={() => updateUserStatus(user.userID, getUserStatus(user) === "DISABLED" ? "ACTIVE" : "DISABLED")}
                    >
                      {getUserStatus(user) === "DISABLED" ? "Enable" : "Disable"}
                    </button>
                  )}
                </div>
              </article>
            ))}
          </div>

          {!users.length && !loading && (
            <div className="dashboard-empty-state admin-empty-state">
              <h4>No users yet</h4>
              <p>Registered client and provider accounts will appear here.</p>
            </div>
          )}
        </section>

        <section className="dashboard-panel admin-section" id="gigs">
          <div className="section-heading">
            <div>
              <p className="dashboard-kicker">Marketplace</p>
              <h3>Posted gigs</h3>
            </div>
          </div>

          <div className="data-list">
            {gigs.map((gig) => (
              <article className="data-row" key={gig.gigID}>
                <div className="admin-row-main">
                  <h4>{gig.title}</h4>
                  <div className="admin-detail-grid">
                    <p><span>Category</span><strong>{gig.category}</strong></p>
                    <p><span>Price</span><strong>PHP {Number(gig.price || 0).toLocaleString()}</strong></p>
                    <p><span>Provider</span><strong>{gig.provider?.firstname || "Provider"} {gig.provider?.lastname || ""}</strong></p>
                    <p><span>Likes</span><strong>{gig.likeCount || 0}</strong></p>
                  </div>
                </div>
                <div className="button-group admin-row-actions">
                  <span className={`status-chip ${getStatusClass(getGigStatus(gig))}`}>
                    {getGigStatus(gig)}
                  </span>
                  <button
                    className={getGigStatus(gig) === "DISABLED" ? "" : "danger-inline"}
                    type="button"
                    onClick={() => updateGigStatus(gig.gigID, getGigStatus(gig) === "DISABLED" ? "ACTIVE" : "DISABLED")}
                  >
                    {getGigStatus(gig) === "DISABLED" ? "Enable" : "Disable"}
                  </button>
                </div>
              </article>
            ))}
          </div>

          {!gigs.length && !loading && (
            <div className="dashboard-empty-state admin-empty-state">
              <h4>No gigs yet</h4>
              <p>Provider services will appear here once approved providers post gigs.</p>
            </div>
          )}
        </section>
      </section>
    </main>
  );
};

export default AdminDashboard;
