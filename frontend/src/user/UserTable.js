import React from 'react';
import userService from "../services/user.service";
import "./UserTable.css"
import {useTranslation} from "react-i18next";
import {getLocalizedErrorMessages} from "../utils/consts";
import imageService from "../services/ImageService";
import UserItem from "./UserItem";
import reviewService from "../services/ReviewService";

const UserTable = (props) => {
    const deleteUser = (user) => {
        Promise.all([
            imageService.remove(user.fileName),
            userService.remove(user.id),
            reviewService.deleteAllByUserId(user.id)
        ])
            .then(response => {
                    props.remove(user);
                    openAlert([t('record deleted')], "success")
                }
            )
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
                            onClick={() => requestSort('username')}
                            className={getClassNamesFor('username')}
                        >
                            {t("username")}
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
                    : users.map((user, index) => (
                        <UserItem key={index}
                                  user = {user}
                                  updateUser={updateUser}
                                  deleteUser={deleteUser}
                                  enableUser={enableUser}/>
                        )
                    )
                }
                </tbody>
            </table>
        </div>
    );
};

export default UserTable;