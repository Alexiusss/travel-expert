import Keycloak from "keycloak-js";
import {CLIENT_ID, KEYCLOAK_REALM, KEYCLOAK_URI} from "../utils/consts";

const keycloak = new Keycloak({
    url: KEYCLOAK_URI + "/auth",
    realm: KEYCLOAK_REALM,
    clientId: CLIENT_ID,
});

export default {keycloak}