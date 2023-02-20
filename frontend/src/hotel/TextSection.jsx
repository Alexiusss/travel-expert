import React, {useState} from 'react';

const TextSection = ({text}) => {
    const [isFullView, setFullView] = useState(false);

    return (
        <div>
            <div>
                {isFullView ?
                    <>{text}</>
                    :
                    <>{text.slice(0, 300)}</>
                }
                <br/>
                <div style={{cursor: "pointer", fontWeight: 450}}>
                    {isFullView ?
                        <span onClick={() => setFullView(false)}>Show less ▲</span>
                        :
                        <span onClick={() => setFullView(true)}>Show more ▼</span>
                    }
                </div>
            </div>
            <hr/>
        </div>
    );
};

export default TextSection;