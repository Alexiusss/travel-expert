import $api from "../http/http-common";
import {RESTAURANTS_ROUTE} from "../utils/consts";

export default class RestaurantService {
    static async getAll(size = 20, page = 1, filter = "") {
        return $api.get(RESTAURANTS_ROUTE, {
            params: {
                size: size,
                page: page - 1,
                filter: filter
            },
        });
    }

    static async get(id) {
        return (await $api.get(RESTAURANTS_ROUTE + `${id}`)).data;
    }

    static async create(data) {
        return $api.post(RESTAURANTS_ROUTE, data);
    }

    static async delete(id) {
        return $api.delete(RESTAURANTS_ROUTE + `${id}`);
    }

    static async update(data, id) {
        return $api.put(RESTAURANTS_ROUTE+ `${id}`, data);
    }
}