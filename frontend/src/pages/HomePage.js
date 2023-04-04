import React, {useState} from 'react';
import {useTranslation} from "react-i18next";
import ButtonGroup from "../components/UI/button/ButtonGroup";
import {useAuth} from "../components/hooks/UseAuth";
import authService from 'services/AuthService.js'
import {setUser} from "../store/slices/userSlice";
import {getLocalizedErrorMessages} from "../utils/consts";
import {useDispatch} from "react-redux";
import MyNotification from "../components/UI/notification/MyNotification";

const HomePage = () => {
    const {t} = useTranslation();
    const {isAuth} = useAuth();
    const dispatch = useDispatch();
    const [alert, setAlert] = useState({open: false, message: '', severity: 'info'})
    const {
        REACT_APP_ADMIN_EMAIL: adminEmail,
        REACT_APP_ADMIN_PASSWORD: adminPassword,
        REACT_APP_USER_EMAIL: userEmail,
        REACT_APP_USER_PASSWORD: userPassword,
    } = process.env;

    const signInAsAdmin = (e) => {
        signIn(e, adminEmail, adminPassword);
    }

    const signInAsUser = (e) => {
        signIn(e, userEmail, userPassword);
    }

    const signIn = (e, email, password) => {
        e.preventDefault()
        authService.login(email, password)
            .then(response => {
                dispatch(setUser({
                    email: response.data.email,
                    id: response.data.userId,
                    token: response.data.accessToken,
                    authorities: response.data.authorities,
                }));
            })
            .catch(error =>
                openAlert(getLocalizedErrorMessages(error.response.data.message), "error")
            )
    }

    const openAlert = (msg, severity) => {
        setAlert({severity: severity, message: msg, open: true})
    }

    return (
        <>
            {!isAuth &&
                <>
                    <ButtonGroup signInAsAdmin={signInAsAdmin} signInAsUser={signInAsUser}/>
                    <MyNotification open={alert.open} setOpen={setAlert} message={alert.message}
                                    severity={alert.severity}/>
                </>
            }
        </>
    )
}
export default HomePage;