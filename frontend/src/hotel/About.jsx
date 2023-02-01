import React from 'react';
import {Card, CardContent, Grid, Typography} from "@material-ui/core";
import FeatureList from "./FeatureList";
import {useTranslation} from "react-i18next";

const About = ({item} = {}) => {
    const services = item.servicesAndFacilitates || [];
    const features = item.roomFeatures || [];
    const types = item.roomTypes || [];
    const styles = item.hotelStyle || [];
    const languages = item.languagesUsed || [];
    const {t} = useTranslation();

    return (
        <Card>
            <CardContent>
                <Typography variant="h5" component="div">
                    <h5>{t('about')}</h5>
                </Typography>
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