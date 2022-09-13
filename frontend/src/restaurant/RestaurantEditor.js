import React, {useEffect, useState} from 'react';
import restaurantService from '../services/RestaurantService'
import imageService from '../services/ImageService'
import {useTranslation} from "react-i18next";
import {getLocalizedErrorMessages} from "../utils/consts";
import PhoneInput from 'react-phone-input-2';
import 'react-phone-input-2/lib/style.css'
import {Button} from "@material-ui/core";

const RestaurantEditor = (props) => {
        const [name, setName] = useState('');
        const [cuisine, setCuisine] = useState('');
        const [email, setEmail] = useState('');
        const [address, setAddress] = useState('');
        const [phone_number, setPhone_number] = useState('');
        const [website, setWebsite] = useState('');
        const [id, setId] = useState(null);
        const [image, setImage] = useState(null);
        const {t} = useTranslation();
        const [filename, setFilename] = useState(t('not uploaded'));


        const saveRestaurant = (e) => {
            e.preventDefault()

            const restaurant = {name, cuisine, email, address, phone_number, website, filename, id}

            if (id) {
                restaurantService.update(restaurant, id)
                    .then(() => {
                        props.update(restaurant)
                        cleanForm();
                    })
                    .catch(error => {
                        openAlert(getLocalizedErrorMessages(error.response.data.message), "error");
                    })
            } else {
                restaurantService.create(restaurant)
                    .then(response => {
                            props.create(response.data);
                            cleanForm();
                            openAlert([t('record saved')], "success");
                        }
                    )
                    .catch(error => {
                        openAlert(getLocalizedErrorMessages(error.response.data.message), "error");
                    })
            }
        }

        const uploadImage = (image) => {
            imageService.post(image)
                .then(response => {
                    console.log(response.data)
                    setFilename(response.data)
                })
                .catch(error => {
                    openAlert(getLocalizedErrorMessages(error.response.data.message), "error");
                })
        }

        const openAlert = (msg, severity) => {
            props.setAlert({severity: severity, message: msg, open: true})
        }

        const cleanForm = () => {
            setEmail('');
            setName('');
            setCuisine('');
            setAddress('');
            setPhone_number('');
            setWebsite('');
            setImage(null)
            setFilename(t('not uploaded'));
            setId(null);
        };

        useEffect(() => {
            if (!props.modal) {
                cleanForm()
            }
        }, [props.modal]);

        useEffect(() => {
            if (props.restaurantFromDB) {
                setName('' + props.restaurantFromDB.name);
                setCuisine('' + props.restaurantFromDB.cuisine);
                setEmail('' + props.restaurantFromDB.email);
                setAddress('' + props.restaurantFromDB.address);
                setPhone_number('' + props.restaurantFromDB.phone_number);
                setWebsite('' + props.restaurantFromDB.website);
                setFilename('' + props.restaurantFromDB.filename)
                setId('' + props.restaurantFromDB.id);
            }
        }, [props.restaurantFromDB]);

        useEffect(() => {
            if (image) {
                uploadImage(image);
            }
        }, [image])

        return (
            <div className='container'>
                <h4>{t("restaurant editor")}</h4>
                <hr/>
                <form>
                    <div className="form-group">
                        <input
                            type="text"
                            className="form-control col-4"
                            id="name"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            placeholder={t("enter name")}
                        />
                    </div>
                    <div className="form-group" style={{marginTop: 5}}>
                        <input
                            type="text"
                            className="form-control col-4"
                            id="cuisine"
                            value={cuisine}
                            onChange={(e) => setCuisine(e.target.value)}
                            placeholder={t("enter cuisine")}
                        />
                    </div>
                    <div className="form-group" style={{marginTop: 5}}>
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
                            id="address"
                            value={address}
                            onChange={e => setAddress(e.target.value)}
                            placeholder={t("enter address")}
                        />
                    </div>
                    <PhoneInput
                        value={phone_number}
                        onChange={phone => setPhone_number(phone)}
                        style={{marginTop: 5}}
                    />
                    <div className="form-group" style={{marginTop: 5}}>
                        <input
                            type="text"
                            className="form-control col-4"
                            id="website"
                            value={website}
                            onChange={(e) => setWebsite(e.target.value)}
                            placeholder={t("enter website")}
                        />
                    </div>
                    <div className="form-group" style={{marginTop: 5}}>
                        <Button variant="contained" component="label">
                            {t('upload image')}
                            <input hidden accept="image/*" multiple type="file"
                                   onChange={e => setImage(e.target.files[0])}/>
                        </Button>
                        <small>{filename}</small>
                    </div>
                    <div>
                        <button onClick={(e => saveRestaurant(e))} className="btn btn-outline-primary ml-2 btn-sm"
                                style={{marginTop: 10}}>{t("save")}
                        </button>
                    </div>
                </form>
            </div>
        );
    }
;

export default RestaurantEditor;