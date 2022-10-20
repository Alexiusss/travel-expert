import $api from "../http/http-common";
import {REVIEWS_ROUTE} from "../utils/consts";

export default class ReviewService {

    static async get(id) {
        return $api.get(REVIEWS_ROUTE+ `${id}`);
    }

    static async getRating(id) {
        return (await $api.get(REVIEWS_ROUTE + `${id}` + '/item/rating')).data;
    }

    static async getRatingByUserId(userId) {
        return $api.get(REVIEWS_ROUTE + `${userId}` + '/user/rating');
    }

    static async getRatingValueByItemId(itemId) {
        return $api.get(REVIEWS_ROUTE + `${itemId}` + '/rating');
    }

    static async getCountByUserId(userId) {
        return $api(REVIEWS_ROUTE + `${userId}` + '/user/count');
    }

    static async getAll(size = 20, page = 1, filter = "") {
        return $api.get(REVIEWS_ROUTE, {
            params: {
                size: size,
                page: page - 1,
                filter: filter.query
            }
        });
    }

    static async getAllByItemId(id,size = 20, page = 1, filter = "") {
        return $api.get(REVIEWS_ROUTE + `${id}` + '/item', {
            params: {
                size: size,
                page: page - 1,
                filter: filter.query,
                ratings: filter.ratingFilters.toString()
            }
        });
    }

    static async getAllByUserId(userId) {
        return $api.get(REVIEWS_ROUTE + `${userId}` + '/user');
    }

    static async getAllActiveByUserId(userId) {
        return $api.get(REVIEWS_ROUTE + `${userId}` + '/user/active');
    }

    static async create(data) {
        return $api.post(REVIEWS_ROUTE, data);
    }

    static async update(id, data) {
        return $api.put(REVIEWS_ROUTE+ `${id}`, data)
    }

    static async delete(id) {
        return $api.delete(REVIEWS_ROUTE+ `${id}`);
    }

    static async deleteAllByUserId(userId) {
        return $api.delete(REVIEWS_ROUTE+ `${userId}` + "/user");
    }

    static async deleteAllByItemId(itemId) {
        return $api.delete(REVIEWS_ROUTE+ `${itemId}` + '/item');
    }
}