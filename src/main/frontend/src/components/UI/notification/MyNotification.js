import React, {useRef} from 'react';
import Snackbar from "@material-ui/core/Snackbar";
import MuiAlert from "@material-ui/lab/Alert";
import AlertTitle from '@material-ui/lab/AlertTitle';

const Alert = React.forwardRef(function Alert(props, ref) {
    return <MuiAlert elevation={6} ref={ref} variant="filled" {...props} />;
});

const MyNotification = (props) => {
    let messages;
    if (props.message) {
        messages = props.message.split('\n');
    }

    return (
        <React.Fragment>
            <Snackbar open={props.open} autoHideDuration={3000} onClose={() => props.setOpen(false)}
                      anchorOrigin={{vertical: "bottom", horizontal: "right"}}>
                <Alert severity={props.severity}>
                    {messages ?
                        messages.map((msg, index) =>
                            <AlertTitle key={index}>{msg}</AlertTitle>
                        ) : ""}
                </Alert>
            </Snackbar>
        </React.Fragment>
    );
};

export default MyNotification;