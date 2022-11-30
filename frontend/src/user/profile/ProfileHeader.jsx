import React from 'react';
import {Card, CardActions, CardHeader} from "@material-ui/core";
import {API_URL} from "../../http/http-common";
import {IMAGE_ROUTE} from "../../utils/consts";
import defaultAvatar from "../../components/UI/images/DefaultAvatar.jpg";
import MyButton from "../../components/UI/button/MyButton";
import {useTranslation} from "react-i18next";
import {useAuth} from "../../components/hooks/UseAuth";
import './ProfileHeader.css'

const ProfileHeader = (props) => {
    const {
        id = '',
        firstName = '',
        username = '',
        fileName = '',
        subscribers = [],
        subscriptions = [],
        contributionsCount = 0,
        editProfile = Function.prototype,
        unSubscribe = Function.prototype,
        subscribe = Function.prototype,
        loadSubscribers = Function.prototype
    } = props;
    const {t} = useTranslation();
    const {isAuth, isAdmin, authUserId} = useAuth();
    const isOwner = authUserId === id;
    const isAuthUserSubscribed = subscribers.includes(authUserId) || false;

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
                                <div>
                                    <span className="profile-name">{firstName}</span>
                                    <span className="profile-username">@{username}</span>
                                </div>
                                <div className="profile-flex">
                                    <div>
                                        <span className="profile-block">{t('contributions')}</span>
                                        <span className="profile-block">{contributionsCount}</span>
                                    </div>
                                    <div>
                                        <span className="profile-block">{t('followers')}</span>
                                        <span className="profile-block">
                                            {subscribers.length ?
                                                <a onClick={() => loadSubscribers(subscribers)}>{subscribers.length}</a>
                                                :
                                                subscribers.length
                                            }
                                        </span>
                                    </div>
                                    <div>
                                        <span className="profile-block">{t('following')}</span>
                                        <span className="profile-block">
                                            {subscriptions.length ?
                                                <a onClick={() => loadSubscribers(subscriptions)}>{subscriptions.length}</a>
                                                :
                                                subscriptions.length
                                            }
                                        </span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    }
                    action={
                        <CardActions style={{marginTop: 10}}>
                            {(isAuth && !isOwner) ?
                                <MyButton className={"btn btn-outline-primary ml-2 btn-sm"}
                                          onClick={() => isAuthUserSubscribed ? unSubscribe() : subscribe()}
                                >
                                    {isAuthUserSubscribed ? t('unsubscribe') : t('follow')}
                                </MyButton> : <></>
                            }

                            {(isOwner || isAdmin)
                                ?
                                <MyButton className={"btn btn-outline-primary ml-2 btn-sm"}
                                          onClick={() => editProfile()}
                                >
                                    {t("edit")}
                                </MyButton>
                                :
                                null
                            }
                        </CardActions>
                    }
                />
            </Card>
        </div>
    );
};

export default ProfileHeader;