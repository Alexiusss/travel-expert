const webpack = require('webpack')

// https://github.com/react-keycloak/react-keycloak/issues/176#issuecomment-1382936969
module.exports = {

    webpack: {
        configure: {
            ignoreWarnings: [/Failed to parse source map.*react-keycloak/],
        },
    },
}