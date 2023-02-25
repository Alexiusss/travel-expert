import axios from "axios";
import {TRANSLATION_URL} from "../utils/consts";

const $api = axios.create({
    transformRequest: [(data) => JSON.stringify(data.data)],
    headers: {
         'Content-Type': 'application/json',
    }
});

export const translate = (text, from, to) => {
    return $api.post(TRANSLATION_URL, {
        data: {
            format: 'text',
            from: from,
            input: text,
            options: {
                contextResults: true,
                languageDetection: true,
                origin: 'reversomobile',
                sentenceSplitter: false,
            },
            to: to,
        },
    })
}

export const getTest = () => {
    // https://stackoverflow.com/a/53977372
    return $api.get('https://cors-anywhere.herokuapp.com/https://en.wikipedia.org/w/api.php?action=opensearch&format=json&search=fish');
}

export default {translate, getTest}