import React, {useState, useRef, useEffect} from 'react';
import userService from '../services/user.service'
import { useTranslation } from "react-i18next";

const AddUser = (props) => {
    const [email, setEmail] = useState('');
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [password, setPassword] = useState('');
    const [id, setId] = useState('')
    const {t} = useTranslation();

    const cleanForm = () => {
        setEmail('');
        setFirstName('');
        setLastName('');
        setPassword('');
        setId('');
    };

    const saveUser = (e) => {
        e.preventDefault()
        const user = {email, firstName, lastName, password, id}
        if (id) {
            userService.update(user, id)
                .then(response => {
                    props.update(response.data)
                    cleanForm()
                    openAlert(t('record saved'), "success")
                })
                .catch(error => {
                    openAlert(error.response.data.message, "error");
                })

        } else {
            userService.create(user)
                .then(response => {
                    console.table(response.data)
                    props.create(response.data)
                    cleanForm()
                    openAlert(t('record saved'), "success")
                })
                //https://mui.com/material-ui/react-snackbar/
                .catch(error => {
                    openAlert(error.response.data.message, "error");
                })
        }
    }

    const openAlert = (msg, severity) => {
        props.severity(severity);
        props.message(msg);
        props.open(true);
    }

    useEffect(() => {
        if(!props.modal) {
            cleanForm()
        }
    })

    useEffect(() => {
        if (props.userFromDB) {
            setEmail('' + props.userFromDB.email)
            setFirstName('' + props.userFromDB.firstName)
            setLastName('' + props.userFromDB.lastName)
            setId('' + props.userFromDB.id)
        }
    }, [props.userFromDB])


    return (
        <div className='container'>
            <h4>{t("user editor")}</h4>
            <hr/>
            <form>
                <div className="form-group">
                    <input
                        type="email"
                        className="form-control col-4"
                        id="email"
                        value={email}
                        onChange={e => setEmail(e.target.value)}
                        placeholder={t("enter email")}
                    />
                </div>

                <div className="form-group" style={{marginTop: 5}}>
                    <input
                        type="text"
                        className="form-control col-4"
                        id="firstName"
                        value={firstName}
                        onChange={(e) => setFirstName(e.target.value)}
                        placeholder={t("enter first name")}
                    />
                </div>

                <div className="form-group" style={{marginTop: 5}}>
                    <input
                        type="text"
                        className="form-control col-4"
                        id="lastName"
                        value={lastName}
                        onChange={(e) => setLastName(e.target.value)}
                        placeholder={t("enter last name")}
                    />
                </div>

                <div className="form-group" style={{marginTop: 5}}>
                    <input
                        type="password"
                        className="form-control col-4"
                        id="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder={t("enter password")}
                    />
                </div>
                <div>
                    <button onClick={(e => saveUser(e))} className="btn btn-outline-primary ml-2 btn-sm"
                            style={{marginTop: 10}}>{t("save")}
                    </button>
                </div>
            </form>
        </div>
    );
};

export default AddUser;