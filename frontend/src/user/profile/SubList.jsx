import React from 'react';
import SubItem from "./SubItem";
import './ProfilePage.css'

const SubList = (props) => {
    const {items} = props;
    return (
        <div className="sub-list">
            {items.map(item => (
                <SubItem key={item.authorId} item={item}/>
            ))}
        </div>
    );
};

export default SubList;