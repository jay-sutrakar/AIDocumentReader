import { useState, useRef, useEffect } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { ArrowLeft, Send, Bot } from "lucide-react";
import { useSelector } from "react-redux";

export default function ChatView({ selectedDocument, onBack }) {
  const { userId, sessionId } = useSelector((state) => state.auth);
  const [loading, setLoading] = useState(false);
  const [messages, setMessages] = useState([
    {
      id: 1,
      role: "ai",
      content:
        "I've analyzed your document(s). Ask me anything about the content!",
      avatar: "🤖",
    },
  ]);
  const [input, setInput] = useState("");
  const messagesEndRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };
  useEffect(() => {
    handleChatHistoryFetch();
  }, [selectedDocument]);

  useEffect(scrollToBottom, [messages]);

  const handleChatHistoryFetch = async () => {
    try {
      const url = new URL("http://localhost:7070/api/document/chat-history");
      url.searchParams.append("documentId", selectedDocument.id);
      const response = await fetch(url, {
        headers: {
          "Content-Type": "application/json",
        },
      });
      if (response.ok) {
        const chatHistoryResponse = await response.json();
        const chatHistory = chatHistoryResponse.map((doc, idx) => ({
          id: idx,
          role: doc.role,
          content: doc.content,
          avatar: doc.role === "ai" ? "🤖" : null,
        }));
        if (chatHistory.length > 0) {
          setMessages(chatHistory);
        }
      }
    } catch (error) {
      setMessages([{
        id: "-1",
        role: "system error",
        content: error,
      }]);
    }
  };

  const sendMessage = async () => {
    if (!input.trim()) return;

    const userMsg = { id: Date.now(), role: "user", content: input };
    setMessages((prev) => [...prev, userMsg]);

    try {
      setLoading(true);
      const response = await fetch(`http://localhost:7070/api/document/chat`, {
        method: "POST",
        body: JSON.stringify({
          message: input,
          documentId: selectedDocument.id,
          sessionId: sessionId,
        }),
        headers: {
          "Content-Type": "application/json",
          userId: userId,
        },
      });
      const data = await response.json();
      if (response.ok) {
        const aiMessage = {
          id: Date.now(),
          role: "ai",
          content: data.message,
          avatar: "🤖",
        };
        setMessages((prev) => [...prev, aiMessage]);
      } else {
        const errorMessage = {
          id: Date.now(),
          role: "ai",
          content: "Failed to get ai response !",
        };
        setMessages((prev) => [...prev, errorMessage]);
        console.log(data);
      }
    } catch (error) {
      setInput("");
      setLoading(false);
    } finally {
      setInput("");
      setLoading(false);
    }
  };

  return (
    <motion.div
      initial={{ opacity: 0, x: 20 }}
      animate={{ opacity: 1, x: 0 }}
      exit={{ opacity: 0, x: -20 }}
      className="h-[700px] flex flex-col"
    >
      {/* Header */}
      <div className="flex items-center p-6 border-b border-white/10">
        <button
          onClick={onBack}
          className="p-2 hover:bg-white/10 rounded-xl transition-all"
        >
          <ArrowLeft className="w-5 h-5" />
        </button>
        <div className="ml-4 flex items-center gap-3">
          <div className="w-10 h-10 bg-gradient-to-r from-purple-500 to-pink-500 rounded-2xl flex items-center justify-center shadow-lg">
            <Bot className="w-5 h-5" />
          </div>
          <div>
            <h2 className="font-semibold text-lg">Document Assistant</h2>
            <p className="text-sm text-gray-400">
              {selectedDocument.fileName} loaded
            </p>
          </div>
        </div>
      </div>

      {/* Messages */}
      <div className="flex-1 p-10 overflow-y-auto space-y-4">
        <AnimatePresence>
          {messages.map((msg, index) => (
            <motion.div
              key={msg.id}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              className={`flex ${msg.role === "user" ? "justify-end" : "justify-start"}`}
            >
              <div
                className={`max-w-xs lg:max-w-md px-4 py-3 rounded-2xl shadow-lg ${
                  msg.role === "user"
                    ? "bg-gradient-to-r from-purple-500 to-pink-500 text-white"
                    : msg.role === "system-error"
                      ? "bg-gradient-to-r from-red-500/20 to-orange-500/20 border-2 border-red-400/50 backdrop-blur-sm text-red-100 shadow-red-500/25"
                      : "bg-white/10 backdrop-blur-sm border border-white/20 text-white"
                }`}
              >
                <p>{msg.content}</p>
                {loading && index === messages.length - 1 && (
                  <span className="text-white/80 text-sm font-medium">
                    AI is typing...
                  </span>
                )}
              </div>
            </motion.div>
          ))}
        </AnimatePresence>
        <div ref={messagesEndRef} />
      </div>

      {/* Input */}
      <div className="p-6 border-t border-white/10">
        <div className="flex items-end gap-3 bg-white/5 backdrop-blur-sm rounded-2xl p-4">
          <input
            value={input}
            onChange={(e) => setInput(e.target.value)}
            // onKeyPress={(e) => e.key === 'Enter' && sendMessage()}
            placeholder="Ask about your document..."
            className="flex-1 bg-transparent outline-none placeholder-gray-200 text-white"
          />
          <button
            onClick={sendMessage}
            disabled={!input.trim()}
            className="p-3 bg-gradient-to-r from-emerald-500 to-teal-500 hover:from-emerald-600 rounded-2xl shadow-lg transition-all disabled:opacity-50 hover:scale-105"
          >
            <Send className="w-5 h-5" />
          </button>
        </div>
      </div>
    </motion.div>
  );
}
