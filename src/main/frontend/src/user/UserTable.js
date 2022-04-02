import React from 'react';

const UserTable = (props) => {
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
                </tr>
                </thead>
                <tbody>
                {props.promiseInProgress
                    ? <tr>
                        <td>Wait, the data is downloading!</td>
                    </tr>
                    : props.users.map((user) =>
                        <tr key={user.id}>
                            <td>{user.firstName}</td>
                            <td>{user.lastName}</td>
                            <td>{user.email}</td>
                            <td><input type="checkbox" checked={user.enabled} readOnly={true}/></td>
                            <td>{user.roles}</td>
                        </tr>
                    )
                }
                </tbody>
            </table>
        </div>
    );
};

export default UserTable;