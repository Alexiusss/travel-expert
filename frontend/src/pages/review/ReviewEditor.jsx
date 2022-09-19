import React, {useEffect, useState} from 'react';
import reviewService from '../../services/ReviewService'
import {useTranslation} from "react-i18next";
import {Box, Button, Container} from "@material-ui/core";
import {useAuth} from "../../components/hooks/UseAuth";
import {Rating} from "@material-ui/lab";
import {getLocalizedErrorMessages} from "../../utils/consts";
import imageService from "../../services/ImageService";

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
    const [images, setImages] = useState([]);
    const {t} = useTranslation();
    const [fileNames, setFileNames] = useState([]);

    const saveReview = (e) => {
        e.preventDefault();
        let review = {title, description, rating, active, id, itemId, fileNames, userId}
        console.log(review)

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

    const uploadImages = (images) => {
        imageService.post(images)
            .then(response => {
                setFileNames(prevFileNames => prevFileNames.concat(response.data))
            })
            .catch(error => {
                openAlert(getLocalizedErrorMessages(error.response.data.message), "error");
            })
    }

    const cleanForm = () => {
        setTitle('');
        setDescription('');
        setRating(0);
        setHover(-1)
        setId(null);
        setUserId(null);
        setActive(false)
        setImages([]);
        setFileNames([]);
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
    }, [props.modal])

    useEffect(() => {
        const review = props.reviewFomDB;
        if (review) {
            setTitle(review.title)
            setDescription(review.description)
            setRating(review.rating)
            setActive(review.active)
            setItemId(review.itemId)
            setFileNames(review.fileNames || [])
            setId(review.id)
            setUserId(review.userId)
        }
    }, [props.reviewFomDB])

    useEffect(() => {
        if (props.modal && images.length > 0) {
            uploadImages(images);
        }
    }, [images])

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

                <Button variant="contained" component="label">
                    {t('upload image')}
                    <input hidden accept="image/*" multiple type="file"
                           onChange={e => setImages(Array.from(e.target.files))}/>
                </Button>
                <small>{fileNames.length ? fileNames : t('not uploaded')}</small>

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