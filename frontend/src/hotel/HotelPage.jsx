import React, {useEffect, useState} from 'react';
import {useLocation} from "react-router-dom";
import {trackPromise, usePromiseTracker} from "react-promise-tracker";
import reviewService from "../services/ReviewService";
import hotelService from "../services/HotelService";
import {Container, Grid} from "@material-ui/core";
import ItemPageHeader from "../components/items/ItemPageHeader";
import ItemContact from "../components/items/ItemContact";
import ItemImages from "../components/items/ItemImages";
import ReviewsSection from "../pages/review/ReviewsSection";
import {NotFound} from "../pages/NotFound";
import imageService from "../services/ImageService";
import About from "./About";
import MyModal from "../components/UI/modal/MyModal";
import AboutEditor from "./AboutEditor";
import {getLocalizedErrorMessages} from "../utils/consts";
import {useTranslation} from "react-i18next";
import MyNotification from "../components/UI/notification/MyNotification";

const HotelPage = () => {
    const {t} = useTranslation();
    const {pathname} = useLocation();
    const [id, setId] = useState(pathname.replace(/.+-/, ""));
    const area = 'hotels';
    const {promiseInProgress} = usePromiseTracker({area});
    const [hotel, setHotel] = useState({});
    const [images, setImages] = useState([]);
    const [rating, setRating] = useState({});
    const [modal, setModal] = useState(false);
    const [alert, setAlert] = useState({open: false, message: '', severity: 'info'});

    useEffect(() => {
        trackPromise(Promise.all([hotelService.get(id), reviewService.getRating(id)]), area)
            .then((values) => {
                const [hotel, rating] = values;
                setHotel(hotel);
                setImages(hotel.fileNames);
                setRating(rating);
            })
    }, [])

    const updateHotel = (updatedHotel) => {
        hotelService.update(updatedHotel, id)
            .then(() => {
                setModal(false);
                setHotel(updatedHotel);
                openAlert([t('record saved')], "success")
            })
            .catch(error => {
                openAlert(getLocalizedErrorMessages(error.response.data.message), "error");
            })
    }

    const openAlert = (msg, severity) => {
        setAlert({severity: severity, message: msg, open: true})
    }

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
                                <ItemImages images={images}
                                            removeImage={removeImage}
                                            promiseInProgress={promiseInProgress}
                                />
                                <br/>
                                <Grid container
                                      spacing={3}
                                      direction="row"
                                >
                                    <Grid xs sm md item>
                                        <About item={hotel} setModal={setModal}/>
                                    </Grid>
                                    <Grid xs={12} sm={6} md={4} item>
                                        <ItemContact item={hotel}/>
                                    </Grid>
                                </Grid>
                                <br/>
                                {id.length ?
                                    <ReviewsSection itemId={id} rating={rating}/> : null
                                }
                                {modal ?
                                    <MyModal visible={modal} setVisible={setModal}>
                                        <AboutEditor setModal={setModal} hotel={hotel} save={updateHotel}/>
                                    </MyModal> : null
                                }
                                <MyNotification open={alert.open} setOpen={setAlert} message={alert.message} severity={alert.severity}/>
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