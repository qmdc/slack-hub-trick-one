import React, {useCallback} from 'react'
import {Form, Input, Select, Button, Space, InputNumber, Card, Divider} from 'antd'
import {DeleteOutlined, PlusOutlined} from '@ant-design/icons'
import type {Node} from 'reactflow'
import type {NodeConfig, OptionItem, ConditionItem, CardButton} from '../../../apis/modules/dialogflow'
import styles from './propertyPanel.module.scss'

/**
 * 属性面板组件
 * 用于编辑选中节点的属性配置
 */

interface PropertyPanelProps {
    selectedNode: Node | null
    onUpdateNode: (nodeId: string, updates: Partial<Node>) => void
    onDeleteNode: (nodeId: string) => void
}

const PropertyPanel: React.FC<PropertyPanelProps> = ({selectedNode, onUpdateNode, onDeleteNode}) => {
    const [form] = Form.useForm()

    const nodeTypeLabels: Record<string, string> = {
        start: '开始节点',
        userInput: '用户输入节点',
        aiReply: 'AI回复节点',
        condition: '条件分支节点',
        end: '结束节点'
    }

    const handleLabelChange = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
        if (selectedNode) {
            onUpdateNode(selectedNode.id, {
                data: {...selectedNode.data, label: e.target.value}
            })
        }
    }, [selectedNode, onUpdateNode])

    const handleConfigChange = useCallback((field: string, value: any) => {
        if (selectedNode) {
            const currentConfig = (selectedNode.data.config as NodeConfig) || {}
            onUpdateNode(selectedNode.id, {
                data: {
                    ...selectedNode.data,
                    config: {...currentConfig, [field]: value}
                }
            })
        }
    }, [selectedNode, onUpdateNode])

    const handleOptionsChange = useCallback((options: OptionItem[]) => {
        handleConfigChange('options', options)
    }, [handleConfigChange])

    const handleConditionsChange = useCallback((conditions: ConditionItem[]) => {
        handleConfigChange('conditions', conditions)
    }, [handleConfigChange])

    const handleCardButtonsChange = useCallback((cardButtons: CardButton[]) => {
        handleConfigChange('cardButtons', cardButtons)
    }, [handleConfigChange])

    if (!selectedNode) {
        return (
            <div className={styles['property-panel']}>
                <div className={styles['panel-header']}>
                    <span className={styles['panel-title']}>属性配置</span>
                </div>
                <div className={styles['empty-state']}>
                    <div className={styles['empty-icon']}>👆</div>
                    <div className={styles['empty-text']}>选择一个节点以编辑属性</div>
                </div>
            </div>
        )
    }

    const config = (selectedNode.data.config as NodeConfig) || {}

    return (
        <div className={styles['property-panel']}>
            <div className={styles['panel-header']}>
                <span className={styles['panel-title']}>
                    {nodeTypeLabels[selectedNode.type] || '节点属性'}
                </span>
                <Button
                    type="text"
                    danger
                    icon={<DeleteOutlined/>}
                    onClick={() => onDeleteNode(selectedNode.id)}
                >
                    删除
                </Button>
            </div>

            <div className={styles['panel-content']}>
                <Form layout="vertical" form={form}>
                    <Form.Item label="节点ID" className={styles['readonly-item']}>
                        <code>{selectedNode.id}</code>
                    </Form.Item>

                    <Form.Item label="节点标签">
                        <Input
                            value={selectedNode.data.label as string || ''}
                            onChange={handleLabelChange}
                            placeholder="请输入节点标签"
                        />
                    </Form.Item>

                    {selectedNode.type === 'userInput' && (
                        <>
                            <Divider orientation="left">输入配置</Divider>
                            <Form.Item label="提示文本">
                                <Input.TextArea
                                    value={config.placeholder || ''}
                                    onChange={(e) => handleConfigChange('placeholder', e.target.value)}
                                    placeholder="请输入提示文本"
                                    rows={2}
                                />
                            </Form.Item>
                            <Form.Item label="输入类型">
                                <Select
                                    value={config.inputType || 'text'}
                                    onChange={(value) => handleConfigChange('inputType', value)}
                                    options={[
                                        {value: 'text', label: '文本输入'},
                                        {value: 'options', label: '选项选择'}
                                    ]}
                                />
                            </Form.Item>
                            {config.inputType === 'options' && (
                                <Form.Item label="选项列表">
                                    <OptionsList
                                        options={config.options || []}
                                        onChange={handleOptionsChange}
                                    />
                                </Form.Item>
                            )}
                        </>
                    )}

                    {selectedNode.type === 'aiReply' && (
                        <>
                            <Divider orientation="left">回复配置</Divider>
                            <Form.Item label="消息类型">
                                <Select
                                    value={config.messageType || 'text'}
                                    onChange={(value) => handleConfigChange('messageType', value)}
                                    options={[
                                        {value: 'text', label: '文本消息'},
                                        {value: 'card', label: '卡片消息'}
                                    ]}
                                />
                            </Form.Item>
                            {config.messageType === 'text' && (
                                <Form.Item label="回复内容">
                                    <Input.TextArea
                                        value={config.content || ''}
                                        onChange={(e) => handleConfigChange('content', e.target.value)}
                                        placeholder="请输入回复内容，支持变量如 {userName}"
                                        rows={4}
                                    />
                                </Form.Item>
                            )}
                            {config.messageType === 'card' && (
                                <>
                                    <Form.Item label="卡片标题">
                                        <Input
                                            value={config.cardTitle || ''}
                                            onChange={(e) => handleConfigChange('cardTitle', e.target.value)}
                                            placeholder="请输入卡片标题"
                                        />
                                    </Form.Item>
                                    <Form.Item label="卡片描述">
                                        <Input.TextArea
                                            value={config.cardDescription || ''}
                                            onChange={(e) => handleConfigChange('cardDescription', e.target.value)}
                                            placeholder="请输入卡片描述"
                                            rows={2}
                                        />
                                    </Form.Item>
                                    <Form.Item label="卡片图片URL">
                                        <Input
                                            value={config.cardImageUrl || ''}
                                            onChange={(e) => handleConfigChange('cardImageUrl', e.target.value)}
                                            placeholder="请输入卡片图片URL"
                                        />
                                    </Form.Item>
                                    <Form.Item label="卡片按钮">
                                        <CardButtonsList
                                            buttons={config.cardButtons || []}
                                            onChange={handleCardButtonsChange}
                                        />
                                    </Form.Item>
                                </>
                            )}
                        </>
                    )}

                    {selectedNode.type === 'condition' && (
                        <>
                            <Divider orientation="left">分支配置</Divider>
                            <Form.Item label="条件列表">
                                <ConditionsList
                                    conditions={config.conditions || []}
                                    onChange={handleConditionsChange}
                                />
                            </Form.Item>
                            <Form.Item label="默认分支目标节点ID">
                                <Input
                                    value={config.defaultTarget || ''}
                                    onChange={(e) => handleConfigChange('defaultTarget', e.target.value)}
                                    placeholder="输入默认分支目标节点ID"
                                />
                            </Form.Item>
                        </>
                    )}
                </Form>
            </div>
        </div>
    )
}

/**
 * 选项列表子组件
 */
interface OptionsListProps {
    options: OptionItem[]
    onChange: (options: OptionItem[]) => void
}

const OptionsList: React.FC<OptionsListProps> = ({options, onChange}) => {
    const addOption = () => {
        onChange([...options, {label: '', value: ''}])
    }

    const removeOption = (index: number) => {
        const newOptions = [...options]
        newOptions.splice(index, 1)
        onChange(newOptions)
    }

    const updateOption = (index: number, field: 'label' | 'value', value: string) => {
        const newOptions = [...options]
        newOptions[index] = {...newOptions[index], [field]: value}
        onChange(newOptions)
    }

    return (
        <div className={styles['options-config']}>
            <div className={styles['options-list']}>
                {options.map((option, index) => (
                    <div key={index} className={styles['option-item']}>
                        <Input
                            placeholder="显示文本"
                            value={option.label}
                            onChange={(e) => updateOption(index, 'label', e.target.value)}
                            style={{width: '40%'}}
                        />
                        <Input
                            placeholder="值"
                            value={option.value}
                            onChange={(e) => updateOption(index, 'value', e.target.value)}
                            style={{width: '40%'}}
                        />
                        <Button
                            type="text"
                            danger
                            icon={<DeleteOutlined/>}
                            onClick={() => removeOption(index)}
                        />
                    </div>
                ))}
            </div>
            <Button
                type="dashed"
                icon={<PlusOutlined/>}
                onClick={addOption}
                block
                className={styles['add-btn']}
            >
                添加选项
            </Button>
        </div>
    )
}

/**
 * 条件列表子组件
 */
interface ConditionsListProps {
    conditions: ConditionItem[]
    onChange: (conditions: ConditionItem[]) => void
}

const ConditionsList: React.FC<ConditionsListProps> = ({conditions, onChange}) => {
    const addCondition = () => {
        const newCondition: ConditionItem = {
            id: `cond_${Date.now()}`,
            expression: '',
            description: '',
            targetNodeId: '',
            params: {}
        }
        onChange([...conditions, newCondition])
    }

    const removeCondition = (index: number) => {
        const newConditions = [...conditions]
        newConditions.splice(index, 1)
        onChange(newConditions)
    }

    const updateCondition = (index: number, field: keyof ConditionItem, value: any) => {
        const newConditions = [...conditions]
        newConditions[index] = {...newConditions[index], [field]: value}
        onChange(newConditions)
    }

    return (
        <div className={styles['condition-config']}>
            <div className={styles['condition-list']}>
                {conditions.map((condition, index) => (
                    <Card
                        key={condition.id}
                        size="small"
                        title={`条件 ${index + 1}`}
                        extra={
                            <Button
                                type="text"
                                danger
                                icon={<DeleteOutlined/>}
                                onClick={() => removeCondition(index)}
                            />
                        }
                        className={styles['condition-card']}
                    >
                        <Form layout="vertical">
                            <Form.Item label="条件描述">
                                <Input
                                    value={condition.description}
                                    onChange={(e) => updateCondition(index, 'description', e.target.value)}
                                    placeholder="描述此条件（如：用户选择是）"
                                />
                            </Form.Item>
                            <Form.Item label="条件表达式">
                                <Input.TextArea
                                    value={condition.expression}
                                    onChange={(e) => updateCondition(index, 'expression', e.target.value)}
                                    placeholder="条件表达式（如：userInput === 'yes'）"
                                    rows={2}
                                />
                            </Form.Item>
                            <Form.Item label="目标节点ID">
                                <Input
                                    value={condition.targetNodeId}
                                    onChange={(e) => updateCondition(index, 'targetNodeId', e.target.value)}
                                    placeholder="输入满足条件时跳转的目标节点ID"
                                />
                            </Form.Item>
                        </Form>
                    </Card>
                ))}
            </div>
            <Button
                type="dashed"
                icon={<PlusOutlined/>}
                onClick={addCondition}
                block
                className={styles['add-btn']}
            >
                添加条件分支
            </Button>
        </div>
    )
}

/**
 * 卡片按钮列表子组件
 */
interface CardButtonsListProps {
    buttons: CardButton[]
    onChange: (buttons: CardButton[]) => void
}

const CardButtonsList: React.FC<CardButtonsListProps> = ({buttons, onChange}) => {
    const addButton = () => {
        onChange([...buttons, {text: '', type: 'action', value: ''}])
    }

    const removeButton = (index: number) => {
        const newButtons = [...buttons]
        newButtons.splice(index, 1)
        onChange(newButtons)
    }

    const updateButton = (index: number, field: keyof CardButton, value: any) => {
        const newButtons = [...buttons]
        newButtons[index] = {...newButtons[index], [field]: value}
        onChange(newButtons)
    }

    return (
        <div className={styles['options-config']}>
            <div className={styles['options-list']}>
                {buttons.map((button, index) => (
                    <Space key={index} wrap className={styles['option-item']}>
                        <Input
                            placeholder="按钮文本"
                            value={button.text}
                            onChange={(e) => updateButton(index, 'text', e.target.value)}
                            style={{width: 120}}
                        />
                        <Select
                            value={button.type}
                            onChange={(value) => updateButton(index, 'type', value)}
                            style={{width: 100}}
                            options={[
                                {value: 'action', label: '动作'},
                                {value: 'link', label: '链接'}
                            ]}
                        />
                        <Input
                            placeholder={button.type === 'link' ? 'URL' : '动作值'}
                            value={button.value}
                            onChange={(e) => updateButton(index, 'value', e.target.value)}
                            style={{width: 120}}
                        />
                        <Button
                            type="text"
                            danger
                            icon={<DeleteOutlined/>}
                            onClick={() => removeButton(index)}
                        />
                    </Space>
                ))}
            </div>
            <Button
                type="dashed"
                icon={<PlusOutlined/>}
                onClick={addButton}
                block
                className={styles['add-btn']}
            >
                添加按钮
            </Button>
        </div>
    )
}

export default PropertyPanel
