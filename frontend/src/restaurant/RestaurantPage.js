import React, {useEffect, useState} from 'react';
import {useLocation} from "react-router-dom";
import {trackPromise, usePromiseTracker} from 'react-promise-tracker';
import {Container, Grid} from "@material-ui/core";
import restaurantService from "../services/RestaurantService";
import ItemPageHeader from "../components/items/ItemPageHeader";
import ItemContact from "../components/items/ItemContact";

const RestaurantPage = () => {
    const location = useLocation()
    const area = 'restaurants';
    const {promiseInProgress} = usePromiseTracker({area});
    const [restaurant, setRestaurant] = useState({});

    useEffect(() => {
        trackPromise(
            restaurantService.get(location.state.id), area).then(({data}) =>
            setRestaurant(data))
    }, [setRestaurant])

    return (
        <Container>
            {promiseInProgress
                ?
                <div>Data is loading</div>
                :
                <>
                    <br/>
                    <ItemPageHeader name={restaurant.name} description={restaurant.cuisine}/>
                    <br/>
                    <ItemContact item={restaurant}/>
                </>
            }
        </Container>
    );
};

export default RestaurantPage;