import React, {useState} from 'react';
import {useTranslation} from "react-i18next";
import ButtonSection from "../components/UI/button/ButtonSection";
import ItemInput from "./ItemInput";
import {
    HOTEL_STYLES_LIST,
    LANGUAGES_LIST,
    ROOM_FEATURES_LIST,
    ROOM_TYPES_LIST,
    SERVICES_AND_FACILITATES_LIST
} from "./resources/Data";

const AboutEditor = (props) => {
    const {t} = useTranslation(['translation', 'hotel']);
    const {setModal = Function.prototype, save = Function.prototype} = props;
    const {hotel = {}} = props;
    const [servicesAndFacilitates, setServicesAndFacilitates] = useState(hotel.servicesAndFacilitates || []);
    const [roomFeatures, setRoomFeatures] = useState(hotel.roomFeatures || []);
    const [roomTypes, setRoomTypes] = useState(hotel.roomTypes || []);
    const [hotelStyle, setHotelStyle] = useState(hotel.hotelStyle || []);
    const [languagesUsed, setLanguagesUsed] = useState(hotel.languagesUsed || []);

    const prepareAndSave = () => {
        const updatedHotel = {...hotel, servicesAndFacilitates, roomFeatures, roomTypes, hotelStyle, languagesUsed}
        save(updatedHotel);
    }

    return (
        <div className='container' style={{padding: 25}}>
            <h4>{t("hotel editor", {ns: 'hotel'})}</h4>
            <hr/>
            <br/>
            {/*https://www.i18next.com/translation-function/objects-and-arrays#arrays*/}
            <ItemInput
                items={servicesAndFacilitates} setItems={setServicesAndFacilitates}
                name={t('services and facilitates', {ns: 'hotel'})}
                options={SERVICES_AND_FACILITATES_LIST}
            />
            <br/>
            <ItemInput
                items={roomFeatures} setItems={setRoomFeatures}
                name={t('room features', {ns: 'hotel'})}
                options={ROOM_FEATURES_LIST}
            />
            <br/>
            <ItemInput
                items={roomTypes} setItems={setRoomTypes}
                name={t('room types', {ns: 'hotel'})}
                options={ROOM_TYPES_LIST}
            />
            <br/>
            <ItemInput
                items={hotelStyle} setItems={setHotelStyle}
                name={t('hotel style', {ns: 'hotel'})}
                options={HOTEL_STYLES_LIST}
            />
            <br/>
            <ItemInput
                items={languagesUsed} setItems={setLanguagesUsed}
                name={t('languages used', {ns: 'hotel'})}
                options={LANGUAGES_LIST}
            />
            <br/>
            <ButtonSection save={prepareAndSave} close={() => setModal(false)}
                           name={t('services and facilitates', {ns: 'hotel'})}/>
        </div>
    );
};

export default AboutEditor;