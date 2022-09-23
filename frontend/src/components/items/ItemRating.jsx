import React, {useEffect} from 'react';
import {Box, Checkbox, FormControlLabel, FormLabel, LinearProgress} from "@material-ui/core";
import {FormGroup} from "reactstrap";
import FormControl from "@material-ui/core/FormControl";
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
        <Box sx={{display: 'flex'}}>
            <FormControl sx={{m: 5}} component="fieldset" variant="standard">
                <FormLabel component="legend">Traveler rating</FormLabel>
                <FormGroup>
                    {Object.entries(props.rating.ratingsMap).map(([key, value]) =>
                        <Box key={key} sx={{ml: 3}}>
                            <FormControlLabel
                                control={
                                    <Checkbox checked={false} size="small" color="default"/>
                                }
                                label={t(labels[key])}
                            />
                            <LinearProgress variant="determinate" value={100 - (100 / key)}/>
                        </Box>
                    )
                    }
                </FormGroup>
            </FormControl>
        </Box>
    );
};

export default ItemRating;