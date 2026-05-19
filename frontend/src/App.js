import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import SignupPage from "./components/signuppage";
import LoginPage from "./components/loginpage";
import Dashboard from "./components/dashboard";
import AdminLogin from "./components/AdminLogin";
import AdminDashboard from "./components/AdminDashboard";
import MyProfile from "./components/MyProfile";
import ClientBookings from "./components/ClientBookings";
import './App.css';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/signup" element={<SignupPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/dashboard" element={<Dashboard/>} />
        <Route path="/profile" element={<MyProfile />} />
        <Route path="/bookings" element={<ClientBookings />} />
        <Route path="/admin/login" element={<AdminLogin />} />
        <Route path="/admin/dashboard" element={<AdminDashboard />} />
      </Routes>
    </Router>
  );
}

export default App;
