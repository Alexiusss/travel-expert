import React from 'react';
import {Switch, Route} from 'react-router-dom'
import UsersList from "../user/UsersList";
import HomePage from "../pages/HomePage";
import LoginForm from "../pages/auth/LoginForm";
import {useAuth} from "./hooks/UseAuth";

const AppRouter = () => {
    const {isAdmin, isModerator} = useAuth();

    return (
        <Switch>
            <Route exact path={"/"} component={HomePage} />
            <Route exact path={"/login"} component={LoginForm} />
            { isAdmin || isModerator ?
                <Route exact path={"/users"}
                       component={UsersList}/>
                :
                <h3>403 Access denied</h3>
            }
        </Switch>
    );
};

export default AppRouter;