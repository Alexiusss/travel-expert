import React, {Suspense} from 'react';
import ReactDOM from 'react-dom';
import {Provider} from "react-redux";
import {PersistGate} from "redux-persist/integration/react";
import store, {persistor} from "./store";
import App from './App';
import 'bootstrap/dist/css/bootstrap.min.css';
import './index.css';
import './i18n';

ReactDOM.render(
    <React.StrictMode>
        <Provider store={store}>
            <PersistGate loading={null} persistor={persistor}>
                <Suspense fallback={<div>Loading...</div>}>
                    <App/>
                </Suspense>
            </PersistGate>
        </Provider>
    </React.StrictMode>,
    document.getElementById('root')
);

