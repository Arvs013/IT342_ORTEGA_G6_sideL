import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import "../styles/landingpage.css";
const SERVICES = [
  { icon: "💻", name: "Laptop & PC Repair", desc: "Screen replacement, hardware upgrades, virus removal, and diagnostics." },
  { icon: "📱", name: "Phone Repair", desc: "Cracked screens, battery replacements, charging ports and water damage." },
  { icon: "🧊", name: "Appliance Repair", desc: "Refrigerators, washing machines, air conditioners, and more." },
  { icon: "📺", name: "TV & Electronics", desc: "Smart TV setup, panel replacements, and audio system repairs." },
  { icon: "🔌", name: "Electrical Services", desc: "Wiring, outlets, breaker panels, and home electrical diagnostics." },
  { icon: "🚿", name: "Plumbing", desc: "Leak fixes, pipe installations, faucet repairs, and drain cleaning." },
];

const STEPS = [
  { n: "01", title: "Find a Service", desc: "Browse verified shops and technicians near your location." },
  { n: "02", title: "Book Anytime", desc: "Choose your preferred schedule — same day or in advance." },
  { n: "03", title: "Get It Fixed", desc: "A certified technician arrives and resolves the issue." },
  { n: "04", title: "Pay & Review", desc: "Secure payment and leave a review to help the community." },
];

const REVIEWS = [
  { stars: 5, text: "Found a technician within 30 minutes. My laptop screen was fixed the same day!", name: "Maria T.", loc: "Cebu City" },
  { stars: 5, text: "Super convenient. Booked an AC repair through sideL and the guy was professional and fast.", name: "Jerico V.", loc: "Mandaue" },
  { stars: 5, text: "Love how easy it is to check reviews before choosing a shop. Highly recommended!", name: "Anika R.", loc: "Oslob" },
];

const STATS = [
  { num: "12K+", label: "Repairs Completed" },
  { num: "850+", label: "Verified Technicians" },
  { num: "4.9★", label: "Average Rating" },
  { num: "98%", label: "Satisfaction Rate" },
];

export default function LandingPage() {
  const [scrolled, setScrolled] = useState(false);

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 40);
    window.addEventListener("scroll", onScroll);
    return () => window.removeEventListener("scroll", onScroll);
  }, []);

  return (
    <>
<div className="lp-root">

        {/* NAV */}
        <nav className="lp-nav" style={scrolled ? { boxShadow: "0 4px 40px rgba(0,0,0,0.4)" } : {}}>
          <div className="lp-logo">side<span>L</span></div>
          <ul className="lp-nav-links">
            {["Home","Services","About us","Reviews","Contact"].map(n => (
              <li key={n}><a href={`#${n.toLowerCase().replace(" ","")}`}>{n}</a></li>
            ))}
          </ul>
          <div className="lp-nav-actions">
            <Link to="/login" className="btn-teal">Login</Link>
            <Link to="/signup" className="btn-ghost">Sign up</Link>
          </div>
        </nav>

        {/* HERO */}
        <section className="lp-hero" id="home">
          <div className="lp-hero-bg-grid" />
          <div className="lp-hero-inner">
            <div className="lp-hero-badge"><span className="lp-hero-badge-dot" />Now live in Cebu &amp; Visayas</div>
            <h1 className="lp-hero-title">
              Fast &amp; Trusted<br />
              Repair Services<br />
              at Your <em>Fingertips</em>
            </h1>
            <p className="lp-hero-sub">
              Find verified shops and technicians near you.<br />Book anytime, anywhere with sideL.
            </p>
            <div className="lp-hero-cta">
              <Link to="/signup" className="btn-hero">Book Now</Link>
              <a href="#services" className="btn-hero-outline">Explore Services</a>
            </div>
            <div className="lp-devices">
              {[["📱","Phones"],["💻","Laptops"],["🧊","Appliances"],["📺","Electronics"],["🔌","Electrical"]].map(([icon,name]) => (
                <div className="lp-device-chip" key={name}><span>{icon}</span>{name}</div>
              ))}
            </div>
          </div>
        </section>

        {/* STATS */}
        <div className="lp-stats">
          {STATS.map((s, i) => (
            <div className="lp-stat" key={s.label} style={{ animationDelay: `${0.1 * i}s` }}>
              <div className="lp-stat-num">{s.num}</div>
              <div className="lp-stat-label">{s.label}</div>
            </div>
          ))}
        </div>

        {/* SERVICES */}
        <section id="services" style={{ background: "var(--dark)" }}>
          <div className="lp-section">
            <div className="lp-section-label">What we offer</div>
            <h2 className="lp-section-title">Every repair,<br />one platform.</h2>
            <p className="lp-section-sub">From smartphones to home appliances, sideL connects you with certified professionals.</p>
            <div className="lp-services-grid">
              {SERVICES.map(s => (
                <div className="lp-service-card" key={s.name}>
                  <span className="lp-service-icon">{s.icon}</span>
                  <div className="lp-service-name">{s.name}</div>
                  <div className="lp-service-desc">{s.desc}</div>
                </div>
              ))}
            </div>
          </div>
        </section>

        {/* HOW IT WORKS */}
        <section id="aboutus" style={{ background: "rgba(255,255,255,0.015)", borderTop: "1px solid var(--border)" }}>
          <div className="lp-section">
            <div className="lp-section-label">How it works</div>
            <h2 className="lp-section-title">Repair in<br />4 easy steps.</h2>
            <p className="lp-section-sub">Getting your device or appliance fixed has never been simpler.</p>
            <div className="lp-steps">
              {STEPS.map(s => (
                <div className="lp-step" key={s.n}>
                  <div className="lp-step-num">{s.n}</div>
                  <div className="lp-step-title">{s.title}</div>
                  <div className="lp-step-desc">{s.desc}</div>
                </div>
              ))}
            </div>
          </div>
        </section>

        {/* REVIEWS */}
        <section id="reviews" className="lp-reviews">
          <div className="lp-reviews-inner">
            <div className="lp-section-label">Customer reviews</div>
            <h2 className="lp-section-title">Trusted by thousands.</h2>
            <div className="lp-reviews-grid">
              {REVIEWS.map(r => (
                <div className="lp-review-card" key={r.name}>
                  <div className="lp-review-stars">{"★".repeat(r.stars)}</div>
                  <div className="lp-review-text">"{r.text}"</div>
                  <div className="lp-review-author">
                    <div className="lp-review-avatar">{r.name[0]}</div>
                    <div>
                      <div className="lp-review-name">{r.name}</div>
                      <div className="lp-review-loc">{r.loc}</div>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </section>

        {/* CTA */}
        <section id="contact" className="lp-cta-section">
          <div className="lp-cta-inner">
            <h2 className="lp-cta-title">Ready to get your<br />device fixed today?</h2>
            <p className="lp-cta-sub">Join thousands of satisfied customers. Book a certified technician near you in minutes.</p>
            <Link to="/signup" className="btn-hero" style={{ fontSize: "1.05rem", padding: "0.95rem 2.8rem" }}>
              Get Started — It's Free
            </Link>
          </div>
        </section>

        {/* FOOTER */}
        <footer className="lp-footer">
          <div className="lp-footer-copy">© 2025 sideL. All rights reserved.</div>
          <div className="lp-footer-links">
            <button type="button">Privacy Policy</button>
            <button type="button">Terms of Service</button>
            <a href="#contact">Contact</a>
          </div>
        </footer>

      </div>
    </>
  );
}

