import { createSlice } from '@reduxjs/toolkit';

const documentsSlice = createSlice({
    name: 'documents',
    initialState: {
        items: [],  // { id, name, size, url, uploadedAt }
        status: 'idle',  // 'loading' | 'succeeded' | 'failed'
    },
    reducers: {
        uploadDocumentsStart: (state) => {
            state.status = 'loading';
        },
        uploadDocumentsSuccess: (state, action) => {
            state.status = 'succeeded';
            state.items = [...state.items, ...action.payload];
        },
        uploadDocumentsFailure: (state) => {
            state.status = 'failed';
        },
        removeDocument: (state, action) => {
            state.items = state.items.filter(doc => doc.id !== action.payload);
        },
    },
});

export const { uploadDocumentsStart, uploadDocumentsSuccess, uploadDocumentsFailure, removeDocument } = documentsSlice.actions;
export default documentsSlice.reducer;
