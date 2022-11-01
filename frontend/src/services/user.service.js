import httpClient from "../http/http-common";
import { USERS_ROUTE } from "../utils/consts";

const getAll = (size = 20, page = 1) => {
    return httpClient.get(USERS_ROUTE, {
        params: {
            size: size,
            page: page - 1,
        },
    });
};

const create = (data) => {
    return httpClient.post(USERS_ROUTE, data);
};

const get = (id) => {
    return httpClient.get(USERS_ROUTE + `${id}`);
};

const getAuthor = (id) => {
    return httpClient.get(USERS_ROUTE + `${id}` + '/author');
};

const getAuthorByUsername = (username) => {
    return httpClient.get(USERS_ROUTE + `${username}` + "/authorByUsername");
};

const update = (data, id) => {
    return httpClient.put(USERS_ROUTE + `${id}`, data);
};

const enable = (id, enabled) => {
    return httpClient.patch(USERS_ROUTE + `${id}` + "?enable=" + enabled);
};

const remove = (id) => {
    return httpClient.delete(USERS_ROUTE + `${id}`);
};

export default { getAll, create, get, getAuthor, getAuthorByUsername, update, enable, remove };
