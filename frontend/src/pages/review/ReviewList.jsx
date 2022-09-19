import React from 'react';
import {Container} from "@material-ui/core";
import ReviewItem from "./ReviewItem";

const ReviewList = (props) => {

    return (
        <Container>
            {props.reviews.map((review) =>
                <ReviewItem item={review} key={review.id} update={props.update} remove={props.remove} promiseInProgress={props.promiseInProgress}/>
            )
            }
        </Container>
    );
};

export default ReviewList;