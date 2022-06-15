import React from 'react';
import {Switch, Route} from 'react-router-dom'
import UsersList from "../user/UsersList";
import HomePage from "../pages/HomePage";
import LoginForm from "../pages/auth/LoginForm";

const AppRouter = () => {
    return (
        <Switch>
            <Route exact path={"/"} component={HomePage} />
            <Route exact path={"/login"} component={LoginForm} />
            <Route exact path={"/users"} component={UsersList} />
        </Switch>
    );
};

export default AppRouter;