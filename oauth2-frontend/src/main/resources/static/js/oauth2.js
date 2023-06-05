// https://www.rfc-editor.org/rfc/rfc7636

const SHA_256 = "SHA-256";

function initValues() {
    let state = generateState(30);
    document.getElementById("originalState").innerHTML = state;
    
    let codeVerifier = generateCodeVerifier();
    document.getElementById("codeVerifier").innerHTML = codeVerifier;

    generateCodeChallenge(codeVerifier).then(codeChallenge => {
        console.log("codeChallenge = " + codeChallenge)
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