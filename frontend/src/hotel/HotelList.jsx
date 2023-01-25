import React, {useEffect, useState} from 'react';
import {Container} from "@material-ui/core";
import ItemGrid from "../components/items/ItemGrid";
import {HOTELS_ROUTE} from "../utils/consts";
import hotelService from "../services/HotelService"
import SkeletonGrid from "../components/SkeletonGrid";
import {trackPromise, usePromiseTracker} from "react-promise-tracker";

const HotelList = () => {
    const area = 'hotels';
    const {promiseInProgress} = usePromiseTracker({area});
    const [hotels, setHotels] = useState([]);

    useEffect(() => {
        trackPromise(
            hotelService.getAll(), area).then(({data}) => {
            setHotels(data);
        });
    }, [setHotels]);

    return (
        <Container>
            {promiseInProgress
                ? <SkeletonGrid listsToRender={16}/>
                : <ItemGrid items={hotels} route={HOTELS_ROUTE}/>
            }
        </Container>

    );
};

export default HotelList;