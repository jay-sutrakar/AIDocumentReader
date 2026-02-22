import './App.css';
import { motion, AnimatePresence } from 'framer-motion';
import UploadView from "./components/UploadView";
import {useState} from "react";
import ChatView from "./components/ChatView";
import { UploadCloud, X, Trash } from "lucide-react";
import {useDispatch, useSelector} from "react-redux";
import {removeDocument} from "./redux/documentsSlice";

function App() {
    const documents = useSelector((state) => state.documents.items);
    const [showChat, setShowChat] = useState(false);
    const dispatch = useDispatch();
    const transformDateFormate = (date) => {
        const [day, month, year] = date.split('-').map(Number);
        const transformedDate = new Date(year, month - 1, day);
        return transformedDate.toLocaleDateString("en-US");
    }

    const deleteDocument = id => {
        console.log('delete operation called : ' + id);
        // dispatch(removeDocument(id));
    }
    return (
        <div className="min-h-screen bg-gradient-to-br from-indigo-900 via-purple-900/30 to-slate-900 text-white overflow-hidden">
            {/* Animated Background */}
            <div className="fixed inset-0 pointer-events-none">
                <div className="absolute -top-40 -right-40 w-80 h-80 bg-gradient-to-r from-purple-400/20 to-pink-400/20 rounded-full blur-3xl animate-pulse" />
                <div className="absolute -bottom-40 -left-40 w-96 h-96 bg-gradient-to-r from-emerald-400/20 to-cyan-400/20 rounded-full blur-3xl animate-pulse delay-1000" />
            </div>

            <div className="relative z-10 flex h-screen">
                {/* LEFT SIDEBAR: Document List */}
                <div className="w-60 border-r border-white/10 bg-white/3 backdrop-blur-xl">
                    <div className="p-6 border-b border-white/10">
                        <h2 className="text-2xl font-bold bg-gradient-to-r from-white to-gray-200 bg-clip-text text-transparent">
                            📄 Documents
                        </h2>
                        <p className="text-sm text-gray-400 mt-1">{documents.length} loaded</p>
                    </div>

                    <div className="p-4 space-y-3 overflow-y-auto h-[calc(100vh-140px)]">
                        {documents.length === 0 ? (
                            <div className="text-center py-12 text-gray-200">
                                <UploadCloud className="w-16 h-16 mx-auto mb-4 opacity-50" />
                                <p className="text-lg">No documents yet</p>
                                <p className="text-sm">Upload files to get started</p>
                            </div>
                        ) : (
                            documents.map((doc, index) => (
                                <motion.div
                                    key={doc.id}
                                    initial={{ opacity: 0, x: -20 }}
                                    animate={{ opacity: 1, x: 0 }}
                                    className="
                    group bg-white/5 backdrop-blur-sm border border-white/10
                    hover:bg-white/10 hover:border-white/20 rounded-2xl p-4
                    cursor-pointer transition-all hover:shadow-xl hover:-translate-x-1
                  "
                                    onClick={() => {/* Select document for chat */}}
                                >
                                    <div className="flex items-center justify-between">
                                        <div className="flex items-center space-x-3">
                                            <div className="w-8 h-8 bg-gradient-to-br from-purple-500/30 to-pink-500/30 rounded-xl flex items-center justify-center">
                        <span className="text-sm font-bold uppercase text-white">
                          {doc.fileName?.split('.').pop() || 'PDF'}
                        </span>
                                            </div>
                                            <div className="min-w-0 flex-1">
                                                <p className="font-small text-white truncate">{doc.fileName}</p>
                                                <p className="font-small text-white truncate">{doc.documentId}</p>
                                                <p className="text-xs text-gray-400">
                                                    {transformDateFormate(doc.uploadedAt)}
                                                </p>
                                            </div>
                                        </div>
                                        <div className="opacity-0 group-hover:opacity-100 transition-opacity">
                                            <Trash className="w-4 h-4 text-gray-400 hover:text-white" onClick={() => deleteDocument(doc.id)}/>
                                        </div>
                                    </div>
                                </motion.div>
                            ))
                        )}
                    </div>
                </div>

                {/* MAIN CONTENT: Upload or Chat */}
                <div className="flex-1 flex flex-col">
                    <div className="flex-1 p-8 overflow-y-auto">
                        <AnimatePresence mode="wait">
                            {!showChat ? (
                                <motion.div
                                    key="upload"
                                    initial={{ opacity: 0, scale: 0.95 }}
                                    animate={{ opacity: 1, scale: 1 }}
                                    exit={{ opacity: 0, scale: 0.95 }}
                                    className="max-w-4xl mx-auto"
                                >
                                    <UploadView
                                        documents={documents}
                                        onChatOpen={() => setShowChat(true)}
                                    />
                                </motion.div>
                            ) : (
                                <motion.div
                                    key="chat"
                                    initial={{ opacity: 0, x: 20 }}
                                    animate={{ opacity: 1, x: 0 }}
                                    exit={{ opacity: 0, x: -20 }}
                                    className="max-w-4xl mx-auto"
                                >
                                    <ChatView
                                        documents={documents}
                                        onBack={() => setShowChat(false)}
                                    />
                                </motion.div>
                            )}
                        </AnimatePresence>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default App;
