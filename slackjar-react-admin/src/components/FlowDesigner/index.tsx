import React, {useCallback, useRef, useState, useMemo, useEffect, forwardRef, useImperativeHandle} from 'react'
import {
    ReactFlow,
    ReactFlowProvider,
    addEdge,
    useNodesState,
    useEdgesState,
    Controls,
    Background,
    MiniMap,
    Connection,
    Edge,
    Node,
    BackgroundVariant,
    useReactFlow,
} from '@xyflow/react'
import {nanoid} from 'nanoid'
import '@xyflow/react/dist/style.css'
import NodePanel from './NodePanel'
import PropertyPanel from './PropertyPanel'
import StartNode from './CustomNodes/StartNode'
import UserInputNode from './CustomNodes/UserInputNode'
import AiReplyNode from './CustomNodes/AiReplyNode'
import ConditionNode from './CustomNodes/ConditionNode'
import EndNode from './CustomNodes/EndNode'
import type {FlowData, FlowNode} from '../../apis/modules/dialogflow'
import styles from './flowDesigner.module.scss'

/**
 * 流程设计器主组件
 * 提供可视化的节点拖拽、连线、配置功能
 */

export interface FlowDesignerRef {
    zoomIn: () => void
    zoomOut: () => void
    fitView: () => void
    setData: (data: FlowData) => void
    getData: () => FlowData
}

interface FlowDesignerProps {
    initialData?: FlowData
    onChange?: (data: FlowData) => void
    onSave?: () => void
}

const nodeTypes = {
    start: StartNode,
    userInput: UserInputNode,
    aiReply: AiReplyNode,
    condition: ConditionNode,
    end: EndNode,
}

const nodeDefaultLabels: Record<string, string> = {
    start: '开始',
    userInput: '用户输入',
    aiReply: 'AI回复',
    condition: '条件分支',
    end: '结束',
}

const FlowDesignerContent = forwardRef<FlowDesignerRef, FlowDesignerProps>(({initialData, onChange}, ref) => {
    const reactFlowWrapper = useRef<HTMLDivElement>(null)
    const {zoomIn, zoomOut, fitView: rfFitView, setViewport} = useReactFlow()
    const [selectedNode, setSelectedNode] = useState<Node | null>(null)
    const isInitialized = useRef(false)

    const getInitialNodes = useCallback((data?: FlowData): Node[] => {
        if (data?.nodes?.length) {
            return data.nodes.map((node) => ({
                id: node.id,
                type: node.type,
                position: node.position,
                data: node.data,
            })) as Node[]
        }
        return [
            {
                id: 'start_1',
                type: 'start',
                position: {x: 100, y: 100},
                data: {label: '开始'},
            },
        ]
    }, [])

    const getInitialEdges = useCallback((data?: FlowData): Edge[] => {
        if (data?.edges?.length) {
            return data.edges.map((edge) => ({
                ...edge,
                animated: true,
            })) as Edge[]
        }
        return []
    }, [])

    const [nodes, setNodes, onNodesChange] = useNodesState(getInitialNodes(initialData))
    const [edges, setEdges, onEdgesChange] = useEdgesState(getInitialEdges(initialData))

    useEffect(() => {
        if (initialData && !isInitialized.current) {
            isInitialized.current = true
        }
    }, [initialData])

    useEffect(() => {
        if (onChange) {
            const flowData: FlowData = {
                nodes: nodes.map((node) => ({
                    id: node.id,
                    type: node.type || 'default',
                    position: node.position,
                    data: node.data,
                })) as FlowNode[],
                edges: edges.map((edge) => ({
                    id: edge.id,
                    source: edge.source,
                    target: edge.target,
                    sourceHandle: edge.sourceHandle,
                    targetHandle: edge.targetHandle,
                    animated: edge.animated,
                })),
            }
            onChange(flowData)
        }
    }, [nodes, edges, onChange])

    useImperativeHandle(ref, () => ({
        zoomIn: () => {
            zoomIn({duration: 300})
        },
        zoomOut: () => {
            zoomOut({duration: 300})
        },
        fitView: () => {
            rfFitView({duration: 300, padding: 0.2})
        },
        setData: (data: FlowData) => {
            setNodes(getInitialNodes(data))
            setEdges(getInitialEdges(data))
        },
        getData: (): FlowData => ({
            nodes: nodes.map((node) => ({
                id: node.id,
                type: node.type || 'default',
                position: node.position,
                data: node.data,
            })) as FlowNode[],
            edges: edges.map((edge) => ({
                id: edge.id,
                source: edge.source,
                target: edge.target,
                sourceHandle: edge.sourceHandle,
                targetHandle: edge.targetHandle,
                animated: edge.animated,
            })),
        }),
    }), [zoomIn, zoomOut, rfFitView, setNodes, setEdges, getInitialNodes, getInitialEdges, nodes, edges])

    const onConnect = useCallback(
        (params: Connection) => {
            const newEdge: Edge = {
                ...params,
                id: `edge_${nanoid(8)}`,
                animated: true,
                type: 'smoothstep',
            }
            setEdges((eds) => addEdge(newEdge, eds))
        },
        [setEdges]
    )

    const onDragStart = (event: React.DragEvent, nodeType: string) => {
        event.dataTransfer.setData('application/reactflow/type', nodeType)
        event.dataTransfer.effectAllowed = 'move'
    }

    const onDragOver = useCallback((event: React.DragEvent) => {
        event.preventDefault()
        event.dataTransfer.dropEffect = 'move'
    }, [])

    const onDrop = useCallback(
        (event: React.DragEvent) => {
            event.preventDefault()

            const nodeType = event.dataTransfer.getData('application/reactflow/type')

            if (typeof nodeType === 'undefined' || !nodeType) {
                return
            }

            const bounds = reactFlowWrapper.current?.getBoundingClientRect()
            if (!bounds) return

            const position = {
                x: event.clientX - bounds.left,
                y: event.clientY - bounds.top,
            }

            const newNode: Node = {
                id: `${nodeType}_${nanoid(8)}`,
                type: nodeType,
                position,
                data: {label: nodeDefaultLabels[nodeType] || '新节点'},
            }

            setNodes((nds) => nds.concat(newNode))
        },
        [setNodes]
    )

    const onNodeClick = useCallback((_: React.MouseEvent, node: Node) => {
        setSelectedNode(node)
    }, [])

    const onPaneClick = useCallback(() => {
        setSelectedNode(null)
    }, [])

    const handleUpdateNode = useCallback((nodeId: string, updates: Partial<Node>) => {
        setNodes((nds) =>
            nds.map((node) => {
                if (node.id === nodeId) {
                    return {...node, ...updates}
                }
                return node
            })
        )
        setSelectedNode((prev) =>
            prev?.id === nodeId ? {...prev, ...updates} : prev
        )
    }, [setNodes])

    const handleDeleteNode = useCallback((nodeId: string) => {
        setNodes((nds) => nds.filter((node) => node.id !== nodeId))
        setEdges((eds) => eds.filter((edge) => edge.source !== nodeId && edge.target !== nodeId))
        setSelectedNode(null)
    }, [setNodes, setEdges])

    return (
        <div className={styles['flow-designer']}>
            <NodePanel onDragStart={onDragStart}/>
            <div className={styles['canvas-wrapper']} ref={reactFlowWrapper}>
                <ReactFlow
                    nodes={nodes}
                    edges={edges}
                    onNodesChange={onNodesChange}
                    onEdgesChange={onEdgesChange}
                    onConnect={onConnect}
                    onDrop={onDrop}
                    onDragOver={onDragOver}
                    onNodeClick={onNodeClick}
                    onPaneClick={onPaneClick}
                    nodeTypes={nodeTypes}
                    fitView
                    snapToGrid
                    snapGrid={[15, 15]}
                >
                    <Controls/>
                    <MiniMap
                        nodeStrokeColor={(n) => {
                            if (n.type === 'start') return '#52c41a'
                            if (n.type === 'end') return '#ff4d4f'
                            if (n.type === 'condition') return '#fa8c16'
                            if (n.type === 'aiReply') return '#722ed1'
                            return '#1890ff'
                        }}
                        nodeColor={(n) => {
                            if (n.type === 'start') return '#52c41a'
                            if (n.type === 'end') return '#ff4d4f'
                            if (n.type === 'condition') return '#fa8c16'
                            if (n.type === 'aiReply') return '#722ed1'
                            return '#1890ff'
                        }}
                    />
                    <Background variant={BackgroundVariant.Dots} gap={20} size={1}/>
                </ReactFlow>
            </div>
            <PropertyPanel
                selectedNode={selectedNode}
                onUpdateNode={handleUpdateNode}
                onDeleteNode={handleDeleteNode}
            />
        </div>
    )
})

FlowDesignerContent.displayName = 'FlowDesignerContent'

const FlowDesigner: React.FC<FlowDesignerProps & {ref?: React.Ref<FlowDesignerRef>}> = (props) => {
    return (
        <ReactFlowProvider>
            <FlowDesignerContent {...props}/>
        </ReactFlowProvider>
    )
}

export default forwardRef<FlowDesignerRef, FlowDesignerProps>((props, ref) => {
    return (
        <ReactFlowProvider>
            <FlowDesignerContent {...props} ref={ref}/>
        </ReactFlowProvider>
    )
})
