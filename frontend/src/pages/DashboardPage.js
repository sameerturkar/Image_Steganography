import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import stegoAPI from '../services/api';

function DashboardPage() {
  const [stats, setStats] = useState(null);
  const [latest, setLatest] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    Promise.all([stegoAPI.getStats(), stegoAPI.getLatest()])
      .then(([statsRes, latestRes]) => {
        setStats(statsRes.data);
        setLatest(latestRes.data || []);
      })
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  const getBadgeClass = (status) => {
    const map = { ENCODED: 'badge-encoded', DECODED: 'badge-decoded', FAILED: 'badge-failed' };
    return `badge ${map[status] || 'badge-encoded'}`;
  };

  if (loading) {
    return (
      <div className="loading-center">
        <div className="spinner" /> Loading dashboard...
      </div>
    );
  }

  return (
    <div>
      <div className="page-header">
        <h1>🏠 Dashboard</h1>
        <p>Overview of your steganography operations</p>
      </div>

      {stats && (
        <div className="grid-4" style={{ marginBottom: '2rem' }}>
          <div className="stat-card">
            <div className="stat-value">{stats.totalImages}</div>
            <div className="stat-label">Total Images</div>
          </div>
          <div className="stat-card">
            <div className="stat-value" style={{ color: '#66bb6a' }}>{stats.encodedCount}</div>
            <div className="stat-label">Encoded</div>
          </div>
          <div className="stat-card">
            <div className="stat-value" style={{ color: '#4fc3f7' }}>{stats.decodedCount}</div>
            <div className="stat-label">Decoded</div>
          </div>
          <div className="stat-card">
            <div className="stat-value" style={{ color: '#ef5350' }}>{stats.failedCount}</div>
            <div className="stat-label">Failed</div>
          </div>
        </div>
      )}

      <div className="grid-2" style={{ marginBottom: '2rem' }}>
        <div className="card" style={{ cursor: 'pointer' }} onClick={() => navigate('/encode')}>
          <div className="card-title">🔒 Encode a Message</div>
          <p style={{ color: '#6b8aad', fontSize: '0.9rem' }}>
            Hide a secret message inside any image using the LSB algorithm. 
            Supports PNG, JPG, and BMP formats.
          </p>
          <button className="btn btn-primary" style={{ marginTop: '1rem' }}>
            Start Encoding →
          </button>
        </div>
        <div className="card" style={{ cursor: 'pointer' }} onClick={() => navigate('/decode')}>
          <div className="card-title">🔓 Decode a Message</div>
          <p style={{ color: '#6b8aad', fontSize: '0.9rem' }}>
            Extract a hidden message from a previously encoded image. 
            Upload the image or use a saved record.
          </p>
          <button className="btn btn-success" style={{ marginTop: '1rem' }}>
            Start Decoding →
          </button>
        </div>
      </div>

      <div className="card">
        <div className="card-title">📋 Recent Activity</div>
        {latest.length === 0 ? (
          <div className="empty-state">
            <div className="empty-state-icon">📭</div>
            <h3>No images yet</h3>
            <p>Encode your first image to get started!</p>
          </div>
        ) : (
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>File Name</th>
                  <th>Status</th>
                  <th>Description</th>
                  <th>Date</th>
                </tr>
              </thead>
              <tbody>
                {latest.map((img) => (
                  <tr key={img.id}>
                    <td>#{img.id}</td>
                    <td style={{ maxWidth: '200px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                      {img.originalFileName}
                    </td>
                    <td>
                      <span className={getBadgeClass(img.status)}>{img.status}</span>
                    </td>
                    <td style={{ color: '#6b8aad', fontSize: '0.85rem' }}>
                      {img.description || '—'}
                    </td>
                    <td style={{ color: '#6b8aad', fontSize: '0.85rem' }}>
                      {new Date(img.createdAt).toLocaleDateString()}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      <div className="card" style={{ marginTop: '1.5rem' }}>
        <div className="card-title">ℹ️ How LSB Steganography Works</div>
        <div style={{ color: '#6b8aad', fontSize: '0.9rem', lineHeight: '1.7' }}>
          <p>
            <strong style={{ color: '#e0e6f0' }}>LSB (Least Significant Bit)</strong> steganography 
            works by replacing the last bit of each color channel (R, G, B) in each pixel with a bit 
            of the secret message.
          </p>
          <br />
          <p>
            Since changing only the last bit of a byte (0–255) results in a difference of just ±1, 
            the visual change to the image is imperceptible to the human eye.
          </p>
          <br />
          <p>
            For a 100×100 pixel image: 10,000 pixels × 3 channels = <strong style={{ color: '#4fc3f7' }}>30,000 bits = 3,750 characters</strong> of hidden data.
          </p>
        </div>
      </div>
    </div>
  );
}

export default DashboardPage;
