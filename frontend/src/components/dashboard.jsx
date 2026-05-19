import React, { useCallback, useEffect, useMemo, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import DashboardHeader from "./DashboardHeader";
import "../styles/dashboard.css";

const API_BASE_URL = "http://localhost:8080/api";

const categories = [
  "ALL",
  "PLUMBING",
  "ELECTRONICS",
  "APPLIANCES",
  "GADGETS",
  "MOTORCYCLE",
  "CAR",
];

const bookingStatuses = ["ACCEPTED", "REJECTED", "COMPLETED", "CANCELLED"];

const emptyProviderForm = {
  title: "",
  description: "",
  image: "",
  category: "PLUMBING",
  address: "",
  email: "",
  requirements: "",
};

const Dashboard = () => {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [gigs, setGigs] = useState([]);
  const [myGigs, setMyGigs] = useState([]);
  const [myBookings, setMyBookings] = useState([]);
  const [providerJobs, setProviderJobs] = useState([]);
  const [applicants, setApplicants] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState("ALL");
  const [searchTerm, setSearchTerm] = useState("");
  const [bookingDate, setBookingDate] = useState("");
  const [activeMessage, setActiveMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);
  const [showProviderForm, setShowProviderForm] = useState(false);
  const [providerForm, setProviderForm] = useState(emptyProviderForm);
  const [gigForm, setGigForm] = useState({
    title: "",
    description: "",
    price: "",
    category: "PLUMBING",
  });

  const isProvider = Boolean(user?.isProvider);
  const isAdmin = Boolean(user?.isAdmin);

  const visibleGigs = useMemo(() => {
    const normalizedSearch = searchTerm.trim().toLowerCase();

    return gigs.filter((gig) => {
      const matchesCategory = selectedCategory === "ALL" || gig.category === selectedCategory;
      const searchableText = [
        gig.title,
        gig.description,
        gig.category,
        gig.provider?.firstname,
        gig.provider?.lastname,
      ]
        .filter(Boolean)
        .join(" ")
        .toLowerCase();

      return matchesCategory && (!normalizedSearch || searchableText.includes(normalizedSearch));
    });
  }, [gigs, selectedCategory, searchTerm]);

  const providerStatusLabel = user?.providerStatus || "NONE";

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

  const refreshUser = useCallback(async (userId) => {
    const freshUser = await request(`/users/${userId}`);
    setUser(freshUser);
    localStorage.setItem("loggedUser", JSON.stringify(freshUser));
    return freshUser;
  }, [request]);

  const loadDashboard = useCallback(async (currentUser) => {
    if (!currentUser?.userID) return;

    setLoading(true);
    setError("");

    try {
      const freshUser = await refreshUser(currentUser.userID);
      const allGigs = await request("/gigs");
      const clientBookings = await request(`/bookings/client/${freshUser.userID}`);

      setGigs(allGigs);
      setMyBookings(clientBookings);

      if (freshUser.isProvider) {
        const [postedGigs, jobs] = await Promise.all([
          request(`/gigs/provider/${freshUser.userID}`),
          request(`/bookings/provider/${freshUser.userID}`),
        ]);
        setMyGigs(postedGigs);
        setProviderJobs(jobs);
      } else {
        setMyGigs([]);
        setProviderJobs([]);
      }

      if (freshUser.isAdmin) {
        const pendingApplicants = await request("/admin/applicants");
        setApplicants(pendingApplicants);
      } else {
        setApplicants([]);
      }
    } catch (err) {
      setError(err.message || "Could not load dashboard.");
    } finally {
      setLoading(false);
    }
  }, [refreshUser, request]);

  useEffect(() => {
    const savedUser = localStorage.getItem("loggedUser");
    if (!savedUser) {
      setLoading(false);
      return;
    }

    const parsedUser = JSON.parse(savedUser);
    setUser(parsedUser);
    loadDashboard(parsedUser);
  }, [loadDashboard]);

  const handleLogout = () => {
    localStorage.removeItem("loggedUser");
    navigate("/login");
  };

  const applyAsProvider = async (event) => {
    event.preventDefault();

    if (!user?.userID) return;

    if (
      !providerForm.title ||
      !providerForm.description ||
      !providerForm.category ||
      !providerForm.address ||
      !providerForm.email
    ) {
      setError("Please complete the required provider application fields.");
      return;
    }

    try {
      setActiveMessage("");
      setError("");
      await request(`/gigs/apply/${user.userID}`, {
        method: "POST",
        body: JSON.stringify(providerForm),
      });
      await refreshUser(user.userID);
      setProviderForm(emptyProviderForm);
      setShowProviderForm(false);
      setActiveMessage("Provider application submitted. Please wait for admin approval.");
    } catch (err) {
      setError(err.message || "Unable to submit provider application.");
    }
  };

  const createGig = async (event) => {
    event.preventDefault();

    if (!gigForm.title || !gigForm.price || !gigForm.category) {
      setError("Please add a title, price, and category for your gig.");
      return;
    }

    try {
      setActiveMessage("");
      setError("");
      await request(`/gigs/create/${user.userID}`, {
        method: "POST",
        body: JSON.stringify({
          title: gigForm.title,
          description: gigForm.description,
          price: Number(gigForm.price),
          category: gigForm.category,
        }),
      });

      setGigForm({
        title: "",
        description: "",
        price: "",
        category: "PLUMBING",
      });
      await loadDashboard(user);
      setActiveMessage("Gig posted successfully.");
    } catch (err) {
      setError(err.message || "Unable to create gig.");
    }
  };

  const bookGig = async (gig) => {
    if (!bookingDate) {
      setError("Please choose a booking date and time first.");
      return;
    }

    try {
      setActiveMessage("");
      setError("");
      await request("/bookings", {
        method: "POST",
        body: JSON.stringify({
          client: { userID: user.userID },
          gig: { gigID: gig.gigID },
          bookingDate,
        }),
      });

      await loadDashboard(user);
      setActiveMessage(`Booking request sent for ${gig.title}.`);
    } catch (err) {
      setError(err.message || "Unable to book this gig.");
    }
  };

  const updateApplicant = async (applicantId, status) => {
    try {
      setActiveMessage("");
      setError("");
      await request(`/admin/applicants/${applicantId}/status?status=${status}`, {
        method: "PUT",
      });
      await loadDashboard(user);
      setActiveMessage(`Applicant ${status.toLowerCase()} successfully.`);
    } catch (err) {
      setError(err.message || "Unable to update applicant.");
    }
  };

  const updateBookingStatus = async (bookingId, status) => {
    try {
      setActiveMessage("");
      setError("");
      await request(`/bookings/${bookingId}/status?status=${status}`, {
        method: "PUT",
      });
      await loadDashboard(user);
      setActiveMessage(`Booking marked as ${status.toLowerCase()}.`);
    } catch (err) {
      setError(err.message || "Unable to update booking.");
    }
  };

  if (!loading && !user) {
    return (
      <main className="dashboard-empty">
        <section>
          <p className="dashboard-kicker">sideL</p>
          <h1>Please log in to open your dashboard.</h1>
          <Link className="primary-action" to="/login">Go to login</Link>
        </section>
      </main>
    );
  }

  return (
    <main className="dashboard-shell">
      <aside className="dashboard-sidebar">
        <div>
          <p className="dashboard-kicker">sideL</p>
          <h1>Dashboard</h1>
        </div>

        <nav className="dashboard-nav" aria-label="Dashboard sections">
          <a href="#browse">Browse gigs</a>
          <a href="#bookings">My bookings</a>
          {isProvider && <a href="#provider">Provider tools</a>}
          {isAdmin && <a href="#admin">Admin</a>}
        </nav>

        <button className="secondary-action" type="button" onClick={handleLogout}>
          Logout
        </button>
      </aside>

      <section className="dashboard-content">
        <DashboardHeader
          user={user}
          providerStatus={providerStatusLabel}
          searchTerm={searchTerm}
          onSearchChange={setSearchTerm}
          canApplyAsProvider={!isProvider && providerStatusLabel !== "PENDING" && !isAdmin}
          onApplyClick={() => setShowProviderForm((current) => !current)}
        />

        {loading && <p className="status-line">Loading dashboard...</p>}
        {error && <p className="error-banner">{error}</p>}
        {activeMessage && <p className="success-banner">{activeMessage}</p>}

        {showProviderForm && !isProvider && !isAdmin && providerStatusLabel !== "PENDING" && (
          <section className="dashboard-panel" id="provider-application">
            <div className="section-heading">
              <div>
                <p className="dashboard-kicker">ProvidersForm</p>
                <h3>Apply as a provider</h3>
              </div>
            </div>

            <form className="provider-form" onSubmit={applyAsProvider}>
              <label>
                Service title
                <input
                  type="text"
                  placeholder="Example: Home plumbing repair"
                  value={providerForm.title}
                  onChange={(event) => setProviderForm({ ...providerForm, title: event.target.value })}
                />
              </label>

              <label>
                Category
                <select
                  value={providerForm.category}
                  onChange={(event) => setProviderForm({ ...providerForm, category: event.target.value })}
                >
                  {categories.filter((category) => category !== "ALL").map((category) => (
                    <option key={category} value={category}>{category}</option>
                  ))}
                </select>
              </label>

              <label>
                Email
                <input
                  type="email"
                  placeholder="Provider contact email"
                  value={providerForm.email}
                  onChange={(event) => setProviderForm({ ...providerForm, email: event.target.value })}
                />
              </label>

              <label>
                Address
                <input
                  type="text"
                  placeholder="Service area or full address"
                  value={providerForm.address}
                  onChange={(event) => setProviderForm({ ...providerForm, address: event.target.value })}
                />
              </label>

              <label className="wide-field">
                Image URL
                <input
                  type="url"
                  placeholder="Optional image link for your work/sample"
                  value={providerForm.image}
                  onChange={(event) => setProviderForm({ ...providerForm, image: event.target.value })}
                />
              </label>

              <label className="wide-field">
                Description
                <textarea
                  placeholder="Tell admin what services you offer and your experience."
                  value={providerForm.description}
                  onChange={(event) => setProviderForm({ ...providerForm, description: event.target.value })}
                />
              </label>

              <label className="wide-field">
                Other requirements
                <textarea
                  placeholder="Add certificates, tools, years of experience, ID notes, or other proof."
                  value={providerForm.requirements}
                  onChange={(event) => setProviderForm({ ...providerForm, requirements: event.target.value })}
                />
              </label>

              <div className="form-actions">
                <button className="secondary-inline" type="button" onClick={() => setShowProviderForm(false)}>
                  Cancel
                </button>
                <button className="primary-action" type="submit">
                  Submit application
                </button>
              </div>
            </form>
          </section>
        )}

        <section className="dashboard-panel" id="browse">
          <div className="section-heading">
            <div>
              <p className="dashboard-kicker">Client marketplace</p>
              <h3>Browse available gigs</h3>
            </div>
            <input
              className="booking-date"
              type="datetime-local"
              value={bookingDate}
              onChange={(event) => setBookingDate(event.target.value)}
              aria-label="Booking date and time"
            />
          </div>

          <div className="category-tabs">
            {categories.map((category) => (
              <button
                className={selectedCategory === category ? "active" : ""}
                key={category}
                type="button"
                onClick={() => setSelectedCategory(category)}
              >
                {category}
              </button>
            ))}
          </div>

          <div className="gig-grid">
            {visibleGigs.map((gig) => (
              <article className="gig-card" key={gig.gigID}>
                <div>
                  <span className="pill">{gig.category}</span>
                  <h4>{gig.title}</h4>
                  <p>{gig.description || "No description provided."}</p>
                </div>
                <div className="card-footer">
                  <strong>PHP {Number(gig.price || 0).toLocaleString()}</strong>
                  <button type="button" onClick={() => bookGig(gig)}>
                    Book
                  </button>
                </div>
              </article>
            ))}
          </div>

          {!visibleGigs.length && !loading && (
            <p className="muted-text">No gigs found for this category yet.</p>
          )}
        </section>

        <section className="dashboard-panel" id="bookings">
          <div className="section-heading">
            <div>
              <p className="dashboard-kicker">Client activity</p>
              <h3>My bookings</h3>
            </div>
          </div>

          <div className="data-list">
            {myBookings.map((booking) => (
              <article className="data-row" key={booking.bookingID}>
                <div>
                  <h4>{booking.gig?.title || "Booked service"}</h4>
                  <p>{new Date(booking.bookingDate).toLocaleString()}</p>
                </div>
                <span className="status-chip">{booking.status}</span>
              </article>
            ))}
          </div>

          {!myBookings.length && !loading && (
            <p className="muted-text">You have no bookings yet.</p>
          )}
        </section>

        {isProvider && (
          <section className="dashboard-panel provider-layout" id="provider">
            <div className="section-heading">
              <div>
                <p className="dashboard-kicker">Provider workspace</p>
                <h3>Manage gigs and requests</h3>
              </div>
            </div>

            <form className="gig-form" onSubmit={createGig}>
              <input
                type="text"
                placeholder="Service title"
                value={gigForm.title}
                onChange={(event) => setGigForm({ ...gigForm, title: event.target.value })}
              />
              <select
                value={gigForm.category}
                onChange={(event) => setGigForm({ ...gigForm, category: event.target.value })}
              >
                {categories.filter((category) => category !== "ALL").map((category) => (
                  <option key={category} value={category}>{category}</option>
                ))}
              </select>
              <input
                type="number"
                min="1"
                placeholder="Price"
                value={gigForm.price}
                onChange={(event) => setGigForm({ ...gigForm, price: event.target.value })}
              />
              <textarea
                placeholder="Short service description"
                value={gigForm.description}
                onChange={(event) => setGigForm({ ...gigForm, description: event.target.value })}
              />
              <button type="submit">Post gig</button>
            </form>

            <div className="split-grid">
              <div>
                <h4 className="list-title">My posted gigs</h4>
                <div className="data-list">
              {myGigs.map((gig) => (
                <article className="data-row" key={gig.gigID}>
                  <div>
                    <h4>{gig.title}</h4>
                    <p>{gig.category} - PHP {Number(gig.price || 0).toLocaleString()}</p>
                  </div>
                </article>
              ))}
                </div>
              </div>

              <div>
                <h4 className="list-title">Incoming jobs</h4>
                <div className="data-list">
                  {providerJobs.map((booking) => (
                    <article className="data-row stacked" key={booking.bookingID}>
                      <div>
                        <h4>{booking.gig?.title || "Service request"}</h4>
                        <p>
                          {booking.client?.firstname} {booking.client?.lastname} -{" "}
                          {new Date(booking.bookingDate).toLocaleString()}
                        </p>
                      </div>
                      <div className="button-group">
                        {bookingStatuses.map((status) => (
                          <button
                            key={status}
                            type="button"
                            onClick={() => updateBookingStatus(booking.bookingID, status)}
                          >
                            {status}
                          </button>
                        ))}
                      </div>
                    </article>
                  ))}
                </div>
              </div>
            </div>
          </section>
        )}

        {isAdmin && (
          <section className="dashboard-panel" id="admin">
            <div className="section-heading">
              <div>
                <p className="dashboard-kicker">Admin review</p>
                <h3>Provider applicants</h3>
              </div>
            </div>

            <div className="data-list">
              {applicants.map((applicant) => (
                <article className="data-row applicant-row" key={applicant.applicationID}>
                  <div>
                    <span className="pill">{applicant.category}</span>
                    <h4>{applicant.title}</h4>
                    <p>
                      {applicant.user?.firstname} {applicant.user?.lastname} - {applicant.email}
                    </p>
                    <p>{applicant.address}</p>
                    <p>{applicant.description}</p>
                    {applicant.requirements && <p>Requirements: {applicant.requirements}</p>}
                    {applicant.image && (
                      <a className="application-link" href={applicant.image} target="_blank" rel="noreferrer">
                        View image
                      </a>
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
              ))}
            </div>

            {!applicants.length && !loading && (
              <p className="muted-text">No pending provider applications.</p>
            )}
          </section>
        )}
      </section>
    </main>
  );
};

export default Dashboard;
