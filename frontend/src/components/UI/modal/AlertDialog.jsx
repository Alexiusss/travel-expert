import React from 'react';
import {
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogContentText,
    DialogTitle
} from "@material-ui/core";
import {useTranslation} from "react-i18next";

const AlertDialog = (props) => {
    const {t} = useTranslation();

    const {
        isOpened = false,
        title = "",
        description = "",
        setOpen = Function.prototype,
        confirm = Function.prototype
    } = props;

    const handleClose = (e) => {
        e.preventDefault();
        setOpen(false)
    }

    const handleConfirm = (e) => {
        e.preventDefault();
        confirm(true);
        setOpen(false);
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
                {description.length ?
                    <DialogContent>
                        <DialogContentText id="alert-dialog-description">
                            {description}
                        </DialogContentText>
                    </DialogContent>
                    :
                    null
                }
                <DialogActions>
                    <Button onClick={e => handleClose(e)}>{t("cancel")}</Button>
                    <Button onClick={e => handleConfirm(e)} autoFocus>
                        {t("confirm")}
                    </Button>
                </DialogActions>
            </Dialog>
        </div>
    );
};

export default AlertDialog;