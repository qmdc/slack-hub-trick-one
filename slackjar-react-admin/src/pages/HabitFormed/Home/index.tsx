import React, {useState, useEffect, useCallback} from 'react'
import {Card, List, Button, Tag, Statistic, Row, Col, Badge, Empty, message} from 'antd'
import {
    CheckOutlined,
    ClockCircleOutlined,
    FireOutlined,
    TrophyOutlined,
    TeamOutlined,
    PlusOutlined,
} from '@ant-design/icons'
import {useNavigate} from 'react-router'
import {getHomePageData, checkin} from '../../../apis'
import type {HomePageResponse, HabitGoalResponse, HabitCheckinResponse} from '../../../apis'
import styles from './home.module.scss'

/**
 * 习惯养成社区首页
 */
const Home: React.FC = () => {
    const navigate = useNavigate()
    const [loading, setLoading] = useState(false)
    const [homeData, setHomeData] = useState<HomePageResponse | null>(null)
    const [checkingGoalId, setCheckingGoalId] = useState<number | null>(null)

    const fetchData = useCallback(async () => {
        setLoading(true)
        try {
            const res = await getHomePageData()
            if (res?.code === 200) {
                setHomeData(res.data)
            }
        } catch (error) {
            message.error('获取首页数据失败')
            console.error(error)
        } finally {
            setLoading(false)
        }
    }, [])

    useEffect(() => {
        fetchData()
    }, [fetchData])

    const handleCheckin = async (goal: HabitGoalResponse) => {
        setCheckingGoalId(goal.id)
        try {
            const res = await checkin({
                goalId: goal.id,
                visibility: 1,
            })
            if (res?.code === 200) {
                message.success('打卡成功！')
                fetchData()
            }
        } catch (error) {
            message.error('打卡失败')
            console.error(error)
        } finally {
            setCheckingGoalId(null)
        }
    }

    const renderGoalCard = (goal: HabitGoalResponse, isCompleted: boolean) => (
        <Card
            key={goal.id}
            className={styles['goal-card']}
            style={{borderLeftColor: goal.goalColor || '#1890ff', borderLeftWidth: 4}}
        >
            <div className={styles['goal-header']}>
                <div className={styles['goal-icon']} style={{backgroundColor: goal.goalColor || '#1890ff'}}>
                    {goal.goalIcon || '🎯'}
                </div>
                <div className={styles['goal-info']}>
                    <h3 className={styles['goal-name']}>{goal.goalName}</h3>
                    <p className={styles['goal-desc']}>{goal.description || '暂无描述'}</p>
                </div>
                {!isCompleted && (
                    <Button
                        type="primary"
                        icon={<CheckOutlined/>}
                        loading={checkingGoalId === goal.id}
                        onClick={() => handleCheckin(goal)}
                    >
                        打卡
                    </Button>
                )}
                {isCompleted && <Tag color="success">今日已打卡</Tag>}
            </div>
            <div className={styles['goal-stats']}>
                <Statistic
                    title="连续打卡"
                    value={goal.currentStreak || 0}
                    suffix="天"
                    prefix={<FireOutlined style={{color: '#ff4d4f'}}/>}
                    className={styles['stat-item']}
                />
                <Statistic
                    title="已打卡"
                    value={goal.checkinCount || 0}
                    suffix="天"
                    className={styles['stat-item']}
                />
                <Statistic
                    title="完成率"
                    value={goal.completionRate || 0}
                    precision={1}
                    suffix="%"
                    className={styles['stat-item']}
                />
            </div>
        </Card>
    )

    const renderFriendFeedItem = (item: HabitCheckinResponse) => (
        <List.Item key={item.id} className={styles['feed-item']}>
            <List.Item.Meta
                avatar={<div className={styles['avatar-placeholder']}>{item.avatarUrl ? <img src={item.avatarUrl} alt=""/> : item.nickname?.charAt(0) || '?'}</div>}
                title={
                    <div className={styles['feed-title']}>
                        <span className={styles['user-name']}>{item.nickname || item.username}</span>
                        <span className={styles['goal-badge']}>
                            {item.goalIcon || '🎯'} {item.goalName}
                        </span>
                    </div>
                }
                description={
                    <div className={styles['feed-content']}>
                        <p>{item.content || '打卡成功！'}</p>
                        <div className={styles['feed-meta']}>
                            <span>
                                <ClockCircleOutlined style={{marginRight: 4}}/>
                                {item.checkinTime ? new Date(item.checkinTime).toLocaleString('zh-CN') : ''}
                            </span>
                            <span className={styles['stats']}>
                                <span style={{marginRight: 16}}>❤️ {item.likeCount || 0}</span>
                                <span>💬 {item.commentCount || 0}</span>
                            </span>
                        </div>
                    </div>
                }
            />
        </List.Item>
    )

    return (
        <div className={styles['home-page']}>
            <Row gutter={[16, 16]}>
                <Col xs={24} lg={6}>
                    <Card className={styles['stat-card']}>
                        <Statistic
                            title="当前连续打卡"
                            value={homeData?.currentStreak || 0}
                            suffix="天"
                            prefix={<FireOutlined style={{color: '#ff4d4f'}}/>}
                        />
                    </Card>
                </Col>
                <Col xs={24} lg={6}>
                    <Card className={styles['stat-card']}>
                        <Statistic
                            title="最长连续打卡"
                            value={homeData?.longestStreak || 0}
                            suffix="天"
                            prefix={<TrophyOutlined style={{color: '#faad14'}}/>}
                        />
                    </Card>
                </Col>
                <Col xs={24} lg={6}>
                    <Card className={styles['stat-card']}>
                        <Statistic
                            title="今日完成率"
                            value={homeData?.todayCompletionRate || 0}
                            precision={1}
                            suffix="%"
                            prefix={<CheckOutlined style={{color: '#52c41a'}}/>}
                        />
                    </Card>
                </Col>
                <Col xs={24} lg={6}>
                    <Card className={styles['stat-card']}>
                        <Statistic
                            title="待处理好友申请"
                            value={homeData?.pendingFriendRequests || 0}
                            prefix={<TeamOutlined style={{color: '#1890ff'}}/>}
                        />
                    </Card>
                </Col>
            </Row>

            <Row gutter={[16, 16]}>
                <Col xs={24} lg={14}>
                    <Card
                        title={
                            <div className={styles['card-title']}>
                                <Badge count={homeData?.pendingGoals?.length || 0} showZero>
                                    <span>今日待打卡</span>
                                </Badge>
                                <Button
                                    type="link"
                                    icon={<PlusOutlined/>}
                                    onClick={() => navigate('/habit/goals')}
                                >
                                    添加目标
                                </Button>
                            </div>
                        }
                        loading={loading}
                        className={styles['main-card']}
                    >
                        {homeData?.pendingGoals && homeData.pendingGoals.length > 0 ? (
                            <div className={styles['goal-list']}>
                                {homeData.pendingGoals.map((goal) => renderGoalCard(goal, false))}
                            </div>
                        ) : (
                            <Empty description="暂无待打卡目标，快去创建一个吧！"/>
                        )}
                    </Card>

                    {homeData?.completedGoals && homeData.completedGoals.length > 0 && (
                        <Card
                            title={
                                <Badge count={homeData.completedGoals.length} showZero>
                                    <span>今日已完成</span>
                                </Badge>
                            }
                            className={styles['main-card']}
                        >
                            <div className={styles['goal-list']}>
                                {homeData.completedGoals.map((goal) => renderGoalCard(goal, true))}
                            </div>
                        </Card>
                    )}
                </Col>

                <Col xs={24} lg={10}>
                    <Card
                        title="好友动态"
                        loading={loading}
                        className={styles['main-card']}
                    >
                        {homeData?.friendFeed && homeData.friendFeed.length > 0 ? (
                            <List
                                itemLayout="vertical"
                                dataSource={homeData.friendFeed}
                                renderItem={renderFriendFeedItem}
                            />
                        ) : (
                            <Empty description="暂无好友动态"/>
                        )}
                    </Card>

                    {homeData?.recentAchievement && (
                        <Card
                            title="最近解锁成就"
                            className={styles['main-card']}
                        >
                            <div className={styles['achievement-item']}>
                                <div
                                    className={styles['achievement-icon']}
                                    style={{backgroundColor: homeData.recentAchievement.achievementColor || '#1890ff'}}
                                >
                                    {homeData.recentAchievement.achievementIcon || '🏆'}
                                </div>
                                <div className={styles['achievement-info']}>
                                    <h4>{homeData.recentAchievement.achievementName}</h4>
                                    <p>{homeData.recentAchievement.description}</p>
                                    <p className={styles['unlock-time']}>
                                        解锁于：{homeData.recentAchievement.unlockTime
                                            ? new Date(homeData.recentAchievement.unlockTime).toLocaleDateString('zh-CN')
                                            : ''}
                                    </p>
                                </div>
                            </div>
                        </Card>
                    )}
                </Col>
            </Row>
        </div>
    )
}

export default Home
