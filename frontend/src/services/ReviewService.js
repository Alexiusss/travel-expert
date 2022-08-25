import $api from "../http/http-common";
import {REVIEWS_ROUTE} from "../utils/consts";

export default class ReviewService {
    static async getAll() {
        return $api(REVIEWS_ROUTE);
    }
}