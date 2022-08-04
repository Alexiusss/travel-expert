import React, {useEffect, useState} from 'react';
import restaurantService from '../services/RestaurantService'
import {Container} from "@material-ui/core";
import ItemGrid from "../components/items/ItemGrid";
import {trackPromise, usePromiseTracker} from 'react-promise-tracker';
import {useTranslation} from "react-i18next";
import SkeletonGrid from "../components/SkeletonGrid";
import {getLocalizedErrorMessages, RESTAURANTS_ROUTE} from "../utils/consts";
import MyButton from "../components/UI/button/MyButton";
import {useAuth} from "../components/hooks/UseAuth";
import MyModal from "../components/UI/modal/MyModal";
import RestaurantEditor from "./RestaurantEditor";
import MyNotification from "../components/UI/notification/MyNotification";

const RestaurantList = () => {
    const area = 'restaurants';
    const {promiseInProgress} = usePromiseTracker({area});
    const [restaurants, setRestaurants] = useState([]);
    const [restaurantFromDB, setRestaurantFromDB] = useState([])
    const {isAdmin} = useAuth();
    const [modal, setModal] = useState(false);
    const [alert, setAlert] = useState({open: false, message: '', severity: 'info'})
    const {t} = useTranslation();

    useEffect(() => {
        trackPromise(
            restaurantService.getAll(), area).then(({data}) => {
            setRestaurants(data.content)
        });
    }, [setRestaurants])

    const openAlert = (msg, severity) => {
        setAlert({severity: severity, message: msg, open: true})
    }

    const createRestaurant = (newRestaurant) => {
        setRestaurants([...restaurants, newRestaurant])
        setModal(false)
    }

    const update = (restaurant) => {
        restaurantService.get(restaurant.id)
            .then(response => {
                setRestaurantFromDB(response.data)
                setModal(true)
            })
            .catch(
                error => {
                    openAlert(getLocalizedErrorMessages(error.response.data.message), "error");
                });
    }

    const updateRestaurant = (updatedRestaurant) => {
        setModal(false);
        setRestaurants(restaurants => {
            return restaurants.map(restaurant => {
                    return restaurant.id === updatedRestaurant.id
                        ?
                        {
                            ...restaurant,
                            email: updatedRestaurant.email,
                            name: updatedRestaurant.name,
                            cuisine: updatedRestaurant.cuisine,
                            address: updatedRestaurant.address,
                            phone_number: updatedRestaurant.phone_number,
                            website: updatedRestaurant.website,

                        }
                        :
                        restaurant
                }
            )
        })
        openAlert([t('record saved')], "success")
    }

    const removeRestaurant = (restaurant) => {
        restaurantService.delete(restaurant.id)
            .then(() => {
                    setRestaurants(restaurants.filter(r => r.id !== restaurant.id))
                    openAlert([t('record deleted')], "success")
                }
            )
            .catch(
                error => {
                    openAlert(getLocalizedErrorMessages(error.response.data.message), "error");
                });
    }

    return (

        <Container>
            {isAdmin
                ?
                <>
                    <MyButton style={{marginTop: 10}} className={"btn btn-outline-primary ml-2 btn-sm"}
                              onClick={() => setModal(true)}>
                        {t("add")}
                    </MyButton>
                </>
                :
                ""}
            {promiseInProgress
                ? <SkeletonGrid listsToRender={16}/>
                : <ItemGrid items={restaurants} route={RESTAURANTS_ROUTE} update={update}
                            remove={removeRestaurant}/>
            }
            <MyModal visible={modal} setVisible={setModal}>
                <RestaurantEditor create={createRestaurant} update={updateRestaurant}
                                  restaurantFromDB={restaurantFromDB} setAlert={setAlert}
                                  modal={modal}/>
            </MyModal>
            <MyNotification open={alert.open} setOpen={setAlert} message={alert.message} severity={alert.severity}/>
        </Container>
    );
};

export default RestaurantList;