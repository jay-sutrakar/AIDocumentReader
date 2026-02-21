import './App.css';
import { motion, AnimatePresence } from 'framer-motion';
import { Upload, MessageCircle, FileText } from 'lucide-react';
import UploadView from "./components/UploadView";
import {useState} from "react";
import ChatView from "./components/ChatView";

function App() {
    const [documents, setDocuments] = useState([]);
    const [showChat, setShowChat] = useState(true);
    return (
        <div className="min-h-screen bg-gradient-to-br from-indigo-900 via-purple-900/30 to-slate-900 text-white overflow-hidden">
            {/* Animated background */}
            <div className="fixed inset-0">
                <div className="absolute -top-40 -right-40 w-80 h-80 bg-gradient-to-r from-purple-400/20 to-pink-400/20 rounded-full blur-3xl animate-pulse" />
                <div className="absolute -bottom-40 -left-40 w-96 h-96 bg-gradient-to-r from-emerald-400/20 to-cyan-400/20 rounded-full blur-3xl animate-pulse delay-1000" />
            </div>
            <div className="relative z-10 flex flex-col items-center justify-center min-h-screen p-8">
                <motion.div
                    initial={{ opacity: 0, scale: 0.9 }}
                    animate={{ opacity: 1, scale: 1 }}
                    className="w-full max-w-2xl backdrop-blur-xl bg-white/5 border border-white/10 rounded-3xl shadow-2xl p-8"
                >
                    <AnimatePresence mode="wait">
                        {!showChat ? (
                            <UploadView
                                documents={documents}
                                onUpload={setDocuments}
                                onChatOpen={() => setShowChat(true)}
                            />
                        ) : (
                            <ChatView
                                documents={documents}
                                onBack={() => setShowChat(false)}
                            />
                        )}
                    </AnimatePresence>
                </motion.div>
        </div>
        </div>
    )
}

export default App;
