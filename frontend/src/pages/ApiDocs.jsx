import React from 'react';
import MyButton from "../components/UI/button/MyButton";
import {
    HOTEL_API_DOCS_URL,
    IMAGE_API_DOCS_URL,
    RESTAURANT_API_DOCS_URL,
    REVIEW_API_DOCS_URL,
    USER_API_DOCS_URL
} from "../utils/consts";

const ApiDocs = () => {
    return (
        <main className="container py-5">
            <div className="text-center">
                <div className="btn-group-vertical gap-5">
                    <MyButton className={"btn btn-outline-success ml-2 btn-lg"}
                              onClick={() => window.open(HOTEL_API_DOCS_URL)}
                    >
                        Hotel API Documentation
                    </MyButton>
                    <MyButton className={"btn btn-outline-success ml-2 btn-lg"}
                              onClick={() => window.open(RESTAURANT_API_DOCS_URL)}
                    >
                        Restaurant API Documentation
                    </MyButton>
                    <MyButton className={"btn btn-outline-success ml-2 btn-lg"}
                              onClick={() => window.open(REVIEW_API_DOCS_URL)}
                    >
                        Review API Documentation
                    </MyButton>
                    <MyButton className={"btn btn-outline-success ml-2 btn-lg"}
                              onClick={() => window.open(IMAGE_API_DOCS_URL)}
                    >
                        Image API Documentation
                    </MyButton>
                    <MyButton className={"btn btn-outline-success ml-2 btn-lg"}
                              onClick={() => window.open(USER_API_DOCS_URL)}
                    >
                        User API Documentation
                    </MyButton>
                </div>
            </div>
        </main>
    );
};

export default ApiDocs;