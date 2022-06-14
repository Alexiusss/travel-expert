import React, {useState} from 'react';
import authService from '../../services/AuthService.js'
import classes from './LoginForm.module.css'
import {useTranslation} from "react-i18next";
import MyNotification from "../UI/notification/MyNotification";
import {getLocalizedErrorMessages} from "../../utils/consts";

const LoginForm = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [alert, setAlert] = useState({open: false, message: '', severity: 'info'})
    const {t} = useTranslation();

    const openAlert = (msg, severity) => {
        setAlert({severity: severity, message: msg, open: true})
    }

    const login = (e) => {
        e.preventDefault()
        authService.login(email, password)
            .then(response => {
                localStorage.setItem('access-token', response.data.accessToken)
            })
            .catch(error =>
                openAlert(getLocalizedErrorMessages(error.response.data.message), "error")
            )
    }

    return (
        <div className={classes.myForm}>
            <form>
                <div className="form-group">
                    <input
                        value={email}
                        onChange={e => setEmail(e.target.value)}
                        placeholder={t("enter email")}
                        className="form-group row"
                        type="email"
                    />
                </div>

                <div className="form-group" style={{marginTop: 10}}>
                    <input
                        value={password}
                        onChange={e => setPassword(e.target.value)}
                        placeholder={t("enter password")}
                        className="form-group row"
                        type="password"
                    />
                </div>
                <button style={{marginTop: 15}} className={"btn btn-outline-primary ml-2 btn-sm"}
                        onClick={(e) => login(e)}>
                    {t("sign in")}
                </button>
            </form>
            <MyNotification open={alert.open} setOpen={setAlert} message={alert.message} severity={alert.severity}/>
        </div>
    );
};

export default LoginForm;