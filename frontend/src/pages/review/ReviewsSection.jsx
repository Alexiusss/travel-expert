import React, {useEffect, useState} from 'react';
import reviewService from "../../services/ReviewService";
import ReviewList from "./ReviewList";
import {trackPromise, usePromiseTracker} from "react-promise-tracker";
import {Container} from "@material-ui/core";
import SkeletonCard from "./SceletonCard";

const ReviewsSection = (props) => {
    const area = 'reviews';
    const {promiseInProgress} = usePromiseTracker({area});
    const [reviews, setReviews] = useState([]);

    useEffect(() => {
        if (props.itemId == null) {
            trackPromise(
                reviewService.getAll(), area)
                .then(({data}) => {
                    setReviews(data.content);
                });
        } else {
            trackPromise(
                reviewService.getAllByItemId(props.itemId, 20, 1), area)
                .then(({data}) => {
                    setReviews(data.content);
                });
        }
    }, [setReviews])

    return (
        <Container maxWidth="md">
            {promiseInProgress
                ? <SkeletonCard listsToRender={10}/>
                : <ReviewList reviews={reviews}/>
            }
        </Container>
    )
        ;
};

export default ReviewsSection;