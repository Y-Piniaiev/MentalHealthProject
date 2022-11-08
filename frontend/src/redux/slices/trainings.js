import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import axios from "../../axios";

export const fetchTraining = createAsyncThunk('posts/fetchTraining', async () => {
    const { data } = await axios.get('/training')
    return data;
});

export const fetchRemoveTraining = createAsyncThunk('posts/fetchRemoveTraining', async (id) => axios.delete(`/training/${id}`)
);

const initialState = {
    trainings: {
        items: [],
        status: 'loading'
    },
}

const trainingsSlice = createSlice({
    name: 'trainings',
    initialState,
    reducer: {},
    extraReducers: {
        [fetchTraining.pending]: (state) => {
            state.trainings.status = 'loading';
        },
        [fetchTraining.fulfilled]: (state, action) => {
            state.trainings.items = action.payload;
            state.trainings.status = 'loaded';
        },
        [fetchTraining.rejected]: (state) => {
            state.trainings.items = [];
            state.trainings.status = 'error';
        },
        [fetchRemoveTraining.pending]: (state, action) => {
            state.trainings.items = state.trainings.items.filter((obj) => obj._id !== action.meta.arg)
        },
    },
})

export const trainingsReducer = trainingsSlice.reducer;