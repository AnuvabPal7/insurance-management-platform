import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { policyService, customerService } from '../services/api';

export default function Policies() {
    const { user } = useAuth();
    const [policies, setPolicies] = useState([]);
    const [customers, setCustomers] = useState([]);
    const [form, setForm] = useState({ customerId: '', policyType: 'HEALTH', policyName: '', coverageAmount: '', premiumAmount: '', startDate: '', endDate: '' });
    const [error, setError] = useState('');
    const [showForm, setShowForm] = useState(false);

    const isStaff = user.role === 'ADMIN' || user.role === 'AGENT';
    const isAdmin = user.role === 'ADMIN';

    const loadPolicies = () => {
        const call = isStaff ? policyService.getAll() : policyService.getMine();
        call.then(res => setPolicies(res.data)).catch(() => {});
    };

    useEffect(() => {
        loadPolicies();
        if (isStaff) {
            customerService.getAll().then(res => setCustomers(res.data)).catch(() => {});
        }
    }, [isStaff]);

    const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

    const handleCreate = async (e) => {
        e.preventDefault();
        setError('');
        try {
            await policyService.create(form);
            setShowForm(false);
            setForm({ customerId: '', policyType: 'HEALTH', policyName: '', coverageAmount: '', premiumAmount: '', startDate: '', endDate: '' });
            loadPolicies();
        } catch (err) {
            setError(err.response?.data?.error || 'Failed to create policy');
        }
    };

    const handleDelete = async (id) => {
        if (!window.confirm('Delete this policy? This cannot be undone.')) return;
        try {
            await policyService.delete(id);
            loadPolicies();
        } catch (err) {
            alert(err.response?.data?.error || 'Failed to delete policy');
        }
    };

    return (
        <div className="container">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
                <h1>Policies</h1>
                {isStaff && <button style={{ width: 'auto', padding: '8px 16px' }} onClick={() => setShowForm(!showForm)}>
                    {showForm ? 'Cancel' : '+ Create Policy'}
                </button>}
            </div>

            {showForm && (
                <div className="card">
                    <h3 style={{ marginBottom: 12 }}>New Policy</h3>
                    {error && <p style={{ color: 'red', marginBottom: 10 }}>{error}</p>}
                    <form onSubmit={handleCreate}>
                        <div style={{ marginBottom: 10 }}>
                            <select name="customerId" onChange={handleChange} required defaultValue="">
                                <option value="" disabled>Select Customer</option>
                                {customers.map(c => <option key={c.id} value={c.id}>{c.fullName} ({c.email})</option>)}
                            </select>
                        </div>
                        <div style={{ marginBottom: 10 }}>
                            <select name="policyType" onChange={handleChange} value={form.policyType}>
                                <option value="LIFE">Life</option>
                                <option value="HEALTH">Health</option>
                                <option value="VEHICLE">Vehicle</option>
                                <option value="HOME">Home</option>
                                <option value="TRAVEL">Travel</option>
                            </select>
                        </div>
                        <div style={{ marginBottom: 10 }}>
                            <input name="policyName" placeholder="Policy Name" onChange={handleChange} required />
                        </div>
                        <div style={{ marginBottom: 10 }}>
                            <input name="coverageAmount" type="number" placeholder="Coverage Amount" onChange={handleChange} required />
                        </div>
                        <div style={{ marginBottom: 10 }}>
                            <input name="premiumAmount" type="number" placeholder="Premium Amount" onChange={handleChange} required />
                        </div>
                        <div style={{ marginBottom: 10 }}>
                            <input name="startDate" type="date" onChange={handleChange} required />
                        </div>
                        <div style={{ marginBottom: 14 }}>
                            <input name="endDate" type="date" onChange={handleChange} required />
                        </div>
                        <button type="submit">Create Policy</button>
                    </form>
                </div>
            )}

            <div className="card">
                <table>
                    <thead>
                        <tr>
                            <th>Policy Number</th><th>Customer</th><th>Type</th><th>Coverage</th><th>Premium</th><th>Status</th>
                            {isAdmin && <th>Action</th>}
                        </tr>
                    </thead>
                    <tbody>
                        {policies.map(p => (
                            <tr key={p.id}>
                                <td data-label="Policy Number">{p.policyNumber}</td>
                                <td data-label="Customer">{p.customerName}</td>
                                <td data-label="Type">{p.policyType}</td>
                                <td data-label="Coverage">Rs.{p.coverageAmount}</td>
                                <td data-label="Premium">Rs.{p.premiumAmount}</td>
                                <td data-label="Status"><span className={'badge badge-' + p.status.toLowerCase()}>{p.status}</span></td>
                                {isAdmin && <td data-label="Action">
                                    <button style={{ width: 'auto', padding: '6px 12px', background: '#dc2626' }}
                                        onClick={() => handleDelete(p.id)}>Delete</button>
                                </td>}
                            </tr>
                        ))}
                    </tbody>
                </table>
                {policies.length === 0 && <p style={{ padding: 20, textAlign: 'center', color: '#888' }}>No policies found.</p>}
            </div>
        </div>
    );
}