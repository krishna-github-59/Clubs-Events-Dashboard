import { jwtDecode } from "jwt-decode";

export const getLoggedInUser = () => {
  const token = localStorage.getItem('token');
  if (!token) return null;

  try {
    return jwtDecode(token); // 👈 returns decoded payload
  } catch (e) {
    console.error('Invalid token');
    return null;
  }
};


export const logout = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("user");

  // Full reload + history replace
  window.location.replace("/login");
};

