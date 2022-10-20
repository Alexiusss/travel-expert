import React, {useEffect, useState} from 'react';
import {useLocation} from "react-router-dom";
import {trackPromise, usePromiseTracker} from 'react-promise-tracker';
import {Container} from "@material-ui/core";
import restaurantService from "../services/RestaurantService";
import reviewService from "../services/ReviewService";
import imageService from '../services/ImageService'
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

    const removeImage = (fileName) => {
        const filteredImages = images.filter(imageName => imageName !== fileName);
        const updatedRestaurant = {
            ...restaurant,
            fileNames: filteredImages,
        }
        Promise.all([restaurantService.update(updatedRestaurant, location.state.id), imageService.remove(fileName)])
            .then(() => setImages(filteredImages))
    }

    useEffect(() => {
        const id = location.state.id;
        trackPromise(Promise.all([restaurantService.get(id), reviewService.getRating(id)]), area)
            .then((values) => {
                const [restaurant, rating] = values;
                setRestaurant(restaurant);
                setImages(restaurant.fileNames);
                setRating(rating);
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
                    <ItemPageHeader name={restaurant.name} description={restaurant.cuisine} rating={rating}/>
                    <br/>
                    <ItemContact item={restaurant}/>
                    <br/>
                    <ItemImages images={images} promiseInProgress={promiseInProgress} removeImage={removeImage}/>
                    <br/>
                    <ReviewsSection itemId={location.state.id} rating={rating}/>
                </>
            }
        </Container>
    );
};

export default RestaurantPage;