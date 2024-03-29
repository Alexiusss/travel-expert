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
import Pagination from "@material-ui/lab/Pagination";
import MySelect from "../components/UI/select/MySelect";
import ItemFilter from "../components/items/ItemFilter";
import imageService from "../services/ImageService";
import reviewService from "../services/ReviewService";
import {useHistory, useLocation} from "react-router-dom";

const RestaurantList = () => {
    const area = 'restaurants';
    const {promiseInProgress} = usePromiseTracker({area});
    const {pathname, search} = useLocation();
    const {push} = useHistory();
    const params = new URLSearchParams(search);
    const [restaurants, setRestaurants] = useState([]);
    const [restaurantFromDB, setRestaurantFromDB] = useState({})
    const {isAdmin} = useAuth();
    const [totalPages, setTotalPages] = useState(0);
    const [page, setPage] = useState(+params.get('page') || 1);
    const [size, setSize] = useState(+params.get('size') || 12);
    const pageSizes = [12, 36, 96];
    const [filter, setFilter] = useState({config: null, query: params.get('query') || ''})
    const [modal, setModal] = useState(false);
    const [alert, setAlert] = useState({open: false, message: '', severity: 'info'})
    const {t} = useTranslation();

    useEffect(() => {
        trackPromise(
            restaurantService.getAll(size, page, filter.query), area).then(({data}) => {
            setRestaurants(data.content);
            setTotalPages(data.totalPages);
        });

        if (filter.query.length) {
            push({
                pathname,
                search: `?query=${filter.query}&size=${size}&page=${page}`,
            });
        } else {
            push(pathname)
        }
    }, [setRestaurants, page, size, filter])

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
                setRestaurantFromDB(response)
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
                            fileName: updatedRestaurant.fileName,
                        }
                        :
                        restaurant
                }
            )
        })
        openAlert([t('record saved')], "success")
    }

    const removeRestaurant = (restaurant) => {
        Promise.all([
            imageService.removeAllByFileNames(restaurant.fileNames),
            reviewService.deleteAllByItemId(restaurant.id),
            restaurantService.delete(restaurant.id),
        ])
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

    const changePage = (event, value) => {
        setPage(value);
    }

    const changeSize = (event) => {
        setSize(event.target.value);
        setPage(1);
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
                    <hr style={{margin: '15px 0'}}/>
                </>
                :
                ""}

            <ItemFilter
                filter={filter}
                setFilter={setFilter}
            />

            <MySelect size={size} changeSize={changeSize} pageSizes={pageSizes}/>
            {promiseInProgress
                ? <SkeletonGrid listsToRender={16}/>
                : <>
                    <ItemGrid items={restaurants} route={RESTAURANTS_ROUTE} update={update}
                              remove={removeRestaurant}/>
                    <Pagination count={totalPages} page={page} onChange={changePage} shape="rounded"
                                className="pagination"/>
                </>
            }
            <MyModal visible={modal} setVisible={setModal}>
                <RestaurantEditor create={createRestaurant} update={updateRestaurant}
                                  restaurantFromDB={restaurantFromDB} setAlert={setAlert}
                                  modal={modal} setModal={setModal}/>
            </MyModal>
            <MyNotification open={alert.open} setOpen={setAlert} message={alert.message} severity={alert.severity}/>
        </Container>
    );
};

export default RestaurantList;