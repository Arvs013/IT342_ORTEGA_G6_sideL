import React from "react";

const DashboardHeader = ({
  user,
  providerStatus,
  searchTerm,
  onSearchChange,
  canApplyAsProvider,
  onApplyClick,
}) => {
  const initials = `${user?.firstname?.[0] || ""}${user?.lastname?.[0] || ""}` || "SL";

  return (
    <header className="dashboard-header">
      <div className="header-brand">
        <div className="brand-mark" aria-label="sideL logo">
          sideL
        </div>
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

        <div className="profile-summary">
          <div className="profile-avatar" aria-hidden="true">
            {initials.toUpperCase()}
          </div>
          <div>
            <strong>{user?.firstname}</strong>
            <span>{providerStatus}</span>
          </div>
        </div>
      </div>
    </header>
  );
};

export default DashboardHeader;
