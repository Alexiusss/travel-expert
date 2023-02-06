import React, {useEffect, useState} from 'react';
import {Container} from "@material-ui/core";
import ItemGrid from "../components/items/ItemGrid";
import {getLocalizedErrorMessages, HOTELS_ROUTE} from "../utils/consts";
import hotelService from "../services/HotelService"
import SkeletonGrid from "../components/SkeletonGrid";
import {trackPromise, usePromiseTracker} from "react-promise-tracker";
import Pagination from "@material-ui/lab/Pagination";
import MySelect from "../components/UI/select/MySelect";
import ItemFilter from "../components/items/ItemFilter";
import {useHistory, useLocation} from "react-router-dom";
import {useTranslation} from "react-i18next";
import reviewService from "../services/ReviewService";
import imageService from "../services/ImageService";
import MyNotification from "../components/UI/notification/MyNotification";
import MyButton from "../components/UI/button/MyButton";
import {useAuth} from "../components/hooks/UseAuth";
import MyModal from "../components/UI/modal/MyModal";
import HotelEditor from "./HotelEditor";

const HotelList = () => {
    const area = 'hotels';
    const {promiseInProgress} = usePromiseTracker({area});
    const {pathname, search} = useLocation();
    const {push} = useHistory();
    const params = new URLSearchParams(search);
    const [hotels, setHotels] = useState([]);
    const [hotelFromDB, setHotelFromDB] = useState({})
    const {isAdmin} = useAuth();
    const [totalPages, setTotalPages] = useState(0);
    const [page, setPage] = useState(+params.get('page') || 1);
    const [size, setSize] = useState(+params.get('size') || 12);
    const pageSizes = [12, 36, 96];
    const [filter, setFilter] = useState({config: null, query: params.get('query') || ''})
    const [modal, setModal] = useState(false);
    const [alert, setAlert] = useState({open: false, message: '', severity: 'info'});
    const {t} = useTranslation();


    const createHotel = (newHotel) => {
        setHotels([...hotels, newHotel])
        setModal(false)
    }

    const update = (hotel) => {
        hotelService.get(hotel.id)
            .then(response => {
                setHotelFromDB(response);
                setModal(true);
            })
            .catch(
                error => {
                    openAlert(getLocalizedErrorMessages(error.response.data.message), "error");
                });
    }

    const updateHotel = (updatedHotel) => {
        setModal(false);
        setHotels(hotels => {
                return hotels.map(hotel => {
                    return hotel.id === updatedHotel.id
                        ?
                        {
                            ...hotel,
                            name: updatedHotel.name,
                        }
                        :
                        hotel
                })
            }
        )
        openAlert([t('record saved')], "success")
    }

    const changePage = (e, value) => {
        setPage(value);
    }

    const changeSize = (event) => {
        setSize(event.target.value);
        setPage(1);
    }

    const removeHotel = ({id, fileNames}) => {
        Promise.all([
            imageService.removeAllByFileNames(fileNames),
            reviewService.deleteAllByItemId(id),
            hotelService.remove(id),
        ])
            .then(() => {
                setHotels(hotels.filter(hotel => hotel.id !== id));
                openAlert([t('record deleted')], "success");
            })
            .catch(
                error => {
                    openAlert(getLocalizedErrorMessages(error.response.data.message), "error");
                });
    }

    const openAlert = (msg, severity) => {
        setAlert({severity: severity, message: msg, open: true})
    }

    useEffect(() => {
        trackPromise(
            hotelService.getAll(size, page, filter.query), area).then(({data}) => {
            setHotels(data.content);
            setTotalPages(data.totalPages)
        });
        if (filter.query.length) {
            push({
                pathname,
                search: `?query=${filter.query}&size=${size}&page=${page}`,
            });
        } else {
            push(pathname)
        }
    }, [setHotels, page, size, filter]);

    return (
        <Container>
            {isAdmin ?
                <>
                    <MyButton style={{marginTop: 10}} className={"btn btn-outline-primary ml-2 btn-sm"}
                              onClick={() => setModal(true)}>
                        {t("add")}
                    </MyButton>
                    <hr style={{margin: '15px 0'}}/>
                </> : null
            }
            <ItemFilter filter={filter} setFilter={setFilter}/>
            {(hotels.length > pageSizes[0]) ?
                <MySelect size={size} changeSize={changeSize} pageSizes={pageSizes}/> : null
            }
            {promiseInProgress
                ? <SkeletonGrid listsToRender={16}/>
                : <>
                    <ItemGrid items={hotels} update={update} remove={removeHotel} route={HOTELS_ROUTE}/>
                    {totalPages > 1 ?
                        <Pagination count={totalPages} page={page} onChange={changePage} shape="rounded"
                                    className="pagination"/>
                        : null
                    }
                </>
            }
            <MyModal visible={modal} setVisible={setModal}>
                <HotelEditor
                    create={createHotel}
                    hotelFromDB={hotelFromDB}
                    update={updateHotel}
                    setAlert={setAlert}
                    modal={modal}
                    setModal={setModal}
                />
            </MyModal>
            <MyNotification open={alert.open} setOpen={setAlert} message={alert.message} severity={alert.severity}/>
        </Container>

    );
};

export default HotelList;