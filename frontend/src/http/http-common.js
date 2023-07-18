import axios from "axios";
import {getAccessToken, updateLocalAccessToken} from "../utils/consts";
import store from "../store";
import Cookies from "js-cookie";
import jwt_decode from "jwt-decode";
import keycloakAuthService from "../services/kc/KeycloakAuthService"
//export const API_URL = "https://travelexpert.sytes.net/api/v1";
export const API_URL = "http://localhost:8083/api/v1";

const $api = axios.create({
    withCredentials: true,
    baseURL: API_URL,
    headers: {
        "Content-Type": "application/json",
        "X-XSRF-TOKEN": Cookies.get('XSRF-TOKEN'),
    },
});

// $api.interceptors.request.use( async (config) => {
//     if (!!store.getState().user.token) {
//         const decoded = jwt_decode(store.getState().user.token);
//         if (decoded.exp && decoded.exp - Math.floor(Date.now() / 1000) < 60) {
//             const response = await axios.get(`${API_URL}/auth/refresh`, {withCredentials: true}) || "";
//             updateLocalAccessToken(response.data.accessToken)
//         } else {
//             config.headers.Authorization = `Bearer ${getAccessToken()}`;
//         }
//     }
//     return config;
// });

// $api.interceptors.response.use((config) => {
//     return config;
// }, async (error) => {
//     const originalRequest = error.config;
//     if (error.response.status === 401 && error.config && !error.config._isRetry) {
//         originalRequest._isRetry = true;
//         //const response = await axios.get(`${API_URL}/auth/refresh`, {withCredentials: true})
//         try {
//             //updateLocalAccessToken(response.data.accessToken)
//             await keycloakAuthService.updateAccessToken()
//             return $api.request(originalRequest)
//         } catch (e) {
//             console.error('Unauthorized')
//         }
//     }
//     throw error;
// })

export default $api;