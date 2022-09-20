import React, {useEffect, useState} from 'react';
import authService from 'services/AuthService.js'
import MyButton from "../components/UI/button/MyButton";
import {useTranslation} from "react-i18next";
import MyModal from "../components/UI/modal/MyModal";
import UserEditor from "../user/UserEditor";
import MyNotification from "../components/UI/notification/MyNotification";
import defaultAvatar from "../components/UI/images/DefaultAvatar.jpg"
import {IMAGE_ROUTE} from "../utils/consts";
import {API_URL} from "../http/http-common";

const ProfilePage = () => {
    const [id, setId] = useState(null);
    const [email, setEmail] = useState('');
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [isEnabled, setEnabled] = useState(false);
    const {t} = useTranslation();
    const [modal, setModal] = useState(false);
    const [profile, setProfile] = useState({});
    const [alert, setAlert] = useState({open: false, message: '', severity: 'info'})
    const [fileName, setFileName] = useState(null);

    useEffect(() => {
        authService.profile()
            .then(response => {
                setId(response.data.id);
                setEmail(response.data.email);
                setFirstName(response.data.firstName);
                setLastName(response.data.lastName);
                setEnabled(response.data.enabled);
                setFileName(response.data.fileName)
            })
    }, [email, firstName, lastName, fileName]);

    const editProfile = () => {
        setProfile({id, email, firstName, lastName, fileName});
        setModal(true);
    }

    const updateProfile = (updateProfile) => {
        setModal(false);
        setEmail(updateProfile.email)
        setFirstName(updateProfile.firstName)
        setLastName(updateProfile.lastName)
        setFileName(updateProfile.fileName)
    }

    return (
        <div className="container justify-content-center align-items-center">
            <div className="card py-4" style={{border: "none"}}>
                <div className="d-flex justify-content-center align-items-center">
                    <div className="round-image">
                        <img src={fileName ? API_URL + IMAGE_ROUTE + `${fileName}` : defaultAvatar} alt="Avatar" className="rounded-circle" width="97"/>
                    </div>
                </div>
                <div className="text-center">
                    <h4 className="mt-3">{firstName + " " + lastName}</h4>
                    <span>{email}</span>
                    <br/>
                    <span>status: {isEnabled ? 'active' : 'inactive'}</span>
                    <div>
                        <MyButton style={{marginTop: 10}} className={"btn btn-outline-primary ml-2 btn-sm"}
                                  onClick={() => editProfile()}>
                            {t("edit")}
                        </MyButton>
                        <MyModal visible={modal} setVisible={setModal}>
                            <UserEditor userFromDB={profile} update={updateProfile} modal={modal} setAlert={setAlert}/>
                        </MyModal>

                        <MyNotification open={alert.open} setOpen={setAlert} message={alert.message}
                                        severity={alert.severity}/>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ProfilePage;