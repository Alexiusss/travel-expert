import React, {useState} from 'react';
import MyButton from "../components/button/MyButton";
import AddUser from "./AddUser";
import MyModal from "../components/modal/MyModal";

const UsersList = () => {

    const[modal, setModal] = useState(false)

    const createUser = () => {
        setModal(false)
    }

    return (
        <div>
            <MyButton style={{marginTop: 10}} className={"btn btn-outline-primary ml-2 btn-sm"} onClick={() => setModal(true)}>
                Добавить
            </MyButton>
            <MyModal visible={modal} setVisible={setModal}>
                <AddUser create={createUser}/>
            </MyModal>
            <hr style={{margin: '15px 0'}}/>

            Users List
        </div>
    );
};

export default UsersList;