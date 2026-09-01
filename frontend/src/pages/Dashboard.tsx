import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';
import { PlusCircle, AlertCircle } from 'lucide-react';
import HoldingsTable from '../components/HoldingsTable';
import type { Holding } from '../components/HoldingsTable';
import HoldingModal from '../components/HoldingModal';
import DeleteConfirmationModal from '../components/DeleteConfirmationModal';

const Dashboard: React.FC = () => {
    const { email, logout } = useAuth();
    const navigate = useNavigate();

    const [holdings, setHoldings] = useState<Holding[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState('');

    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingHolding, setEditingHolding] = useState<Holding | null>(null);
    
    const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
    const [deletingHolding, setDeletingHolding] = useState<Holding | null>(null);

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    const fetchHoldings = async () => {
        try {
            setIsLoading(true);
            setError('');
            const response = await api.get('/holdings');
            setHoldings(response.data);
        } catch (err: any) {
            setError(err.response?.data?.message || 'Failed to fetch holdings');
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        fetchHoldings();
    }, []);

    const handleSaveHolding = async (holdingData: Partial<Holding>) => {
        if (holdingData.id) {
            await api.put(`/holdings/${holdingData.id}`, holdingData);
        } else {
            await api.post('/holdings', holdingData);
        }
        await fetchHoldings();
    };

    const handleDeleteHolding = async () => {
        if (!deletingHolding) return;
        await api.delete(`/holdings/${deletingHolding.id}`);
        await fetchHoldings();
    };

    const openAddModal = () => {
        setEditingHolding(null);
        setIsModalOpen(true);
    };

    const openEditModal = (holding: Holding) => {
        setEditingHolding(holding);
        setIsModalOpen(true);
    };

    const openDeleteModal = (holding: Holding) => {
        setDeletingHolding(holding);
        setIsDeleteModalOpen(true);
    };

    return (
        <div className="min-h-screen bg-gray-50">
            <nav className="bg-white shadow-sm p-4 flex justify-between items-center sticky top-0 z-10">
                <h1 className="text-xl font-bold text-gray-800">Portfolio Pulse</h1>
                <div className="flex items-center space-x-4">
                    <span className="text-gray-600 hidden sm:inline">{email}</span>
                    <button
                        onClick={handleLogout}
                        className="bg-gray-200 hover:bg-gray-300 text-gray-800 font-semibold py-1 px-4 rounded transition-colors"
                    >
                        Logout
                    </button>
                </div>
            </nav>
            <main className="p-4 sm:p-8 max-w-7xl mx-auto">
                <div className="flex justify-between items-center mb-6">
                    <h2 className="text-2xl font-bold text-gray-800">Your Holdings</h2>
                    <button
                        onClick={openAddModal}
                        className="bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-2 px-4 rounded flex items-center transition-colors"
                    >
                        <PlusCircle className="w-5 h-5 mr-2" />
                        Add Holding
                    </button>
                </div>

                {error && (
                    <div className="mb-6 p-4 bg-red-50 border-l-4 border-red-500 rounded flex items-start">
                        <AlertCircle className="w-5 h-5 text-red-500 mr-3 mt-0.5" />
                        <p className="text-red-700">{error}</p>
                    </div>
                )}

                {isLoading ? (
                    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6 animate-pulse">
                        <div className="h-8 bg-gray-200 rounded w-full mb-4"></div>
                        <div className="h-8 bg-gray-200 rounded w-full mb-4"></div>
                        <div className="h-8 bg-gray-200 rounded w-full"></div>
                    </div>
                ) : holdings.length === 0 ? (
                    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-12 text-center">
                        <div className="inline-flex items-center justify-center w-16 h-16 rounded-full bg-gray-100 mb-4">
                            <PlusCircle className="w-8 h-8 text-gray-400" />
                        </div>
                        <h3 className="text-lg font-medium text-gray-900 mb-1">No holdings yet</h3>
                        <p className="text-gray-500 mb-4">Get started by adding your first investment.</p>
                        <button
                            onClick={openAddModal}
                            className="text-indigo-600 hover:text-indigo-800 font-medium"
                        >
                            + Add new holding
                        </button>
                    </div>
                ) : (
                    <HoldingsTable 
                        holdings={holdings} 
                        onEdit={openEditModal} 
                        onDelete={openDeleteModal} 
                    />
                )}
            </main>

            <HoldingModal 
                isOpen={isModalOpen}
                onClose={() => setIsModalOpen(false)}
                onSubmit={handleSaveHolding}
                initialData={editingHolding}
            />

            <DeleteConfirmationModal
                isOpen={isDeleteModalOpen}
                onClose={() => setIsDeleteModalOpen(false)}
                onConfirm={handleDeleteHolding}
                holding={deletingHolding}
            />
        </div>
    );
};

export default Dashboard;
