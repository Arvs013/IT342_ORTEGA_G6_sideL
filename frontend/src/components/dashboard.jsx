import React, { useCallback, useEffect, useMemo, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import DashboardHeader from "./DashboardHeader";
import { clearAuthSession, getAuthHeaders } from "../utils/auth";
import "../styles/dashboard.css";

const API_BASE_URL = "http://localhost:8080/api";
const API_ORIGIN = API_BASE_URL.replace("/api", "");

const categories = [
  "ALL",
  "Electrical",
  "Plumbing",
  "Home Cleaning",
  "Appliance Repair",
  "Electronics Repair",
  "Gadget Repair",
  "Motorcycle Mechanic",
  "Car Mechanic",
  "HVAC",
  "Handyman",
  "Landscaping",
  "Pest Control",
  "Pool Maintenance",
  "Window Cleaning",
  "Carpet Cleaning",
];

const emptyProviderForm = {
  title: "",
  description: "",
  image: "",
  category: "Electrical",
  address: "",
  email: "",
  requirements: "",
};

const emptyGigForm = {
  title: "",
  description: "",
  price: "",
  category: "Electrical",
  imageUrls: "",
};

const getNextBookingStatuses = (status) => {
  switch (status) {
    case "PENDING":
      return ["ACCEPTED", "REJECTED", "CANCELLED"];
    case "ACCEPTED":
      return ["IN_PROGRESS", "CANCELLED"];
    case "IN_PROGRESS":
      return ["COMPLETED", "CANCELLED"];
    default:
      return [];
  }
};

const formatStatus = (status = "") => status.replace(/_/g, " ");

const getStatusClass = (status = "") => {
  const normalized = status.toUpperCase();
  if (normalized === "COMPLETED") return "success-status";
  if (["REJECTED", "CANCELLED"].includes(normalized)) return "danger-status";
  if (normalized === "PENDING") return "warning-status";
  return "info-status";
};

const formatBookingDateTime = (value, fallback = "No schedule") => {
  if (!value) return fallback;
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;

  return date.toLocaleString("en-US", {
    month: "2-digit",
    day: "2-digit",
    year: "numeric",
    hour: "numeric",
    minute: "2-digit",
    hour12: true,
  });
};

const sortNewestBookings = (bookingList = []) =>
  [...bookingList].sort((a, b) => {
    const first = new Date(a.bookingDate || a.createdAt || 0).getTime();
    const second = new Date(b.bookingDate || b.createdAt || 0).getTime();
    return second - first;
  });

const getUserName = (person, fallback = "User") =>
  `${person?.firstname || ""} ${person?.lastname || ""}`.trim() || fallback;

const getMinimumBookingDateTime = () => {
  const now = new Date();
  now.setMinutes(now.getMinutes() - now.getTimezoneOffset() + 30);
  return now.toISOString().slice(0, 16);
};

const normalizeCategory = (category = "") =>
  category.toString().trim().toUpperCase().replace(/[\s-]+/g, "_");

const getGigImages = (gig) => {
  const rawImages = gig?.imageUrls || gig?.image || "";
  if (!rawImages) return [];

  return rawImages
    .split(/[\n,;]+/)
    .map((image) => image.trim())
    .filter(Boolean)
    .slice(0, 5);
};

const getImageSource = (image) =>
  image.startsWith("/uploads/") ? `${API_ORIGIN}${image}` : image;

const mapLikedGigs = (gigList = []) =>
  Object.fromEntries(gigList.map((gig) => [gig.gigID, Boolean(gig.likedByCurrentUser)]));

const Dashboard = () => {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [gigs, setGigs] = useState([]);
  const [myGigs, setMyGigs] = useState([]);
  const [providerJobs, setProviderJobs] = useState([]);
  const [reviewsByGig, setReviewsByGig] = useState({});
  const [likedGigs, setLikedGigs] = useState({});
  const [imageIndexes, setImageIndexes] = useState({});
  const [selectedGig, setSelectedGig] = useState(null);
  const [selectedCategory, setSelectedCategory] = useState("ALL");
  const [searchTerm, setSearchTerm] = useState("");
  const [bookingGig, setBookingGig] = useState(null);
  const [reportModal, setReportModal] = useState(null);
  const [reportForm, setReportForm] = useState({ reason: "Issue with client", details: "" });
  const [bookingForm, setBookingForm] = useState({
    bookingDate: "",
    firstname: "",
    lastname: "",
    email: "",
    phoneNumber: "",
    address: "",
    notes: "",
  });
  const [activeMessage, setActiveMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);
  const [showProviderForm, setShowProviderForm] = useState(false);
  const [providerForm, setProviderForm] = useState(emptyProviderForm);
  const [providerImageFiles, setProviderImageFiles] = useState([]);
  const [showGigForm, setShowGigForm] = useState(false);
  const [editingGig, setEditingGig] = useState(null);
  const [gigForm, setGigForm] = useState(emptyGigForm);
  const [gigImageFiles, setGigImageFiles] = useState([]);

  const isProvider = Boolean(user?.isProvider);
  const isAdmin = Boolean(user?.isAdmin);
  const providerStatusLabel = user?.providerStatus || "NONE";
  const activeGigCount = myGigs.filter((gig) => (gig.status || "ACTIVE") !== "DISABLED").length;
  const disabledGigCount = myGigs.length - activeGigCount;
  const openProviderJobs = providerJobs.filter((booking) => ["PENDING", "ACCEPTED", "IN_PROGRESS"].includes(booking.status)).length;

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

  const uploadImages = useCallback(async (files) => {
    if (!files.length) return [];

    const formData = new FormData();
    files.forEach((file) => formData.append("images", file));

    const response = await fetch(`${API_BASE_URL}/uploads/images`, {
      method: "POST",
      headers: getAuthHeaders(),
      body: formData,
    });

    const contentType = response.headers.get("content-type") || "";
    const data = contentType.includes("application/json")
      ? await response.json()
      : await response.text();

    if (!response.ok) {
      throw new Error(typeof data === "string" ? data : data.message || "Upload failed");
    }

    return data;
  }, []);

  const getGigReviews = useCallback(
    (gigId) => reviewsByGig[gigId] || [],
    [reviewsByGig]
  );

  const getAverageRating = useCallback(
    (gigId) => {
      const reviews = getGigReviews(gigId);
      if (!reviews.length) return null;

      const total = reviews.reduce((sum, review) => sum + Number(review.rating || 0), 0);
      return (total / reviews.length).toFixed(1);
    },
    [getGigReviews]
  );

  const visibleGigs = useMemo(() => {
    const normalizedSearch = searchTerm.trim().toLowerCase();

    return gigs.filter((gig) => {
      const matchesCategory =
        selectedCategory === "ALL" ||
        normalizeCategory(gig.category) === normalizeCategory(selectedCategory);
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

  const loadReviewsForGigs = useCallback(async (allGigs) => {
    const reviewEntries = await Promise.all(
      allGigs.map(async (gig) => {
        const reviews = await request(`/reviews/gig/${gig.gigID}`);
        return [gig.gigID, reviews];
      })
    );

    setReviewsByGig(Object.fromEntries(reviewEntries));
  }, [request]);

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
      const allGigs = await request(`/services?userId=${freshUser.userID}`);
      setGigs(allGigs);
      setLikedGigs(mapLikedGigs(allGigs));
      if (freshUser.isProvider) {
        const [providerGigs, incomingJobs] = await Promise.all([
          request(`/gigs/provider/${freshUser.userID}`),
          request(`/bookings/provider/${freshUser.userID}`),
        ]);
        setMyGigs(providerGigs);
        setProviderJobs(sortNewestBookings(incomingJobs));
      } else {
        setMyGigs([]);
        setProviderJobs([]);
      }
      await loadReviewsForGigs(allGigs);
    } catch (err) {
      setError(err.message || "Could not load dashboard.");
    } finally {
      setLoading(false);
    }
  }, [loadReviewsForGigs, refreshUser, request]);

  useEffect(() => {
    const savedUser = localStorage.getItem("loggedUser");
    const savedToken = localStorage.getItem("authToken");
    if (!savedUser || !savedToken) {
      clearAuthSession();
      setLoading(false);
      return;
    }

    const parsedUser = JSON.parse(savedUser);
    setUser(parsedUser);
    loadDashboard(parsedUser);
  }, [loadDashboard]);

  const handleLogout = () => {
    clearAuthSession();
    navigate("/login");
  };

  const toggleLike = async (gigId) => {
    if (!user?.userID) {
      navigate("/login");
      return;
    }

    const nextLiked = !likedGigs[gigId];

    try {
      setError("");
      const updatedGig = await request(`/gigs/${gigId}/like/${user.userID}?liked=${nextLiked}`, {
        method: "PUT",
      });

      setLikedGigs((current) => ({
        ...current,
        [gigId]: Boolean(updatedGig.likedByCurrentUser),
      }));

      const replaceUpdatedGig = (gig) =>
        gig.gigID === gigId
          ? {
              ...gig,
              likeCount: updatedGig.likeCount,
              likedByCurrentUser: updatedGig.likedByCurrentUser,
            }
          : gig;

      setGigs((current) => current.map(replaceUpdatedGig));
      setMyGigs((current) => current.map(replaceUpdatedGig));
    } catch (err) {
      setError(err.message || "Unable to update gig like.");
    }
  };

  const changeGigImage = (gigId, imageCount, direction) => {
    if (imageCount <= 1) return;

    setImageIndexes((current) => {
      const currentIndex = current[gigId] || 0;
      const nextIndex = (currentIndex + direction + imageCount) % imageCount;
      return {
        ...current,
        [gigId]: nextIndex,
      };
    });
  };

  const emptyBookingForm = {
    bookingDate: "",
    firstname: "",
    lastname: "",
    email: "",
    phoneNumber: "",
    address: "",
    notes: "",
  };

  const openBookingModal = (gig) => {
    setError("");
    setActiveMessage("");
    setBookingGig(gig);
    setBookingForm({
      bookingDate: "",
      firstname: user?.firstname || "",
      lastname: user?.lastname || "",
      email: user?.email || "",
      phoneNumber: user?.phoneNumber || "",
      address: user?.address || "",
      notes: "",
    });
  };

  const closeBookingModal = () => {
    setBookingGig(null);
    setBookingForm(emptyBookingForm);
  };

  const openCreateGigForm = () => {
    setEditingGig(null);
    setGigForm(emptyGigForm);
    setGigImageFiles([]);
    setShowGigForm(true);
    setError("");
    setActiveMessage("");
  };

  const openEditGigForm = (gig) => {
    setEditingGig(gig);
    setGigForm({
      title: gig.title || "",
      description: gig.description || "",
      price: gig.price || "",
      category: gig.category || "Electrical",
      imageUrls: gig.imageUrls || "",
    });
    setGigImageFiles([]);
    setShowGigForm(true);
    setError("");
    setActiveMessage("");
  };

  const closeGigForm = () => {
    setEditingGig(null);
    setGigForm(emptyGigForm);
    setGigImageFiles([]);
    setShowGigForm(false);
  };

  const submitGig = async (event) => {
    event.preventDefault();

    if (!user?.userID) return;

    if (!gigForm.title || !gigForm.description || !gigForm.price || !gigForm.category) {
      setError("Please complete the gig title, description, price, and category.");
      return;
    }

    if (gigImageFiles.length > 5) {
      setError("Please upload only up to 5 images for this gig.");
      return;
    }

    try {
      setActiveMessage("");
      setError("");
      const uploadedImages = gigImageFiles.length ? await uploadImages(gigImageFiles) : null;
      const imageUrls = uploadedImages ? uploadedImages.join("\n") : gigForm.imageUrls;
      const path = editingGig
        ? `/gigs/${editingGig.gigID}/provider/${user.userID}`
        : `/gigs/create/${user.userID}`;

      await request(path, {
        method: editingGig ? "PUT" : "POST",
        body: JSON.stringify({
          title: gigForm.title,
          description: gigForm.description,
          price: Number(gigForm.price),
          category: gigForm.category,
          imageUrls,
        }),
      });

      closeGigForm();
      await loadDashboard(user);
      setActiveMessage(editingGig ? "Gig updated successfully." : "Gig posted successfully.");
    } catch (err) {
      setError(err.message || "Unable to save this gig.");
    }
  };

  const updateGigStatus = async (gig, status) => {
    try {
      setActiveMessage("");
      setError("");
      await request(`/gigs/${gig.gigID}/provider/${user.userID}/status?status=${status}`, {
        method: "PUT",
      });
      await loadDashboard(user);
      setActiveMessage(`Gig marked as ${status.toLowerCase()}.`);
    } catch (err) {
      setError(err.message || "Unable to update gig status.");
    }
  };

  const deleteGig = async (gig) => {
    const confirmed = window.confirm(`Delete "${gig.title}"? Gigs with bookings or reviews should be disabled instead.`);
    if (!confirmed) return;

    try {
      setActiveMessage("");
      setError("");
      await request(`/gigs/${gig.gigID}/provider/${user.userID}`, {
        method: "DELETE",
      });
      await loadDashboard(user);
      setActiveMessage("Gig deleted.");
    } catch (err) {
      setError(err.message || "Unable to delete gig.");
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
      setError(err.message || "Unable to update booking status.");
    }
  };

  const openClientReportModal = (booking) => {
    setReportModal({
      booking,
      reportedUser: booking.client,
      label: booking.contactName || `${booking.client?.firstname || "Client"} ${booking.client?.lastname || ""}`.trim(),
    });
    setReportForm({ reason: "Issue with client", details: "" });
    setError("");
    setActiveMessage("");
  };

  const submitReport = async (event) => {
    event.preventDefault();

    if (!reportModal?.reportedUser?.userID) {
      setError("Cannot report this client because the account details are missing.");
      return;
    }
    if (!reportForm.details.trim()) {
      setError("Please explain what happened before submitting the report.");
      return;
    }

    try {
      setError("");
      setActiveMessage("");
      await request("/reports", {
        method: "POST",
        body: JSON.stringify({
          reporterId: user.userID,
          reportedUserId: reportModal.reportedUser.userID,
          bookingId: reportModal.booking.bookingID,
          reason: reportForm.reason,
          details: reportForm.details,
        }),
      });
      setReportModal(null);
      setReportForm({ reason: "Issue with client", details: "" });
      setActiveMessage("Report submitted. Admin will review it.");
    } catch (err) {
      setError(err.message || "Unable to submit report.");
    }
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

    if (providerImageFiles.length > 5) {
      setError("Please upload only up to 5 images.");
      return;
    }

    try {
      setActiveMessage("");
      setError("");
      const uploadedImages = providerImageFiles.length ? await uploadImages(providerImageFiles) : [];
      await request(`/gigs/apply/${user.userID}`, {
        method: "POST",
        body: JSON.stringify({
          ...providerForm,
          image: uploadedImages.join("\n"),
        }),
      });
      await refreshUser(user.userID);
      setProviderForm(emptyProviderForm);
      setProviderImageFiles([]);
      setShowProviderForm(false);
      setActiveMessage("Provider application submitted. Please wait for admin approval.");
    } catch (err) {
      setError(err.message || "Unable to submit provider application.");
    }
  };

  const bookGig = async (event) => {
    event.preventDefault();

    if (!bookingGig) return;

    if (!bookingForm.bookingDate) {
      setError("Please choose a booking date and time first.");
      return;
    }

    if (new Date(bookingForm.bookingDate).getTime() < Date.now() + 30 * 60 * 1000) {
      setError("Please choose a booking schedule at least 30 minutes from now.");
      return;
    }

    if (
      !bookingForm.firstname ||
      !bookingForm.lastname ||
      !bookingForm.email ||
      !bookingForm.phoneNumber ||
      !bookingForm.address
    ) {
      setError("Please complete your client contact details before booking.");
      return;
    }

    try {
      setActiveMessage("");
      setError("");
      await request("/bookings", {
        method: "POST",
        body: JSON.stringify({
          client: { userID: user.userID },
          gig: { gigID: bookingGig.gigID },
          bookingDate: bookingForm.bookingDate,
          contactName: `${bookingForm.firstname} ${bookingForm.lastname}`.trim(),
          contactEmail: bookingForm.email,
          contactPhone: bookingForm.phoneNumber,
          serviceAddress: bookingForm.address,
          clientNotes: bookingForm.notes,
        }),
      });

      setActiveMessage(`Booking request sent for ${bookingGig.title}.`);
      closeBookingModal();
    } catch (err) {
      setError(err.message || "Unable to book this gig.");
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
    <main className="marketplace-shell">
      <section className="dashboard-content marketplace-content">
        <DashboardHeader
          user={user}
          providerStatus={providerStatusLabel}
          searchTerm={searchTerm}
          onSearchChange={setSearchTerm}
          canApplyAsProvider={!isProvider && providerStatusLabel !== "PENDING" && !isAdmin}
          onApplyClick={() => setShowProviderForm((current) => !current)}
          onMyProfile={() => navigate("/profile")}
          onLogout={handleLogout}
        />

        <div className="marketplace-tools">
          <div>
            <p className="dashboard-kicker">Client marketplace</p>
            <h1>Find trusted sideL services</h1>
          </div>
          {isProvider && (
            <div className="marketplace-actions">
              <a className="secondary-inline" href="#provider-bookings">
                Bookings
              </a>
              <a className="secondary-inline" href="#provider-gigs">
                My gigs
              </a>
              <button className="primary-action compact-action" type="button" onClick={openCreateGigForm}>
                Add gig
              </button>
            </div>
          )}
        </div>

        {loading && <p className="status-line">Loading dashboard...</p>}
        {error && <p className="error-banner">{error}</p>}
        {activeMessage && <p className="success-banner">{activeMessage}</p>}

        {isProvider && (
          <section className="dashboard-panel provider-gigs-panel" id="provider-gigs">
            <div className="section-heading">
              <div>
                <p className="dashboard-kicker">Provider workspace</p>
                <h3>My gigs</h3>
              </div>
              <button className="primary-action compact-action" type="button" onClick={openCreateGigForm}>
                Add gig
              </button>
            </div>

            <div className="provider-summary-grid" aria-label="Provider gig summary">
              <article>
                <strong>{myGigs.length}</strong>
                <span>Total gigs</span>
              </article>
              <article>
                <strong>{activeGigCount}</strong>
                <span>Active</span>
              </article>
              <article>
                <strong>{disabledGigCount}</strong>
                <span>Disabled</span>
              </article>
              <article>
                <strong>{myGigs.reduce((sum, gig) => sum + Number(gig.likeCount || 0), 0)}</strong>
                <span>Total likes</span>
              </article>
            </div>

            {showGigForm && (
              <form className="provider-form gig-editor-form" onSubmit={submitGig}>
                <div className="wide-field form-intro">
                  <p className="dashboard-kicker">{editingGig ? "Edit gig" : "New gig"}</p>
                  <h4>{editingGig ? "Update service details" : "Post a service clients can book"}</h4>
                </div>

                <label>
                  Gig title
                  <input
                    type="text"
                    placeholder="Example: Electrical outlet repair"
                    value={gigForm.title}
                    onChange={(event) => setGigForm({ ...gigForm, title: event.target.value })}
                  />
                </label>

                <label>
                  Category
                  <select
                    value={gigForm.category}
                    onChange={(event) => setGigForm({ ...gigForm, category: event.target.value })}
                  >
                    {categories.filter((category) => category !== "ALL").map((category) => (
                      <option key={category} value={category}>{category}</option>
                    ))}
                  </select>
                </label>

                <label>
                  Price
                  <input
                    type="number"
                    min="1"
                    placeholder="Service price"
                    value={gigForm.price}
                    onChange={(event) => setGigForm({ ...gigForm, price: event.target.value })}
                  />
                </label>

                <label className="wide-field">
                  Work images
                  <input
                    type="file"
                    accept="image/*"
                    multiple
                    onChange={(event) => setGigImageFiles(Array.from(event.target.files || []).slice(0, 5))}
                  />
                  <span className="field-hint">
                    {editingGig && !gigImageFiles.length
                      ? `${getGigImages({ imageUrls: gigForm.imageUrls }).length}/5 saved images. Choose new files to replace them.`
                      : `${gigImageFiles.length}/5 replacement images selected.`}
                  </span>
                </label>

                {editingGig && getGigImages({ imageUrls: gigForm.imageUrls }).length > 0 && !gigImageFiles.length && (
                  <div className="wide-field saved-image-preview">
                    {getGigImages({ imageUrls: gigForm.imageUrls }).map((image, index) => (
                      <img
                        key={`${editingGig.gigID}-saved-${image}`}
                        src={getImageSource(image)}
                        alt={`${editingGig.title} saved work ${index + 1}`}
                      />
                    ))}
                  </div>
                )}

                <label className="wide-field">
                  Description
                  <textarea
                    placeholder="Describe the service, coverage area, inclusions, and experience."
                    value={gigForm.description}
                    onChange={(event) => setGigForm({ ...gigForm, description: event.target.value })}
                  />
                </label>

                <div className="form-actions">
                  <button className="secondary-inline" type="button" onClick={closeGigForm}>
                    Cancel
                  </button>
                  <button className="primary-action" type="submit">
                    {editingGig ? "Save gig" : "Post gig"}
                  </button>
                </div>
              </form>
            )}

            <div className="provider-gig-list">
              {myGigs.map((gig) => {
                const images = getGigImages(gig);
                const gigStatus = gig.status || "ACTIVE";
                const isDisabled = gigStatus === "DISABLED";
                return (
                  <article className={`provider-gig-row ${isDisabled ? "disabled-gig" : ""}`} key={gig.gigID}>
                    <div className="provider-gig-thumb">
                      {images.length ? (
                        <img src={getImageSource(images[0])} alt={`${gig.title} preview`} loading="lazy" />
                      ) : (
                        <span>{gig.category}</span>
                      )}
                    </div>
                    <div>
                      <div className="inline-pills">
                        <span className="pill">{gig.category}</span>
                        <span className={`status-chip ${isDisabled ? "danger-status" : "success-status"}`}>
                          {gigStatus}
                        </span>
                      </div>
                      <h4>{gig.title}</h4>
                      <p>PHP {Number(gig.price || 0).toLocaleString()} - {images.length}/5 images - {gig.likeCount || 0} likes</p>
                    </div>
                    <div className="provider-gig-actions">
                      <button className="secondary-inline" type="button" onClick={() => openEditGigForm(gig)}>
                        Edit
                      </button>
                      <button
                        className="secondary-inline"
                        type="button"
                        onClick={() => updateGigStatus(gig, isDisabled ? "ACTIVE" : "DISABLED")}
                      >
                        {isDisabled ? "Activate" : "Disable"}
                      </button>
                      <button className="secondary-inline danger-inline" type="button" onClick={() => deleteGig(gig)}>
                        Delete
                      </button>
                    </div>
                  </article>
                );
              })}
            </div>

            {!myGigs.length && !loading && (
              <div className="dashboard-empty-state">
                <h4>No gigs posted yet</h4>
                <p>Add your first service so clients can discover and book your work.</p>
                <button className="primary-action compact-action" type="button" onClick={openCreateGigForm}>
                  Add your first gig
                </button>
              </div>
            )}
          </section>
        )}

        {isProvider && (
          <section className="dashboard-panel provider-gigs-panel" id="provider-bookings">
            <div className="section-heading">
              <div>
                <p className="dashboard-kicker">Provider bookings</p>
                <h3>Incoming service requests</h3>
              </div>
              <span className="status-chip">{openProviderJobs} active</span>
            </div>

            <div className="provider-booking-list">
              {providerJobs.map((booking) => (
                <article className="provider-booking-card" key={booking.bookingID}>
                  <div className="provider-booking-main">
                    <div>
                      <div className="inline-pills">
                        <span className="pill">{booking.gig?.category || "Service"}</span>
                        <span className={`status-chip ${getStatusClass(booking.status)}`}>
                          {formatStatus(booking.status)}
                        </span>
                      </div>
                      <h4>{booking.gig?.title || "Booked service"}</h4>
                      <p>Request #{booking.bookingID}</p>
                    </div>
                    <strong className="booking-card-date">{formatBookingDateTime(booking.bookingDate)}</strong>
                  </div>

                  <div className="provider-booking-details">
                    <div>
                      <span>Booking</span>
                      <strong>#{booking.bookingID}</strong>
                    </div>
                    <div>
                      <span>Status</span>
                      <strong>{formatStatus(booking.status)}</strong>
                    </div>
                    <div>
                      <span>Client name</span>
                      <strong>{booking.contactName || getUserName(booking.client, "Client")}</strong>
                    </div>
                    <div>
                      <span>Provider name</span>
                      <strong>{getUserName(booking.gig?.provider || user, "Provider")}</strong>
                    </div>
                    <div>
                      <span>Email</span>
                      <strong>{booking.contactEmail || booking.client?.email || "No email provided"}</strong>
                    </div>
                    <div>
                      <span>Phone</span>
                      <strong>{booking.contactPhone || booking.client?.phoneNumber || "No phone provided"}</strong>
                    </div>
                    <div>
                      <span>Address</span>
                      <strong>{booking.serviceAddress || booking.client?.address || "No address provided"}</strong>
                    </div>
                    <div>
                      <span>Date started</span>
                      <strong>{formatBookingDateTime(booking.dateStarted, "Not started yet")}</strong>
                    </div>
                    <div>
                      <span>Date finished</span>
                      <strong>{formatBookingDateTime(booking.dateFinished, "Not finished yet")}</strong>
                    </div>
                    {booking.clientNotes && (
                      <div className="wide-field">
                        <span>Notes</span>
                        <strong>{booking.clientNotes}</strong>
                      </div>
                    )}
                  </div>

                  <div className="button-group provider-booking-actions">
                    {getNextBookingStatuses(booking.status).map((status) => (
                      <button
                        key={status}
                        type="button"
                        className={status === "CANCELLED" || status === "REJECTED" ? "danger-inline" : ""}
                        onClick={() => updateBookingStatus(booking.bookingID, status)}
                      >
                        {formatStatus(status)}
                      </button>
                    ))}
                    {!getNextBookingStatuses(booking.status).length && (
                      <span className="muted-text">No more actions available.</span>
                    )}
                    <button className="danger-inline" type="button" onClick={() => openClientReportModal(booking)}>
                      Report client
                    </button>
                  </div>
                </article>
              ))}
            </div>

            {!providerJobs.length && !loading && (
              <div className="dashboard-empty-state">
                <h4>No booking requests yet</h4>
                <p>When clients book one of your active gigs, the request will appear here.</p>
              </div>
            )}
          </section>
        )}

        {showProviderForm && !isProvider && !isAdmin && providerStatusLabel !== "PENDING" && (
          <section className="dashboard-panel" id="provider-application">
            <div className="section-heading">
              <div>
                <p className="dashboard-kicker">Form</p>
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
                Work images
                <input
                  type="file"
                  accept="image/*"
                  multiple
                  onChange={(event) => setProviderImageFiles(Array.from(event.target.files || []).slice(0, 5))}
                />
                <span className="field-hint">{providerImageFiles.length}/5 images selected</span>
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

        <section className="gig-grid marketplace-grid" aria-label="Available gigs">
          {visibleGigs.map((gig) => {
            const reviews = getGigReviews(gig.gigID);
            const averageRating = getAverageRating(gig.gigID);
            const providerName = `${gig.provider?.firstname || "Provider"} ${gig.provider?.lastname || ""}`.trim();
            const providerInitials = `${gig.provider?.firstname?.[0] || ""}${gig.provider?.lastname?.[0] || ""}` || "SL";
            const providerPhoto = gig.provider?.profileImageUrl;
            const gigImages = getGigImages(gig);
            const activeImageIndex = Math.min(imageIndexes[gig.gigID] || 0, Math.max(gigImages.length - 1, 0));
            const activeImage = gigImages[activeImageIndex];

            return (
              <article className="gig-card marketplace-card" key={gig.gigID}>
                <button className="provider-profile-button" type="button" onClick={() => setSelectedGig(gig)}>
                  <span className="provider-card-avatar" aria-hidden="true">
                    {providerPhoto ? (
                      <img src={getImageSource(providerPhoto)} alt="" />
                    ) : (
                      providerInitials.toUpperCase()
                    )}
                  </span>
                  <span className="provider-card-meta">
                    <strong>{providerName}</strong>
                    <small>{averageRating ? `${averageRating}/5 rating` : "No ratings yet"} - {reviews.length} reviews</small>
                  </span>
                </button>

                <button
                  className={`heart-button ${likedGigs[gig.gigID] ? "liked" : ""}`}
                  type="button"
                  aria-label={likedGigs[gig.gigID] ? "Unlike gig" : "Like gig"}
                  onClick={() => toggleLike(gig.gigID)}
                >
                  <span aria-hidden="true">{likedGigs[gig.gigID] ? "\u2665" : "\u2661"}</span>
                  <small>{gig.likeCount || 0}</small>
                </button>

                <div className="gig-image-carousel">
                  {activeImage ? (
                    <>
                      <img
                        src={getImageSource(activeImage)}
                        alt={`${gig.title} work sample ${activeImageIndex + 1}`}
                        loading="lazy"
                      />
                      {gigImages.length > 1 && (
                        <div className="image-carousel-controls">
                          <button type="button" onClick={() => changeGigImage(gig.gigID, gigImages.length, -1)}>
                            &lt;
                          </button>
                          <span>{activeImageIndex + 1}/{gigImages.length}</span>
                          <button type="button" onClick={() => changeGigImage(gig.gigID, gigImages.length, 1)}>
                            &gt;
                          </button>
                        </div>
                      )}
                    </>
                  ) : (
                    <div className="gig-image-placeholder">
                      <span>{gig.category || "Service"}</span>
                    </div>
                  )}
                </div>

                <div>
                  <span className="pill">{gig.category}</span>
                  <h4>{gig.title}</h4>
                  <p>{gig.description || "No description provided."}</p>
                </div>

                <div className="card-footer">
                  <strong>PHP {Number(gig.price || 0).toLocaleString()}</strong>
                  <button type="button" onClick={() => openBookingModal(gig)}>
                    Book
                  </button>
                </div>
              </article>
            );
          })}
        </section>

        {!visibleGigs.length && !loading && (
          <div className="dashboard-empty-state marketplace-empty-state">
            <h4>No gigs found</h4>
            <p>Try another keyword or category. Approved provider services will show here once they are active.</p>
          </div>
        )}
      </section>

      {selectedGig && (
        <div className="profile-modal-backdrop" role="presentation" onClick={() => setSelectedGig(null)}>
          <section
            className="profile-modal"
            role="dialog"
            aria-modal="true"
            aria-labelledby="profile-modal-title"
            onClick={(event) => event.stopPropagation()}
          >
            <div className="profile-drawer-header">
              <span className="provider-modal-avatar" aria-hidden="true">
                {selectedGig.provider?.profileImageUrl ? (
                  <img src={getImageSource(selectedGig.provider.profileImageUrl)} alt="" />
                ) : (
                  `${selectedGig.provider?.firstname?.[0] || ""}${selectedGig.provider?.lastname?.[0] || ""}`.toUpperCase() || "SL"
                )}
              </span>
              <div>
                <p className="dashboard-kicker">Provider profile</p>
                <h2 id="profile-modal-title">{selectedGig.provider?.firstname} {selectedGig.provider?.lastname}</h2>
              </div>
              <button className="secondary-inline" type="button" onClick={() => setSelectedGig(null)}>
                Close
              </button>
            </div>

            <div className="profile-modal-content">
              <article className="provider-detail-card">
                {(() => {
                  const images = getGigImages(selectedGig);
                  const activeIndex = Math.min(imageIndexes[selectedGig.gigID] || 0, Math.max(images.length - 1, 0));
                  const activeImage = images[activeIndex];

                  return activeImage ? (
                    <div className="gig-image-carousel detail-gallery">
                      <img
                        src={getImageSource(activeImage)}
                        alt={`${selectedGig.title} work sample ${activeIndex + 1}`}
                        loading="lazy"
                      />
                      {images.length > 1 && (
                        <div className="image-carousel-controls">
                          <button type="button" onClick={() => changeGigImage(selectedGig.gigID, images.length, -1)}>
                            &lt;
                          </button>
                          <span>{activeIndex + 1}/{images.length}</span>
                          <button type="button" onClick={() => changeGigImage(selectedGig.gigID, images.length, 1)}>
                            &gt;
                          </button>
                        </div>
                      )}
                    </div>
                  ) : null;
                })()}
                <span className="pill">{selectedGig.category}</span>
                <h3>{selectedGig.title}</h3>
                <p>{selectedGig.description || "This provider has not added a detailed description yet."}</p>
                {selectedGig.provider?.bio && <p>{selectedGig.provider.bio}</p>}
                {selectedGig.provider?.address && <p>Service area: {selectedGig.provider.address}</p>}
                <strong>PHP {Number(selectedGig.price || 0).toLocaleString()}</strong>
              </article>

              <section className="reviews-panel">
                <div className="section-heading">
                  <div>
                    <p className="dashboard-kicker">Reviews</p>
                    <h3>
                      {getAverageRating(selectedGig.gigID) || "No"} rating
                    </h3>
                  </div>
                </div>

                <div className="data-list">
                  {getGigReviews(selectedGig.gigID).map((review) => (
                    <article className="review-row" key={review.reviewID}>
                      <div className="review-author-line">
                        <span className="review-avatar" aria-hidden="true">
                          {review.client?.profileImageUrl ? (
                            <img src={getImageSource(review.client.profileImageUrl)} alt="" />
                          ) : (
                            `${review.client?.firstname?.[0] || ""}${review.client?.lastname?.[0] || ""}`.toUpperCase() || "CL"
                          )}
                        </span>
                        <div>
                          <strong>{review.rating}/5</strong>
                          <span>{review.client?.firstname} {review.client?.lastname}</span>
                        </div>
                      </div>
                      <p>{review.comment || "No written comment."}</p>
                    </article>
                  ))}
                </div>

                {!getGigReviews(selectedGig.gigID).length && (
                  <p className="muted-text">No reviews yet. You can be the first to post one.</p>
                )}

                <p className="muted-text">Reviews can be posted from your booking history after a completed service.</p>
              </section>
            </div>
          </section>
        </div>
      )}

      {reportModal && (
        <div className="booking-modal-backdrop" role="presentation" onClick={() => setReportModal(null)}>
          <section
            className="booking-modal report-modal"
            role="dialog"
            aria-modal="true"
            aria-labelledby="provider-report-modal-title"
            onClick={(event) => event.stopPropagation()}
          >
            <div className="booking-modal-header">
              <div>
                <p className="dashboard-kicker">Report client</p>
                <h2 id="provider-report-modal-title">{reportModal.label || "Client"}</h2>
              </div>
              <button className="secondary-inline" type="button" onClick={() => setReportModal(null)}>
                Close
              </button>
            </div>

            <form className="booking-form report-form" onSubmit={submitReport}>
              <label>
                Reason
                <select
                  value={reportForm.reason}
                  onChange={(event) => setReportForm({ ...reportForm, reason: event.target.value })}
                >
                  <option value="Issue with client">Issue with client</option>
                  <option value="No show">No show</option>
                  <option value="Rude behavior">Rude behavior</option>
                  <option value="Payment conflict">Payment conflict</option>
                  <option value="Safety concern">Safety concern</option>
                  <option value="Other">Other</option>
                </select>
              </label>
              <label className="wide-field">
                Details
                <textarea
                  placeholder="Explain what happened so admin can review it."
                  value={reportForm.details}
                  onChange={(event) => setReportForm({ ...reportForm, details: event.target.value })}
                  required
                />
              </label>
              <div className="form-actions">
                <button className="secondary-inline" type="button" onClick={() => setReportModal(null)}>
                  Cancel
                </button>
                <button className="primary-action" type="submit">
                  Submit report
                </button>
              </div>
            </form>
          </section>
        </div>
      )}

      {bookingGig && (
        <div className="booking-modal-backdrop" role="presentation">
          <section className="booking-modal" role="dialog" aria-modal="true" aria-labelledby="booking-modal-title">
            <div className="booking-modal-header">
              <div>
                <p className="dashboard-kicker">Book service</p>
                <h2 id="booking-modal-title">{bookingGig.title}</h2>
              </div>
              <button className="secondary-inline" type="button" onClick={closeBookingModal}>
                Close
              </button>
            </div>

            <div className="booking-summary">
              <span className="pill">{bookingGig.category}</span>
              <strong>PHP {Number(bookingGig.price || 0).toLocaleString()}</strong>
              <p>{bookingGig.provider?.firstname} {bookingGig.provider?.lastname}</p>
            </div>

            <form className="booking-form" onSubmit={bookGig}>
              <label className="wide-field">
                Booking date and time
                <input
                  type="datetime-local"
                  min={getMinimumBookingDateTime()}
                  required
                  value={bookingForm.bookingDate}
                  onChange={(event) => setBookingForm({ ...bookingForm, bookingDate: event.target.value })}
                />
                <span className="field-hint">Choose a schedule at least 30 minutes from now.</span>
              </label>

              <label>
                First name
                <input
                  type="text"
                  required
                  value={bookingForm.firstname}
                  onChange={(event) => setBookingForm({ ...bookingForm, firstname: event.target.value })}
                />
              </label>

              <label>
                Last name
                <input
                  type="text"
                  required
                  value={bookingForm.lastname}
                  onChange={(event) => setBookingForm({ ...bookingForm, lastname: event.target.value })}
                />
              </label>

              <label>
                Email
                <input
                  type="email"
                  required
                  value={bookingForm.email}
                  onChange={(event) => setBookingForm({ ...bookingForm, email: event.target.value })}
                />
              </label>

              <label>
                Phone number
                <input
                  type="text"
                  required
                  value={bookingForm.phoneNumber}
                  onChange={(event) => setBookingForm({ ...bookingForm, phoneNumber: event.target.value })}
                />
              </label>

              <label className="wide-field">
                Service address
                <input
                  type="text"
                  required
                  value={bookingForm.address}
                  onChange={(event) => setBookingForm({ ...bookingForm, address: event.target.value })}
                />
              </label>

              <label className="wide-field">
                Additional details
                <textarea
                  placeholder="Add notes about the issue, preferred time, or exact location."
                  value={bookingForm.notes}
                  onChange={(event) => setBookingForm({ ...bookingForm, notes: event.target.value })}
                />
              </label>

              <div className="form-actions">
                <button className="secondary-inline" type="button" onClick={closeBookingModal}>
                  Cancel
                </button>
                <button className="primary-action" type="submit">
                  Send booking request
                </button>
              </div>
            </form>
          </section>
        </div>
      )}
    </main>
  );
};

export default Dashboard;
