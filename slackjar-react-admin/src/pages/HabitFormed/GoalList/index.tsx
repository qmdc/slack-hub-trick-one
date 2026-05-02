import React, {useState, useEffect, useCallback} from 'react'
import {Table, Button, Input, Select, Space, Modal, Form, Input as InputAntd, message, Popconfirm, Switch, Tag, Card, Statistic, Row, Col} from 'antd'
import {
    PlusOutlined,
    EditOutlined,
    DeleteOutlined,
    SearchOutlined,
    ReloadOutlined,
    FireOutlined,
    TrophyOutlined,
    CalendarOutlined,
} from '@ant-design/icons'
import type {ColumnsType} from 'antd/es/table'
import {
    pageQueryGoals,
    saveGoal,
    deleteGoal,
    getTodayPendingGoals,
    getTodayCompletedGoals,
} from '../../../apis'
import type {HabitGoalResponse, HabitGoalPageQuery, HabitGoalSaveRequest} from '../../../apis'
import styles from './goallist.module.scss'

const {TextArea} = InputAntd

const commonGoalIcons = ['🌅', '🏃', '📚', '🧘', '💪', '🎯', '✍️', '🎨', '🎵', '💧', '🍎', '😴', '🚀', '💡']
const commonGoalColors = ['#1890ff', '#52c41a', '#faad14', '#ff4d4f', '#722ed1', '#eb2f96', '#13c2c2', '#fa8c16']

const GoalList: React.FC = () => {
    const [loading, setLoading] = useState(false)
    const [data, setData] = useState<HabitGoalResponse[]>([])
    const [total, setTotal] = useState(0)
    const [pageNo, setPageNo] = useState(1)
    const [pageSize, setPageSize] = useState(10)
    const [searchName, setSearchName] = useState('')
    const [searchStatus, setSearchStatus] = useState<number | undefined>(undefined)
    const [modalVisible, setModalVisible] = useState(false)
    const [editingGoal, setEditingGoal] = useState<HabitGoalResponse | null>(null)
    const [form] = Form.useForm()
    const [stats, setStats] = useState({
        totalGoals: 0,
        activeGoals: 0,
        todayPending: 0,
        todayCompleted: 0,
        longestStreak: 0,
    })

    const fetchData = useCallback(async () => {
        setLoading(true)
        try {
            const params: HabitGoalPageQuery = {
                pageNo,
                pageSize,
                goalName: searchName || undefined,
                status: searchStatus,
            }
            const res = await pageQueryGoals(params)
            if (res?.code === 200) {
                setData(res.data?.list || [])
                setTotal(res.data?.total || 0)
            }

            const [pendingRes, completedRes] = await Promise.all([
                getTodayPendingGoals(),
                getTodayCompletedGoals(),
            ])

            const pendingList = pendingRes?.data || []
            const completedList = completedRes?.data || []
            const allGoals = [...pendingList, ...completedList]
            const activeGoals = allGoals.filter(g => g.status === 1).length
            const longestStreak = Math.max(0, ...allGoals.map(g => g.currentStreak || 0))

            setStats({
                totalGoals: res.data?.total || 0,
                activeGoals,
                todayPending: pendingList.length,
                todayCompleted: completedList.length,
                longestStreak,
            })
        } catch (error) {
            message.error('获取目标列表失败')
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

    const handleCreate = () => {
        setEditingGoal(null)
        form.resetFields()
        form.setFieldsValue({
            status: 1,
            visibility: 1,
            goalIcon: '🎯',
            goalColor: '#1890ff',
        })
        setModalVisible(true)
    }

    const handleEdit = (record: HabitGoalResponse) => {
        setEditingGoal(record)
        form.setFieldsValue({
            goalName: record.goalName,
            description: record.description,
            status: record.status,
            visibility: record.visibility,
            goalIcon: record.goalIcon || '🎯',
            goalColor: record.goalColor || '#1890ff',
        })
        setModalVisible(true)
    }

    const handleDelete = async (id: number) => {
        try {
            const res = await deleteGoal(id)
            if (res?.code === 200) {
                message.success('删除成功')
                fetchData()
            }
        } catch (error) {
            message.error('删除失败')
            console.error(error)
        }
    }

    const handleSave = async (values: HabitGoalSaveRequest) => {
        try {
            const res = await saveGoal({
                id: editingGoal?.id,
                ...values,
            })
            if (res?.code === 200) {
                message.success(editingGoal ? '修改成功' : '创建成功')
                setModalVisible(false)
                form.resetFields()
                fetchData()
            }
        } catch (error) {
            message.error('保存失败')
            console.error(error)
        }
    }

    const columns: ColumnsType<HabitGoalResponse> = [
        {
            title: 'ID',
            dataIndex: 'id',
            width: 60,
        },
        {
            title: '目标',
            dataIndex: 'goalName',
            width: 180,
            render: (text, record) => (
                <div className={styles['goal-name-col']}>
                    <span
                        className={styles['goal-icon-col']}
                        style={{backgroundColor: record.goalColor || '#1890ff'}}
                    >
                        {record.goalIcon || '🎯'}
                    </span>
                    <span>{text}</span>
                </div>
            ),
        },
        {
            title: '描述',
            dataIndex: 'description',
            ellipsis: true,
        },
        {
            title: '连续打卡',
            dataIndex: 'currentStreak',
            width: 100,
            align: 'center',
            render: (val) => (
                <Tag color={val && val > 0 ? 'success' : 'default'}>
                    <FireOutlined style={{marginRight: 4}}/>
                    {val || 0}天
                </Tag>
            ),
        },
        {
            title: '最长连续',
            dataIndex: 'longestStreak',
            width: 100,
            align: 'center',
            render: (val) => (
                <Tag color={val && val > 0 ? 'warning' : 'default'}>
                    <TrophyOutlined style={{marginRight: 4}}/>
                    {val || 0}天
                </Tag>
            ),
        },
        {
            title: '总打卡次数',
            dataIndex: 'checkinCount',
            width: 100,
            align: 'center',
            render: (val) => (
                <span>
                    <CalendarOutlined style={{marginRight: 4, color: '#1890ff'}}/>
                    {val || 0}
                </span>
            ),
        },
        {
            title: '完成率',
            dataIndex: 'completionRate',
            width: 100,
            align: 'center',
            render: (val) => (
                <span style={{color: val && val >= 80 ? '#52c41a' : val && val >= 50 ? '#faad14' : '#8c8c8c'}}>
                    {val || 0}%
                </span>
            ),
        },
        {
            title: '状态',
            dataIndex: 'status',
            width: 80,
            align: 'center',
            render: (status) => (
                <Switch
                    checked={status === 1}
                    checkedChildren="启用"
                    unCheckedChildren="禁用"
                    size="small"
                />
            ),
        },
        {
            title: '创建时间',
            dataIndex: 'createTime',
            width: 160,
            render: (time) => {
                if (!time) return '-'
                return new Date(time).toLocaleString('zh-CN')
            },
        },
        {
            title: '操作',
            width: 140,
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
                    <Popconfirm
                        title="确定删除该目标吗？"
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
        <div className={styles['goal-list-page']}>
            <Row gutter={[16, 16]} style={{marginBottom: 16}}>
                <Col xs={24} sm={12} lg={6}>
                    <Card className={styles['mini-stat-card']}>
                        <Statistic
                            title="全部目标"
                            value={stats.totalGoals}
                            valueStyle={{color: '#1890ff'}}
                        />
                    </Card>
                </Col>
                <Col xs={24} sm={12} lg={6}>
                    <Card className={styles['mini-stat-card']}>
                        <Statistic
                            title="进行中"
                            value={stats.activeGoals}
                            valueStyle={{color: '#52c41a'}}
                        />
                    </Card>
                </Col>
                <Col xs={24} sm={12} lg={6}>
                    <Card className={styles['mini-stat-card']}>
                        <Statistic
                            title="今日待打卡"
                            value={stats.todayPending}
                            valueStyle={{color: '#faad14'}}
                        />
                    </Card>
                </Col>
                <Col xs={24} sm={12} lg={6}>
                    <Card className={styles['mini-stat-card']}>
                        <Statistic
                            title="今日已完成"
                            value={stats.todayCompleted}
                            valueStyle={{color: '#52c41a'}}
                        />
                    </Card>
                </Col>
            </Row>

            <div className={styles['page-header']}>
                <span className={styles['page-title']}>目标管理</span>
                <Button
                    type="primary"
                    icon={<PlusOutlined/>}
                    onClick={handleCreate}
                >
                    新建目标
                </Button>
            </div>

            <div className={styles['search-bar']}>
                <div className={styles['search-row']}>
                    <div className={styles['search-item']}>
                        <span className={styles['item-label']}>目标名称：</span>
                        <Input
                            placeholder="请输入目标名称"
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
                    scroll={{x: 1200}}
                />
            </div>

            <Modal
                title={editingGoal ? '编辑目标' : '新建目标'}
                open={modalVisible}
                onOk={() => form.submit()}
                onCancel={() => setModalVisible(false)}
                okText="确定"
                cancelText="取消"
                width={600}
            >
                <Form
                    form={form}
                    layout="vertical"
                    onFinish={handleSave}
                    style={{marginTop: 24}}
                >
                    <Form.Item
                        name="goalName"
                        label="目标名称"
                        rules={[{required: true, message: '请输入目标名称'}]}
                    >
                        <Input placeholder="如：早起、跑步、读书等"/>
                    </Form.Item>

                    <Form.Item name="description" label="目标描述">
                        <TextArea
                            placeholder="描述你的目标，激励自己坚持"
                            rows={3}
                        />
                    </Form.Item>

                    <Form.Item name="goalIcon" label="选择图标">
                        <div className={styles['icon-selector']}>
                            {commonGoalIcons.map((icon) => (
                                <div
                                    key={icon}
                                    className={`${styles['icon-item']} ${
                                        form.getFieldValue('goalIcon') === icon ? styles['selected'] : ''
                                    }`}
                                    onClick={() => form.setFieldValue('goalIcon', icon)}
                                >
                                    {icon}
                                </div>
                            ))}
                        </div>
                    </Form.Item>

                    <Form.Item name="goalColor" label="选择颜色">
                        <div className={styles['color-selector']}>
                            {commonGoalColors.map((color) => (
                                <div
                                    key={color}
                                    className={`${styles['color-item']} ${
                                        form.getFieldValue('goalColor') === color ? styles['selected'] : ''
                                    }`}
                                    style={{backgroundColor: color}}
                                    onClick={() => form.setFieldValue('goalColor', color)}
                                />
                            ))}
                        </div>
                    </Form.Item>

                    <Form.Item name="status" label="状态" valuePropName="checked">
                        <Switch checkedChildren="启用" unCheckedChildren="禁用"/>
                    </Form.Item>

                    <Form.Item name="visibility" label="可见性">
                        <Select
                            style={{width: 200}}
                            options={[
                                {value: 1, label: '公开 - 好友可见'},
                                {value: 2, label: '私密 - 仅自己可见'},
                            ]}
                        />
                    </Form.Item>
                </Form>
            </Modal>
        </div>
    )
}

export default GoalList
