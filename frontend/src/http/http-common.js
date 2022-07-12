import axios from "axios";
import {getAccessToken, updateLocalAccessToken} from "../utils/consts";
import store from "../store";
import Cookies from "js-cookie";
export const API_URL = "http://localhost:8080/api/v1";

const $api = axios.create({
    withCredentials: true,
    baseURL: API_URL,
    headers: {
        "Content-Type": "application/json",
        "X-XSRF-TOKEN": Cookies.get('XSRF-TOKEN'),
    },
});

$api.interceptors.request.use((config) => {
    if (!!store.getState().user.token) {
        config.headers.Authorization = `Bearer ${getAccessToken()}`;
    }
    return config;
});

$api.interceptors.response.use((config) => {
    return config;
}, async (error) => {
    const originalRequest = error.config;
    if (error.response.status === 401 && error.config && !error.config._isRetry) {
        originalRequest._isRetry = true;
        const response = await axios.get(`${API_URL}/auth/refresh`, {withCredentials: true})
        try {
            updateLocalAccessToken(response.data.accessToken)
            return $api.request(originalRequest)
        } catch (e) {
            console.error('Unauthorized')
        }
    }
    throw error;
})

export default $api;