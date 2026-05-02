import React from 'react'
import {Handle, Position, NodeProps} from 'reactflow'
import {UserOutlined} from '@ant-design/icons'
import styles from './nodes.module.scss'

/**
 * 用户输入节点
 * 等待用户输入，有输入和输出连接点
 */
const UserInputNode: React.FC<NodeProps> = ({data, selected}) => {
    const config = data.config as Record<string, any> || {}

    return (
        <div className={`${styles['custom-node']} ${styles['user-input-node']} ${selected ? styles.selected : ''}`}>
            <Handle
                type="target"
                position={Position.Left}
                className={styles['handle-left']}
            />
            <div className={styles['node-header']}>
                <UserOutlined className={styles['node-icon']}/>
                <span className={styles['node-title']}>用户输入</span>
            </div>
            <div className={styles['node-body']}>
                <div className={styles['node-label']}>
                    {config.placeholder || data.label as string || '等待用户输入...'}
                </div>
                {config.inputType === 'options' && config.options?.length > 0 && (
                    <div className={styles['node-options']}>
                        选项: {config.options.length} 个
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

export default UserInputNode
