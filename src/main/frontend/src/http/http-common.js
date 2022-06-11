import axios from "axios";

export const API_URL = "http://localhost:8080/api/v1";

const $api = axios.create({
    withCredentials: true,
    baseURL: API_URL,
    headers: {
        "Content-Type": "application/json",
    },
});

$api.interceptors.request.use((config) => {
    config.headers.Authorization = 'Bearer ' + localStorage.getItem('access-token');
    return config;
});

export default $api;