import React from 'react'
import {Handle, Position, NodeProps} from 'reactflow'
import {BranchesOutlined} from '@ant-design/icons'
import styles from './nodes.module.scss'

/**
 * 条件分支节点
 * 根据条件分流，有一个输入连接点和多个输出连接点
 */
const ConditionNode: React.FC<NodeProps> = ({data, selected}) => {
    const config = data.config as Record<string, any> || {}
    const conditions = config.conditions || []

    return (
        <div className={`${styles['custom-node']} ${styles['condition-node']} ${selected ? styles.selected : ''}`}>
            <Handle
                type="target"
                position={Position.Left}
                className={styles['handle-left']}
            />
            <div className={styles['node-header']}>
                <BranchesOutlined className={styles['node-icon']}/>
                <span className={styles['node-title']}>条件分支</span>
            </div>
            <div className={styles['node-body']}>
                <div className={styles['node-label']}>
                    {data.label as string || `条件分支 (${conditions.length + 1} 分支)`}
                </div>
                <div className={styles['condition-list']}>
                    {conditions.map((cond: Record<string, any>, index: number) => (
                        <div key={index} className={styles['condition-item']}>
                            条件 {index + 1}: {cond.description || cond.expression}
                        </div>
                    ))}
                    <div className={styles['condition-item']}>
                        默认: 其他情况
                    </div>
                </div>
            </div>
            {conditions.map((_: Record<string, any>, index: number) => (
                <Handle
                    key={`condition-${index}`}
                    type="source"
                    position={Position.Right}
                    id={`condition-${index}`}
                    className={styles['handle-multiple']}
                    style={{top: `${30 + index * 20}%`}}
                />
            ))}
            <Handle
                type="source"
                position={Position.Right}
                id="default"
                className={styles['handle-multiple']}
                style={{bottom: 10}}
            />
        </div>
    )
}

export default ConditionNode
