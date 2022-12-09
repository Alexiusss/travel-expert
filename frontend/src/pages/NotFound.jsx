import {useTranslation} from "react-i18next";

function NotFound() {
    const {t} = useTranslation();

    return (
        <div className="container">
        <h4>{t('page doesn\'t exist')}</h4>
        </div>
    );
}

export { NotFound };
