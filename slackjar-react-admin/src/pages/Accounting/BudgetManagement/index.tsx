import { useState, useEffect } from 'react';
import { Table, Button, Modal, Form, Input, Select, message, Card, Tag, Progress } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, WarningOutlined } from '@ant-design/icons';
import { getBudgets, createBudget, updateBudget, deleteBudget, getExpenseCategories, type Budget, type Category } from '../../../apis/modules/accounting';

const { Option } = Select;

const BudgetManagement: React.FC = () => {
  const [budgets, setBudgets] = useState<Budget[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingBudget, setEditingBudget] = useState<Budget | null>(null);
  const [form] = Form.useForm();
  const [year, setYear] = useState(new Date().getFullYear());
  const [month, setMonth] = useState(new Date().getMonth() + 1);

  useEffect(() => {
    loadBudgets();
    loadCategories();
  }, [year, month]);

  const loadBudgets = () => {
    getBudgets('MONTHLY', year, month).then((res) => setBudgets(res.data || []));
  };

  const loadCategories = () => {
    getExpenseCategories().then((res) => setCategories(res.data || []));
  };

  const showModal = (budget?: Budget) => {
    if (budget) {
      setEditingBudget(budget);
      form.setFieldsValue({
        categoryId: budget.categoryId,
        amount: budget.amount,
        periodType: budget.periodType,
        year: budget.year,
        month: budget.month,
        isActive: budget.isActive,
        remark: budget.remark,
      });
    } else {
      setEditingBudget(null);
      form.setFieldsValue({
        periodType: 'MONTHLY',
        year,
        month,
        isActive: 1,
      });
    }
    setIsModalVisible(true);
  };

  const handleOk = () => {
    form.validateFields().then((values) => {
      const budgetData: Budget = {
        categoryId: values.categoryId || undefined,
        amount: values.amount,
        periodType: values.periodType,
        year: values.year,
        month: values.month,
        isActive: values.isActive || 1,
        remark: values.remark,
      };

      if (editingBudget) {
        updateBudget(editingBudget.id!, budgetData).then(() => {
          message.success('更新成功');
          loadBudgets();
        });
      } else {
        createBudget(budgetData).then(() => {
          message.success('创建成功');
          loadBudgets();
        });
      }
      setIsModalVisible(false);
    });
  };

  const handleCancel = () => {
    setIsModalVisible(false);
  };

  const handleDelete = (id: number) => {
    Modal.confirm({
      title: '确认删除',
      content: '确定要删除这个预算吗？',
      onOk: () => {
        deleteBudget(id).then(() => {
          message.success('删除成功');
          loadBudgets();
        });
      },
    });
  };

  const getCategoryName = (categoryId?: number) => {
    if (!categoryId) return '总预算';
    const cat = categories.find((c) => c.id === categoryId);
    return cat?.name || '未知分类';
  };

  const getProgress = (budget: Budget) => {
    if (!budget.amount || budget.amount <= 0) return 0;
    return Math.min(100, ((budget.spentAmount || 0) / budget.amount) * 100);
  };

  const isExceeded = (budget: Budget) => {
    return (budget.spentAmount || 0) > budget.amount;
  };

  const columns = [
    {
      title: '分类',
      dataIndex: 'categoryId',
      key: 'categoryId',
      render: (categoryId: number | undefined) => getCategoryName(categoryId),
    },
    {
      title: '预算金额',
      dataIndex: 'amount',
      key: 'amount',
      render: (amount: number) => `¥${amount.toFixed(2)}`,
    },
    {
      title: '已消费',
      dataIndex: 'spentAmount',
      key: 'spentAmount',
      render: (spentAmount: number, record: Budget) => (
        <div className="spent-amount">
          <span>{`¥${spentAmount?.toFixed(2) || '0.00'}`}</span>
          {isExceeded(record) && <Tag color="red" icon={<WarningOutlined />}>超支</Tag>}
        </div>
      ),
    },
    {
      title: '剩余',
      key: 'remaining',
      render: (_: unknown, record: Budget) => {
        const remaining = record.amount - (record.spentAmount || 0);
        return <span style={{ color: remaining < 0 ? '#ff4d4f' : '#52c41a' }}>¥{remaining.toFixed(2)}</span>;
      },
    },
    {
      title: '进度',
      key: 'progress',
      render: (_: unknown, record: Budget) => (
        <Progress
          percent={getProgress(record)}
          status={isExceeded(record) ? 'exception' : 'active'}
          showInfo={false}
        />
      ),
    },
    {
      title: '状态',
      dataIndex: 'isActive',
      key: 'isActive',
      render: (isActive: number) => (isActive === 1 ? <Tag color="green">启用</Tag> : <Tag color="gray">禁用</Tag>),
    },
    {
      title: '操作',
      key: 'action',
      render: (_: unknown, record: Budget) => (
        <div className="action-buttons">
          <Button icon={<EditOutlined />} onClick={() => showModal(record)} />
          <Button icon={<DeleteOutlined />} danger onClick={() => handleDelete(record.id!)} />
        </div>
      ),
    },
  ];

  return (
    <Card
      title="预算管理"
      extra={
        <>
          <Select value={year} onChange={setYear} style={{ width: 100, marginRight: 8 }}>
            {Array.from({ length: 5 }, (_, i) => {
              const val = new Date().getFullYear() - i;
              return <Option key={val} value={val}>{val}年</Option>;
            })}
          </Select>
          <Select value={month} onChange={setMonth} style={{ width: 80, marginRight: 8 }}>
            {Array.from({ length: 12 }, (_, i) => (
              <Option key={i + 1} value={i + 1}>{i + 1}月</Option>
            ))}
          </Select>
          <Button icon={<PlusOutlined />} onClick={() => showModal()}>新建预算</Button>
        </>
      }
    >
      <Table columns={columns} dataSource={budgets} rowKey="id" />

      <Modal
        title={editingBudget ? '编辑预算' : '新建预算'}
        visible={isModalVisible}
        onOk={handleOk}
        onCancel={handleCancel}
      >
        <Form form={form} layout="vertical">
          <Form.Item label="分类" name="categoryId">
            <Select placeholder="不选则为总预算">
              <Option value={undefined}>总预算</Option>
              {categories.map((cat) => (
                <Option key={cat.id} value={cat.id}>{cat.name}</Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item label="预算金额" name="amount" rules={[{ required: true, message: '请输入预算金额' }, { type: 'number', min: 0.01, message: '金额必须大于0' }]}>
            <Input type="number" placeholder="请输入预算金额" />
          </Form.Item>

          <Form.Item label="周期类型" name="periodType" rules={[{ required: true }]}>
            <Select>
              <Option value="MONTHLY">月度</Option>
              <Option value="YEARLY">年度</Option>
            </Select>
          </Form.Item>

          <Form.Item label="年份" name="year" rules={[{ required: true }]}>
            <Input type="number" />
          </Form.Item>

          <Form.Item label="月份" name="month">
            <Input type="number" placeholder="月度预算必填" />
          </Form.Item>

          <Form.Item label="状态" name="isActive">
            <Select>
              <Option value={1}>启用</Option>
              <Option value={0}>禁用</Option>
            </Select>
          </Form.Item>

          <Form.Item label="备注" name="remark">
            <Input.TextArea placeholder="添加备注（可选）" />
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  );
};

export default BudgetManagement;