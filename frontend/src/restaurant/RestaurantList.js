import React, {useEffect, useState} from 'react';
import restaurantService from '../services/RestaurantService'
import {Container} from "@material-ui/core";
import ItemGrid from "../components/ItemGrid";

const RestaurantList = () => {
    const [restaurants, setRestaurants] = useState([]);

    useEffect(() => {
        restaurantService.getAll().then(({data}) => {
            setRestaurants(data.content)
        });
    }, [setRestaurants])

    return (
        <Container>
            <ItemGrid items={restaurants}/>
        </Container>
    );
};

export default RestaurantList;