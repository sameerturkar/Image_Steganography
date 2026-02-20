import React, { useState, useCallback } from 'react';
import { useDropzone } from 'react-dropzone';
import { toast } from 'react-toastify';
import stegoAPI from '../services/api';

function EncodePage() {
  const [file, setFile] = useState(null);
  const [preview, setPreview] = useState(null);
  const [message, setMessage] = useState('');
  const [description, setDescription] = useState('');
  const [loading, setLoading] = useState(false);
  const [capacity, setCapacity] = useState(null);
  const [result, setResult] = useState(null);

  const onDrop = useCallback(async (accepted) => {
    const f = accepted[0];
    if (!f) return;
    setFile(f);
    setPreview(URL.createObjectURL(f));
    setResult(null);
    setCapacity(null);

    // Check capacity
    try {
      const res = await stegoAPI.getCapacity(f);
      setCapacity(res.data);
      toast.info(`This image can hide up to ${res.data} characters.`);
    } catch {
      // ignore
    }
  }, []);

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: { 'image/*': ['.png', '.jpg', '.jpeg', '.bmp'] },
    maxSize: 10 * 1024 * 1024,
    multiple: false,
  });

  const handleEncode = async (e) => {
    e.preventDefault();
    if (!file) { toast.error('Please select an image.'); return; }
    if (!message.trim()) { toast.error('Please enter a message.'); return; }
    if (capacity && message.length > capacity) {
      toast.error(`Message is too long! Max ${capacity} characters for this image.`);
      return;
    }

    setLoading(true);
    try {
      const res = await stegoAPI.encodeImage(file, message.trim(), description);
      setResult(res.data);
      toast.success('✅ Message encoded successfully!');
    } catch (err) {
      toast.error(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleDownload = () => {
    if (!result) return;
    const link = document.createElement('a');
    link.href = stegoAPI.downloadImage(result.id);
    link.download = `encoded_${result.id}.png`;
    link.click();
  };

  const handleReset = () => {
    setFile(null);
    setPreview(null);
    setMessage('');
    setDescription('');
    setResult(null);
    setCapacity(null);
  };

  return (
    <div>
      <div className="page-header">
        <h1>🔒 Encode Message</h1>
        <p>Hide a secret message inside an image using LSB steganography</p>
      </div>

      <div className="grid-2">
        {/* Left: Form */}
        <div>
          <div className="card">
            <div className="card-title">📁 Upload Image</div>
            <div {...getRootProps()} className={`dropzone ${isDragActive ? 'active' : ''}`}>
              <input {...getInputProps()} />
              <div className="dropzone-icon">🖼️</div>
              {isDragActive ? (
                <p className="dropzone-text">Drop the image here!</p>
              ) : (
                <p className="dropzone-text">
                  <strong>Click to upload</strong> or drag & drop<br />
                  PNG, JPG, BMP (max 10MB)
                </p>
              )}
            </div>

            {preview && (
              <div className="image-preview" style={{ marginTop: '1rem' }}>
                <img src={preview} alt="Preview" />
              </div>
            )}

            {capacity !== null && (
              <div className="alert alert-info" style={{ marginTop: '0.8rem' }}>
                📊 Max capacity: <strong>{capacity.toLocaleString()} characters</strong>
              </div>
            )}
          </div>

          <div className="card" style={{ marginTop: '1.2rem' }}>
            <div className="card-title">✍️ Secret Message</div>
            <form onSubmit={handleEncode}>
              <div className="form-group">
                <label className="form-label">
                  Message to Hide *
                  {capacity && (
                    <span style={{ float: 'right', color: message.length > capacity ? '#ef5350' : '#4fc3f7' }}>
                      {message.length}/{capacity}
                    </span>
                  )}
                </label>
                <textarea
                  className="form-textarea"
                  value={message}
                  onChange={(e) => setMessage(e.target.value)}
                  placeholder="Enter your secret message here..."
                  rows={5}
                />
              </div>

              <div className="form-group">
                <label className="form-label">Description (optional)</label>
                <input
                  className="form-input"
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  placeholder="Add a description for this image..."
                />
              </div>

              <div style={{ display: 'flex', gap: '0.8rem' }}>
                <button className="btn btn-primary" type="submit" disabled={loading}>
                  {loading ? <><span className="spinner" /> Encoding...</> : '🔒 Encode'}
                </button>
                <button className="btn btn-secondary" type="button" onClick={handleReset}>
                  Reset
                </button>
              </div>
            </form>
          </div>
        </div>

        {/* Right: Result */}
        <div>
          {!result ? (
            <div className="card">
              <div className="card-title">📋 Instructions</div>
              <ol style={{ color: '#6b8aad', fontSize: '0.9rem', lineHeight: '2', paddingLeft: '1.2rem' }}>
                <li>Upload an image (PNG recommended)</li>
                <li>Check the maximum message capacity</li>
                <li>Type your secret message</li>
                <li>Click Encode to hide the message</li>
                <li>Download the encoded image</li>
                <li>Share the image — the message is invisible!</li>
              </ol>
              <div className="alert alert-warning" style={{ marginTop: '1rem' }}>
                ⚠️ Always use PNG output for lossless storage. JPEG compression will destroy the hidden data.
              </div>
            </div>
          ) : (
            <div className="card">
              <div className="card-title" style={{ color: '#66bb6a' }}>✅ Encoding Successful!</div>

              <div style={{ marginBottom: '1rem' }}>
                <div style={{ color: '#6b8aad', fontSize: '0.85rem', marginBottom: '0.3rem' }}>Image ID</div>
                <div style={{ fontSize: '1.1rem', color: '#4fc3f7', fontWeight: '600' }}>#{result.id}</div>
              </div>

              <div style={{ marginBottom: '1rem' }}>
                <div style={{ color: '#6b8aad', fontSize: '0.85rem', marginBottom: '0.3rem' }}>Original File</div>
                <div>{result.originalFileName}</div>
              </div>

              <div style={{ marginBottom: '1rem' }}>
                <div style={{ color: '#6b8aad', fontSize: '0.85rem', marginBottom: '0.3rem' }}>Encoded File</div>
                <div style={{ color: '#66bb6a' }}>{result.encodedFileName}</div>
              </div>

              <div style={{ marginBottom: '1rem' }}>
                <div style={{ color: '#6b8aad', fontSize: '0.85rem', marginBottom: '0.3rem' }}>Status</div>
                <span className="badge badge-encoded">{result.status}</span>
              </div>

              <div style={{ marginBottom: '1rem' }}>
                <div style={{ color: '#6b8aad', fontSize: '0.85rem', marginBottom: '0.3rem' }}>Hidden Message Preview</div>
                <div className="decoded-message">
                  {result.hiddenMessage.substring(0, 100)}{result.hiddenMessage.length > 100 ? '...' : ''}
                </div>
              </div>

              <div style={{ display: 'flex', gap: '0.8rem', flexWrap: 'wrap' }}>
                <button className="btn btn-success" onClick={handleDownload}>
                  ⬇️ Download Encoded Image
                </button>
                <button className="btn btn-secondary" onClick={handleReset}>
                  Encode Another
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default EncodePage;
