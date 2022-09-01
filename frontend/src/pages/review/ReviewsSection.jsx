import React, {useEffect, useState} from 'react';
import reviewService from "../../services/ReviewService";
import ReviewList from "./ReviewList";
import {trackPromise, usePromiseTracker} from "react-promise-tracker";
import {Container} from "@material-ui/core";
import SkeletonCard from "./SceletonCard";
import MyButton from "../../components/UI/button/MyButton";
import MyModal from "../../components/UI/modal/MyModal";
import ReviewEditor from "./ReviewEditor";
import {useTranslation} from "react-i18next";
import MyNotification from "../../components/UI/notification/MyNotification";

const ReviewsSection = (props) => {
    const area = 'reviews';
    const {promiseInProgress} = usePromiseTracker({area});
    const [reviews, setReviews] = useState([]);
    const [modal, setModal] = useState(false);
    const [alert, setAlert] = useState({open: false, message: '', severity: 'info'})
    const {t} = useTranslation();

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
        <>
            <Container maxWidth="md">
                {props.itemId &&
                    <MyButton style={{marginTop: 10}} className={"btn btn-outline-primary ml-2 btn-sm"}
                              onClick={() => setModal(true)}>
                        {t('write review')}
                    </MyButton>

                }
                <hr style={{margin: '15px 0'}}/>
                {promiseInProgress
                    ? <SkeletonCard listsToRender={10}/>
                    : <ReviewList reviews={reviews}/>
                }
            </Container>
            <MyModal visible={modal} setVisible={setModal}>
                <ReviewEditor itemId={props.itemId} setAlert={setAlert}
                              modal={modal} setModal={setModal}/>
            </MyModal>
            <MyNotification open={alert.open} setOpen={setAlert} message={alert.message} severity={alert.severity}/>
        </>
    );
};

export default ReviewsSection;