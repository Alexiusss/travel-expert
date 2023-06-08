// https://www.rfc-editor.org/rfc/rfc7636

const RESPONSE_TYPE_CODE = "code";
const GRANT_TYPE_AUTH_CODE = "authorization_code";
const GRANT_TYPE_REFRESH_TOKEN = "refresh_token";

const SHA_256 = "SHA-256";
const S256 = "S256";

const KEYCLOAK_URI = "http://localhost:8180/realms/travel-expert-realm/protocol/openid-connect";
const CLIENT_ROOT_URL = "http://localhost:9191";
const RESOURCE_SERVER_URI = "http://localhost:8080";

let accessToken = "";
let refreshToken = "";
let idToken = "";

const ID_TOKEN_KEY = "IT";
const REFRESH_TOKEN_KEY = "RT";
const STATE_KEY = "SK";
const CODE_VERIFIER_KEY = "CV";

const CLIENT_ID = "travel-expert-client";
const SCOPE = "openid";


function initPage() {

    refreshToken = localStorage.getItem(REFRESH_TOKEN_KEY);

    if (refreshToken) {
        exchangeRefreshTokenToAccessToken();
    } else {
        if (!checkAuthCode()) {
            initAccessToken();
        }
    }
}

function checkAuthCode() {
    let urlSearchParams = new URLSearchParams(window.location.search);
    let authCode = urlSearchParams.get("code"),
        state = urlSearchParams.get("state"),
        error = urlSearchParams.get("error"),
        errorDescription = urlSearchParams.get("errorDescription");

    if (!authCode) {
        return false;
    }

    requestTokens(state, authCode);

    return true;
}

function initAccessToken() {
    let state = generateState(30);
    localStorage.setItem(STATE_KEY, state);

    let codeVerifier = generateCodeVerifier();
    localStorage.setItem(CODE_VERIFIER_KEY, codeVerifier)

    generateCodeChallenge(codeVerifier).then(codeChallenge => {
        requestAuthCode(state, codeChallenge);
    })
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

function generateCodeVerifier() {
    let randomByteArray = new Uint8Array(43);
    window.crypto.getRandomValues(randomByteArray);
    return base64UrlEncode(randomByteArray);
}

async function generateCodeChallenge(codeVerifier) {
    let textEncoder = new TextEncoder('US-ASCII');
    let encodedValue = textEncoder.encode(codeVerifier);
    let digest = await window.crypto.subtle.digest(SHA_256, encodedValue);
    return base64UrlEncode(Array.from(new Uint8Array(digest)));
}

function base64UrlEncode(sourceValue) {
    let stringValue = String.fromCharCode.apply(null, sourceValue);
    let base64Encoded = btoa(stringValue);
    let base64UrlEncoded = base64Encoded.replace(/\+/g, '-').replace(/\//g, '_').replace(/=/g, '');
    return base64UrlEncoded;
}

function requestAuthCode(state, codeChallenge) {
    let authUrl = KEYCLOAK_URI + "/auth";

    authUrl += "?response_type=" + RESPONSE_TYPE_CODE;
    authUrl += "&client_id=" + CLIENT_ID;
    authUrl += "&state=" + state;
    authUrl += "&scope=" + SCOPE;
    authUrl += "&code_challenge=" + codeChallenge;
    authUrl += "&code_challenge_method=" + S256;
    authUrl += "&redirect_uri=" + CLIENT_ROOT_URL;

    window.open(authUrl, "_self");
}

function requestTokens(stateFromAuthServer, authCode) {
    let originalState = localStorage.getItem(STATE_KEY);

    if (stateFromAuthServer === originalState) {
        let codeVerifier = localStorage.getItem(CODE_VERIFIER_KEY);

        let data = {
            "grant_type": GRANT_TYPE_AUTH_CODE,
            "client_id": CLIENT_ID,
            "code": authCode,
            "code_verifier": codeVerifier,
            "redirect_uri": CLIENT_ROOT_URL
        };

        $.ajax({
            function(request) {
                request.setRequestHeader("Content-type", "application/x-www-form-urlencoded: charset=UTF8")
            },
            type: "POST",
            url: KEYCLOAK_URI + "/token",
            data: data,
            success: accessTokenResponse,
            dataType: "json"
        })

    } else {
        initAccessToken();
    }
}

function accessTokenResponse(data, status, jqXHR) {
    localStorage.removeItem(STATE_KEY);
    localStorage.removeItem(CODE_VERIFIER_KEY);

    accessToken = data["access_token"];
    refreshToken = data["refresh_token"];
    idToken = data["id_token"];

    console.log("access_token = " + accessToken);
    console.log("refresh_token = " + refreshToken);
    console.log("id_token = " + idToken);

    let payload = getJsonPayload(idToken);
    document.getElementById("email").innerHTML = "Hi " + payload["email"];

    localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
    localStorage.setItem(ID_TOKEN_KEY, idToken);

    getDataFromResourceServer();
}

function getDataFromResourceServer() {
    $.ajax({
        beforeSend: function (request) {
            request.setRequestHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF8");
            request.setRequestHeader("Authorization", "Bearer " + accessToken);
        },
        type: "GET",
        url: RESOURCE_SERVER_URI + "/api/v1/kc-users/",
        success: resourceServerResponse,
        error: resourceServerError,
        dataType: "json"
    })
}

function resourceServerResponse(data, status, jqXHR) {
    document.getElementById("userData").innerHTML = data;
}

function resourceServerError(request, status, error) {
    let json = JSON.parse(request.responseText);
    let errorType = json["type"];

    console.log(errorType)

    let refreshToken = localStorage.getItem(REFRESH_TOKEN_KEY);
    if (refreshToken) {
        exchangeRefreshTokenToAccessToken();
    } else {
        initAccessToken();
    }
}

function exchangeRefreshTokenToAccessToken() {
    console.log("new access token initiated");

    let data = {
        "grant_type": GRANT_TYPE_REFRESH_TOKEN,
        "client_id": CLIENT_ID,
        "refresh_token": refreshToken
    }

    $.ajax({
        beforeSend: function (request) {
            request.setRequestHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        },
        type: "POST",
        url: KEYCLOAK_URI + "/token",
        data: data,
        success: accessTokenResponse,
        dataType: "json"
    });
}

function logout() {
    let idToken = localStorage.getItem(ID_TOKEN_KEY);

    console.log("logout")

    let authUrl = KEYCLOAK_URI + "/logout";

    authUrl += "?post_logout_redirect_uri=" + CLIENT_ROOT_URL;
    authUrl += "&id_token_hint=" + idToken;
    authUrl += "&client_id=" + CLIENT_ID;

    window.open(authUrl, '_self');

    localStorage.removeItem(REFRESH_TOKEN_KEY);
    localStorage.removeItem(ID_TOKEN_KEY);

    accessToken = "";
    refreshToken = ""
}

// https://stackoverflow.com/questions/38552003/how-to-decode-jwt-token-in-javascript-without-using-a-library
function getJsonPayload(token) {
    let base64Url = token.split('.')[1];
    let base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    let jsonPayload = decodeURIComponent(window.atob(base64).split('').map(function (c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));

    return JSON.parse(jsonPayload);
}