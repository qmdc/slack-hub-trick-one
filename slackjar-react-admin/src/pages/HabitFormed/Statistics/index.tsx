import React, {useState, useEffect, useCallback} from 'react'
import {Card, Statistic, Row, Col, Select, Tabs, Spin, Empty, Tag, Progress, Tooltip, Descriptions} from 'antd'
import {
    FireOutlined,
    TrophyOutlined,
    CalendarOutlined,
    CheckCircleOutlined,
    TargetOutlined,
    LineChartOutlined,
    BarChartOutlined,
    PieChartOutlined,
} from '@ant-design/icons'
import {
    getStatisticsOverview,
    getWeeklyStatistics,
    getMonthlyStatistics,
    getGoalStatistics,
} from '../../../apis'
import type {
    HabitStatisticsResponse,
    HabitGoalResponse,
    WeeklyData,
    MonthlyData,
} from '../../../apis'
import styles from './statistics.module.scss'

const {Option} = Select
const {TabPane} = Tabs

const Statistics: React.FC = () => {
    const [loading, setLoading] = useState(false)
    const [overview, setOverview] = useState<HabitStatisticsResponse | null>(null)
    const [weeklyData, setWeeklyData] = useState<WeeklyData[]>([])
    const [monthlyData, setMonthlyData] = useState<MonthlyData[]>([])
    const [goalsData, setGoalsData] = useState<HabitGoalResponse[]>([])
    const [activeTab, setActiveTab] = useState('overview')
    const [selectedMonth, setSelectedMonth] = useState<string>('')
    const [selectedYear, setSelectedYear] = useState<string>(String(new Date().getFullYear()))

    const currentYear = new Date().getFullYear()
    const years = Array.from({length: 5}, (_, i) => String(currentYear - i))
    const months = Array.from({length: 12}, (_, i) => String(i + 1).padStart(2, '0'))

    const fetchData = useCallback(async () => {
        setLoading(true)
        try {
            const [overviewRes, weeklyRes, monthlyRes, goalsRes] = await Promise.all([
                getStatisticsOverview(),
                getWeeklyStatistics(),
                getMonthlyStatistics({
                    year: parseInt(selectedYear),
                    month: selectedMonth ? parseInt(selectedMonth) : undefined,
                }),
                getGoalStatistics(),
            ])

            if (overviewRes?.code === 200) {
                setOverview(overviewRes.data)
            }
            if (weeklyRes?.code === 200) {
                setWeeklyData(weeklyRes.data || [])
            }
            if (monthlyRes?.code === 200) {
                setMonthlyData(monthlyRes.data || [])
            }
            if (goalsRes?.code === 200) {
                setGoalsData(goalsRes.data || [])
            }
        } catch (error) {
            console.error(error)
        } finally {
            setLoading(false)
        }
    }, [selectedYear, selectedMonth])

    useEffect(() => {
        fetchData()
    }, [fetchData])

    const getCompletionColor = (rate: number) => {
        if (rate >= 80) return '#52c41a'
        if (rate >= 50) return '#faad14'
        return '#ff4d4f'
    }

    const renderOverview = () => (
        <div className={styles['overview-section']}>
            <Row gutter={[16, 16]}>
                <Col xs={24} sm={12} lg={6}>
                    <Card className={styles['stat-card']}>
                        <Statistic
                            title="总打卡天数"
                            value={overview?.totalCheckinDays || 0}
                            prefix={<CalendarOutlined/>}
                            valueStyle={{color: '#1890ff'}}
                        />
                    </Card>
                </Col>
                <Col xs={24} sm={12} lg={6}>
                    <Card className={styles['stat-card']}>
                        <Statistic
                            title="当前连续打卡"
                            value={overview?.currentStreak || 0}
                            suffix="天"
                            prefix={<FireOutlined/>}
                            valueStyle={{color: '#ff4d4f'}}
                        />
                    </Card>
                </Col>
                <Col xs={24} sm={12} lg={6}>
                    <Card className={styles['stat-card']}>
                        <Statistic
                            title="最长连续打卡"
                            value={overview?.longestStreak || 0}
                            suffix="天"
                            prefix={<TrophyOutlined/>}
                            valueStyle={{color: '#faad14'}}
                        />
                    </Card>
                </Col>
                <Col xs={24} sm={12} lg={6}>
                    <Card className={styles['stat-card']}>
                        <Statistic
                            title="完成目标数"
                            value={overview?.completedGoals || 0}
                            prefix={<CheckCircleOutlined/>}
                            valueStyle={{color: '#52c41a'}}
                        />
                    </Card>
                </Col>
            </Row>

            <Row gutter={[16, 16]} style={{marginTop: 16}}>
                <Col xs={24} lg={12}>
                    <Card title={<span><BarChartOutlined style={{marginRight: 8}}/>本周完成情况</span>}>
                        {weeklyData.length > 0 ? (
                            <div className={styles['weekly-chart']}>
                                {weeklyData.map((item, index) => (
                                    <div key={index} className={styles['week-day-item']}>
                                        <div className={styles['bar-container']}>
                                            <div
                                                className={styles['bar']}
                                                style={{
                                                    height: `${Math.max(5, item.completionRate || 0)}%`,
                                                    backgroundColor: getCompletionColor(item.completionRate || 0),
                                                }}
                                            />
                                        </div>
                                        <div className={styles['day-label']}>{item.dayOfWeek}</div>
                                        <div className={styles['day-value']}>
                                            {item.completionRate || 0}%
                                        </div>
                                    </div>
                                ))}
                            </div>
                        ) : (
                            <Empty description="暂无数据"/>
                        )}
                    </Card>
                </Col>

                <Col xs={24} lg={12}>
                    <Card title={<span><LineChartOutlined style={{marginRight: 8}}/>目标进度</span>}>
                        {goalsData.length > 0 ? (
                            <div className={styles['goals-progress']}>
                                {goalsData.map((goal) => (
                                    <div key={goal.id} className={styles['goal-progress-item']}>
                                        <div className={styles['goal-header']}>
                                            <span
                                                className={styles['goal-icon']}
                                                style={{backgroundColor: goal.goalColor || '#1890ff'}}
                                            >
                                                {goal.goalIcon || '🎯'}
                                            </span>
                                            <div className={styles['goal-info']}>
                                                <span className={styles['goal-name']}>{goal.goalName}</span>
                                                <span className={styles['goal-streak']}>
                                                    <FireOutlined style={{color: '#ff4d4f', marginRight: 4}}/>
                                                    连续 {goal.currentStreak || 0} 天
                                                </span>
                                            </div>
                                            <span className={styles['goal-rate']}>
                                                {goal.completionRate || 0}%
                                            </span>
                                        </div>
                                        <Progress
                                            percent={goal.completionRate || 0}
                                            strokeColor={getCompletionColor(goal.completionRate || 0)}
                                            size="small"
                                            showInfo={false}
                                        />
                                    </div>
                                ))}
                            </div>
                        ) : (
                            <Empty description="暂无目标数据"/>
                        )}
                    </Card>
                </Col>
            </Row>

            <Row gutter={[16, 16]} style={{marginTop: 16}}>
                <Col xs={24} lg={12}>
                    <Card title={<span><PieChartOutlined style={{marginRight: 8}}/>月度统计</span>}>
                        <div className={styles['month-filter']}>
                            <Select
                                value={selectedYear}
                                onChange={setSelectedYear}
                                style={{width: 100, marginRight: 8}}
                            >
                                {years.map((year) => (
                                    <Option key={year} value={year}>{year}年</Option>
                                ))}
                            </Select>
                            <Select
                                value={selectedMonth || undefined}
                                onChange={setSelectedMonth}
                                style={{width: 100}}
                                allowClear
                                placeholder="选择月份"
                            >
                                {months.map((month) => (
                                    <Option key={month} value={month}>{month}月</Option>
                                ))}
                            </Select>
                        </div>
                        {monthlyData.length > 0 ? (
                            <div className={styles['monthly-list']}>
                                {monthlyData.map((item, index) => (
                                    <div key={index} className={styles['month-item']}>
                                        <div className={styles['month-left']}>
                                            <div className={styles['month-badge']}>{item.month}月</div>
                                            <div className={styles['month-info']}>
                                                <div className={styles['month-stats']}>
                                                    <Tag icon={<FireOutlined/>} color="orange">
                                                        连续 {item.maxStreak || 0} 天
                                                    </Tag>
                                                    <Tag icon={<CalendarOutlined/>} color="blue">
                                                        打卡 {item.totalCheckinDays || 0} 天
                                                    </Tag>
                                                </div>
                                                <Progress
                                                    percent={item.completionRate || 0}
                                                    strokeColor={getCompletionColor(item.completionRate || 0)}
                                                    size="small"
                                                />
                                            </div>
                                        </div>
                                        <div className={styles['month-rate']}>
                                            {item.completionRate || 0}%
                                        </div>
                                    </div>
                                ))}
                            </div>
                        ) : (
                            <Empty description="暂无月度数据"/>
                        )}
                    </Card>
                </Col>

                <Col xs={24} lg={12}>
                    <Card title={<span><TargetOutlined style={{marginRight: 8}}/>完成率趋势</span>}>
                        {weeklyData.length > 0 ? (
                            <div className={styles['trend-chart']}>
                                <div className={styles['trend-grid']}>
                                    {Array.from({length: 7}, (_, i) => i + 1).map((val) => (
                                        <div key={val} className={styles['grid-line']}>
                                            <span className={styles['grid-label']}>{val * 15}%</span>
                                            <div className={styles['grid-line-inner']/>
                                        </div>
                                    ))}
                                </div>
                                <svg viewBox="0 0 400 200" className={styles['trend-svg']}>
                                    <defs>
                                        <linearGradient id="trendGradient" x1="0%" y1="0%" x2="0%" y2="100%">
                                            <stop offset="0%" style={{stopColor: '#1890ff', stopOpacity: 0.3}}/>
                                            <stop offset="100%" style={{stopColor: '#1890ff', stopOpacity: 0}}/>
                                        </linearGradient>
                                    </defs>
                                    {weeklyData.length > 1 && (
                                        <>
                                            <polygon
                                                points={weeklyData
                                                    .map((item, index) =>
                                                        `${(index / (weeklyData.length - 1 || 1)) * 400},${200 - (item.completionRate || 0) * 2}`
                                                    )
                                                    .join(' ')} ${(weeklyData.length - 1) * 400 / Math.max(weeklyData.length - 1, 1)},200 0,200 Z
                                                fill="url(#trendGradient)"
                                            />
                                            <polyline
                                                points={weeklyData
                                                    .map((item, index) =>
                                                        `${(index / (weeklyData.length - 1 || 1)) * 400},${200 - (item.completionRate || 0) * 2}`
                                                    )
                                                    .join(' ')}
                                                fill="none"
                                                stroke="#1890ff"
                                                strokeWidth="3"
                                                strokeLinecap="round"
                                                strokeLinejoin="round"
                                            />
                                            {weeklyData.map((item, index) => (
                                                <circle
                                                    key={index}
                                                    cx={(index / (weeklyData.length - 1 || 1)) * 400}
                                                    cy={200 - (item.completionRate || 0) * 2}
                                                    r="6"
                                                    fill="#fff"
                                                    stroke="#1890ff"
                                                    strokeWidth="2"
                                                />
                                            ))}
                                        </>
                                    )}
                                </svg>
                                <div className={styles['trend-labels']}>
                                    {weeklyData.map((item, index) => (
                                        <span key={index} className={styles['trend-label']}>
                                            {item.dayOfWeek}
                                        </span>
                                    ))}
                                </div>
                            </div>
                        ) : (
                            <Empty description="暂无数据"/>
                        )}
                    </Card>
                </Col>
            </Row>
        </div>
    )

    const renderGoalDetails = () => (
        <div className={styles['goals-detail-section']}>
            {goalsData.length > 0 ? (
                <Row gutter={[16, 16]}>
                    {goalsData.map((goal) => (
                        <Col xs={24} sm={12} lg={8} key={goal.id}>
                            <Card
                                className={styles['goal-detail-card']}
                                style={{borderTopColor: goal.goalColor || '#1890ff', borderTopWidth: 4}}
                            >
                                <div className={styles['goal-detail-header']}>
                                    <span
                                        className={styles['goal-detail-icon']}
                                        style={{backgroundColor: goal.goalColor || '#1890ff'}}
                                    >
                                        {goal.goalIcon || '🎯'}
                                    </span>
                                    <div className={styles['goal-detail-info']}>
                                        <h3 className={styles['goal-detail-name']}>{goal.goalName}</h3>
                                        <Tag color={goal.status === 1 ? 'success' : 'default'}>
                                            {goal.status === 1 ? '进行中' : '已暂停'}
                                        </Tag>
                                    </div>
                                </div>
                                <Descriptions column={2} size="small" className={styles['goal-detail-desc']}>
                                    <Descriptions.Item label="当前连续">
                                        <span style={{color: '#ff4d4f', fontWeight: 600}}>
                                            {goal.currentStreak || 0} 天
                                        </span>
                                    </Descriptions.Item>
                                    <Descriptions.Item label="最长连续">
                                        <span style={{color: '#faad14', fontWeight: 600}}>
                                            {goal.longestStreak || 0} 天
                                        </span>
                                    </Descriptions.Item>
                                    <Descriptions.Item label="总打卡">
                                        {goal.checkinCount || 0} 天
                                    </Descriptions.Item>
                                    <Descriptions.Item label="完成率">
                                        <span style={{color: getCompletionColor(goal.completionRate || 0), fontWeight: 600}}>
                                            {goal.completionRate || 0}%
                                        </span>
                                    </Descriptions.Item>
                                </Descriptions>
                                <Progress
                                    percent={goal.completionRate || 0}
                                    strokeColor={getCompletionColor(goal.completionRate || 0)}
                                    status={goal.completionRate && goal.completionRate >= 100 ? 'success' : undefined}
                                />
                            </Card>
                        </Col>
                    ))}
                </Row>
            ) : (
                <Card>
                    <Empty description="暂无目标数据，请先创建打卡目标"/>
                </Card>
            )}
        </div>
    )

    return (
        <div className={styles['statistics-page']}>
            <Spin spinning={loading}>
                <Tabs
                    activeKey={activeTab}
                    onChange={setActiveTab}
                    items={[
                        {
                            key: 'overview',
                            label: (
                                <span>
                                    <LineChartOutlined style={{marginRight: 8}}/>
                                    统计概览
                                </span>
                            ),
                            children: renderOverview(),
                        },
                        {
                            key: 'goals',
                            label: (
                                <span>
                                    <TargetOutlined style={{marginRight: 8}}/>
                                    目标详情
                                </span>
                            ),
                            children: renderGoalDetails(),
                        },
                    ]}
                />
            </Spin>
        </div>
    )
}

export default Statistics
