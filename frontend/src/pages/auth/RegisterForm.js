import React, {useState} from 'react';
import classes from './Form.module.css'
import {useHistory} from "react-router-dom";
import {useTranslation} from "react-i18next";
import MyNotification from "../../components/UI/notification/MyNotification";
import authService from "../../services/AuthService";
import {getLocalizedErrorMessages} from "../../utils/consts";

const RegisterForm = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [alert, setAlert] = useState({open: false, message: '', severity: 'info'})
    const {push} = useHistory();
    const {t} = useTranslation();

    const openAlert = (msg, severity) => {
        setAlert({severity: severity, message: msg, open: true})
    }

    const register = (e) => {
        e.preventDefault()
        authService.register(firstName, lastName, email, password)
            .then(() => openAlert(getLocalizedErrorMessages(t("success registration")), "success"))
            .then(() => new Promise((resolve) => setTimeout(resolve, 3000)))
            .then(() => push('/login'))
            .catch(error =>
                openAlert(getLocalizedErrorMessages(error.response.data.message), "error")
            )
    }

    return (
        <div>
            <div className={classes.myForm}>
                <form>
                    <div className="form-group" style={{marginTop: 10}}>
                        <input
                            value={firstName}
                            onChange={e => setFirstName(e.target.value)}
                            placeholder={t("enter first name")}
                            className="form-group row"
                            type="text"
                        />
                    </div>

                    <div className="form-group" style={{marginTop: 10}}>
                        <input
                            value={lastName}
                            onChange={e => setLastName(e.target.value)}
                            placeholder={t("enter last name")}
                            className="form-group row"
                            type="text"
                        />
                    </div>

                    <div className="form-group" style={{marginTop: 10}}>
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
                    <div style={{marginTop: 15}}>
                        <button style={{float: "left"}} className={"btn btn-outline-success ml-2 btn-sm"}
                                onClick={(e) => register(e)}>
                            {t("sign up")}
                        </button>
                        <button style={{float: "right"}} className={"btn btn-outline-primary ml-2 btn-sm"}
                                onClick={() => push('/login')}>
                            {t("sign in")}
                        </button>
                    </div>
                </form>
                <MyNotification open={alert.open} setOpen={setAlert} message={alert.message} severity={alert.severity}/>
            </div>
        </div>
    );
};

export default RegisterForm;