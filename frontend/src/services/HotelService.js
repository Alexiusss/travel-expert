import $api from "../http/http-common";
import {HOTELS_ROUTE} from "../utils/consts";

const getAll = (size = 20, page = 1, filter = "") => {
    return $api.get(HOTELS_ROUTE, {
            params: {
                size: size,
                page: page - 1,
                filter: filter
            },
        });
};

const get = (id) => {
    return $api.get(HOTELS_ROUTE + `${id}`).then(({data}) => data);
}

export default {getAll, get}