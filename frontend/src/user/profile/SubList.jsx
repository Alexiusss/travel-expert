import React from 'react';
import './Sublist.css'
import SkeletonSub from "./SceletonSub";
import SubItem from "./SubItem";

const SubList = (props) => {
    const {
        items,
        subscribe = Function.prototype,
        unsubscribe = Function.prototype,
        close = Function.prototype,
        isLoaded = false
    } = props;
    return (
        <>
            <div className="sub-header">
                <span id="close" onClick={close}>x</span>
            </div>
            {!isLoaded
                ?
                <SkeletonSub listsToRender={13}/>
                :
                <div className="sub-list">
                    {items.map(item => (
                        <SubItem key={item.authorId} item={item} subscribe={subscribe} unsubscribe={unsubscribe}/>
                    ))}
                </div>
            }
        </>
    );
};

export default SubList;