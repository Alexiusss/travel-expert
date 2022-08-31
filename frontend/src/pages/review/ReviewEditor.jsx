import React, {useState} from 'react';
import reviewService from '../../services/ReviewService'
import {useTranslation} from "react-i18next";
import {Container} from "@material-ui/core";
import {useAuth} from "../../components/hooks/UseAuth";

const ReviewEditor = (props) => {
    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [rating, setRating] = useState('');
    const [id, setId] = useState(null);
    const [userId, setUserId] = useState('');
    const itemId = props.itemId;
    const {t} = useTranslation();
    const {authUserId} = useAuth();

    const saveReview = (e) => {
        e.preventDefault();
        console.log(itemId)
        const userId = authUserId;
        const review = {title, description, rating, id, itemId, userId}

        reviewService.create(review)
            .then()
            .catch(console.error)
    }

    return (
        <Container>
            <h4>{t("review editor")}</h4>
            <hr/>
            <form>
                <div className="form-group">
                    <input
                        type="text"
                        className="form-control col-4"
                        id="title"
                        value={title}
                        onChange={e => setTitle(e.target.value)}
                        placeholder={t("enter title")}
                    />
                </div>
                <div className="form-group" style={{marginTop: 5}}>
                    <input
                        type="text"
                        className="form-control col-4"
                        id="description"
                        value={description}
                        onChange={e => setDescription(e.target.value)}
                        placeholder={t("enter description")}
                    />
                </div>
                <div className="form-group" style={{marginTop: 5}}>
                    <input
                        type="text"
                        className="form-control col-4"
                        id="rating"
                        value={rating}
                        onChange={e => setRating(e.target.value)}
                        placeholder={t("enter rating")}
                        // https://stackoverflow.com/a/65138192
                        onKeyPress={(event) => {
                            if (!/[1-5]/.test(event.key)) {
                                event.preventDefault();
                            }
                        }}
                    />
                </div>
                <div>
                    <button onClick={(e => saveReview(e))} className="btn btn-outline-primary ml-2 btn-sm"
                            style={{marginTop: 10}}>{t("save")}
                    </button>
                </div>
            </form>
        </Container>
    );
};

export default ReviewEditor;