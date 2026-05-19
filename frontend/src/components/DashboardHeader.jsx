import React from "react";
import { Link } from "react-router-dom";

const DashboardHeader = ({
  user,
  providerStatus,
  searchTerm,
  onSearchChange,
  canApplyAsProvider,
  onApplyClick,
  onMyProfile,
  onLogout,
}) => {
  const initials = `${user?.firstname?.[0] || ""}${user?.lastname?.[0] || ""}` || "SL";

  return (
    <header className="dashboard-header">
      <div className="header-brand">
        <Link className="brand-mark" to="/dashboard" aria-label="Go to dashboard">
          sideL
        </Link>
      </div>

      <label className="dashboard-search" aria-label="Search services">
        <input
          type="search"
          placeholder="Search services..."
          value={searchTerm}
          onChange={(event) => onSearchChange(event.target.value)}
        />
      </label>

      <div className="header-actions">
        {canApplyAsProvider && (
          <button className="primary-action compact-action" type="button" onClick={onApplyClick}>
            Apply as provider
          </button>
        )}

        <div className="profile-menu">
          <button className="profile-summary" type="button">
            <div className="profile-avatar" aria-hidden="true">
              {initials.toUpperCase()}
            </div>
            <div>
              <strong>{user?.firstname}</strong>
              <span>{providerStatus}</span>
            </div>
          </button>

          <div className="profile-dropdown">
            <button type="button" onClick={onMyProfile}>
              My Profile
            </button>
            <Link to="/bookings">
              My Bookings
            </Link>
            <button type="button" onClick={onLogout}>
              Logout
            </button>
          </div>
        </div>
      </div>
    </header>
  );
};

export default DashboardHeader;
