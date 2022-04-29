import React from 'react';
import MyButton from "../components/UI/button/MyButton";
import userService from "../services/user.service";
import "./UserTable.css"
import { useTranslation } from "react-i18next";

const UserTable = (props) => {

    const handleDelete = (user) => {
        userService.remove(user.id)
            .then(response => {
                    props.remove(user)
                }
            )
            .catch(error => {
                console.log('Something went wrong', error);
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
                            <td><input type="checkbox" checked={user.enabled} readOnly={true}/></td>
                            <td>{user.roles}</td>
                            <td>
                                <MyButton className="btn btn-outline-info ml-2 btn-sm" onClick={() =>
                                    updateUser(user)
                                }>
                                    {t("edit")}
                                </MyButton>
                                <MyButton className="btn btn-outline-danger ml-2 btn-sm" onClick={() => {
                                    handleDelete(user)
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