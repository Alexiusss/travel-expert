import React from 'react';
import Container from "react-bootstrap/Container";
import {Card} from "reactstrap";
import {Skeleton} from "@material-ui/lab";
import {Box, CardHeader, CardMedia, ImageList, ImageListItem} from "@material-ui/core";

const SkeletonReview = ({listsToRender}) => {
    return (
        <Container style={{padding: 5}}>
            {Array(listsToRender)
                .fill(1)
                .map((card, index) => (
                    <Card className="paperItem" elevation={3} key={index}>
                        <CardHeader
                            avatar={
                            <>
                                <Skeleton variant="circle" height={40} width={40}/>
                                <Skeleton variant="text" height={15} width={50}/>
                                <Skeleton variant="text" height={15} width={70}/>
                            </>
                            }
                            title={
                                <>
                                    <Skeleton variant="text" height={20} width={290}/>
                                    <Skeleton variant="text" height={20} width={170}/>
                                    <Skeleton variant="text" height={20} width={"100%"}/>
                                </>
                            }
                        />
                        <CardMedia>
                            <Box sx={{height: 190}}>
                                <ImageList
                                    cols={3}
                                    gap={5}
                                >
                                    <ImageListItem>
                                        <Skeleton variant="rect" height={180}/>
                                    </ImageListItem>
                                    <ImageListItem>
                                        <Skeleton variant="rect" height={180} />
                                    </ImageListItem>
                                    <ImageListItem>
                                        <Skeleton variant="rect" height={180} />
                                    </ImageListItem>
                                </ImageList>
                            </Box>
                        </CardMedia>

                    </Card>
                ))}
        </Container>
    );
};

export default SkeletonReview;