import React, {useState} from 'react';
import {Trans, useTranslation} from "react-i18next";
import ButtonGroup from "../components/UI/button/ButtonGroup";
import {useAuth} from "../components/hooks/UseAuth";
import authService from 'services/AuthService.js'
import {setUser} from "../store/slices/userSlice";
import {getLocalizedErrorMessages} from "../utils/consts";
import {useDispatch} from "react-redux";
import MyNotification from "../components/UI/notification/MyNotification";
import MyButton from "../components/UI/button/MyButton";

const HomePage = () => {
    const {t} = useTranslation();
    const {isAuth} = useAuth();
    const dispatch = useDispatch();
    const [alert, setAlert] = useState({open: false, message: '', severity: 'info'})
    const {
        REACT_APP_ADMIN_EMAIL: adminEmail,
        REACT_APP_ADMIN_PASSWORD: adminPassword,
        REACT_APP_MODER_EMAIL: moderEmail,
        REACT_APP_MODER_PASSWORD: moderPassword,
        REACT_APP_USER_EMAIL: userEmail,
        REACT_APP_USER_PASSWORD: userPassword,
    } = process.env;

    const signInAsAdmin = (e) => {
        signIn(e, adminEmail, adminPassword);
    }

    const signInAsModerator = (e) => {
        signIn(e, moderEmail, moderPassword);
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
        <div className="container">
            {!isAuth &&
                <>
                    <ButtonGroup signInAsAdmin={signInAsAdmin} signInAsModerator={signInAsModerator} signInAsUser={signInAsUser}/>
                    <MyNotification open={alert.open} setOpen={setAlert} message={alert.message}
                                    severity={alert.severity}/>
                </>
            }
            <div className="lead py-2">
                {t('application stack')}:
                <br/>
                JDK 11, Spring Boot(Data, MVC, Security, Cloud), React, Redux, Docker, Maven, Hibernate ORM, PostgreSQL,
                FlyWay, JUnit 5, Testcontainers, WireMock, RabbitMQ, Swagger/Open API 3.0, Lombok, Caffeine Cache.
            </div>
            <hr/>
            <div className="lead py-1">
                {/*https://stackoverflow.com/a/75143145*/}
                <Trans i18nKey="application description" components={{ 1: <br /> }} />
            </div>
            <div className="lead py-3">
                    <MyButton className={"btn btn-outline-success"}
                              onClick={() => window.open("/travel-expert/apiDocs", "_blank")}
                    >
                        Api Documentation
                    </MyButton>
            </div>
        </div>
    )
}
export default HomePage;