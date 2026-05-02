import React from 'react'
import {Handle, Position, NodeProps} from 'reactflow'
import {PlayCircleOutlined} from '@ant-design/icons'
import styles from './nodes.module.scss'

/**
 * 开始节点
 * 流程入口节点，只有输出连接点
 */
const StartNode: React.FC<NodeProps> = ({data, selected}) => {
    return (
        <div className={`${styles['custom-node']} ${styles['start-node']} ${selected ? styles.selected : ''}`}>
            <div className={styles['node-header']}>
                <PlayCircleOutlined className={styles['node-icon']}/>
                <span className={styles['node-title']}>开始</span>
            </div>
            <div className={styles['node-body']}>
                <div className={styles['node-label']}>{data.label as string || '流程开始'}</div>
            </div>
            <Handle
                type="source"
                position={Position.Right}
                className={styles['handle-right']}
            />
        </div>
    )
}

export default StartNode
