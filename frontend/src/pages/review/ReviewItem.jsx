import React, {useEffect, useState} from 'react';
import {
    Avatar,
    Box,
    Button, CardActions,
    CardHeader,
    CardMedia,
    Typography
} from "@material-ui/core";
import {Rating} from "@material-ui/lab";
import {useTranslation} from "react-i18next";
import {useAuth} from "../../components/hooks/UseAuth";
import ItemImages from "../../components/items/ItemImages";
import {API_URL} from "../../http/http-common";
import {IMAGE_ROUTE} from "../../utils/consts";
import {Card} from "reactstrap";
import userService from "../../services/user.service";
import reviewService from "../../services/ReviewService";

const ReviewItem = (props) => {

    const {t, i18n} = useTranslation();
    const {authUserId, isAdmin, isModerator} = useAuth();
    const isAuthor = authUserId === props.item.userId;
    const [author, setAuthor] = useState("");
    const [reviewsCount, setReviewsCount] = useState(0);
    const {removeImage = Function.prototype} = props;

    // https://stackoverflow.com/a/50607453
    const getFormattedDate = (date) => {
        const DATE_OPTIONS = {year: 'numeric', month: 'short', day: 'numeric'};
        return new Date(date).toLocaleDateString(i18n.language, DATE_OPTIONS);
    }

    const updateReview = (e, review) => {
        e.preventDefault();
        props.update(review);
    }

    const removeReview = (e, reviewId) => {
        e.preventDefault();
        props.remove(reviewId)
    }

    useEffect(() => {
        userService.getAuthor(props.item.userId)
            .then(({data}) => {
                setAuthor(data)
            })
    }, [])

    useEffect(() => {
        reviewService.getCountByUserId(props.item.userId)
            .then(({data}) => {
                setReviewsCount(data)
            })
    }, [])

    return (
        <div>
            <Card className="paperItem" elevation={3}
                  style={!props.item.active ? {backgroundColor: "darkgray"} : {}}
            >
                <CardHeader
                    avatar={
                        <div>
                            <div>
                                <Avatar src={API_URL + IMAGE_ROUTE + author.fileName}/>
                            </div>
                            <div>
                                <Typography
                                    variant="caption">{author && author.authorName}</Typography>
                            </div>
                            <div>
                                <Typography variant="caption">{t('reviews')+ ": " +reviewsCount}</Typography>
                            </div>
                        </div>
                    }
                    title={
                        <Box
                            sx={{
                                display: 'flex',
                                alignItems: 'center',
                            }}
                        >
                            <Rating name="half-rating-read" defaultValue={0} value={props.item.rating}
                                    size="small"
                                    readOnly/>
                            <Box
                                sx={{ml: 1}}><small>{t("published")} {getFormattedDate(props.item.createdAt)}</small></Box>
                        </Box>
                    }
                    subheader={
                        <>
                            <h6> {props.item.title} </h6>
                            <p>{props.item.description}</p>

                        </>
                    }
                />
                {props.item.fileNames.length > 0 &&
                    <CardMedia>
                        <ItemImages images={props.item.fileNames} itemId={props.item.id} promiseInProgress={props.promiseInProgress} removeImage={removeImage}/>
                    </CardMedia>
                }
                {(isAdmin || isAuthor || isAuthor) &&
                    <CardActions>
                        {isModerator &&
                            <Button size="small" color="primary"
                                    onClick={e => updateReview(e, props.item.id)}
                            >
                                {t("edit")}
                            </Button>
                        }
                        {'  '}
                        {(isAdmin || isAuthor) &&
                            <Button size="small" color="secondary"
                                    onClick={e => removeReview(e, props.item)}>
                                {t("delete")}</Button>
                        }
                    </CardActions>
                }
            </Card>
        </div>
    );
};

export default ReviewItem;