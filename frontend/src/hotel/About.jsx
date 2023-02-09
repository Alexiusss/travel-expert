import React from 'react';
import {Card, CardContent, Grid, Typography} from "@material-ui/core";
import FeatureList from "./FeatureList";
import {useTranslation} from "react-i18next";
import {useAuth} from "../components/hooks/UseAuth";
import MyButton from "../components/UI/button/MyButton";

const About = ({item = {}, setModal = Function.prototype}) => {
    const services = item.servicesAndFacilitates || [];
    const features = item.roomFeatures || [];
    const types = item.roomTypes || [];
    const styles = item.hotelStyle || [];
    const languages = item.languagesUsed || [];
    const {t} = useTranslation();
    const {isAdmin} = useAuth();

    return (
        <Card>
            <CardContent>
                <div style={{display: "flex", justifyContent: 'space-between'}}>
                    <Typography variant="h5" component="div">
                        <h5>{t('about')}</h5>
                    </Typography>
                    {
                        isAdmin ?
                            <div>
                                <MyButton  className={"btn btn-outline-primary ml-2 btn-sm"}
                                      onClick={() => setModal(true)}
                                >
                                    {t("edit")}
                                </MyButton>
                            </div> : null
                    }
                </div>
                <hr/>
                {
                    services.length > 0 ? <FeatureList name={t('services and facilitates')} items={services}/> : null
                }
                {
                    features.length > 0 ? <FeatureList name={t('room features')} items={features}/> : null
                }
                {
                    types.length > 0 ? <FeatureList name={t('room types')} items={types}/> : null
                }
                {
                    styles.length > 0 ? <FeatureList name={t('hotel style')} items={styles}/> : null
                }
                {
                    languages.length > 0 ? <FeatureList name={t('languages used')} items={languages}/> : null
                }
            </CardContent>
        </Card>
    );
};

export default About;