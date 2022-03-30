import React, {useState, useRef} from 'react';
import {Link, useHistory, useParams} from "react-router-dom";
import userService from '../services/user.service'
import {USERS_ROUTE} from "../utils/consts";

const AddUser = ({create}) => {
    const [email, setEmail] = useState('');
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [password, setPassword] = useState('');
    const history = useHistory();
    const {id} = useParams();

    const cleanForm = () => {
        setEmail('');
        setFirstName('');
        setLastName('');
        setPassword('');
    };

    const saveUser = (e) => {
        e.preventDefault()

        const user = {email, firstName, lastName, password, id}
        if (id) {
            //update
        } else {
            userService.create(user)
                .then(response => {
                    console.log("user added successfully", response.data);
                    history.push(USERS_ROUTE);
                    create()
                    cleanForm()
                })
                .catch(error => {
                    console.log('something went wrong', error);
                })
        }
    }

    return (
        <div className='container'>
            <h4>User editor</h4>
            <hr/>
            <form>
                <div className="form-group">
                    <input
                        type="email"
                        className="form-control col-4"
                        id="email"
                        value={email}
                        onChange={(e => setEmail(e.target.value))}
                        placeholder="Enter email"
                    />
                </div>

                <div className="form-group" style={{marginTop: 5}}>
                    <input
                        type="text"
                        className="form-control col-4"
                        id="firstName"
                        value={firstName}
                        onChange={(e) => setFirstName(e.target.value)}
                        placeholder="Enter first name"
                    />
                </div>

                <div className="form-group" style={{marginTop: 5}}>
                    <input
                        type="text"
                        className="form-control col-4"
                        id="lastName"
                        value={lastName}
                        onChange={(e) => setLastName(e.target.value)}
                        placeholder="Enter last name"
                    />
                </div>

                <div className="form-group" style={{marginTop: 5}}>
                    <input
                        type="password"
                        className="form-control col-4"
                        id="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="Enter password"
                    />
                </div>
                <div>
                    <button onClick={(e => saveUser(e))} className="btn btn-outline-primary ml-2 btn-sm"
                            style={{marginTop: 10}}>Save
                    </button>
                </div>
            </form>
        </div>
    );
};

export default AddUser;