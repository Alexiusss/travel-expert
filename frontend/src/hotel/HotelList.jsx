import React, {useEffect, useState} from 'react';
import {Container} from "@material-ui/core";
import ItemGrid from "../components/items/ItemGrid";
import {HOTELS_ROUTE} from "../utils/consts";
import hotelService from "../services/HotelService"
import SkeletonGrid from "../components/SkeletonGrid";
import {trackPromise, usePromiseTracker} from "react-promise-tracker";
import Pagination from "@material-ui/lab/Pagination";
import MySelect from "../components/UI/select/MySelect";
import ItemFilter from "../components/items/ItemFilter";

const HotelList = () => {
    const area = 'hotels';
    const {promiseInProgress} = usePromiseTracker({area});
    const [hotels, setHotels] = useState([]);
    const [totalPages, setTotalPages] = useState(0);
    const [page, setPage] = useState(1);
    const [size, setSize] = useState(12);
    const pageSizes = [12, 36, 96];
    const [filter, setFilter] = useState({config: null, query: ''})

    useEffect(() => {
        trackPromise(
            hotelService.getAll(size, page, filter.query), area).then(({data}) => {
            setHotels(data.content);
            setTotalPages(data.totalPages)
        });
    }, [setHotels, page, size, filter]);

    const changePage = (e, value) => {
        setPage(value);
    }

    const changeSize = (event) => {
        setSize(event.target.value);
        setPage(1);
    }

    return (
        <Container>
            <ItemFilter filter={filter} setFilter={setFilter}/>
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