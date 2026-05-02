import { useState, useEffect } from 'react';
import { Table, Button, Modal, Form, Input, Select, message, Card, Tag } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, WalletOutlined, CreditCardOutlined, AlipayCircleOutlined, MessageOutlined } from '@ant-design/icons';
import { getAccounts, createAccount, updateAccount, deleteAccount, type Account } from '../../../apis/modules/accounting';

const { Option } = Select;

const ACCOUNT_TYPE_MAP: Record<string, { label: string; icon: React.ReactNode; color: string }> = {
  CASH: { label: '现金', icon: <WalletOutlined />, color: 'gold' },
  BANK_CARD: { label: '银行卡', icon: <CreditCardOutlined />, color: 'blue' },
  ALIPAY: { label: '支付宝', icon: <AlipayCircleOutlined />, color: 'green' },
  WECHAT: { label: '微信', icon: <MessageOutlined />, color: 'cyan' },
};

const AccountList: React.FC = () => {
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingAccount, setEditingAccount] = useState<Account | null>(null);
  const [form] = Form.useForm();

  useEffect(() => {
    loadAccounts();
  }, []);

  const loadAccounts = () => {
    getAccounts().then((res) => setAccounts(res.data || []));
  };

  const showModal = (account?: Account) => {
    if (account) {
      setEditingAccount(account);
      form.setFieldsValue({
        name: account.name,
        type: account.type,
        balance: account.balance,
        bankName: account.bankName,
        cardNumber: account.cardNumber,
        isDefault: account.isDefault,
        remark: account.remark,
      });
    } else {
      setEditingAccount(null);
      form.resetFields();
    }
    setIsModalVisible(true);
  };

  const handleOk = () => {
    form.validateFields().then((values) => {
      const accountData: Account = {
        name: values.name,
        type: values.type,
        balance: values.balance || 0,
        bankName: values.bankName,
        cardNumber: values.cardNumber,
        isDefault: values.isDefault || 0,
        remark: values.remark,
      };

      if (editingAccount) {
        updateAccount(editingAccount.id!, accountData).then(() => {
          message.success('更新成功');
          loadAccounts();
        });
      } else {
        createAccount(accountData).then(() => {
          message.success('创建成功');
          loadAccounts();
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
      content: '确定要删除这个账户吗？',
      onOk: () => {
        deleteAccount(id).then(() => {
          message.success('删除成功');
          loadAccounts();
        });
      },
    });
  };

  const columns = [
    {
      title: '账户名称',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      render: (type: string) => {
        const config = ACCOUNT_TYPE_MAP[type];
        return <Tag color={config?.color}>{config?.icon} {config?.label}</Tag>;
      },
    },
    {
      title: '余额',
      dataIndex: 'balance',
      key: 'balance',
      render: (balance: number) => `¥${balance.toFixed(2)}`,
    },
    {
      title: '银行',
      dataIndex: 'bankName',
      key: 'bankName',
    },
    {
      title: '默认账户',
      dataIndex: 'isDefault',
      key: 'isDefault',
      render: (isDefault: number) => (isDefault === 1 ? <Tag color="green">是</Tag> : '-'),
    },
    {
      title: '操作',
      key: 'action',
      render: (_: unknown, record: Account) => (
        <div className="action-buttons">
          <Button icon={<EditOutlined />} onClick={() => showModal(record)} />
          <Button icon={<DeleteOutlined />} danger onClick={() => handleDelete(record.id!)} />
        </div>
      ),
    },
  ];

  return (
    <Card
      title="账户管理"
      extra={<Button icon={<PlusOutlined />} onClick={() => showModal()}>新建账户</Button>}
    >
      <Table columns={columns} dataSource={accounts} rowKey="id" />

      <Modal
        title={editingAccount ? '编辑账户' : '新建账户'}
        visible={isModalVisible}
        onOk={handleOk}
        onCancel={handleCancel}
      >
        <Form form={form} layout="vertical">
          <Form.Item label="账户名称" name="name" rules={[{ required: true, message: '请输入账户名称' }]}>
            <Input placeholder="请输入账户名称" />
          </Form.Item>

          <Form.Item label="账户类型" name="type" rules={[{ required: true, message: '请选择账户类型' }]}>
            <Select placeholder="请选择账户类型">
              {Object.entries(ACCOUNT_TYPE_MAP).map(([key, value]) => (
                <Option key={key} value={key}>
                  {value.icon} {value.label}
                </Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item label="初始余额" name="balance">
            <Input type="number" placeholder="请输入初始余额" />
          </Form.Item>

          <Form.Item label="银行名称" name="bankName">
            <Input placeholder="请输入银行名称（银行卡类型）" />
          </Form.Item>

          <Form.Item label="卡号" name="cardNumber">
            <Input placeholder="请输入卡号（银行卡类型）" />
          </Form.Item>

          <Form.Item label="设为默认账户" name="isDefault">
            <Select placeholder="是否设为默认账户">
              <Option value={0}>否</Option>
              <Option value={1}>是</Option>
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

export default AccountList;