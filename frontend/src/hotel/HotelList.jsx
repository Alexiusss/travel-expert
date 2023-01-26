import React, {useEffect, useState} from 'react';
import {Container} from "@material-ui/core";
import ItemGrid from "../components/items/ItemGrid";
import {HOTELS_ROUTE} from "../utils/consts";
import hotelService from "../services/HotelService"
import SkeletonGrid from "../components/SkeletonGrid";
import {trackPromise, usePromiseTracker} from "react-promise-tracker";
import Pagination from "@material-ui/lab/Pagination";
import MySelect from "../components/UI/select/MySelect";

const HotelList = () => {
    const area = 'hotels';
    const {promiseInProgress} = usePromiseTracker({area});
    const [hotels, setHotels] = useState([]);
    const [totalPages, setTotalPages] = useState(0);
    const [page, setPage] = useState(0);
    const [size, setSize] = useState(0);
    const pageSizes = [12, 36, 96];

    useEffect(() => {
        trackPromise(
            hotelService.getAll(size, page), area).then(({data}) => {
            setHotels(data.content);
            setTotalPages(data.totalPages)
        });
    }, [setHotels, page, size]);

    const changePage = (e, value) => {
        setPage(value);
    }

    const changeSize = (event) => {
        setSize(event.target.value);
        setPage(1);
    }

    return (
        <Container>
            <MySelect size={size} changeSize={changeSize} pageSizes={pageSizes}/>
            {promiseInProgress
                ? <SkeletonGrid listsToRender={16}/>
                : <>
                    <ItemGrid items={hotels} route={HOTELS_ROUTE}/>
                    {totalPages > 0 ?
                        <Pagination count={totalPages} page={page} onChange={changePage} shape="rounded"
                                    className="pagination"/>
                        : null
                    }
                </>
            }
        </Container>

    );
};

export default HotelList;