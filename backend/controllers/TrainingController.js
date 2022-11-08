import TrainingModel from '../models/Training.js'

export const getAll = async (req, res) => {
  try {
    const trainings = await TrainingModel.find().populate('user').exec()

    res.json(trainings)
  } catch (err) {
    console.log(err)
    res.status(500).json({
      message: 'Не вдалося знайти статті',
    })
  }
}

export const getOne = async (req, res) => {
  try {
    const trainingId = req.params.id

    TrainingModel.findOneAndUpdate(
      {
        _id: trainingId,
      },
      {
        $inc: { viewsCount: 1 },
      },
      {
        returnDocument: 'after',
      },
      (err, doc) => {
        if (err) {
          console.log(err)
          return res.status(500).json({
            message: 'Не вдалося повернути статтю',
          })
        }

        if (!doc) {
          return res.status(404).json({
            message: 'Статтю не знайдено',
          })
        }

        res.json(doc)
      }
    )
  } catch (err) {
    console.log(err)
    res.status(500).json({
      message: 'Не вдалося знайти статті',
    })
  }
}

export const remove = async (req, res) => {
  try {
    const trainingId = req.params.id

    TrainingModel.findOneAndDelete(
      {
        _id: trainingId,
      },
      (err, doc) => {
        if (err) {
          console.log(err)
          return res.status(500).json({
            message: 'Не вдалося повернути статтю',
          })
        }

        if (!doc) {
          return res.status(404).json({
            message: 'Статтю не знайдено',
          })
        }

        res.json({
          success: true,
        })
      }
    )
  } catch (err) {
    console.log(err)
    res.status(500).json({
      message: 'Не вдалося знайти статті',
    })
  }
}

export const update = async (req, res) => {
  try {
    const trainingId = req.params.id

    await TrainingModel.updateOne(
      {
        _id: trainingId,
      },
      {
        title: req.body.title,
        text: req.body.text,
        task: req.body.task,
        time: req.body.time,
        imageUrl: req.body.imageUrl,
        user: req.userId,
      }
    )

    res.json({
      success: true,
    })
  } catch (err) {
    console.log(err)
    res.status(500).json({
      message: 'Не вдалося оновити статтю',
    })
  }
}

export const create = async (req, res) => {
  try {
    const doc = new TrainingModel({
      title: req.body.title,
      text: req.body.text,
      task: req.body.task,
      time: req.body.time,
      imageUrl: req.body.imageUrl,
      user: req.userId,
    })

    const post = await doc.save()

    res.json(post)
  } catch (err) {
    console.log(err)
    res.status(500).json({
      message: 'Не вдалося створити статтю',
    })
  }
}
