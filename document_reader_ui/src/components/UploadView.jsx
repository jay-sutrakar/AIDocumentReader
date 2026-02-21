import { motion } from 'framer-motion';
import DocumentUpload from './DocumentUpload';
import {FileText, Upload} from "lucide-react";

export default function UploadView({ documents, onUpload, onChatOpen }) {
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
                    <h1 className="text-4xl font-bold bg-gradient-to-r from-white to-gray-200 bg-clip-text text-transparent mb-2">
                        Document Q&A
                    </h1>
                    <p className="text-gray-300 text-lg max-w-md mx-auto">
                        Upload your document to start asking questions
                    </p>
                </div>
            </motion.div>

            <DocumentUpload
                onUpload={(data) => {
                    onUpload(data);
                    onChatOpen();
                }}
            />
            {documents.length > 0 && (
                <motion.p
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    className="text-emerald-400 flex items-center gap-2 justify-center"
                >
                    <Upload className="w-5 h-5" />
                    {documents.length} document(s) ready for chat
                </motion.p>
            )}
        </motion.div>
    );
}
