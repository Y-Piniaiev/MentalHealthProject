import TestModel from '../models/Test.js'

export const getAll = async (req, res) => {
    try {
        const tests = await TestModel.find()

        res.json(tests)

    } catch (err) {
        console.log(err)
        res.status(500).json({
            message: 'Не вдалося знайти тести',
        })
    }
}

export const getOne = async (req, res) => {
    try {
        const testId = req.params.id;

        TestModel.findOne({
            _id: testId,
        }, (err, doc) => {
            if (err) {
                console.log(err);
                return res.status(500).json({
                    message: 'Не вдалося повернути тест'
                });
            }

            if (!doc) {
                return res.status(404).json({
                    message: 'Тест не знайдено'
                })
            }

            res.json(doc);
        }
        )
    } catch (err) {
        console.log(err)
        res.status(500).json({
            message: 'Не вдалося знайти тести',
        })
    }
}

export const remove = async (req, res) => {
    try {
        const testId = req.params.id

        TestModel.findOneAndDelete(
            {
                _id: testId,
            },
            (err, doc) => {
                if (err) {
                    console.log(err)
                    return res.status(500).json({
                        message: 'Не вдалося повернути тест',
                    })
                }

                if (!doc) {
                    return res.status(404).json({
                        message: 'Тест не знайдено',
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
            message: 'Не вдалося знайти тести',
        })
    }
}

export const update = async (req, res) => {
    try {
        const testId = req.params.id

        const test = await TestModel.findOneAndUpdate({ _id: testId }, req.body, { new: true })

        res.json({
            success: true,
        })
    } catch (err) {
        console.log(err)
        res.status(500).json({
            message: 'Не вдалося оновити тест',
        })
    }
}

export const create = async (req, res) => {
    const fields = Object.fromEntries(Object.entries(req.body).filter(([_, value]) => !!value))
    try {
        const testDoc = new TestModel(fields);
        const test = await testDoc.save();
        res.json(test);

    } catch (err) {
        console.log(err)
        res.status(500).json({
            message: 'Не вдалося створити тест',
        })
    }
}
