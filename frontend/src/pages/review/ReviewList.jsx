import React from 'react';
import {Container} from "@material-ui/core";
import ReviewItem from "./ReviewItem";
import reviewService from "../../services/ReviewService";
import {useAuth} from "../../components/hooks/UseAuth";
import SkeletonReview from "./SkeletonReview";

const ReviewList = (props) => {
    const {
        author,
        reviewsCount,
        reviews = [],
        isLoaded = false,
        removeImage = Function.prototype,
        setReviews = Function.prototype,
    } = props;
    const {authUserId} = useAuth();

    const like = (reviewId, isAuthUserLiked) => {
        reviewService.like(reviewId, authUserId)
            .then(() => {
                setReviews(reviews.map(review => {
                    if (review.id === reviewId) {
                        let likes = review.likes;
                        if (isAuthUserLiked) {
                            return {
                                ...review,
                                likes: likes.filter(userId => userId !== authUserId)
                            }
                        } else {
                            return {
                                ...review,
                                likes: [...likes, authUserId]
                            }
                        }
                    }
                    return review;
                }))
            })
    }

    return (
        <>
            {!isLoaded
                ?
                <SkeletonReview listsToRender={3}/>
                :
                <Container style={{padding: 5}}>
                    {reviews.map((review) =>
                        <ReviewItem
                            item={review}
                            key={review.id}
                            update={props.update}
                            like={like}
                            remove={props.remove}
                            promiseInProgress={props.promiseInProgress}
                            removeImage={removeImage}
                            author={author}
                            reviewsCount={reviewsCount}
                        />
                    )
                    }
                </Container>
            }
        </>
    );
};

export default ReviewList;