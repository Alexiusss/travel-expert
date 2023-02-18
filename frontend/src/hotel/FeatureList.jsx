import React from 'react';
import {useTranslation} from "react-i18next";

const FeatureList = ({name = "", items = [], showMore = false, setModal = false}) => {

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
            {showMore ?
                <div
                    style={{
                        cursor: "pointer",
                        marginTop: -15,
                        marginBottom: 15,
                        fontSize: 14,
                        fontWeight: 600
                    }}
                    onClick={() => setModal(true)}
                >Show more</div> : null
            }
        </div>
    );
};

export default FeatureList;