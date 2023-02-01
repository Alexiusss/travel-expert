import React from 'react';
import {Card, CardContent, Typography} from "@material-ui/core";
import {useTranslation} from "react-i18next";

const ItemContact = ({item}) => {
    const {t} = useTranslation();
    return (
            <Card>
                <CardContent>
                    <Typography variant="h5" component="div">
                        <h5>{t('location and contact')}</h5>
                    </Typography>
                    <div>{t('address')}: {item.address}</div>
                    <div>{t('email')}: {item.email}</div>
                    <div>{t('phone number')} {item.phoneNumber}</div>
                    <div>{t('website')}: {item.website}</div>
                </CardContent>
            </Card>
    );
};

export default ItemContact;