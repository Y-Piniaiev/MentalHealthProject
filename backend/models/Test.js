import mongoose from 'mongoose'

const QuestionType = {
    questionTest: String,
    answers: [{
        isRight: Boolean,
        text: String,
    }]
}

const TestSchema = new mongoose.Schema({
    name: {
        type: String,
        required: true,
    },
    description: String,
    timeForPass: String,
    questions: {
        type: [QuestionType],
        required: true,
    },
    user: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User',
        requires: true,
    },
}, { timestamps: true })

export default mongoose.model('Test', TestSchema)