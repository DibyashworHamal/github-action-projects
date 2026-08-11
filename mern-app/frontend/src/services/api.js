// frontend/src/services/api.js
const API_URL = import.meta.env.VITE_API_URL || "http://localhost:5000/api";
//                                                                      
export const api = {
  // Videos
  getVideos: async (query = "") => {
    const url = query
      ? `${API_URL}/videos?q=${encodeURIComponent(query)}`
      : `${API_URL}/videos`;
    const res = await fetch(url);
    if (!res.ok) throw new Error("Failed to fetch videos");
    return res.json();
  },

  getVideoById: async (id) => {
    const res = await fetch(`${API_URL}/videos/${id}`);
    if (!res.ok) throw new Error("Failed to fetch video");
    return res.json();
  },

  likeVideo: async (id) => {
    const token = localStorage.getItem("token");
    const res = await fetch(`${API_URL}/videos/${id}/like`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
    });
    if (!res.ok) throw new Error("Failed to like video");
    return res.json();
  },

  // Comments
  getComments: async (videoId) => {
    const res = await fetch(`${API_URL}/comments/${videoId}`);
    if (!res.ok) throw new Error("Failed to fetch comments");
    return res.json();
  },

  addComment: async (videoId, text) => {
    const token = localStorage.getItem("token");
    if (!token) throw new Error("Not authenticated");

    const res = await fetch(`${API_URL}/comments`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ videoId, text }),
    });
    if (!res.ok) throw new Error("Failed to add comment");
    return res.json();
  },

  // Auth
  register: async (username, email, password) => {
    const res = await fetch(`${API_URL}/auth/register`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username, email, password }),
    });
    if (!res.ok) throw new Error("Registration failed");
    const data = await res.json();
    localStorage.setItem("token", data.token);
    localStorage.setItem("username", data.user.username);
    localStorage.setItem("userId", data.user.id);
    return data;
  },

  login: async (email, password) => {
    const res = await fetch(`${API_URL}/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password }),
    });
    if (!res.ok) throw new Error("Login failed");
    const data = await res.json();
    localStorage.setItem("token", data.token);
    localStorage.setItem("username", data.user.username);
    localStorage.setItem("userId", data.user.id);
    return data;
  },

  logout: () => {
    localStorage.removeItem("token");
    localStorage.removeItem("username");
    localStorage.removeItem("userId");
  },

  getProfile: async () => {
    const token = localStorage.getItem("token");
    if (!token) throw new Error("Not authenticated");

    const res = await fetch(`${API_URL}/auth/profile`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!res.ok) throw new Error("Failed to fetch profile");
    return res.json();
  },
};
