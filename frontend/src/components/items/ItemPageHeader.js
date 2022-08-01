import React from 'react';
import {Rating} from "@material-ui/lab";


const ItemPageHeader = (props) => {
    return (
        <div>
            <h2>{props.name}</h2>
            <Rating name="half-rating-read" defaultValue={3.44}  precision={0.1} readOnly />
            <br/>
            <span>{props.description}</span>
        </div>
    );
};

export default ItemPageHeader;