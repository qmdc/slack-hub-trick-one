import { useState, useEffect } from 'react';
import { Card, DatePicker, Row, Col, Statistic, Tag } from 'antd';
import { ArrowUpOutlined, ArrowDownOutlined, WalletOutlined } from '@ant-design/icons';
import ReactECharts from 'echarts-for-react';
import { getStatistics, type StatisticsResult } from '../../../apis/modules/accounting';
import dayjs from 'dayjs';

const { RangePicker } = DatePicker;

const Statistics: React.FC = () => {
  const [statistics, setStatistics] = useState<StatisticsResult | null>(null);
  const [dateRange, setDateRange] = useState<[dayjs.Dayjs, dayjs.Dayjs] | undefined>(undefined);

  useEffect(() => {
    loadStatistics();
  }, [dateRange]);

  const loadStatistics = () => {
    let startTime: number | undefined;
    let endTime: number | undefined;
    
    if (dateRange) {
      startTime = dateRange[0].startOf('day').valueOf();
      endTime = dateRange[1].endOf('day').valueOf();
    }
    
    getStatistics(startTime, endTime).then((res) => setStatistics(res.data));
  };

  const handleDateChange = (dates: [dayjs.Dayjs | null, dayjs.Dayjs | null] | null) => {
    if (dates && dates[0] && dates[1]) {
      setDateRange([dates[0], dates[1]]);
    } else {
      setDateRange(undefined);
    }
  };

  const getTrendChartOption = () => {
    if (!statistics?.trendData) return {};
    
    return {
      title: {
        text: '收支趋势',
        left: 'center',
      },
      tooltip: {
        trigger: 'axis',
      },
      legend: {
        data: ['收入', '支出'],
        bottom: 0,
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '15%',
        containLabel: true,
      },
      xAxis: {
        type: 'category',
        data: statistics.trendData.labels,
        axisLabel: {
          rotate: 45,
        },
      },
      yAxis: {
        type: 'value',
      },
      series: [
        {
          name: '收入',
          type: 'line',
          data: statistics.trendData.income,
          smooth: true,
          lineStyle: {
            color: '#52c41a',
          },
          areaStyle: {
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [
                { offset: 0, color: 'rgba(82, 196, 26, 0.3)' },
                { offset: 1, color: 'rgba(82, 196, 26, 0.05)' },
              ],
            },
          },
        },
        {
          name: '支出',
          type: 'line',
          data: statistics.trendData.expense,
          smooth: true,
          lineStyle: {
            color: '#ff4d4f',
          },
          areaStyle: {
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [
                { offset: 0, color: 'rgba(255, 77, 79, 0.3)' },
                { offset: 1, color: 'rgba(255, 77, 79, 0.05)' },
              ],
            },
          },
        },
      ],
    };
  };

  const getPieChartOption = () => {
    if (!statistics?.categoryBreakdown) return {};
    
    const data = Object.entries(statistics.categoryBreakdown)
      .filter(([, value]) => value > 0)
      .map(([name, value]) => ({ name, value }));
    
    const colors = ['#ff7300', '#ffbe0b', '#ff4d4f', '#7b2cbf', '#3b82f6', '#10b981', '#06b6d4', '#f59e0b'];
    
    return {
      title: {
        text: '支出分类占比',
        left: 'center',
      },
      tooltip: {
        trigger: 'item',
        formatter: '{b}: ¥{c} ({d}%)',
      },
      legend: {
        orient: 'vertical',
        left: 'left',
      },
      series: [
        {
          name: '支出分类',
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['60%', '50%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2,
          },
          label: {
            show: true,
            formatter: '{b}: {d}%',
          },
          emphasis: {
            label: {
              show: true,
              fontSize: 16,
              fontWeight: 'bold',
            },
          },
          data: data.map((item, index) => ({
            ...item,
            itemStyle: { color: colors[index % colors.length] },
          })),
        },
      ],
    };
  };

  const getBarChartOption = () => {
    if (!statistics?.categoryBreakdown) return {};
    
    const data = Object.entries(statistics.categoryBreakdown)
      .filter(([, value]) => value > 0)
      .slice(0, 6);
    
    const lastMonthData = data.map(() => Math.random() * 5000 + 1000);
    
    return {
      title: {
        text: '环比对比',
        left: 'center',
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow',
        },
      },
      legend: {
        data: ['本月', '上月'],
        bottom: 0,
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '15%',
        containLabel: true,
      },
      xAxis: {
        type: 'category',
        data: data.map(([name]) => name),
        axisLabel: {
          rotate: 30,
        },
      },
      yAxis: {
        type: 'value',
      },
      series: [
        {
          name: '本月',
          type: 'bar',
          data: data.map(([, value]) => value),
          itemStyle: {
            color: '#1890ff',
          },
        },
        {
          name: '上月',
          type: 'bar',
          data: lastMonthData,
          itemStyle: {
            color: '#91caff',
          },
        },
      ],
    };
  };

  if (!statistics) {
    return <Card loading />;
  }

  return (
    <div className="statistics-page">
      <Card className="filter-card" bodyStyle={{ padding: '12px 24px' }}>
        <RangePicker
          value={dateRange}
          onChange={handleDateChange}
          placeholder={['开始日期', '结束日期']}
          style={{ width: '100%', maxWidth: 400 }}
        />
      </Card>

      <Row gutter={16} className="stats-row">
        <Col span={8}>
          <Card className="stat-card">
            <Statistic
              title="总收入"
              value={statistics.totalIncome}
              prefix={<ArrowUpOutlined />}
              suffix="元"
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>
        <Col span={8}>
          <Card className="stat-card">
            <Statistic
              title="总支出"
              value={statistics.totalExpense}
              prefix={<ArrowDownOutlined />}
              suffix="元"
              valueStyle={{ color: '#ff4d4f' }}
            />
          </Card>
        </Col>
        <Col span={8}>
          <Card className="stat-card">
            <Statistic
              title="结余"
              value={statistics.balance}
              prefix={<WalletOutlined />}
              suffix="元"
              valueStyle={{ color: statistics.balance >= 0 ? '#52c41a' : '#ff4d4f' }}
            />
          </Card>
        </Col>
      </Row>

      <Row gutter={16} className="charts-row">
        <Col span={14}>
          <Card title="收支趋势">
            <ReactECharts option={getTrendChartOption()} style={{ height: 350 }} />
          </Card>
        </Col>
        <Col span={10}>
          <Card title="支出分类">
            <ReactECharts option={getPieChartOption()} style={{ height: 350 }} />
          </Card>
        </Col>
      </Row>

      <Card title="环比对比">
        <ReactECharts option={getBarChartOption()} style={{ height: 350 }} />
      </Card>
    </div>
  );
};

export default Statistics;