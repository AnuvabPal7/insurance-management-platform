import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { claimService, policyService } from '../services/api';

export default function Claims() {
    const { user } = useAuth();
    const [claims, setClaims] = useState([]);
    const [myPolicies, setMyPolicies] = useState([]);
    const [form, setForm] = useState({ policyId: '', reason: '', description: '', claimAmount: '' });
    const [reviewForm, setReviewForm] = useState({});
    const [error, setError] = useState('');
    const [showForm, setShowForm] = useState(false);
    const [reviewingId, setReviewingId] = useState(null);

    const isStaff = user.role === 'ADMIN' || user.role === 'AGENT';
    const isAdmin = user.role === 'ADMIN';
    const isCustomer = user.role === 'CUSTOMER';

    const loadClaims = () => {
        const call = isStaff ? claimService.getAll() : claimService.getMine();
        call.then(res => setClaims(res.data)).catch(() => {});
    };

    useEffect(() => {
        loadClaims();
        if (isCustomer) {
            policyService.getMine().then(res => setMyPolicies(res.data)).catch(() => {});
        }
    }, [isStaff, isCustomer]);

    const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

    const handleFile = async (e) => {
        e.preventDefault();
        setError('');
        try {
            await claimService.file(form);
            setShowForm(false);
            setForm({ policyId: '', reason: '', description: '', claimAmount: '' });
            loadClaims();
        } catch (err) {
            setError(err.response?.data?.error || 'Failed to file claim');
        }
    };

    const openReview = (claim) => {
        setReviewingId(claim.id);
        setReviewForm({ status: 'APPROVED', approvedAmount: claim.claimAmount, remarks: '' });
    };

    const handleReview = async (id) => {
        try {
            await claimService.review(id, reviewForm);
            setReviewingId(null);
            loadClaims();
        } catch (err) {
            alert(err.response?.data?.error || 'Failed to review claim');
        }
    };

    const handleDelete = async (id) => {
        if (!window.confirm('Delete this claim? This cannot be undone.')) return;
        try {
            await claimService.delete(id);
            loadClaims();
        } catch (err) {
            alert(err.response?.data?.error || 'Failed to delete claim');
        }
    };

    return (
        <div className="container">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
                <h1>Claims</h1>
                {isCustomer && <button style={{ width: 'auto', padding: '8px 16px' }} onClick={() => setShowForm(!showForm)}>
                    {showForm ? 'Cancel' : '+ File Claim'}
                </button>}
            </div>

            {showForm && (
                <div className="card">
                    <h3 style={{ marginBottom: 12 }}>File a Claim</h3>
                    {error && <p style={{ color: 'red', marginBottom: 10 }}>{error}</p>}
                    <form onSubmit={handleFile}>
                        <div style={{ marginBottom: 10 }}>
                            <select name="policyId" onChange={handleChange} required defaultValue="">
                                <option value="" disabled>Select Policy</option>
                                {myPolicies.map(p => <option key={p.id} value={p.id}>{p.policyNumber} - {p.policyName}</option>)}
                            </select>
                        </div>
                        <div style={{ marginBottom: 10 }}>
                            <input name="reason" placeholder="Reason for Claim" onChange={handleChange} required />
                        </div>
                        <div style={{ marginBottom: 10 }}>
                            <input name="description" placeholder="Description (optional)" onChange={handleChange} />
                        </div>
                        <div style={{ marginBottom: 14 }}>
                            <input name="claimAmount" type="number" placeholder="Claim Amount" onChange={handleChange} required />
                        </div>
                        <button type="submit">Submit Claim</button>
                    </form>
                </div>
            )}

            <div className="card">
                <table>
                    <thead>
                        <tr>
                            <th>Claim Number</th><th>Policy</th><th>Reason</th><th>Amount</th><th>Status</th>
                            {isStaff && <th>Action</th>}
                        </tr>
                    </thead>
                    <tbody>
                        {claims.map(c => (
                            <tr key={c.id}>
                                <td data-label="Claim Number">{c.claimNumber}</td>
                                <td data-label="Policy">{c.policyNumber}</td>
                                <td data-label="Reason">{c.reason}</td>
                                <td data-label="Amount">Rs.{c.claimAmount}</td>
                                <td data-label="Status"><span className={'badge badge-' + c.status.toLowerCase()}>{c.status}</span></td>
                                {isStaff && <td data-label="Action">
                                    <button style={{ width: 'auto', padding: '6px 10px', marginRight: 6 }}
                                        onClick={() => openReview(c)}>Review</button>
                                    {isAdmin && <button style={{ width: 'auto', padding: '6px 10px', background: '#dc2626' }}
                                        onClick={() => handleDelete(c.id)}>Delete</button>}
                                </td>}
                            </tr>
                        ))}
                    </tbody>
                </table>
                {claims.length === 0 && <p style={{ padding: 20, textAlign: 'center', color: '#888' }}>No claims found.</p>}
            </div>

            {reviewingId && (
                <div className="card">
                    <h3 style={{ marginBottom: 12 }}>Review Claim #{reviewingId}</h3>
                    <div style={{ marginBottom: 10 }}>
                        <select value={reviewForm.status} onChange={(e) => setReviewForm({ ...reviewForm, status: e.target.value })}>
                            <option value="UNDER_REVIEW">Under Review</option>
                            <option value="APPROVED">Approved</option>
                            <option value="REJECTED">Rejected</option>
                            <option value="SETTLED">Settled</option>
                        </select>
                    </div>
                    <div style={{ marginBottom: 10 }}>
                        <input type="number" placeholder="Approved Amount" value={reviewForm.approvedAmount || ''}
                            onChange={(e) => setReviewForm({ ...reviewForm, approvedAmount: e.target.value })} />
                    </div>
                    <div style={{ marginBottom: 14 }}>
                        <input placeholder="Remarks" value={reviewForm.remarks || ''}
                            onChange={(e) => setReviewForm({ ...reviewForm, remarks: e.target.value })} />
                    </div>
                    <button style={{ marginRight: 8, width: 'auto', padding: '8px 16px' }}
                        onClick={() => handleReview(reviewingId)}>Submit Review</button>
                    <button style={{ width: 'auto', padding: '8px 16px', background: '#888' }}
                        onClick={() => setReviewingId(null)}>Cancel</button>
                </div>
            )}
        </div>
    );
}