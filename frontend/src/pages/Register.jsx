import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../services/api';
import { useAuth } from '../context/AuthContext';

export default function Register() {
    const [form, setForm] = useState({ username: '', email: '', password: '', role: 'CUSTOMER' });
    const [error, setError] = useState('');
    const { login } = useAuth();
    const navigate = useNavigate();

    const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        try {
            const payload = { ...form, name: form.username };
            const res = await authService.register(payload);
            login({ userId: res.data.userId, name: res.data.name, role: res.data.role }, res.data.token);
            navigate('/dashboard');
        } catch (err) {
            setError(err.response?.data?.error || err.response?.data?.username || 'Registration failed');
        }
    };

    return (
        <div className="container" style={{ maxWidth: 400, marginTop: 60 }}>
            <h1 style={{ textAlign: 'center', marginBottom: 24 }}>Insurance Management Platform</h1>
            <div className="card">
                <h2 style={{ marginBottom: 16 }}>Register</h2>
                {error && <p style={{ color: 'red', marginBottom: 10 }}>{error}</p>}
                <form onSubmit={handleSubmit}>
                    <div style={{ marginBottom: 10 }}>
                        <input name="username" placeholder="Username" onChange={handleChange} required />
                    </div>
                    <div style={{ marginBottom: 10 }}>
                        <input name="email" type="email" placeholder="Email" onChange={handleChange} required />
                    </div>
                    <div style={{ marginBottom: 10 }}>
                        <input name="password" type="password" placeholder="Password" onChange={handleChange} required />
                    </div>
                    <div style={{ marginBottom: 14 }}>
                        <select name="role" onChange={handleChange} value={form.role}>
                            <option value="CUSTOMER">Customer</option>
                            <option value="AGENT">Agent</option>
                            <option value="ADMIN">Admin</option>
                        </select>
                    </div>
                    <button type="submit">Register</button>
                </form>
                <p style={{ marginTop: 14, fontSize: 14 }}>
                    Already have an account? <a href="/login">Login</a>
                </p>
            </div>
        </div>
    );
}