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
        trackPromise(
            reviewService.getAll(), area)
            .then(({data}) => {
                setReviews(data.content);
            });
    }, [setReviews])

    return (
        <Container>
            {promiseInProgress
                ? <SkeletonCard listsToRender={10}/>
                : <ReviewList reviews={reviews}/>
            }
        </Container>
    )
        ;
};

export default ReviewsSection;