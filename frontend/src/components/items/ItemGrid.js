import React from 'react';
import ItemCard from "./ItemCard";
import {Grid} from "@material-ui/core";


const ItemGrid = (props) => {

    const removeItem = (e, item) => {
        e.preventDefault();
        props.remove(item)
    }

    const updateItem = (e, item) => {
        e.preventDefault()
        props.update(item)
    }

    return (
        <Grid container
              spacing={1}
              direction="row"
        >
            {props.items.map((item) =>
                <ItemCard item={item} itemId={item.id} route={props.route} remove={removeItem} update={updateItem} key={item.id}/>
            )}
        </Grid>
    );
};

export default ItemGrid;