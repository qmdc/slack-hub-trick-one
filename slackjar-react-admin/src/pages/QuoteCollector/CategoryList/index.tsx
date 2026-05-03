import {useState, useEffect} from 'react';
import {
    Table,
    Button,
    Modal,
    Form,
    Input,
    message,
    Space,
    Popconfirm,
    Tag,
    Card,
    Row,
    Col,
    Statistic,
    Empty,
    InputNumber
} from 'antd';
import {PlusOutlined, EditOutlined, DeleteOutlined, SearchOutlined, TagOutlined} from '@ant-design/icons';
import {quoteCategoryApi, type QuoteCategory} from '../../../apis/modules/quotecollector';

const CategoryList: React.FC = () => {
    const [categories, setCategories] = useState<QuoteCategory[]>([]);
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [editingItem, setEditingItem] = useState<QuoteCategory | null>(null);
    const [searchKeyword, setSearchKeyword] = useState('');
    const [form] = Form.useForm();

    const fetchCategories = async () => {
        try {
            const response = await quoteCategoryApi.list();
            let data = response.data || [];
            if (searchKeyword.trim()) {
                data = data.filter(cat => cat.name.includes(searchKeyword) || cat.description?.includes(searchKeyword));
            }
            setCategories(data);
        } catch {
            message.error('获取分类列表失败');
        }
    };

    useEffect(() => {
        fetchCategories();
    }, [searchKeyword]);

    const showAddModal = () => {
        setEditingItem(null);
        form.resetFields();
        setIsModalVisible(true);
    };

    const showEditModal = (record: QuoteCategory) => {
        setEditingItem(record);
        form.setFieldsValue({
            name: record.name,
            icon: record.icon,
            sortOrder: record.sortOrder,
            description: record.description,
        });
        setIsModalVisible(true);
    };

    const handleDelete = async (id: number) => {
        try {
            await quoteCategoryApi.delete(id);
            message.success('删除成功');
            fetchCategories();
        } catch {
            message.error('删除失败');
        }
    };

    const handleModalOk = async () => {
        try {
            const values = await form.validateFields();
            const data: Omit<QuoteCategory, 'id'> = {
                name: values.name,
                icon: values.icon || '📝',
                sortOrder: values.sortOrder || 0,
                description: values.description,
            };

            if (editingItem) {
                await quoteCategoryApi.update(editingItem.id, data);
                message.success('更新成功');
            } else {
                await quoteCategoryApi.create(data);
                message.success('添加成功');
            }
            setIsModalVisible(false);
            fetchCategories();
        } catch {
            message.error('操作失败');
        }
    };

    const handleModalCancel = () => {
        setIsModalVisible(false);
        setEditingItem(null);
        form.resetFields();
    };

    const columns = [
        {
            title: '图标',
            dataIndex: 'icon',
            key: 'icon',
            width: '8%',
            render: (text: string) => <span className="text-2xl">{text}</span>,
        },
        {title: '分类名称', dataIndex: 'name', key: 'name'},
        {title: '排序', dataIndex: 'sortOrder', key: 'sortOrder'},
        {
            title: '描述',
            dataIndex: 'description',
            key: 'description',
            ellipsis: true,
        },
        {
            title: '操作',
            key: 'actions',
            render: (_: unknown, record: QuoteCategory) => (
                <Space>
                    <Button icon={<EditOutlined/>} size="small" onClick={() => showEditModal(record)}/>
                    <Popconfirm title="确定删除这个分类吗？" onConfirm={() => handleDelete(record.id)} okText="确定"
                                cancelText="取消">
                        <Button icon={<DeleteOutlined/>} size="small" danger/>
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
                        <Statistic title="分类总数" value={categories.length} prefix={<TagOutlined/>}/>
                    </Col>
                </Row>
            </Card>

            <Card className="mb-6">
                <Space direction="vertical" className="w-full">
                    <Row align="middle" gutter={16}>
                        <Col span={8}>
                            <Input.Search
                                placeholder="搜索分类名称"
                                prefix={<SearchOutlined/>}
                                value={searchKeyword}
                                onChange={(e) => setSearchKeyword(e.target.value)}
                                className="w-full"
                            />
                        </Col>
                        <Col span={16}>
                            <Space>
                                <Button type="primary" icon={<PlusOutlined/>} onClick={showAddModal}>
                                    添加分类
                                </Button>
                            </Space>
                        </Col>
                    </Row>
                </Space>
            </Card>

            <Card title="分类列表">
                <Table
                    columns={columns}
                    dataSource={categories}
                    rowKey="id"
                    pagination={false}
                    bordered
                    locale={{emptyText: <Empty description="暂无分类"/>}}
                />
            </Card>

            <Modal
                title={editingItem ? '编辑分类' : '添加分类'}
                open={isModalVisible}
                onOk={handleModalOk}
                onCancel={handleModalCancel}
                width={500}
            >
                <Form form={form} layout="vertical">
                    <Form.Item name="name" label="分类名称" rules={[{required: true, message: '请输入分类名称'}]}>
                        <Input placeholder="例如：励志"/>
                    </Form.Item>
                    <Form.Item name="icon" label="分类图标">
                        <Input placeholder="输入emoji图标，例如：🏆"/>
                    </Form.Item>
                    <Form.Item name="sortOrder" label="排序序号">
                        <InputNumber defaultValue={0} min={0}/>
                    </Form.Item>
                    <Form.Item name="description" label="分类描述">
                        <Input.TextArea placeholder="添加分类描述（可选）" rows={3}/>
                    </Form.Item>
                </Form>
            </Modal>
        </div>
    );
};

export default CategoryList;