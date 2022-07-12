import React from 'react';
import Snackbar from "@material-ui/core/Snackbar";
import MuiAlert from "@material-ui/lab/Alert";
import AlertTitle from '@material-ui/lab/AlertTitle';

const Alert = React.forwardRef(function Alert(props, ref) {
    return <MuiAlert elevation={6} ref={ref} variant="filled" {...props} />;
});

const MyNotification = (props) => {
       return (
        <React.Fragment>
            <Snackbar open={props.open} autoHideDuration={3000} onClose={() => props.setOpen({...props, open: false})}
                      anchorOrigin={{vertical: "bottom", horizontal: "right"}}>
                <Alert severity={props.severity}>
                    {props.message ?
                        props.message.map((msg, index) =>
                            <AlertTitle key={index}>{msg}</AlertTitle>
                        ) : ""}
                </Alert>
            </Snackbar>
        </React.Fragment>
    );
};

export default MyNotification;