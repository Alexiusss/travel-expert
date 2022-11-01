import React from 'react';
import {Card, CardHeader} from "@material-ui/core";
import {API_URL} from "../../http/http-common";
import {IMAGE_ROUTE} from "../../utils/consts";
import defaultAvatar from "../../components/UI/images/DefaultAvatar.jpg";
import MyButton from "../../components/UI/button/MyButton";
import {useTranslation} from "react-i18next";
import {useAuth} from "../../components/hooks/UseAuth";

const ProfileHeader = (props) => {
    const {
        id = '',
        firstName = '',
        username = '',
        fileName = '',
        editProfile = Function.prototype
    } = props;
    const {t} = useTranslation();
    const {isAdmin, authUserId} = useAuth();
    const isOwner = authUserId === id;

    return (
        <div>
            <Card className="paperItem" elevation={3}>
                <CardHeader
                    avatar={
                        <div style={{display: 'flex'}}>
                            <div className="round-image">
                                <img src={fileName ? API_URL + IMAGE_ROUTE + `${fileName}` : defaultAvatar} alt="Avatar"
                                     className="rounded-circle profile-avatar"/>
                            </div>
                            <div>
                                <div className="username">
                                <h4 >{firstName}</h4>
                                <span>@{username}</span>
                                </div>
                                <div className="profile-flex">
                                    <div>
                                        <span className="profile-block">Contributions</span>
                                        <span className="profile-block">0</span>
                                    </div>
                                    <div>
                                        <span className="profile-block">Followers</span>
                                        <span className="profile-block">0</span>
                                    </div>
                                    <div>
                                        <span className="profile-block">Following</span>
                                        <span className="profile-block">0</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    }
                    action={
                        (isOwner || isAdmin)
                            ?
                            <MyButton style={{marginTop: 10}}
                                      className={"btn btn-outline-primary ml-2 btn-sm"}
                                      onClick={() => editProfile()}
                            >
                                {t("edit")}
                            </MyButton>
                            :
                            null
                    }
                />
            </Card>
        </div>
    );
};

export default ProfileHeader;