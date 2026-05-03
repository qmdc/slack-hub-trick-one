import {BookOutlined} from "@ant-design/icons";
import lazyLoad from "../lazyLoad";
import React, {lazy} from "react";
import {MenuRouteObject} from "../router";

const quoteCollector: MenuRouteObject = {
    path: "quote-collector",
    label: "menu.quote collector",
    icon: <BookOutlined/>,
    children: [
        {
            path: "quote-list",
            label: "menu.quote list",
            icon: <BookOutlined/>,
            element: lazyLoad(lazy(() => import("../../pages/QuoteCollector/QuoteList")))
        }
    ] as MenuRouteObject[]
}

export default quoteCollector