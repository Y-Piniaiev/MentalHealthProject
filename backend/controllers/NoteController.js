import NoteModel from '../models/Note.js'
import MobileUserModel from '../models/MobileUser.js'

export const getAll = async (req, res, next) => {
  try {
    const user = await MobileUserModel.findOne({
      _id: req.mobileUserId,
    })

    if (!user) {
      res.status(401).json({
        message: 'Користувач не авторизований',
      })
    }

    const notes = await NoteModel.find({
      _id: { $in: user._doc.notesIds },
    })

    res.json(notes)
  } catch (err) {
    console.log(err)
    res.status(500).json({
      message: 'Bad request',
    })
  }
}

export const getOne = async (req, res) => {
  try {
    const noteId = req.params.id;

    NoteModel.findOne({
      _id: noteId,
    }, (err, doc) => {
      if (err) {
        console.log(err);
        return res.status(500).json({
          message: 'Не вдалося повернути нотатку'
        });
      }

      if (!doc) {
        return res.status(404).json({
          message: 'Нотатку не знайдено'
        })
      }

      res.json(doc);
    }
    )
  } catch (err) {
    console.log(err);
    res.status(500).json({
      message: 'Не вдалося знайти нотатку'
    });
  }
};

export const remove = async (req, res) => {
  try {
    const noteId = req.params.id;

    NoteModel.findOneAndDelete({
      _id: noteId,
    }, (err, doc) => {
      if (err) {
        console.log(err);
        return res.status(500).json({
          message: 'Не вдалося повернути нотатку'
        });
      }

      if (!doc) {
        return res.status(404).json({
          message: 'Нотатку не знайдено'
        })
      }

      res.json({
        success: true,
      })
    });

  } catch (err) {
    console.log(err);
    res.status(500).json({
      message: 'Не вдалося знайти нотатку'
    });
  }
};

export const update = async (req, res) => {
  try {
    const noteId = req.params.id;

    await NoteModel.updateOne({
      _id: noteId,
    },
      {
        title: req.body.title,
        text: req.body.text,
      });

    res.json({
      success: true,
    })
  } catch (err) {
    console.log(err);
    res.status(500).json({
      message: 'Не вдалося оновити нотатку'
    });
  }
}

export const create = async (req, res) => {
  const mobileUserId = req.mobileUserId;
  try {
    const noteDoc = new NoteModel({
      title: req.body.title,
      text: req.body.text,
    });


    const note = await noteDoc.save();
    const mobileUser = await MobileUserModel.findOne({
      _id: mobileUserId
    });

    await mobileUser.updateOne({
      notesIds: [...mobileUser._doc.notesIds, note._id]
    })

    res.json(note);

  } catch (err) {
    console.log(err);
    res.status(500).json({
      message: 'Не вдалося створити нотатку'
    });
  }
};
