import React, { useState, useEffect, useCallback } from 'react';
import { toast } from 'react-toastify';
import stegoAPI from '../services/api';

function GalleryPage() {
  const [images, setImages] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [selectedImage, setSelectedImage] = useState(null);
  const [decoding, setDecoding] = useState(null);
  const [decodedMsg, setDecodedMsg] = useState({});

  const fetchImages = useCallback(async (currentPage = 0) => {
    setLoading(true);
    try {
      let res;
      if (search.trim()) {
        res = await stegoAPI.searchImages(search.trim(), currentPage);
      } else if (statusFilter) {
        res = await stegoAPI.getByStatus(statusFilter, currentPage);
      } else {
        res = await stegoAPI.getAllImages(currentPage);
      }
      setImages(res.data?.content || []);
      setTotalPages(res.data?.totalPages || 0);
    } catch (err) {
      toast.error(err.message);
    } finally {
      setLoading(false);
    }
  }, [search, statusFilter]);

  useEffect(() => {
    setPage(0);
    fetchImages(0);
  }, [search, statusFilter, fetchImages]);

  useEffect(() => {
    fetchImages(page);
  }, [page, fetchImages]);

  const handleDecode = async (id) => {
    setDecoding(id);
    try {
      const res = await stegoAPI.decodeById(id);
      setDecodedMsg(prev => ({ ...prev, [id]: res.data.decodedMessage }));
      toast.success('Decoded!');
    } catch (err) {
      toast.error(err.message);
    } finally {
      setDecoding(null);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm(`Delete image #${id}? This cannot be undone.`)) return;
    try {
      await stegoAPI.deleteImage(id);
      toast.success('Image deleted!');
      fetchImages(page);
    } catch (err) {
      toast.error(err.message);
    }
  };

  const handleDownload = (id) => {
    const link = document.createElement('a');
    link.href = stegoAPI.downloadImage(id);
    link.download = `encoded_${id}.png`;
    link.click();
  };

  const getBadgeClass = (status) => {
    const map = { ENCODED: 'badge-encoded', DECODED: 'badge-decoded', FAILED: 'badge-failed' };
    return `badge ${map[status] || 'badge-encoded'}`;
  };

  const formatDate = (dt) => dt ? new Date(dt).toLocaleDateString('en-IN', {
    day: '2-digit', month: 'short', year: 'numeric'
  }) : '—';

  return (
    <div>
      <div className="page-header">
        <h1>🖼️ Image Gallery</h1>
        <p>Browse, decode, and manage all encoded images</p>
      </div>

      {/* Filters */}
      <div className="card" style={{ marginBottom: '1.5rem' }}>
        <div style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap', alignItems: 'flex-end' }}>
          <div className="form-group" style={{ flex: 1, minWidth: '200px', marginBottom: 0 }}>
            <label className="form-label">🔍 Search</label>
            <input
              className="form-input"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Search by filename or description..."
            />
          </div>
          <div className="form-group" style={{ minWidth: '160px', marginBottom: 0 }}>
            <label className="form-label">📊 Status</label>
            <select
              className="form-select"
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
            >
              <option value="">All</option>
              <option value="ENCODED">ENCODED</option>
              <option value="DECODED">DECODED</option>
              <option value="FAILED">FAILED</option>
            </select>
          </div>
          <button className="btn btn-secondary" onClick={() => { setSearch(''); setStatusFilter(''); }}>
            Clear
          </button>
        </div>
      </div>

      {/* Table */}
      <div className="card">
        {loading ? (
          <div className="loading-center"><div className="spinner" /> Loading images...</div>
        ) : images.length === 0 ? (
          <div className="empty-state">
            <div className="empty-state-icon">🗂️</div>
            <h3>No images found</h3>
            <p>Try changing filters or encode a new image.</p>
          </div>
        ) : (
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Original File</th>
                  <th>Status</th>
                  <th>Description</th>
                  <th>Date</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {images.map((img) => (
                  <React.Fragment key={img.id}>
                    <tr>
                      <td style={{ color: '#4fc3f7', fontWeight: 600 }}>#{img.id}</td>
                      <td style={{ maxWidth: '160px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                        {img.originalFileName}
                      </td>
                      <td>
                        <span className={getBadgeClass(img.status)}>{img.status}</span>
                      </td>
                      <td style={{ color: '#6b8aad', fontSize: '0.85rem', maxWidth: '150px', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                        {img.description || '—'}
                      </td>
                      <td style={{ color: '#6b8aad', fontSize: '0.85rem' }}>{formatDate(img.createdAt)}</td>
                      <td>
                        <div style={{ display: 'flex', gap: '0.4rem', flexWrap: 'wrap' }}>
                          <button
                            className="btn btn-success btn-sm"
                            onClick={() => handleDecode(img.id)}
                            disabled={decoding === img.id}
                          >
                            {decoding === img.id ? <span className="spinner" /> : '🔓'}
                          </button>
                          <button className="btn btn-secondary btn-sm" onClick={() => handleDownload(img.id)}>
                            ⬇️
                          </button>
                          <button
                            className="btn btn-secondary btn-sm"
                            onClick={() => setSelectedImage(selectedImage?.id === img.id ? null : img)}
                          >
                            👁️
                          </button>
                          <button className="btn btn-danger btn-sm" onClick={() => handleDelete(img.id)}>
                            🗑️
                          </button>
                        </div>
                      </td>
                    </tr>
                    {/* Expanded row */}
                    {selectedImage?.id === img.id && (
                      <tr>
                        <td colSpan={6} style={{ background: 'rgba(79,195,247,0.03)' }}>
                          <div style={{ padding: '1rem' }}>
                            <div style={{ marginBottom: '0.5rem', color: '#6b8aad', fontSize: '0.85rem' }}>
                              Encoded File: <span style={{ color: '#e0e6f0' }}>{img.encodedFileName}</span>
                            </div>
                            {decodedMsg[img.id] && (
                              <div>
                                <div style={{ color: '#6b8aad', fontSize: '0.85rem', marginBottom: '0.3rem' }}>Hidden Message:</div>
                                <div className="decoded-message">{decodedMsg[img.id]}</div>
                              </div>
                            )}
                          </div>
                        </td>
                      </tr>
                    )}
                  </React.Fragment>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* Pagination */}
        {totalPages > 1 && (
          <div className="pagination">
            <button className="page-btn" disabled={page === 0} onClick={() => setPage(0)}>«</button>
            <button className="page-btn" disabled={page === 0} onClick={() => setPage(p => p - 1)}>‹</button>
            {Array.from({ length: totalPages }, (_, i) => (
              <button
                key={i}
                className={`page-btn ${i === page ? 'current' : ''}`}
                onClick={() => setPage(i)}
              >
                {i + 1}
              </button>
            ))}
            <button className="page-btn" disabled={page === totalPages - 1} onClick={() => setPage(p => p + 1)}>›</button>
            <button className="page-btn" disabled={page === totalPages - 1} onClick={() => setPage(totalPages - 1)}>»</button>
          </div>
        )}
      </div>
    </div>
  );
}

export default GalleryPage;
