import React from 'react';
import MyButton from "../components/button/MyButton";
import userService from "../services/user.service";
import "./UserTable.css"

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
                            First Name
                        </button>

                    </th>
                    <th>
                        <button
                            type="button"
                            onClick={() => requestSort('lastName')}
                            className={getClassNamesFor('lastName')}
                            >
                            Last Name
                        </button>
                    </th>
                    <th>
                        <button
                            type="button"
                            onClick={() => requestSort('email')}
                            className={getClassNamesFor('email')}
                        >
                            Email
                        </button>
                    </th>
                    <th>
                        <button
                            type="button"
                            onClick={() => requestSort('enabled')}
                            className={getClassNamesFor('enabled')}
                        >
                            Enabled
                        </button>
                    </th>
                    <th>
                        <button
                            type="button"
                            onClick={() => requestSort('roles')}
                            className={getClassNamesFor('roles')}
                        >
                            Roles
                        </button>
                    </th>
                    <th>
                        <button
                            type="button"
                        >
                            Actions
                        </button>
                    </th>
                </tr>
                </thead>
                <tbody>
                {props.promiseInProgress
                    ? <tr>
                        <td>Wait, the data is downloading!</td>
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
                                    Edit
                                </MyButton>
                                <MyButton className="btn btn-outline-danger ml-2 btn-sm" onClick={() => {
                                    handleDelete(user)
                                }}>
                                    Delete</MyButton>
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