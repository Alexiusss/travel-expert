import React, {useEffect, useState} from 'react';
import authService from 'services/AuthService.js'
import {useTranslation} from "react-i18next";
import MyModal from "../../components/UI/modal/MyModal";
import UserEditor from "../UserEditor";
import MyNotification from "../../components/UI/notification/MyNotification";
import ProfileHeader from "./ProfileHeader";
import "./ProfilePage.css"
import userService from "../../services/user.service";
import reviewService from "../../services/ReviewService";
import {useAuth} from "../../components/hooks/UseAuth";
import {useHistory, useLocation} from "react-router-dom";
import ReviewList from "../../pages/review/ReviewList";
import {Grid} from "@material-ui/core";
import imageService from "../../services/ImageService";
import {getLocalizedErrorMessages, PROFILE} from "../../utils/consts";
import Intro from "../../pages/review/Intro";
import {NotFound} from "../../pages/NotFound";
import SubList from "./SubList";

const ProfilePage = () => {
    const {t} = useTranslation();
    const {pathname, state} = useLocation();
    const {push} = useHistory();
    const {isAdmin, isModerator, authUserId} = useAuth();
    const username = pathname.replace(PROFILE, "");
    const [profile, setProfile] = useState({});
    const [authorId, setAuthorId] = useState(state ? state.id : profile.id);
    const isAuthor = authUserId === authorId;
    const [modal, setModal] = useState(false);
    const [subModal, setSubModal] = useState(false);
    const [alert, setAlert] = useState({open: false, message: '', severity: 'info'});
    const [reviews, setReviews] = useState([]);
    const [subscribers, setSubscribers] = useState([]);

    useEffect(() => {
        if (username.length) {
            userService.getAuthorByUsername(username)
                .then(({data}) => {
                    setProfile({
                        id: data.authorId,
                        firstName: data.authorName,
                        username: data.username,
                        fileName: data.fileName,
                        registeredAt: data.registeredAt,
                        subscribers: data.subscribers,
                        subscriptions: data.subscriptions,
                    })
                    setAuthorId(data.authorId)
                })
        } else {
            userService.getAuthor(authUserId)
                .then(({data}) => {
                    setProfile({
                        id: data.authorId,
                        firstName: data.authorName,
                        username: data.username,
                        fileName: data.fileName,
                        registeredAt: data.registeredAt,
                        subscribers: data.subscribers,
                        subscriptions: data.subscriptions,
                    })
                    setAuthorId(data.authorId)
                    push(PROFILE + data.username)
                })
        }
    }, []);

    useEffect(() => {
        if (authorId) {
            if (isAuthor || isAdmin || isModerator) {
                reviewService.getAllByUserId(authorId)
                    .then(({data}) => setReviews(data))
            } else {
                reviewService.getAllActiveByUserId(authorId)
                    .then(({data}) => setReviews(data))
            }
        }
    }, [authorId])

    const editProfile = () => {
        authService.profile()
            .then(({data}) => {
                setProfile({
                    ...profile,
                    id: data.id,
                    email: data.email,
                    firstName: data.firstName,
                    lastName: data.lastName,
                    username: data.username,
                    enabled: data.enabled,
                    fileName: data.fileName,
                    registeredAt: data.createdAt,
                })
            })
            .then(() => setModal(true))
    }

    const updateProfile = (updateProfile) => {
        setModal(false);
        setProfile({
            ...profile,
            email: updateProfile.email,
            firstName: updateProfile.firstName,
            lastName: updateProfile.lastName,
            username: updateProfile.username,
            fileName: updateProfile.fileName,
        })
        push(PROFILE + updateProfile.username)
    }

    const removeReview = (review) => {
        Promise.all([reviewService.delete(review.id), imageService.removeAllByFileNames(review.fileNames)])
            .then(() => {
                setReviews(reviews.filter(item => item.id !== review.id));
                setModal(false);
                openAlert([t('record deleted')], "success")
            })
            .catch(error => {
                openAlert(getLocalizedErrorMessages(error.response.data.message), "error");
            })
    }

    const subscribe = (e, id = null) => {
        if (id) {
            e.stopPropagation()
            e.preventDefault();
            userService.subscribe(id)
                .then(() => setSubscribers(subscribers.map(subscriber => {
                        if (subscriber.authorId === id) {
                            return {
                                ...subscriber,
                                subscribers: [...subscriber.subscribers, authUserId]
                            }
                        } else {
                            return subscriber;
                        }
                    }))
                )
        } else {
            userService.subscribe(authorId)
                .then(() => setProfile({
                    ...profile,
                    subscribers: [...profile.subscribers, authUserId],
                }))
        }
    }

    const unSubscribe = (e, id = null) => {
        if (id) {
            e.preventDefault();
            e.stopPropagation()
            userService.unSubscribe(id)
                .then(() => setSubscribers(subscribers.map(subscriber => {
                    if (subscriber.authorId === id) {
                        return {
                            ...subscriber,
                            subscribers: subscriber.subscribers.filter(userId => userId !== authUserId)
                        }
                    }
                    return subscriber;
                })))
        } else {
            userService.unSubscribe(authorId)
                .then(() => setProfile({
                    ...profile,
                    subscribers: profile.subscribers.filter(userId => userId !== authUserId),
                }))
        }
    }

    const loadSubscribers = (subIds = []) => {
        userService.getAuthorList(subIds)
            .then(({data}) => {
                setSubscribers(data);
                setSubModal(true);
            })
    }

    const openAlert = (msg, severity) => {
        setAlert({severity: severity, message: msg, open: true})
    }

    return (
        <>
            {profile.username ?
                <div className="container justify-content-center align-items-center">
                    <ProfileHeader {...profile}
                                   editProfile={editProfile}
                                   contributionsCount={reviews.length}
                                   subscribe={subscribe}
                                   unSubscribe={unSubscribe}
                                   loadSubscribers={loadSubscribers}
                    />
                    <Grid container
                          spacing={1}
                          direction="row"
                    >
                        <Intro {...profile}/>
                        <Grid item xs={12} sm={12} md={6} key="introKey">
                            <ReviewList reviews={reviews} remove={removeReview}/>
                        </Grid>
                    </Grid>
                    {modal &&
                        <MyModal visible={modal} setVisible={setModal}>
                            <UserEditor userFromDB={profile} update={updateProfile} modal={modal}
                                        setAlert={setAlert}/>
                        </MyModal>
                    }
                    {subModal &&
                        <MyModal visible={subModal} setVisible={setSubModal}>
                            <SubList items={subscribers} loadSubscribers={loadSubscribers} subscribe={subscribe}
                                     unsubscribe={unSubscribe}/>
                        </MyModal>
                    }
                    <MyNotification open={alert.open} setOpen={setAlert} message={alert.message}
                                    severity={alert.severity}/>
                </div>
                :
                <NotFound/>
            }
        </>
    );
};

export default ProfilePage;