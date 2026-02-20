import React, { useState, useCallback } from 'react';
import { useDropzone } from 'react-dropzone';
import { toast } from 'react-toastify';
import stegoAPI from '../services/api';

function DecodePage() {
  const [mode, setMode] = useState('upload'); // 'upload' or 'id'
  const [file, setFile] = useState(null);
  const [preview, setPreview] = useState(null);
  const [imageId, setImageId] = useState('');
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);

  const onDrop = useCallback((accepted) => {
    const f = accepted[0];
    if (!f) return;
    setFile(f);
    setPreview(URL.createObjectURL(f));
    setResult(null);
  }, []);

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: { 'image/*': ['.png', '.jpg', '.jpeg', '.bmp'] },
    maxSize: 10 * 1024 * 1024,
    multiple: false,
  });

  const handleDecode = async (e) => {
    e.preventDefault();
    setLoading(true);
    setResult(null);

    try {
      let res;
      if (mode === 'upload') {
        if (!file) { toast.error('Please select an image.'); setLoading(false); return; }
        res = await stegoAPI.decodeUploadedImage(file);
      } else {
        if (!imageId) { toast.error('Please enter an image ID.'); setLoading(false); return; }
        res = await stegoAPI.decodeById(parseInt(imageId));
      }
      setResult(res.data);
      toast.success('✅ Message decoded successfully!');
    } catch (err) {
      toast.error(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleReset = () => {
    setFile(null);
    setPreview(null);
    setImageId('');
    setResult(null);
  };

  const copyToClipboard = () => {
    if (result?.decodedMessage) {
      navigator.clipboard.writeText(result.decodedMessage);
      toast.success('Copied to clipboard!');
    }
  };

  return (
    <div>
      <div className="page-header">
        <h1>🔓 Decode Message</h1>
        <p>Extract hidden messages from steganographic images</p>
      </div>

      <div className="grid-2">
        {/* Left: Form */}
        <div>
          <div className="card">
            <div className="card-title">⚙️ Decode Method</div>
            <div style={{ display: 'flex', gap: '0.8rem', marginBottom: '1.5rem' }}>
              <button
                className={`btn ${mode === 'upload' ? 'btn-primary' : 'btn-secondary'}`}
                onClick={() => { setMode('upload'); setResult(null); }}
              >
                📤 Upload Image
              </button>
              <button
                className={`btn ${mode === 'id' ? 'btn-primary' : 'btn-secondary'}`}
                onClick={() => { setMode('id'); setResult(null); }}
              >
                🆔 Use Image ID
              </button>
            </div>

            {mode === 'upload' ? (
              <>
                <div {...getRootProps()} className={`dropzone ${isDragActive ? 'active' : ''}`}>
                  <input {...getInputProps()} />
                  <div className="dropzone-icon">🔍</div>
                  {isDragActive ? (
                    <p className="dropzone-text">Drop the encoded image here!</p>
                  ) : (
                    <p className="dropzone-text">
                      <strong>Click to upload</strong> the encoded image<br />
                      PNG, JPG, BMP (max 10MB)
                    </p>
                  )}
                </div>

                {preview && (
                  <div className="image-preview" style={{ marginTop: '1rem' }}>
                    <img src={preview} alt="Preview" />
                  </div>
                )}
              </>
            ) : (
              <div className="form-group">
                <label className="form-label">Image ID (from Gallery or encode result)</label>
                <input
                  className="form-input"
                  type="number"
                  value={imageId}
                  onChange={(e) => setImageId(e.target.value)}
                  placeholder="Enter image ID e.g. 1"
                  min="1"
                />
              </div>
            )}

            <div style={{ display: 'flex', gap: '0.8rem', marginTop: '1rem' }}>
              <button className="btn btn-success" onClick={handleDecode} disabled={loading}>
                {loading ? <><span className="spinner" /> Decoding...</> : '🔓 Decode Message'}
              </button>
              <button className="btn btn-secondary" onClick={handleReset}>
                Reset
              </button>
            </div>
          </div>
        </div>

        {/* Right: Result */}
        <div>
          {!result ? (
            <div className="card">
              <div className="card-title">📋 How to Decode</div>
              <ol style={{ color: '#6b8aad', fontSize: '0.9rem', lineHeight: '2', paddingLeft: '1.2rem' }}>
                <li>Choose a decode method (upload or ID)</li>
                <li>If uploading, select the encoded PNG image</li>
                <li>If using ID, find it in the Gallery</li>
                <li>Click "Decode Message"</li>
                <li>The hidden message will appear here</li>
              </ol>
              <div className="alert alert-warning" style={{ marginTop: '1rem' }}>
                ⚠️ Only images encoded with this tool can be decoded. The image must not be re-compressed (use PNG).
              </div>
            </div>
          ) : (
            <div className="card">
              <div className="card-title" style={{ color: '#66bb6a' }}>✅ Message Decoded!</div>

              {result.imageId && (
                <div style={{ marginBottom: '0.8rem' }}>
                  <span style={{ color: '#6b8aad', fontSize: '0.85rem' }}>Image ID: </span>
                  <span style={{ color: '#4fc3f7' }}>#{result.imageId}</span>
                </div>
              )}

              <div style={{ marginBottom: '0.8rem' }}>
                <span style={{ color: '#6b8aad', fontSize: '0.85rem' }}>Source File: </span>
                <span>{result.originalFileName}</span>
              </div>

              <div style={{ marginBottom: '0.8rem' }}>
                <span style={{ color: '#6b8aad', fontSize: '0.85rem' }}>Decoded At: </span>
                <span style={{ color: '#6b8aad', fontSize: '0.85rem' }}>
                  {new Date(result.decodedAt).toLocaleString()}
                </span>
              </div>

              <div style={{ marginBottom: '1rem' }}>
                <div style={{ color: '#6b8aad', fontSize: '0.85rem', marginBottom: '0.5rem' }}>
                  🔐 Hidden Message ({result.decodedMessage?.length} characters):
                </div>
                <div className="decoded-message">{result.decodedMessage}</div>
              </div>

              <div style={{ display: 'flex', gap: '0.8rem' }}>
                <button className="btn btn-primary btn-sm" onClick={copyToClipboard}>
                  📋 Copy Message
                </button>
                <button className="btn btn-secondary btn-sm" onClick={handleReset}>
                  Decode Another
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default DecodePage;
