import React, {useEffect, useState} from 'react';
import authService from 'services/AuthService.js'
import {useTranslation} from "react-i18next";
import MyModal from "../../components/UI/modal/MyModal";
import UserEditor from "../UserEditor";
import MyNotification from "../../components/UI/notification/MyNotification";
import ProfileHeader from "./ProfileHeader";
import "./ProfilePage.css"
import userService from "../../services/user.service";
import {useAuth} from "../../components/hooks/UseAuth";
import {useLocation} from "react-router-dom";

const ProfilePage = () => {
    const {t} = useTranslation();
    const {authorId} = useLocation().state || "";
    const {authUserId} = useAuth();
    const isAuthor = authUserId === authorId;
    const [modal, setModal] = useState(false);
    const [profile, setProfile] = useState({});
    const [alert, setAlert] = useState({open: false, message: '', severity: 'info'})

    useEffect(() => {
        if (authorId && !isAuthor) {
            userService.getAuthor(authorId)
                .then(({data}) => setProfile({
                    id: data.authorId,
                    firstName: data.authorName,
                    fileName: data.fileName,
                }))
        } else {
            authService.profile()
                .then(({data}) => {
                    setProfile({
                        id: data.id,
                        email: data.email,
                        firstName: data.firstName,
                        lastName: data.lastName,
                        enabled: data.enabled,
                        fileName: data.fileName,
                    })
                })
        }
    }, []);

    const editProfile = () => {
        setModal(true);
    }

    const updateProfile = (updateProfile) => {
        setModal(false);
        setProfile({
            ...profile,
            email: updateProfile.email,
            firstName: updateProfile.firstName,
            lastName: updateProfile.lastName,
            fileName: updateProfile.fileName,
        })
    }

    return (
        <div className="container justify-content-center align-items-center">
            <ProfileHeader {...profile} editProfile={editProfile}/>
            {modal &&
                <MyModal visible={modal} setVisible={setModal}>
                    <UserEditor userFromDB={profile} update={updateProfile} modal={modal}
                                setAlert={setAlert}/>
                </MyModal>
            }

            <MyNotification open={alert.open} setOpen={setAlert} message={alert.message}
                            severity={alert.severity}/>
        </div>
    );
};

export default ProfilePage;