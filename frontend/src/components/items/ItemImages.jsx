import React from 'react';
import {Box, Button, ImageList, ImageListItem, ImageListItemBar} from "@material-ui/core";
import {API_URL} from "../../http/http-common";
import {IMAGE_ROUTE} from "../../utils/consts";
import {useAuth} from "../hooks/UseAuth";


const ItemImages = (props) => {
    const {isAdmin, isModerator} = useAuth();
    const {images = [], itemId, removeImage = Function.prototype} = props;

    return (
        <Box sx={{height: 190, overflow: 'scroll' }}>
        <ImageList
             cols={3}
        >
            {
                images.map((image, index) =>
                    <ImageListItem key={index}>
                        <img
                            src={API_URL + IMAGE_ROUTE + `${image}`}
                            alt="Image"
                            loading="lazy"
                        />
                        {(isAdmin || isModerator) &&
                            <ImageListItemBar
                                actionIcon={<Button size="small" color="secondary">X</Button>}
                                position='top'
                                onClick={() => removeImage(image, itemId)}
                            />
                        }
                    </ImageListItem>
                )
            }
        </ImageList>
        </Box>
    );
};

export default ItemImages;