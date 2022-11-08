import jwt from 'jsonwebtoken';
import bcrypt from 'bcrypt';
import UserModel from '../models/User.js';
import MobileUserModel from '../models/MobileUser.js';
import AdministratorModel from '../models/Administrator.js';
import { ROLES } from '../utils/roles.js';

export const register = async (req, res) => {

    const { role: originRole } = req.body;
    const role = originRole?.toLowerCase();

    if (role !== undefined && role !== ROLES.USER && role !== ROLES.ADMIN) {

        return res.status(500).json({
            message: 'Bad request'
        });
    }
    if (role !== ROLES.USER && req.body?.chiefKey !== process.env.CHIEF_PASSWORD) {

        return res.status(403).json({
            message: 'Forbidden'
        });
    }

    try {

        const password = req.body.password;
        const salt = await bcrypt.genSalt(10);
        const hash = await bcrypt.hash(password, salt)

        const doc = new UserModel({
            email: req.body.email,
            passwordHash: hash,
            role: role ?? ROLES.USER,
        });

        const user = await doc.save();

        switch (role) {
            case undefined:
            case (ROLES.USER):
                const { name, birth } = req.body;
                const userDoc = new MobileUserModel({
                    userId: user._id,
                    name, birth
                })
                await userDoc.save();
                break;
            case (ROLES.ADMIN):
                const { education } = req.body;
                const adminDoc = new AdministratorModel({
                    userId: user._id,
                    education,
                })
                await adminDoc.save();
                break;

        }

        const token = jwt.sign(
            { _id: user._id }, 'secret123', { expiresIn: '30d', },);

        const { passwordHash, ...userData } = user._doc;

        res.json({
            ...userData,
            token
        });

    } catch (err) {

        console.log(err);
        return res.status(500).json({
            message: 'Рагістрація не успішна'
        });
    }
}

export const login = async (req, res) => {
    try {
        const user = await UserModel.findOne({
            email: req.body.email
        })

        if (!user) {
            return res.status(404).json({
                message: 'Користувача не знайдено'
            });
        }

        const isValidPass = await bcrypt.compare(req.body.password, user._doc.passwordHash);

        if (!isValidPass) {
            return res.status(400).json({
                message: 'Неправильний логін або пароль'
            });
        }

        const token = jwt.sign(
            {
                _id: user._id
            },
            'secret123',
            {
                expiresIn: '30d',
            },
        );

        const { passwordHash, ...userData } = user._doc;

        res.json({
            ...userData,
            token
        });

    } catch (err) {
        console.log(err);
        return res.status(500).json({
            message: 'Авторизація не успішна'
        });
    }
}

export const getMe = async (req, res) => {
    try {
        const user = await UserModel.findById(req.userId);

        if (!user) {
            return res.status(404).json({
                message: 'Користувача не знайдено'
            })
        }
        let additionalInfo = null;

        switch (user._doc.role) {
            case (ROLES.USER):
                additionalInfo = (await MobileUserModel.findOne({ userId: user._id }))?._doc
                break;
            case (ROLES.ADMIN):
                additionalInfo = (await AdministratorModel.findOne({ userId: user._id }))?._doc
                break;
        }

        const { passwordHash, ...userData } = user._doc;

        if (additionalInfo) {
            const { _id, userId, ...editedAdditionalInfo } = additionalInfo;
            return res.status(200).json({ ...userData, info: editedAdditionalInfo })
        }

        res.json(userData);

    } catch (err) {
        console.log(err);
        return res.status(500).json({
            message: 'Немає доступа'
        });
    }
}