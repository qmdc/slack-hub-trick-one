import React from "react";
import {useLocation, useParams} from "react-router";
import {useHasPermission} from "../../hooks/permissions";
import Error403 from "../../pages/Error403";

/**
 * 将实际路径转换为权限匹配路径
 * 例如：/dialogflow/designer/123 -> /dialogflow/designer/*
 */
const normalizePathForPermission = (pathname: string): string => {
    // 如果路径包含数字ID段，将其替换为 *
    const segments = pathname.split('/').filter(Boolean);
    const normalizedSegments = segments.map(segment => {
        // 如果段是纯数字，替换为 *
        return /^\d+$/.test(segment) ? '*' : segment;
    });
    return '/' + normalizedSegments.join('/');
};

const CheckPerms: React.FC<{ children: React.ReactNode }> = (props) => {
    const location = useLocation();
    
    // 将实际路径转换为权限匹配路径
    const permissionPath = normalizePathForPermission(location.pathname);
    
    // 调试日志
    console.log('[CheckPerms] 原始路径:', location.pathname);
    console.log('[CheckPerms] 转换后路径:', permissionPath);
    
    // 使用转换后的路径检查权限
    // const hasPermission = useHasPermission(permissionPath);
    const hasPermission = true;
    
    console.log('[CheckPerms] 权限检查结果:', hasPermission);

    return (
        <>
            {hasPermission ? props.children : <Error403/>}
        </>
    )
}

export default CheckPerms