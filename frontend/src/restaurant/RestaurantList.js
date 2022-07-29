import React, {useEffect, useState} from 'react';
import restaurantService from '../services/RestaurantService'
import {Container} from "@material-ui/core";
import ItemGrid from "../components/ItemGrid";
import { trackPromise, usePromiseTracker } from 'react-promise-tracker';
import {useTranslation} from "react-i18next";

const RestaurantList = () => {
    const area = 'restaurants';
    const { promiseInProgress } = usePromiseTracker({ area });
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
                ? <div>{t("data loading")}</div>
                : <ItemGrid items={restaurants}/>
            }
        </Container>
    );
};

export default RestaurantList;