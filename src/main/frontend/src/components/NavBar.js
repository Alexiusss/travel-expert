import React from 'react';
import {USERS_ROUTE} from "../utils/consts";
import {Link} from "react-router-dom";
import { useTranslation } from "react-i18next";
import InputLabel from "@material-ui/core/InputLabel";
import MenuItem from "@material-ui/core/MenuItem";
import FormControl from "@material-ui/core/FormControl";
import Select from "@material-ui/core/Select";

const NavBar = () => {
    const {t, i18n} = useTranslation();
    const changeLanguage = (language) => {
        i18n.changeLanguage(language);
    }
    return (
        <div>
            <nav className="navbar navbar-expand-lg navbar-light bg-light">
                <a href="/" className="navbar-brand">
                    Restaurant advisor
                </a>
                <div className="navbar-nav mr-auto">
                    <li className="nav-item">
                        <Link to={USERS_ROUTE} className="nav-link">
                            {t("users")}
                        </Link>
                    </li>
                </div>
                <div className="navbar-nav ms-auto">
                    <FormControl sx={{ m: 1, minWidth: 80}}>
                        <InputLabel>{t("lang")}</InputLabel>
                        <Select
                            defaultValue={"en"}
                            autoWidth
                        >
                            <MenuItem value={"en"} onClick={() => changeLanguage("en")}>English</MenuItem>
                            <MenuItem value={"ru"} onClick={() => changeLanguage("ru")}>Русский</MenuItem>
                        </Select>
                    </FormControl>
                </div>
            </nav>
        </div>
    );
};

export default NavBar;