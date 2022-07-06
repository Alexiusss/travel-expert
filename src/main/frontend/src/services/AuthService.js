import $api from "../http/http-common";
import {LOGIN, LOGOUT, PROFILE, REGISTER} from "../utils/consts";

export default class AuthService {
    static async login(email, password) {
        return $api.post(LOGIN, {email, password});
    }
    static async logout() {
        return $api.get(LOGOUT);
    }
    static async register(firstName, lastName, email, password) {
        return $api.post(REGISTER, {firstName, lastName, email, password});
    }

    static async profile() {
        return $api.get(PROFILE);
    }

}