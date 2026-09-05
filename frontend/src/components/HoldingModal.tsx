import React, { useState, useEffect } from 'react';
import type { HoldingSummary } from '../api/portfolioApi';

interface HoldingModalProps {
    isOpen: boolean;
    onClose: () => void;
    onSubmit: (holdingData: Partial<HoldingSummary>) => void;
    initialData?: HoldingSummary | null;
}

const HoldingModal: React.FC<HoldingModalProps> = ({ isOpen, onClose, onSubmit, initialData }) => {
    const [tickerSymbol, setTickerSymbol] = useState('');
    const [assetType, setAssetType] = useState<'STOCK' | 'ETF' | 'MUTUAL_FUND'>('STOCK');
    const [quantity, setQuantity] = useState<string>('');
    const [purchasePrice, setPurchasePrice] = useState<string>('');
    const [purchaseDate, setPurchaseDate] = useState<string>('');
    
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        if (initialData) {
            setTickerSymbol(initialData.tickerSymbol);
            setAssetType(initialData.assetType);
            setQuantity(initialData.quantity.toString());
            setPurchasePrice(initialData.purchasePrice.toString());
            // Format datetime-local requires YYYY-MM-DDThh:mm
            const date = new Date(initialData.purchaseDate);
            setPurchaseDate(date.toISOString().slice(0, 16));
        } else {
            setTickerSymbol('');
            setAssetType('STOCK');
            setQuantity('');
            setPurchasePrice('');
            // Set default date to today
            setPurchaseDate(new Date().toISOString().slice(0, 16));
        }
        setError('');
    }, [initialData, isOpen]);

    if (!isOpen) return null;

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        
        if (Number(quantity) <= 0) {
            setError('Quantity must be greater than zero');
            return;
        }
        if (Number(purchasePrice) <= 0) {
            setError('Purchase price must be greater than zero');
            return;
        }
        if (new Date(purchaseDate) > new Date()) {
            setError('Purchase date cannot be in the future');
            return;
        }

        setIsSubmitting(true);
        try {
            await onSubmit({
                id: initialData?.id,
                tickerSymbol,
                assetType,
                quantity: Number(quantity),
                purchasePrice: Number(purchasePrice),
                purchaseDate: new Date(purchaseDate).toISOString()
            });
            onClose();
        } catch (err: any) {
            setError(err.response?.data?.details?.tickerSymbol || 
                     err.response?.data?.message || 
                     'Failed to save holding. Please try again.');
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white rounded-lg shadow-xl p-6 w-full max-w-md">
                <h2 className="text-xl font-bold mb-4 text-gray-800">
                    {initialData ? 'Edit Holding' : 'Add New Holding'}
                </h2>
                
                {error && (
                    <div className="mb-4 bg-red-50 text-red-600 p-3 rounded text-sm border border-red-200">
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Ticker Symbol</label>
                        <input
                            type="text"
                            value={tickerSymbol}
                            onChange={(e) => setTickerSymbol(e.target.value.toUpperCase())}
                            className="w-full border-gray-300 rounded-md shadow-sm border p-2 focus:ring-indigo-500 focus:border-indigo-500"
                            required
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Asset Type</label>
                        <select
                            value={assetType}
                            onChange={(e) => setAssetType(e.target.value as any)}
                            className="w-full border-gray-300 rounded-md shadow-sm border p-2 focus:ring-indigo-500 focus:border-indigo-500"
                        >
                            <option value="STOCK">Stock</option>
                            <option value="ETF">ETF</option>
                            <option value="MUTUAL_FUND">Mutual Fund</option>
                        </select>
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Quantity</label>
                            <input
                                type="number"
                                step="0.0001"
                                value={quantity}
                                onChange={(e) => setQuantity(e.target.value)}
                                className="w-full border-gray-300 rounded-md shadow-sm border p-2 focus:ring-indigo-500 focus:border-indigo-500"
                                required
                            />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Avg Price ($)</label>
                            <input
                                type="number"
                                step="0.01"
                                value={purchasePrice}
                                onChange={(e) => setPurchasePrice(e.target.value)}
                                className="w-full border-gray-300 rounded-md shadow-sm border p-2 focus:ring-indigo-500 focus:border-indigo-500"
                                required
                            />
                        </div>
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Purchase Date</label>
                        <input
                            type="datetime-local"
                            value={purchaseDate}
                            onChange={(e) => setPurchaseDate(e.target.value)}
                            max={new Date().toISOString().slice(0, 16)}
                            className="w-full border-gray-300 rounded-md shadow-sm border p-2 focus:ring-indigo-500 focus:border-indigo-500"
                            required
                        />
                    </div>

                    <div className="flex justify-end space-x-3 mt-6">
                        <button
                            type="button"
                            onClick={onClose}
                            className="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50"
                            disabled={isSubmitting}
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            disabled={isSubmitting}
                            className="px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 disabled:bg-indigo-400"
                        >
                            {isSubmitting ? 'Saving...' : 'Save Holding'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default HoldingModal;
