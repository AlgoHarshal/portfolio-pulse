import React, { useState } from 'react';
import type { Holding } from './HoldingsTable';

interface DeleteConfirmationModalProps {
    isOpen: boolean;
    onClose: () => void;
    onConfirm: () => Promise<void>;
    holding: Holding | null;
}

const DeleteConfirmationModal: React.FC<DeleteConfirmationModalProps> = ({ 
    isOpen, 
    onClose, 
    onConfirm,
    holding 
}) => {
    const [isDeleting, setIsDeleting] = useState(false);

    if (!isOpen || !holding) return null;

    const handleConfirm = async () => {
        setIsDeleting(true);
        try {
            await onConfirm();
        } finally {
            setIsDeleting(false);
            onClose();
        }
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white rounded-lg shadow-xl p-6 w-full max-w-sm">
                <h2 className="text-xl font-bold text-red-600 mb-2">Delete Holding</h2>
                <p className="text-gray-600 mb-6">
                    Are you sure you want to delete your <span className="font-bold">{holding.tickerSymbol}</span> holding? This action cannot be undone.
                </p>
                <div className="flex justify-end space-x-3">
                    <button
                        onClick={onClose}
                        disabled={isDeleting}
                        className="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50"
                    >
                        Cancel
                    </button>
                    <button
                        onClick={handleConfirm}
                        disabled={isDeleting}
                        className="px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700 disabled:bg-red-400 flex items-center"
                    >
                        {isDeleting ? 'Deleting...' : 'Delete'}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default DeleteConfirmationModal;
