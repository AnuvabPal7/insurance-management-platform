import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { customerService } from '../services/api';

export default function Customers() {
    const { user } = useAuth();
    const [customers, setCustomers] = useState([]);
    const [myProfile, setMyProfile] = useState(null);
    const [form, setForm] = useState({ fullName: '', phone: '', dateOfBirth: '', address: '', city: '', state: '', pincode: '' });
    const [error, setError] = useState('');

    const isStaff = user.role === 'ADMIN' || user.role === 'AGENT';

    useEffect(() => {
        if (isStaff) {
            customerService.getAll().then(res => setCustomers(res.data)).catch(() => {});
        } else {
            customerService.getMine()
                .then(res => setMyProfile(res.data))
                .catch(() => setMyProfile(null));
        }
    }, [isStaff]);

    const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

    const handleCreate = async (e) => {
        e.preventDefault();
        setError('');
        try {
            const res = await customerService.create(form);
            setMyProfile(res.data);
        } catch (err) {
            setError(err.response?.data?.error || 'Failed to create profile');
        }
    };

    if (isStaff) {
        return (
            <div className="container">
                <h1 style={{ marginBottom: 20 }}>Customers</h1>
                <div className="card">
                    <table>
                        <thead>
                            <tr><th>Name</th><th>Email</th><th>Phone</th><th>City</th><th>Registered</th></tr>
                        </thead>
                        <tbody>
                            {customers.map(c => (
                                <tr key={c.id}>
                                    <td data-label="Name">{c.fullName}</td>
                                    <td data-label="Email">{c.email}</td>
                                    <td data-label="Phone">{c.phone}</td>
                                    <td data-label="City">{c.city}</td>
                                    <td data-label="Registered">{c.registeredOn}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                    {customers.length === 0 && <p style={{ padding: 20, textAlign: 'center', color: '#888' }}>No customers found.</p>}
                </div>
            </div>
        );
    }

    return (
        <div className="container">
            <h1 style={{ marginBottom: 20 }}>My Profile</h1>
            {myProfile ? (
                <div className="card">
                    <p><strong>Name:</strong> {myProfile.fullName}</p>
                    <p><strong>Email:</strong> {myProfile.email}</p>
                    <p><strong>Phone:</strong> {myProfile.phone}</p>
                    <p><strong>DOB:</strong> {myProfile.dateOfBirth}</p>
                    <p><strong>Address:</strong> {myProfile.address}, {myProfile.city}, {myProfile.state} - {myProfile.pincode}</p>
                    <p><strong>Registered On:</strong> {myProfile.registeredOn}</p>
                </div>
            ) : (
                <div className="card">
                    <h3 style={{ marginBottom: 12 }}>Complete Your Profile</h3>
                    {error && <p style={{ color: 'red', marginBottom: 10 }}>{error}</p>}
                    <form onSubmit={handleCreate}>
                        <div style={{ marginBottom: 10 }}>
                            <input name="fullName" placeholder="Full Name" onChange={handleChange} required />
                        </div>
                        <div style={{ marginBottom: 10 }}>
                            <input name="phone" placeholder="Phone (10 digits)" onChange={handleChange} required />
                        </div>
                        <div style={{ marginBottom: 10 }}>
                            <input name="dateOfBirth" type="date" onChange={handleChange} required />
                        </div>
                        <div style={{ marginBottom: 10 }}>
                            <input name="address" placeholder="Address" onChange={handleChange} />
                        </div>
                        <div style={{ marginBottom: 10 }}>
                            <input name="city" placeholder="City" onChange={handleChange} />
                        </div>
                        <div style={{ marginBottom: 10 }}>
                            <input name="state" placeholder="State" onChange={handleChange} />
                        </div>
                        <div style={{ marginBottom: 14 }}>
                            <input name="pincode" placeholder="Pincode" onChange={handleChange} />
                        </div>
                        <button type="submit">Save Profile</button>
                    </form>
                </div>
            )}
        </div>
    );
}