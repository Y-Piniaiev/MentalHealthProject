import mongoose from 'mongoose'

const { ObjectId } = mongoose.Schema.Types

const MobileUserSchema = new mongoose.Schema(
  {
    userId: {
      type: ObjectId,
      ref: 'User',
      required: true,
      unique: true,
    },
    name: {
      type: String,
      required: true,
    },
    birth: {
      type: Date,
      required: true,
    },
    notesIds: {
      type: [
        {
          type: ObjectId,
          ref: 'Note',
        },
      ],
    },
    imageUrl: String,
  },
  {
    timestamps: true,
  }
)

export default mongoose.model('MobileUser', MobileUserSchema)
