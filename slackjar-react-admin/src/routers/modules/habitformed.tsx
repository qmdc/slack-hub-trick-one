import {HomeOutlined, AimOutlined, TeamOutlined, BarChartOutlined, TrophyOutlined} from "@ant-design/icons";
import lazyLoad from "../lazyLoad";
import React, {lazy} from "react";
import {MenuRouteObject} from "../router";

const habitformed: MenuRouteObject = {
    path: "habit",
    label: "menu.habit formed",
    icon: <AimOutlined/>,
    children: [
        {
            path: "home",
            label: "menu.habit home",
            icon: <HomeOutlined/>,
            element: lazyLoad(lazy(() => import("../../pages/HabitFormed/Home")))
        },
        {
            path: "goals",
            label: "menu.habit goals",
            icon: <AimOutlined/>,
            element: lazyLoad(lazy(() => import("../../pages/HabitFormed/GoalList")))
        },
        {
            path: "friends",
            label: "menu.habit friends",
            icon: <TeamOutlined/>,
            element: lazyLoad(lazy(() => import("../../pages/HabitFormed/FriendList")))
        },
        {
            path: "statistics",
            label: "menu.habit statistics",
            icon: <BarChartOutlined/>,
            element: lazyLoad(lazy(() => import("../../pages/HabitFormed/Statistics")))
        },
        {
            path: "achievements",
            label: "menu.habit achievements",
            icon: <TrophyOutlined/>,
            element: lazyLoad(lazy(() => import("../../pages/HabitFormed/Achievements")))
        }
    ] as MenuRouteObject[]
}

export default habitformed;
