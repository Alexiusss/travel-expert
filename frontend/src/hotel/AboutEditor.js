import React from 'react';
import {useTranslation} from "react-i18next";
import ButtonSection from "../components/UI/button/ButtonSection";
import ItemInput from "./ItemInput";

const AboutEditor = (props) => {
    const {t} = useTranslation();
    const {setModal = Function.prototype} = props;
    const {hotel = {}} = props;

    const save = () => {

    }

    return (
        <div className='container' style={{padding: 25}}>
            <h4>{t("hotel editor")}</h4>
            <hr/>
            <ItemInput />

            <ButtonSection save={save} close={() => setModal(false)}/>
        </div>
    );
};

export default AboutEditor;