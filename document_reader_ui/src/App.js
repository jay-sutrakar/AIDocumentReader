import "./App.css";
import { motion, AnimatePresence } from "framer-motion";
import UploadView from "./components/UploadView";
import { useEffect, useState } from "react";
import ChatView from "./components/ChatView";
import { UploadCloud, X, Trash } from "lucide-react";
import { useDispatch, useSelector } from "react-redux";
import { removeDocument, uploadDocumentsSuccess } from "./redux/documentsSlice";
import AuthModal from "./components/Authentication";
import { createSession, login, logout } from "./redux/authSlice";

function App() {
  const documents = useSelector((state) => state.documents.items);
  const [showChat, setShowChat] = useState(false);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [showAuthModal, setShowAuthModal] = useState(false);
  const [selectedDocument, setSelectedDocument] = useState({});
  const [documentsErrorMessage, setDocumentsErrorMessage] = useState("");
  const dispatch = useDispatch();
  const { userId, sessionId } = useSelector((state) => state.auth);

  const transformDateFormate = (date) => {
    // const [day, month, year] = date.split('-').map(Number);
    // const transformedDate = new Date(year, month - 1, day);
    return date;
  };
  const fetchDocuments = async (userId) => {
    try {
      const headers = {};
      if (userId != null) {
        headers["userId"] = userId;
      }
      if (sessionId != null) {
        headers["sessionId"] = sessionId;
      }
      const url = new URL(
        "http://localhost:7070/api/document/uploaded-documents",
      );
      if (userId != null) {
        url.searchParams.set("userId", userId);
      }
      if (sessionId != null) {
        url.searchParams.set("sessionId", sessionId);
      }
      const response = await fetch(url.toString(), {
        method: "GET",
      });
      const data = await response.json();
      if (response.ok) {
        dispatch(uploadDocumentsSuccess(data));
      }
    } catch (error) {
      setDocumentsErrorMessage(error);
    }
  };
  useEffect(() => {
    console.log("useEffect called");
    console.log(documents);
    const storedUserId = localStorage.getItem("userId");
    const sessionId = localStorage.getItem("sessionId");
    if (storedUserId) {
      dispatch(login({ userId: storedUserId }));
      setIsLoggedIn(true);
      fetchDocuments(storedUserId).then((r) => {
        console.log("fetched documents");
      });
    } else if (sessionId) {
        dispatch(createSession({sessionId: sessionId}));
    }
  }, []);

  const deleteDocument = (id) => {
    console.log("delete operation called : " + id);
    // dispatch(removeDocument(id));
  };

  const handleLogin = () => {
    setShowAuthModal(true);
  };

  const handleLogout = () => {
    localStorage.removeItem("userId");
    localStorage.removeItem("sessionId")
    dispatch(logout({}));
    setShowChat(false);
    dispatch(uploadDocumentsSuccess([]));
    setIsLoggedIn(false);
  };
  // Add to App.jsx state

  const handleLoginSuccess = (userId, sessionId) => {
    setIsLoggedIn(true);
    console.log("Logged in:", userId);
    localStorage.setItem("userId", userId);
    localStorage.setItem("sessionId", sessionId);
    dispatch(login({userId: userId, sessionId: sessionId}))
    fetchDocuments(userId);
  };

  const hasReachedFreeLimit = !isLoggedIn && documents.length >= 1;
  if (showAuthModal) {
    return (
      <AuthModal
        isOpen={showAuthModal}
        onClose={() => setShowAuthModal(false)}
        onLoginSuccess={handleLoginSuccess}
      />
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-indigo-900 via-purple-900/50 to-slate-900 text-white overflow-y-auto">
      {/* Animated Background */}
      <div className="fixed inset-0 pointer-events-none">
        <div className="absolute -top-40 -right-40 w-80 h-80 bg-gradient-to-r from-purple-400/20 to-pink-400/20 rounded-full blur-3xl animate-pulse" />
        <div className="absolute -bottom-40 -left-40 w-96 h-96 bg-gradient-to-r from-emerald-400/20 to-cyan-400/20 rounded-full blur-3xl animate-pulse delay-1000" />
      </div>

      <div className="relative z-10 flex flex-col h-screen">
        {/* TOP BAR: Title + Login */}
        <div className="flex items-center justify-between px-6 py-4  backdrop-blur-lg">
          <div>
            <h1 className="text-2xl font-bold bg-gradient-to-r from-white to-gray-200 bg-clip-text text-transparent">
              Mr. Doc Expert
            </h1>
            <p className="text-xs text-gray-400">
              Upload your documents and chat with them
            </p>
          </div>
          {/* User logging section */}
          <div className="flex items-center space-x-3">
            {isLoggedIn ? (
              <>
                <span className="text-sm text-emerald-300">Logged in</span>
                <button
                  onClick={handleLogout}
                  className="px-3 py-1 text-sm rounded-full bg-white/10 hover:bg-white/20 border border-white/20"
                >
                  Logout
                </button>
              </>
            ) : (
              <button
                onClick={handleLogin}
                className="px-3 py-1 text-sm rounded-full bg-emerald-500 hover:bg-emerald-600 text-black font-medium"
              >
                Login
              </button>
            )}
          </div>
        </div>

        <div className="flex flex-1">
          {/* LEFT SIDEBAR: Document List */}
          <div className="w-60 border-r border-white/10 bg-white/3 backdrop-blur-xl">
            <div className="p-6 border-b border-white/10">
              <h2 className="text-xl font-bold bg-gradient-to-r from-white to-gray-200 bg-clip-text text-transparent">
                📄 Documents
              </h2>
              <p className="text-sm text-gray-400 mt-1">
                {documents.length} loaded
              </p>
            </div>

            <div className="p-4 space-y-3 overflow-y-auto h-[calc(100vh-180px)]">
              {documents.length === 0 ? (
                <div className="text-center py-12 text-gray-200">
                  <UploadCloud className="w-16 h-16 mx-auto mb-4 opacity-50" />
                  <p className="text-lg">No documents yet</p>
                  <p className="text-sm">Upload files to get started</p>
                </div>
              ) : (
                documents.map((doc) => (
                  <motion.div
                    key={doc.id}
                    initial={{ opacity: 0, x: -20 }}
                    animate={{ opacity: 1, x: 0 }}
                    className="
                                            group bg-white/5 backdrop-blur-sm border border-white/10
                                            hover:bg-white/10 hover:border-white/20 rounded-2xl p-4
                                            cursor-pointer transition-all hover:shadow-xl hover:-translate-x-1
                                        "
                    onClick={() => {
                      console.log(doc);
                      setSelectedDocument(doc);
                      setShowChat(true);
                    }}
                  >
                    <div className="flex items-center justify-between">
                      <div className="flex items-center space-x-3">
                        <div className="w-8 h-8 bg-gradient-to-br from-purple-500/30 to-pink-500/30 rounded-xl flex items-center justify-center">
                          <span className="text-sm font-bold uppercase text-white">
                            {doc.fileName?.split(".").pop() || "PDF"}
                          </span>
                        </div>
                        <div className="min-w-0 flex-1">
                          <p className="font-small text-white truncate">
                            {doc.fileName}
                          </p>
                          <p className="font-small text-white truncate">
                            {doc.documentId}
                          </p>
                          <p className="text-xs text-gray-400">
                            {transformDateFormate(doc.uploadedAt)}
                          </p>
                        </div>
                      </div>
                      <div className="opacity-0 group-hover:opacity-100 transition-opacity">
                        <Trash
                          className="w-4 h-4 text-gray-400 hover:text-white"
                          onClick={() => deleteDocument(doc.id)}
                        />
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
              {/* Login required message for >1 doc */}
              {hasReachedFreeLimit && (
                <div className="max-w-4xl mx-auto mb-4">
                  <div className="rounded-xl border border-amber-400/50 bg-amber-500/10 px-4 py-3 text-sm text-amber-100">
                    <p className="font-medium">
                      You have reached the free limit of 1 document.
                    </p>
                    <p className="text-xs mt-1">
                      Please login to upload more documents and continue
                      analyzing additional files.
                    </p>
                  </div>
                </div>
              )}

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
                      isLoggedIn={isLoggedIn}
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
                      selectedDocument={selectedDocument}
                      onBack={() => {
                        setShowChat(false);
                        setSelectedDocument({});
                      }}
                      isLoggedIn={isLoggedIn}
                    />
                  </motion.div>
                )}
              </AnimatePresence>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default App;
