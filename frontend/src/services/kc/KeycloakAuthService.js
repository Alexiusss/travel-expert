import Keycloak from "keycloak-js";
import $api from "../../http/http-common";
import {BFF_ROUTE, PROFILE} from "../../utils/consts";

export const keycloak = new Keycloak("/keycloak.json");

export const initOptions = {
    responseType: "code",
    scope: "openid",
    redirectUri: "http://localhost:3000/travel-expert/",
};

const exchangeCodeForToken = (authCode) => {
    return $api.post(BFF_ROUTE + "token", authCode);
};

const updateAccessToken = () => {
    return $api.get(BFF_ROUTE + "newaccesstoken");
};

const profile = () => {
    return $api.get(PROFILE);
}

const logout = () => {
    return $api.get(BFF_ROUTE + "logout");
}

export default {exchangeCodeForToken, updateAccessToken, profile, logout}