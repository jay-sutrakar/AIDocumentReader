import { motion } from 'framer-motion';
import DocumentUpload from './DocumentUpload';
import {FileText, Upload, MessageCircle} from "lucide-react";

export default function UploadView({ documents, setSelectedDocument, onChatOpen }) {
    return (
        <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
            className="text-center space-y-6"
        >
            <motion.div className="flex flex-col items-center space-y-4">
                <motion.div
                    animate={{ rotate: 360 }}
                    transition={{ duration: 20, repeat: Infinity, ease: "linear" }}
                    className="w-24 h-24 bg-gradient-to-r from-purple-400 to-pink-400 rounded-2xl p-2 shadow-2xl"
                >
                    <FileText className="w-full h-full text-white p-4 bg-black/20 rounded-xl" />
                </motion.div>
                <div>
                    <h1 className="text-2xl font-bold bg-gradient-to-r from-white to-gray-200 bg-clip-text text-transparent mb-2">
                        Document Q&A
                    </h1>
                    <p className="text-gray-300 text-lg max-w-md mx-auto">
                        Upload your document to start asking questions
                    </p>
                </div>
            </motion.div>

             <DocumentUpload
                onUpload={(uploadedDocInfo) => {
                    setSelectedDocument(uploadedDocInfo);
                    onChatOpen();
                
                }}
            />
            {documents.length > 0 && (
                <motion.div
                    initial={{ opacity: 0, scale: 0.95 }}
                    animate={{ opacity: 1, scale: 1 }}
                    className="flex flex-col items-center space-y-4 pt-6 border-t border-white/10"
                >
                    {/* Status Message */}
                    <motion.p
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                        className="text-emerald-400 flex items-center gap-2 text-sm"
                    >
                        <Upload className="w-5 h-5" />
                        {documents.length} document(s) ready for chat
                    </motion.p>

                    {/* Start Chat Button */}
                    <motion.button
                        whileHover={{ scale: 1.05 }}
                        whileTap={{ scale: 0.98 }}
                        onClick={() => {
                            setSelectedDocument(documents[0])
                            onChatOpen()    
                        }}
                        className="
                            px-6 py-3 bg-gradient-to-r from-emerald-400 to-teal-600
                            hover:from-emerald-600 hover:to-teal-700
                            text-white font-semibold text-sm rounded-2xl shadow-xl
                            hover:shadow-2xl hover:-translate-y-1 transition-all duration-200
                            flex items-center gap-3 ring-2 ring-emerald-500/30
                        "
                    >
                        <MessageCircle className="w-6 h-6" />
                        Start Chatting
                    </motion.button>
                </motion.div>
            )}
        </motion.div>
    );
}
