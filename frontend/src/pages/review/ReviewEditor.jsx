import React, {useEffect, useState} from 'react';
import reviewService from '../../services/ReviewService'
import {useTranslation} from "react-i18next";
import {Box, Container} from "@material-ui/core";
import {useAuth} from "../../components/hooks/UseAuth";
import {Rating} from "@material-ui/lab";
import {getLocalizedErrorMessages} from "../../utils/consts";

const ReviewEditor = (props) => {
    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [rating, setRating] = useState(0);
    const [hover, setHover] = useState(-1);
    const [active, setActive] = useState(false);
    const [id, setId] = useState(null);
    const [userId, setUserId] = useState('');
    const [itemId, setItemId] = useState(props.itemId)
    const {authUserId, isAdmin, isModerator} = useAuth();
    const labels = {
        0: 'set rating',
        1: 'useless',
        2: 'poor',
        3: 'ok',
        4: 'good',
        5: 'excellent',
    };
    const {t} = useTranslation();

    const saveReview = (e) => {
        e.preventDefault();
        let review = {title, description, rating, active, id, itemId, userId}

        if (id) {
            reviewService.update(id, review)
                .then(() => {
                    props.updateReview(review)
                    cleanForm();
                })
                .catch(error => {
                    openAlert(getLocalizedErrorMessages(error.response.data.message), "error");
                })
        } else {
            review.userId = authUserId;
            reviewService.create(review)
                .then(response => {
                    cleanForm();
                    props.setModal(false)
                    openAlert([t('record saved')], "success");
                })
                .catch(error => {
                    openAlert(getLocalizedErrorMessages(error.response.data.message), "error");
                })
        }
    }

    const cleanForm = () => {
        setTitle('');
        setDescription('');
        setRating(0);
        setHover(-1)
        setId(null);
        setUserId(null);
        setActive(false)
    }

    const openAlert = (msg, severity) => {
        props.setAlert({severity: severity, message: msg, open: true})
    }

    const getLabelText = (rating) => {
        return `${rating} Star${rating !== 1 ? 's' : ''}, ${labels[rating]}`;
    }

    useEffect(() => {
        if (!props.modal) {
            cleanForm()
        }
    })

    useEffect(() => {
        const review = props.reviewFomDB;
        if (review) {
            setTitle(review.title)
            setDescription(review.description)
            setRating(review.rating)
            setActive(review.active)
            setItemId(review.itemId)
            setId(review.id)
            setUserId(review.userId)
        }
    }, [props.reviewFomDB])


    return (
        <Container>
            <h4>{t("review editor")}</h4>
            <hr/>
            <form>
                <div className="form-group">
                    <input
                        type="text"
                        className="form-control col-4"
                        id="title"
                        value={title}
                        onChange={e => setTitle(e.target.value)}
                        placeholder={t("enter title")}
                    />
                </div>
                <div className="form-group" style={{marginTop: 5}}>
                    <textarea
                        className="form-control col-4"
                        id="description"
                        value={description}
                        onChange={e => setDescription(e.target.value)}
                        placeholder={t("enter description")}
                    />
                </div>
                <Box
                    sx={{
                        display: 'flex',
                        marginTop: 10,
                    }}
                >
                    <Rating
                        disabled={id !== null}
                        name="hover-feedback"
                        value={rating}
                        getLabelText={getLabelText}
                        onChange={(e, newRating) => setRating(newRating)}
                        onChangeActive={(e, newHover) => setHover(newHover)}
                    />
                    <Box sx={{ml: 2}}>{t(labels[hover !== -1 ? hover : rating])}</Box>
                </Box>
                {(isAdmin || isModerator) && (
                    <div className="form-group" style={{marginTop: 10}}>
                        <label>
                            <input
                                type="checkbox"
                                id="active"
                                checked={active}
                                onChange={e => setActive(e.target.checked)}
                            />
                            {'  '}{t('publish')}?
                        </label>
                    </div>
                )}
                <div>
                    <button onClick={(e => saveReview(e))} className="btn btn-outline-primary ml-2 btn-sm"
                            style={{marginTop: 10}}>{t("save")}
                    </button>
                </div>
            </form>
        </Container>
    );
};

export default ReviewEditor;