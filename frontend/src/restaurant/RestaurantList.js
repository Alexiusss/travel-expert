import React, {useEffect, useState} from 'react';
import restaurantService from '../services/RestaurantService'
import {Box, Container} from "@material-ui/core";
import ItemGrid from "../components/ItemGrid";
import {trackPromise, usePromiseTracker} from 'react-promise-tracker';
import {useTranslation} from "react-i18next";
import SkeletonGrid from "../components/SkeletonGrid";

const RestaurantList = () => {
    const area = 'restaurants';
    const {promiseInProgress} = usePromiseTracker({area});
    const [restaurants, setRestaurants] = useState([]);

    const {t} = useTranslation();

    useEffect(() => {
        trackPromise(
            restaurantService.getAll(), area).then(({data}) => {
            setRestaurants(data.content)
        });
    }, [setRestaurants])

    return (
        <Container>
            {promiseInProgress
                ? <SkeletonGrid listsToRender={16} />
                : <ItemGrid items={restaurants}/>
            }
        </Container>
    );
};

export default RestaurantList;