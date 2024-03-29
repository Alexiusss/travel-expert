import React, {useEffect, useState} from 'react';
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
import {Rating} from "@material-ui/lab";
import reviewService from '../../services/ReviewService';
import AlertDialog from "../UI/modal/AlertDialog";


const ItemCard = (props) => {
    const {
        itemId = "",
        item = {},
        route,
        update = Function.prototype,
        remove = Function.prototype
    } = props;
    const {isAdmin} = useAuth();
    const {t} = useTranslation();
    const [ratingValue, setRatingValue] = useState(0);
    const image = API_URL + IMAGE_ROUTE + `${item.fileNames[0]}`;
    const [isDialogOpened, setDialogOpen] = useState(false);
    const [isDeleteConfirmed, setDeleteConfirmation] = useState(false);

    const openDialog = (e) => {
        e.preventDefault();
        setDialogOpen(true)
    }

    useEffect(() => {
        reviewService.getRatingValueByItemId(itemId)
            .then(({data}) => {
                setRatingValue(+data)
            });
    }, [])

    useEffect(() => {
        if (isDeleteConfirmed) {
            remove(item)
        }
    }, [isDeleteConfirmed])

    return (
        <Grid item xs={12} sm={6} md={3} key={item.id}>
            <Link
                to={{
                    pathname: `${route}${item.name.replaceAll(" ", "_")}-${itemId}`,
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
                            <Rating name="half-rating-read" value={ratingValue} precision={0.1} size="medium" readOnly/>
                        </CardContent>
                    </CardActionArea>
                    {isAdmin ?
                        <>
                            <div style={{float: 'right'}}>
                                <MyButton className="btn btn-outline-info btn-sm" onClick={(e) =>
                                    update(e, item)
                                }>
                                    {t("edit")}
                                </MyButton>
                                {' '}
                                <MyButton className="btn btn-outline-danger btn-sm"
                                          onClick={e => openDialog(e)}>
                                    {t("delete")}</MyButton>
                            </div>
                            {isDialogOpened &&
                                <AlertDialog isOpened={isDialogOpened} setOpen={setDialogOpen} title={t("are you sure")}
                                             confirm={setDeleteConfirmation}/>
                            }
                        </>
                        :
                        null
                    }
                </Card>
            </Link>
        </Grid>
    );
};

export default ItemCard;