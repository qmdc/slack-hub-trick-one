import React, {useState, useEffect, useCallback} from 'react'
import {
    Card,
    List,
    Button,
    Input,
    Select,
    Space,
    Modal,
    Form,
    message,
    Popconfirm,
    Tag,
    Tabs,
    Badge,
    Avatar,
    Empty,
    Row,
    Col,
    Statistic,
} from 'antd'
import {
    SearchOutlined,
    UserAddOutlined,
    CheckOutlined,
    CloseOutlined,
    TeamOutlined,
    ClockCircleOutlined,
    FireOutlined,
    TrophyOutlined,
} from '@ant-design/icons'
import {
    searchUsers,
    getFriendList,
    getPendingRequests,
    getSentRequests,
    sendFriendRequest,
    confirmFriendRequest,
    rejectFriendRequest,
    isFriend,
} from '../../../apis'
import type {HabitFriendResponse, HabitFriendshipPageQuery} from '../../../apis'
import styles from './friendlist.module.scss'

const {Search} = Input
const {TabPane} = Tabs

const FriendList: React.FC = () => {
    const [activeTab, setActiveTab] = useState('friends')
    const [friends, setFriends] = useState<HabitFriendResponse[]>([])
    const [pendingRequests, setPendingRequests] = useState<HabitFriendResponse[]>([])
    const [sentRequests, setSentRequests] = useState<HabitFriendResponse[]>([])
    const [loading, setLoading] = useState(false)
    const [searchKeyword, setSearchKeyword] = useState('')
    const [searchResults, setSearchResults] = useState<HabitFriendResponse[]>([])
    const [searching, setSearching] = useState(false)
    const [selectedUser, setSelectedUser] = useState<HabitFriendResponse | null>(null)
    const [userModalVisible, setUserModalVisible] = useState(false)
    const [sendingRequest, setSendingRequest] = useState(false)

    const fetchData = useCallback(async () => {
        setLoading(true)
        try {
            const params: HabitFriendshipPageQuery = {
                pageNo: 1,
                pageSize: 100,
            }

            const [friendRes, pendingRes, sentRes] = await Promise.all([
                getFriendList(params),
                getPendingRequests(),
                getSentRequests(),
            ])

            if (friendRes?.code === 200) {
                setFriends(friendRes.data?.list || [])
            }
            if (pendingRes?.code === 200) {
                setPendingRequests(pendingRes.data || [])
            }
            if (sentRes?.code === 200) {
                setSentRequests(sentRes.data || [])
            }
        } catch (error) {
            message.error('获取好友列表失败')
            console.error(error)
        } finally {
            setLoading(false)
        }
    }, [])

    useEffect(() => {
        fetchData()
    }, [fetchData])

    const handleSearch = async (value: string) => {
        if (!value.trim()) {
            setSearchResults([])
            return
        }
        setSearching(true)
        try {
            const res = await searchUsers(value)
            if (res?.code === 200) {
                setSearchResults(res.data || [])
            }
        } catch (error) {
            message.error('搜索失败')
            console.error(error)
        } finally {
            setSearching(false)
        }
    }

    const handleSendRequest = async (userId: number) => {
        setSendingRequest(true)
        try {
            const res = await sendFriendRequest(userId)
            if (res?.code === 200) {
                message.success('好友请求已发送')
                setUserModalVisible(false)
                fetchData()
            }
        } catch (error) {
            message.error('发送失败')
            console.error(error)
        } finally {
            setSendingRequest(false)
        }
    }

    const handleConfirmRequest = async (userId: number) => {
        try {
            const res = await confirmFriendRequest(userId)
            if (res?.code === 200) {
                message.success('已添加为好友')
                fetchData()
            }
        } catch (error) {
            message.error('操作失败')
            console.error(error)
        }
    }

    const handleRejectRequest = async (userId: number) => {
        try {
            const res = await rejectFriendRequest(userId)
            if (res?.code === 200) {
                message.success('已拒绝好友请求')
                fetchData()
            }
        } catch (error) {
            message.error('操作失败')
            console.error(error)
        }
    }

    const checkFriendshipStatus = async (userId: number) => {
        try {
            const res = await checkFriendship(userId)
            return res?.data
        } catch (error) {
            console.error(error)
            return null
        }
    }

    const handleViewUser = async (user: HabitFriendResponse) => {
        setSelectedUser(user)
        const status = await checkFriendshipStatus(user.friendUserId || user.id)
        setSelectedUser({...user, friendshipStatus: status as any})
        setUserModalVisible(true)
    }

    const renderFriendItem = (item: HabitFriendResponse) => (
        <List.Item key={item.id} className={styles['friend-item']}>
            <List.Item.Meta
                avatar={
                    <Avatar
                        size={48}
                        src={item.avatarUrl}
                        style={{backgroundColor: '#1890ff'}}
                    >
                        {item.nickname?.charAt(0) || item.username?.charAt(0) || '?'}
                    </Avatar>
                }
                title={
                    <div className={styles['friend-title']}>
                        <span className={styles['friend-name']}>
                            {item.nickname || item.username}
                        </span>
                        {item.isOnline && <Badge status="success" text="在线"/>}
                    </div>
                }
                description={
                    <div className={styles['friend-stats']}>
                        <Tag icon={<FireOutlined/>} color="orange">
                            连续 {item.currentStreak || 0} 天
                        </Tag>
                        <Tag icon={<TrophyOutlined/>} color="gold">
                            最长 {item.longestStreak || 0} 天
                        </Tag>
                        {item.lastCheckinTime && (
                            <span className={styles['last-checkin']}>
                                <ClockCircleOutlined style={{marginRight: 4}}/>
                                最近打卡：{new Date(item.lastCheckinTime).toLocaleDateString('zh-CN')}
                            </span>
                        )}
                    </div>
                }
            />
        </List.Item>
    )

    const renderPendingItem = (item: HabitFriendResponse) => (
        <List.Item key={item.id} className={styles['friend-item']}>
            <List.Item.Meta
                avatar={
                    <Avatar
                        size={48}
                        src={item.avatarUrl}
                        style={{backgroundColor: '#1890ff'}}
                    >
                        {item.nickname?.charAt(0) || item.username?.charAt(0) || '?'}
                    </Avatar>
                }
                title={
                    <div className={styles['friend-title']}>
                        <span className={styles['friend-name']}>
                            {item.nickname || item.username}
                        </span>
                        <Tag color="blue">请求添加你为好友</Tag>
                    </div>
                }
                description={
                    <div className={styles['friend-stats']}>
                        <span className={styles['last-checkin']}>
                            <ClockCircleOutlined style={{marginRight: 4}}/>
                            发送于：{item.createTime ? new Date(item.createTime).toLocaleString('zh-CN') : ''}
                        </span>
                    </div>
                }
            />
            <Space>
                <Button
                    type="primary"
                    icon={<CheckOutlined/>}
                    onClick={() => handleConfirmRequest(item.friendUserId || item.id)}
                >
                    同意
                </Button>
                <Popconfirm
                    title="确定拒绝该好友请求吗？"
                    onConfirm={() => handleRejectRequest(item.friendUserId || item.id)}
                    okText="确定"
                    cancelText="取消"
                >
                    <Button danger icon={<CloseOutlined/>}>
                        拒绝
                    </Button>
                </Popconfirm>
            </Space>
        </List.Item>
    )

    const renderSearchItem = (item: HabitFriendResponse) => (
        <List.Item key={item.id} className={styles['friend-item']}>
            <List.Item.Meta
                avatar={
                    <Avatar
                        size={48}
                        src={item.avatarUrl}
                        style={{backgroundColor: '#1890ff'}}
                    >
                        {item.nickname?.charAt(0) || item.username?.charAt(0) || '?'}
                    </Avatar>
                }
                title={
                    <div className={styles['friend-title']}>
                        <span className={styles['friend-name']}>
                            {item.nickname || item.username}
                        </span>
                        {item.isOnline && <Badge status="success" text="在线"/>}
                    </div>
                }
                description={
                    <div className={styles['friend-stats']}>
                        <Tag icon={<FireOutlined/>} color="orange">
                            连续 {item.currentStreak || 0} 天
                        </Tag>
                        <Tag icon={<TrophyOutlined/>} color="gold">
                            最长 {item.longestStreak || 0} 天
                        </Tag>
                    </div>
                }
            />
            <Button
                type="primary"
                icon={<UserAddOutlined/>}
                onClick={() => handleViewUser(item)}
            >
                查看
            </Button>
        </List.Item>
    )

    const renderSentItem = (item: HabitFriendResponse) => (
        <List.Item key={item.id} className={styles['friend-item']}>
            <List.Item.Meta
                avatar={
                    <Avatar
                        size={48}
                        src={item.avatarUrl}
                        style={{backgroundColor: '#1890ff'}}
                    >
                        {item.nickname?.charAt(0) || item.username?.charAt(0) || '?'}
                    </Avatar>
                }
                title={
                    <div className={styles['friend-title']}>
                        <span className={styles['friend-name']}>
                            {item.nickname || item.username}
                        </span>
                        <Tag color="orange">等待对方确认</Tag>
                    </div>
                }
                description={
                    <div className={styles['friend-stats']}>
                        <span className={styles['last-checkin']}>
                            <ClockCircleOutlined style={{marginRight: 4}}/>
                            发送于：{item.createTime ? new Date(item.createTime).toLocaleString('zh-CN') : ''}
                        </span>
                    </div>
                }
            />
        </List.Item>
    )

    return (
        <div className={styles['friend-list-page']}>
            <Row gutter={[16, 16]} style={{marginBottom: 16}}>
                <Col xs={24} sm={12} lg={6}>
                    <Card className={styles['mini-stat-card']}>
                        <Statistic
                            title="我的好友"
                            value={friends.length}
                            valueStyle={{color: '#1890ff'}}
                            prefix={<TeamOutlined/>}
                        />
                    </Card>
                </Col>
                <Col xs={24} sm={12} lg={6}>
                    <Card className={styles['mini-stat-card']}>
                        <Statistic
                            title="待处理请求"
                            value={pendingRequests.length}
                            valueStyle={{color: '#faad14'}}
                        />
                    </Card>
                </Col>
                <Col xs={24} sm={12} lg={6}>
                    <Card className={styles['mini-stat-card']}>
                        <Statistic
                            title="已发送请求"
                            value={sentRequests.length}
                            valueStyle={{color: '#8c8c8c'}}
                        />
                    </Card>
                </Col>
                <Col xs={24} sm={12} lg={6}>
                    <Card className={styles['mini-stat-card']}>
                        <Statistic
                            title="在线好友"
                            value={friends.filter(f => f.isOnline).length}
                            valueStyle={{color: '#52c41a'}}
                        />
                    </Card>
                </Col>
            </Row>

            <div className={styles['search-section']}>
                <Search
                    placeholder="搜索用户名或昵称添加好友"
                    allowClear
                    enterButton={<span><SearchOutlined/> 搜索</span>}
                    size="large"
                    onSearch={handleSearch}
                    onChange={(e) => setSearchKeyword(e.target.value)}
                    loading={searching}
                    style={{maxWidth: 600}}
                />
            </div>

            {searchResults.length > 0 && (
                <Card
                    title="搜索结果"
                    className={styles['search-result-card']}
                    style={{marginBottom: 16}}
                >
                    <List
                        dataSource={searchResults}
                        renderItem={renderSearchItem}
                    />
                </Card>
            )}

            <Card className={styles['tabs-card']}>
                <Tabs
                    activeKey={activeTab}
                    onChange={setActiveTab}
                    items={[
                        {
                            key: 'friends',
                            label: (
                                <span>
                                    我的好友
                                    <Badge count={friends.length} size="small" style={{marginLeft: 8}}/>
                                </span>
                            ),
                            children: (
                                <List
                                    loading={loading}
                                    dataSource={friends}
                                    locale={{emptyText: <Empty description="暂无好友，快去搜索添加吧！"/>}}
                                    renderItem={renderFriendItem}
                                />
                            ),
                        },
                        {
                            key: 'pending',
                            label: (
                                <span>
                                    好友申请
                                    <Badge
                                        count={pendingRequests.length}
                                        size="small"
                                        offset={[8, 0]}
                                        style={{marginLeft: 8}}
                                    />
                                </span>
                            ),
                            children: (
                                <List
                                    loading={loading}
                                    dataSource={pendingRequests}
                                    locale={{emptyText: <Empty description="暂无待处理的好友请求"/>}}
                                    renderItem={renderPendingItem}
                                />
                            ),
                        },
                        {
                            key: 'sent',
                            label: (
                                <span>
                                    已发送
                                    <Badge count={sentRequests.length} size="small" style={{marginLeft: 8}}/>
                                </span>
                            ),
                            children: (
                                <List
                                    loading={loading}
                                    dataSource={sentRequests}
                                    locale={{emptyText: <Empty description="暂无已发送的好友请求"/>}}
                                    renderItem={renderSentItem}
                                />
                            ),
                        },
                    ]}
                />
            </Card>

            <Modal
                title="用户信息"
                open={userModalVisible}
                onCancel={() => setUserModalVisible(false)}
                footer={null}
                width={500}
            >
                {selectedUser && (
                    <div className={styles['user-detail']}>
                        <div className={styles['user-header']}>
                            <Avatar
                                size={80}
                                src={selectedUser.avatarUrl}
                                style={{backgroundColor: '#1890ff'}}
                            >
                                {selectedUser.nickname?.charAt(0) || selectedUser.username?.charAt(0) || '?'}
                            </Avatar>
                            <div className={styles['user-info']}>
                                <h2 className={styles['user-name']}>
                                    {selectedUser.nickname || selectedUser.username}
                                </h2>
                                <p className={styles['user-username']}>
                                    @{selectedUser.username}
                                </p>
                                {selectedUser.isOnline && <Badge status="success" text="在线"/>}
                            </div>
                        </div>

                        <div className={styles['user-stats']}>
                            <div className={styles['stat-item']}>
                                <FireOutlined style={{color: '#faad14', fontSize: 20}}/>
                                <div>
                                    <p className={styles['stat-value']}>{selectedUser.currentStreak || 0}</p>
                                    <p className={styles['stat-label']}>连续打卡</p>
                                </div>
                            </div>
                            <div className={styles['stat-item']}>
                                <TrophyOutlined style={{color: '#fa8c16', fontSize: 20}}/>
                                <div>
                                    <p className={styles['stat-value']}>{selectedUser.longestStreak || 0}</p>
                                    <p className={styles['stat-label']}>最长连续</p>
                                </div>
                            </div>
                            <div className={styles['stat-item']}>
                                <TeamOutlined style={{color: '#1890ff', fontSize: 20}}/>
                                <div>
                                    <p className={styles['stat-value']}>{selectedUser.friendCount || 0}</p>
                                    <p className={styles['stat-label']}>好友数</p>
                                </div>
                            </div>
                        </div>

                        <div className={styles['user-action']}>
                            {selectedUser.friendshipStatus === null && (
                                <Button
                                    type="primary"
                                    size="large"
                                    icon={<UserAddOutlined/>}
                                    loading={sendingRequest}
                                    onClick={() => handleSendRequest(selectedUser.friendUserId || selectedUser.id)}
                                >
                                    发送好友请求
                                </Button>
                            )}
                            {selectedUser.friendshipStatus === 1 && (
                                <Tag color="blue">已经是好友</Tag>
                            )}
                            {selectedUser.friendshipStatus === 2 && (
                                <Tag color="orange">等待对方确认</Tag>
                            )}
                            {selectedUser.friendshipStatus === 3 && (
                                <Tag color="gold">对方已发送请求</Tag>
                            )}
                        </div>
                    </div>
                )}
            </Modal>
        </div>
    )
}

export default FriendList
