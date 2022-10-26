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
    const {pathname, state} = useLocation();
    const [id, setId] = useState("");
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
        Promise.all([restaurantService.update(updatedRestaurant, id), imageService.remove(fileName)])
            .then(() => setImages(filteredImages))
    }

    useEffect(() => {
        if (state) {
            setId(state.id)
            trackPromise(Promise.all([restaurantService.get(state.id), reviewService.getRating(state.id)]), area)
                .then((values) => {
                    const [restaurant, rating] = values;
                    setRestaurant(restaurant);
                    setImages(restaurant.fileNames);
                    setRating(rating);
                })
        } else if (pathname) {
            const name = pathname.replace("/restaurants/", "");
            trackPromise(restaurantService.getByName(name), area)
                .then(value => {
                    setId(value.id);
                    setRestaurant(value);
                    setImages(value.fileNames);
                    return reviewService.getRating(value.id)
                })
                .then(value => setRating(value))
        }
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
                    <ReviewsSection itemId={id} rating={rating}/>
                </>
            }
        </Container>
    );
};

export default RestaurantPage;