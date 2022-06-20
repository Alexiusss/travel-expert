import $api from "../http/http-common";
import {LOGIN, LOGOUT} from "../utils/consts";

export default class AuthService {
    static async login(email, password) {
        return $api.post(LOGIN, {email, password})
    }
    static async logout() {
        return $api.get(LOGOUT)
    }
}