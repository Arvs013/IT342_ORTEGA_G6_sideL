export const storeAuthSession = (data) => {
  const user = data?.user || data;

  if (data?.token) {
    localStorage.setItem("authToken", data.token);
  }

  localStorage.setItem("loggedUser", JSON.stringify(user));
  return user;
};

export const getAuthHeaders = (headers = {}) => {
  const token = localStorage.getItem("authToken");
  return {
    ...headers,
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };
};

export const clearAuthSession = () => {
  localStorage.removeItem("loggedUser");
  localStorage.removeItem("authToken");
};
