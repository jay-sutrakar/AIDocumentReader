import { createSlice } from '@reduxjs/toolkit';

const authSlice = createSlice({
    name: 'auth',
    initialState: {
        userId: null,
        sessionId: null,
        isUserLoggedIn: false,
    },
    reducers: {
        login: (state, action) => {
            state.userId = action.payload.userId;
            state.sessionId = action.payload.sessionId;
            state.isUserLoggedIn = true;
        },
        logout: (state) => {
            state.userId = null;
            state.sessionId = null;
            state.isUserLoggedIn = false;
        },
        createSession: (state, action) => {
            state.sessionId = action.payload.sessionId
        }
    },
});

export const { login, logout, createSession } = authSlice.actions;
export default authSlice.reducer;
