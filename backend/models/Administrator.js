import mongoose from 'mongoose'

const { ObjectId } = mongoose.Schema.Types

const AdministratorSchema = new mongoose.Schema(
  {
    userId: {
      type: ObjectId,
      ref: 'User',
      required: true,
      unique: true,
    },
    education: {
      type: String,
      required: true,
    },
  },
  {
    timestamps: true,
  }
)

export default mongoose.model('Administrator', AdministratorSchema)
