import MobileUserModel from '../models/MobileUser.js'

export default async (req, res, next) => {
  try {
    const mobileUser = await MobileUserModel.findOne({
      userId: req.userId,
    })

    if (!mobileUser) {
      return res.status(403).json({
        message: 'Немає доступа',
      })
    }

    req.mobileUserId = mobileUser._id

    next()
  } catch (err) {
    console.log(err)
    res.status(500).json({
      message: 'Bad request',
    })
  }
}
