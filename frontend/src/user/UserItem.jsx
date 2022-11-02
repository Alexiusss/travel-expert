import React from 'react';
import MyButton from "../components/UI/button/MyButton";
import {useAuth} from "../components/hooks/UseAuth";
import {useTranslation} from "react-i18next";

const UserItem = (props) => {
    const {isAdmin} = useAuth();
    const {
        user = {},
        updateUser = Function.prototype,
        deleteUser = Function.prototype,
        enableUser = Function.prototype
    } = props;
    const {t} = useTranslation();

    return (
        <tr>
            <td>{user.firstName}</td>
            <td>{user.lastName}</td>
            <td>{user.username}</td>
            <td>{user.email}</td>
            <td><input type="checkbox" checked={user.enabled}
                       onChange={() => enableUser(user, !user.enabled)}/></td>
            <td>{user.roles.join(', ')}</td>
            <td>
                {isAdmin ?
                    <MyButton className="btn btn-outline-info btn-sm" onClick={() =>
                        updateUser(user)
                    }>
                        {t("edit")}
                    </MyButton>
                    :
                    ""
                }
            </td>
            <td>
                {isAdmin ?
                    <MyButton className="btn btn-outline-danger btn-sm" onClick={() => {
                        deleteUser(user)
                    }}>
                        {t("delete")}</MyButton>
                    :
                    ""
                }
            </td>
        </tr>
    );
};

export default UserItem;