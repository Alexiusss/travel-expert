// https://www.rfc-editor.org/rfc/rfc7636

const SHA_256 = "SHA-256";
const KEYCLOAK_URI = "http://localhost:8180/realms/travel-expert-realm/protocol/openid-connect";
const RESPONSE_TYPE_CODE = "code";
const GRANT_TYPE_AUTH_CODE = "authorization_code";
const CLIENT_ID = "travel-expert-client";
const SCOPE = "openid";
const S256 = "S256";
const AUTH_CODE_REDIRECT_URI = "http://localhost:9191/redirect";
const RESOURCE_SERVER_URI = "http://localhost:8080";

function initValues() {
    let state = generateState(30);
    document.getElementById("originalState").innerHTML = state;

    let codeVerifier = generateCodeVerifier();
    document.getElementById("codeVerifier").innerHTML = codeVerifier;

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
    authUrl += "&redirect_uri=" + AUTH_CODE_REDIRECT_URI;

    window.open(authUrl, 'auth window', 'width=800,height=600,left=350,top=200')
}

function requestTokens(stateFromAuthServer, authCode) {
     let originalState = document.getElementById("originalState").innerText;

    if (stateFromAuthServer === originalState) {
        let codeVerifier = document.getElementById("codeVerifier").innerText;

        let data = {
            "grant_type": GRANT_TYPE_AUTH_CODE,
            "client_id": CLIENT_ID,
            "code": authCode,
            "code_verifier": codeVerifier,
            "redirect_uri": AUTH_CODE_REDIRECT_URI
        };

        $.ajax({
            function (request) {
                request.setRequestHeader("Content-type", "application/x-www-form-urlencoded: charset=UTF8")
            },
            type: "POST",
            url: KEYCLOAK_URI + "/token",
            data: data,
            success: accessTokenResponse,
            dataType: "json"
        })

    } else {
        alert("Error state value");
    }
}

function accessTokenResponse(data, status, jqXHR) {
    let accessToken  = data["access_token"];
    getDataFromResourceServer(accessToken);
}

function getDataFromResourceServer(accessToken) {
    $.ajax({
        beforeSend: function (request) {
            request.setRequestHeader("Content-type", "application/x-www-form-urlencoded: charset=UTF8");
            request.setRequestHeader("Authorization", "Bearer " + accessToken);
        },
        type: "GET",
        url: RESOURCE_SERVER_URI + "/api/v1/kc-users/",
        success: resourceServerResponse,
        dataType: "json"
    })
}

function resourceServerResponse(data, status, jqXHR) {
    document.getElementById("userData").innerHTML = data;
}