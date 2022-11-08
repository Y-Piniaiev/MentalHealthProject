import React from 'react';
import TextField from '@mui/material/TextField';
import Paper from '@mui/material/Paper';
import Button from '@mui/material/Button';
import SimpleMDE from 'react-simplemde-editor';

import 'easymde/dist/easymde.min.css';
import { useSelector } from 'react-redux';
import { selectIsAuth } from "../../redux/slices/auth";
import styles from './AddTraining.module.scss';
import { useNavigate, Navigate, useParams } from 'react-router-dom';
import axios from '../../axios';
import { useTranslation } from 'react-i18next';

export const AddTraining = () => {
  const { t } = useTranslation();
  const { i18n } = useTranslation();
  const { id } = useParams();
  const navigate = useNavigate();
  const isAuth = useSelector(selectIsAuth);
  const [isLoading, setLoading] = React.useState(false);
  const [text, setText] = React.useState('');
  const [task, setTask] = React.useState('');
  const [time, setTime] = React.useState('');
  const [title, setTitle] = React.useState('');
  const [imageUrl, setImageUrl] = React.useState('');
  const inputFileRef = React.useRef(null);
  const isEditing = Boolean(id);

  const handleChangeFile = async (event) => {
    try {
      const formData = new FormData();
      const file = event.target.files[0];
      formData.append('image', file);
      const { data } = await axios.post('/upload', formData);
      setImageUrl(data.url);
    } catch (err) {
      console.warn(err);
      alert(`${t('An error occured while downloading the file')}`);
    }
  };

  const onClickRemoveImage = () => {
    setImageUrl('');
  };

  const onChange1 = React.useCallback((value) => {
    setText(value);
  }, []);
  const onChange2 = React.useCallback((value) => {
    setTask(value);
  }, []);

  const onSubmit = async () => {
    try {
      setLoading(true);
      const fields = {
        title, imageUrl, text, task, time,
      };
      const { data } = isEditing ? await axios.patch(`/training/${id}`, fields) : await axios.post('/training', fields);
      const _id = isEditing ? id : data._id;
      navigate(`/training/${_id}`);
    } catch (err) {
      console.warn(err);
      alert(`${t('Error when creating training')}`);
    }
  };

  React.useEffect(() => {
    if (id) {
      axios.get(`/training/${id}`).then(({ data }) => {
        setTitle(data.title);
        setText(data.text);
        setTask(data.task);
        setTime(data.time);
        setImageUrl(data.imageUrl);
      }).catch(err => {
        console.warn(err);
        alert(`${t('Error when receiving training')}`);
      });
    }
  }, []);

  const options = React.useMemo(
    () => ({
      spellChecker: false,
      maxHeight: '400px',
      autofocus: true,
      placeholder: `${t('Enter text...')}`,
      status: false,
      autosave: {
        enabled: true,
        delay: 1000,
      },
    }),
    [],
  );

  if (!window.localStorage.getItem('token') && !isAuth) {
    return <Navigate to="/" />
  }

  return (
    <Paper style={{ padding: 30 }}>
      <Button onClick={() => inputFileRef.current.click()} variant="outlined" size="large">
        {t('Download preview')}
      </Button>
      <input ref={inputFileRef} type="file" onChange={handleChangeFile} hidden />
      {imageUrl && (
        <Button variant="contained" color="error" onClick={onClickRemoveImage}>
          {t('Delete')}
        </Button>
      )}
      {imageUrl && (
        <img className={styles.image} src={`http://localhost:5000${imageUrl}`} alt="Uploaded" />
      )}
      <br />
      <br />
      <h2>
        {t('Training title')}
      </h2>
      <TextField
        variant="standard"
        placeholder={t('Training title')}
        value={title}
        onChange={(e) => setTitle(e.target.value)}
        fullWidth
      />
      <h2>
        {t('Description of the training')}
      </h2>
      <SimpleMDE className={styles.editor} value={text} onChange={onChange1} options={options} />
      <h2>
        {t('Training tasks')}
      </h2>
      <SimpleMDE className={styles.editor} value={task} onChange={onChange2} options={options} />
      <h2>
        {t('Training time')}
      </h2>
      <TextField
        variant="standard"
        placeholder={t('Training time')}
        value={time}
        onChange={(e) => setTime(e.target.value)}
        fullWidth
      />
      <h2>
      </h2>
      <div className={styles.buttons}>
        <Button onClick={onSubmit} size="large" variant="contained">
          {isEditing ? `${t('Save')}` : `${t('Create')}`}
        </Button>
        <a href="/">
          <Button size="large">{t('Cancel')}</Button>
        </a>
      </div>
    </Paper>
  );
};
