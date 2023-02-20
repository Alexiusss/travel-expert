import React, {useEffect, useState} from 'react';
import hotelService from "../services/HotelService";
import {useTranslation} from "react-i18next";
import {getLocalizedErrorMessages} from "../utils/consts";
import PhoneInput from 'react-phone-input-2';
import 'react-phone-input-2/lib/style.css'
import ButtonSection from "../components/UI/button/ButtonSection";
import {Button} from "@material-ui/core";
import imageService from "../services/ImageService";

const HotelEditor = (props) => {
    const {t} = useTranslation(['translation', 'hotel']);
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [address, setAddress] = useState('');
    const [phoneNumber, setPhoneNumber] = useState('');
    const [website, setWebsite] = useState('');
    const [description, setDescription] = useState('');
    const [hotelClass, setHotelClass] = useState(0);
    const [id, setId] = useState(null);
    const [images, setImages] = useState([]);
    const [fileNames, setFileNames] = useState([]);
    const [servicesAndFacilitates, setServicesAndFacilitates] = useState([]);
    const [roomTypes, setRoomTypes] = useState([]);
    const [roomFeatures, setRoomFeatures] = useState([]);
    const [hotelStyle, setHotelStyle] = useState([]);
    const [languagesUsed, setLanguagesUsed] = useState([]);

    const saveHotel = (e) => {
        e.preventDefault()

        const hotel = {
            name,
            email,
            address,
            phoneNumber,
            website,
            description,
            hotelClass,
            fileNames,
            servicesAndFacilitates,
            roomTypes,
            roomFeatures,
            hotelStyle,
            languagesUsed,
            id
        }
        if (id) {
            hotelService.update(hotel, id)
                .then(() => {
                    props.update(hotel);
                    cleanForm();
                })
                .catch(error => {
                    openAlert(getLocalizedErrorMessages(error.response.data.message), "error");
                })
        } else {
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

    }

    const uploadImages = (images) => {
        imageService.post(images)
            .then(response => {
                setFileNames(prevFileNames => prevFileNames.concat(response.data))
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
        setHotelClass(0)
        setImages([]);
        setFileNames([]);
        setServicesAndFacilitates([]);
        setRoomTypes([]);
        setRoomFeatures([]);
        setHotelStyle([]);
        setLanguagesUsed([]);
        setId(null);
    };

    useEffect(() => {
        if (props.hotelFromDB.id) {
            setName('' + props.hotelFromDB.name);
            setEmail('' + props.hotelFromDB.email);
            setAddress('' + props.hotelFromDB.address);
            setPhoneNumber('' + props.hotelFromDB.phoneNumber);
            setWebsite('' + props.hotelFromDB.website);
            setFileNames(props.hotelFromDB.fileNames || [])
            setDescription(props.hotelFromDB.description || '')
            setHotelClass(props.hotelFromDB.hotelClass || 0)
            setServicesAndFacilitates(props.hotelFromDB.servicesAndFacilitates || [])
            setRoomTypes(props.hotelFromDB.roomTypes || [])
            setRoomFeatures(props.hotelFromDB.roomFeatures || [])
            setLanguagesUsed(props.hotelFromDB.languagesUsed || [])
            setId('' + props.hotelFromDB.id);
        }
    }, [props.hotelFromDB]);

    useEffect(() => {
        if (!props.modal) {
            cleanForm()
        }
    }, [props.modal]);

    useEffect(() => {
        if (props.modal && images.length > 0) {
            uploadImages(images);
        }
    }, [images])

    return (
        <div className='container' style={{padding: 25}}>
            <h4>{t("hotel editor", {ns: 'hotel'})}</h4>
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
                    <label>  {t("select hotel class", {ns: 'hotel'})}
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
                        {t("stars")}
                    </label>
                </div>
                <div className="form-group" style={{marginTop: 5}}>
                    <Button variant="contained" component="label">
                        {t('upload image')}
                        <input hidden accept="image/*" multiple type="file"
                               onChange={e => setImages(Array.from(e.target.files))}/>
                    </Button>
                    <small>{fileNames.length ? fileNames : t('not uploaded')}</small>
                </div>
                <ButtonSection
                    save={saveHotel}
                    close={close}
                />
            </form>
        </div>
    );
};

export default HotelEditor;