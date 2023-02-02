import React from 'react';
import {useTranslation} from "react-i18next";

const ButtonSection = (props) => {
    const {t} = useTranslation();
    const {save = Function.prototype, close = Function.prototype} = props

    return (
        <div style={{marginTop: 10}}>
            <button onClick={(e => save(e))} className="btn btn-outline-primary ml-2 btn-sm"
            >{t("save")}
            </button>
            <button onClick={close} style={{float: "right"}} className="btn btn-outline-danger ml-2 btn-sm">
                {t("cancel")}
            </button>
        </div>
    );
};

export default ButtonSection;