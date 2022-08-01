import React from 'react';
import {Card, CardContent, Typography} from "@material-ui/core";

const ItemContact = (props) => {
    return (
        <Card>
            <CardContent>
                <Typography variant="h5" component="div">
                    <h5>Location and contact</h5>
                </Typography>
                <div>Address: {props.item.address}</div>
                <div>Email: {props.item.email}</div>
                <div>Phone number: {props.number}</div>
                <div>Website: {props.item.website}</div>
            </CardContent>

        </Card>
    );
};

export default ItemContact;