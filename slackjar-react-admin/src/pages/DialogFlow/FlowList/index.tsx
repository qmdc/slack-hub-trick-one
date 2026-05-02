import React, {useState, useEffect, useCallback} from 'react'
import {Button, Table, Input, Select, Space, Modal, Form, Input as InputAntd, message, Popconfirm} from 'antd'
import {
    PlusOutlined,
    EditOutlined,
    DeleteOutlined,
    ExportOutlined,
    ImportOutlined,
    SearchOutlined,
    ReloadOutlined,
} from '@ant-design/icons'
import {useNavigate} from 'react-router'
import type {ColumnsType} from 'antd/es/table'
import {
    pageQueryFlows,
    saveFlow,
    deleteFlow,
    exportFlow,
    importFlow,
} from '../../../apis/modules/dialogflow'
import type {FlowItem} from '../../../apis/modules/dialogflow'
import styles from '../dialogflow.module.scss'

/**
 * 对话流程列表页面
 */

const {TextArea} = InputAntd

const FlowList: React.FC = () => {
    const navigate = useNavigate()
    const [loading, setLoading] = useState(false)
    const [data, setData] = useState<FlowItem[]>([])
    const [total, setTotal] = useState(0)
    const [pageNo, setPageNo] = useState(1)
    const [pageSize, setPageSize] = useState(10)
    const [searchName, setSearchName] = useState('')
    const [searchStatus, setSearchStatus] = useState<number | undefined>(undefined)
    const [createModalVisible, setCreateModalVisible] = useState(false)
    const [importModalVisible, setImportModalVisible] = useState(false)
    const [form] = Form.useForm()
    const [importJson, setImportJson] = useState('')

    const fetchData = useCallback(async () => {
        setLoading(true)
        try {
            const res = await pageQueryFlows({
                pageNo,
                pageSize,
                name: searchName || undefined,
                status: searchStatus,
            })
            if (res?.code === 200) {
                setData(res.data?.list || [])
                setTotal(res.data?.total || 0)
            }
        } catch (error) {
            message.error('获取流程列表失败')
            console.error(error)
        } finally {
            setLoading(false)
        }
    }, [pageNo, pageSize, searchName, searchStatus])

    useEffect(() => {
        fetchData()
    }, [fetchData])

    const handleSearch = () => {
        setPageNo(1)
        fetchData()
    }

    const handleReset = () => {
        setSearchName('')
        setSearchStatus(undefined)
        setPageNo(1)
        setTimeout(fetchData, 100)
    }

    const handleCreate = async (values: {name: string; description?: string}) => {
        try {
            const defaultFlowData = JSON.stringify({
                nodes: [
                    {
                        id: 'start_1',
                        type: 'start',
                        position: {x: 100, y: 100},
                        data: {label: '开始'},
                    },
                ],
                edges: [],
            })

            const res = await saveFlow({
                name: values.name,
                description: values.description || '',
                flowData: defaultFlowData,
                status: 1,
            })
            if (res?.code === 200) {
                message.success('创建成功')
                setCreateModalVisible(false)
                form.resetFields()
                navigate(`/dialogflow/designer/${res.data}`)
            }
        } catch (error) {
            message.error('创建失败')
            console.error(error)
        }
    }

    const handleEdit = (record: FlowItem) => {
        navigate(`/dialogflow/designer/${record.id}`)
    }

    const handleDelete = async (id: number) => {
        try {
            const res = await deleteFlow(id)
            if (res?.code === 200) {
                message.success('删除成功')
                fetchData()
            }
        } catch (error) {
            message.error('删除失败')
            console.error(error)
        }
    }

    const handleExport = async (record: FlowItem) => {
        try {
            const res = await exportFlow(record.id)
            if (res?.code === 200) {
                const blob = new Blob([JSON.stringify(res.data, null, 2)], {
                    type: 'application/json',
                })
                const url = URL.createObjectURL(blob)
                const a = document.createElement('a')
                a.href = url
                a.download = `${record.name}_${Date.now()}.json`
                a.click()
                URL.revokeObjectURL(url)
                message.success('导出成功')
            }
        } catch (error) {
            message.error('导出失败')
            console.error(error)
        }
    }

    const handleImport = async () => {
        if (!importJson.trim()) {
            message.warning('请输入JSON数据')
            return
        }
        try {
            JSON.parse(importJson)
        } catch {
            message.error('JSON格式错误')
            return
        }
        try {
            const res = await importFlow(importJson)
            if (res?.code === 200) {
                message.success('导入成功')
                setImportModalVisible(false)
                setImportJson('')
                fetchData()
            }
        } catch (error) {
            message.error('导入失败')
            console.error(error)
        }
    }

    const columns: ColumnsType<FlowItem> = [
        {
            title: 'ID',
            dataIndex: 'id',
            width: 80,
        },
        {
            title: '流程名称',
            dataIndex: 'name',
            width: 200,
        },
        {
            title: '描述',
            dataIndex: 'description',
            ellipsis: true,
        },
        {
            title: '状态',
            dataIndex: 'status',
            width: 100,
            render: (status) => (
                <Select
                    value={status}
                    style={{width: 80}}
                    options={[
                        {value: 1, label: '启用'},
                        {value: 0, label: '禁用'},
                    ]}
                />
            ),
        },
        {
            title: '创建时间',
            dataIndex: 'createTime',
            width: 180,
            render: (time) => {
                if (!time) return '-'
                return new Date(time).toLocaleString('zh-CN')
            },
        },
        {
            title: '操作',
            width: 200,
            fixed: 'right',
            render: (_, record) => (
                <Space size="middle">
                    <Button
                        type="link"
                        icon={<EditOutlined/>}
                        onClick={() => handleEdit(record)}
                    >
                        编辑
                    </Button>
                    <Button
                        type="link"
                        icon={<ExportOutlined/>}
                        onClick={() => handleExport(record)}
                    >
                        导出
                    </Button>
                    <Popconfirm
                        title="确定删除该流程吗？"
                        onConfirm={() => handleDelete(record.id)}
                        okText="确定"
                        cancelText="取消"
                    >
                        <Button type="link" danger icon={<DeleteOutlined/>}>
                            删除
                        </Button>
                    </Popconfirm>
                </Space>
            ),
        },
    ]

    return (
        <div className={styles['flow-list-page']}>
            <div className={styles['page-header']}>
                <span className={styles['page-title']}>对话流程管理</span>
                <Space>
                    <Button
                        icon={<ImportOutlined/>}
                        onClick={() => setImportModalVisible(true)}
                    >
                        导入流程
                    </Button>
                    <Button
                        type="primary"
                        icon={<PlusOutlined/>}
                        onClick={() => setCreateModalVisible(true)}
                    >
                        新建流程
                    </Button>
                </Space>
            </div>

            <div className={styles['search-bar']}>
                <div className={styles['search-row']}>
                    <div className={styles['search-item']}>
                        <span className={styles['item-label']}>流程名称：</span>
                        <Input
                            placeholder="请输入流程名称"
                            value={searchName}
                            onChange={(e) => setSearchName(e.target.value)}
                            style={{width: 200}}
                            allowClear
                        />
                    </div>
                    <div className={styles['search-item']}>
                        <span className={styles['item-label']}>状态：</span>
                        <Select
                            placeholder="全部"
                            value={searchStatus}
                            onChange={setSearchStatus}
                            style={{width: 120}}
                            allowClear
                            options={[
                                {value: 1, label: '启用'},
                                {value: 0, label: '禁用'},
                            ]}
                        />
                    </div>
                    <div className={styles['search-actions']}>
                        <Button icon={<SearchOutlined/>} onClick={handleSearch}>
                            搜索
                        </Button>
                        <Button icon={<ReloadOutlined/>} onClick={handleReset}>
                            重置
                        </Button>
                    </div>
                </div>
            </div>

            <div className={styles['table-container']}>
                <Table
                    columns={columns}
                    dataSource={data}
                    rowKey="id"
                    loading={loading}
                    pagination={{
                        current: pageNo,
                        pageSize,
                        total,
                        showSizeChanger: true,
                        showQuickJumper: true,
                        showTotal: (total) => `共 ${total} 条`,
                        onChange: (page, size) => {
                            setPageNo(page)
                            setPageSize(size)
                        },
                    }}
                    scroll={{x: 1000}}
                />
            </div>

            <Modal
                title="新建流程"
                open={createModalVisible}
                onOk={() => form.submit()}
                onCancel={() => setCreateModalVisible(false)}
                okText="确定"
                cancelText="取消"
                className={styles['flow-modal']}
            >
                <Form
                    form={form}
                    layout="vertical"
                    onFinish={handleCreate}
                    style={{marginTop: 24}}
                >
                    <Form.Item
                        name="name"
                        label="流程名称"
                        rules={[{required: true, message: '请输入流程名称'}]}
                    >
                        <Input placeholder="请输入流程名称"/>
                    </Form.Item>
                    <Form.Item name="description" label="流程描述">
                        <TextArea
                            placeholder="请输入流程描述"
                            rows={3}
                        />
                    </Form.Item>
                </Form>
            </Modal>

            <Modal
                title="导入流程"
                open={importModalVisible}
                onOk={handleImport}
                onCancel={() => setImportModalVisible(false)}
                okText="导入"
                cancelText="取消"
                className={styles['flow-modal']}
            >
                <div style={{marginTop: 24}}>
                    <TextArea
                        placeholder="请粘贴JSON数据"
                        value={importJson}
                        onChange={(e) => setImportJson(e.target.value)}
                        rows={10}
                    />
                    <p style={{marginTop: 8, color: '#8c8c8c', fontSize: 12}}>
                        请粘贴从导出功能获取的JSON数据
                    </p>
                </div>
            </Modal>
        </div>
    )
}

export default FlowList
