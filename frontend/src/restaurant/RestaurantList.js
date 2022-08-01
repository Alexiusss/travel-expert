import React, {useEffect, useState} from 'react';
import restaurantService from '../services/RestaurantService'
import {Container} from "@material-ui/core";
import ItemGrid from "../components/items/ItemGrid";
import {trackPromise, usePromiseTracker} from 'react-promise-tracker';
import {useTranslation} from "react-i18next";
import SkeletonGrid from "../components/SkeletonGrid";
import {RESTAURANTS_ROUTE} from "../utils/consts";

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
                : <ItemGrid items={restaurants} route={RESTAURANTS_ROUTE}/>
            }
        </Container>
    );
};

export default RestaurantList;