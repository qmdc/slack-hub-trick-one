import {KeyOutlined} from "@ant-design/icons";
import lazyLoad from "../lazyLoad";
import React, {lazy} from "react";
import {MenuRouteObject} from "../router";

const passwordManager: MenuRouteObject = {
    path: "password-manager",
    label: "menu.password manager",
    icon: <KeyOutlined/>,
    children: [
        {
            path: "password-list",
            label: "menu.password list",
            icon: <KeyOutlined/>,
            element: lazyLoad(lazy(() => import("../../pages/PasswordManager/PasswordList")))
        }
    ] as MenuRouteObject[]
}

export default passwordManager
