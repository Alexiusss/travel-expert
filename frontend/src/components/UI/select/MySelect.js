import React from 'react';
import {useTranslation} from "react-i18next";

const MySelect = (props) => {
    const {t} = useTranslation();

    return (
        <div>
            {t("items per page")}
            <select onChange={props.changeSize} value={props.size}>
                {props.pageSizes.map((pageSize) => (
                    <option key={pageSize} value={pageSize}>
                        {pageSize}
                    </option>
                ))}
            </select>
        </div>
    );
};

export default MySelect;