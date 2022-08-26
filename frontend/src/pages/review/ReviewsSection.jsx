import React, {useEffect, useState} from 'react';

import reviewService from "../../services/ReviewService";
import ReviewList from "./ReviewList";

const ReviewsSection = (props) => {
    const [reviews, setReviews] = useState([]);

    useEffect(() => {
        reviewService.getAll()
            .then(({data}) => {
                setReviews(data.content)
            })
            .catch(console.error)
    }, [setReviews])

    return (
        <ReviewList reviews={reviews}/>
    );
};

export default ReviewsSection;