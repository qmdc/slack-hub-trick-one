import React, {useState, useEffect, useCallback} from 'react'
import {Card, Row, Col, Tag, Empty, Tabs, Spin, Tooltip, Badge, Divider, Statistic} from 'antd'
import {
    TrophyOutlined,
    LockOutlined,
    StarOutlined,
    FireOutlined,
    CheckCircleOutlined,
    CalendarOutlined,
    TeamOutlined,
} from '@ant-design/icons'
import {
    getAchievementList,
    getUnlockedAchievements,
    getRecentUnlocked,
} from '../../../apis'
import type {HabitAchievementResponse} from '../../../apis'
import styles from './achievements.module.scss'

const {TabPane} = Tabs

const Achievements: React.FC = () => {
    const [loading, setLoading] = useState(false)
    const [activeTab, setActiveTab] = useState('all')
    const [allAchievements, setAllAchievements] = useState<HabitAchievementResponse[]>([])
    const [unlockedAchievements, setUnlockedAchievements] = useState<HabitAchievementResponse[]>([])
    const [recentAchievements, setRecentAchievements] = useState<HabitAchievementResponse[]>([])

    const fetchData = useCallback(async () => {
        setLoading(true)
        try {
            const [allRes, unlockedRes, recentRes] = await Promise.all([
                getAchievementList(),
                getUnlockedAchievements(),
                getRecentUnlocked(),
            ])

            if (allRes?.code === 200) {
                setAllAchievements(allRes.data || [])
            }
            if (unlockedRes?.code === 200) {
                setUnlockedAchievements(unlockedRes.data || [])
            }
            if (recentRes?.code === 200) {
                setRecentAchievements(recentRes.data || [])
            }
        } catch (error) {
            console.error(error)
        } finally {
            setLoading(false)
        }
    }, [])

    useEffect(() => {
        fetchData()
    }, [fetchData])

    const isUnlocked = (achievementId: number) => {
        return unlockedAchievements.some(a => a.id === achievementId)
    }

    const getUnlockTime = (achievementId: number) => {
        const achievement = unlockedAchievements.find(a => a.id === achievementId)
        return achievement?.unlockTime
    }

    const renderAchievementCard = (achievement: HabitAchievementResponse) => {
        const unlocked = isUnlocked(achievement.id)
        const unlockTime = getUnlockTime(achievement.id)

        return (
            <Col xs={24} sm={12} lg={8} key={achievement.id}>
                <Card
                    className={`${styles['achievement-card']} ${unlocked ? styles['unlocked'] : styles['locked']}`}
                    hoverable
                >
                    <div className={styles['card-inner']}>
                        <div
                            className={styles['achievement-icon-wrapper']}
                            style={{backgroundColor: unlocked ? (achievement.achievementColor || '#1890ff') : '#d9d9d9'}}
                        >
                            <span className={styles['achievement-icon']}>
                                {unlocked ? (achievement.achievementIcon || '🏆') : <LockOutlined/>}
                            </span>
                            {unlocked && (
                                <Badge
                                    count={<CheckCircleOutlined style={{color: '#52c41a'}}/>}
                                    className={styles['unlock-badge']}
                                />
                            )}
                        </div>

                        <div className={styles['achievement-info']}>
                            <h3 className={styles['achievement-name']}>
                                {achievement.achievementName}
                                {achievement.achievementLevel && (
                                    <Tag
                                        color={achievement.achievementLevel === 1 ? 'blue' :
                                               achievement.achievementLevel === 2 ? 'gold' :
                                               achievement.achievementLevel === 3 ? 'orange' : 'default'}
                                    >
                                        {achievement.achievementLevel === 1 ? '铜' :
                                         achievement.achievementLevel === 2 ? '银' :
                                         achievement.achievementLevel === 3 ? '金' : '普通'}
                                    </Tag>
                                )}
                            </h3>
                            <p className={styles['achievement-desc']}>
                                {achievement.description}
                            </p>

                            <div className={styles['achievement-meta']}>
                                {achievement.achievementType && (
                                    <Tag icon={<StarOutlined/>}>
                                        {achievement.achievementType === 'streak' ? '连续打卡' :
                                         achievement.achievementType === 'total' ? '累计打卡' :
                                         achievement.achievementType === 'friend' ? '好友互动' : '其他'}
                                    </Tag>
                                )}
                                {achievement.rewardPoints && (
                                    <Tag color="gold">奖励 {achievement.rewardPoints} 积分</Tag>
                                )}
                            </div>

                            {unlockTime && (
                                <div className={styles['unlock-time']}>
                                    <CalendarOutlined style={{marginRight: 4}}/>
                                    解锁于：{new Date(unlockTime).toLocaleString('zh-CN')}
                                </div>
                            )}
                        </div>
                    </div>
                </Card>
            </Col>
        )
    }

    const renderAll = () => (
        <div className={styles['achievements-grid']}>
            {allAchievements.length > 0 ? (
                <Row gutter={[16, 16]}>
                    {allAchievements.map(renderAchievementCard)}
                </Row>
            ) : (
                <Empty description="暂无成就数据"/>
            )}
        </div>
    )

    const renderUnlocked = () => (
        <div className={styles['achievements-grid']}>
            {unlockedAchievements.length > 0 ? (
                <Row gutter={[16, 16]}>
                    {unlockedAchievements.map(renderAchievementCard)}
                </Row>
            ) : (
                <Empty description="暂无已解锁的成就，继续努力！"/>
            )}
        </div>
    )

    const renderLocked = () => {
        const lockedAchievements = allAchievements.filter(a => !isUnlocked(a.id))
        return (
            <div className={styles['achievements-grid']}>
                {lockedAchievements.length > 0 ? (
                    <Row gutter={[16, 16]}>
                        {lockedAchievements.map(renderAchievementCard)}
                    </Row>
                ) : (
                    <Empty description="恭喜！你已解锁所有成就！"/>
                )}
            </div>
        )
    }

    return (
        <div className={styles['achievements-page']}>
            <Spin spinning={loading}>
                {recentAchievements.length > 0 && (
                    <Card className={styles['recent-card']} style={{marginBottom: 16}}>
                        <div className={styles['recent-header']}>
                            <h3 style={{margin: 0}}>
                                <TrophyOutlined style={{marginRight: 8, color: '#faad14'}}/>
                                最近解锁的成就
                            </h3>
                        </div>
                        <Row gutter={[16, 16]} style={{marginTop: 16}}>
                            {recentAchievements.map(renderAchievementCard)}
                        </Row>
                    </Card>
                )}

                <Row gutter={[16, 16]} style={{marginBottom: 16}}>
                    <Col xs={12} sm={6}>
                        <Card className={styles['stat-card']}>
                            <Statistic
                                title="全部成就"
                                value={allAchievements.length}
                                suffix="个"
                                valueStyle={{color: '#1890ff'}}
                            />
                        </Card>
                    </Col>
                    <Col xs={12} sm={6}>
                        <Card className={styles['stat-card']}>
                            <Statistic
                                title="已解锁"
                                value={unlockedAchievements.length}
                                suffix="个"
                                valueStyle={{color: '#52c41a'}}
                            />
                        </Card>
                    </Col>
                    <Col xs={12} sm={6}>
                        <Card className={styles['stat-card']}>
                            <Statistic
                                title="未解锁"
                                value={allAchievements.length - unlockedAchievements.length}
                                suffix="个"
                                valueStyle={{color: '#8c8c8c'}}
                            />
                        </Card>
                    </Col>
                    <Col xs={12} sm={6}>
                        <Card className={styles['stat-card']}>
                            <Statistic
                                title="完成率"
                                value={allAchievements.length > 0
                                    ? Math.round((unlockedAchievements.length / allAchievements.length) * 100)
                                    : 0}
                                suffix="%"
                                valueStyle={{color: '#faad14'}}
                            />
                        </Card>
                    </Col>
                </Row>

                <Card className={styles['tabs-card']}>
                    <Tabs
                        activeKey={activeTab}
                        onChange={setActiveTab}
                        items={[
                            {
                                key: 'all',
                                label: (
                                    <span>
                                        <TrophyOutlined style={{marginRight: 8}}/>
                                        全部成就
                                        <Badge count={allAchievements.length} size="small" style={{marginLeft: 8}}/>
                                    </span>
                                ),
                                children: renderAll(),
                            },
                            {
                                key: 'unlocked',
                                label: (
                                    <span>
                                        <CheckCircleOutlined style={{marginRight: 8, color: '#52c41a'}}/>
                                        已解锁
                                        <Badge
                                            count={unlockedAchievements.length}
                                            size="small"
                                            style={{marginLeft: 8, backgroundColor: '#52c41a'}}
                                        />
                                    </span>
                                ),
                                children: renderUnlocked(),
                            },
                            {
                                key: 'locked',
                                label: (
                                    <span>
                                        <LockOutlined style={{marginRight: 8}}/>
                                        未解锁
                                        <Badge
                                            count={allAchievements.length - unlockedAchievements.length}
                                            size="small"
                                            style={{marginLeft: 8, backgroundColor: '#8c8c8c'}}
                                        />
                                    </span>
                                ),
                                children: renderLocked(),
                            },
                        ]}
                    />
                </Card>

                <Card title="成就等级说明" style={{marginTop: 16}}>
                    <Row gutter={[16, 16]}>
                        <Col xs={24} sm={8}>
                            <div className={styles['level-card']}>
                                <div className={`${styles['level-icon']} ${styles['level-bronze']}`}>🥉</div>
                                <div className={styles['level-info']}>
                                    <h4>铜级成就</h4>
                                    <p>基础成就，容易达成，适合新手入门</p>
                                    <Tag color="blue">奖励积分较少</Tag>
                                </div>
                            </div>
                        </Col>
                        <Col xs={24} sm={8}>
                            <div className={styles['level-card']}>
                                <div className={`${styles['level-icon']} ${styles['level-silver']}`}>🥈</div>
                                <div className={styles['level-info']}>
                                    <h4>银级成就</h4>
                                    <p>中级成就，需要一定的坚持和努力</p>
                                    <Tag color="gold">奖励积分中等</Tag>
                                </div>
                            </div>
                        </Col>
                        <Col xs={24} sm={8}>
                            <div className={styles['level-card']}>
                                <div className={`${styles['level-icon']} ${styles['level-gold']}`}>🥇</div>
                                <div className={styles['level-info']}>
                                    <h4>金级成就</h4>
                                    <p>高级成就，需要长期坚持才能解锁</p>
                                    <Tag color="orange">奖励积分丰厚</Tag>
                                </div>
                            </div>
                        </Col>
                    </Row>

                    <Divider/>

                    <Row gutter={[16, 16]}>
                        <Col xs={24} sm={8}>
                            <div className={styles['type-card']}>
                                <FireOutlined className={styles['type-icon']} style={{color: '#ff4d4f'}}/>
                                <h4>连续打卡类型</h4>
                                <p>保持连续打卡天数即可解锁，考验你的毅力和坚持</p>
                                <Tag>例：连续打卡7天、30天、100天</Tag>
                            </div>
                        </Col>
                        <Col xs={24} sm={8}>
                            <div className={styles['type-card']}>
                                <CalendarOutlined className={styles['type-icon']} style={{color: '#1890ff'}}/>
                                <h4>累计打卡类型</h4>
                                <p>累计打卡达到一定天数即可解锁，厚积薄发</p>
                                <Tag>例：累计打卡30天、100天、365天</Tag>
                            </div>
                        </Col>
                        <Col xs={24} sm={8}>
                            <div className={styles['type-card']}>
                                <TeamOutlined className={styles['type-icon']} style={{color: '#722ed1'}}/>
                                <h4>好友互动类型</h4>
                                <p>通过添加好友、互相点赞评论等互动解锁</p>
                                <Tag>例：添加5个好友、获得100个赞</Tag>
                            </div>
                        </Col>
                    </Row>
                </Card>
            </Spin>
        </div>
    )
}

export default Achievements
