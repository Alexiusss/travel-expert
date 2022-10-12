import React from 'react';
import {
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogContentText,
    DialogTitle
} from "@material-ui/core";

const AlertDialog = (props) => {
    const {
        isOpened,
        title = "",
        description = "",
        setOpen = Function.prototype,
        confirm = Function.prototype
    } = props;

    const handleClose = () => {
        setOpen(false)
    }

    return (
        <div>
            <Dialog
                open={isOpened}
                onClose={handleClose}
                aria-labelledby="alert-dialog-title"
                aria-describedby="alert-dialog-description"
            >
                <DialogTitle id="alert-dialog-title">
                    {title}
                </DialogTitle>
                {description.length &&
                    <DialogContent>
                        <DialogContentText id="alert-dialog-description">
                            {description}
                        </DialogContentText>
                    </DialogContent>
                }
                <DialogActions>
                    <Button onClick={handleClose}>Disagree</Button>
                    <Button onClick={handleClose} autoFocus>
                        Agree
                    </Button>
                </DialogActions>
            </Dialog>
        </div>
    );
};

export default AlertDialog;