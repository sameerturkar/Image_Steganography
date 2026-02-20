import React from 'react';
import { BrowserRouter as Router, Routes, Route, NavLink } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import EncodePage from './pages/EncodePage';
import DecodePage from './pages/DecodePage';
import GalleryPage from './pages/GalleryPage';
import DashboardPage from './pages/DashboardPage';
import './App.css';

function App() {
  return (
    <Router>
      <div className="app">
        <nav className="navbar">
          <div className="nav-brand">
            <span className="brand-icon">🔐</span>
            <span className="brand-text">SteganoGraphy</span>
          </div>
          <div className="nav-links">
            <NavLink to="/" end className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
              Dashboard
            </NavLink>
            <NavLink to="/encode" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
              Encode
            </NavLink>
            <NavLink to="/decode" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
              Decode
            </NavLink>
            <NavLink to="/gallery" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
              Gallery
            </NavLink>
          </div>
        </nav>

        <main className="main-content">
          <Routes>
            <Route path="/" element={<DashboardPage />} />
            <Route path="/encode" element={<EncodePage />} />
            <Route path="/decode" element={<DecodePage />} />
            <Route path="/gallery" element={<GalleryPage />} />
          </Routes>
        </main>

        <footer className="footer">
          <p>Image Steganography — LSB Technique | Build by - Sameer Turkar</p>
        </footer>
      </div>
      <ToastContainer position="top-right" autoClose={3000} theme="dark" />
    </Router>
  );
}

export default App;
