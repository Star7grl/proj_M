import React, { useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Cookies from 'js-cookie';
import RegisterPage from './pages/RegisterPage';
import LoginPage from './pages/LoginPage';
import HomePage from './pages/HomePage';
import ServicesPage from './pages/ServicesPage';
import RoomsPage from './pages/RoomsPage';
import ProfilePage from './pages/ProfilePage';
import AdminPanelPage from './pages/AdminPanelPage';
import ForgotPasswordPage from './pages/ForgotPasswordPage';
import ResetPasswordPage from './pages/ResetPasswordPage';
import ServicesManagementPage from './pages/ServicesManagementPage';
import RoomsManagementPage from './pages/RoomsManagementPage';
import RoomSelectionPage from './pages/RoomSelectionPage';
import RentalFormPage from './pages/RentalFormPage';
import RoomDetailPage from './pages/RoomDetailPage';
import UsersManagementPage from './pages/UsersManagementPage';
import BookingsManagementPage from './pages/BookingsManagementPage';
import PaymentPage from './pages/PaymentPage';
import ContactsPage from './pages/ContactsPage';
import SupportMessagesManagementPage from './pages/SupportMessagesManagementPage';
import ServicesPurchasePage from './pages/ServicesPurchasePage'; // Новый импорт
import Header from './components/Header';
import Footer from './components/Footer';
import ProtectedRoute from './components/ProtectedRoute';
import RentalsListPage from './pages/RentalsListPage.jsx';
import useUserStore from './store/UserStore';
import apiClient from './config/apiClient.js';

const App = () => {
    const { setUser } = useUserStore();

    useEffect(() => {
        const loadUser = async () => {
            try {
                const storedUser = Cookies.get('user');
                if (storedUser) {
                    setUser(JSON.parse(storedUser));
                } else {
                    const response = await apiClient.get('/api/auth/me');
                    if (response.status === 200) {
                        setUser(response.data);
                        Cookies.set('user', JSON.stringify(response.data), { expires: 7 });
                    }
                }
            } catch (error) {
                console.error('Failed to load user', error);
            }
        };
        loadUser();
    }, [setUser]);

    return (
        <Router>
            <Header />
            <Routes>
                <Route path="/" element={<HomePage />} />
                <Route path="/register" element={<RegisterPage />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/services" element={<ServicesPage />} />
                <Route path="/rooms" element={<RoomsPage />} />
                <Route path="/rooms/:id" element={<RoomDetailPage />} />
                <Route path="/profile" element={<ProtectedRoute><ProfilePage /></ProtectedRoute>} />
                <Route path="/contacts" element={<ContactsPage />} />
                <Route path="/hostes/rentals" element={<ProtectedRoute role="HOSTES"><RentalsListPage /></ProtectedRoute>} />
                <Route path="/admin/users" element={<ProtectedRoute role="ADMIN"><UsersManagementPage /></ProtectedRoute>} />
                <Route path="/admin/bookings" element={<ProtectedRoute role="ADMIN"><BookingsManagementPage /></ProtectedRoute>} />
                <Route path="/admin" element={<ProtectedRoute role="ADMIN"><AdminPanelPage /></ProtectedRoute>} />
                <Route path="/admin/services" element={<ProtectedRoute role="ADMIN"><ServicesManagementPage /></ProtectedRoute>} />
                <Route path="/admin/rooms" element={<ProtectedRoute role="ADMIN"><RoomsManagementPage /></ProtectedRoute>} />
                <Route path="/admin/support-messages" element={<ProtectedRoute role="ADMIN"><SupportMessagesManagementPage /></ProtectedRoute>} />
                <Route path="/forgot-password" element={<ForgotPasswordPage />} />
                <Route path="/reset-password" element={<ResetPasswordPage />} />
                <Route path="/payment" element={<ProtectedRoute><PaymentPage /></ProtectedRoute>} />
                <Route path="/hostes/rooms" element={<ProtectedRoute role="HOSTES"><RoomSelectionPage /></ProtectedRoute>} />
                <Route path="/hostes/rentals/new" element={<ProtectedRoute role="HOSTES"><RentalFormPage /></ProtectedRoute>} />
                <Route path="/services/purchase" element={<ProtectedRoute><ServicesPurchasePage /></ProtectedRoute>} /> {/* Новый маршрут */}
                <Route path="*" element={<Navigate to="/" />} />
            </Routes>
            <Footer />
        </Router>
    );
};

export default App;