import {t} from "i18next";

export const USERS_ROUTE = '/users/'
export const LOGIN = '/auth/login/'

export const getLocalizedErrorMessages = (messages) => {
    return messages.split('\n').map(message => {
        let splitMessage = message.match('(?<=(\\[\\w*\\] )).+');
        if(splitMessage) {
            return splitMessage[1] + t(splitMessage[0]);
        }
        return t(message);
    });
}