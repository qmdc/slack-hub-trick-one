import React from 'react'
import {Handle, Position, NodeProps} from '@xyflow/react'
import {RobotOutlined} from '@ant-design/icons'
import styles from './nodes.module.scss'

/**
 * AI回复节点
 * AI发送消息，有输入和输出连接点
 */
const AiReplyNode: React.FC<NodeProps> = ({data, selected}) => {
    const config = data.config as Record<string, any> || {}

    return (
        <div className={`${styles['custom-node']} ${styles['ai-reply-node']} ${selected ? styles.selected : ''}`}>
            <Handle
                type="target"
                position={Position.Left}
                className={styles['handle-left']}
            />
            <div className={styles['node-header']}>
                <RobotOutlined className={styles['node-icon']}/>
                <span className={styles['node-title']}>AI回复</span>
            </div>
            <div className={styles['node-body']}>
                <div className={styles['node-label']}>
                    {config.content?.substring(0, 50) || data.label as string || 'AI回复内容'}
                    {config.content?.length > 50 && '...'}
                </div>
                {config.messageType === 'card' && (
                    <div className={styles['node-card']}>
                        [卡片消息]
                    </div>
                )}
            </div>
            <Handle
                type="source"
                position={Position.Right}
                className={styles['handle-right']}
            />
        </div>
    )
}

export default AiReplyNode
