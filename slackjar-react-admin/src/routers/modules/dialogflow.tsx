import {MessageOutlined, BranchesOutlined} from "@ant-design/icons";
import lazyLoad from "../lazyLoad";
import React, {lazy} from "react";
import {MenuRouteObject} from "../router";

const dialogflow: MenuRouteObject = {
    path: "dialogflow",
    label: "menu.dialogflow",
    icon: <MessageOutlined/>,
    hidden: true,
    children: [
        {
            path: "flow-list",
            label: "menu.flow list",
            icon: <BranchesOutlined/>,
            element: lazyLoad(lazy(() => import("../../pages/DialogFlow/FlowList")))
        },
        {
            path: "designer/:id?",
            label: "menu.flow designer",
            icon: <BranchesOutlined/>,
            hidden: true,
            element: lazyLoad(lazy(() => import("../../pages/DialogFlow/FlowDesignerPage")))
        }
    ] as MenuRouteObject[]
}

export default dialogflow;
