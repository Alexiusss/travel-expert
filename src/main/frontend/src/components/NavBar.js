import React from 'react';
import {USERS_ROUTE} from "../utils/consts";
import {Link} from "react-router-dom";

const NavBar = () => {
    return (
        <div>
            <nav className="navbar navbar-expand-lg navbar-light bg-light">
                <a href="/" className="navbar-brand">
                    Restaurant advisor
                </a>
                <div className="navbar-nav mr-auto">
                    <li className="nav-item">
                        <Link to={USERS_ROUTE} className="nav-link">
                            Users
                        </Link>
                    </li>
                </div>
            </nav>
        </div>
    );
};

export default NavBar;