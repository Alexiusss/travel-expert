import React from 'react';
import SubItem from "./SubItem";
import './ProfilePage.css'

const SubList = (props) => {
    const {
        items,
        subscribe = Function.prototype,
        unsubscribe = Function.prototype,
        close = Function.prototype
    } = props;
    return (
        <>
            <div className="sub-header">
                    <span id="close" onClick={close}>x</span>
            </div>
            <div className="sub-list">
                {items.map(item => (
                    <SubItem key={item.authorId} item={item} subscribe={subscribe} unsubscribe={unsubscribe}/>
                ))}
            </div>
        </>
    );
};

export default SubList;