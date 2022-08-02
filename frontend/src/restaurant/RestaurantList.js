import React, {useEffect, useState} from 'react';
import restaurantService from '../services/RestaurantService'
import {Container} from "@material-ui/core";
import ItemGrid from "../components/items/ItemGrid";
import {trackPromise, usePromiseTracker} from 'react-promise-tracker';
import {useTranslation} from "react-i18next";
import SkeletonGrid from "../components/SkeletonGrid";
import {RESTAURANTS_ROUTE} from "../utils/consts";
import MyButton from "../components/UI/button/MyButton";
import {useAuth} from "../components/hooks/UseAuth";
import MyModal from "../components/UI/modal/MyModal";
import RestaurantEditor from "./RestaurantEditor";

const RestaurantList = () => {
    const area = 'restaurants';
    const {promiseInProgress} = usePromiseTracker({area});
    const [restaurants, setRestaurants] = useState([]);
    const {isAdmin} = useAuth();
    const [modal, setModal] = useState(false);
    const {t} = useTranslation();

    useEffect(() => {
        trackPromise(
            restaurantService.getAll(), area).then(({data}) => {
            setRestaurants(data.content)
        });
    }, [setRestaurants])

    return (
        <Container>
            {isAdmin
                ?
                <>
                <MyButton style={{marginTop: 10}} className={"btn btn-outline-primary ml-2 btn-sm"}
                          onClick={() => setModal(true)}>
                    {t("add")}
                </MyButton>
                <MyModal visible={modal} setVisible={setModal}>
                    <RestaurantEditor />
                </MyModal>
                </>
                :
                ""}
            {promiseInProgress
                ? <SkeletonGrid listsToRender={16}/>
                : <ItemGrid items={restaurants} route={RESTAURANTS_ROUTE}/>
            }
        </Container>
    );
};

export default RestaurantList;