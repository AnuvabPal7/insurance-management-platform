import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import Policies from './pages/Policies';
import Claims from './pages/Claims';
import Payments from './pages/Payments';
import Customers from './pages/Customers';
import Documents from './pages/Documents';
import Reports from './pages/Reports';
import Navbar from './components/Navbar';

function PrivateRoute({ children }) {
    const { user } = useAuth();
    return user ? children : <Navigate to="/login" />;
}

function PublicOnlyRoute({ children }) {
    const { user } = useAuth();
    return user ? <Navigate to="/dashboard" /> : children;
}

function AppRoutes() {
    return (
        <>
            <Navbar />
            <Routes>
                <Route path="/login" element={<PublicOnlyRoute><Login /></PublicOnlyRoute>} />
                <Route path="/register" element={<PublicOnlyRoute><Register /></PublicOnlyRoute>} />
                <Route path="/dashboard" element={<PrivateRoute><Dashboard /></PrivateRoute>} />
                <Route path="/policies" element={<PrivateRoute><Policies /></PrivateRoute>} />
                <Route path="/claims" element={<PrivateRoute><Claims /></PrivateRoute>} />
                <Route path="/payments" element={<PrivateRoute><Payments /></PrivateRoute>} />
                <Route path="/customers" element={<PrivateRoute><Customers /></PrivateRoute>} />
                <Route path="/documents" element={<PrivateRoute><Documents /></PrivateRoute>} />
                <Route path="/reports" element={<PrivateRoute><Reports /></PrivateRoute>} />
                <Route path="/" element={<Navigate to="/login" />} />
            </Routes>
        </>
    );
}

export default function App() {
    return (
        <BrowserRouter>
            <AuthProvider>
                <AppRoutes />
            </AuthProvider>
        </BrowserRouter>
    );
}