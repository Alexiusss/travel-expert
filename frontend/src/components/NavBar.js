import React, {useEffect, useState} from 'react';
import {useDispatch} from 'react-redux'
import {HOTELS_ROUTE, PROFILE, RESTAURANTS_ROUTE, REVIEWS_ROUTE, USERS_ROUTE} from "../utils/consts";
import {Link, useLocation} from "react-router-dom";
import {useTranslation} from "react-i18next";
import {useAuth} from "./hooks/UseAuth";
import MyButton from "./UI/button/MyButton";
import {removeUser, setUser} from "../store/slices/userSlice";
import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import keycloakAuthService from '../services/kc/KeycloakAuthService';
import {useKeycloak} from "@react-keycloak/web";

const NavBar = () => {
    const {t} = useTranslation(['translation', 'hotel']);
    const {isAuth, isAdmin, isModerator, authUserId} = useAuth();
    const dispatch = useDispatch();
    const location = useLocation();
    const {keycloak} = useKeycloak();
    // https://stackoverflow.com/a/58530447
    const [expanded, setExpanded] = useState(false);
    const [isAuthCodeReceived, setIsAuthCodeReceived] = useState(false);

    const logout = () => {
        keycloakAuthService.logout()
            .then(
                () => {
                    dispatch(removeUser());
                    setExpanded(false);
                    setIsAuthCodeReceived(false)
                    //history.push("/");
                }
            )
            .catch(error =>
                console.error(error))
    }

    useEffect(() => {
        const authCode = location.hash.split('code=')[1]
        if (!isAuthCodeReceived && authCode) {
            keycloakAuthService.exchangeCodeForToken(authCode)
                .then(() => {
                    setIsAuthCodeReceived(true)
                    keycloakAuthService.profile()
                        .then(({data}) => {
                            dispatch(setUser({
                                email: data.email,
                                id: data.id,
                                token: "",
                                authorities: data.roles,
                            }));
                        })
                })
                .catch(() => console.log('Token exchange failed'));
        }
    }, [keycloak])

    return (
        <Navbar bg="light" expand="lg" className="fixed-top px-3" expanded={expanded}>
            <Container>
                <Link to="/" className="navbar-brand">
                    Travel expert
                </Link>
                <Navbar.Toggle aria-controls="navbar-nav" onClick={() => setExpanded(!expanded)}/>
                <Navbar.Collapse id="navbar-nav">
                    <Nav className="container-fluid">
                        <Nav.Item>
                            <Link to={RESTAURANTS_ROUTE} className="nav-link" onClick={() => setTimeout(() => {
                                setExpanded(false)
                            }, 150)}>
                                {t("restaurants")}
                            </Link>
                        </Nav.Item>
                        <Nav.Item>
                            <Link to={HOTELS_ROUTE} className="nav-link" onClick={() => setExpanded(false)}>
                                {t("hotels", {ns: 'hotel'})}
                            </Link>
                        </Nav.Item>
                        {isAdmin || isModerator ?
                            <>
                                <Nav.Item>
                                    <Link to={USERS_ROUTE} className="nav-link" onClick={() => setExpanded(false)}>
                                        {t("users")}
                                    </Link>
                                </Nav.Item>
                                <Nav.Item>
                                    <Link to={REVIEWS_ROUTE} className="nav-link" onClick={() => setExpanded(false)}>
                                        {t("reviews")}
                                    </Link>
                                </Nav.Item>
                            </>
                            :
                            null
                        }
                        {isAuth
                            ?
                            <Nav.Item>
                                <Link className="nav-link"
                                      onClick={() => setExpanded(false)}
                                      to={{
                                          pathname: PROFILE,
                                          state: {authorId: authUserId}
                                      }}>
                                    {t("profile")}
                                </Link>
                            </Nav.Item>
                            :
                            null
                        }
                        <Nav.Item className="ms-auto centered">
                            {isAuth ?
                                <MyButton className={"btn btn-outline-primary ml-2 btn-sm"}
                                          onClick={() => logout()}>
                                    Logout
                                </MyButton>
                                :
                                // <Link to="/login" className="nav-link" onClick={() => setExpanded(false)}>
                                //     {t("sign in")}
                                // </Link>
                                <MyButton className={"btn btn-outline-primary ml-2 btn-sm"}
                                          onClick={() => keycloak.login()}>
                                    {t("sign in")}
                                </MyButton>
                            }
                        </Nav.Item>
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
};

export default NavBar;