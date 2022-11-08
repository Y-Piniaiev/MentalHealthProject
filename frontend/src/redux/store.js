import { configureStore } from "@reduxjs/toolkit";
import { postReducer } from "./slices/posts";
import { trainingsReducer } from "./slices/trainings";
import { authReducer } from "./slices/auth";

const store = configureStore({
    reducer: {
        posts: postReducer,
        auth: authReducer,
        trainings: trainingsReducer,
    }
})

export default store;