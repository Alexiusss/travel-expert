import $api from "../http/http-common";
import {HOTELS_ROUTE} from "../utils/consts";

const getAll = () => {
    return $api.get(HOTELS_ROUTE);
};

export default {getAll}