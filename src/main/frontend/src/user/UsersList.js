import React, {useEffect, useState} from 'react';
import MyButton from "../components/UI/button/MyButton";
import AddUser from "./AddUser";
import MyModal from "../components/UI/modal/MyModal";
import userService from '../services/user.service'
import UserTable from "./UserTable";
import {trackPromise, usePromiseTracker} from 'react-promise-tracker';
import ItemFilter from "../components/ItemFilter";
import {useItems} from "../components/hooks/UseData";
import Pagination from "../components/UI/pagination/Pagination";

const UsersList = () => {

    // https://habr.com/ru/post/521902/#comment_22151160
    const area = 'users';
    const {promiseInProgress} = usePromiseTracker({area});
    const [users, setUsers] = useState([]);
    const [userFromDB, setUserFromDB] = useState([]);

    useEffect(() => {
        trackPromise(userService.getAll(), area).then(({data}) => {
            setUsers(data);
        });
    }, [setUsers]);

    const [filter, setFilter] = useState({config: null, query: ''})
    const searchedColumns = ["email", "firstName", "lastName"];
    const sortedAndSearchedUsers = useItems(users, filter.config, filter.query, searchedColumns);

    const [modal, setModal] = useState(false);

    const createUser = (newUser) => {
        setUsers([...users, newUser])
        setModal(false)
    }

    // https://www.codingdeft.com/posts/react-usestate-array/
    const updateUser = (updatedUser) => {
        setModal(false)

        setUsers(users => {
            return users.map(user => {
                return user.id === updatedUser.id
                    ?
                    {
                        ...user,
                        email: updatedUser.email,
                        firstName: updatedUser.firstName,
                        lastName: updatedUser.lastName,
                        password: updatedUser.password
                    }
                    :
                    user
            })
        })
    }

    const removeUser = (user) => {
        setUsers(users.filter(u => u.id !== user.id))
    }

    return (
        <div className='container'>
            <MyButton style={{marginTop: 10}} className={"btn btn-outline-primary ml-2 btn-sm"}
                      onClick={() => setModal(true)}>
                Add
            </MyButton>
            <MyModal visible={modal} setVisible={setModal}>
                <AddUser userFromDB={userFromDB} create={createUser} update={updateUser} modal={modal}/>
            </MyModal>
            <hr style={{margin: '15px 0'}}/>
            <h2 style={{textAlign: "center"}}>
                Users list
            </h2>

            <ItemFilter
                filter={filter}
                setFilter={setFilter}
            />

            <UserTable promiseInProgress={promiseInProgress} users={sortedAndSearchedUsers}
                       userFromDB={setUserFromDB} remove={removeUser} modalVisible={setModal}
                       />
        </div>
    );
};

export default UsersList;