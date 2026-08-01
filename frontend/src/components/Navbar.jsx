import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Navbar() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    const isAuthPage = location.pathname === '/login' || location.pathname === '/register';
    if (!user || isAuthPage) return null;

    const isStaff = user.role === 'ADMIN' || user.role === 'AGENT';

    return (
        <nav className="navbar">
            <div>
                <Link to="/dashboard">Dashboard</Link>
                <Link to="/customers">{isStaff ? 'Customers' : 'My Profile'}</Link>
                <Link to="/policies">Policies</Link>
                <Link to="/claims">Claims</Link>
                <Link to="/payments">Payments</Link>
                <Link to="/documents">Documents</Link>
                {isStaff && <Link to="/reports">Reports</Link>}
            </div>
            <div>
                <span style={{ marginRight: 12 }}>{user.name} ({user.role})</span>
                <button onClick={handleLogout} style={{ width: 'auto', padding: '6px 14px' }}>Logout</button>
            </div>
        </nav>
    );
}