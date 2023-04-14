import React from "react";
import InputLabel from "@material-ui/core/InputLabel";
import MenuItem from "@material-ui/core/MenuItem";
import FormControl from "@material-ui/core/FormControl";
import Select from "@material-ui/core/Select";
import {useTranslation} from "react-i18next";
const Footer = () => {
    const {t, i18n} = useTranslation();

    const changeLanguage = (language) => {
        i18n.changeLanguage(language);
    }

    return (
        <footer className="text-center text-lg-start bg-light text-muted fixed-bottom">
            <div className="text-center " >
                <div className="container centered" >
                    © {new Date().getFullYear()} Copyright Text
                    <a  target="_blank" href="https://github.com/Alexiusss/travel-expert" style={{padding: "15px"}}>
                         GitHub
                    </a>
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
            </div>
        </footer>
    );
}

export default Footer;