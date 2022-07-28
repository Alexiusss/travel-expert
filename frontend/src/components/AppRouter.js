import React from 'react';
import {Switch, Route} from 'react-router-dom'
import UsersList from "../user/UsersList";
import HomePage from "../pages/HomePage";
import LoginForm from "../pages/auth/LoginForm";
import RegisterForm from "../pages/auth/RegisterForm";
import {useAuth} from "./hooks/UseAuth";
import ProfilePage from "../pages/ProfilePage";
import RestaurantList from "../restaurant/RestaurantList";

const AppRouter = () => {
    const {isAdmin, isModerator, isAuth} = useAuth();

    return (
        <Switch>
            <Route exact path={"/"} component={HomePage} />
            <Route exact path={"/login"} component={LoginForm} />
            <Route exact path={"/register"} component={RegisterForm} />
            {isAuth
                ?
                <Route exact path={"/profile"}
                component={ProfilePage}/>
                :
                <h3>403 Access denied</h3>
            }
            { isAdmin || isModerator ?
                <Route exact path={"/users"}
                       component={UsersList}/>
                :
                <h3>403 Access denied</h3>
            }
            <Route exact path={"/restaurants"} component={RestaurantList} />
        </Switch>
    );
};

export default AppRouter;