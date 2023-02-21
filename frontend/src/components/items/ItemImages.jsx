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
    const overFlowY = (images.length > (3 / columnsCount)) ? 'scroll' : 'hidden';

    // https://stackoverflow.com/a/56011277
    useEffect(() => {
        setColumnsCount(Math.round(componentRef.current.offsetWidth / 410))
    }, [componentRef.current]);

    return (

         <div ref={componentRef} style={{height: 182, overflowY: {overFlowY}, overflowX: 'hidden'}}>
            <ImageList
                cols={columnsCount}
            >
                {
                    images.map((image, index) =>
                        <ImageListItem key={index} >
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
        </div>
    );
};

export default ItemImages;