import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../services/api';
import { useAuth } from '../context/AuthContext';

export default function Login() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const { login } = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        try {
            const res = await authService.login({ email, password });
            login({ userId: res.data.userId, name: res.data.name, role: res.data.role }, res.data.token);
            navigate('/dashboard');
        } catch (err) {
            setError(err.response?.data?.error || 'Login failed');
        }
    };

    return (
        <div className="container" style={{ maxWidth: 400, marginTop: 60 }}>
            <h1 style={{ textAlign: 'center', marginBottom: 24 }}>Insurance Management Platform</h1>
            <div className="card">
                <h2 style={{ marginBottom: 16 }}>Login</h2>
                {error && <p style={{ color: 'red', marginBottom: 10 }}>{error}</p>}
                <form onSubmit={handleSubmit}>
                    <div style={{ marginBottom: 10 }}>
                        <input type="email" placeholder="Email" value={email}
                            onChange={(e) => setEmail(e.target.value)} required />
                    </div>
                    <div style={{ marginBottom: 14 }}>
                        <input type="password" placeholder="Password" value={password}
                            onChange={(e) => setPassword(e.target.value)} required />
                    </div>
                    <button type="submit">Login</button>
                </form>
                <p style={{ marginTop: 14, fontSize: 14 }}>
                    No account? <a href="/register">Register</a>
                </p>
            </div>
        </div>
    );
}