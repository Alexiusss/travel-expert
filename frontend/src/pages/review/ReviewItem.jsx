import React from 'react';
import {Box, Paper} from "@material-ui/core";
import {Rating} from "@material-ui/lab";
import {useTranslation} from "react-i18next";
import {useAuth} from "../../components/hooks/UseAuth";
import MyButton from "../../components/UI/button/MyButton";

const ReviewItem = (props) => {

    const {t, i18n} = useTranslation();
    const {authUserId, isAdmin} = useAuth();
    const isAuthor = authUserId === props.item.userId;

    // https://stackoverflow.com/a/50607453
    const getFormattedDate = (date) => {
        const DATE_OPTIONS = {year: 'numeric', month: 'short', day: 'numeric'};
        return new Date(date).toLocaleDateString(i18n.language, DATE_OPTIONS);
    }

    const removeReview = (e, review) => {
        e.preventDefault();
        props.remove(review)
    }

    return (
        <div>
            <Paper elevation={3}>
                <Box
                    sx={{
                        display: 'flex',
                        alignItems: 'center',
                    }}
                >
                    <Rating name="half-rating-read" defaultValue={0} value={props.item.rating} size="small" readOnly/>
                    <Box sx={{ml: 1}}><small>{t("published")} {getFormattedDate(props.item.createdAt)}</small></Box>
                </Box>
                {(isAdmin || isAuthor) &&
                    (
                        <div style={{float: 'right'}}>
                            <MyButton className="btn btn-outline-danger btn-sm"
                                      onClick={e => removeReview(e, props.item)}>
                                {t("delete")}</MyButton>
                        </div>
                    )
                }
                <h5> {props.item.title} </h5>
                <p>{props.item.description}</p>
            </Paper>
        </div>
    );
};

export default ReviewItem;