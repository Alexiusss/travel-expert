import React from 'react';
import {Switch, Route} from 'react-router-dom'
import UsersList from "../user/UsersList";
import MainPage from "./MainPage";
import LoginForm from "./auth/LoginForm";

const AppRouter = () => {
    return (
        <Switch>
            <Route exact path={"/"} component={MainPage} />
            <Route exact path={"/login"} component={LoginForm} />
            <Route exact path={"/users"} component={UsersList} />
        </Switch>
    );
};

export default AppRouter;