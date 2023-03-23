import React from 'react';
import {Container, Grid, Typography} from "@material-ui/core";
import {Card} from "reactstrap";
import {getFormattedDate} from "../../utils/consts";
import {useTranslation} from "react-i18next";

const Intro = (props) => {
    const {registeredAt} = props;
    const {t, i18n} = useTranslation();

    return (
        <Grid item xs={12} sm={12} md={3} key="introKey">
            <Container style={{padding: 5}}>
                <Card className="paperItem">
                    <Typography variant="subtitle1">
                        {t("joined at")} {getFormattedDate(registeredAt, i18n)}
                    </Typography>
                </Card>
            </Container>
        </Grid>
    );
};

export default Intro;