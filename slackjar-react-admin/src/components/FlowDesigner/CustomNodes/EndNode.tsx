import React from 'react'
import {Handle, Position, NodeProps} from '@xyflow/react'
import {StopOutlined} from '@ant-design/icons'
import styles from './nodes.module.scss'

/**
 * 结束节点
 * 流程结束节点，只有输入连接点
 */
const EndNode: React.FC<NodeProps> = ({data, selected}) => {
    return (
        <div className={`${styles['custom-node']} ${styles['end-node']} ${selected ? styles.selected : ''}`}>
            <Handle
                type="target"
                position={Position.Left}
                className={styles['handle-left']}
            />
            <div className={styles['node-header']}>
                <StopOutlined className={styles['node-icon']}/>
                <span className={styles['node-title']}>结束</span>
            </div>
            <div className={styles['node-body']}>
                <div className={styles['node-label']}>{data.label as string || '流程结束'}</div>
            </div>
        </div>
    )
}

export default EndNode
