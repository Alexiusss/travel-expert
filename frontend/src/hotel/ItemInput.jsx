import React from 'react';
import {Autocomplete} from "@material-ui/lab";
import {TextField} from "@material-ui/core";
import {useTranslation} from "react-i18next";

const ItemInput = ({items = [], setItems = Function.prototype, name = '', options = []}) => {
    const {t} = useTranslation(['translation', 'hotel']);

    return (
        <div style={{marginTop: 5}}>
            <Autocomplete
                //  https://stackoverflow.com/a/72136454
                value={items || null}
                multiple
                limitTags={2}
                options={options}
                getOptionLabel={(option) => t(option, {ns: 'hotel'})}
                onChange={(e, value) => setItems(value)}
                defaultValue={items}
                renderInput={(params) => (
                    <TextField {...params} label={name} style={{width: 400}}/>
                )}
            />
        </div>
    );
};

export default ItemInput;