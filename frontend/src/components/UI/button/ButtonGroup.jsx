import React from 'react';
import {Link} from "react-router-dom";
import MyButton from "./MyButton";
import {useTranslation} from "react-i18next";

const ButtonGroup = (props) => {
    const {t} = useTranslation();
    const {
        signInAsAdmin = Function.prototype,
        signInAsUser = Function.prototype
    } = props;
    return (
        <div className="container">
            <Link to="/register">
                <MyButton className={"btn btn-outline-success ml-2 btn-sm"}>
                    {t("sign up")} >>
                </MyButton>
            </Link>
            {' '}
            <MyButton className={"btn btn-outline-primary ml-2 btn-sm"}
                      onClick={(e) => signInAsAdmin(e)}
            >
                {t("sign in")} {t('as')} Admin
            </MyButton>
            {' '}
            <MyButton className={"btn btn-outline-primary ml-2 btn-sm"}
                      onClick={(e) => signInAsUser(e)}
            >
                {t("sign in")} {t('as')} User
            </MyButton>
        </div>
    );
};

export default ButtonGroup;