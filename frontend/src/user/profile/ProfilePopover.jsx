import React from 'react';
import {Popover} from "@material-ui/core";
import ItemRating from "../../components/items/ItemRating";
import {Link} from "react-router-dom";

const ProfilePopover = (props) => {
    const {anchorEl, setAnchorEl, authorName, rating} = props;

    const handleClose = () => {
        setAnchorEl(null);
    };

    const open = Boolean(anchorEl);
    const id = open ? "simple-popover" : undefined;

    return (
        <div>
            <Popover
                id={id}
                open={open}
                anchorEl={anchorEl}
                onClose={handleClose}
                anchorOrigin={{
                    vertical: 'center',
                    horizontal: 'right',
                }}
                transformOrigin={{
                    vertical: 'center',
                    horizontal: 'left',
                }}
            >
                <div style={{padding: 7}}>
                    <Link to='/profile/'>{authorName}</Link>
                </div>
                <div style={{padding: 7}}>
                    <ItemRating rating={rating} setRatingFilter={null}/>
                </div>
            </Popover>
        </div>
    );
};

export default ProfilePopover;