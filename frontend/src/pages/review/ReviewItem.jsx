import React from 'react';
import {Box, Paper} from "@material-ui/core";
import {Rating} from "@material-ui/lab";
import {useTranslation} from "react-i18next";

const ReviewItem = (props) => {

    const {t, i18n} = useTranslation();

    // https://stackoverflow.com/a/50607453
    const getFormattedDate = (date) => {
        const DATE_OPTIONS = {year: 'numeric', month: 'short', day: 'numeric'};
        return new Date(date).toLocaleDateString(i18n.language, DATE_OPTIONS);
    }

    return (
        <div>
            <Paper elevation={3} >
                <Box
                    sx={{
                        display: 'flex',
                        alignItems: 'center',
                    }}
                >
                    <Rating name="half-rating-read" defaultValue={0} value={props.item.rating} size="small" readOnly/>
                    <Box sx={{ml: 1}}><small>{t("published")} {getFormattedDate(props.item.createdAt)}</small></Box>
                </Box>
                <h5> {props.item.title} </h5>
                <p>{props.item.description}</p>
            </Paper>
        </div>
    );
};

export default ReviewItem;