import $api from "../http/http-common";
import {IMAGE_ROUTE} from "../utils/consts";

const get = (id) => {
    return $api.get(IMAGE_ROUTE + `${id}`);
}

const post = async (files) => {
    let formData = new FormData();

    for (let i = 0; i < files.length; i++) {
        formData.append("files", files[i]);
    }

    return $api.post(IMAGE_ROUTE, formData, {
        headers: {
            "Content-Type": "multipart/form-data",
        },
    })
}

const remove = (fileName) => {
    return $api.delete(IMAGE_ROUTE + `${fileName}`)
}

const removeAllByFileNames = (fileNames) => {
    return $api.post(IMAGE_ROUTE + "fileNames", fileNames, {
        params : {
            "action": "delete",
        }
    });
}

export default {get, post, remove, removeAllByFileNames}