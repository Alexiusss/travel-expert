import React from 'react';
import {Skeleton} from "@material-ui/lab";
import './Sublist.css'
import "./SubItem.css";
import defaultAvatar from "../../components/UI/images/DefaultAvatar.jpg";

const SkeletonSub = ({listsToRender}) => {
    return (
        <div className="sub-list">
            {Array(listsToRender)
                .fill(1)
                .map((card, index) => (
                    <div className="author-card" key={index}>
                        <div className="avatar-section">
                            <span className="author-avatar">
                                <Skeleton
                                    variant="circle"
                                children={
                                    <img alt="Avatar" src={defaultAvatar}/>
                                }
                                />
                            </span>
                        </div>
                        <div style={{flexGrow: 1, minWidth: 0, width: "100%"}}>
                            <div>
                                <Skeleton className="author-name" width={200}/>
                                <Skeleton className="username" width={100}/>
                            </div>
                            <div className="author-info" style={{display: "block"}}>
                                <Skeleton className="author-subs bC" width={150}/>
                            </div>
                        </div>
                    </div>
                ))}
        </div>
    );
};

export default SkeletonSub;