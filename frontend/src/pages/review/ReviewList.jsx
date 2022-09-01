import React from 'react';
import {Container} from "@material-ui/core";
import ReviewItem from "./ReviewItem";

const ReviewList = (props) => {

    return (
        <Container>
            {props.reviews.map((review) =>
                <ReviewItem item={review} key={review.id} remove={props.remove}/>
            )
            }
        </Container>
    );
};

export default ReviewList;