import $api from "../http/http-common";
import {IMAGE_ROUTE} from "../utils/consts";

const get = (id) => {
    return $api.get(IMAGE_ROUTE + `${id}`);
}

const post = async (file) => {
    let formData = new FormData();
    formData.append("file", file);

    return $api.post(IMAGE_ROUTE, formData, {
        headers: {
            "Content-Type": "multipart/form-data",
        },
    })
}

export default {get, post}