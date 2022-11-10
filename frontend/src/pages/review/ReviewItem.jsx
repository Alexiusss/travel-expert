import React, {useEffect, useState} from 'react';
import {
    Avatar,
    Box,
    Button, CardActions,
    CardHeader,
    CardMedia,
    IconButton,
    Typography
} from "@material-ui/core";
import {Rating} from "@material-ui/lab";
import {useTranslation} from "react-i18next";
import {useAuth} from "../../components/hooks/UseAuth";
import ItemImages from "../../components/items/ItemImages";
import {API_URL} from "../../http/http-common";
import {getFormattedDate, IMAGE_ROUTE} from "../../utils/consts";
import {Card} from "reactstrap";
import userService from "../../services/user.service";
import reviewService from "../../services/ReviewService";
import ProfilePopover from "../../user/profile/ProfilePopover";
import './ReviewItem.css';

const ReviewItem = (props) => {

    const {t, i18n} = useTranslation();
    const {authUserId, isAuth, isAdmin, isModerator} = useAuth();
    const {id, userId, createdAt, title, description, fileNames = [], active, rating, likes = []} = props.item;
    const {update = Function.prototype, removeImage = Function.prototype, remove = Function.prototype, like = Function.prototype} = props;
    const isAuthor = authUserId === userId;
    const [author, setAuthor] = useState("");
    const [reviewsCount, setReviewsCount] = useState(0);
    const isAuthUserLiked = likes.includes(authUserId);
    const likesCount = likes.length || 0;

    const [anchorEl, setAnchorEl] = useState(null);
    const [userRating, setUserRating] = useState({});

    const updateReview = (e, review) => {
        e.preventDefault();
        update(review);
    }

    const removeReview = (e, reviewId) => {
        e.preventDefault();
        remove(reviewId)
    }

    const openProfilePopover = (e) => {
        reviewService.getRatingByUserId(userId)
            .then(({data}) => setUserRating(data))
        setAnchorEl(e.currentTarget)
    }

    useEffect(() => {
        userService.getAuthor(userId)
            .then(({data}) => {
                setAuthor(data)
            })
    }, [])

    useEffect(() => {
        reviewService.getCountByUserId(userId)
            .then(({data}) => {
                setReviewsCount(data)
            })
    }, [])
    return (
        <div>
            {Boolean(anchorEl) &&
                <ProfilePopover anchorEl={anchorEl} setAnchorEl={setAnchorEl} {...author} rating={userRating}/>
            }
            <Card className="paperItem" elevation={3}
                  style={!active ? {backgroundColor: "darkgray"} : {}}
            >
                <CardHeader
                    avatar={
                        <div onClick={e => openProfilePopover(e)}>
                            <div>
                                <Avatar src={API_URL + IMAGE_ROUTE + author.fileName}/>
                            </div>
                            <div>
                                <Typography
                                    variant="caption">{author && author.username}</Typography>
                            </div>
                            <div>
                                <Typography variant="caption">{t('reviews') + ": " + reviewsCount}</Typography>
                            </div>
                        </div>
                    }
                    title={
                        <Box
                            sx={{
                                display: 'flex',
                                alignItems: 'center'
                            }}
                        >
                            <Rating name="half-rating-read" defaultValue={0} value={rating}
                                    size="small"
                                    readOnly/>
                            <Box
                                sx={{ml: 1}}><small>{t("published")} {getFormattedDate(createdAt, i18n)}</small></Box>
                            {(isAuth && props.item.active) ?
                                <div className="row-cols-2 like">
                                    <IconButton onClick={() => props.like(id, isAuthUserLiked)}>
                                        {isAuthUserLiked ?
                                            <span id="heart-icon" className="bi bi-heart-fill small"/>
                                            :
                                            <span id="heart-icon" className="bi bi-heart small"/>
                                        }
                                    </IconButton>
                                    {likesCount > 0 ? likesCount: null}
                                </div> : null
                            }
                        </Box>
                    }
                    subheader={
                        <>
                            <h6> {title} </h6>
                            <p>{description}</p>

                        </>
                    }
                />
                {props.item.fileNames.length > 0 &&
                    <CardMedia>
                        <ItemImages images={fileNames} itemId={id}
                                    promiseInProgress={props.promiseInProgress} removeImage={removeImage}/>
                    </CardMedia>
                }
                {(isAdmin || isAuthor || isAuthor) &&
                    <CardActions>
                        {isModerator &&
                            <Button size="small" color="primary"
                                    onClick={e => updateReview(e, id)}
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