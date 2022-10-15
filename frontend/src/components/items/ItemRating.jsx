import React from 'react';
import {Box, Checkbox, FormControlLabel,  Grid, LinearProgress} from "@material-ui/core";
import {useTranslation} from "react-i18next";

const ItemRating = (props) => {
    const {t} = useTranslation();
    const {rating,  reviewsCount, setRatingFilter = Function.prototype} = props;
    const labels = {
        1: 'useless',
        2: 'poor',
        3: 'ok',
        4: 'good',
        5: 'excellent',
    };

    return (
        <Box>
            <Grid container style={{maxWidth: 450}}>
                {rating.ratingsMap  && Object.entries(rating.ratingsMap).map(([key, value]) =>
                    <Grid key={key} container alignItems="center" direction="row">
                            <Grid item xs={4}>
                                {setRatingFilter !== null ?
                                    <FormControlLabel
                                        control={
                                            <Checkbox onChange={setRatingFilter} size="small" color="default"
                                                      value={key || ''}/>
                                        }
                                        label={t(labels[key])}
                                    />
                                    :
                                    t(labels[key])
                                }
                            </Grid>
                        <Grid item xs={7}>
                            <LinearProgress variant="determinate" value={value ? (+value / reviewsCount) * 100 : 0}/>
                        </Grid>
                        <small style={{marginLeft: 5}}>{value}</small>
                    </Grid>
                )
                }
            </Grid>
        </Box>
    );
};

export default ItemRating;