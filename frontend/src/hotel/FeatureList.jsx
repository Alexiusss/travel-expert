import React from 'react';
import {useTranslation} from "react-i18next";

const FeatureList = ({name = "", items = []}) => {

    const {t} = useTranslation(['translation', 'hotel']);

    return (
        <div>
            <h6>{name}</h6>
            <ul>
                <div className={'container'} style={{columns: '2 auto'}}>
                {items.map((item) =>
                    <li key={item + 'key'}>{t(item, {ns: 'hotel'})}</li>
                )}
                </div>
            </ul>
        </div>
    );
};

export default FeatureList;