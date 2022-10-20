import {useTranslation} from "react-i18next";

function NotFound() {
    const {t} = useTranslation();

    return <h4>{t('page doesn\'t exist')}</h4>;
}

export { NotFound };
