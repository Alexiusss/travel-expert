import React from 'react';
import {Card, CardContent, Grid, Typography} from "@material-ui/core";

const ItemContact = ({item}) => {
    return (
            <Card>
                <CardContent>
                    <Typography variant="h5" component="div">
                        <h5>Location and contact</h5>
                    </Typography>
                    <div>Address: {item.address}</div>
                    <div>Email: {item.email}</div>
                    <div>Phone number: {item.phoneNumber}</div>
                    <div>Website: {item.website}</div>
                </CardContent>
            </Card>
    );
};

export default ItemContact;