import React from "react";
import { useParams } from 'react-router-dom';
import ReactMarkdown from "react-markdown";
import { Training } from "../components/Training";
import axios from "../axios";
import { useTranslation } from 'react-i18next';

export const FullTraining = () => {
    const { t } = useTranslation();
    const { i18n } = useTranslation();
    const [data, setData] = React.useState();
    const [isLoading, setLoading] = React.useState(true);
    const { id } = useParams();

    React.useEffect(() => {
        axios.get(`/training/${id}`)
            .then(res => {
                setData(res.data);
                setLoading(false);
            })
            .catch(err => {
                console.warn(err);
                alert(`${t('Error when receiving training')}`)

            });
    }, []);

    if (isLoading) {
        return <Training isLoading={isLoading} isFullTraining />;
    }

    return (
        <>
            <Training
                id={data._id}
                title={data.title}
                imageUrl={data.imageUrl ? `http://localhost:5000${data.imageUrl}` : ''}
                user={data.user}
                createdAt={data.createdAt}
                viewsCount={data.viewsCount}
                text={data.text}
                time={data.time}
                task={data.task}
                isFullTraining
            >
            </Training>

        </>
    );
};
