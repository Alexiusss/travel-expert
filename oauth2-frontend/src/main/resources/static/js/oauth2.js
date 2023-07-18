const CLIENT_ID = "travel-expert-client";
const SCOPE = "openid";
const RESPONSE_TYPE_CODE = "code";

const KEYCLOAK_URI = "http://localhost:8180/realms/travel-expert-realm/protocol/openid-connect";
const CLIENT_ROOT_URL = "http://localhost:9191";
const BFF_URI = "http://localhost:8902/bff";

let accessToken = "";
let refreshTokenCookieExists = "";

const USE_REFRESH_KEY = "USE_RT";
const STATE_KEY = "ST";

function initPage() {
    $.ajaxSetup({
        crossDomain: true,
        xhrFields: {
            withCredentials: true
        }
    });

    if (!checkAuthCode()) {
        refreshTokenCookieExists = localStorage.getItem(USE_REFRESH_KEY);

        if (refreshTokenCookieExists) {
            exchangeRefreshTokenToAccessToken();
        } else {
            initAccessToken();
        }
    }
}

function checkAuthCode() {
    let urlSearchParams = new URLSearchParams(window.location.search);
    let authCode = urlSearchParams.get("code"),
        state = urlSearchParams.get("state"),
        error = urlSearchParams.get("error"),
        errorDescription = urlSearchParams.get("error_description");

    if (!authCode) {
        return false;
    }

    urlSearchParams.set('code', '');

    history.replaceState(null, null, "?" + urlSearchParams.toString());

    sendCodeToBFF(state, authCode)

    return true;
}

// https://www.rfc-editor.org/rfc/rfc7636
function initAccessToken() {
    let state = generateState(30);
    localStorage.setItem(STATE_KEY, state);

    requestAuthCode(state);
}

function generateState(length) {
    let state = "";
    let alphaNumericCharacters = "ABCDEFGHIJKLMNOPQRSTUWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    let alphaNumericCharactersLength = alphaNumericCharacters.length;
    for (let i = 0; i < length; i++) {
        state += alphaNumericCharacters.charAt(Math.floor(Math.random() * alphaNumericCharactersLength))
    }
    return state;
}

function requestAuthCode(state, codeChallenge) {
    let authUrl = KEYCLOAK_URI + "/auth";

    authUrl += "?response_type=" + RESPONSE_TYPE_CODE;
    authUrl += "&client_id=" + CLIENT_ID;
    authUrl += "&state=" + state;
    authUrl += "&scope=" + SCOPE;
    authUrl += "&redirect_uri=" + CLIENT_ROOT_URL;

    window.open(authUrl, "_self");
}

function sendCodeToBFF(stateFromAuthServer, authCode) {
    let originalState = localStorage.getItem(STATE_KEY);

    if (stateFromAuthServer === originalState) {
        localStorage.removeItem(STATE_KEY);

        $.ajax({
            type: "POST",
            beforeSend: function (request) {
                request.setRequestHeader("Content-type", "application/json; charset=UTF-8");
            },
            url: BFF_URI + "/token",
            data: authCode,
            success: bffTokenResponse
        });
    } else {
        initAccessToken();
    }
}

function bffTokenResponse(data, status, jqXHR) {
    localStorage.setItem(USE_REFRESH_KEY, "true");

    getDataFromResourceServer();
}

function getDataFromResourceServer() {
    $.ajax({
        type: "GET",
        url: BFF_URI + "/data",
        success: resourceServerResponse,
        error: resourceServerError,
        dataType: "text"
    });
}

function resourceServerResponse(data, status, jqXHR) {
    document.getElementById("userData").innerHTML = data;
}

function resourceServerError(request, status, error) {
    try {
        let json = JSON.parse(request.responseText);
        let errorType = json["type"];

        console.log(errorType);

        refreshTokenCookieExists = localStorage.getItem(USE_REFRESH_KEY);

        if (refreshTokenCookieExists) {
            exchangeRefreshTokenToAccessToken();
        } else {
            initAccessToken();
        }
    } catch (exception) {
        console.trace();
    }
}

function exchangeRefreshTokenToAccessToken() {
    console.log("new access token initiated");

    $.ajax({
        type: "GET",
        url: BFF_URI + "/newaccesstoken",
        success: bffTokenResponse,
        error: exchangeRefreshError
    });
}

function exchangeRefreshError(request, status, error) {
    logout();
}

function logout() {
    localStorage.removeItem(USE_REFRESH_KEY);
    localStorage.removeItem(STATE_KEY);

    accessToken = "";
    refreshTokenCookieExists = "";

    console.log("logout");

    $.ajax({
        type: "GET",
        url: BFF_URI + "/logout",
        success: logoutRedirect
    });
}

function logoutRedirect(request, status, error) {
    window.location.href = "/";
}