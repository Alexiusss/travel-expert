import React, {Suspense} from 'react';
import ReactDOM from 'react-dom';
import {Provider} from "react-redux";
import {store} from "./store";
import App from './App';
import 'bootstrap/dist/css/bootstrap.min.css';
import './index.css';
import './i18n';

ReactDOM.render(
    <React.StrictMode>
        <Provider store={store}>
        <Suspense fallback={<div>Loading...</div>}>
            <App/>
        </Suspense>
        </Provider>
    </React.StrictMode>,
    document.getElementById('root')
);

