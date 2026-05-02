import React, {useState, useEffect, useCallback, useRef} from 'react'
import {useParams, useNavigate} from 'react-router'
import {Button, Space, Input, message, Modal, Tooltip} from 'antd'
import {
    SaveOutlined,
    ArrowLeftOutlined,
    PlayCircleOutlined,
    ExportOutlined,
    UndoOutlined,
    RedoOutlined,
    ZoomInOutlined,
    ZoomOutOutlined,
    FullscreenOutlined,
} from '@ant-design/icons'
import FlowDesigner from '../../../components/FlowDesigner'
import PreviewPanel from '../../../components/FlowDesigner/PreviewPanel'
import {getFlowDetail, saveFlow, exportFlow} from '../../../apis/modules/dialogflow'
import type {FlowData, FlowSaveRequest} from '../../../apis/modules/dialogflow'
import styles from '../dialogflow.module.scss'

/**
 * 对话流程设计器页面
 */

const {TextArea} = Input

const FlowDesignerPage: React.FC = () => {
    const params = useParams<{id: string}>()
    const navigate = useNavigate()
    const [loading, setLoading] = useState(false)
    const [flowName, setFlowName] = useState('新流程')
    const [flowDescription, setFlowDescription] = useState('')
    const [initialData, setInitialData] = useState<FlowData | null>(null)
    const [currentData, setCurrentData] = useState<FlowData | null>(null)
    const [showPreview, setShowPreview] = useState(false)
    const [showExportModal, setShowExportModal] = useState(false)
    const [exportJson, setExportJson] = useState('')
    const [history, setHistory] = useState<FlowData[]>([])
    const [historyIndex, setHistoryIndex] = useState(-1)
    const isInitialLoad = useRef(true)

    const flowId = params.id ? parseInt(params.id, 10) : null

    useEffect(() => {
        if (flowId && flowId > 0) {
            loadFlowData(flowId)
        }
    }, [flowId])

    const loadFlowData = async (id: number) => {
        setLoading(true)
        try {
            const res = await getFlowDetail(id)
            if (res?.code === 200 && res.data) {
                setFlowName(res.data.name)
                setFlowDescription(res.data.description || '')
                if (res.data.flowData) {
                    setInitialData(res.data.flowData)
                    setCurrentData(res.data.flowData)
                    setHistory([res.data.flowData])
                    setHistoryIndex(0)
                }
            }
        } catch (error) {
            message.error('加载流程失败')
            console.error(error)
        } finally {
            setLoading(false)
        }
    }

    const handleDataChange = useCallback(
        (data: FlowData) => {
            if (isInitialLoad.current) {
                isInitialLoad.current = false
                return
            }
            setCurrentData(data)
            setHistory((prev) => {
                const newHistory = prev.slice(0, historyIndex + 1)
                newHistory.push(data)
                if (newHistory.length > 50) {
                    newHistory.shift()
                } else {
                    setHistoryIndex(newHistory.length - 1)
                }
                return newHistory
            })
        },
        [historyIndex]
    )

    const handleUndo = () => {
        if (historyIndex > 0) {
            const newIndex = historyIndex - 1
            setHistoryIndex(newIndex)
            setCurrentData(history[newIndex])
        }
    }

    const handleRedo = () => {
        if (historyIndex < history.length - 1) {
            const newIndex = historyIndex + 1
            setHistoryIndex(newIndex)
            setCurrentData(history[newIndex])
        }
    }

    const handleSave = async () => {
        if (!flowName.trim()) {
            message.warning('请输入流程名称')
            return
        }
        if (!currentData) {
            message.warning('没有可保存的数据')
            return
        }
        setLoading(true)
        try {
            const request: FlowSaveRequest = {
                name: flowName,
                description: flowDescription,
                flowData: JSON.stringify(currentData),
                status: 1,
            }
            if (flowId && flowId > 0) {
                request.id = flowId
            }
            const res = await saveFlow(request)
            if (res?.code === 200) {
                message.success('保存成功')
                if (!flowId || flowId <= 0) {
                    navigate(`/dialogflow/designer/${res.data}`)
                }
            }
        } catch (error) {
            message.error('保存失败')
            console.error(error)
        } finally {
            setLoading(false)
        }
    }

    const handleExport = async () => {
        if (!currentData) {
            message.warning('没有可导出的数据')
            return
        }
        try {
            if (flowId && flowId > 0) {
                const res = await exportFlow(flowId)
                if (res?.code === 200) {
                    setExportJson(JSON.stringify(res.data, null, 2))
                    setShowExportModal(true)
                }
            } else {
                setExportJson(JSON.stringify(currentData, null, 2))
                setShowExportModal(true)
            }
        } catch (error) {
            message.error('导出失败')
            console.error(error)
        }
    }

    const handleDownloadJson = () => {
        const blob = new Blob([exportJson], {type: 'application/json'})
        const url = URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url
        a.download = `${flowName}_${Date.now()}.json`
        a.click()
        URL.revokeObjectURL(url)
    }

    const handlePreview = () => {
        if (!currentData || currentData.nodes.length === 0) {
            message.warning('请先添加节点')
            return
        }
        setShowPreview(true)
    }

    const handleBack = () => {
        navigate('/dialogflow/list')
    }

    const canUndo = historyIndex > 0
    const canRedo = historyIndex < history.length - 1

    return (
        <div className={styles['dialog-flow-container']}>
            <div className={styles['toolbar']}>
                <div className={styles['toolbar-left']}>
                    <Tooltip title="返回列表">
                        <Button icon={<ArrowLeftOutlined/>} onClick={handleBack}/>
                    </Tooltip>
                    <Input
                        placeholder="流程名称"
                        value={flowName}
                        onChange={(e) => setFlowName(e.target.value)}
                        style={{width: 200}}
                    />
                </div>
                <div className={styles['toolbar-right']}>
                    <Space>
                        <Tooltip title="撤销">
                            <Button
                                icon={<UndoOutlined/>}
                                onClick={handleUndo}
                                disabled={!canUndo}
                            />
                        </Tooltip>
                        <Tooltip title="重做">
                            <Button
                                icon={<RedoOutlined/>}
                                onClick={handleRedo}
                                disabled={!canRedo}
                            />
                        </Tooltip>
                        <Tooltip title="放大">
                            <Button icon={<ZoomInOutlined/>}/>
                        </Tooltip>
                        <Tooltip title="缩小">
                            <Button icon={<ZoomOutOutlined/>}/>
                        </Tooltip>
                        <Tooltip title="适应画布">
                            <Button icon={<FullscreenOutlined/>}/>
                        </Tooltip>
                    </Space>
                    <Divider type="vertical"/>
                    <Space>
                        <Button
                            icon={<PlayCircleOutlined/>}
                            onClick={handlePreview}
                        >
                            预览
                        </Button>
                        <Button icon={<ExportOutlined/>} onClick={handleExport}>
                            导出
                        </Button>
                        <Button
                            type="primary"
                            icon={<SaveOutlined/>}
                            onClick={handleSave}
                            loading={loading}
                        >
                            保存
                        </Button>
                    </Space>
                </div>
            </div>

            <div className={styles['main-content']}>
                <FlowDesigner
                    initialData={initialData || undefined}
                    onChange={handleDataChange}
                />
            </div>

            {showPreview && (
                <PreviewPanel
                    flowData={currentData}
                    onClose={() => setShowPreview(false)}
                />
            )}

            <Modal
                title="导出JSON"
                open={showExportModal}
                onCancel={() => setShowExportModal(false)}
                footer={[
                    <Button key="copy" onClick={() => {
                        navigator.clipboard.writeText(exportJson)
                        message.success('已复制到剪贴板')
                    }}>
                        复制
                    </Button>,
                    <Button key="download" type="primary" onClick={handleDownloadJson}>
                        下载文件
                    </Button>,
                ]}
                width={640}
            >
                <TextArea
                    value={exportJson}
                    readOnly
                    rows={20}
                    style={{fontFamily: 'monospace', fontSize: 12}}
                />
            </Modal>
        </div>
    )
}

export default FlowDesignerPage
