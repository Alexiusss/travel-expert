import React, {useState} from 'react';
import {Box, Tab, Tabs} from "@material-ui/core";
import FeatureList from "./FeatureList";
import {useTranslation} from "react-i18next";

const AmenitiesList = (props) => {
    const {services, features, types, setAmenitiesModal = Function.prototype} = props;
    const [tabIndex, setTabIndex] = useState(0);
    const {t} = useTranslation(['translation', 'hotel']);

    const close = () => {
        setAmenitiesModal(false);
    }

    return (
        <Box>
            <Box>
                <Tabs value={tabIndex} textColor={'primary'}>
                    <Tab label={t('services and facilitates', {ns: 'hotel'})} onClick={() => setTabIndex(0)}
                         style={{fontSize: 11}}/>
                    <Tab label={t('room features', {ns: 'hotel'})} onClick={() => setTabIndex(1)}
                         style={{fontSize: 11}}/>
                    <Tab label={t('room types', {ns: 'hotel'})} onClick={() => setTabIndex(2)} style={{fontSize: 11}}/>
                    <div className="sub-header">
                        <span id="close" onClick={close}>x</span>
                    </div>
                </Tabs>

            </Box>
            <Box sx={{padding: 2}}>
                {tabIndex === 0 && (
                    <FeatureList items={services}/>
                )}
                {tabIndex === 1 && (
                    <FeatureList items={features}/>
                )}
                {tabIndex === 2 && (
                    <FeatureList items={types}/>
                )}
            </Box>
        </Box>
    );
}

export default AmenitiesList;