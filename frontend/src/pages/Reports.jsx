import { useEffect, useState } from 'react';
import { Bar, Doughnut } from 'react-chartjs-2';
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, ArcElement, Tooltip, Legend } from 'chart.js';
import { reportService } from '../services/api';

ChartJS.register(CategoryScale, LinearScale, BarElement, ArcElement, Tooltip, Legend);

export default function Reports() {
    const [summary, setSummary] = useState(null);

    useEffect(() => {
        reportService.getSummary().then(res => setSummary(res.data)).catch(() => {});
    }, []);

    if (!summary) return <div className="container"><p>Loading reports...</p></div>;

    const claimStatusData = {
        labels: Object.keys(summary.claimsByStatus),
        datasets: [{
            label: 'Claims by Status',
            data: Object.values(summary.claimsByStatus),
            backgroundColor: ['#22c55e', '#eab308', '#3b82f6', '#ef4444', '#8b5cf6'],
        }],
    };

    const financeData = {
        labels: ['Premium Collected', 'Claims Approved'],
        datasets: [{
            label: 'Amount (Rs.)',
            data: [summary.totalPremiumCollected, summary.totalClaimAmountApproved],
            backgroundColor: ['#2563eb', '#f97316'],
        }],
    };

    return (
        <div className="container">
            <h1 style={{ marginBottom: 20 }}>Reports Dashboard</h1>

            <div className="grid" style={{ marginBottom: 20 }}>
                <div className="card"><h3>Total Customers</h3><p style={{ fontSize: 28 }}>{summary.totalCustomers}</p></div>
                <div className="card"><h3>Total Policies</h3><p style={{ fontSize: 28 }}>{summary.totalPolicies}</p></div>
                <div className="card"><h3>Active Policies</h3><p style={{ fontSize: 28 }}>{summary.activePolicies}</p></div>
                <div className="card"><h3>Total Claims</h3><p style={{ fontSize: 28 }}>{summary.totalClaims}</p></div>
                <div className="card"><h3>Pending Payments</h3><p style={{ fontSize: 28 }}>{summary.pendingPayments}</p></div>
                <div className="card"><h3>Overdue Payments</h3><p style={{ fontSize: 28 }}>{summary.overduePayments}</p></div>
            </div>

            <div className="grid">
                <div className="card">
                    <h3 style={{ marginBottom: 14 }}>Claims by Status</h3>
                    <Doughnut data={claimStatusData} />
                </div>
                <div className="card">
                    <h3 style={{ marginBottom: 14 }}>Premium vs Claims (Rs.)</h3>
                    <Bar data={financeData} />
                </div>
            </div>
        </div>
    );
}