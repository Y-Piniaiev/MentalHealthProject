import React from 'react';
import Typography from '@mui/material/Typography';
import TextField from '@mui/material/TextField';
import Paper from '@mui/material/Paper';
import Button from '@mui/material/Button';
import Avatar from '@mui/material/Avatar';
import { useDispatch, useSelector } from "react-redux";
import { useForm } from 'react-hook-form';
import { fetchRegister, selectIsAuth } from "../../redux/slices/auth";
import { Navigate } from 'react-router-dom';
import styles from './Login.module.scss';
import { useTranslation } from 'react-i18next';


export const Registration = () => {
  const { t } = useTranslation();
  const { i18n } = useTranslation();
  const isAuth = useSelector(selectIsAuth);
  const dispatch = useDispatch();
  const { register, handleSubmit, formState: { errors, isValid } } = useForm({
    defaultValues: {
      email: '',
      password: '',
      education: '',
      role: 'admin',
      chiefKey: '',
    },
    mode: 'onChange'
  });

  const onSubmit = async (values) => {
    const data = await dispatch(fetchRegister(values));

    if (!data.payload) {
      return alert(`${t('Failed to register')}`)
    }

    if ('token' in data.payload) {
      window.localStorage.setItem('token', data.payload.token);
    }
  };

  if (isAuth) {
    return <Navigate to="/" />;
  }

  return (
    <Paper classes={{ root: styles.root }}>
      <Typography classes={{ root: styles.title }} variant="h5">
        {t('Creating account')}
      </Typography>
      <div className={styles.avatar}>
        <Avatar sx={{ width: 100, height: 100 }} />
      </div>
      <form onSubmit={handleSubmit(onSubmit)}>
        <TextField className={styles.field} label="E-Mail"
          error={Boolean(errors.email?.message)}
          helperText={errors.email?.message}
          type="email"
          {...register('email', { required: `${t('Enter the e-mail')}` })}
          fullWidth />
        <TextField className={styles.field} label={t('Password')}
          error={Boolean(errors.password?.message)}
          helperText={errors.password?.message}
          type="password"
          {...register('password', { required: `${t('Enter the password')}` })}
          fullWidth />
        <TextField className={styles.field} label={t('Education')}
          error={Boolean(errors.education?.message)}
          helperText={errors.education?.message}
          {...register('education', { required: `${t('Enter the education')}` })}
          fullWidth />
        <TextField className={styles.field} label={t('Master key')}
          error={Boolean(errors.chiefKey?.message)}
          helperText={errors.chiefKey?.message}
          type="password"
          {...register('chiefKey', { required: `${t('Enter the master key')}` })}
          fullWidth />
        <Button disabled={!isValid} type="submit" size="large" variant="contained" fullWidth>
          {t('Register')}
        </Button>
      </form>


    </Paper>
  );
};
