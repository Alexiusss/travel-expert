import React from 'react';
import {Box, Checkbox, FormControlLabel, Grid, LinearProgress, Typography} from "@material-ui/core";
import {useTranslation} from "react-i18next";

const ItemRating = (props) => {
    const {t} = useTranslation();
    const labels = {
        1: 'useless',
        2: 'poor',
        3: 'ok',
        4: 'good',
        5: 'excellent',
    };

    return (
            <Box>
            <Grid container >
                {Object.entries(props.rating.ratingsMap).map(([key, value]) =>
                    <Grid key={key} container alignItems="center" direction="row">
                        <Grid item  xs={4}>
                            <FormControlLabel
                                control={
                                    <Checkbox checked={false} size="small" color="default"/>
                                }
                                label={t(labels[key])}
                            />
                        </Grid>
                        <Grid item xs={4}>
                            <LinearProgress variant="determinate" value={100 - (100 / key)}/>
                        </Grid>
                    </Grid>
                )
                }
            </Grid>
        </Box>
    );
};

export default ItemRating;