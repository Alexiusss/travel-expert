import React from 'react';
import {useDispatch} from 'react-redux'
import {PROFILE, RESTAURANTS_ROUTE, REVIEWS_ROUTE, USERS_ROUTE} from "../utils/consts";
import {Link} from "react-router-dom";
import {useTranslation} from "react-i18next";
import {useAuth} from "./hooks/UseAuth";
import MyButton from "./UI/button/MyButton";
import authService from "services/AuthService"
import {removeUser} from "../store/slices/userSlice";
import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';

const NavBar = () => {
    const {t} = useTranslation();
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
        <Navbar bg="light" expand="lg" className="px-3">
            <Container>
                <Link to="/" className="navbar-brand">
                    Restaurant advisor
                </Link>
                <Navbar.Toggle aria-controls="navbar-nav" />
                <Navbar.Collapse id="navbar-nav">
                    <Nav className="container-fluid">
                        <Nav.Item>
                            <Link to={RESTAURANTS_ROUTE} className="nav-link">
                                {t("restaurants")}
                            </Link>
                        </Nav.Item>
                        {isAdmin || isModerator ?
                            <>
                                <Nav.Item>
                                    <Link to={USERS_ROUTE} className="nav-link">
                                        {t("users")}
                                    </Link>
                                </Nav.Item>
                                <Nav.Item>
                                    <Link to={REVIEWS_ROUTE} className="nav-link">
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
                        <Nav.Item className="ms-auto  ">
                            {isAuth ?
                                <MyButton style={{marginTop: 10}} className={" btn btn-outline-primary ml-2 btn-sm"}
                                          onClick={() => logout()}>
                                    Logout
                                </MyButton>
                                :
                                <Link to="/login" className="nav-link">
                                    Login
                                </Link>
                            }
                        </Nav.Item>
                    </Nav>
                </Navbar.Collapse>

            </Container>
        </Navbar>
    );
};

export default NavBar;