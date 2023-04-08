import React from 'react';
import {Switch, Route} from 'react-router-dom'
import UsersList from "../user/UsersList";
import HomePage from "../pages/HomePage";
import LoginForm from "../pages/auth/LoginForm";
import RegisterForm from "../pages/auth/RegisterForm";
import {useAuth} from "./hooks/UseAuth";
import ProfilePage from "../user/profile/ProfilePage";
import RestaurantList from "../restaurant/RestaurantList";
import RestaurantPage from "../restaurant/RestaurantPage";
import ReviewsSection from "../pages/review/ReviewsSection";
import {NotFound} from "../pages/NotFound";
import HotelList from "../hotel/HotelList";
import HotelPage from "../hotel/HotelPage";
import APIDocs from "../pages/ApiDocs";

const AppRouter = () => {
    const {isAdmin, isModerator} = useAuth();

    return (
        <main className="content">
            <Switch>
                <Route exact path={"/"} component={HomePage}/>
                <Route exact path={"/apiDocs"} component={APIDocs}/>
                <Route exact path={"/login"} component={LoginForm}/>
                <Route exact path={"/register"} component={RegisterForm}/>
                <Route exact path={"/restaurants/"} component={RestaurantList}/>
                <Route exact path={"/restaurants/:name"} component={RestaurantPage}/>
                <Route exact path={"/hotels/"} component={HotelList}/>
                <Route exact path={"/hotels/:name"} component={HotelPage}/>
                <Route path={["/profile/", "/profile/:authorName"]} component={ProfilePage}/>
                {(isAdmin || isModerator) &&
                    [
                        <Route exact path={"/users"} key={"/users"} component={UsersList}/>,
                        <Route exact path={"/reviews"} key={"/reviews"} component={ReviewsSection}/>
                    ]
                }
                <Route component={NotFound}/>
            </Switch>
        </main>
    );
};

export default AppRouter;