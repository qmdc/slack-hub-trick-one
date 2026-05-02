import { useState, useEffect } from 'react';
import { Button, Form, Input, Select, message, Card, Tag } from 'antd';
import { PlusCircleOutlined } from '@ant-design/icons';
import { createBill, getIncomeCategories, getExpenseCategories, getAccounts, type Category, type Account, type Bill } from '../../../apis/modules/accounting';

const { Option } = Select;

const QuickBill: React.FC = () => {
  const [form] = Form.useForm();
  const [billType, setBillType] = useState<'INCOME' | 'EXPENSE'>('EXPENSE');
  const [categories, setCategories] = useState<Category[]>([]);
  const [accounts, setAccounts] = useState<Account[]>([]);

  useEffect(() => {
    loadCategories();
    loadAccounts();
  }, [billType]);

  const loadCategories = () => {
    if (billType === 'INCOME') {
      getIncomeCategories().then((res) => setCategories(res.data || []));
    } else {
      getExpenseCategories().then((res) => setCategories(res.data || []));
    }
  };

  const loadAccounts = () => {
    getAccounts().then((res) => setAccounts(res.data || []));
  };

  const handleSubmit = () => {
    form.validateFields().then((values) => {
      const billData: Bill = {
        amount: parseFloat(values.amount),
        type: billType,
        categoryId: values.categoryId,
        accountId: values.accountId,
        remark: values.remark,
        billDate: Date.now(),
      };
      createBill(billData).then(() => {
        message.success('记账成功');
        form.resetFields();
      }).catch(() => {
        message.error('记账失败');
      });
    });
  };

  return (
    <Card title="快速记账" className="quick-bill-card">
      <div className="bill-type-tabs">
        <Button
          type={billType === 'EXPENSE' ? 'primary' : 'default'}
          onClick={() => {
            setBillType('EXPENSE');
            form.setFieldsValue({ categoryId: undefined });
          }}
        >
          支出
        </Button>
        <Button
          type={billType === 'INCOME' ? 'primary' : 'default'}
          onClick={() => {
            setBillType('INCOME');
            form.setFieldsValue({ categoryId: undefined });
          }}
        >
          收入
        </Button>
      </div>

      <Form form={form} layout="vertical" className="bill-form">
        <Form.Item
          label="金额"
          name="amount"
          rules={[
            { required: true, message: '请输入金额' },
            { pattern: /^\d+(\.\d{1,2})?$/, message: '请输入有效的金额格式（最多两位小数）' },
            ({ getFieldValue }) => ({
              validator(_, value) {
                const numValue = parseFloat(value);
                if (numValue <= 0) {
                  return Promise.reject(new Error('金额必须大于0'));
                }
                return Promise.resolve();
              },
            }),
          ]}
        >
          <Input type="string" placeholder="请输入金额" className="amount-input" />
        </Form.Item>

        <Form.Item
          label="分类"
          name="categoryId"
          rules={[{ required: true, message: '请选择分类' }]}
        >
          <Select placeholder="请选择分类">
            {categories.map((cat) => (
              <Option key={cat.id} value={cat.id}>
                {cat.name}
              </Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          label="账户"
          name="accountId"
          rules={[{ required: true, message: '请选择账户' }]}
        >
          <Select placeholder="请选择账户">
            {accounts.map((acc) => (
              <Option key={acc.id} value={acc.id}>
                {acc.name} ({acc.balance.toFixed(2)})
              </Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item label="备注" name="remark">
          <Input.TextArea placeholder="添加备注（可选）" rows={3} />
        </Form.Item>

        <Form.Item>
          <Button type="primary" onClick={handleSubmit} block icon={<PlusCircleOutlined />}>
            确认记账
          </Button>
        </Form.Item>
      </Form>
    </Card>
  );
};

export default QuickBill;