import React, {useEffect, useState} from 'react';
import {useLocation} from "react-router-dom";
import {trackPromise, usePromiseTracker} from 'react-promise-tracker';
import {Container} from "@material-ui/core";
import restaurantService from "../services/RestaurantService";
import ItemPageHeader from "../components/items/ItemPageHeader";
import ItemContact from "../components/items/ItemContact";
import ReviewsSection from "../pages/review/ReviewsSection";
import ItemImages from "../components/items/ItemImages";

const RestaurantPage = () => {
    const location = useLocation()
    const area = 'restaurants';
    const {promiseInProgress} = usePromiseTracker({area});
    const [restaurant, setRestaurant] = useState({});
    const [images, setImages] = useState([]);

    useEffect(() => {
        trackPromise(
            restaurantService.get(location.state.id), area)
            .then(({data}) => {
                setRestaurant(data)
                setImages(data.fileNames)
            })
    }, [])

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
                    <br/>
                    <ItemImages images={images} promiseInProgress={promiseInProgress}/>
                    <br/>
                    <ReviewsSection itemId={location.state.id}/>
                </>
            }
        </Container>
    );
};

export default RestaurantPage;