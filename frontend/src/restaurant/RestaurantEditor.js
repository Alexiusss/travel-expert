import React, {useState} from 'react';
import restaurantService from '../services/RestaurantService'
import {useTranslation} from "react-i18next";
import {getLocalizedErrorMessages} from "../utils/consts";

const RestaurantEditor = (props) => {
    const [name, setName] = useState('');
    const [cuisine, setCuisine] = useState('');
    const [email, setEmail] = useState('');
    const [address, setAddress] = useState('');
    const [phone_number, setPhone_number] = useState('');
    const [website, setWebsite] = useState('');
    const [id, setId] = useState(null)
    const {t} = useTranslation();

    const cleanForm = () => {
        setEmail('');
        setName('');
        setCuisine('');
        setAddress('');
        setPhone_number('');
        setWebsite('');
        setId(null);
    };

    const saveRestaurant = (e) => {
        e.preventDefault()
        const restaurant = {name, cuisine, email, address, phone_number, website, id}
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

    const openAlert = (msg, severity) => {
        props.setAlert({severity: severity, message: msg, open: true})
    }

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
                        onChange={(e) => setAddress(e.target.value)}
                        placeholder={t("enter address")}
                    />
                </div>
                <div className="form-group" style={{marginTop: 5}}>
                    <input
                        type="text"
                        className="form-control col-4"
                        id="phone_number"
                        value={phone_number}
                        onChange={(e) => setPhone_number(e.target.value)}
                        placeholder={t("enter phone number")}
                    />
                </div>
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
                <div>
                    <button onClick={(e => saveRestaurant(e))} className="btn btn-outline-primary ml-2 btn-sm"
                            style={{marginTop: 10}}>{t("save")}
                    </button>
                </div>
            </form>
        </div>
    );
};

export default RestaurantEditor;