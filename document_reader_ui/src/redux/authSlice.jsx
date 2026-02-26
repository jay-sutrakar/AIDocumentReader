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
            state.isUserLoggedIn = true;
        },
        logout: (state) => {
            state.userId = null;
            state.isUserLoggedIn = false;
        },
    },
});

export const { login, logout } = authSlice.actions;
export default authSlice.reducer;
