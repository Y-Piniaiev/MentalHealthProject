import React from 'react';
import clsx from 'clsx';
import { useDispatch } from 'react-redux';
import { Link } from 'react-router-dom';
import IconButton from '@mui/material/IconButton';
import DeleteIcon from '@mui/icons-material/Clear';
import EditIcon from '@mui/icons-material/Edit';
import EyeIcon from '@mui/icons-material/RemoveRedEyeOutlined';

import styles from './Training.module.scss';
import { UserInfo } from '../UserInfo';
import { TrainingSkeleton } from './Skeleton';
import { fetchRemoveTraining } from '../../redux/slices/trainings';
import { useTranslation } from 'react-i18next';

export const Training = ({
  id,
  title,
  createdAt,
  imageUrl,
  user,
  viewsCount,
  text,
  task,
  time,
  isFullTraining,
  isLoading,
  isEditable,
}) => {
  const { t } = useTranslation();
  const { i18n } = useTranslation();
  const dispatch = useDispatch();
  if (isLoading) {
    return <TrainingSkeleton />;
  }

  const onClickRemove = () => {
    if (window.confirm(`${t('Are you sure you want to delete training?')}`)) {
      dispatch(fetchRemoveTraining(id));
    }
  };



  return (
    <div className={clsx(styles.root, { [styles.rootFull]: isFullTraining })}>
      {isEditable && (
        <div className={styles.editButtons}>
          <Link to={`/training/${id}/edit`}>
            <IconButton color="primary">
              <EditIcon />
            </IconButton>
          </Link>
          <IconButton onClick={onClickRemove} color="secondary">
            <DeleteIcon />
          </IconButton>
        </div>
      )}
      {imageUrl && (
        <img
          className={clsx(styles.image, { [styles.imageFull]: isFullTraining })}
          src={imageUrl}
          alt={title}
        />
      )}
      <div className={styles.wrapper}>
        <UserInfo {...user} additionalText={createdAt} />
        <div className={styles.indention}>
          <h2 className={clsx(styles.title, { [styles.titleFull]: isFullTraining })}>
            {isFullTraining ? title : <Link to={`/training/${id}`}>{title}</Link>}
          </h2>
          <h2>
            {isFullTraining ? `${t('Description')}` : ''}
          </h2>
          <div className={styles.content}>{text}</div>
          <h2>
            {isFullTraining ? `${t('Task')}` : ''}

          </h2>
          <div className={styles.content}>{task}</div>
          <h2>
            {isFullTraining ? `${t('Execution time')}` : ''}

          </h2>
          <div className={styles.content}>{time}</div>
          <ul className={styles.postDetails}>
            <li>
              <EyeIcon />
              <span>{viewsCount}</span>
            </li>
          </ul>
        </div>
      </div>
    </div >
  );
};
