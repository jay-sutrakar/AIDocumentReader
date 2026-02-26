import React, { useState, useCallback, useRef } from 'react';
import { uploadDocumentsStart, uploadDocumentsSuccess, uploadDocumentsFailure } from '../redux/documentsSlice';
import { v4 as uuidv4 } from 'uuid';
import {useDispatch, useSelector} from "react-redux";
import { UploadCloud, X, Loader2 } from 'lucide-react';  // npm i lucide-react

const DocumentUpload = ({ onUpload, maxSizeMB = 5, allowedTypes = ['.pdf'] }) => {
    const dispatch = useDispatch();
    const {userId, isUserLoggedIn} = useSelector((state) => state.auth);
    const documents = useSelector((state) => state.documents.items);
    const [files, setFiles] = useState([]);
    const [uploading, setUploading] = useState(false);
    const [dragActive, setDragActive] = useState(false);
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

    const handleDragOver = (e) => {
        e.preventDefault();
        setDragActive(true);
    };

    const handleDragLeave = (e) => {
        e.preventDefault();
        setDragActive(false);
    };

    const handleDrop = (e) => {
        e.preventDefault();
        setDragActive(false);
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
        const headers = {};
        if (userId != null) {
            headers['userId'] = userId;
        }

        try {
            const response = await fetch('http://localhost:7070/api/document/upload', {
                method: 'POST',
                body: formData,
                headers: headers,
            });
            if (response.ok) {
                const data = await response.json();
                onUpload();
                console.log(data);
                dispatch(uploadDocumentsSuccess([
                    {
                       id: data.documentId,
                       userId: data.userId,
                       uploadedAt: data.uploadedDate,
                       fileName: data.fileName,
                    }]
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
        <div className="w-full space-y-6">
            {/* Dr +9ag & Drop Zone */}
            <div
                className={`
                    relative group cursor-pointer transition-all duration-300 hover:scale-[1.02]
                    p-10 rounded-3xl border-2 border-dashed border-white/20 bg-white/5 backdrop-blur-xl
                    hover:border-white/40 hover:bg-white/10 hover:shadow-2xl
                    ${dragActive
                    ? 'border-purple-400/60 bg-purple-500/5 shadow-purple-500/25 scale-[1.02] shadow-2xl ring-2 ring-purple-500/30'
                    : 'shadow-xl'
                }
                `}
                onDragOver={handleDragOver}
                onDragLeave={handleDragLeave}
                onDrop={handleDrop}
                onClick={() => fileInputRef.current?.click()}
            >
                {/* Icon */}
                <div className="flex flex-col items-center justify-center space-y-4 mb-6">
                    <div className="
                        w-20 h-20 bg-gradient-to-br from-purple-500/20 to-pink-500/20
                        rounded-2xl flex items-center justify-center shadow-lg border border-white/20
                        group-hover:from-purple-400/30 group-hover:to-pink-400/30 group-hover:scale-110 transition-all
                    ">
                        <UploadCloud className="w-12 h-12 text-white/80 group-hover:text-purple-300 transition-colors" />
                    </div>
                    <div>
                        <p className="text-xl font-semibold text-white mb-1">Drop your files here</p>
                        <p className="text-gray-300 text-sm">or click to browse</p>
                        <p className="text-xs text-gray-200 mt-1">
                            {allowedTypes.join(', ')} • Max {maxSizeMB}MB
                        </p>
                    </div>
                </div>

                <input
                    ref={fileInputRef}
                    type="file"
                    multiple
                    accept={allowedTypes.join(',')}
                    onChange={handleChange}
                    className="absolute inset-0 w-full h-full opacity-0 cursor-pointer"
                />
            </div>

            {/* File List */}
            {files.length === 1 && (
                <div className="space-y-3">
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                        {files.map((file, index) => (
                            <div key={index} className="
                                group bg-white/5 backdrop-blur-sm border border-white/10
                                rounded-2xl p-4 hover:bg-white/10 hover:border-white/20
                                transition-all hover:shadow-xl flex items-center justify-between
                            ">
                                <div className="flex items-center space-x-3 truncate">
                                    <div className="w-10 h-10 bg-gradient-to-br from-gray-500/30 to-gray-600/30 rounded-xl flex items-center justify-center flex-shrink-0">
                                        <span className="text-xs font-bold text-white uppercase">
                                            {file.name.split('.').pop()}
                                        </span>
                                    </div>
                                    <div className="truncate">
                                        <p className="font-medium text-white truncate">{file.name}</p>
                                        <p className="text-sm text-gray-400">
                                            {(file.size / 1024 / 1024).toFixed(1)} MB
                                        </p>
                                    </div>
                                </div>
                                <button
                                    onClick={() => removeFile(index)}
                                    className="
                                        p-2 hover:bg-white/20 rounded-xl transition-all
                                        hover:scale-110 opacity-70 hover:opacity-100
                                    "
                                >
                                    <X className="w-5 h-5 text-gray-400" />
                                </button>
                            </div>
                        ))}
                    </div>

                    {/* Upload Button */}
                    <button
                        onClick={uploadFiles}
                        disabled={uploading || !files.length || (!isUserLoggedIn && documents.length === 1)}
                        className="
                            w-full py-4 px-8 rounded-2xl font-semibold text-lg
                            bg-gradient-to-r from-emerald-500 to-teal-600
                            hover:from-emerald-600 hover:to-teal-700
                            shadow-xl hover:shadow-2xl transform hover:-translate-y-0.5
                            transition-all duration-200 flex items-center justify-center gap-2
                            disabled:from-gray-500 disabled:to-gray-600 disabled:cursor-not-allowed disabled:shadow-none
                            disabled:hover:transform-none
                        "
                    >
                        {uploading ? (
                            <>
                                <Loader2 className="w-5 h-5 animate-spin" />
                                Uploading...
                            </>
                        ) : (
                            <>
                                <UploadCloud className="w-5 h-5" />
                                Upload {files.length} {files.length === 1 ? 'file' : 'files'}
                            </>
                        )}
                    </button>
                </div>
            )}
        </div>
    );
};

export default DocumentUpload;
