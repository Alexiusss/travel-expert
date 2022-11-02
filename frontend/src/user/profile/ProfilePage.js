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
    const [alert, setAlert] = useState({open: false, message: '', severity: 'info'});
    const [reviews, setReviews] = useState([]);

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

    const openAlert = (msg, severity) => {
        setAlert({severity: severity, message: msg, open: true})
    }

    return (
        <>
            {profile.username ?
                <div className="container justify-content-center align-items-center">
                    <ProfileHeader {...profile} editProfile={editProfile}/>
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