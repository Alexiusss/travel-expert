import React, {useState, useEffect} from 'react';
import userService from '../services/user.service'
import authService from '../services/AuthService'
import {useTranslation} from "react-i18next";
import {getLocalizedErrorMessages} from "../utils/consts";
import {useAuth} from "../components/hooks/UseAuth";
import {Button} from "@material-ui/core";
import imageService from '../services/ImageService'

const UserEditor = (props) => {
    const [email, setEmail] = useState('');
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [password, setPassword] = useState('');
    const [id, setId] = useState(null)
    const {t} = useTranslation();
    const currentUserId = useAuth().authUserId;
    const [fileNames, setFileNames] = useState([]);
    const [images, setImages] = useState([]);


    const cleanForm = () => {
        setEmail('');
        setFirstName('');
        setLastName('');
        setPassword('');
        setId(null);
        setImages([]);
        setFileNames([]);
    };

    const saveUser = (e) => {
        e.preventDefault()
        const user = {email, firstName, lastName, password, fileName: fileNames[0], id}
        if (id) {
            if (currentUserId === id) {
                authService.updateProfile(user)
                    .then(response => {
                        props.update(response.data)
                        cleanForm();
                        openAlert([t('record saved')], "success")
                    })
                    .catch(error => {
                        console.error(error)
                        openAlert(getLocalizedErrorMessages(error.response.data.message), "error");
                    })
            } else {
                userService.update(user, id)
                    .then(response => {
                        props.update(response.data)
                        cleanForm();
                        openAlert([t('record saved')], "success")
                    })
                    .catch(error => {
                        openAlert(getLocalizedErrorMessages(error.response.data.message), "error");
                    })
            }
        } else {
            userService.create(user)
                .then(response => {
                    props.create(response.data)
                    cleanForm();
                    openAlert([t('record saved')], "success")
                })
                //https://mui.com/material-ui/react-snackbar/
                .catch(error => {
                    openAlert(getLocalizedErrorMessages(error.response.data.message), "error");
                })
        }
    }

    const uploadImages = (images) => {
        imageService.post(images)
            .then(response => {
                setFileNames(response.data)
            })
            .catch(error => {
                openAlert(getLocalizedErrorMessages(error.response.data.message), "error");
            })
    }

    const openAlert = (msg, severity) => {
        props.setAlert({severity: severity, message: msg, open: true})
    }

    useEffect(() => {
        if (!props.modal) {
            cleanForm()
        }
    }, [props.modal])

    useEffect(() => {
        if (props.userFromDB) {
            setEmail('' + props.userFromDB.email)
            setFirstName('' + props.userFromDB.firstName)
            setLastName('' + props.userFromDB.lastName)
            setId('' + props.userFromDB.id)
            setFileNames(props.userFromDB.fileName || [])
        }
    }, [props.userFromDB])

    useEffect(() => {
        if (props.modal && images.length > 0) {
            uploadImages(images);
        }
    }, [images])


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

                <div className="form-group" style={{marginTop: 5}}>
                    <Button variant="contained" component="label">
                        {t('upload images')}
                        <input hidden accept="image/*" multiple type="file"
                               onChange={e => setImages(Array.from(e.target.files))}
                        />
                    </Button>
                    <small>{fileNames.length ? fileNames : t('not uploaded')}</small>
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

export default UserEditor;