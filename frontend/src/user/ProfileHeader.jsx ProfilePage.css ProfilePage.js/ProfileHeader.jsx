import React from 'react';
import {Card, CardHeader} from "@material-ui/core";
import {API_URL} from "../../http/http-common";
import {IMAGE_ROUTE} from "../../utils/consts";
import defaultAvatar from "../../components/UI/images/DefaultAvatar.jpg";
import MyButton from "../../components/UI/button/MyButton";
import {useTranslation} from "react-i18next";

const ProfileHeader = (props) => {
    const {
        firstName = '',
        lastName = '',
        fileName = '',
        editProfile = Function.prototype
    } = props;
    const {t} = useTranslation();

    return (
        <div>
            <Card className="paperItem" elevation={3}>
                <CardHeader
                    avatar={
                        <div style={{display: 'flex'}}>
                        <div className="round-image" >
                            <img src={fileName ? API_URL + IMAGE_ROUTE + `${fileName}` : defaultAvatar} alt="Avatar"
                                 className="rounded-circle profile-avatar"/>
                        </div>
                            <div>
                                <h4 className="user-name">{firstName + " " + lastName.charAt(0)}</h4>
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
                        <MyButton style={{marginTop: 10}}
                                  className={"btn btn-outline-primary ml-2 btn-sm"}
                                  onClick={() => editProfile()}
                        >
                            {t("edit")}
                        </MyButton>
                    }
                />
            </Card>
        </div>
    );
};

export default ProfileHeader;