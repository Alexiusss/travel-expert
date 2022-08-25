import React, {useEffect, useState} from 'react';
import reviewService from 'services/ReviewService';
import {Paper, Container, Box} from "@material-ui/core";
import {Rating} from "@material-ui/lab";
import {useTranslation} from "react-i18next";

const ReviewList = () => {
    const [reviews, setReviews] = useState([]);
    const {t, i18n} = useTranslation();

    useEffect(() => {
        reviewService.getAll()
            .then(({data}) => {
                setReviews(data.content)
            })
            .catch(console.error)
    }, [setReviews])

    // https://stackoverflow.com/a/50607453
    const getFormattedDate = (date) => {
        const DATE_OPTIONS = {year: 'numeric', month: 'short', day: 'numeric'};
        return new Date(date).toLocaleDateString(i18n.language, DATE_OPTIONS);
    }

    return (
        <Container maxWidth="md">
            {reviews.map((review) =>
                <Paper elevation={3} key={review.id}>
                    <Box
                        sx={{
                            display: 'flex',
                            alignItems: 'center',
                        }}
                    >
                        <Rating name="half-rating-read" defaultValue={0} value={review.rating} size="small" readOnly/>
                        <Box sx={{ml: 1}}><small>{t("published")} {getFormattedDate(review.createdAt)}</small></Box>
                    </Box>
                    <h5> {review.title} </h5>
                    <p>{review.description}</p>
                </Paper>
            )
            }
        </Container>
    );
};

export default ReviewList;