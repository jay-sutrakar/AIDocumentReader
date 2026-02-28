package com.ai.documentreaderservice.utils;

public class QueryUtil {
    private QueryUtil() {
    }

    public static final String GET_USER_QUERY = "SELECT * FROM public.users AS u WHERE u.email=?";
    public static final String CREATE_USER_QUERY = "INSERT INTO public.users (email) VALUES (?) RETURNING id";
    public static final String GET_CHAT_HISTORY = "SELECT * FROM public.chat_history WHERE document_id=?";
    public static final String CREATE_CHAT_HISTORY = """
            INSERT INTO public.chat_history 
            (session_id, document_id, role, content) 
               VALUES (?, ?, ?, ?)""";
    public static final String GET_USER_DOCUMENTS = """
            SELECT * FROM public.document_metadata WHERE user_id = ? or session_id = ?;
            """;
    public static final String CREATE_DOCUMENT_METADATA = """
            INSERT INTO public.document_metadata (user_id, session_id, file_name)
                VALUES (?, ?, ?) RETURNING id
            """;
    public static final String CREATE_SESSION_METADATA = """
            INSERT INTO public.sessions (user_id, expires_at)
                VALUES (?, ?) RETURNING id
            """;

    public static final String DELETE_CHAT_HISTORY = "DELETE FROM public.chat_history WHERE document_id=?";
    public static final String DELETE_DOCUMENT_METADATA = "DELETE FROM public.document_metadata WHERE id=?";


}
