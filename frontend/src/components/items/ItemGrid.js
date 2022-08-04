import React from 'react';
import {
    Grid,
    Card,
    CardContent,
    Typography,
    CardActionArea,
    CardMedia, IconButton
} from "@material-ui/core";
import {Link} from "react-router-dom";
import {useTranslation} from "react-i18next";
import MyButton from "../UI/button/MyButton";
import {useAuth} from "../hooks/UseAuth";

const ItemGrid = (props) => {

    const {isAdmin} = useAuth();
    const {t} = useTranslation();

    const removeItem = (e, item) => {
        e.preventDefault();
        props.remove(item)
    }

    const updateItem = (e, item) => {
        e.preventDefault()
        props.update(item)
    }

    return (
        <Grid container
              spacing={1}
              direction="row"
        >
            {props.items.map((item) =>
                <Grid item xs={12} sm={6} md={3} key={item.id}>
                    <Link
                        to={{
                            pathname: `${props.route}${item.name.replaceAll(" ", "_")}`,
                            // https://stackoverflow.com/a/63876129
                            state: {id: item.id}
                        }}
                        className="nav-link"
                    >
                        <Card>
                            <CardActionArea>
                                <CardMedia
                                    component="img"
                                    height="140"
                                    image="https://www.wilkinslinen.com/website/wp-content/uploads/2016/11/Depositphotos_2099183_m-2015-1.jpg"
                                    alt={item.name}
                                />

                                <CardContent>
                                    <Typography gutterBottom variant="h6" component="div">
                                        {item.name}
                                    </Typography>
                                </CardContent>
                            </CardActionArea>
                            {isAdmin ?
                                    <div style={{float: 'right'}}>
                                        <MyButton className="btn btn-outline-info btn-sm" onClick={(e) =>
                                            updateItem(e, item)
                                        }>
                                            {t("edit")}
                                        </MyButton>
                                        {' '}
                                        <MyButton className="btn btn-outline-danger btn-sm"
                                                  onClick={e => removeItem(e, item)}>
                                            {t("delete")}</MyButton>
                                    </div>
                                :
                                ""
                            }
                        </Card>
                    </Link>

                </Grid>
            )}
        </Grid>
    );
};

export default ItemGrid;