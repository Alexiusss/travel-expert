import React, {useEffect, useState} from 'react';
import {Box, Checkbox, FormControlLabel, Grid, LinearProgress, Typography} from "@material-ui/core";
import {useTranslation} from "react-i18next";

const ItemRating = (props) => {
    const {t} = useTranslation();
    const {rating, reviewsCount, setRatingFilter} = props;
    const labels = {
        1: 'useless',
        2: 'poor',
        3: 'ok',
        4: 'good',
        5: 'excellent',
    };


    return (
        <Box>
            <Grid container>
                {Object.entries(rating.ratingsMap).map(([key, value]) =>
                    <Grid key={key} container alignItems="center" direction="row">
                        <Grid item xs={4}>
                            <FormControlLabel
                                control={
                                    <Checkbox onChange={setRatingFilter} size="small" color="default" value={key || ''}/>
                                }
                                label={t(labels[key])}
                            />
                        </Grid>
                        <Grid item xs={4}>
                            <LinearProgress variant="determinate" value={(+value / reviewsCount) * 100}/>
                        </Grid>
                    </Grid>
                )
                }
            </Grid>
        </Box>
    );
};

export default ItemRating;