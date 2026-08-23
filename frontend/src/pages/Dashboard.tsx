import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';

const Dashboard: React.FC = () => {
    const { email, logout } = useAuth();
    const navigate = useNavigate();
    const [testResponse, setTestResponse] = useState<string>('');

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    const testAuthRequest = async () => {
        try {
            // Note: This endpoint does not exist yet on the backend, 
            // but the request will go through the JWT filter.
            // If the filter works, we should get a 404 instead of a 401.
            await api.get('/dummy-protected-route');
            setTestResponse('Success: Reached protected route!');
        } catch (error: any) {
            if (error.response?.status === 404) {
                setTestResponse('Success: Reached protected route! (Got 404, which means JWT passed auth filter)');
            } else if (error.response?.status === 401) {
                setTestResponse('Failed: Unauthorized (JWT not sent or invalid)');
            } else {
                setTestResponse(`Failed: ${error.message}`);
            }
        }
    };

    return (
        <div className="min-h-screen bg-gray-50">
            <nav className="bg-white shadow-sm p-4 flex justify-between items-center">
                <h1 className="text-xl font-bold text-gray-800">Portfolio Pulse</h1>
                <div className="flex items-center space-x-4">
                    <span className="text-gray-600">{email}</span>
                    <button
                        onClick={handleLogout}
                        className="bg-gray-200 hover:bg-gray-300 text-gray-800 font-semibold py-1 px-4 rounded"
                    >
                        Logout
                    </button>
                </div>
            </nav>
            <main className="p-8">
                <h2 className="text-2xl font-bold mb-4">Dashboard</h2>
                <p className="text-gray-600 mb-6">Welcome to your portfolio dashboard!</p>
                
                <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-100 max-w-md">
                    <h3 className="text-lg font-semibold mb-2">Test Authentication</h3>
                    <button
                        onClick={testAuthRequest}
                        className="bg-indigo-500 hover:bg-indigo-600 text-white font-bold py-2 px-4 rounded transition duration-200"
                    >
                        Send Authenticated Request
                    </button>
                    {testResponse && (
                        <p className="mt-4 p-3 bg-gray-100 rounded text-sm text-gray-700">
                            {testResponse}
                        </p>
                    )}
                </div>
            </main>
        </div>
    );
};

export default Dashboard;
