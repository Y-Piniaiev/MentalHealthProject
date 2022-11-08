import Container from "@mui/material/Container";
import { Routes, Route } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { Header } from "./components";
import { Home, FullPost, Registration, AddPost, Login, FullTraining, AddTraining } from "./pages";
import React from "react";
import { fetchAuthMe, selectIsAuth } from "./redux/slices/auth";

function App() {
  const dispatch = useDispatch();
  const isAuth = useSelector(selectIsAuth);

  React.useEffect(() => {
    dispatch(fetchAuthMe());
  }, []);

  return (
    <>
      <Header />
      <Container maxWidth="lg">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/posts/:id" element={<FullPost />} />
          <Route path="/posts/:id/edit" element={<AddPost />} />
          <Route path="/add-post" element={<AddPost />} />
          <Route path="/training/:id" element={<FullTraining />} />
          <Route path="/training/:id/edit" element={<AddTraining />} />
          <Route path="/add-training" element={<AddTraining />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Registration />} />
        </Routes>
      </Container>
    </>
  );
}

export default App;
