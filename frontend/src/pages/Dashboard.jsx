import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { reportService, policyService, claimService, paymentService } from '../services/api';

export default function Dashboard() {
    const { user } = useAuth();
    const [summary, setSummary] = useState(null);
    const [myCounts, setMyCounts] = useState(null);

    useEffect(() => {
        if (user.role === 'ADMIN' || user.role === 'AGENT') {
            reportService.getSummary().then(res => setSummary(res.data)).catch(() => {});
        } else {
            Promise.all([policyService.getMine(), claimService.getMine(), paymentService.getMine()])
                .then(([p, c, pay]) => setMyCounts({
                    policies: p.data.length,
                    claims: c.data.length,
                    payments: pay.data.length,
                }))
                .catch(() => {});
        }
    }, [user.role]);

    return (
        <div className="container">
            <h1 style={{ marginBottom: 20 }}>Welcome, {user.name}</h1>

            {summary && (
                <div className="grid">
                    <div className="card"><h3>Total Customers</h3><p style={{ fontSize: 28 }}>{summary.totalCustomers}</p></div>
                    <div className="card"><h3>Active Policies</h3><p style={{ fontSize: 28 }}>{summary.activePolicies}</p></div>
                    <div className="card"><h3>Total Claims</h3><p style={{ fontSize: 28 }}>{summary.totalClaims}</p></div>
                    <div className="card"><h3>Premium Collected</h3><p style={{ fontSize: 28 }}>₹{summary.totalPremiumCollected}</p></div>
                    <div className="card"><h3>Claims Approved (₹)</h3><p style={{ fontSize: 28 }}>₹{summary.totalClaimAmountApproved}</p></div>
                    <div className="card"><h3>Overdue Payments</h3><p style={{ fontSize: 28 }}>{summary.overduePayments}</p></div>
                </div>
            )}

            {myCounts && (
                <div className="grid">
                    <div className="card"><h3>My Policies</h3><p style={{ fontSize: 28 }}>{myCounts.policies}</p></div>
                    <div className="card"><h3>My Claims</h3><p style={{ fontSize: 28 }}>{myCounts.claims}</p></div>
                    <div className="card"><h3>My Payments</h3><p style={{ fontSize: 28 }}>{myCounts.payments}</p></div>
                </div>
            )}
        </div>
    );
}