import React from 'react';
import {IMAGE_ROUTE} from "../../utils/consts";
import {API_URL} from "../../http/http-common";
import defaultAvatar from "../../components/UI/images/DefaultAvatar.jpg";
import {useTranslation} from "react-i18next";
import MyButton from "../../components/UI/button/MyButton";
import {useAuth} from "../../components/hooks/UseAuth";
import "./SubItem.css";

const SubItem = (props) => {
    const {t} = useTranslation();
    const {
        authorId,
        authorName,
        username,
        fileName,
        subscribers = [],
        reviewsCount = 0
    } = props.item;
    const {
        subscribe = Function.prototype,
        unsubscribe = Function.prototype
    } = props;
    const {isAuth, authUserId} = useAuth();
    const isOwner = authUserId === authorId;
    const isAuthUserSubscribed = subscribers.includes(authUserId) || false;

    return (
        <a href={`/profile/${username}`}>
            <div className="author-card">
                <div className="avatar-section">
                    <span className="author-avatar">
                        <img alt="Avatar" src={fileName ? API_URL + IMAGE_ROUTE + `${fileName}` : defaultAvatar}/>
                    </span>
                </div>
                <div style={{flexGrow: 1, minWidth: 0, width: "100%"}}>
                    <div>
                        <span className="author-name">{authorName}</span>
                        <span className="username">@{username}</span>
                    </div>
                    <div className="author-info">
                    <span className="author-subs">
                        <span className="bC">
                        {subscribers.length}
                            </span>
                        {t('followers')}
                    </span>
                        <span className="author-subs">
                            <span className="bC">
                        {reviewsCount}
                                </span>
                            {t('contributions')}
                    </span>
                    </div>
                </div>
                {(isAuth && !isOwner) ?
                    <div className="button-section">
                        <MyButton className={"btn btn-outline-primary ml-2 btn-sm"}
                                  onClick={(e) => isAuthUserSubscribed ? unsubscribe(e, authorId) : subscribe(e, authorId)}
                        >
                            {isAuthUserSubscribed ? t('unsubscribe') : t('follow')}
                        </MyButton>
                    </div>
                    : null
                }
            </div>
        </a>
    )
        ;
};

export default SubItem;