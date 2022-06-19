import axios from "axios";
import {getAccessToken} from "../utils/consts";
import {store} from "../store";

export const API_URL = "http://localhost:8080/api/v1";

const $api = axios.create({
    withCredentials: true,
    baseURL: API_URL,
    headers: {
        "Content-Type": "application/json",
    },
});

$api.interceptors.request.use((config) => {
    if(!!store.getState().user.token) {
        config.headers.Authorization = `Bearer ${getAccessToken()}`;
    }
    return config;
});

export default $api;