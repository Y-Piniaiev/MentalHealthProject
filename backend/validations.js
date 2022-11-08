import { body } from 'express-validator'

export const registerValidation = [
  body('email', 'Неправильний формат пошти').isEmail(),
  body('password', 'Пароль повинен містити не менше 6 символів').isLength({
    min: 6,
  }),
  body('role', 'Неправильно введено роль').optional().isString(),
]

export const postCreateValidation = [
  body('title', 'Введіть заголовок статті').isLength({ min: 3 }).isString(),
  body('text', 'Введіть текст статті').isLength({ min: 100 }).isString(),
  body('imageUrl', 'Неправильне посилання на картинку').optional().isString(),
]

export const trainingCreateValidation = [
  body('title', 'Введіть заголовок тренінгу').isLength({ min: 3 }).isString(),
  body('text', 'Введіть опис тренінгу').isLength({ min: 100 }).isString(),
  body('task', 'Введіть завдання тренінгу').isLength({ min: 100 }).isString(),
  body('time', 'Введіть час виконання тренінгу').isInt(),
  body('imageUrl', 'Неправильне посилання на картинку').optional().isString(),
]

export const noteValidation = [
  body('title', 'Введіть заголовок запису').isLength({ min: 3 }).isString(),
  body('text', 'Введіть текст запису').isLength({ min: 10 }).isString(),
]

export const createTestValidation = [
  body('name', 'Введіть назву тесту').isString(),
  body('description', 'Введіть опис тесту').optional().isLength({ min: 100 }).isString(),
  body('timeForPass', 'Введіть час проходження тесту').optional().isString(),
  body('questions', 'Введіть текст питання').isArray(),
]

export const updateTestValidation = [
  body('name', 'Введіть назву тесту').optional().isString(),
  body('description', 'Введіть опис тесту').optional().isLength({ min: 100 }).isString(),
  body('timeForPass', 'Введіть час проходження тесту').optional().isString(),
  body('questions', 'Введіть текст питання').optional().isArray(),
]