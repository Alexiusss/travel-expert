import React from 'react';
import {Container} from "@material-ui/core";
import ReviewItem from "./ReviewItem";

const ReviewList = (props) => {
    const {author, reviewsCount, removeImage = Function.prototype} = props;

    return (
        <Container style={{padding: 5}}>
            {props.reviews.map((review) =>
                <ReviewItem
                    item={review}
                    key={review.id}
                    update={props.update}
                    like={props.like}
                    remove={props.remove}
                    promiseInProgress={props.promiseInProgress}
                    removeImage={removeImage}
                    author={author}
                    reviewsCount={reviewsCount}
                />
            )
            }
        </Container>
    );
};

export default ReviewList;