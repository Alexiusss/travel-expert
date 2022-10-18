import React from 'react';
import {useDispatch} from 'react-redux'
import {PROFILE, RESTAURANTS_ROUTE, REVIEWS_ROUTE, USERS_ROUTE} from "../utils/consts";
import {Link} from "react-router-dom";
import {useTranslation} from "react-i18next";
import InputLabel from "@material-ui/core/InputLabel";
import MenuItem from "@material-ui/core/MenuItem";
import FormControl from "@material-ui/core/FormControl";
import Select from "@material-ui/core/Select";
import {useAuth} from "./hooks/UseAuth";
import MyButton from "./UI/button/MyButton";
import authService from "services/AuthService"
import {removeUser} from "../store/slices/userSlice";

const NavBar = () => {
    const {t, i18n} = useTranslation();
    const changeLanguage = (language) => {
        i18n.changeLanguage(language);
    }
    const {isAuth, isAdmin, isModerator, authUserId} = useAuth();
    const dispatch = useDispatch();

    const logout = () => {
        authService.logout()
            .then(
                () => dispatch(removeUser())
            )
            .catch(error =>
                console.error(error))
    }
    return (
        <div>
            <nav className="navbar navbar-expand-lg navbar-light bg-light px-3">
                < Link to="/" className="navbar-brand">
                    Restaurant advisor
                </Link>
                <div className="navbar-nav mr-auto">
                    <li className="nav-item">
                        <Link to={RESTAURANTS_ROUTE} className="nav-link">
                        {t("restaurants")}
                    </Link>
                    </li>
                </div>
                <div className="navbar-nav mr-auto">
                    {isAdmin || isModerator ?
                        <li className="nav-item">
                            <Link to={USERS_ROUTE} className="nav-link">
                                {t("users")}
                            </Link>
                        </li>
                        :
                        ""
                    }
                </div>
                <div className="navbar-nav mr-auto">
                    {isAdmin || isModerator ?
                        <li className="nav-item">
                            <Link to={REVIEWS_ROUTE} className="nav-link">
                                {t("reviews")}
                            </Link>
                        </li>
                        :
                        ""
                    }
                </div>
                <div className="navbar-nav mr-auto">
                    {isAuth?
                        <li className="nav-item">
                            <Link  className="nav-link"
                                to={{
                                pathname: PROFILE,
                                state: {authorId: authUserId}
                            }}>
                                {t("profile")}
                            </Link>
                        </li>
                        :
                        ""
                    }
                </div>
                <div className="navbar-nav ms-auto">
                    <FormControl sx={{m: 1, minWidth: 80}}>
                        <InputLabel>{t("lang")}</InputLabel>
                        <Select
                            defaultValue={"en"}
                            autoWidth
                        >
                            <MenuItem value={"en"} onClick={() => changeLanguage("en")}>English</MenuItem>
                            <MenuItem value={"ru"} onClick={() => changeLanguage("ru")}>Русский</MenuItem>
                        </Select>
                    </FormControl>
                    <div className="navbar-nav mr-auto">
                        <li className="nav-item">
                            {isAuth ?
                                <MyButton style={{marginTop: 10}} className={"btn btn-outline-primary ml-2 btn-sm"}
                                          onClick={() => logout()}>
                                    Logout
                                </MyButton>
                                :
                                < Link to="/login" className="nav-link">
                                    Login
                                </Link>
                            }
                        </li>
                    </div>
                </div>
            </nav>
        </div>
    );
};

export default NavBar;