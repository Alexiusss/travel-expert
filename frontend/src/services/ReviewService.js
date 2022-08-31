import $api from "../http/http-common";
import {REVIEWS_ROUTE} from "../utils/consts";

export default class ReviewService {
    static async getAll() {
        return $api.get(REVIEWS_ROUTE);
    }

    static async getAllByItemId(id,size = 20, page = 1) {
        return $api.get(REVIEWS_ROUTE + `${id}` + '/item', {
            params: {
                size: size,
                page: page - 1
            }
        });
    }

    static async create(data) {
        return $api.post(REVIEWS_ROUTE, data);
    }
}