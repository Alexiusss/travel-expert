import React from 'react';
import {
    Grid,
    Card
} from "@material-ui/core";
import {Skeleton} from "@material-ui/lab";
const SkeletonGrid = ({listsToRender}) => {
    return (
        <Grid container
              spacing={1}
              direction="row"
        >
            {Array(listsToRender)
                .fill(1)
                .map((card, index) => (
                    <Grid item xs={12} sm={6} md={3} key={index}>
                        <Card>
                        <Skeleton variant="rect" height={140}/>
                        <Skeleton variant="text" height={60}/>
                        </Card>
                    </Grid>
                ))}
        </Grid>
    );
};

export default SkeletonGrid;