import React from 'react';
import MyButton from "../components/button/MyButton";
import userService from "../services/user.service";

const UserTable = (props) => {

    const handleDelete = (user) => {
        console.log('Printing id', user.id);
        userService.remove(user.id)
            .then(response => {
                console.log('user deleted successfully');
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
    return (
        <div className="container">
            <table className="table table-striped">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Last name</th>
                    <th>Email</th>
                    <th>Enabled</th>
                    <th>Roles</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {props.promiseInProgress
                    ? <tr>
                        <td>Wait, the data is downloading!</td>
                    </tr>
                    : props.users.map((user, index) =>
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