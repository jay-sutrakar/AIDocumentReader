import React, { useState, useCallback, useRef } from 'react';
import { uploadDocumentsStart, uploadDocumentsSuccess, uploadDocumentsFailure } from '../redux/documentsSlice';
import { v4 as uuidv4 } from 'uuid';
import "../css/DocumentUpload.css"
import {useDispatch} from "react-redux";

const DocumentUpload = ({ onUpload, maxSizeMB = 5, allowedTypes = ['.pdf', '.doc', '.docx'] }) => {
    const dispatch = useDispatch();
    const [files, setFiles] = useState([]);
    const [uploading, setUploading] = useState(false);
    const fileInputRef = useRef(null);

    const isValidFile = useCallback((file) => {
        const typeValid = allowedTypes.some(t => file.name.toLowerCase().endsWith(t.toLowerCase()));
        const sizeValid = file.size <= maxSizeMB * 1024 * 1024;
        return typeValid && sizeValid;
    }, [allowedTypes, maxSizeMB]);

    const handleFiles = (newFiles) => {
        const validFiles = Array.from(newFiles).filter(isValidFile);
        setFiles(prev => [...prev, ...validFiles]);
    };

    const handleDragOver = (e) => e.preventDefault();

    const handleDrop = (e) => {
        e.preventDefault();
        handleFiles(e.dataTransfer.files);
    };

    const handleChange = (e) => {
        if (e.target.files) handleFiles(e.target.files);
    };

    const removeFile = (index) => {
        setFiles(prev => prev.filter((_, i) => i !== index));
    };

    const uploadFiles = async () => {
        if (!files.length || uploading) return;
        setUploading(true);
        const formData = new FormData();
        files.forEach(file => formData.append('file', file));

        try {
            const response = await fetch('http://localhost:7070/api/document/upload', {  // Replace with your backend endpoint
                method: 'POST',
                body: formData,
                headers: {
                    'userId' : uuidv4(),
                }
            });
            if (response.ok) {
                onUpload?.(await response.json());
                dispatch(uploadDocumentsSuccess(
                    response.map(data => ({
                        id: uuidv4(),
                        // name: data.name || files[0].name,
                        // size: data.size || files[0].size,
                        // url: data.url,  // Backend returns this
                        uploadedAt: new Date().toISOString(),
                    }))
                ));
                setFiles([]);
            }
        } catch (error) {
            console.error('Upload failed:', error);
            dispatch(uploadDocumentsFailure());
        } finally {
            setUploading(false);
        }
    };

    return (
        <div className="document-upload">
            <div
                className="drop-zone"
                onDragOver={handleDragOver}
                onDrop={handleDrop}
                onClick={() => fileInputRef.current?.click()}
            >
                <p>Drag & drop documents here or click to browse</p>
                <input
                    ref={fileInputRef}
                    type="file"
                    accept={allowedTypes.join(',')}
                    onChange={handleChange}
                    style={{ display: 'none' }}
                />
            </div>
            {files.length > 0 && (
                <div className="file-list">
                    {files.map((file, index) => (
                        <div key={index} className="file-item">
                            <span>{file.name} ({(file.size / 1024 / 1024).toFixed(2)} MB)</span>
                            <button onClick={() => removeFile(index)}>Remove</button>
                        </div>
                    ))}
                    <button onClick={uploadFiles} disabled={uploading}>
                        {uploading ? 'Uploading...' : `Upload ${files.length} file(s)`}
                    </button>
                </div>
            )}
        </div>
    );
};

export default DocumentUpload;
