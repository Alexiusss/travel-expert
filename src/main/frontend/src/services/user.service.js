import httpClient from "../http-common";
import {USERS_ROUTE} from "../utils/consts";

const getAll = () => {
    return httpClient.get(USERS_ROUTE)
}

const create = data => {
    return httpClient.post(USERS_ROUTE, data)
}

const get = id => {
    return httpClient.get(USERS_ROUTE + {id});
}

const update = data => {
    return httpClient.put(USERS_ROUTE, data);
}

const remove = id => {
    return httpClient.delete(USERS_ROUTE  + {id});
   // return httpClient.delete(USERS_ROUTE, ${id});
}

export default { getAll, create, get, update, remove };