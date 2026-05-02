import {MessageOutlined, FlowOutlined} from "@ant-design/icons";
import lazyLoad from "../lazyLoad";
import React, {lazy} from "react";
import {MenuRouteObject} from "../router";

const dialogflow: MenuRouteObject = {
    path: "dialogflow",
    label: "menu.dialogflow",
    icon: <MessageOutlined/>,
    children: [
        {
            path: "flow-list",
            label: "menu.flow list",
            icon: <FlowOutlined/>,
            element: lazyLoad(lazy(() => import("../../pages/DialogFlow/FlowList")))
        },
        {
            path: "designer/:id?",
            label: "menu.flow designer",
            icon: <FlowOutlined/>,
            hidden: true,
            element: lazyLoad(lazy(() => import("../../pages/DialogFlow/FlowDesignerPage")))
        }
    ] as MenuRouteObject[]
}

export default dialogflow;
