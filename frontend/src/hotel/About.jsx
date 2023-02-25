import React, {useEffect, useState} from 'react';
import {Card, CardContent, Grid, Typography} from "@material-ui/core";
import FeatureList from "./FeatureList";
import {useTranslation} from "react-i18next";
import {useAuth} from "../components/hooks/UseAuth";
import MyButton from "../components/UI/button/MyButton";
import MyModal from "../components/UI/modal/MyModal";
import AmenitiesList from "./AmenitiesList";
import TextSection from "./TextSection";
import translateService from "../services/TranslateService"

const About = ({item = {}, setModal = Function.prototype}) => {

    const {t, i18n} = useTranslation(['translation', 'hotel']);
    const services = item.servicesAndFacilitates || [];
    const features = item.roomFeatures || [];
    const types = item.roomTypes || [];
    const styles = item.hotelStyle || [];
    const languages = item.languagesUsed || [];
    const description = item.description || '';
    const [detectedLanguage, setDetectedLanguage] = useState('')
    const [translatedDescription, setTranslatedDescription] = useState('');
    const {isAdmin} = useAuth();
    const [amenitiesModal, setAmenitiesModal] = useState(false);

    useEffect(() => {
        const currentLanguage = i18n.language;
        if (item.description.length > 0 && detectedLanguage !== currentLanguage) {
            translateService.translate(item.description, 'eng', 'rus')
                .then(({data}) => {
                    const detectedLanguage = data.languageDetection.detectedLanguage.toString().slice(0, 2);
                    setDetectedLanguage(detectedLanguage)
                    if (detectedLanguage !== currentLanguage) {
                        setTranslatedDescription(data.translation)
                    }
                })
        } else {
            setTranslatedDescription(description);
        }
    }, [i18n.language])

    return (
        <Card>
            <CardContent>
                <div style={{display: "flex", justifyContent: 'space-between'}}>
                    <Typography variant="h5" component="div">
                        {/*https://github.com/i18next/react-i18next-gitbook/blob/master/latest/usetranslation-hook.md*/}
                        <h5>{t('about', {ns: 'hotel'})}</h5>
                    </Typography>
                    {
                        isAdmin ?
                            <div>
                                <MyButton className={"btn btn-outline-primary ml-2 btn-sm"}
                                          onClick={() => setModal(true)}
                                >
                                    {t("edit")}
                                </MyButton>
                            </div> : null
                    }
                </div>
                <hr/>
                {
                    description.length > 0 ?
                        <TextSection
                            text={translatedDescription.length > 0 ? translatedDescription : description}/> : null
                }
                {
                    services.length > 0 ?
                        <FeatureList name={t('services and facilitates', {ns: 'hotel'})} items={services.slice(0, 4)}
                                     showMore={services.length > 4} setModal={setAmenitiesModal}/> : null
                }
                {
                    features.length > 0 ?
                        <FeatureList name={t('room features', {ns: 'hotel'})} items={features.slice(0, 4)}
                                     showMore={features.length > 4} setModal={setAmenitiesModal}/> : null
                }
                {
                    types.length > 0 ? <FeatureList name={t('room types', {ns: 'hotel'})} items={types.slice(0, 4)}
                                                    showMore={types.length > 4} setModal={setAmenitiesModal}/> : null
                }
                {
                    styles.length > 0 ?
                        <FeatureList style={{padding: 0}} name={t('hotel style', {ns: 'hotel'})} items={styles}/> : null
                }
                {
                    languages.length > 0 ?
                        <FeatureList name={t('languages used', {ns: 'hotel'})} items={languages}/> : null
                }
            </CardContent>
            {amenitiesModal ?
                <MyModal visible={amenitiesModal} setVisible={() => setAmenitiesModal(true)}>
                    <AmenitiesList services={services} features={features} types={types}
                                   setAmenitiesModal={setAmenitiesModal}/>
                </MyModal> : null
            }
        </Card>
    );
};

export default About;