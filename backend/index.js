import * as dotenv from 'dotenv';
dotenv.config();
import { registerValidation, postCreateValidation, trainingCreateValidation, noteValidation, createTestValidation, updateTestValidation } from './validations.js';
import express from 'express';
import mongoose from "mongoose";
import checkAuth from './utils/checkAuth.js';
import * as UserController from './controllers/UserController.js';
import * as PostController from './controllers/PostController.js';
import * as NoteController from './controllers/NoteController.js';
import * as TestController from './controllers/TestController.js';
import * as TrainingController from './controllers/TrainingController.js';
import multer from 'multer';
import handleValidationErrors from './utils/handleValidationErrors.js';
import checkMobileUser from './utils/checkMobileUser.js';
import cors from 'cors';


const app = express();
const port = 5000;

app.use(express.json());
app.use(cors());
app.use(express.static('public'));
app.use('/uploads', express.static('uploads'));

mongoose
    .connect(`mongodb+srv://${process.env.DB_USERNAME}:${process.env.DB_PASSWORD}@mentalhealth.6r7dd2f.mongodb.net/MentalHealth?retryWrites=true&w=majority`)
    .then(() => console.log("DB okey"))
    .catch((err) => console.log("DB error", err))

const storage = multer.diskStorage({
    destination: (_, __, cb) => {
        cb(null, 'uploads')
    },
    filename: (_, file, cb) => {
        cb(null, file.originalname)
    }
});

const upload = multer({
    storage
});

app.get('/', (req, res) => {
    res.send('Hello World!')
});

app.post('/upload', checkAuth, upload.single('image'), (req, res) => {
    res.json({
        url: `/uploads/${req.file.originalname}`
    })
});

app.post('/auth/login', registerValidation, handleValidationErrors, UserController.login);
app.post('/auth/register', registerValidation, handleValidationErrors, UserController.register);
app.get('/auth/me', checkAuth, UserController.getMe);


app.get('/posts', PostController.getAll);
app.get('/posts/:id', PostController.getOne);
app.post('/posts', checkAuth, postCreateValidation, handleValidationErrors, PostController.create);
app.delete('/posts/:id', checkAuth, PostController.remove);
app.patch('/posts/:id', checkAuth, postCreateValidation, handleValidationErrors, PostController.update);


app.get('/training', TrainingController.getAll);
app.get('/training/:id', TrainingController.getOne);
app.post('/training', checkAuth, trainingCreateValidation, handleValidationErrors, TrainingController.create);
app.delete('/training/:id', checkAuth, TrainingController.remove);
app.patch('/training/:id', checkAuth, trainingCreateValidation, handleValidationErrors, TrainingController.update);


app.get('/note', checkAuth, checkMobileUser, NoteController.getAll);
app.get('/note/:id', checkAuth, checkMobileUser, NoteController.getOne);
app.post('/note', checkAuth, checkMobileUser, noteValidation, handleValidationErrors, NoteController.create);
app.delete('/note/:id', checkAuth, checkMobileUser, NoteController.remove);
app.patch('/note/:id', checkAuth, checkMobileUser, noteValidation, handleValidationErrors, NoteController.update);


app.get('/test', TestController.getAll);
app.get('/test/:id', TestController.getOne);
app.post('/test', checkAuth, createTestValidation, handleValidationErrors, TestController.create);
app.delete('/test/:id', checkAuth, TestController.remove);
app.patch('/test/:id', checkAuth, updateTestValidation, handleValidationErrors, TestController.update);


app.listen(port, () => {
    console.log(`Example app listening on port ${port}`)
});