import React from 'react';
import {Box, ImageList, ImageListItem} from "@material-ui/core";
import {API_URL} from "../../http/http-common";
import {IMAGE_ROUTE} from "../../utils/consts";

const ItemImages = (props) => {
    return (
        <Box sx={{height: 190, overflowY: 'scroll' }}>
        <ImageList
             cols={3}
        >
            {
                props.images.map((image, index) =>
                    <ImageListItem key={index}>
                        <img
                            src={API_URL + IMAGE_ROUTE + `${image}`}
                            alt="Image"
                            loading="lazy"
                        />
                    </ImageListItem>
                )
            }
        </ImageList>
        </Box>
    );
};

export default ItemImages;