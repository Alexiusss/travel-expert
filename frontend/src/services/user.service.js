import httpClient from "../http/http-common";
import {USERS_ROUTE} from "../utils/consts";

const {REACT_APP_USERS_PATH:USERS} = process.env;

const getAll = (size = 20, page = 1) => {
    return httpClient.get(USERS, {
        params: {
            size: size,
            page: page - 1,
        },
    });
};

const create = (data) => {
    return httpClient.post(USERS, data);
};

const get = (id) => {
    return httpClient.get(USERS + `${id}`);
};

const getAuthor = (id) => {
    return httpClient.get(USERS + `${id}` + '/author');
};

const getAuthorList = (ids) => {
    return httpClient.post(USERS_ROUTE + 'authorList', ids);
};

const getAuthorByUsername = (username) => {
    return httpClient.get(USERS + `${username}` + "/authorByUsername");
};

const update = (data, id) => {
    return httpClient.put(USERS + `${id}`, data);
};

const subscribe = (id) => {
    return httpClient.get(USERS_ROUTE + 'subscribe/' + `${id}`)
}

const unSubscribe = (id) => {
    return httpClient.get(USERS_ROUTE + 'unSubscribe/' + `${id}`)
}

const enable = (id, enabled) => {
    return httpClient.patch(USERS + `${id}` + "?enable=" + enabled);
};

const remove = (id) => {
    return httpClient.delete(USERS + `${id}`);
};

export default {
    getAll,
    create,
    get,
    getAuthor,
    getAuthorByUsername,
    getAuthorList,
    update,
    subscribe,
    unSubscribe,
    enable,
    remove
};
