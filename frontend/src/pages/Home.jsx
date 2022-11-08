import React from 'react';
import { Tab, Tabs, TabList, TabPanel } from 'react-tabs';
import Grid from '@mui/material/Grid';

import { Post } from '../components/Post';
import { Training } from '../components/Training';
import { useDispatch, useSelector } from 'react-redux';
import { fetchPosts } from '../redux/slices/posts';
import { fetchTraining } from '../redux/slices/trainings';
import { selectIsAuth } from "../redux/slices/auth";
import 'react-tabs/style/react-tabs.css';
import { useTranslation } from 'react-i18next';


export const Home = () => {
  const { t } = useTranslation();
  const { i18n } = useTranslation();
  const dispatch = useDispatch();
  const { posts } = useSelector(state => state.posts);
  const { trainings } = useSelector(state => state.trainings);
  const isAuth = useSelector(selectIsAuth);

  const isPostsLoading = posts.status == 'loading';
  const isTrainingsLoading = trainings.status == 'loading';

  React.useEffect(() => {
    dispatch(fetchPosts());
    dispatch(fetchTraining());
  }, [])



  return (
    <>
      <Tabs>
        <TabList>
          <Tab>{t('Articles')}</Tab>
          <Tab>{t('Trainings')}</Tab>
        </TabList>

        <TabPanel>
          <Grid container spacing={4}>
            <Grid item xs={2}></Grid>
            <Grid item xs={8}>
              {(isPostsLoading ? [...Array(5)] : posts.items).map((obj, index) => isPostsLoading ? <Post key={index} isLoading={true} /> : (
                <Post
                  id={obj._id}
                  title={obj.title}
                  imageUrl={obj.imageUrl ? `http://localhost:5000${obj.imageUrl}` : ''}
                  user={obj.user}
                  createdAt={obj.createdAt}
                  viewsCount={obj.viewsCount}
                  isEditable={isAuth}
                />
              ))}
            </Grid>
            <Grid item xs={1}></Grid>
          </Grid>
        </TabPanel>
        <TabPanel>
          <Grid container spacing={4}>
            <Grid item xs={2}></Grid>
            <Grid item xs={8}>
              {(isTrainingsLoading ? [...Array(5)] : trainings.items).map((obj, index) => isTrainingsLoading ? <Training key={index} isLoading={true} /> : (
                <Training
                  id={obj._id}
                  title={obj.title}
                  imageUrl={obj.imageUrl ? `http://localhost:5000${obj.imageUrl}` : ''}
                  user={obj.user}
                  createdAt={obj.createdAt}
                  viewsCount={obj.viewsCount}
                  isEditable={isAuth}
                />
              ))}
            </Grid>
            <Grid item xs={1}></Grid>
          </Grid>
        </TabPanel>
      </Tabs>
    </>
  );
};
