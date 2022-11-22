import React from 'react';
import SubItem from "./SubItem";
import './ProfilePage.css'

const SubList = (props) => {
    const {
        items,
        subscribe = Function.prototype,
        unsubscribe = Function.prototype
    } = props;
    return (
        <div className="sub-list">
            {items.map(item => (
                <SubItem key={item.authorId} item={item} subscribe={subscribe} unsubscribe={unsubscribe}/>
            ))}
        </div>
    );
};

export default SubList;