import { useState, useEffect, useRef } from 'react';
import { Table, Button, Modal, Form, Input, Select, message, Space, Popconfirm, Tag, Card, Row, Col, Statistic, Empty, Tooltip, Spin } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, HeartOutlined, HeartFilled, PictureOutlined, RedoOutlined, SearchOutlined, CalendarOutlined, EyeOutlined } from '@ant-design/icons';
import { quoteApi, quoteCategoryApi, type Quote, type QuoteCategory, type FavoriteStatus } from '../../../apis/modules/quotecollector';
import dayjs from 'dayjs';

const QuoteList: React.FC = () => {
  const [quoteList, setQuoteList] = useState<Quote[]>([]);
  const [categories, setCategories] = useState<QuoteCategory[]>([]);
  const [selectedCategory, setSelectedCategory] = useState<number | null>(null);
  const [searchKeyword, setSearchKeyword] = useState('');
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [isImageModalVisible, setIsImageModalVisible] = useState(false);
  const [editingItem, setEditingItem] = useState<Quote | null>(null);
  const [form] = Form.useForm();
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [favoriteMap, setFavoriteMap] = useState<Map<number, FavoriteStatus>>(new Map());
  const [dailyQuote, setDailyQuote] = useState<Quote | null>(null);
  const [showDailyModal, setShowDailyModal] = useState(false);
  const canvasRef = useRef<HTMLCanvasElement>(null);

  const fetchQuotes = async (page: number = currentPage, keyword?: string) => {
    setLoading(true);
    try {
      const response = await quoteApi.list({
        page,
        size: pageSize,
        categoryId: selectedCategory || undefined,
        keyword: keyword || searchKeyword
      });
      const data = response.data;
      setQuoteList(data.records);
      setTotal(data.total);
      setCurrentPage(data.current);
      await fetchFavoriteStatuses(data.records);
    } catch {
      message.error('获取名言列表失败');
    } finally {
      setLoading(false);
    }
  };

  const fetchCategories = async () => {
    try {
      const response = await quoteCategoryApi.list();
      setCategories(response.data || []);
    } catch {
      message.error('获取分类列表失败');
    }
  };

  const fetchFavoriteStatuses = async (quotes: Quote[]) => {
    const newMap = new Map(favoriteMap);
    for (const quote of quotes) {
      if (!newMap.has(quote.id)) {
        try {
          const response = await quoteApi.getFavoriteStatus(quote.id);
          newMap.set(quote.id, response.data);
        } catch {
          newMap.set(quote.id, { isFavorite: false, favoriteCount: 0 });
        }
      }
    }
    setFavoriteMap(newMap);
  };

  const fetchDailyQuote = async () => {
    const lastShown = localStorage.getItem('dailyQuoteDate');
    const today = dayjs().format('YYYY-MM-DD');
    if (lastShown !== today) {
      try {
        const response = await quoteApi.getRandom();
        setDailyQuote(response.data);
        localStorage.setItem('dailyQuoteDate', today);
        setTimeout(() => setShowDailyModal(true), 1000);
      } catch {
        console.log('获取每日名言失败');
      }
    }
  };

  useEffect(() => {
    fetchQuotes();
    fetchCategories();
    fetchDailyQuote();
  }, []);

  const handleSearch = () => {
    fetchQuotes(1);
  };

  const handleCategoryChange = (value: number | null) => {
    setSelectedCategory(value);
    fetchQuotes(1);
  };

  const showAddModal = () => {
    setEditingItem(null);
    form.resetFields();
    setIsModalVisible(true);
  };

  const showEditModal = (record: Quote) => {
    setEditingItem(record);
    form.setFieldsValue({
      content: record.content,
      author: record.author,
      source: record.source,
      categoryId: record.categoryId,
    });
    setIsModalVisible(true);
  };

  const handleDelete = async (id: number) => {
    try {
      await quoteApi.delete(id);
      message.success('删除成功');
      fetchQuotes(currentPage);
    } catch {
      message.error('删除失败');
    }
  };

  const handleModalOk = async () => {
    try {
      const values = await form.validateFields();
      const data = {
        content: values.content,
        author: values.author,
        source: values.source,
        categoryId: values.categoryId,
      };

      if (editingItem) {
        await quoteApi.update(editingItem.id, data);
        message.success('更新成功');
      } else {
        await quoteApi.create(data);
        message.success('添加成功');
      }
      setIsModalVisible(false);
      fetchQuotes(1);
    } catch {
      message.error('操作失败');
    }
  };

  const handleModalCancel = () => {
    setIsModalVisible(false);
    setEditingItem(null);
    form.resetFields();
  };

  const handleFavorite = async (id: number) => {
    try {
      const response = await quoteApi.toggleFavorite(id);
      const status = response.data;
      setFavoriteMap(prev => {
        const newMap = new Map(prev);
        newMap.set(id, status);
        return newMap;
      });
      message.success(status.isFavorite ? '已收藏' : '已取消收藏');
    } catch {
      message.error('操作失败');
    }
  };

  const getCategoryName = (categoryId: number) => {
    const cat = categories.find(c => c.id === categoryId);
    return cat ? `${cat.icon} ${cat.name}` : '未分类';
  };

  const handleGenerateImage = (quote: Quote) => {
    setEditingItem(quote);
    setIsImageModalVisible(true);
  };

  const generateImage = () => {
    if (!canvasRef.current || !editingItem) return;
    
    const canvas = canvasRef.current;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    const gradient = ctx.createLinearGradient(0, 0, canvas.width, canvas.height);
    gradient.addColorStop(0, '#667eea');
    gradient.addColorStop(1, '#764ba2');
    ctx.fillStyle = gradient;
    ctx.fillRect(0, 0, canvas.width, canvas.height);

    ctx.fillStyle = 'rgba(0, 0, 0, 0.1)';
    for (let i = 0; i < 20; i++) {
      const x = Math.random() * canvas.width;
      const y = Math.random() * canvas.height;
      const radius = Math.random() * 100 + 50;
      ctx.beginPath();
      ctx.arc(x, y, radius, 0, Math.PI * 2);
      ctx.fill();
    }

    ctx.fillStyle = '#ffffff';
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    
    const maxWidth = canvas.width - 100;
    const fontSize = Math.max(24, Math.min(48, 2800 / editingItem.content.length));
    ctx.font = `300 ${fontSize}px "Microsoft YaHei", sans-serif`;
    
    const lines: string[] = [];
    let currentLine = '';
    for (const char of editingItem.content) {
      const testLine = currentLine + char;
      const metrics = ctx.measureText(testLine);
      if (metrics.width > maxWidth) {
        lines.push(currentLine);
        currentLine = char;
      } else {
        currentLine = testLine;
      }
    }
    lines.push(currentLine);

    const lineHeight = fontSize * 1.5;
    const startY = (canvas.height - (lines.length - 1) * lineHeight) / 2;
    
    lines.forEach((line, i) => {
      ctx.fillText(line, canvas.width / 2, startY + i * lineHeight);
    });

    if (editingItem.author) {
      ctx.font = `400 ${fontSize * 0.6}px "Microsoft YaHei", sans-serif`;
      ctx.fillStyle = 'rgba(255, 255, 255, 0.8)';
      ctx.fillText(`—— ${editingItem.author}`, canvas.width / 2, startY + lines.length * lineHeight + 40);
    }

    const link = document.createElement('a');
    link.download = `quote_${editingItem.id}.png`;
    link.href = canvas.toDataURL('image/png');
    link.click();
    message.success('图片已保存');
  };

  const getRandomQuote = async () => {
    try {
      const response = await quoteApi.getRandom(selectedCategory || undefined);
      setDailyQuote(response.data);
      setShowDailyModal(true);
    } catch {
      message.error('获取随机名言失败');
    }
  };

  const columns = [
    {
      title: '名言内容',
      dataIndex: 'content',
      key: 'content',
      ellipsis: true,
      width: '40%',
      render: (text: string) => (
        <Tooltip title={text}>
          <span className="text-gray-700">{text}</span>
        </Tooltip>
      ),
    },
    { title: '作者', dataIndex: 'author', key: 'author', ellipsis: true },
    { title: '来源', dataIndex: 'source', key: 'source', ellipsis: true },
    {
      title: '分类',
      dataIndex: 'categoryId',
      key: 'categoryId',
      render: (text: number) => <Tag color="blue">{getCategoryName(text)}</Tag>,
    },
    {
      title: '收藏数',
      dataIndex: 'id',
      key: 'favoriteCount',
      render: (id: number) => (
        <span className="text-gray-600">
          <HeartOutlined className="mr-1" />
          {favoriteMap.get(id)?.favoriteCount || 0}
        </span>
      ),
    },
    {
      title: '查看次数',
      dataIndex: 'viewCount',
      key: 'viewCount',
      render: (text: number) => (
        <span className="text-gray-600">
          <EyeOutlined className="mr-1" />
          {text}
        </span>
      ),
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      render: (text: string) => dayjs(text).format('YYYY-MM-DD'),
    },
    {
      title: '操作',
      key: 'actions',
      render: (_: unknown, record: Quote) => (
        <Space>
          <Button
            icon={favoriteMap.get(record.id)?.isFavorite ? <HeartFilled /> : <HeartOutlined />}
            size="small"
            type={favoriteMap.get(record.id)?.isFavorite ? 'primary' : 'default'}
            onClick={() => handleFavorite(record.id)}
          />
          <Button icon={<PictureOutlined />} size="small" onClick={() => handleGenerateImage(record)} />
          <Button icon={<EditOutlined />} size="small" onClick={() => showEditModal(record)} />
          <Popconfirm title="确定删除这条名言吗？" onConfirm={() => handleDelete(record.id)} okText="确定" cancelText="取消">
            <Button icon={<DeleteOutlined />} size="small" danger />
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div className="p-6">
      <Card className="mb-6">
        <Row gutter={16}>
          <Col span={6}>
            <Statistic title="名言总数" value={total} prefix={<CalendarOutlined />} />
          </Col>
          <Col span={6}>
            <Statistic title="分类数量" value={categories.length} prefix={<Tag color="blue" />} />
          </Col>
          <Col span={6}>
            <Statistic title="今日名言" value="1" prefix={<RedoOutlined />} />
          </Col>
          <Col span={6}>
            <Button type="primary" icon={<RedoOutlined />} onClick={getRandomQuote}>
              获取随机名言
            </Button>
          </Col>
        </Row>
      </Card>

      <Card className="mb-6">
        <Space direction="vertical" className="w-full">
          <Row align="middle" gutter={16}>
            <Col span={8}>
              <Input.Search
                placeholder="搜索名言内容或作者"
                prefix={<SearchOutlined />}
                value={searchKeyword}
                onChange={(e) => setSearchKeyword(e.target.value)}
                onSearch={handleSearch}
                className="w-full"
              />
            </Col>
            <Col span={6}>
              <Select
                placeholder="选择分类"
                value={selectedCategory}
                onChange={(value) => handleCategoryChange(value)}
                className="w-full"
              >
                <Select.Option value={null}>全部</Select.Option>
                {categories.map(cat => (
                  <Select.Option key={cat.id} value={cat.id}>
                    {cat.icon} {cat.name}
                  </Select.Option>
                ))}
              </Select>
            </Col>
            <Col span={10}>
              <Space>
                <Button type="primary" icon={<PlusOutlined />} onClick={showAddModal}>
                  添加名言
                </Button>
                <Button icon={<RedoOutlined />} onClick={() => fetchQuotes(1)}>
                  刷新
                </Button>
              </Space>
            </Col>
          </Row>
        </Space>
      </Card>

      <Card title="名言列表">
        <Spin spinning={loading}>
          <Table
            columns={columns}
            dataSource={quoteList}
            rowKey="id"
            pagination={{
              current: currentPage,
              pageSize: pageSize,
              total: total,
              onChange: (page, size) => {
                setCurrentPage(page);
                setPageSize(size);
                fetchQuotes(page);
              },
            }}
            bordered
            locale={{ emptyText: <Empty description="暂无名言记录" /> }}
          />
        </Spin>
      </Card>

      <Modal
        title={editingItem ? '编辑名言' : '添加名言'}
        open={isModalVisible}
        onOk={handleModalOk}
        onCancel={handleModalCancel}
        width={600}
      >
        <Form form={form} layout="vertical">
          <Form.Item name="content" label="名言内容" rules={[{ required: true, message: '请输入名言内容' }]}>
            <Input.TextArea placeholder="请输入名言内容" rows={4} />
          </Form.Item>
          <Form.Item name="author" label="作者">
            <Input placeholder="例如：莎士比亚" />
          </Form.Item>
          <Form.Item name="source" label="来源">
            <Input placeholder="例如：哈姆雷特" />
          </Form.Item>
          <Form.Item name="categoryId" label="分类" rules={[{ required: true, message: '请选择分类' }]}>
            <Select placeholder="请选择分类">
              {categories.map(cat => (
                <Select.Option key={cat.id} value={cat.id}>
                  {cat.icon} {cat.name}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="生成精美配图"
        open={isImageModalVisible}
        onCancel={() => setIsImageModalVisible(false)}
        footer={null}
        width={700}
      >
        <div className="text-center mb-4">
          <canvas
            ref={canvasRef}
            width={600}
            height={400}
            className="border rounded-lg"
          />
        </div>
        <Space className="float-right">
          <Button onClick={() => setIsImageModalVisible(false)}>取消</Button>
          <Button type="primary" onClick={generateImage}>
            保存图片
          </Button>
        </Space>
      </Modal>

      <Modal
        title="每日名言"
        open={showDailyModal}
        onCancel={() => setShowDailyModal(false)}
        footer={null}
        width={500}
      >
        {dailyQuote && (
          <div className="text-center py-8">
            <div className="text-4xl mb-4">✨</div>
            <p className="text-xl text-gray-800 leading-relaxed mb-4">
              "{dailyQuote.content}"
            </p>
            {dailyQuote.author && (
              <p className="text-gray-500">—— {dailyQuote.author}</p>
            )}
            <div className="mt-6">
              <Button type="primary" onClick={() => setShowDailyModal(false)}>
                知道了
              </Button>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default QuoteList;