import React, {useState, useRef, useEffect, useCallback} from 'react'
import {Button, Input} from 'antd'
import {CloseOutlined, RobotOutlined, UserOutlined, SendOutlined} from '@ant-design/icons'
import type {FlowData, NodeConfig, FlowNode} from '../../../apis/modules/dialogflow'
import styles from './previewPanel.module.scss'

/**
 * 对话预览面板组件
 * 实时预览对话流程效果
 */

interface PreviewPanelProps {
    flowData: FlowData | null
    onClose: () => void
}

interface Message {
    id: string
    type: 'user' | 'ai'
    content: string
    options?: {label: string; value: string}[]
    cardData?: {
        title: string
        description: string
        imageUrl?: string
        buttons?: {text: string; type: 'link' | 'action'; value: string}[]
    }
}

const PreviewPanel: React.FC<PreviewPanelProps> = ({flowData, onClose}) => {
    const [messages, setMessages] = useState<Message[]>([])
    const [inputValue, setInputValue] = useState('')
    const [currentNodeId, setCurrentNodeId] = useState<string | null>(null)
    const [waitingForInput, setWaitingForInput] = useState(false)
    const [inputType, setInputType] = useState<'text' | 'options'>('text')
    const [options, setOptions] = useState<{label: string; value: string}[]>([])
    const messagesEndRef = useRef<HTMLDivElement>(null)

    const scrollToBottom = useCallback(() => {
        messagesEndRef.current?.scrollIntoView({behavior: 'smooth'})
    }, [])

    useEffect(() => {
        scrollToBottom()
    }, [messages, scrollToBottom])

    useEffect(() => {
        if (flowData) {
            startPreview()
        }
    }, [flowData])

    const addMessage = useCallback((message: Message) => {
        setMessages((prev) => [...prev, message])
    }, [])

    const findNodeById = useCallback((nodeId: string): FlowNode | undefined => {
        return flowData?.nodes?.find((n) => n.id === nodeId)
    }, [flowData])

    const findNextNode = useCallback((fromNodeId: string, handle?: string): string | null => {
        const edge = flowData?.edges?.find((e) => {
            if (e.source === fromNodeId) {
                if (handle) {
                    return e.sourceHandle === handle
                }
                return true
            }
            return false
        })
        return edge?.target || null
    }, [flowData])

    const executeNode = useCallback((nodeId: string) => {
        const node = findNodeById(nodeId)
        if (!node) {
            addMessage({
                id: `msg_${Date.now()}`,
                type: 'ai',
                content: '❌ 流程执行出错：找不到节点',
            })
            return
        }

        setCurrentNodeId(nodeId)
        const config = (node.data.config as NodeConfig) || {}

        switch (node.type) {
            case 'start':
                addMessage({
                    id: `msg_${Date.now()}`,
                    type: 'ai',
                    content: '▶️ 对话开始',
                })
                const nextStart = findNextNode(nodeId)
                if (nextStart) {
                    setTimeout(() => executeNode(nextStart), 500)
                }
                break

            case 'userInput':
                setWaitingForInput(true)
                setInputType(config.inputType || 'text')
                setOptions(config.options || [])
                addMessage({
                    id: `msg_${Date.now()}`,
                    type: 'ai',
                    content: config.placeholder || '请输入您的回复',
                    options: config.options,
                })
                break

            case 'aiReply':
                if (config.messageType === 'card') {
                    addMessage({
                        id: `msg_${Date.now()}`,
                        type: 'ai',
                        content: '',
                        cardData: {
                            title: config.cardTitle || '卡片消息',
                            description: config.cardDescription || '',
                            imageUrl: config.cardImageUrl,
                            buttons: config.cardButtons,
                        },
                    })
                } else {
                    addMessage({
                        id: `msg_${Date.now()}`,
                        type: 'ai',
                        content: config.content || 'AI回复内容',
                    })
                }
                const nextReply = findNextNode(nodeId)
                if (nextReply) {
                    setTimeout(() => executeNode(nextReply), 800)
                }
                break

            case 'condition':
                const conditions = config.conditions || []
                let matchedTarget: string | null = null

                for (const condition of conditions) {
                    try {
                        if (evalCondition(condition.expression)) {
                            matchedTarget = condition.targetNodeId
                            addMessage({
                                id: `msg_${Date.now()}`,
                                type: 'ai',
                                content: `🔀 满足条件：${condition.description || condition.expression}`,
                            })
                            break
                        }
                    } catch (e) {
                        console.error('条件判断出错:', e)
                    }
                }

                if (!matchedTarget) {
                    matchedTarget = config.defaultTarget || null
                    if (matchedTarget) {
                        addMessage({
                            id: `msg_${Date.now()}`,
                            type: 'ai',
                            content: '🔀 走默认分支',
                        })
                    }
                }

                if (matchedTarget) {
                    setTimeout(() => executeNode(matchedTarget!), 500)
                } else {
                    addMessage({
                        id: `msg_${Date.now()}`,
                        type: 'ai',
                        content: '⚠️ 条件分支没有匹配的目标节点',
                    })
                }
                break

            case 'end':
                addMessage({
                    id: `msg_${Date.now()}`,
                    type: 'ai',
                    content: '🏁 对话结束',
                })
                setWaitingForInput(false)
                break

            default:
                const nextDefault = findNextNode(nodeId)
                if (nextDefault) {
                    setTimeout(() => executeNode(nextDefault), 500)
                }
        }
    }, [findNodeById, findNextNode, addMessage])

    const evalCondition = (expression: string): boolean => {
        if (!expression) return false
        try {
            return new Function(`return ${expression}`)()
        } catch {
            return false
        }
    }

    const startPreview = () => {
        setMessages([])
        setWaitingForInput(false)
        setCurrentNodeId(null)

        const startNode = flowData?.nodes?.find((n) => n.type === 'start')
        if (startNode) {
            setTimeout(() => executeNode(startNode.id), 300)
        } else {
            addMessage({
                id: `msg_${Date.now()}`,
                type: 'ai',
                content: '⚠️ 没有找到开始节点，请先添加一个开始节点',
            })
        }
    }

    const handleSend = () => {
        if (!inputValue.trim() || !waitingForInput) return

        addMessage({
            id: `msg_${Date.now()}`,
            type: 'user',
            content: inputValue,
        })
        setInputValue('')
        setWaitingForInput(false)

        const nextNode = findNextNode(currentNodeId!)
        if (nextNode) {
            setTimeout(() => executeNode(nextNode), 500)
        }
    }

    const handleOptionClick = (option: {label: string; value: string}) => {
        if (!waitingForInput) return

        addMessage({
            id: `msg_${Date.now()}`,
            type: 'user',
            content: option.label,
        })
        setWaitingForInput(false)

        const nextNode = findNextNode(currentNodeId!)
        if (nextNode) {
            setTimeout(() => executeNode(nextNode), 500)
        }
    }

    const handleKeyPress = (e: React.KeyboardEvent) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault()
            handleSend()
        }
    }

    return (
        <div className={styles['preview-panel']}>
            <div className={styles['preview-header']}>
                <span className={styles['preview-title']}>对话预览</span>
                <Button type="text" icon={<CloseOutlined/>} onClick={onClose}/>
            </div>
            <div className={styles['preview-content']}>
                <div className={styles['message-list']}>
                    {messages.map((msg) => (
                        <div
                            key={msg.id}
                            className={`${styles['message']} ${msg.type === 'user' ? styles['user-message'] : styles['ai-message']}`}
                        >
                            <div
                                className={`${styles['avatar']} ${msg.type === 'user' ? styles['user-avatar'] : styles['ai-avatar']}`}
                            >
                                {msg.type === 'user' ? <UserOutlined/> : <RobotOutlined/>}
                            </div>
                            {msg.cardData ? (
                                <div className={styles['card-message']}>
                                    {msg.cardData.imageUrl && (
                                        <img
                                            src={msg.cardData.imageUrl}
                                            alt={msg.cardData.title}
                                            className={styles['card-image']}
                                        />
                                    )}
                                    <div className={styles['card-content']}>
                                        <div className={styles['card-title']}>{msg.cardData.title}</div>
                                        <div className={styles['card-desc']}>{msg.cardData.description}</div>
                                        {msg.cardData.buttons && msg.cardData.buttons.length > 0 && (
                                            <div className={styles['card-buttons']}>
                                                {msg.cardData.buttons.map((btn, idx) => (
                                                    <div key={idx} className={styles['card-button']}>
                                                        {btn.text}
                                                    </div>
                                                ))}
                                            </div>
                                        )}
                                    </div>
                                </div>
                            ) : (
                                <div
                                    className={`${styles['bubble']} ${msg.type === 'user' ? styles['user-bubble'] : styles['ai-bubble']}`}
                                >
                                    {msg.content}
                                </div>
                            )}
                            {msg.options && msg.options.length > 0 && waitingForInput && inputType === 'options' && (
                                <div className={styles['options-container']}>
                                    {msg.options.map((opt, idx) => (
                                        <div
                                            key={idx}
                                            className={styles['option-button']}
                                            onClick={() => handleOptionClick(opt)}
                                        >
                                            {opt.label}
                                        </div>
                                    ))}
                                </div>
                            )}
                        </div>
                    ))}
                    <div ref={messagesEndRef}/>
                </div>
            </div>
            <div className={styles['preview-input']}>
                <div className={styles['input-wrapper']}>
                    <Input
                        placeholder={waitingForInput ? '请输入消息...' : '对话已结束或等待中...'}
                        value={inputValue}
                        onChange={(e) => setInputValue(e.target.value)}
                        onKeyDown={handleKeyPress}
                        disabled={!waitingForInput || inputType === 'options'}
                    />
                    <Button
                        type="primary"
                        icon={<SendOutlined/>}
                        onClick={handleSend}
                        disabled={!waitingForInput || !inputValue.trim() || inputType === 'options'}
                    >
                        发送
                    </Button>
                </div>
            </div>
        </div>
    )
}

export default PreviewPanel
