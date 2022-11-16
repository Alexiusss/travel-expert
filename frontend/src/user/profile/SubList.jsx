import React from 'react';
import SubItem from "./SubItem";

const SubList = (props) => {
    const {items} = props;
    return (
        <div>
            {items.map(item => (
                <SubItem item={item}/>
            ))}
        </div>
    );
};

export default SubList;