import React, {useState} from 'react';
import {useTranslation} from "react-i18next";
import ButtonSection from "../components/UI/button/ButtonSection";
import ItemInput from "./ItemInput";

const AboutEditor = (props) => {
    const {t} = useTranslation();
    const {setModal = Function.prototype, save = Function.prototype} = props;
    const {hotel = {}} = props;
    const [services, setServices] = useState(hotel.servicesAndFacilitates || []);
    const [roomFeatures, setRoomFeatures] = useState(hotel.roomFeatures || []);
    const [roomTypes, setRoomTypes] = useState(hotel.roomTypes || []);
    const [hotelStyle, setHotelStyle] = useState(hotel.hotelStyle || []);
    const [languagesUsed, setLanguagesUsed] = useState(hotel.languagesUsed || []);

    const prepareAndSave = () => {
        const updatedHotel = {...hotel, services, roomFeatures, roomTypes, hotelStyle, languagesUsed}
        save(updatedHotel);
    }

    return (
        <div className='container' style={{padding: 25}}>
            <h4>{t("hotel editor")}</h4>
            <hr/>
            <br/>
            <ItemInput items={services} setItems={setServices} name={t('services and facilitates')}/>
            <br/>
            <ItemInput items={roomFeatures} setItems={setRoomFeatures} name={t('room features')}/>
            <br/>
            <ItemInput items={roomTypes} setItems={setRoomTypes} name={t('room types')}/>
            <br/>
            <ItemInput items={hotelStyle} setItems={setHotelStyle} name={t('hotel style')}/>
            <br/>
            <ItemInput items={languagesUsed} setItems={setLanguagesUsed} name={t('languages used')}/>
            <br/>
            <ButtonSection save={prepareAndSave} close={() => setModal(false)} name={t('services and facilitates')}/>
        </div>
    );
};

export default AboutEditor;