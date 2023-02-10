import React from 'react';
import {Autocomplete} from "@material-ui/lab";
import {TextField} from "@material-ui/core";

const ItemInput = ({items = [], setItems = Function.prototype, name = ''}) => {
    return (
        <div style={{marginTop: 5}}>
            <Autocomplete
                multiple
                limitTags={2}
                options={testItems}
                //getOptionLabel={(option) => option}
                onChange={() => setItems}
                defaultValue={items}
                renderInput={(params) => (
                    <TextField {...params} label={name} style={{width: 400}}/>
                )}
            />
        </div>
    );
};

export default ItemInput;

const testItems = ['item1', 'item2', 'item3', 'item4', 'item4']