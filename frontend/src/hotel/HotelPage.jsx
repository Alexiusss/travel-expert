import React, {useEffect, useState} from 'react';
import {useLocation} from "react-router-dom";
import {trackPromise, usePromiseTracker} from "react-promise-tracker";
import reviewService from "../services/ReviewService";
import hotelService from "../services/HotelService";
import {Container} from "@material-ui/core";
import ItemPageHeader from "../components/items/ItemPageHeader";
import ItemContact from "../components/items/ItemContact";
import ItemImages from "../components/items/ItemImages";
import ReviewsSection from "../pages/review/ReviewsSection";
import {NotFound} from "../pages/NotFound";
import imageService from "../services/ImageService";

const HotelPage = () => {
    const {pathname} = useLocation();
    const [id, setId] = useState(pathname.replace(/.+-/, ""));
    const area = 'hotels';
    const {promiseInProgress} = usePromiseTracker({area});
    const [hotel, setHotel] = useState({});
    const [images, setImages] = useState([]);
    const [rating, setRating] = useState({});

    useEffect(() => {
        trackPromise(Promise.all([hotelService.get(id), reviewService.getRating(id)]), area)
            .then((values) => {
                const [hotel, rating] = values;
                setHotel(hotel);
                setImages(hotel.fileNames);
                setRating(rating);
            })
    }, [])

    const removeImage = (fileName) => {
        const filteredImages = images.filter(imageName => imageName !== fileName);
        const updatedHotel = {
            ...hotel,
            fileNames: filteredImages,
        }
        Promise.all([hotelService.update(updatedHotel, id), imageService.remove(fileName)])
            .then(() => setImages(filteredImages))
    }

    return (
        <Container>
            {promiseInProgress
                ?
                <div>Data is loading</div>
                :
                <>
                    {
                        hotel.name ?
                            <>
                                <br/>
                                <ItemPageHeader name={hotel.name}
                                                description={hotel.description}
                                                rating={rating}
                                />
                                <br/>
                                <ItemContact item={hotel}/>
                                <br/>
                                <ItemImages images={images}
                                            removeImage={removeImage}
                                            promiseInProgress={promiseInProgress}
                                />
                                <br/>
                                {id.length ?
                                    <ReviewsSection itemId={id} rating={rating}/> : null
                                }
                            </>
                            :
                            <NotFound/>
                    }
                </>
            }
        </Container>
    );
};

export default HotelPage;