import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { paymentService, policyService } from '../services/api';

export default function Payments() {
    const { user } = useAuth();
    const [payments, setPayments] = useState([]);
    const [allPolicies, setAllPolicies] = useState([]);
    const [form, setForm] = useState({ policyId: '', amount: '', dueDate: '' });
    const [settleId, setSettleId] = useState(null);
    const [method, setMethod] = useState('UPI');
    const [error, setError] = useState('');
    const [showForm, setShowForm] = useState(false);

    const isStaff = user.role === 'ADMIN' || user.role === 'AGENT';
    const isAdmin = user.role === 'ADMIN';

    const loadPayments = () => {
        const call = isStaff ? paymentService.getAll() : paymentService.getMine();
        call.then(res => setPayments(res.data)).catch(() => {});
    };

    useEffect(() => {
        loadPayments();
        if (isStaff) {
            policyService.getAll().then(res => setAllPolicies(res.data)).catch(() => {});
        }
    }, [isStaff]);

    const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

    const handleCreate = async (e) => {
        e.preventDefault();
        setError('');
        try {
            await paymentService.create(form);
            setShowForm(false);
            setForm({ policyId: '', amount: '', dueDate: '' });
            loadPayments();
        } catch (err) {
            setError(err.response?.data?.error || 'Failed to create payment');
        }
    };

    const handleSettle = async (id) => {
        try {
            await paymentService.settle(id, { paymentMethod: method });
            setSettleId(null);
            loadPayments();
        } catch (err) {
            alert(err.response?.data?.error || 'Failed to settle payment');
        }
    };

    const handleDelete = async (id) => {
        if (!window.confirm('Delete this payment? This cannot be undone.')) return;
        try {
            await paymentService.delete(id);
            loadPayments();
        } catch (err) {
            alert(err.response?.data?.error || 'Failed to delete payment');
        }
    };

    return (
        <div className="container">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
                <h1>Payments</h1>
                {isStaff && <button style={{ width: 'auto', padding: '8px 16px' }} onClick={() => setShowForm(!showForm)}>
                    {showForm ? 'Cancel' : '+ Create Payment'}
                </button>}
            </div>

            {showForm && (
                <div className="card">
                    <h3 style={{ marginBottom: 12 }}>New Payment Due</h3>
                    {error && <p style={{ color: 'red', marginBottom: 10 }}>{error}</p>}
                    <form onSubmit={handleCreate}>
                        <div style={{ marginBottom: 10 }}>
                            <select name="policyId" onChange={handleChange} required defaultValue="">
                                <option value="" disabled>Select Policy</option>
                                {allPolicies.map(p => <option key={p.id} value={p.id}>{p.policyNumber} - {p.customerName}</option>)}
                            </select>
                        </div>
                        <div style={{ marginBottom: 10 }}>
                            <input name="amount" type="number" placeholder="Amount" onChange={handleChange} required />
                        </div>
                        <div style={{ marginBottom: 14 }}>
                            <input name="dueDate" type="date" onChange={handleChange} required />
                        </div>
                        <button type="submit">Create Payment</button>
                    </form>
                </div>
            )}

            <div className="card">
                <table>
                    <thead>
                        <tr>
                            <th>Receipt Number</th><th>Policy</th><th>Amount</th><th>Due Date</th><th>Status</th><th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        {payments.map(p => (
                            <tr key={p.id}>
                                <td data-label="Receipt Number">{p.receiptNumber}</td>
                                <td data-label="Policy">{p.policyNumber}</td>
                                <td data-label="Amount">Rs.{p.amount}</td>
                                <td data-label="Due Date">{p.dueDate}</td>
                                <td data-label="Status"><span className={'badge badge-' + p.status.toLowerCase()}>{p.status}</span></td>
                                <td data-label="Action">
                                    {p.status !== 'PAID' && <button style={{ width: 'auto', padding: '6px 10px', marginRight: 6 }}
                                        onClick={() => setSettleId(p.id)}>Settle</button>}
                                    {isAdmin && <button style={{ width: 'auto', padding: '6px 10px', background: '#dc2626' }}
                                        onClick={() => handleDelete(p.id)}>Delete</button>}
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
                {payments.length === 0 && <p style={{ padding: 20, textAlign: 'center', color: '#888' }}>No payments found.</p>}
            </div>

            {settleId && (
                <div className="card">
                    <h3 style={{ marginBottom: 12 }}>Settle Payment #{settleId}</h3>
                    <div style={{ marginBottom: 14 }}>
                        <select value={method} onChange={(e) => setMethod(e.target.value)}>
                            <option value="UPI">UPI</option>
                            <option value="Cash">Cash</option>
                            <option value="Card">Card</option>
                            <option value="Bank Transfer">Bank Transfer</option>
                        </select>
                    </div>
                    <button style={{ marginRight: 8, width: 'auto', padding: '8px 16px' }}
                        onClick={() => handleSettle(settleId)}>Confirm Payment</button>
                    <button style={{ width: 'auto', padding: '8px 16px', background: '#888' }}
                        onClick={() => setSettleId(null)}>Cancel</button>
                </div>
            )}
        </div>
    );
}