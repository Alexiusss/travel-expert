import React, {useEffect, useState} from 'react';
import reviewService from "../../services/ReviewService";
import ReviewList from "./ReviewList";
import {trackPromise, usePromiseTracker} from "react-promise-tracker";
import {Container, Tooltip} from "@material-ui/core";
import SkeletonCard from "./SceletonCard";
import MyButton from "../../components/UI/button/MyButton";
import MyModal from "../../components/UI/modal/MyModal";
import ReviewEditor from "./ReviewEditor";
import {useTranslation} from "react-i18next";
import MyNotification from "../../components/UI/notification/MyNotification";
import {getLocalizedErrorMessages} from "../../utils/consts";
import ItemFilter from "../../components/items/ItemFilter";
import MySelect from "../../components/UI/select/MySelect";
import Pagination from "@material-ui/lab/Pagination";
import ItemRating from "../../components/items/ItemRating";
import imageService from "../../services/ImageService";
import {useAuth} from "../../components/hooks/UseAuth";

const ReviewsSection = (props) => {
    const area = 'reviews';
    const {promiseInProgress} = usePromiseTracker({area});
    const [reviews, setReviews] = useState([]);
    const [reviewsCount, setReviewsCount] = useState(0);
    const [reviewFromDB, setReviewFromDB] = useState(null);
    const [modal, setModal] = useState(false);
    const [alert, setAlert] = useState({open: false, message: '', severity: 'info'})
    const [totalPages, setTotalPages] = useState(0);
    const [page, setPage] = useState(1);
    const [size, setSize] = useState(12);
    const pageSizes = [20, 50, 100];
    const [filter, setFilter] = useState({ratingFilters: [], query: ''})
    const {t} = useTranslation();
    const {isAuth, authUserId} = useAuth();

    useEffect(() => {
        if (props.itemId == null) {
            trackPromise(
                reviewService.getAll(size, page, filter), area)
                .then(({data}) => {
                    setReviews(data.content);
                    setTotalPages(data.totalPages);
                });
        } else {
            trackPromise(
                reviewService.getAllByItemId(props.itemId, size, page, filter), area)
                .then(({data}) => {
                    setReviews(data.content);
                    setTotalPages(data.totalPages);
                    if (filter.ratingFilters.length === 0) {
                        setReviewsCount(data.content.length);
                    }
                });
        }
        return () => {
            setReviews([]);
            setTotalPages(0);
        }
    }, [setReviews, page, size, filter])

    const update = (id) => {
        reviewService.get(id)
            .then(response => {
                setReviewFromDB(response.data);
                setModal(true);
            })
            .catch(
                error => {
                    openAlert(getLocalizedErrorMessages(error.response.data.message), "error");
                });
    }

    const updateReview = (updatedReview) => {
        setModal(false);
        setReviews(reviews => {
            return reviews.map(review => {
                return review.id === updatedReview.id
                    ?
                    {
                        ...review,
                        title: updatedReview.title,
                        description: updatedReview.description,
                        active: updatedReview.active,
                        filenames: updatedReview.fileNames,
                    }
                    :
                    review
            })
        })
        openAlert([t('record saved')], "success")
    }

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

    const removeReview = (review) => {
        Promise.all([imageService.removeAllByFileNames(review.fileNames), reviewService.delete(review.id)])
            .then(() => {
                setReviews(reviews.filter(r => r.id !== review.id));
                openAlert([t('record deleted')], "success");
            })
            .catch(
                error => {
                    openAlert(getLocalizedErrorMessages(error.response.data.message), "error");
                });
    }

    const openAlert = (msg, severity) => {
        setAlert({severity: severity, message: msg, open: true})
    }

    const changePage = (event, value) => {
        setPage(value);
    }

    const changeSize = (event) => {
        setSize(event.target.value);
        setPage(1);
    }

    const setRatingFilter = (e) => {
        let value = +e.target.value;
        let ratingFilters = filter.ratingFilters;

        if (ratingFilters.includes(value)) {
            setFilter({...ratingFilters, ratingFilters: ratingFilters.filter(r => r !== value), query: ''})
        } else {
            setFilter({...ratingFilters, ratingFilters: [...ratingFilters, value], query: ''})
        }
    }

    const removeImage = (fileName, itemId) => {
        const updatedReview = reviews.find(review => review.id === itemId);
        updatedReview.fileNames = updatedReview.fileNames.filter(imageName => imageName !== fileName);
        Promise.all([reviewService.update(updatedReview.id, updatedReview), imageService.remove(fileName)])
            .then(() => updateReview(updatedReview))
            .catch(
                error => {
                    openAlert(getLocalizedErrorMessages(error.response.data.message), "error");
                });
    }

    return (
        <>
            <Container maxWidth="md">
                {props.itemId && (
                    <>
                        <div style={{display: "flex", justifyContent: 'space-between'}}>
                            <h4>Reviews <span>({reviews.length})</span></h4>
                            {!isAuth ?
                                <Tooltip title={t('only registered users can leave a review')}>
                                   <span>
                                        <MyButton disabled={true} props={"disabled"} style={{marginTop: 10}}
                                                  className={"btn btn-outline-primary ml-2 btn-sm"}>
                                            {t('write review')}
                                        </MyButton>
                                   </span>
                                </Tooltip>
                                :
                                <MyButton style={{marginTop: 10}} className={"btn btn-outline-primary ml-2 btn-sm"}
                                          onClick={() => setModal(true)}>
                                    {t('write review')}
                                </MyButton>
                            }
                        </div>
                        <hr style={{margin: '15px 0'}}/>
                        {(props.rating && reviewsCount > 0) &&
                            <div style={{paddingLeft: '3%'}}>
                                <ItemRating rating={props.rating} reviewsCount={reviewsCount}
                                            setRatingFilter={setRatingFilter}/>
                            </div>
                        }
                    </>
                )
                }
                {reviewsCount > 0 &&
                    <>
                        <ItemFilter filter={filter} setFilter={setFilter}/>
                        <MySelect size={size} changeSize={changeSize} pageSizes={pageSizes}/>
                    </>
                }
                {promiseInProgress
                    ? <SkeletonCard listsToRender={10}/>
                    :
                    <>
                        <ReviewList reviews={reviews}
                                    update={update}
                                    like={like}
                                    remove={removeReview}
                                    removeImage={removeImage}
                                    promiseInProgress={promiseInProgress}/>
                        {(reviewsCount > 0 || reviews) &&
                            <Pagination count={totalPages} page={page} onChange={changePage} shape="rounded"
                                        className="pagination"/>
                        }
                    </>
                }
            </Container>
            <MyModal visible={modal} setVisible={setModal}>
                <ReviewEditor itemId={props.itemId} setAlert={setAlert}
                              modal={modal} setModal={setModal}
                              reviewFomDB={reviewFromDB}
                              updateReview={updateReview}
                />
            </MyModal>
            <MyNotification open={alert.open} setOpen={setAlert} message={alert.message} severity={alert.severity}/>
        </>
    );
};

export default ReviewsSection;