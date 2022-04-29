import React from 'react';
import MyInput from "./UI/input/MyInput";
import { useTranslation } from "react-i18next";

const ItemFilter = ({filter, setFilter}) => {
    const {t} = useTranslation();
    return (
        <div>
            <MyInput
                value={filter.query}
                onChange={e => setFilter({...filter, query: e.target.value})}
                placeholder={t("search")}
            />
        </div>
    );
};

export default ItemFilter;