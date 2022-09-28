import React, {useEffect, useState} from 'react';
import {Box, Checkbox, FormControlLabel, Grid, LinearProgress, Typography} from "@material-ui/core";
import {useTranslation} from "react-i18next";

const ItemRating = (props) => {
    const {t} = useTranslation();
    const [ratingFilters, setRatingFilters] = useState(props.filter.ratingFilters || []);
    const labels = {
        1: 'useless',
        2: 'poor',
        3: 'ok',
        4: 'good',
        5: 'excellent',
    };

    const handleCheckbox = (e) => {
        let value = +e.target.value;
        if (ratingFilters.includes(value)) {
            setRatingFilters(ratingFilters.filter(r => r !== value))
        } else {
            setRatingFilters([...ratingFilters, value]);
        }
    }

    useEffect(() => {
        props.setFilter({...ratingFilters, ratingFilters: ratingFilters})
    }, [ratingFilters])

    return (
        <Box>
            <Grid container>
                {Object.entries(props.rating.ratingsMap).map(([key, value]) =>
                    <Grid key={key} container alignItems="center" direction="row">
                        <Grid item xs={4}>
                            <FormControlLabel
                                control={
                                    <Checkbox onChange={handleCheckbox} size="small" color="default" value={key || ''}/>
                                }
                                label={t(labels[key])}
                            />
                        </Grid>
                        <Grid item xs={4}>
                            <LinearProgress variant="determinate" value={(+value / props.reviewsCount) * 100}/>
                        </Grid>
                    </Grid>
                )
                }
            </Grid>
        </Box>
    );
};

export default ItemRating;