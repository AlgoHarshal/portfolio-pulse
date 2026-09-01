import React, { useState } from 'react';
import { Edit2, Trash2, ArrowUpDown } from 'lucide-react';

export interface Holding {
    id: string;
    tickerSymbol: string;
    assetType: 'STOCK' | 'ETF' | 'MUTUAL_FUND';
    quantity: number;
    purchasePrice: number;
    purchaseDate: string;
    sector: string | null;
}

interface HoldingsTableProps {
    holdings: Holding[];
    onEdit: (holding: Holding) => void;
    onDelete: (holding: Holding) => void;
}

type SortField = 'tickerSymbol' | 'quantity' | 'purchasePrice' | 'purchaseDate' | 'assetType';
type SortOrder = 'asc' | 'desc';

const HoldingsTable: React.FC<HoldingsTableProps> = ({ holdings, onEdit, onDelete }) => {
    const [sortField, setSortField] = useState<SortField>('tickerSymbol');
    const [sortOrder, setSortOrder] = useState<SortOrder>('asc');

    const handleSort = (field: SortField) => {
        if (field === sortField) {
            setSortOrder(sortOrder === 'asc' ? 'desc' : 'asc');
        } else {
            setSortField(field);
            setSortOrder('asc');
        }
    };

    const sortedHoldings = [...holdings].sort((a, b) => {
        const aValue = a[sortField];
        const bValue = b[sortField];
        
        if (aValue === null) return sortOrder === 'asc' ? 1 : -1;
        if (bValue === null) return sortOrder === 'asc' ? -1 : 1;
        
        if (aValue < bValue) return sortOrder === 'asc' ? -1 : 1;
        if (aValue > bValue) return sortOrder === 'asc' ? 1 : -1;
        return 0;
    });

    const SortIcon = () => <ArrowUpDown className="inline w-4 h-4 ml-1 text-gray-400" />;

    return (
        <div className="overflow-x-auto bg-white rounded-lg shadow-sm border border-gray-200">
            <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                    <tr>
                        <th 
                            onClick={() => handleSort('tickerSymbol')}
                            className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-100"
                        >
                            Ticker <SortIcon />
                        </th>
                        <th 
                            onClick={() => handleSort('assetType')}
                            className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-100"
                        >
                            Asset Type <SortIcon />
                        </th>
                        <th 
                            onClick={() => handleSort('quantity')}
                            className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-100"
                        >
                            Quantity <SortIcon />
                        </th>
                        <th 
                            onClick={() => handleSort('purchasePrice')}
                            className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-100"
                        >
                            Avg Price <SortIcon />
                        </th>
                        <th 
                            onClick={() => handleSort('purchaseDate')}
                            className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-100"
                        >
                            Purchase Date <SortIcon />
                        </th>
                        <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                            Actions
                        </th>
                    </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                    {sortedHoldings.map((holding) => (
                        <tr key={holding.id} className="hover:bg-gray-50 transition-colors duration-150">
                            <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                                {holding.tickerSymbol}
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                {holding.assetType}
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                {Number(holding.quantity).toFixed(4)}
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                ${Number(holding.purchasePrice).toFixed(2)}
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                {new Date(holding.purchaseDate).toLocaleDateString()}
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                                <button 
                                    onClick={() => onEdit(holding)}
                                    className="text-indigo-600 hover:text-indigo-900 mr-4"
                                >
                                    <Edit2 className="w-4 h-4 inline" />
                                </button>
                                <button 
                                    onClick={() => onDelete(holding)}
                                    className="text-red-600 hover:text-red-900"
                                >
                                    <Trash2 className="w-4 h-4 inline" />
                                </button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default HoldingsTable;
