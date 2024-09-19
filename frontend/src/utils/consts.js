import {t} from "i18next";
import store from "../store";

export const USERS_ROUTE = '/users/';
export const RESTAURANTS_ROUTE = '/restaurants/';
export const HOTELS_ROUTE = '/hotels/';
export const IMAGE_ROUTE = '/images/';
export const REVIEWS_ROUTE = '/reviews/';
export const PROFILE = '/profile/';
export const LOGIN = '/auth/login/';
export const REGISTER = '/auth/register/';
export const LOGOUT = '/auth/logout/';
export const TRANSLATION_URL = 'https://api.reverso.net/translate/v1/translation';
const BASE_URL = 'https://travelexpert.ddns.net';
export const HOTEL_API_DOCS_URL = BASE_URL + '/hotel/swagger-ui';
export const RESTAURANT_API_DOCS_URL = BASE_URL + '/restaurant/swagger-ui';
export const REVIEW_API_DOCS_URL = BASE_URL + '/review/swagger-ui';
export const IMAGE_API_DOCS_URL = BASE_URL + '/image/swagger-ui';
export const USER_API_DOCS_URL = BASE_URL + '/user/swagger-ui';

export const getLocalizedErrorMessages = (messages) => {
    return messages.split('\n').map(message => {
        let splitMessage = message.match('(?<=(\\[\\w*\\] )).+');
        if (splitMessage) {
            return splitMessage[1] + t(splitMessage[0]);
        }
        return t(message);
    });
}

// https://stackoverflow.com/a/50607453
export const getFormattedDate = (date, i18n) => {
    const DATE_OPTIONS = {year: 'numeric', month: 'short', day: 'numeric'};
    return new Date(date).toLocaleDateString(i18n.language, DATE_OPTIONS);
}

export const getAccessToken = () => {
    let accessToken = store.getState().user.token;
    if (accessToken === "undefined" || accessToken === null) {
        accessToken = '';
    }
    return accessToken;
}

export const updateLocalAccessToken = (newToken) => {
    let storage = JSON.parse(localStorage.getItem('persist:root'));
    let user = JSON.parse(storage.user);
    user.token = newToken;
    storage.user = JSON.stringify(user);
    storage = JSON.stringify(storage);
    localStorage.setItem('persist:root', storage)
    window.location.reload();
}
