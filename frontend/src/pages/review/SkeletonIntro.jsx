import React from 'react';
import {Container, Grid, Typography} from "@material-ui/core";
import {Card} from "reactstrap";
import {Skeleton} from "@material-ui/lab";

const SkeletonIntro = () => {
    return (
        <Grid item xs={12} sm={12} md={3} key="introKey">
            <Container style={{padding: 5}}>
                <Card className="paperItem">
                    <Typography variant="subtitle1">
                        <Skeleton variant="text"/>
                    </Typography>
                </Card>
            </Container>
        </Grid>
    );
};

export default SkeletonIntro;