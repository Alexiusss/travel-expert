import React from 'react';
import MyButton from "./MyButton";
import {useTranslation} from "react-i18next";
import {useHistory} from "react-router-dom";

const ButtonGroup = (props) => {
    const {t} = useTranslation();
    const {push} = useHistory();
    const {
        signInAsAdmin = Function.prototype,
        signInAsUser = Function.prototype
    } = props;
    return (
        <div className="lead py-1">
            {/*https://stackoverflow.com/a/67375149*/}
            <div className="d-grid gap-3 d-md-flex">
                <MyButton className={"btn btn-outline-success w-100"}
                          onClick={() => push("/register")}
                >
                    {t("sign up")} >>
                </MyButton>
                {' '}
                <MyButton className={"btn btn-outline-primary w-100"}
                          onClick={(e) => signInAsAdmin(e)}
                >
                    {t("sign in")} {t('as')} Admin
                </MyButton>
                {' '}
                <MyButton className={"btn btn-outline-primary w-100"}
                          onClick={(e) => signInAsUser(e)}
                >
                    {t("sign in")} {t('as')} User
                </MyButton>
            </div>
        </div>
    );
};

export default ButtonGroup;