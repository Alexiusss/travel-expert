import React, {useEffect, useState} from 'react';
import {useLocation} from "react-router-dom";
import {trackPromise, usePromiseTracker} from 'react-promise-tracker';
import {Container} from "@material-ui/core";
import restaurantService from "../services/RestaurantService";
import reviewService from "../services/ReviewService";
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
    const [rating, setRating] = useState({});

    useEffect(() => {
        trackPromise(
            restaurantService.get(location.state.id), area)
            .then(({data}) => {
                setRestaurant(data)
                setImages(data.fileNames)
            })
    }, [])

    useEffect(() => {
        trackPromise(reviewService.getRating(location.state.id), area)
            .then(({data}) => setRating(data))
    }, [])
    return (
        <Container>
            {promiseInProgress
                ?
                <div>Data is loading</div>
                :
                <>
                    <br/>
                    <ItemPageHeader name={restaurant.name} description={restaurant.cuisine} rating={rating}/>
                    <br/>
                    <ItemContact item={restaurant}/>
                    <br/>
                    <ItemImages images={images} promiseInProgress={promiseInProgress}/>
                    <br/>
                    <ReviewsSection itemId={location.state.id} rating={rating}/>
                </>
            }
        </Container>
    );
};

export default RestaurantPage;