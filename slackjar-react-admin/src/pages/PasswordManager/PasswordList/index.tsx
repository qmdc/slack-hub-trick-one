import { useState, useEffect } from 'react';
import { Table, Button, Modal, Form, Input, Select, message, Space, Popconfirm, Tag, Card, Row, Col, Statistic, Badge, Slider, Checkbox, Empty } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, EyeOutlined, SearchOutlined, CopyOutlined, KeyOutlined, ReloadOutlined } from '@ant-design/icons';
import { getPasswordList, searchPasswords, createPassword, updatePassword, deletePassword, checkPasswordStrength, generatePassword, getCategoryList, getPasswordStatistics, type PasswordEntryDTO, type PasswordEntryCreateRequest, type PasswordCategory } from '../../../apis/modules/passwordmanager';
import dayjs from 'dayjs';

const PasswordList: React.FC = () => {
  const [passwordList, setPasswordList] = useState<PasswordEntryDTO[]>([]);
  const [categories, setCategories] = useState<PasswordCategory[]>([]);
  const [statistics, setStatistics] = useState<Record<string, number>>({});
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [isGenerateModalVisible, setIsGenerateModalVisible] = useState(false);
  const [editingItem, setEditingItem] = useState<PasswordEntryDTO | null>(null);
  const [searchKeyword, setSearchKeyword] = useState('');
  const [selectedCategory, setSelectedCategory] = useState<string>('');
  const [form] = Form.useForm();
  const [generateForm] = Form.useForm();
  const [visiblePasswords, setVisiblePasswords] = useState<Set<number>>(new Set());
  const [passwordStrength, setPasswordStrength] = useState<number>(0);

  const fetchPasswordList = async (category?: string) => {
    try {
      const response = await getPasswordList(category);
      setPasswordList(response.data || []);
    } catch {
      message.error('获取密码列表失败');
    }
  };

  const fetchCategories = async () => {
    try {
      const response = await getCategoryList();
      setCategories(response.data || []);
    } catch {
      message.error('获取分类列表失败');
    }
  };

  const fetchStatistics = async () => {
    try {
      const response = await getPasswordStatistics();
      setStatistics(response.data || {});
    } catch {
      message.error('获取统计数据失败');
    }
  };

  useEffect(() => {
    fetchPasswordList();
    fetchCategories();
    fetchStatistics();
  }, []);

  const handleSearch = async () => {
    if (!searchKeyword.trim()) {
      fetchPasswordList(selectedCategory || undefined);
      return;
    }
    try {
      const response = await searchPasswords(searchKeyword);
      setPasswordList(response.data || []);
    } catch {
      message.error('搜索失败');
    }
  };

  const handleCategoryChange = (value: string) => {
    setSelectedCategory(value);
    fetchPasswordList(value || undefined);
  };

  const showAddModal = () => {
    setEditingItem(null);
    form.resetFields();
    setIsModalVisible(true);
  };

  const showEditModal = (record: PasswordEntryDTO) => {
    setEditingItem(record);
    form.setFieldsValue({
      websiteName: record.websiteName,
      website: record.website,
      account: record.account,
      password: record.password,
      category: record.category,
      notes: record.notes,
    });
    setIsModalVisible(true);
  };

  const handleDelete = async (id: number) => {
    try {
      await deletePassword(id);
      message.success('删除成功');
      fetchPasswordList(selectedCategory || undefined);
      fetchStatistics();
    } catch {
      message.error('删除失败');
    }
  };

  const handleModalOk = async () => {
    try {
      const values = await form.validateFields();
      const data: PasswordEntryCreateRequest = {
        websiteName: values.websiteName,
        website: values.website,
        account: values.account,
        password: values.password,
        category: values.category,
        notes: values.notes,
      };

      if (editingItem) {
        await updatePassword(editingItem.id, data);
        message.success('更新成功');
      } else {
        await createPassword(data);
        message.success('添加成功');
      }
      setIsModalVisible(false);
      fetchPasswordList(selectedCategory || undefined);
      fetchStatistics();
    } catch {
      message.error('操作失败');
    }
  };

  const handleModalCancel = () => {
    setIsModalVisible(false);
    setEditingItem(null);
    form.resetFields();
  };

  const togglePasswordVisible = (id: number) => {
    setVisiblePasswords(prev => {
      const newSet = new Set(prev);
      if (newSet.has(id)) {
        newSet.delete(id);
      } else {
        newSet.add(id);
      }
      return newSet;
    });
  };

  const copyPassword = async (password: string) => {
    try {
      await navigator.clipboard.writeText(password);
      message.success('已复制到剪贴板');
    } catch {
      message.error('复制失败');
    }
  };

  const showGenerateModal = () => {
    generateForm.resetFields();
    setPasswordStrength(0);
    setIsGenerateModalVisible(true);
  };

  const handleGenerate = async () => {
    try {
      const values = generateForm.getFieldsValue();
      const options = {
        length: values.length || 16,
        includeUppercase: values.includeUppercase !== undefined ? values.includeUppercase : true,
        includeLowercase: values.includeLowercase !== undefined ? values.includeLowercase : true,
        includeNumbers: values.includeNumbers !== undefined ? values.includeNumbers : true,
        includeSpecialChars: values.includeSpecialChars !== undefined ? values.includeSpecialChars : false,
      };
      const response = await generatePassword(options);
      const generatedPwd = response.data || '';
      generateForm.setFieldsValue({ generatedPassword: generatedPwd });
      const strength = await checkPasswordStrength(generatedPwd);
      setPasswordStrength(strength.data || 0);
    } catch {
      message.error('生成失败');
    }
  };

  const useGeneratedPassword = () => {
    const generatedPassword = generateForm.getFieldValue('generatedPassword');
    form.setFieldsValue({ password: generatedPassword });
    setIsGenerateModalVisible(false);
  };

  const handlePasswordChange = async (password: string) => {
    if (password) {
      const response = await checkPasswordStrength(password);
      setPasswordStrength(response.data || 0);
    } else {
      setPasswordStrength(0);
    }
  };

  const getStrengthTag = (strength: number) => {
    const colors: Record<number, string> = { 0: 'error', 1: 'warning', 2: 'success' };
    const texts: Record<number, string> = { 0: '弱', 1: '中', 2: '强' };
    return <Tag color={colors[strength]}>{texts[strength]}</Tag>;
  };

  const getCategoryTag = (category: string) => {
    const cat = categories.find(c => c.code === category);
    if (cat) {
      return <Tag color={cat.color}>{cat.name}</Tag>;
    }
    return <Tag>{category}</Tag>;
  };

  const columns = [
    {
      title: '网站名称',
      dataIndex: 'websiteName',
      key: 'websiteName',
      ellipsis: true,
      render: (text: string, record: PasswordEntryDTO) => (
        <a href={record.website} target="_blank" rel="noopener noreferrer" className="text-blue-500 hover:text-blue-700">
          {text}
        </a>
      ),
    },
    { title: '账号', dataIndex: 'account', key: 'account', ellipsis: true },
    {
      title: '密码',
      dataIndex: 'password',
      key: 'password',
      render: (text: string, record: PasswordEntryDTO) => (
        <Space>
          <span className="font-mono text-sm">{visiblePasswords.has(record.id) ? text : '******'}</span>
          <Button icon={<EyeOutlined />} size="small" onClick={() => togglePasswordVisible(record.id)} />
          <Button icon={<CopyOutlined />} size="small" onClick={() => copyPassword(text)} />
        </Space>
      ),
    },
    { title: '分类', dataIndex: 'category', key: 'category', render: (text: string) => getCategoryTag(text) },
    { title: '密码强度', dataIndex: 'passwordStrength', key: 'passwordStrength', render: (text: number) => getStrengthTag(text) },
    {
      title: '最后登录',
      dataIndex: 'lastLoginTime',
      key: 'lastLoginTime',
      render: (text: number) => {
        if (!text) return <span className="text-gray-400">从未</span>;
        return dayjs(text).format('YYYY-MM-DD HH:mm');
      },
    },
    {
      title: '操作',
      key: 'actions',
      render: (_: unknown, record: PasswordEntryDTO) => (
        <Space>
          <Button icon={<EditOutlined />} size="small" onClick={() => showEditModal(record)} />
          <Popconfirm title="确定删除这条记录吗？" onConfirm={() => handleDelete(record.id)} okText="确定" cancelText="取消">
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
          <Col span={8}><Statistic title="总密码数" value={passwordList.length} prefix={<KeyOutlined />} /></Col>
          <Col span={8}><Statistic title="工作分类" value={statistics['work'] || 0} prefix={<Badge color="#1890ff" />} /></Col>
          <Col span={8}><Statistic title="个人分类" value={statistics['personal'] || 0} prefix={<Badge color="#52c41a" />} /></Col>
          <Col span={8}><Statistic title="社交分类" value={statistics['social'] || 0} prefix={<Badge color="#faad14" />} /></Col>
        </Row>
      </Card>

      <Card className="mb-6">
        <Space direction="vertical" className="w-full">
          <Row align="middle" gutter={16}>
            <Col span={8}>
              <Input.Search
                placeholder="搜索网站名称或账号"
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
                onChange={handleCategoryChange}
                className="w-full"
              >
                <Select.Option value="">全部</Select.Option>
                {categories.map(cat => (
                  <Select.Option key={cat.code} value={cat.code}>{cat.name}</Select.Option>
                ))}
              </Select>
            </Col>
            <Col span={10}>
              <Space>
                <Button type="primary" icon={<PlusOutlined />} onClick={showAddModal}>添加密码</Button>
                <Button icon={<ReloadOutlined />} onClick={() => fetchPasswordList(selectedCategory || undefined)}>刷新</Button>
                <Button icon={<KeyOutlined />} onClick={showGenerateModal}>生成密码</Button>
              </Space>
            </Col>
          </Row>
        </Space>
      </Card>

      <Card title="密码列表">
        <Table columns={columns} dataSource={passwordList} rowKey="id" pagination={{ pageSize: 10 }} bordered locale={{ emptyText: <Empty description="暂无密码记录" /> }} />
      </Card>

      <Modal
        title={editingItem ? '编辑密码' : '添加密码'}
        open={isModalVisible}
        onOk={handleModalOk}
        onCancel={handleModalCancel}
        width={600}
      >
        <Form form={form} layout="vertical">
          <Form.Item name="websiteName" label="网站名称" rules={[{ required: true, message: '请输入网站名称' }]}>
            <Input placeholder="例如：GitHub" />
          </Form.Item>
          <Form.Item name="website" label="网站地址">
            <Input placeholder="例如：https://github.com" />
          </Form.Item>
          <Form.Item name="account" label="账号" rules={[{ required: true, message: '请输入账号' }]}>
            <Input placeholder="例如：username" />
          </Form.Item>
          <Form.Item name="password" label="密码" rules={[{ required: true, message: '请输入密码' }]}>
            <Input.Password placeholder="请输入密码" onChange={(e) => handlePasswordChange(e.target.value)} />
          </Form.Item>
          <div className="mb-4">
            <span className="text-sm text-gray-600 mr-2">密码强度：</span>
            {getStrengthTag(passwordStrength)}
          </div>
          <Form.Item name="category" label="分类" rules={[{ required: true, message: '请选择分类' }]}>
            <Select placeholder="请选择分类">
              {categories.map(cat => (<Select.Option key={cat.code} value={cat.code}>{cat.name}</Select.Option>))}
            </Select>
          </Form.Item>
          <Form.Item name="notes" label="备注">
            <Input.TextArea placeholder="添加备注信息（可选）" rows={3} />
          </Form.Item>
        </Form>
      </Modal>

      <Modal title="生成随机密码" open={isGenerateModalVisible} onCancel={() => setIsGenerateModalVisible(false)} footer={null} width={500}>
        <Form form={generateForm} layout="vertical">
          <Form.Item name="length" label="密码长度">
            <Slider min={8} max={32} defaultValue={16} marks={{ 8: '8', 16: '16', 24: '24', 32: '32' }} />
          </Form.Item>
          <Form.Item name="includeUppercase" label="包含大写字母">
            <Checkbox defaultChecked>A-Z</Checkbox>
          </Form.Item>
          <Form.Item name="includeLowercase" label="包含小写字母">
            <Checkbox defaultChecked>a-z</Checkbox>
          </Form.Item>
          <Form.Item name="includeNumbers" label="包含数字">
            <Checkbox defaultChecked>0-9</Checkbox>
          </Form.Item>
          <Form.Item name="includeSpecialChars" label="包含特殊字符">
            <Checkbox>!@#$%^&*</Checkbox>
          </Form.Item>
          <Form.Item name="generatedPassword" label="生成的密码">
            <Input.Group>
              <Input disabled className="font-mono" name="generatedPassword" />
              <Button icon={<CopyOutlined />} onClick={() => {
                const password = generateForm.getFieldValue('generatedPassword');
                if (password) {
                  navigator.clipboard.writeText(password);
                  message.success('已复制');
                }
              }} />
            </Input.Group>
          </Form.Item>
          {generateForm.getFieldValue('generatedPassword') && (
            <div className="mb-4">
              <span className="text-sm text-gray-600 mr-2">密码强度：</span>
              {getStrengthTag(passwordStrength)}
            </div>
          )}
          <Space className="float-right">
            <Button onClick={() => setIsGenerateModalVisible(false)}>取消</Button>
            <Button onClick={handleGenerate} icon={<ReloadOutlined />}>重新生成</Button>
            <Button type="primary" onClick={useGeneratedPassword}>使用此密码</Button>
          </Space>
        </Form>
      </Modal>
    </div>
  );
};

export default PasswordList;
