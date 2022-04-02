import React, {useEffect, useState} from 'react';
import MyButton from "../components/button/MyButton";
import AddUser from "./AddUser";
import MyModal from "../components/modal/MyModal";
import userService from '../services/user.service'
import UserTable from "./UserTable";
import {trackPromise, usePromiseTracker} from 'react-promise-tracker';

const UsersList = () => {

    // https://habr.com/ru/post/521902/#comment_22151160
    const area = 'users';
    const {promiseInProgress} = usePromiseTracker({area});
    const [users, setUsers] = useState([])

    useEffect(() => {
        trackPromise(userService.getAll(), area).then(({data}) => {
            setUsers(data);
        });
    }, [setUsers]);

    const [modal, setModal] = useState(false)

    const createUser = () => {
        setModal(false)
    }

    return (
        <div>
            <MyButton style={{marginTop: 10}} className={"btn btn-outline-primary ml-2 btn-sm"}
                      onClick={() => setModal(true)}>
                Добавить
            </MyButton>
            <MyModal visible={modal} setVisible={setModal}>
                <AddUser create={createUser}/>
            </MyModal>
            <hr style={{margin: '15px 0'}}/>

            <h2 style={{textAlign: "center"}}>
                Users list
            </h2>

            <UserTable  promiseInProgress={promiseInProgress} users={users}/>

        </div>
    );
};

export default UsersList;