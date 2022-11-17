import React from 'react';

const SubItem = (props) => {
    const {item} = props;
    return (
        <div>
            {item.authorId}
        </div>
    );
};

export default SubItem;