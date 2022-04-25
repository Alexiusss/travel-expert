import React, {useEffect, useState} from 'react';
import MyButton from "../components/UI/button/MyButton";
import AddUser from "./AddUser";
import MyModal from "../components/UI/modal/MyModal";
import userService from '../services/user.service'
import UserTable from "./UserTable";
import {trackPromise, usePromiseTracker} from 'react-promise-tracker';
import ItemFilter from "../components/ItemFilter";
import {useItems} from "../components/hooks/UseData";
import Pagination from "@material-ui/lab/Pagination";

const UsersList = () => {

    // https://habr.com/ru/post/521902/#comment_22151160
    const area = 'users';
    const {promiseInProgress} = usePromiseTracker({area});
    const [users, setUsers] = useState([]);
    // https://www.bezkoder.com/react-pagination-hooks/
    const [totalPages, setTotalPages] = useState(0);
    const [page, setPage] = useState(1);
    const [size, setSize] = useState(10);
    const pageSizes = [20, 50, 100];
    const [userFromDB, setUserFromDB] = useState([]);

    useEffect(() => {
        trackPromise(userService.getAll(size, page), area).then(({data}) => {
            setUsers(data.content);
            setTotalPages(data.totalPages)
        });
    }, [setUsers, page, size]);

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

    const changePage = (event, value) => {
        setPage(value);
    }

    const changeSize = (event) => {
        setSize(event.target.value);
        setPage(1);
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
            <ItemFilter
                filter={filter}
                setFilter={setFilter}
            />
            <h2 style={{textAlign: "center"}}>
                Users list
            </h2>

            {"Items per Page: "}
            <select onChange={changeSize} value={size}>
                {pageSizes.map((pageSize) => (
                    <option key={pageSize} value={pageSize}>
                        {pageSize}
                    </option>
                ))}
            </select>

            <UserTable promiseInProgress={promiseInProgress} users={sortedAndSearchedUsers}
                       userFromDB={setUserFromDB} remove={removeUser} modalVisible={setModal}
            />

            <Pagination count={totalPages} page={page} onChange={changePage} shape="rounded"/>
        </div>
    );
};

export default UsersList;