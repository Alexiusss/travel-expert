import React, {useEffect, useState} from 'react';
import restaurantService from '../services/RestaurantService'

const RestaurantList = () => {
    const [restaurants, setRestaurants] = useState();

    useEffect(() => {
        restaurantService.getAll().then(({data}) => console.log(data));
    }, [setRestaurants])

    return (
        <div className='container'>
            Restaurant list
        </div>
    );
};

export default RestaurantList;