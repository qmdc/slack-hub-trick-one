import React from 'react'
import {
    PlayCircleOutlined,
    UserOutlined,
    RobotOutlined,
    BranchesOutlined,
    StopOutlined
} from '@ant-design/icons'
import styles from './nodePanel.module.scss'

/**
 * 节点面板组件
 * 提供可拖拽的节点模板
 */

interface NodeItem {
    type: string
    label: string
    description: string
    icon: React.ReactNode
    color: string
}

const nodeItems: NodeItem[] = [
    {
        type: 'start',
        label: '开始',
        description: '流程入口',
        icon: <PlayCircleOutlined style={{color: '#52c41a'}}/>,
        color: '#52c41a'
    },
    {
        type: 'userInput',
        label: '用户输入',
        description: '等待用户输入',
        icon: <UserOutlined style={{color: '#1890ff'}}/>,
        color: '#1890ff'
    },
    {
        type: 'aiReply',
        label: 'AI回复',
        description: 'AI发送消息',
        icon: <RobotOutlined style={{color: '#722ed1'}}/>,
        color: '#722ed1'
    },
    {
        type: 'condition',
        label: '条件分支',
        description: '根据条件分流',
        icon: <BranchesOutlined style={{color: '#fa8c16'}}/>,
        color: '#fa8c16'
    },
    {
        type: 'end',
        label: '结束',
        description: '流程结束',
        icon: <StopOutlined style={{color: '#ff4d4f'}}/>,
        color: '#ff4d4f'
    }
]

interface NodePanelProps {
    onDragStart: (event: React.DragEvent, nodeType: string) => void
}

const NodePanel: React.FC<NodePanelProps> = ({onDragStart}) => {
    return (
        <div className={styles['node-panel']}>
            <div className={styles['panel-title']}>节点列表</div>
            <div className={styles['node-list']}>
                {nodeItems.map((item) => (
                    <div
                        key={item.type}
                        className={styles['node-item']}
                        draggable
                        onDragStart={(event) => onDragStart(event, item.type)}
                    >
                        <div className={styles['node-icon']}>{item.icon}</div>
                        <div className={styles['node-info']}>
                            <div className={styles['node-name']}>{item.label}</div>
                            <div className={styles['node-desc']}>{item.description}</div>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    )
}

export default NodePanel
