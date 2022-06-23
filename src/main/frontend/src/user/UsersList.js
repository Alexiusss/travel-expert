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
import {useTranslation} from "react-i18next";
import MyNotification from "../components/UI/notification/MyNotification";
import {useAuth} from "../components/hooks/UseAuth";

const UsersList = () => {

    // https://habr.com/ru/post/521902/#comment_22151160
    const area = 'users';
    const {promiseInProgress} = usePromiseTracker({area});
    const {t} = useTranslation();
    const [users, setUsers] = useState([]);
    // https://www.bezkoder.com/react-pagination-hooks/
    const [totalPages, setTotalPages] = useState(0);
    const [page, setPage] = useState(1);
    const [size, setSize] = useState(20);
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

    const [alert, setAlert] = useState({open: false, message: '', severity: 'info'})

    const {isAdmin} = useAuth();

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
                        password: updatedUser.password,
                        enabled: updatedUser.enabled
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
            {isAdmin ?
                <MyButton style={{marginTop: 10}} className={"btn btn-outline-primary ml-2 btn-sm"}
                          onClick={() => setModal(true)}>
                    {t("add")}
                </MyButton>
                :
                ""
            }
            <MyModal visible={modal} setVisible={setModal}>
                <AddUser userFromDB={userFromDB} create={createUser} update={updateUser}
                         modal={modal} setAlert={setAlert}/>
            </MyModal>
            <hr style={{margin: '15px 0'}}/>
            <ItemFilter
                filter={filter}
                setFilter={setFilter}
            />
            <h2 style={{textAlign: "center"}}>
                {t("user list")}
            </h2>

            {t("items per page")}
            <select onChange={changeSize} value={size}>
                {pageSizes.map((pageSize) => (
                    <option key={pageSize} value={pageSize}>
                        {pageSize}
                    </option>
                ))}
            </select>

            <UserTable promiseInProgress={promiseInProgress} users={sortedAndSearchedUsers}
                       userFromDB={setUserFromDB} remove={removeUser} enable={updateUser}
                       modalVisible={setModal} setAlert={setAlert}/>

            <Pagination count={totalPages} page={page} onChange={changePage} shape="rounded"/>

            <MyNotification open={alert.open} setOpen={setAlert} message={alert.message} severity={alert.severity}/>
        </div>
    );
};

export default UsersList;