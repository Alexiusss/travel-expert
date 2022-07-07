import React, {useEffect, useState} from 'react';
import authService from 'services/AuthService.js'
import MyButton from "../components/UI/button/MyButton";
import {useTranslation} from "react-i18next";
import MyModal from "../components/UI/modal/MyModal";
import UserEditor from "../user/UserEditor";
import MyNotification from "../components/UI/notification/MyNotification";

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

    useEffect(() => {
        authService.profile()
            .then(response => {
                setId(response.data.id);
                setEmail(response.data.email);
                setFirstName(response.data.firstName);
                setLastName(response.data.lastName);
                setEnabled(response.data.enabled);
            })
    }, [email, firstName, lastName]);

    const editProfile = () => {
        setProfile({id, email, firstName, lastName});
        setModal(true);
    }

    const updateProfile = (updateProfile) => {
        setModal(false);
        setEmail(updateProfile.email)
        setFirstName(updateProfile.firstName)
        setLastName(updateProfile.lastName)
    }

    return (
        <div className="container justify-content-center align-items-center">
            <div className="card py-4" style={{border: "none"}}>
                <div className="d-flex justify-content-center align-items-center">
                    <div className="round-image">
                        <img src="https://i.imgur.com/Mn9kglf.jpg" className="rounded-circle" width="97"/>
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