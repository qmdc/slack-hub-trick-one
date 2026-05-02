import request from '../request'
import type {PageResult, ResponseData} from './types'

/**
 * 分页查询对话流程列表
 */
export function pageQueryFlows(data: FlowPageQuery): Promise<ResponseData<PageResult<FlowItem>>> {
    return request.post('/dialogflow/flow/pageQuery', data)
}

/**
 * 获取对话流程详情
 */
export function getFlowDetail(id: number): Promise<ResponseData<FlowDetail>> {
    return request.get(`/dialogflow/flow/detail/${id}`)
}

/**
 * 保存对话流程
 */
export function saveFlow(data: FlowSaveRequest): Promise<ResponseData<number>> {
    return request.post('/dialogflow/flow/save', data)
}

/**
 * 删除对话流程
 */
export function deleteFlow(id: number): Promise<ResponseData<boolean>> {
    return request.delete(`/dialogflow/flow/delete/${id}`)
}

/**
 * 导出对话流程
 */
export function exportFlow(id: number): Promise<ResponseData<string>> {
    return request.get(`/dialogflow/flow/export/${id}`)
}

/**
 * 导入对话流程
 */
export function importFlow(jsonData: string): Promise<ResponseData<number>> {
    return request.post('/dialogflow/flow/import', jsonData)
}

/**
 * 获取所有节点类型
 */
export function getNodeTypes(): Promise<ResponseData<NodeType[]>> {
    return request.get('/dialogflow/node-type/list')
}

// ============================================
// 类型定义
// ============================================

/**
 * 对话流程分页查询参数
 */
export interface FlowPageQuery {
    pageNo?: number
    pageSize?: number
    name?: string
    description?: string
    status?: number
}

/**
 * 对话流程列表项
 */
export interface FlowItem {
    id: number
    name: string
    description: string
    status: number
    createTime: number
    updateTime: number
}

/**
 * 对话流程详情
 */
export interface FlowDetail {
    id: number
    name: string
    description: string
    flowData: FlowData
    status: number
    createTime: number
    updateTime: number
}

/**
 * 流程数据
 */
export interface FlowData {
    nodes: FlowNode[]
    edges: FlowEdge[]
    viewport?: Viewport
}

/**
 * 流程节点
 */
export interface FlowNode {
    id: string
    type: string
    position: Position
    data: NodeData
    width?: number
    height?: number
    selected?: boolean
    dragging?: boolean
}

/**
 * 节点数据
 */
export interface NodeData {
    label: string
    config?: NodeConfig
}

/**
 * 节点配置
 */
export interface NodeConfig {
    placeholder?: string
    inputType?: 'text' | 'options'
    options?: OptionItem[]
    content?: string
    messageType?: 'text' | 'card'
    cardTitle?: string
    cardDescription?: string
    cardImageUrl?: string
    cardButtons?: CardButton[]
    conditions?: ConditionItem[]
    defaultTarget?: string
}

/**
 * 选项项
 */
export interface OptionItem {
    label: string
    value: string
}

/**
 * 卡片按钮
 */
export interface CardButton {
    text: string
    type: 'link' | 'action'
    value: string
}

/**
 * 条件项
 */
export interface ConditionItem {
    id: string
    expression: string
    description: string
    targetNodeId: string
    params?: Record<string, any>
}

/**
 * 流程边
 */
export interface FlowEdge {
    id: string
    source: string
    target: string
    sourceHandle?: string
    targetHandle?: string
    type?: string
    animated?: boolean
    label?: string
    data?: Record<string, any>
    style?: Record<string, any>
}

/**
 * 位置
 */
export interface Position {
    x: number
    y: number
}

/**
 * 视口
 */
export interface Viewport {
    x: number
    y: number
    zoom: number
}

/**
 * 保存流程请求
 */
export interface FlowSaveRequest {
    id?: number
    name: string
    description?: string
    flowData: string
    status?: number
}

/**
 * 节点类型
 */
export interface NodeType {
    id: number
    typeCode: string
    typeName: string
    description: string
    icon: string
    color: string
    sortOrder: number
}
