import React, {useEffect, useState} from 'react';
import authService from 'services/AuthService.js'

const ProfilePage = () => {
    const [id, setId] = useState('');
    const [email, setEmail] = useState('');
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [isEnabled, setEnabled] = useState(false);

    useEffect(() => {
        authService.profile()
            .then(response => {
                setId(response.data.id);
                setEmail(response.data.email);
                setFirstName(response.data.firstName);
                setLastName(response.data.lastName);
                setEnabled(response.data.enabled);
            })
    });

    return (
        <div className="container justify-content-center align-items-center">
            <div className="card py-4" style={{border: "none"}}>
                <div className="d-flex justify-content-center align-items-center">
                    <div className="round-image">
                        <img src="https://i.imgur.com/Mn9kglf.jpg" className="rounded-circle" width="97"/>
                    </div>
                </div>
                <div className="text-center">
                    <h4 className="mt-3">{firstName + " " + lastName}</h4>
                    <span>{email}</span>
                    <br/>
                    <span>status: {isEnabled ? 'active' : 'inactive'}</span>
                </div>
            </div>
        </div>
    );
};

export default ProfilePage;