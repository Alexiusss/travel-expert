import React, {useEffect, useState} from 'react';
import hotelService from "../services/HotelService";
import {useTranslation} from "react-i18next";
import {getLocalizedErrorMessages} from "../utils/consts";
import PhoneInput from 'react-phone-input-2';
import 'react-phone-input-2/lib/style.css'
import ButtonSection from "../components/UI/button/ButtonSection";

const HotelEditor = (props) => {
    const {t} = useTranslation();
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [address, setAddress] = useState('');
    const [phoneNumber, setPhoneNumber] = useState('');
    const [website, setWebsite] = useState('');
    const [description, setDescription] = useState('');
    const [hotelClass, setHotelClass] = useState('');
    const [id, setId] = useState(null);


    const saveRestaurant = (e) => {
        e.preventDefault()
        const hotel = {name, email, address, phoneNumber, website, description, hotelClass, id}
        hotelService.create(hotel)
            .then(response => {
                props.create(response.data);
                cleanForm()
                openAlert([t('record saved')], "success");
            })
            .catch(error => {
                openAlert(getLocalizedErrorMessages(error.response.data.message), "error");
            })

    }

    const openAlert = (msg, severity) => {
        props.setAlert({severity: severity, message: msg, open: true})
    }
    const close = (e) => {
        e.preventDefault();
        props.setModal(false)
    }

    const cleanForm = () => {
        setName('');
        setEmail('');
        setAddress('');
        setPhoneNumber('');
        setWebsite('');
        setDescription('');
        setHotelClass('')
        setId(null);
    };

    useEffect(() => {
        if (!props.modal) {
            cleanForm()
        }
    }, [props.modal]);

    return (
        <div className='container' style={{padding: 25}}>
            <h4>{t("hotel editor")}</h4>
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
                    value={phoneNumber}
                    onChange={phone => setPhoneNumber(phone)}
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
                    <textarea
                        className="form-control col-4"
                        id="description"
                        value={description}
                        onChange={e => setDescription(e.target.value)}
                        placeholder={t("enter description")}
                    />
                </div>
                <div className="form-group" style={{marginTop: 5}}>
                    <label> Select hotel class:
                        <select
                            style={{marginLeft: 10, marginRight: 10}}
                            value={hotelClass}
                            onChange={(e) => setHotelClass(e.target.value)}
                        >
                            <option disabled/>
                            <option value="1">1</option>
                            <option value="2">2</option>
                            <option value="3">3</option>
                            <option value="4">4</option>
                            <option value="5">5</option>
                        </select>
                        star(s)
                    </label>
                </div>
                <ButtonSection
                    save={saveRestaurant}
                    close={close}
                />
            </form>
        </div>
    );
};

export default HotelEditor;