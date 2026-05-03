import {BookOutlined, TagOutlined} from "@ant-design/icons";
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
        },
        {
            path: "category-list",
            label: "menu.category list",
            icon: <TagOutlined/>,
            element: lazyLoad(lazy(() => import("../../pages/QuoteCollector/CategoryList")))
        }
    ] as MenuRouteObject[]
}

export default quoteCollector