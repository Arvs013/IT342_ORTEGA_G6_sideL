import React, { useCallback, useEffect, useMemo, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import "../styles/dashboard.css";

const API_BASE_URL = "http://localhost:8080/api";
const API_ORIGIN = API_BASE_URL.replace("/api", "");

const historyStatuses = ["COMPLETED", "CANCELLED", "REJECTED"];

const getImageSource = (image) =>
  image?.startsWith("/uploads/") ? `${API_ORIGIN}${image}` : image;

const ClientBookings = () => {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [bookings, setBookings] = useState([]);
  const [receiptFiles, setReceiptFiles] = useState({});
  const [reviewForms, setReviewForms] = useState({});
  const [loading, setLoading] = useState(true);
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

  const uploadReceipt = useCallback(async (file) => {
    const formData = new FormData();
    formData.append("images", file);

    const response = await fetch(`${API_BASE_URL}/uploads/images`, {
      method: "POST",
      body: formData,
    });

    const contentType = response.headers.get("content-type") || "";
    const data = contentType.includes("application/json")
      ? await response.json()
      : await response.text();

    if (!response.ok) {
      throw new Error(typeof data === "string" ? data : data.message || "Upload failed");
    }

    return data[0];
  }, []);

  const loadBookings = useCallback(async (currentUser) => {
    if (!currentUser?.userID) return;

    setLoading(true);
    setError("");

    try {
      const clientBookings = await request(`/bookings/client/${currentUser.userID}`);
      setBookings(clientBookings);
    } catch (err) {
      setError(err.message || "Could not load bookings.");
    } finally {
      setLoading(false);
    }
  }, [request]);

  useEffect(() => {
    const savedUser = localStorage.getItem("loggedUser");
    if (!savedUser) {
      navigate("/login");
      return;
    }

    const parsedUser = JSON.parse(savedUser);
    setUser(parsedUser);
    loadBookings(parsedUser);
  }, [loadBookings, navigate]);

  const scheduledBookings = useMemo(
    () => bookings.filter((booking) => !historyStatuses.includes(booking.status)),
    [bookings]
  );

  const historyBookings = useMemo(
    () => bookings.filter((booking) => historyStatuses.includes(booking.status)),
    [bookings]
  );

  const cancelBooking = async (bookingId) => {
    try {
      setError("");
      setMessage("");
      await request(`/bookings/${bookingId}/status?status=CANCELLED`, {
        method: "PUT",
      });
      await loadBookings(user);
      setMessage("Booking cancelled.");
    } catch (err) {
      setError(err.message || "Unable to cancel booking.");
    }
  };

  const submitReceipt = async (bookingId) => {
    const file = receiptFiles[bookingId];
    if (!file) {
      setError("Please choose a receipt image first.");
      return;
    }

    try {
      setError("");
      setMessage("");
      const receiptUrl = await uploadReceipt(file);
      await request(`/bookings/${bookingId}/receipt`, {
        method: "PUT",
        body: JSON.stringify({ receiptUrl }),
      });
      setReceiptFiles((current) => ({ ...current, [bookingId]: null }));
      await loadBookings(user);
      setMessage("Receipt submitted and booking moved to history.");
    } catch (err) {
      setError(err.message || "Unable to submit receipt.");
    }
  };

  const submitReview = async (booking) => {
    const form = reviewForms[booking.bookingID] || { rating: 5, comment: "" };

    if (!form.comment.trim()) {
      setError("Please write a short review before submitting.");
      return;
    }

    try {
      setError("");
      setMessage("");
      await request(`/reviews/gig/${booking.gig.gigID}/client/${user.userID}`, {
        method: "POST",
        body: JSON.stringify({
          rating: Number(form.rating),
          comment: form.comment,
        }),
      });
      setReviewForms((current) => ({
        ...current,
        [booking.bookingID]: { rating: 5, comment: "" },
      }));
      setMessage("Review posted successfully.");
    } catch (err) {
      setError(err.message || "Unable to submit review.");
    }
  };

  const renderBooking = (booking, isHistory = false) => {
    const isAccepted = booking.status === "ACCEPTED";
    const isInProgress = booking.status === "IN_PROGRESS";
    const canReview = isHistory && booking.status === "COMPLETED" && booking.gig?.gigID;
    const reviewForm = reviewForms[booking.bookingID] || { rating: 5, comment: "" };
    const statusLabel = isAccepted ? "SCHEDULED" : isInProgress ? "IN PROGRESS" : booking.status;

    return (
      <article className="client-booking-card" key={booking.bookingID}>
        <div className="provider-booking-main">
          <div>
            <span className="pill">{booking.gig?.category || "Service"}</span>
            <h4>{booking.gig?.title || "Booked service"}</h4>
            <p>{new Date(booking.bookingDate).toLocaleString()}</p>
            <p>Provider: {booking.gig?.provider?.firstname || "Provider"} {booking.gig?.provider?.lastname || ""}</p>
          </div>
          <span className="status-chip">{statusLabel}</span>
        </div>

        <div className="provider-booking-details">
          <p><strong>Address:</strong> {booking.serviceAddress || "No address provided"}</p>
          {booking.clientNotes && <p><strong>Notes:</strong> {booking.clientNotes}</p>}
          {booking.receiptUrl && (
            <a className="application-link" href={getImageSource(booking.receiptUrl)} target="_blank" rel="noreferrer">
              View receipt
            </a>
          )}
        </div>

        {!isHistory && (
          <div className="client-booking-actions">
            {(isAccepted || isInProgress) && (
              <label className="receipt-upload">
                Receipt image
                <input
                  type="file"
                  accept="image/*"
                  onChange={(event) => setReceiptFiles({
                    ...receiptFiles,
                    [booking.bookingID]: event.target.files?.[0] || null,
                  })}
                />
              </label>
            )}
            {(isAccepted || isInProgress) && (
              <button className="primary-action compact-action" type="button" onClick={() => submitReceipt(booking.bookingID)}>
                Submit receipt
              </button>
            )}
            {booking.status !== "COMPLETED" && (
              <button className="secondary-inline" type="button" onClick={() => cancelBooking(booking.bookingID)}>
                Cancel booking
              </button>
            )}
          </div>
        )}

        {canReview && (
          <div className="history-review-form">
            <label>
              Rating
              <select
                value={reviewForm.rating}
                onChange={(event) => setReviewForms({
                  ...reviewForms,
                  [booking.bookingID]: { ...reviewForm, rating: event.target.value },
                })}
              >
                <option value="5">5 - Excellent</option>
                <option value="4">4 - Good</option>
                <option value="3">3 - Fair</option>
                <option value="2">2 - Poor</option>
                <option value="1">1 - Bad</option>
              </select>
            </label>
            <label>
              Review
              <textarea
                placeholder="Share your experience with this completed service."
                value={reviewForm.comment}
                onChange={(event) => setReviewForms({
                  ...reviewForms,
                  [booking.bookingID]: { ...reviewForm, comment: event.target.value },
                })}
              />
            </label>
            <button className="primary-action compact-action" type="button" onClick={() => submitReview(booking)}>
              Post review
            </button>
          </div>
        )}
      </article>
    );
  };

  return (
    <main className="profile-page">
      <header className="profile-page-header">
        <Link className="brand-mark" to="/dashboard" aria-label="Go to dashboard">
          sideL
        </Link>
        <div className="header-actions">
          <Link className="secondary-inline" to="/profile">My Profile</Link>
          <Link className="secondary-inline" to="/dashboard">Dashboard</Link>
        </div>
      </header>

      <section className="dashboard-panel profile-page-card client-bookings-page">
        <div className="section-heading">
          <div>
            <p className="dashboard-kicker">Client bookings</p>
            <h1>Scheduled and history</h1>
          </div>
          <span className="status-chip">{bookings.length} bookings</span>
        </div>

        {loading && <p className="status-line">Loading bookings...</p>}
        {error && <p className="error-banner">{error}</p>}
        {message && <p className="success-banner">{message}</p>}

        {!loading && (
          <>
            <section className="booking-section">
              <div className="section-heading">
                <div>
                  <p className="dashboard-kicker">Scheduled</p>
                  <h3>Pending and accepted requests</h3>
                </div>
              </div>
              <div className="provider-booking-list">
                {scheduledBookings.map((booking) => renderBooking(booking))}
              </div>
              {!scheduledBookings.length && <p className="muted-text">No scheduled bookings yet.</p>}
            </section>

            <section className="booking-section">
              <div className="section-heading">
                <div>
                  <p className="dashboard-kicker">History</p>
                  <h3>Completed, cancelled, and rejected</h3>
                </div>
              </div>
              <div className="provider-booking-list">
                {historyBookings.map((booking) => renderBooking(booking, true))}
              </div>
              {!historyBookings.length && <p className="muted-text">No booking history yet.</p>}
            </section>
          </>
        )}
      </section>
    </main>
  );
};

export default ClientBookings;
