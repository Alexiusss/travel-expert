import {t} from "i18next";
import {store} from "../store";

export const USERS_ROUTE = '/users/'
export const LOGIN = '/auth/login/'
export const LOGOUT = '/auth/logout/'

export const getLocalizedErrorMessages = (messages) => {
    return messages.split('\n').map(message => {
        let splitMessage = message.match('(?<=(\\[\\w*\\] )).+');
        if(splitMessage) {
            return splitMessage[1] + t(splitMessage[0]);
        }
        return t(message);
    });
}

export const getAccessToken = () => {
    let accessToken = store.getState().user.token;
    if (accessToken === "undefined" || accessToken === null) {
        accessToken = '';
    }
    return accessToken;
}