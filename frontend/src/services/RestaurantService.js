import $api from "../http/http-common";
import {RESTAURANTS_ROUTE} from "../utils/consts";

export default class RestaurantService {
    static async getAll(size = 20, page = 1) {
        return $api.get(RESTAURANTS_ROUTE, {
            params: {
                size: size,
                page: page - 1,
            },
        })
    }

    static async get(id) {
        return $api.get(RESTAURANTS_ROUTE + `${id}`)
    }

}