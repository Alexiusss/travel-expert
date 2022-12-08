import React from 'react';
import './ProfileHeader.css'
import {Card, CardHeader} from "@material-ui/core";
import defaultAvatar from "../../components/UI/images/DefaultAvatar.jpg";
import {Skeleton} from "@material-ui/lab";

const SkeletonHeader = () => {
    return (
        <div>
            <Card className="paperItem" elevation={3}>
                <CardHeader
                    avatar={
                        <div style={{display: 'flex'}}>
                            <div className="round-image">
                                <img alt="Avatar" src={defaultAvatar} className="rounded-circle profile-avatar"/>
                            </div>
                            <div>
                                <div>
                                    <Skeleton>
                                        <span className="profile-name">
                                        User Name
                                             </span>
                                    </Skeleton>
                                    <Skeleton>
                                        <span className="profile-username">
                                        @Username
                                             </span>
                                    </Skeleton>
                                </div>
                                <div className="profile-flex">
                                    <div>
                                        <div className="profile-block">
                                            <Skeleton variant="text" width={250}/>
                                        </div>
                                        <div className="profile-block">
                                            <Skeleton variant="text"/>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    }
                />
            </Card>
        </div>
    );
};

export default SkeletonHeader;