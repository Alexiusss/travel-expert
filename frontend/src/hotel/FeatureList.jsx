import React from 'react';

const FeatureList = ({name = "", items = []}) => {
    return (
        <div>
            <h6>{name}</h6>
            <ul>
                <div className={'container'} style={{columns: '2 auto'}}>
                {items.map((item) =>
                    <li key={item + 'key'}>{item}</li>
                )}
                </div>
            </ul>
        </div>
    );
};

export default FeatureList;