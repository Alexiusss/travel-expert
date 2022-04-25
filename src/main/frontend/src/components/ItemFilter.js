import React from 'react';
import MyInput from "./UI/input/MyInput";

const ItemFilter = ({filter, setFilter}) => {
    return (
        <div>
            <MyInput
                value={filter.query}
                onChange={e => setFilter({...filter, query: e.target.value})}
                placeholder="Search..."
            />
        </div>
    );
};

export default ItemFilter;