import React from 'react';
import {Popover} from "@material-ui/core";
import ItemRating from "../../components/items/ItemRating";
import {Link} from "react-router-dom";
import {PROFILE} from "../../utils/consts";

const ProfilePopover = (props) => {
    const {anchorEl, setAnchorEl, authorId, authorName, username, rating} = props;

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
                    <Link to={{
                        pathname: `${PROFILE}${username}`,
                        state: {authorId: authorId}
                    }}
                    >{authorName || username}</Link>
                </div>
                <div style={{padding: 7}}>
                    <ItemRating rating={rating} setRatingFilter={null}/>
                </div>
            </Popover>
        </div>
    );
};

export default ProfilePopover;