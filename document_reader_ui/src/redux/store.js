import { configureStore } from '@reduxjs/toolkit';
import documentsReducer from './documentsSlice';

export const store = configureStore({
    reducer: {
        documents: documentsReducer,
    },
});
