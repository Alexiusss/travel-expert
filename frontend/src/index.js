import React, {Suspense} from 'react';
import { ReactKeycloakProvider } from '@react-keycloak/web'
import ReactDOM from 'react-dom';
import {Provider} from "react-redux";
import {PersistGate} from "redux-persist/integration/react";
import {initOptions, keycloak} from "./services/kc/KeycloakAuthService"
import store, {persistor} from "./store";
import App from './App';
import 'bootstrap/dist/css/bootstrap.min.css';
import './index.css';
import './i18n';

    ReactDOM.render(
        <React.StrictMode>
            <ReactKeycloakProvider authClient={keycloak} initOptions={initOptions}>
                <Provider store={store}>
                    <PersistGate loading={null} persistor={persistor}>
                        <Suspense fallback={<div>Loading...</div>}>
                            <App/>
                        </Suspense>
                    </PersistGate>
                </Provider>
            </ReactKeycloakProvider>
        </React.StrictMode>,
        document.getElementById('root')
    );

