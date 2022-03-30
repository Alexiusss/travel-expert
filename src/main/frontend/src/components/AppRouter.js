import React from 'react';
import {Switch, Route} from 'react-router-dom'
import UsersList from "../user/UsersList";

const AppRouter = () => {
    return (
        <Switch>
            <Route exact path={["/", "/users"]} component={UsersList} />
        </Switch>
    );
};

export default AppRouter;