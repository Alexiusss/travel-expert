import React, {useEffect, useRef, useState} from 'react';
import {Button, ImageList, ImageListItem, ImageListItemBar} from "@material-ui/core";
import {API_URL} from "../../http/http-common";
import {IMAGE_ROUTE} from "../../utils/consts";
import {useAuth} from "../hooks/UseAuth";


const ItemImages = (props) => {
    const {isAdmin, isModerator} = useAuth();
    const {images = [], itemId, removeImage = Function.prototype} = props;
    const componentRef = useRef(null);
    const [columnsCount, setColumnsCount] = useState(1);

    // https://stackoverflow.com/a/56011277
    useEffect(() => {
        setColumnsCount(Math.round(componentRef.current.offsetWidth / 410))
    }, [componentRef.current]);

    return (
   // https://stackoverflow.com/questions/69597992/how-to-implement-horizontal-scrolling-of-tiles-in-mui-gridlist
        <ImageList ref={componentRef} cols={columnsCount} style={{flexWrap: "nowrap"}}>
            {images.map((image) => (
                <ImageListItem style={{maxWidth: 410}} key={image}>
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
            ))}
        </ImageList>
    );
};

export default ItemImages;