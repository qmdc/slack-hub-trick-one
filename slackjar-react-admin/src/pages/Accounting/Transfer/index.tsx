import { useState, useEffect } from 'react';
import { Button, Form, Input, Select, message, Card, Table, Tag } from 'antd';
import { SwapOutlined, ReloadOutlined } from '@ant-design/icons';
import { getAccounts, transfer, getTransfers, type Account, type Transfer, type TransferRequest } from '../../../apis/modules/accounting';

const { Option } = Select;

const Transfer: React.FC = () => {
  const [form] = Form.useForm();
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [transfers, setTransfers] = useState<Transfer[]>([]);

  useEffect(() => {
    loadAccounts();
    loadTransfers();
  }, []);

  const loadAccounts = () => {
    getAccounts().then((res) => setAccounts(res.data || []));
  };

  const loadTransfers = () => {
    getTransfers().then((res) => setTransfers(res.data || []));
  };

  const handleSubmit = () => {
    form.validateFields().then((values) => {
      if (values.fromAccountId === values.toAccountId) {
        message.error('转出账户和转入账户不能相同');
        return;
      }

      const transferData: TransferRequest = {
        fromAccountId: values.fromAccountId,
        toAccountId: values.toAccountId,
        amount: values.amount,
        remark: values.remark,
      };

      transfer(transferData).then(() => {
        message.success('转账成功');
        form.resetFields();
        loadAccounts();
        loadTransfers();
      }).catch(() => {
        message.error('转账失败');
      });
    });
  };

  const getAccountName = (accountId: number) => {
    const account = accounts.find((acc) => acc.id === accountId);
    return account?.name || '未知账户';
  };

  const columns = [
    {
      title: '转出账户',
      dataIndex: 'fromAccountId',
      key: 'fromAccountId',
      render: (id: number) => getAccountName(id),
    },
    {
      title: '转入账户',
      dataIndex: 'toAccountId',
      key: 'toAccountId',
      render: (id: number) => getAccountName(id),
    },
    {
      title: '金额',
      dataIndex: 'amount',
      key: 'amount',
      render: (amount: number) => <Tag color="blue">¥{amount.toFixed(2)}</Tag>,
    },
    {
      title: '转账时间',
      dataIndex: 'transferDate',
      key: 'transferDate',
      render: (date: number) => new Date(date).toLocaleString('zh-CN'),
    },
    {
      title: '备注',
      dataIndex: 'remark',
      key: 'remark',
    },
  ];

  return (
    <div className="transfer-page">
      <Card title="账户转账" className="transfer-card">
        <Form form={form} layout="vertical" className="transfer-form">
          <Form.Item
            label="转出账户"
            name="fromAccountId"
            rules={[{ required: true, message: '请选择转出账户' }]}
          >
            <Select placeholder="请选择转出账户">
              {accounts.map((acc) => (
                <Option key={acc.id} value={acc.id}>
                  {acc.name} (¥{acc.balance.toFixed(2)})
                </Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            label="转入账户"
            name="toAccountId"
            rules={[{ required: true, message: '请选择转入账户' }]}
          >
            <Select placeholder="请选择转入账户">
              {accounts.map((acc) => (
                <Option key={acc.id} value={acc.id}>
                  {acc.name} (¥{acc.balance.toFixed(2)})
                </Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            label="转账金额"
            name="amount"
            rules={[{ required: true, message: '请输入转账金额' }, { type: 'number', min: 0.01, message: '金额必须大于0' }]}
          >
            <Input type="number" placeholder="请输入转账金额" />
          </Form.Item>

          <Form.Item label="备注" name="remark">
            <Input.TextArea placeholder="添加备注（可选）" rows={2} />
          </Form.Item>

          <Form.Item>
            <Button type="primary" onClick={handleSubmit} block icon={<SwapOutlined />}>
              确认转账
            </Button>
          </Form.Item>
        </Form>
      </Card>

      <Card
        title="转账记录"
        extra={<Button icon={<ReloadOutlined />} onClick={loadTransfers}>刷新</Button>}
      >
        <Table columns={columns} dataSource={transfers} rowKey="id" />
      </Card>
    </div>
  );
};

export default Transfer;