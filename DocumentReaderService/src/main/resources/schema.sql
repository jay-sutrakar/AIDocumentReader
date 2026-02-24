-- Extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS vector;

-- Vector store table
CREATE TABLE IF NOT EXISTS public.vector_store (
   id        uuid PRIMARY KEY DEFAULT gen_random_uuid(),
   content   text,
   metadata  jsonb,
   embedding vecto(1024)
);

-- 1) Users (for logged-in users)
CREATE TABLE IF NOT EXISTS users (
   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   email VARCHAR(255) UNIQUE NOT NULL,
   created_at TIMESTAMP DEFAULT NOW()
);

-- 2) Sessions (for anonymous + logged-in)
CREATE TABLE IF NOT EXISTS sessions (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID REFERENCES users(id),  -- NULL for anonymous
  created_at TIMESTAMP DEFAULT NOW(),
  expires_at TIMESTAMP,               -- auto-delete after X hours
  active_document_count INT DEFAULT 0 CHECK (active_document_count <= 1)
);

-- 3) Chat History (per session + document)
CREATE TABLE IF NOT EXISTS chat_history (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  session_id UUID REFERENCES sessions(id) ON DELETE CASCADE,
  document_id UUID NOT NULL,          -- from vector_store metadata
  role VARCHAR(20) NOT NULL,          -- 'user' | 'assistant'
  content TEXT NOT NULL,
  created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE
    IF NOT EXISTS document_metadata (
        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
        file_name VARCHAR(20) NOT NULL,
        created_at TIMESTAMP DEFAULT NOW()
);
-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_sessions_expires ON sessions(expires_at) WHERE expires_at < NOW();
CREATE INDEX IF NOT EXISTS idx_chat_session_doc ON chat_history(session_id, document_id);
