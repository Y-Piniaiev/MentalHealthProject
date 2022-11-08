import mongoose from 'mongoose'
import { ROLES } from '../utils/roles.js'

const UserSchema = new mongoose.Schema(
  {
    email: {
      type: String,
      required: true,
      unique: true,
    },
    passwordHash: {
      type: String,
      required: true,
    },
    role: {
      type: String,
      required: true,
      enum: Object.values(ROLES),
    },
  },
  {
    timestamps: true,
  }
)

export default mongoose.model('User', UserSchema)
