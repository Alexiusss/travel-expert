import React from 'react';
import {Container, Paper, Box} from "@material-ui/core";
import {Skeleton} from "@material-ui/lab";

const SkeletonCard = ({listsToRender}) => {
    return (
        <Container maxWidth="md">
            {Array(listsToRender)
                .fill(1)
                .map((card, index) => (
                    <Paper elevation={3} key={index}>
                        <Box>
                            <Skeleton variant="text" height={30}/>
                            <Skeleton variant="text" height={70}/>
                            <Skeleton variant="text" height={40}/>
                        </Box>
                    </Paper>
                ))}
        </Container>
    );
};

export default SkeletonCard;