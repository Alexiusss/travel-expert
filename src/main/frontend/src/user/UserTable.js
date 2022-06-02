import React from 'react';
import MyButton from "../components/UI/button/MyButton";
import userService from "../services/user.service";
import "./UserTable.css"
import {useTranslation} from "react-i18next";
import {getLocalizedErrorMessages} from "../utils/consts";

const UserTable = (props) => {

    const deleteUser = (user) => {
        userService.remove(user.id)
            .then(response => {
                    props.remove(user);
                    openAlert([t('record deleted')], "success")
                }
            )
            .catch(error => {
                console.log('Something went wrong', error);
                openAlert(getLocalizedErrorMessages(error.response.data.message), "error");
            })
    }

    const updateUser = (user) => {
        userService.get(user.id)
            .then(response => {
                    props.userFromDB(response.data)
                    props.modalVisible(true);
                }
            )
            .catch(error => {
                console.log('Something went wrong', error);
            })

    }

    const openAlert = (msg, severity) => {
        props.setAlert({severity: severity, message: msg, open: true})
    }

    const enableUser = (user, enable) => {
        userService.enable(user.id, enable)
            .then(() => {
                    user.enabled = enable;
                    props.enable(user);
                }
            )
            .catch(error => {
                console.log('Something went wrong', error);
                openAlert(getLocalizedErrorMessages(error.response.data.message), "error");
            })
    }

    const {items: users, requestSort, sortConfig} = props.users;

    const getClassNamesFor = (name) => {
        if (!sortConfig) {
            return;
        }
        return sortConfig.key === name ? sortConfig.direction : undefined;
    };

    const {t} = useTranslation();

    return (
        <div>
            <table className="table table-striped">
                <thead>
                <tr>
                    <th>
                        <button
                            type="button"
                            onClick={() => requestSort('firstName')}
                            className={getClassNamesFor('firstName')}
                        >
                            {t("first name")}
                        </button>

                    </th>
                    <th>
                        <button
                            type="button"
                            onClick={() => requestSort('lastName')}
                            className={getClassNamesFor('lastName')}
                        >
                            {t("last name")}
                        </button>
                    </th>
                    <th>
                        <button
                            type="button"
                            onClick={() => requestSort('email')}
                            className={getClassNamesFor('email')}
                        >
                            {t("email")}
                        </button>
                    </th>
                    <th>
                        <button
                            type="button"
                            onClick={() => requestSort('enabled')}
                            className={getClassNamesFor('enabled')}
                        >
                            {t("enabled")}
                        </button>
                    </th>
                    <th>
                        <button
                            type="button"
                            onClick={() => requestSort('roles')}
                            className={getClassNamesFor('roles')}
                        >
                            {t("roles")}
                        </button>
                    </th>
                </tr>
                </thead>
                <tbody>
                {props.promiseInProgress
                    ? <tr>
                        <td>{t("data loading")}</td>
                    </tr>
                    : users.map((user, index) =>
                        <tr key={index}>
                            <td>{user.firstName}</td>
                            <td>{user.lastName}</td>
                            <td>{user.email}</td>
                            <td><input type="checkbox" checked={user.enabled}
                                       onChange={() => enableUser(user, !user.enabled)}/></td>
                            <td>{user.roles.join(', ')}</td>
                            <td>
                                <MyButton className="btn btn-outline-info btn-sm" onClick={() =>
                                    updateUser(user)
                                }>
                                    {t("edit")}
                                </MyButton>
                            </td>
                            <td>
                                <MyButton className="btn btn-outline-danger btn-sm" onClick={() => {
                                    deleteUser(user)
                                }}>
                                    {t("delete")}</MyButton>
                            </td>
                        </tr>
                    )
                }
                </tbody>
            </table>
        </div>
    );
};

export default UserTable;