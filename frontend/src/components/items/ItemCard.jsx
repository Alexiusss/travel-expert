import React from 'react';
import {
    Grid,
    Card,
    CardContent,
    Typography,
    CardActionArea,
    CardMedia
} from "@material-ui/core";
import {Link} from "react-router-dom";
import MyButton from "../UI/button/MyButton";
import {useAuth} from "../hooks/UseAuth";
import {useTranslation} from "react-i18next";
import {IMAGE_ROUTE} from "../../utils/consts";
import {API_URL} from "../../http/http-common";


const ItemCard = (props) => {
    const {item, route} = props;
    const {isAdmin} = useAuth();
    const {t} = useTranslation();
    const image = API_URL + IMAGE_ROUTE + `${item.filename}`;

    return (
        <Grid item xs={12} sm={6} md={3} key={item.id}>
            <Link
                to={{
                    pathname: `${route}${item.name.replaceAll(" ", "_")}`,
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
                            image={image}
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
                                props.update(e, item)
                            }>
                                {t("edit")}
                            </MyButton>
                            {' '}
                            <MyButton className="btn btn-outline-danger btn-sm"
                                      onClick={e => props.remove(e, item)}>
                                {t("delete")}</MyButton>
                        </div>
                        :
                        ""
                    }
                </Card>
            </Link>

        </Grid>
    );
};

export default ItemCard;