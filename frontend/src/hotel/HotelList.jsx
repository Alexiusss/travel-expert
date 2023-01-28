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
import {useHistory, useLocation} from "react-router-dom";

const HotelList = () => {
    const area = 'hotels';
    const {promiseInProgress} = usePromiseTracker({area});
    const {pathname, search} = useLocation();
    const {push} = useHistory();
    const params = new URLSearchParams(search);
    const [hotels, setHotels] = useState([]);
    const [totalPages, setTotalPages] = useState(0);
    const [page, setPage] = useState(+params.get('page') || 1);
    const [size, setSize] = useState(+params.get('size') || 12);
    const pageSizes = [12, 36, 96];
    const [filter, setFilter] = useState({config: null, query: params.get('query') || ''})

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
            {(hotels.length > pageSizes[0]) ?
                <MySelect size={size} changeSize={changeSize} pageSizes={pageSizes}/> : null
            }
            {promiseInProgress
                ? <SkeletonGrid listsToRender={16}/>
                : <>
                    <ItemGrid items={hotels} route={HOTELS_ROUTE}/>
                    {totalPages > 1 ?
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